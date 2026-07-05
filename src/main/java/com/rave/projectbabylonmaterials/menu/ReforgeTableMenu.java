package com.rave.projectbabylonmaterials.menu;

import com.rave.projectbabylonmaterials.balance.PBMBalances;
import com.rave.projectbabylonmaterials.balance.RarityBalance;

import com.rave.projectbabylonmaterials.block.entity.ReforgeTableBlockEntity;
import com.rave.projectbabylonmaterials.gem.GemSlotHelper;
import com.rave.projectbabylonmaterials.gem.GemType;
import com.rave.projectbabylonmaterials.init.PBMItems;
import com.rave.projectbabylonmaterials.init.PBMMenus;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;
import com.rave.projectbabylonmaterials.rarity.ItemReforgeHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class ReforgeTableMenu extends AbstractContainerMenu {
    private static final int BUTTON_REFORGE = 0;

    private final Container container;

    public ReforgeTableMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, buffer));
    }

    public ReforgeTableMenu(int containerId, Inventory playerInventory, Container container) {
        super(PBMMenus.REFORGE_TABLE_MENU.get(), containerId);

        checkContainerSize(container, 4);
        this.container = container;

        container.startOpen(playerInventory.player);

        this.addSlot(new Slot(container, ReforgeTableBlockEntity.SLOT_TOP_OUTPUT, 80, 22) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, ReforgeTableBlockEntity.SLOT_LEFT_INPUT, 27, 59) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isValidReforgeMaterial(stack);
            }
        });
        this.addSlot(new Slot(container, ReforgeTableBlockEntity.SLOT_CENTER_INPUT, 80, 60) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ItemRarityHelper.supportsSlottedItem(stack);
            }
        });
        this.addSlot(new Slot(container, ReforgeTableBlockEntity.SLOT_RIGHT_INPUT, 132, 59) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return GemType.fromStack(stack).isPresent();
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    private static Container getBlockEntity(Inventory inventory, RegistryFriendlyByteBuf buffer) {
        Level level = inventory.player.level();
        BlockEntity blockEntity = level.getBlockEntity(buffer.readBlockPos());
        if (blockEntity instanceof Container container) {
            return container;
        }
        throw new IllegalStateException("Menu provider is not a container");
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        int machineSlotCount = 4;
        int inventoryStart = machineSlotCount;
        int hotbarStart = inventoryStart + 27;
        int playerEnd = hotbarStart + 9;
        int inputStart = ReforgeTableBlockEntity.SLOT_LEFT_INPUT;
        int inputEnd = ReforgeTableBlockEntity.SLOT_RIGHT_INPUT + 1;

        if (index < machineSlotCount) {
            if (!this.moveItemStackTo(sourceStack, inventoryStart, playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(sourceStack, inputStart, inputEnd, false)) {
            if (index >= inventoryStart && index < hotbarStart) {
                if (!this.moveItemStackTo(sourceStack, hotbarStart, playerEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= hotbarStart && index < playerEnd) {
                if (!this.moveItemStackTo(sourceStack, inventoryStart, hotbarStart, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        broadcastChanges();
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId != BUTTON_REFORGE) {
            return super.clickMenuButton(player, buttonId);
        }

        if (player.level().isClientSide) {
            return true;
        }

        if (!this.container.getItem(ReforgeTableBlockEntity.SLOT_TOP_OUTPUT).isEmpty()) {
            return false;
        }

        ItemStack centerStack = this.container.getItem(ReforgeTableBlockEntity.SLOT_CENTER_INPUT);
        ItemStack gemStack = this.container.getItem(ReforgeTableBlockEntity.SLOT_RIGHT_INPUT);
        Optional<ItemReforgeHelper.ReforgeRecipe> reforgeRecipeOptional = getCurrentReforgeRecipe();
        boolean canSocket = canSocketCurrentItem();

        if (reforgeRecipeOptional.isEmpty() && !canSocket) {
            handleSocketFailureMessage(player, centerStack, gemStack);
            return false;
        }

        int requiredXp = getRequiredXp();
        if (player.experienceLevel < requiredXp) {
            return false;
        }

        int spentXp = 0;
        boolean processedOperation = false;
        boolean outputChanged = false;
        ItemStack resultStack = centerStack.copy();

        // Socket the gem first, while resultStack still matches the item canSocket was
        // validated against. Reforging afterwards re-rolls the gem-slot count, which would
        // otherwise make socketGem fail on the reforged item and silently drop the gem.
        if (canSocket && GemSlotHelper.socketGem(resultStack, gemStack)) {
            gemStack.shrink(1);
            spentXp += getSocketXp();
            processedOperation = true;
            outputChanged = true;
        } else if (!gemStack.isEmpty()) {
            handleSocketFailureMessage(player, centerStack, gemStack);
        }

        if (reforgeRecipeOptional.isPresent()) {
            ItemReforgeHelper.ReforgeRecipe recipe = reforgeRecipeOptional.get();
            ItemStack materialStack = this.container.getItem(ReforgeTableBlockEntity.SLOT_LEFT_INPUT);
            materialStack.shrink(recipe.materialCount());
            spentXp += recipe.requiredXp();
            processedOperation = true;

            boolean success = player.getRandom().nextInt(100) < recipe.successChance();
            if (success) {
                // Carries over any gem just socketed (SOCKETED_GEMS is preserved by the copy).
                resultStack = ItemReforgeHelper.createReforgedItem(resultStack, recipe.nextRarity(), player.getRandom());
                outputChanged = true;
            }
        }

        if (!processedOperation) {
            return false;
        }

        player.giveExperienceLevels(-spentXp);
        player.level().playSound(null, player.blockPosition(), SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (outputChanged) {
            this.container.setItem(ReforgeTableBlockEntity.SLOT_TOP_OUTPUT, resultStack);
            this.container.setItem(ReforgeTableBlockEntity.SLOT_CENTER_INPUT, ItemStack.EMPTY);
        }

        this.container.setChanged();
        broadcastChanges();
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 116 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 174));
        }
    }

    public int getRequiredXp() {
        int totalXp = getCurrentReforgeRecipe().map(ItemReforgeHelper.ReforgeRecipe::requiredXp).orElse(0);
        if (canSocketCurrentItem()) {
            totalXp += getSocketXp();
        }
        return totalXp;
    }

    public boolean canAttemptAction() {
        return this.container.getItem(ReforgeTableBlockEntity.SLOT_TOP_OUTPUT).isEmpty()
                && (getCurrentReforgeRecipe().isPresent() || canSocketCurrentItem());
    }

    private int getSocketXp() {
        ItemStack gemStack = this.container.getItem(ReforgeTableBlockEntity.SLOT_RIGHT_INPUT);
        Optional<ItemRarityTier> rarityOptional = ItemRarityHelper.getRarity(gemStack);
        if (rarityOptional.isEmpty()) {
            return 0;
        }

        return PBMBalances.rarity(rarityOptional.get()).map(RarityBalance::socketXp).orElseGet(() -> switch (rarityOptional.get()) {
            case COMMON -> 1;
            case UNCOMMON -> 2;
            case RARE -> 3;
            case EPIC -> 4;
            case LEGENDARY -> 5;
        });
    }

    private boolean canSocketCurrentItem() {
        ItemStack centerStack = this.container.getItem(ReforgeTableBlockEntity.SLOT_CENTER_INPUT);
        ItemStack gemStack = this.container.getItem(ReforgeTableBlockEntity.SLOT_RIGHT_INPUT);
        return GemSlotHelper.canSocketGem(centerStack, gemStack);
    }

    private Optional<ItemReforgeHelper.ReforgeRecipe> getCurrentReforgeRecipe() {
        return ItemReforgeHelper.getReforgeRecipe(
                this.container.getItem(ReforgeTableBlockEntity.SLOT_LEFT_INPUT),
                this.container.getItem(ReforgeTableBlockEntity.SLOT_CENTER_INPUT)
        );
    }

    private static boolean isValidReforgeMaterial(ItemStack stack) {
        return stack.is(PBMItems.PURE_TEAR.get())
                || stack.is(PBMItems.ANCIENT_AMBER.get())
                || stack.is(PBMItems.MAGIC_CRYSTAL.get())
                || stack.is(PBMItems.FATE_ORB.get());
    }

    private void handleSocketFailureMessage(Player player, ItemStack centerStack, ItemStack gemStack) {
        if (centerStack.isEmpty() || gemStack.isEmpty() || GemType.fromStack(gemStack).isEmpty() || !ItemRarityHelper.supportsSlottedItem(centerStack)) {
            return;
        }

        if (!GemSlotHelper.hasAnyGemSlots(centerStack)) {
            showActionBar(player, "tooltip.project_babylon_materials.no_gem_slots");
            return;
        }

        if (!GemSlotHelper.hasAvailableGemSlot(centerStack)) {
            showActionBar(player, "tooltip.project_babylon_materials.no_available_gem_slots");
        }
    }

    private static void showActionBar(Player player, String translationKey) {
        player.displayClientMessage(Component.translatable(translationKey), true);
    }
}

