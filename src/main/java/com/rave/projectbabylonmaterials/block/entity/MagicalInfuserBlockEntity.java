package com.rave.projectbabylonmaterials.block.entity;

import com.rave.projectbabylonmaterials.block.custom.MagicalInfuserBlock;
import com.rave.projectbabylonmaterials.init.PBMBlockEntities;
import com.rave.projectbabylonmaterials.init.PBMItems;
import com.rave.projectbabylonmaterials.init.PBMRecipes;
import com.rave.projectbabylonmaterials.menu.MagicalInfuserMenu;
import com.rave.projectbabylonmaterials.recipe.MagicalInfuserRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class MagicalInfuserBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int SLOT_FUEL = 0;
    public static final int SLOT_TOP_INPUT = 1;
    public static final int SLOT_BOTTOM_INPUT = 2;
    public static final int SLOT_OUTPUT = 3;

    private static final int[] SLOTS_UP = new int[]{SLOT_TOP_INPUT, SLOT_BOTTOM_INPUT};
    private static final int[] SLOTS_DOWN = new int[]{SLOT_OUTPUT};
    private static final int[] SLOTS_SIDE = new int[]{SLOT_FUEL};

    private static final int DATA_FUEL_OPERATIONS = 0;
    private static final int DATA_PROGRESS = 1;
    private static final int DATA_MAX_PROGRESS = 2;

    private static final int DEFAULT_CRAFT_TIME = 200;
    private static final int FUEL_OPERATIONS_PER_DUST = 20;

    private NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private int fuelOperations;
    private int progress;
    private int maxProgress = DEFAULT_CRAFT_TIME;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_FUEL_OPERATIONS -> fuelOperations;
                case DATA_PROGRESS -> progress;
                case DATA_MAX_PROGRESS -> maxProgress;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case DATA_FUEL_OPERATIONS -> fuelOperations = value;
                case DATA_PROGRESS -> progress = value;
                case DATA_MAX_PROGRESS -> maxProgress = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public MagicalInfuserBlockEntity(BlockPos pos, BlockState state) {
        super(PBMBlockEntities.MAGICAL_INFUSER_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MagicalInfuserBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }

        boolean changed = false;
        Optional<MagicalInfuserRecipe> recipeOptional = blockEntity.getCurrentRecipe();

        if (blockEntity.fuelOperations <= 0 && recipeOptional.isPresent() && blockEntity.canConsumeFuel()) {
            blockEntity.consumeFuel();
            changed = true;
        }

        if (recipeOptional.isPresent() && blockEntity.canCraft(recipeOptional.get())) {
            blockEntity.maxProgress = recipeOptional.get().getCraftTime();

            if (blockEntity.fuelOperations > 0) {
                blockEntity.progress++;
                changed = true;

                if (blockEntity.progress >= blockEntity.maxProgress) {
                    blockEntity.finishRecipe(recipeOptional.get());
                    blockEntity.progress = 0;
                    blockEntity.fuelOperations--;
                    changed = true;
                }
            }
        } else if (blockEntity.progress != 0) {
            blockEntity.progress = 0;
            changed = true;
        }

        boolean lit = blockEntity.fuelOperations > 0;
        if (state.getValue(MagicalInfuserBlock.LIT) != lit) {
            level.setBlock(pos, state.setValue(MagicalInfuserBlock.LIT, lit), 3);
            changed = true;
        }

        if (changed) {
            setChanged(level, pos, state);
        }
    }

    private Optional<MagicalInfuserRecipe> getCurrentRecipe() {
        if (this.level == null) {
            return Optional.empty();
        }

        return this.level.getRecipeManager().getRecipeFor(PBMRecipes.MAGICAL_INFUSING_TYPE.get(), this, this.level);
    }

    private boolean canConsumeFuel() {
        return this.items.get(SLOT_FUEL).is(PBMItems.MAGIC_DUST.get());
    }

    private void consumeFuel() {
        ItemStack fuel = this.items.get(SLOT_FUEL);
        if (!fuel.isEmpty() && fuel.is(PBMItems.MAGIC_DUST.get())) {
            fuel.shrink(1);
            this.fuelOperations += FUEL_OPERATIONS_PER_DUST;
        }
    }

    private boolean canCraft(MagicalInfuserRecipe recipe) {
        ItemStack outputStack = this.items.get(SLOT_OUTPUT);
        ItemStack recipeResult = recipe.getResultItem(RegistryAccess.EMPTY);

        if (recipeResult.isEmpty()) {
            return false;
        }

        if (outputStack.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSameItemSameTags(outputStack, recipeResult)) {
            return false;
        }

        return outputStack.getCount() + recipeResult.getCount() <= outputStack.getMaxStackSize();
    }

    private void finishRecipe(MagicalInfuserRecipe recipe) {
        ItemStack result = recipe.getResultItem(RegistryAccess.EMPTY).copy();
        ItemStack output = this.items.get(SLOT_OUTPUT);

        this.items.get(SLOT_TOP_INPUT).shrink(recipe.getTopCount());
        this.items.get(SLOT_BOTTOM_INPUT).shrink(recipe.getBottomCount());

        if (output.isEmpty()) {
            this.items.set(SLOT_OUTPUT, result);
        } else {
            output.grow(result.getCount());
        }
    }

    public void drops() {
        if (this.level != null) {
            Containers.dropContents(this.level, this.worldPosition, this);
        }
    }

    public ContainerData getData() {
        return data;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.project_babylon_materials.magical_infuser");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new MagicalInfuserMenu(containerId, inventory, this, this.data);
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction side) {
        if (side == net.minecraft.core.Direction.DOWN) {
            return SLOTS_DOWN;
        }
        if (side == net.minecraft.core.Direction.UP) {
            return SLOTS_UP;
        }
        return SLOTS_SIDE;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, net.minecraft.core.Direction direction) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, net.minecraft.core.Direction direction) {
        return index == SLOT_OUTPUT;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemStack = ContainerHelper.removeItem(this.items, index, count);
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.items.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == SLOT_OUTPUT) {
            return false;
        }
        if (index == SLOT_FUEL) {
            return stack.is(PBMItems.MAGIC_DUST.get());
        }
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        tag.putInt("fuel_operations", this.fuelOperations);
        tag.putInt("progress", this.progress);
        tag.putInt("max_progress", this.maxProgress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        this.fuelOperations = tag.getInt("fuel_operations");
        this.progress = tag.getInt("progress");
        this.maxProgress = tag.getInt("max_progress");
        if (this.maxProgress <= 0) {
            this.maxProgress = DEFAULT_CRAFT_TIME;
        }
    }
}
