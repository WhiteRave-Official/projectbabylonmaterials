package com.rave.projectbabylonmaterials.menu;

import com.rave.projectbabylonmaterials.block.entity.JewelryTableBlockEntity;
import com.rave.projectbabylonmaterials.gem.GemUpgradeHelper;
import com.rave.projectbabylonmaterials.init.PBMItems;
import com.rave.projectbabylonmaterials.init.PBMMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.Optional;

public class JewelryTableMenu extends AbstractContainerMenu {
    private static final int BUTTON_UPGRADE = 0;

    private final Container container;

    public JewelryTableMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, buffer));
    }

    public JewelryTableMenu(int containerId, Inventory playerInventory, Container container) {
        super(PBMMenus.JEWELRY_TABLE_MENU.get(), containerId);

        checkContainerSize(container, 4);
        this.container = container;

        container.startOpen(playerInventory.player);

        this.addSlot(new Slot(container, JewelryTableBlockEntity.SLOT_LEFT_MATERIAL, 17, 21) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isValidMaterial(stack);
            }
        });
        this.addSlot(new Slot(container, JewelryTableBlockEntity.SLOT_RIGHT_MATERIAL, 17, 52) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isValidMaterial(stack);
            }
        });
        this.addSlot(new Slot(container, JewelryTableBlockEntity.SLOT_STONE, 75, 36) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return GemUpgradeHelper.isGem(stack);
            }
        });
        this.addSlot(new Slot(container, JewelryTableBlockEntity.SLOT_OUTPUT, 132, 36) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    private static Container getBlockEntity(Inventory inventory, FriendlyByteBuf buffer) {
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
        int inputStart = JewelryTableBlockEntity.SLOT_LEFT_MATERIAL;
        int inputEnd = JewelryTableBlockEntity.SLOT_STONE + 1;

        if (index < machineSlotCount) {
            if (!this.moveItemStackTo(sourceStack, inventoryStart, playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(sourceStack, inputStart, inputEnd, false)) {
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
        if (buttonId != BUTTON_UPGRADE) {
            return super.clickMenuButton(player, buttonId);
        }

        if (player.level().isClientSide) {
            return true;
        }

        if (!canAttemptUpgrade()) {
            return false;
        }

        Optional<GemUpgradeHelper.UpgradeRecipe> recipeOptional = getCurrentRecipe();
        if (recipeOptional.isEmpty()) {
            return false;
        }

        GemUpgradeHelper.UpgradeRecipe recipe = recipeOptional.get();
        if (player.experienceLevel < recipe.requiredXp()) {
            return false;
        }

        if (!this.container.getItem(JewelryTableBlockEntity.SLOT_OUTPUT).isEmpty()) {
            return false;
        }

        player.level().playSound(null, player.blockPosition(), SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

        ItemStack gemStack = this.container.getItem(JewelryTableBlockEntity.SLOT_STONE);
        ItemStack leftStack = this.container.getItem(JewelryTableBlockEntity.SLOT_LEFT_MATERIAL);
        ItemStack rightStack = this.container.getItem(JewelryTableBlockEntity.SLOT_RIGHT_MATERIAL);

        leftStack.shrink(leftStack.is(PBMItems.GEM_DUST.get()) ? recipe.requiredDust() : 1);
        rightStack.shrink(rightStack.is(PBMItems.GEM_DUST.get()) ? recipe.requiredDust() : 1);
        player.giveExperienceLevels(-recipe.requiredXp());

        boolean success = player.getRandom().nextInt(100) < recipe.successChance();
        if (success) {
            ItemStack result = GemUpgradeHelper.createUpgradedGem(gemStack, recipe.nextRarity());
            this.container.setItem(JewelryTableBlockEntity.SLOT_OUTPUT, result);
            this.container.setItem(JewelryTableBlockEntity.SLOT_STONE, ItemStack.EMPTY);
        } else {
            GemUpgradeHelper.consumeFailedAttempt(gemStack);
            this.container.setItem(JewelryTableBlockEntity.SLOT_STONE, gemStack);
        }

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
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 88 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 146));
        }
    }

    public int getRequiredXp() {
        return getCurrentRecipe().map(GemUpgradeHelper.UpgradeRecipe::requiredXp).orElse(0);
    }

    public boolean canAttemptUpgrade() {
        return getCurrentRecipe().isPresent() && this.container.getItem(JewelryTableBlockEntity.SLOT_OUTPUT).isEmpty();
    }

    private Optional<GemUpgradeHelper.UpgradeRecipe> getCurrentRecipe() {
        return GemUpgradeHelper.getUpgradeRecipe(
                this.container.getItem(JewelryTableBlockEntity.SLOT_LEFT_MATERIAL),
                this.container.getItem(JewelryTableBlockEntity.SLOT_RIGHT_MATERIAL),
                this.container.getItem(JewelryTableBlockEntity.SLOT_STONE)
        );
    }

    private static boolean isValidMaterial(ItemStack stack) {
        return stack.is(PBMItems.GEM_DUST.get())
                || stack.is(PBMItems.PURE_TEAR.get())
                || stack.is(PBMItems.ANCIENT_AMBER.get())
                || stack.is(PBMItems.MAGIC_CRYSTAL.get())
                || stack.is(PBMItems.FATE_ORB.get());
    }
}



