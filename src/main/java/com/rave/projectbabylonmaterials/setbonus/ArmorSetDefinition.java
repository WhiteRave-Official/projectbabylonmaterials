package com.rave.projectbabylonmaterials.setbonus;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ArmorSetDefinition {

    private final String id;
    private final String displayNameKey;
    private final Supplier<Item> helmet;
    private final Supplier<Item> chestplate;
    private final Supplier<Item> leggings;
    private final Supplier<Item> boots;
    private final List<ArmorSetBonus> materialBonuses;
    private final List<ArmorSetBonus> classBonuses;
    private final List<ArmorSetBonus> allBonuses;

    private ArmorSetDefinition(String id, String displayNameKey, Supplier<Item> helmet, Supplier<Item> chestplate, Supplier<Item> leggings,
                              Supplier<Item> boots, List<ArmorSetBonus> materialBonuses, List<ArmorSetBonus> classBonuses) {
        this.id = id;
        this.displayNameKey = displayNameKey;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.materialBonuses = List.copyOf(materialBonuses);
        this.classBonuses = List.copyOf(classBonuses);
        List<ArmorSetBonus> combinedBonuses = new ArrayList<>(materialBonuses.size() + classBonuses.size());
        combinedBonuses.addAll(materialBonuses);
        combinedBonuses.addAll(classBonuses);
        this.allBonuses = Collections.unmodifiableList(combinedBonuses);
    }

    public String getId() {
        return id;
    }

    public String getDisplayNameKey() {
        return displayNameKey;
    }

    public List<ArmorSetBonus> getMaterialBonuses() {
        return materialBonuses;
    }

    public List<ArmorSetBonus> getClassBonuses() {
        return classBonuses;
    }

    public List<ArmorSetBonus> getAllBonuses() {
        return allBonuses;
    }

    public boolean matches(LivingEntity entity) {
        return matchesSlot(entity.getItemBySlot(EquipmentSlot.HEAD), helmet)
                && matchesSlot(entity.getItemBySlot(EquipmentSlot.CHEST), chestplate)
                && matchesSlot(entity.getItemBySlot(EquipmentSlot.LEGS), leggings)
                && matchesSlot(entity.getItemBySlot(EquipmentSlot.FEET), boots);
    }

    public int countMatchedPieces(LivingEntity entity) {
        int matchedPieces = 0;
        if (matchesSlot(entity.getItemBySlot(EquipmentSlot.HEAD), helmet)) {
            matchedPieces++;
        }
        if (matchesSlot(entity.getItemBySlot(EquipmentSlot.CHEST), chestplate)) {
            matchedPieces++;
        }
        if (matchesSlot(entity.getItemBySlot(EquipmentSlot.LEGS), leggings)) {
            matchedPieces++;
        }
        if (matchesSlot(entity.getItemBySlot(EquipmentSlot.FEET), boots)) {
            matchedPieces++;
        }
        return matchedPieces;
    }

    public boolean hasPiece(LivingEntity entity, EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> matchesSlot(entity.getItemBySlot(slot), helmet);
            case CHEST -> matchesSlot(entity.getItemBySlot(slot), chestplate);
            case LEGS -> matchesSlot(entity.getItemBySlot(slot), leggings);
            case FEET -> matchesSlot(entity.getItemBySlot(slot), boots);
            default -> false;
        };
    }

    public boolean contains(Item item) {
        return helmet.get() == item
                || chestplate.get() == item
                || leggings.get() == item
                || boots.get() == item;
    }

    public ItemStack getPieceStack(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> new ItemStack(helmet.get());
            case CHEST -> new ItemStack(chestplate.get());
            case LEGS -> new ItemStack(leggings.get());
            case FEET -> new ItemStack(boots.get());
            default -> ItemStack.EMPTY;
        };
    }

    private static boolean matchesSlot(ItemStack stack, Supplier<Item> expected) {
        return !stack.isEmpty() && stack.getItem() == expected.get();
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static final class Builder {
        private final String id;
        private String displayNameKey;
        private Supplier<Item> helmet;
        private Supplier<Item> chestplate;
        private Supplier<Item> leggings;
        private Supplier<Item> boots;
        private final List<ArmorSetBonus> materialBonuses = new ArrayList<>();
        private final List<ArmorSetBonus> classBonuses = new ArrayList<>();

        private Builder(String id) {
            this.id = Objects.requireNonNull(id, "id");
        }

        public Builder displayNameKey(String displayNameKey) {
            this.displayNameKey = Objects.requireNonNull(displayNameKey, "displayNameKey");
            return this;
        }

        public Builder helmet(Supplier<Item> helmet) {
            this.helmet = Objects.requireNonNull(helmet, "helmet");
            return this;
        }

        public Builder chestplate(Supplier<Item> chestplate) {
            this.chestplate = Objects.requireNonNull(chestplate, "chestplate");
            return this;
        }

        public Builder leggings(Supplier<Item> leggings) {
            this.leggings = Objects.requireNonNull(leggings, "leggings");
            return this;
        }

        public Builder boots(Supplier<Item> boots) {
            this.boots = Objects.requireNonNull(boots, "boots");
            return this;
        }

        public Builder materialBonus(ArmorSetBonus bonus) {
            this.materialBonuses.add(Objects.requireNonNull(bonus, "bonus"));
            return this;
        }

        public Builder classBonus(ArmorSetBonus bonus) {
            this.classBonuses.add(Objects.requireNonNull(bonus, "bonus"));
            return this;
        }

        public ArmorSetDefinition build() {
            return new ArmorSetDefinition(
                    id,
                    Objects.requireNonNull(displayNameKey, "displayNameKey"),
                    Objects.requireNonNull(helmet, "helmet"),
                    Objects.requireNonNull(chestplate, "chestplate"),
                    Objects.requireNonNull(leggings, "leggings"),
                    Objects.requireNonNull(boots, "boots"),
                    materialBonuses,
                    classBonuses
            );
        }
    }
}

