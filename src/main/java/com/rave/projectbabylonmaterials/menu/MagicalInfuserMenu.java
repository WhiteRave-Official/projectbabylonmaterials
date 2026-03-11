package com.rave.projectbabylonmaterials.menu;

import com.rave.projectbabylonmaterials.block.entity.MagicalInfuserBlockEntity;
import com.rave.projectbabylonmaterials.init.PBMMenus;
import com.rave.projectbabylonmaterials.init.PBMItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MagicalInfuserMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    public MagicalInfuserMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, buffer), new SimpleContainerData(3));
    }

    public MagicalInfuserMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(PBMMenus.MAGICAL_INFUSER_MENU.get(), containerId);

        checkContainerSize(container, 4);
        checkContainerDataCount(data, 3);

        this.container = container;
        this.data = data;

        container.startOpen(playerInventory.player);

        this.addSlot(new Slot(container, MagicalInfuserBlockEntity.SLOT_FUEL, 17, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(PBMItems.MAGIC_DUST.get());
            }
        });

        this.addSlot(new Slot(container, MagicalInfuserBlockEntity.SLOT_TOP_INPUT, 68, 18));
        this.addSlot(new Slot(container, MagicalInfuserBlockEntity.SLOT_BOTTOM_INPUT, 68, 55));

        this.addSlot(new Slot(container, MagicalInfuserBlockEntity.SLOT_OUTPUT, 125, 36) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        this.addDataSlots(data);
    }

    private static Container getBlockEntity(Inventory inventory, FriendlyByteBuf buffer) {
        Level level = inventory.player.level();
        BlockEntity blockEntity = level.getBlockEntity(buffer.readBlockPos());
        if (blockEntity instanceof Container container) {
            return container;
        }
        throw new IllegalStateException("Menu provider is not a container");
    }

    public int getFuelOperations() {
        return this.data.get(0);
    }

    public int getProgress() {
        return this.data.get(1);
    }

    public int getMaxProgress() {
        return this.data.get(2);
    }

    public boolean hasProgress() {
        return this.getProgress() > 0 && this.getMaxProgress() > 0;
    }

    public int getScaledProgress(int pixels) {
        if (!hasProgress()) {
            return 0;
        }
        return this.getProgress() * pixels / this.getMaxProgress();
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
        int inputStart = MagicalInfuserBlockEntity.SLOT_TOP_INPUT;
        int inputEnd = MagicalInfuserBlockEntity.SLOT_BOTTOM_INPUT + 1;

        if (index < machineSlotCount) {
            if (!this.moveItemStackTo(sourceStack, inventoryStart, playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (sourceStack.is(PBMItems.MAGIC_DUST.get())) {
                if (!this.moveItemStackTo(sourceStack, MagicalInfuserBlockEntity.SLOT_FUEL, MagicalInfuserBlockEntity.SLOT_FUEL + 1, false)) {
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
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }
}
