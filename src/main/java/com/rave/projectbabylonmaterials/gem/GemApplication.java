package com.rave.projectbabylonmaterials.gem;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum GemApplication {
    MELEE("tooltip.project_babylon_materials.gem_application.melee"),
    RANGED("tooltip.project_babylon_materials.gem_application.ranged"),
    MAGIC("tooltip.project_babylon_materials.gem_application.magic"),
    ARMOR("tooltip.project_babylon_materials.gem_application.armor"),
    TOOL("tooltip.project_babylon_materials.gem_application.tool");

    private final String translationKey;

    GemApplication(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();
        return switch (this) {
            case MELEE -> item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem;
            case RANGED -> item instanceof BowItem || item instanceof CrossbowItem;
            case MAGIC -> isMagicWeapon(item);
            case ARMOR -> item instanceof ArmorItem;
            case TOOL -> item instanceof PickaxeItem || item instanceof ShovelItem || item instanceof HoeItem;
        };
    }

    public static List<ItemStack> createPreviewItems(EnumSet<GemApplication> applications) {
        Map<String, ItemStack> previews = new LinkedHashMap<>();

        for (GemApplication application : applications) {
            switch (application) {
                case MELEE -> previews.put("melee", new ItemStack(Items.IRON_SWORD));
                case RANGED -> {
                    previews.put("bow", new ItemStack(Items.BOW));
                    previews.put("crossbow", new ItemStack(Items.CROSSBOW));
                }
                case MAGIC -> previews.put("magic", findMagicPreviewItem());
                case ARMOR -> previews.put("armor", new ItemStack(Items.IRON_CHESTPLATE));
                case TOOL -> previews.put("tool", new ItemStack(Items.IRON_PICKAXE));
            }
        }

        return new ArrayList<>(previews.values());
    }

    public static boolean isMagicWeapon(Item item) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        return key != null && "irons_spellbooks".equals(key.getNamespace()) && key.getPath().contains("staff");
    }

    private static ItemStack findMagicPreviewItem() {
        for (ResourceLocation key : BuiltInRegistries.ITEM.keySet()) {
            if ("irons_spellbooks".equals(key.getNamespace()) && key.getPath().contains("staff")) {
                Item item = BuiltInRegistries.ITEM.get(key);
                if (item != Items.AIR) {
                    return new ItemStack(item);
                }
            }
        }

        return new ItemStack(Items.ENCHANTED_BOOK);
    }
}
