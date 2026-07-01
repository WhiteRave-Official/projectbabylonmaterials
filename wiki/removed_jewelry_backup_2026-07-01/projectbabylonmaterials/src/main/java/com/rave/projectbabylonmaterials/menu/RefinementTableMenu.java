package com.rave.projectbabylonmaterials.menu;

import com.rave.projectbabylonmaterials.block.entity.RefinementTableBlockEntity;
import com.rave.projectbabylonmaterials.gem.GemSlotHelper;
import com.rave.projectbabylonmaterials.init.PBMMenus;
import com.rave.projectbabylonmaterials.rarity.ItemRarityHelper;
import com.rave.projectbabylonmaterials.rarity.ItemRarityTier;
import net.minecraft.network.FriendlyByteBuf;
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

import java.util.List;
import java.util.Optional;

public class RefinementTableMenu extends AbstractContainerMenu {
    private static final int BUTTON_REFINE = 0;

    private final Container container;

    public RefinementTableMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, buffer));
    }

    public RefinementTableMenu(int containerId, Inventory playerInventory, Container container) {
        super(PBMMenus.REFINEMENT_TABLE_MENU.get(), containerId);

        checkContainerSize(container, 3);
        this.container = container;

        container.startOpen(playerInventory.player);

        this.addSlot(new Slot(container, RefinementTableBlockEntity.SLOT_LEFT_OUTPUT, 58, 19) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, RefinementTableBlockEntity.SLOT_RIGHT_OUTPUT, 100, 19) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new Slot(container, RefinementTableBlockEntity.SLOT_INPUT, 79, 51) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ItemRarityHelper.supportsSlottedItem(stack);
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

        int machineSlotCount = 3;
        int inventoryStart = machineSlotCount;
        int hotbarStart = inventoryStart + 27;
        int playerEnd = hotbarStart + 9;

        if (index < machineSlotCount) {
            if (!this.moveItemStackTo(sourceStack, inventoryStart, playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(sourceStack, RefinementTableBlockEntity.SLOT_INPUT, RefinementTableBlockEntity.SLOT_INPUT + 1, false)) {
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
        if (buttonId != BUTTON_REFINE) {
            return super.clickMenuButton(player, buttonId);
        }

        if (player.level().isClientSide) {
            return true;
        }

        if (!canAttemptExtraction()) {
            return false;
        }

        int requiredXp = getRequiredXp();
        if (requiredXp <= 0 || player.experienceLevel < requiredXp) {
            return false;
        }

        ItemStack inputStack = this.container.getItem(RefinementTableBlockEntity.SLOT_INPUT);
        int extractionCount = getAvailableOutputSlots();
        List<ItemStack> extractedGems = GemSlotHelper.extractSocketedGems(inputStack, extractionCount);
        if (extractedGems.isEmpty()) {
            return false;
        }

        placeExtractedGems(extractedGems);
        player.giveExperienceLevels(-requiredXp);
        player.level().playSound(null, player.blockPosition(), SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

        this.container.setItem(RefinementTableBlockEntity.SLOT_INPUT, inputStack);
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
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 94 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 152));
        }
    }

    public boolean canAttemptExtraction() {
        return getAvailableOutputSlots() > 0 && !getExtractableGems().isEmpty();
    }

    public int getRequiredXp() {
        return getExtractableGems().stream().mapToInt(this::getGemExtractionCost).sum();
    }

    private List<ItemStack> getExtractableGems() {
        ItemStack inputStack = this.container.getItem(RefinementTableBlockEntity.SLOT_INPUT);
        int extractionCount = getAvailableOutputSlots();
        List<ItemStack> socketedGems = GemSlotHelper.getSocketedGems(inputStack);
        return socketedGems.subList(0, Math.min(extractionCount, socketedGems.size()));
    }

    private int getAvailableOutputSlots() {
        int available = 0;
        if (this.container.getItem(RefinementTableBlockEntity.SLOT_LEFT_OUTPUT).isEmpty()) {
            available++;
        }
        if (this.container.getItem(RefinementTableBlockEntity.SLOT_RIGHT_OUTPUT).isEmpty()) {
            available++;
        }
        return available;
    }

    private void placeExtractedGems(List<ItemStack> extractedGems) {
        int index = 0;
        if (this.container.getItem(RefinementTableBlockEntity.SLOT_LEFT_OUTPUT).isEmpty() && index < extractedGems.size()) {
            this.container.setItem(RefinementTableBlockEntity.SLOT_LEFT_OUTPUT, extractedGems.get(index++));
        }
        if (this.container.getItem(RefinementTableBlockEntity.SLOT_RIGHT_OUTPUT).isEmpty() && index < extractedGems.size()) {
            this.container.setItem(RefinementTableBlockEntity.SLOT_RIGHT_OUTPUT, extractedGems.get(index));
        }
    }

    private int getGemExtractionCost(ItemStack gemStack) {
        Optional<ItemRarityTier> rarityOptional = ItemRarityHelper.getRarity(gemStack);
        if (rarityOptional.isEmpty()) {
            return 0;
        }

        return switch (rarityOptional.get()) {
            case COMMON, UNCOMMON -> 1;
            case RARE -> 2;
            case EPIC -> 3;
            case LEGENDARY -> 4;
        };
    }
}