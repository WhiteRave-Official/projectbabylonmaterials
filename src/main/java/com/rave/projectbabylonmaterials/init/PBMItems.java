package com.rave.projectbabylonmaterials.init;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.gem.GemType;
import com.rave.projectbabylonmaterials.item.BowlAndMortarItem;
import com.rave.projectbabylonmaterials.item.gem.GemItem;
import com.rave.projectbabylonmaterials.item.smithhammer.DiamondSmithHammerItem;
import com.rave.projectbabylonmaterials.item.smithhammer.GoldenSmithHammerItem;
import com.rave.projectbabylonmaterials.item.smithhammer.IronSmithHammerItem;
import com.rave.projectbabylonmaterials.item.smithhammer.NetheriteSmithHammerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public final class PBMItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(ProjectBabylonMaterials.MODID);

    public static final DeferredItem<Item> DRAGONSTEEL_INGOT = registerSimpleItem("dragonsteel_ingot");
    public static final DeferredItem<Item> DRAGONSTEEL_PLATE = registerSimpleItem("dragonsteel_plate");
    public static final DeferredItem<Item> DRAGONSTEEL_NUGGET = registerSimpleItem("dragonsteel_nugget");
    public static final DeferredItem<Item> ETHEREAL_INGOT = registerSimpleItem("ethereal_ingot");
    public static final DeferredItem<Item> ETHEREAL_PLATE = registerSimpleItem("ethereal_plate");
    public static final DeferredItem<Item> ETHEREAL_NUGGET = registerSimpleItem("ethereal_nugget");
    public static final DeferredItem<Item> EVERFROST_INGOT = registerSimpleItem("everfrost_ingot");
    public static final DeferredItem<Item> EVERFROST_PLATE = registerSimpleItem("everfrost_plate");
    public static final DeferredItem<Item> EVERFROST_NUGGET = registerSimpleItem("everfrost_nugget");
    public static final DeferredItem<Item> INFUSED_GOLDEN_INGOT = registerSimpleItem("infused_golden_ingot");
    public static final DeferredItem<Item> INFUSED_GOLDEN_PLATE = registerSimpleItem("infused_golden_plate");
    public static final DeferredItem<Item> INFUSED_GOLDEN_NUGGET = registerSimpleItem("infused_golden_nugget");
    public static final DeferredItem<Item> DIAMOND_INGOT = registerSimpleItem("diamond_ingot");
    public static final DeferredItem<Item> DIAMOND_PLATE = registerSimpleItem("diamond_plate");
    public static final DeferredItem<Item> DIAMOND_INGOT_NUGGET = registerSimpleItem("diamond_ingot_nugget");
    public static final DeferredItem<Item> STEEL_INGOT = registerSimpleItem("steel_ingot");
    public static final DeferredItem<Item> STEEL_PLATE = registerSimpleItem("steel_plate");
    public static final DeferredItem<Item> STEEL_NUGGET = registerSimpleItem("steel_nugget");
    public static final DeferredItem<Item> IRON_PLATE = registerSimpleItem("iron_plate");
    public static final DeferredItem<Item> NETHERITE_PLATE = registerSimpleItem("netherite_plate");
    public static final DeferredItem<Item> NETHERITE_NUGGET = registerSimpleItem("netherite_nugget");

    public static final DeferredItem<Item> CLOTH = registerSimpleItem("cloth");
    public static final DeferredItem<Item> DIAMOND_CLOTH = registerSimpleItem("diamond_cloth");
    public static final DeferredItem<Item> GOLDEN_CLOTH = registerSimpleItem("golden_cloth");
    public static final DeferredItem<Item> ICE_CLOTH = registerSimpleItem("ice_cloth");
    public static final DeferredItem<Item> NETHERITE_CLOTH = registerSimpleItem("netherite_cloth");
    public static final DeferredItem<Item> ETHEREAL_CLOTH = registerSimpleItem("ethereal_cloth");
    public static final DeferredItem<Item> DRAGONSTEEL_CLOTH = registerSimpleItem("dragonsteel_cloth");

    public static final DeferredItem<Item> MAGICAL_ICE_SHARD = registerSimpleItem("magical_ice_shard");
    public static final DeferredItem<Item> MAGIC_DUST = registerSimpleItem("magic_dust");
    public static final DeferredItem<Item> GEM_DUST = registerSimpleItem("gem_dust");
    public static final DeferredItem<Item> AMETHYST_DUST = registerSimpleItem("amethyst_dust");
    public static final DeferredItem<Item> QUARTZ_DUST = registerSimpleItem("quartz_dust");
    public static final DeferredItem<Item> DIAMOND_DUST = registerSimpleItem("diamond_dust");
    public static final DeferredItem<Item> RUBY = registerSimpleItem("ruby");
    public static final DeferredItem<Item> RUBY_DUST = registerSimpleItem("ruby_dust");
    public static final DeferredItem<Item> PURE_TEAR = registerSimpleItem("pure_tear");
    public static final DeferredItem<Item> ANCIENT_AMBER = registerSimpleItem("ancient_amber");
    public static final DeferredItem<Item> MAGIC_CRYSTAL = registerSimpleItem("magic_crystal");
    public static final DeferredItem<Item> FATE_ORB = registerSimpleItem("fate_orb");

    public static final DeferredItem<Item> HANDLE = registerSimpleItem("handle");
    public static final DeferredItem<Item> SHAFT = registerSimpleItem("shaft");
    public static final DeferredItem<Item> REINFORCED_HANDLE = registerSimpleItem("reinforced_handle");
    public static final DeferredItem<Item> REINFORCED_SHAFT = registerSimpleItem("reinforced_shaft");
    public static final DeferredItem<Item> BOWL_AND_MORTAR = ITEMS.register("bowl_and_mortar", BowlAndMortarItem::new);

    public static final DeferredItem<Item> RUBY_STONE = registerGemItem("ruby_stone", GemType.RUBY);
    public static final DeferredItem<Item> SAPPHIRE_STONE = registerGemItem("sapphire_stone", GemType.SAPPHIRE);
    public static final DeferredItem<Item> TOPAZ_STONE = registerGemItem("topaz_stone", GemType.TOPAZ);
    public static final DeferredItem<Item> WHITE_STONE = registerGemItem("white_stone", GemType.WHITE);
    public static final DeferredItem<Item> BLACK_STONE = registerGemItem("black_stone", GemType.BLACK);
    public static final DeferredItem<Item> CHRIZOLITE_STONE = registerGemItem("chrizolite_stone", GemType.CHRIZOLITE);
    public static final DeferredItem<Item> MALACHITE_STONE = registerGemItem("malachite_stone", GemType.MALACHITE);
    public static final DeferredItem<Item> GARNET_STONE = registerGemItem("garnet_stone", GemType.GARNET);
    public static final DeferredItem<Item> LAPIS_STONE = registerGemItem("lapis_stone", GemType.LAPIS);
    public static final DeferredItem<Item> MANA_STONE = registerGemItem("mana_stone", GemType.MANA);
    public static final DeferredItem<Item> END_STONE = registerGemItem("end_stone", GemType.END);
    public static final DeferredItem<Item> BLOOD_PEARL = registerGemItem("blood_pearl", GemType.BLOOD_PEARL);
    public static final DeferredItem<Item> NORTHERN_STONE = registerGemItem("northern_stone", GemType.NORTHERN);
    public static final DeferredItem<Item> PYRITE_STONE = registerGemItem("pyrite_stone", GemType.PYRITE);
    public static final DeferredItem<Item> MOON_PEARL = registerGemItem("moon_pearl", GemType.MOON_PEARL);
    public static final DeferredItem<Item> DRAGON_STONE = registerGemItem("dragon_stone", GemType.DRAGON);
    public static final DeferredItem<Item> NATURE_STONE = registerGemItem("nature_stone", GemType.NATURE);
    public static final DeferredItem<Item> DIAMOND_STONE = registerGemItem("diamond_stone", GemType.DIAMOND);
    public static final DeferredItem<Item> AMETHYST_STONE = registerGemItem("amethyst_stone", GemType.AMETHYST);
    public static final DeferredItem<Item> HEALTH_STONE = registerGemItem("health_stone", GemType.HEALTH);
    public static final DeferredItem<Item> EMERALD_STONE = registerGemItem("emerald_stone", GemType.EMERALD);
    public static final DeferredItem<Item> AQUAMARINE_STONE = registerGemItem("aquamarine_stone", GemType.AQUAMARINE);

    public static final List<DeferredItem<Item>> GEM_ITEMS = List.of(
            RUBY_STONE, SAPPHIRE_STONE, TOPAZ_STONE, WHITE_STONE, BLACK_STONE, CHRIZOLITE_STONE,
            MALACHITE_STONE, GARNET_STONE, LAPIS_STONE, MANA_STONE, END_STONE, BLOOD_PEARL,
            NORTHERN_STONE, PYRITE_STONE, MOON_PEARL, DRAGON_STONE, NATURE_STONE, DIAMOND_STONE,
            AMETHYST_STONE, HEALTH_STONE, EMERALD_STONE, AQUAMARINE_STONE
    );

    public static final DeferredItem<Item> DIAMOND_SMITHHAMMER = ITEMS.register("diamond_smithhammer", DiamondSmithHammerItem::new);
    public static final DeferredItem<Item> GOLDEN_SMITHHAMMER = ITEMS.register("golden_smithhammer", GoldenSmithHammerItem::new);
    public static final DeferredItem<Item> IRON_SMITHHAMMER = ITEMS.register("iron_smithhammer", IronSmithHammerItem::new);
    public static final DeferredItem<Item> NETHERITE_SMITHHAMMER = ITEMS.register("netherite_smithhammer", NetheriteSmithHammerItem::new);

    public static final DeferredItem<Item> DRAGONSTEEL_BLOCK_ITEM = ITEMS.register("dragonsteel_block",
            () -> new BlockItem(PBMBlocks.DRAGONSTEEL_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> ETHEREAL_BLOCK_ITEM = ITEMS.register("ethereal_block",
            () -> new BlockItem(PBMBlocks.ETHEREAL_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> EVERFROST_BLOCK_ITEM = ITEMS.register("everfrost_block",
            () -> new BlockItem(PBMBlocks.EVERFROST_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> MAGICAL_ICE_DEEPSLATE_ORE_ITEM = ITEMS.register("magical_ice_deepslate_ore",
            () -> new BlockItem(PBMBlocks.MAGICAL_ICE_DEEPSLATE_ORE.get(), new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_INGOT_BLOCK_ITEM = ITEMS.register("diamond_ingot_block",
            () -> new BlockItem(PBMBlocks.DIAMOND_INGOT_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> STEEL_BLOCK_ITEM = ITEMS.register("steel_block",
            () -> new BlockItem(PBMBlocks.STEEL_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> INFUSED_GOLD_BLOCK_ITEM = ITEMS.register("infused_gold_block",
            () -> new BlockItem(PBMBlocks.INFUSED_GOLD_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> RUBY_BLOCK_ITEM = ITEMS.register("ruby_block",
            () -> new BlockItem(PBMBlocks.RUBY_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> RUBY_ORE_ITEM = ITEMS.register("ruby_ore",
            () -> new BlockItem(PBMBlocks.RUBY_ORE.get(), new Item.Properties()));
    public static final DeferredItem<Item> RUBY_DEEPSLATE_ORE_ITEM = ITEMS.register("ruby_deepslate_ore",
            () -> new BlockItem(PBMBlocks.RUBY_DEEPSLATE_ORE.get(), new Item.Properties()));

    public static final DeferredItem<Item> MAGICAL_INFUSER_BLOCK_ITEM = ITEMS.register("magical_infuser_block",
            () -> new BlockItem(PBMBlocks.MAGICAL_INFUSER_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> JEWERLY_TABLE_BLOCK_ITEM = ITEMS.register("jewerly_table_block",
            () -> new BlockItem(PBMBlocks.JEWERLY_TABLE_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> REFORGE_TABLE_BLOCK_ITEM = ITEMS.register("reforge_table_block",
            () -> new BlockItem(PBMBlocks.REFORGE_TABLE_BLOCK.get(), new Item.Properties()));
    public static final DeferredItem<Item> REFINEMENT_TABLE_BLOCK_ITEM = ITEMS.register("refinement_table_block",
            () -> new BlockItem(PBMBlocks.REFINEMENT_TABLE_BLOCK.get(), new Item.Properties()));

    private PBMItems() {
    }

    private static DeferredItem<Item> registerSimpleItem(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    private static DeferredItem<Item> registerGemItem(String name, GemType gemType) {
        return ITEMS.register(name, () -> new GemItem(gemType));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
