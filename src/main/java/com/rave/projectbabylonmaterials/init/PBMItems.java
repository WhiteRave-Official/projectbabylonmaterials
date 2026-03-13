package com.rave.projectbabylonmaterials.init;

import com.rave.projectbabylonmaterials.item.smithhammer.DiamondSmithHammerItem;
import com.rave.projectbabylonmaterials.item.smithhammer.GoldenSmithHammerItem;
import com.rave.projectbabylonmaterials.item.smithhammer.IronSmithHammerItem;
import com.rave.projectbabylonmaterials.item.smithhammer.NetheriteSmithHammerItem;
import com.rave.projectbabylonmaterials.item.BowlAndMortarItem;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class PBMItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ProjectBabylonMaterials.MODID);

    public static final RegistryObject<Item> DRAGONSTEEL_INGOT = registerSimpleItem("dragonsteel_ingot");
    public static final RegistryObject<Item> DRAGONSTEEL_PLATE = registerSimpleItem("dragonsteel_plate");
    public static final RegistryObject<Item> DRAGONSTEEL_NUGGET = registerSimpleItem("dragonsteel_nugget");
    public static final RegistryObject<Item> ETHEREAL_INGOT = registerSimpleItem("ethereal_ingot");
    public static final RegistryObject<Item> ETHEREAL_PLATE = registerSimpleItem("ethereal_plate");
    public static final RegistryObject<Item> ETHEREAL_NUGGET = registerSimpleItem("ethereal_nugget");
    public static final RegistryObject<Item> EVERFROST_INGOT = registerSimpleItem("everfrost_ingot");
    public static final RegistryObject<Item> EVERFROST_PLATE = registerSimpleItem("everfrost_plate");
    public static final RegistryObject<Item> EVERFROST_NUGGET = registerSimpleItem("everfrost_nugget");
    public static final RegistryObject<Item> INFUSED_GOLDEN_INGOT = registerSimpleItem("infused_golden_ingot");
    public static final RegistryObject<Item> INFUSED_GOLDEN_PLATE = registerSimpleItem("infused_golden_plate");
    public static final RegistryObject<Item> INFUSED_GOLDEN_NUGGET = registerSimpleItem("infused_golden_nugget");
    public static final RegistryObject<Item> DIAMOND_INGOT = registerSimpleItem("diamond_ingot");
    public static final RegistryObject<Item> DIAMOND_PLATE = registerSimpleItem("diamond_plate");
    public static final RegistryObject<Item> DIAMOND_INGOT_NUGGET = registerSimpleItem("diamond_ingot_nugget");
    public static final RegistryObject<Item> STEEL_INGOT = registerSimpleItem("steel_ingot");
    public static final RegistryObject<Item> STEEL_PLATE = registerSimpleItem("steel_plate");
    public static final RegistryObject<Item> STEEL_NUGGET = registerSimpleItem("steel_nugget");
    public static final RegistryObject<Item> IRON_PLATE = registerSimpleItem("iron_plate");
    public static final RegistryObject<Item> NETHERITE_PLATE = registerSimpleItem("netherite_plate");
    public static final RegistryObject<Item> NETHERITE_NUGGET = registerSimpleItem("netherite_nugget");

    public static final RegistryObject<Item> MAGICAL_ICE_SHARD = registerSimpleItem("magical_ice_shard");
    public static final RegistryObject<Item> MAGIC_DUST = registerSimpleItem("magic_dust");
    public static final RegistryObject<Item> AMETHYST_DUST = registerSimpleItem("amethyst_dust");
    public static final RegistryObject<Item> QUARTZ_DUST = registerSimpleItem("quartz_dust");
    public static final RegistryObject<Item> DIAMOND_DUST = registerSimpleItem("diamond_dust");
    public static final RegistryObject<Item> RUBY = registerSimpleItem("ruby");
    public static final RegistryObject<Item> RUBY_DUST = registerSimpleItem("ruby_dust");

    public static final RegistryObject<Item> HANDLE = registerSimpleItem("handle");
    public static final RegistryObject<Item> SHAFT = registerSimpleItem("shaft");
    public static final RegistryObject<Item> REINFORCED_HANDLE = registerSimpleItem("reinforced_handle");
    public static final RegistryObject<Item> REINFORCED_SHAFT = registerSimpleItem("reinforced_shaft");
    public static final RegistryObject<Item> BOWL_AND_MORTAR = ITEMS.register("bowl_and_mortar", BowlAndMortarItem::new);

    public static final RegistryObject<Item> DIAMOND_SMITHHAMMER = ITEMS.register("diamond_smithhammer", DiamondSmithHammerItem::new);
    public static final RegistryObject<Item> GOLDEN_SMITHHAMMER = ITEMS.register("golden_smithhammer", GoldenSmithHammerItem::new);
    public static final RegistryObject<Item> IRON_SMITHHAMMER = ITEMS.register("iron_smithhammer", IronSmithHammerItem::new);
    public static final RegistryObject<Item> NETHERITE_SMITHHAMMER = ITEMS.register("netherite_smithhammer", NetheriteSmithHammerItem::new);

    public static final RegistryObject<Item> DRAGONSTEEL_BLOCK_ITEM = ITEMS.register("dragonsteel_block",
            () -> new BlockItem(PBMBlocks.DRAGONSTEEL_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> ETHEREAL_BLOCK_ITEM = ITEMS.register("ethereal_block",
            () -> new BlockItem(PBMBlocks.ETHEREAL_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> EVERFROST_BLOCK_ITEM = ITEMS.register("everfrost_block",
            () -> new BlockItem(PBMBlocks.EVERFROST_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> MAGICAL_ICE_DEEPSLATE_ORE_ITEM = ITEMS.register("magical_ice_deepslate_ore",
            () -> new BlockItem(PBMBlocks.MAGICAL_ICE_DEEPSLATE_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> DIAMOND_INGOT_BLOCK_ITEM = ITEMS.register("diamond_ingot_block",
            () -> new BlockItem(PBMBlocks.DIAMOND_INGOT_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> STEEL_BLOCK_ITEM = ITEMS.register("steel_block",
            () -> new BlockItem(PBMBlocks.STEEL_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> INFUSED_GOLD_BLOCK_ITEM = ITEMS.register("infused_gold_block",
            () -> new BlockItem(PBMBlocks.INFUSED_GOLD_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> RUBY_BLOCK_ITEM = ITEMS.register("ruby_block",
            () -> new BlockItem(PBMBlocks.RUBY_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> RUBY_ORE_ITEM = ITEMS.register("ruby_ore",
            () -> new BlockItem(PBMBlocks.RUBY_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RUBY_DEEPSLATE_ORE_ITEM = ITEMS.register("ruby_deepslate_ore",
            () -> new BlockItem(PBMBlocks.RUBY_DEEPSLATE_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> MAGICAL_INFUSER_BLOCK_ITEM = ITEMS.register("magical_infuser_block",
            () -> new BlockItem(PBMBlocks.MAGICAL_INFUSER_BLOCK.get(), new Item.Properties()));

    private PBMItems() {
    }

    private static RegistryObject<Item> registerSimpleItem(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
