package com.rave.projectbabylonmaterials.init;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PBMCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ProjectBabylonMaterials.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MATERIALS_TAB = CREATIVE_MODE_TABS.register("materials_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.project_babylon_materials.materials_tab"))
                    .icon(() -> PBMItems.DRAGONSTEEL_INGOT.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(PBMItems.DRAGONSTEEL_INGOT.get());
                        output.accept(PBMItems.DRAGONSTEEL_PLATE.get());
                        output.accept(PBMItems.DRAGONSTEEL_NUGGET.get());
                        output.accept(PBMItems.ETHEREAL_INGOT.get());
                        output.accept(PBMItems.ETHEREAL_PLATE.get());
                        output.accept(PBMItems.ETHEREAL_NUGGET.get());
                        output.accept(PBMItems.EVERFROST_INGOT.get());
                        output.accept(PBMItems.EVERFROST_PLATE.get());
                        output.accept(PBMItems.EVERFROST_NUGGET.get());
                        output.accept(PBMItems.INFUSED_GOLDEN_INGOT.get());
                        output.accept(PBMItems.INFUSED_GOLDEN_PLATE.get());
                        output.accept(PBMItems.INFUSED_GOLDEN_NUGGET.get());
                        output.accept(PBMItems.DIAMOND_INGOT.get());
                        output.accept(PBMItems.DIAMOND_PLATE.get());
                        output.accept(PBMItems.DIAMOND_INGOT_NUGGET.get());
                        output.accept(PBMItems.STEEL_INGOT.get());
                        output.accept(PBMItems.STEEL_PLATE.get());
                        output.accept(PBMItems.STEEL_NUGGET.get());
                        output.accept(PBMItems.IRON_PLATE.get());
                        output.accept(PBMItems.NETHERITE_PLATE.get());
                        output.accept(PBMItems.NETHERITE_NUGGET.get());
                        output.accept(PBMItems.PURE_TEAR.get());
                        output.accept(PBMItems.ANCIENT_AMBER.get());
                        output.accept(PBMItems.MAGIC_CRYSTAL.get());
                        output.accept(PBMItems.FATE_ORB.get());
                        output.accept(PBMItems.DIAMOND_SMITHHAMMER.get());
                        output.accept(PBMItems.GOLDEN_SMITHHAMMER.get());
                        output.accept(PBMItems.IRON_SMITHHAMMER.get());
                        output.accept(PBMItems.NETHERITE_SMITHHAMMER.get());

                        output.accept(PBMItems.DRAGONSTEEL_BLOCK_ITEM.get());
                        output.accept(PBMItems.ETHEREAL_BLOCK_ITEM.get());
                        output.accept(PBMItems.EVERFROST_BLOCK_ITEM.get());
                        output.accept(PBMItems.INFUSED_GOLD_BLOCK_ITEM.get());
                        output.accept(PBMItems.DIAMOND_INGOT_BLOCK_ITEM.get());
                        output.accept(PBMItems.MAGICAL_ICE_DEEPSLATE_ORE_ITEM.get());
                        output.accept(PBMItems.STEEL_BLOCK_ITEM.get());
                        output.accept(PBMItems.MAGICAL_INFUSER_BLOCK_ITEM.get());
                        output.accept(PBMItems.JEWERLY_TABLE_BLOCK_ITEM.get());
                        output.accept(PBMItems.REFORGE_TABLE_BLOCK_ITEM.get());
                        output.accept(PBMItems.REFINEMENT_TABLE_BLOCK_ITEM.get());

                        output.accept(PBMItems.MAGICAL_ICE_SHARD.get());
                        output.accept(PBMItems.MAGIC_DUST.get());
                        output.accept(PBMItems.GEM_DUST.get());
                        output.accept(PBMItems.AMETHYST_DUST.get());
                        output.accept(PBMItems.QUARTZ_DUST.get());
                        output.accept(PBMItems.DIAMOND_DUST.get());
                        output.accept(PBMItems.RUBY.get());
                        output.accept(PBMItems.RUBY_DUST.get());

                        output.accept(PBMItems.CLOTH.get());
                        output.accept(PBMItems.DIAMOND_CLOTH.get());
                        output.accept(PBMItems.GOLDEN_CLOTH.get());
                        output.accept(PBMItems.ICE_CLOTH.get());
                        output.accept(PBMItems.NETHERITE_CLOTH.get());
                        output.accept(PBMItems.ETHEREAL_CLOTH.get());
                        output.accept(PBMItems.DRAGONSTEEL_CLOTH.get());

                        output.accept(PBMItems.BOWL_AND_MORTAR.get());
                        output.accept(PBMItems.HANDLE.get());
                        output.accept(PBMItems.SHAFT.get());
                        output.accept(PBMItems.REINFORCED_HANDLE.get());
                        output.accept(PBMItems.REINFORCED_SHAFT.get());
                        output.accept(PBMItems.RUBY_BLOCK_ITEM.get());
                        output.accept(PBMItems.RUBY_ORE_ITEM.get());
                        output.accept(PBMItems.RUBY_DEEPSLATE_ORE_ITEM.get());
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GEMS_TAB = CREATIVE_MODE_TABS.register("gems_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.project_babylon_materials.gems_tab"))
                    .icon(() -> PBMItems.RUBY_STONE.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(PBMItems.RUBY_STONE.get());
                        output.accept(PBMItems.SAPPHIRE_STONE.get());
                        output.accept(PBMItems.TOPAZ_STONE.get());
                        output.accept(PBMItems.WHITE_STONE.get());
                        output.accept(PBMItems.BLACK_STONE.get());
                        output.accept(PBMItems.CHRIZOLITE_STONE.get());
                        output.accept(PBMItems.MALACHITE_STONE.get());
                        output.accept(PBMItems.GARNET_STONE.get());
                        output.accept(PBMItems.LAPIS_STONE.get());
                        output.accept(PBMItems.MANA_STONE.get());
                        output.accept(PBMItems.END_STONE.get());
                        output.accept(PBMItems.BLOOD_PEARL.get());
                        output.accept(PBMItems.NORTHERN_STONE.get());
                        output.accept(PBMItems.PYRITE_STONE.get());
                        output.accept(PBMItems.MOON_PEARL.get());
                        output.accept(PBMItems.DRAGON_STONE.get());
                        output.accept(PBMItems.NATURE_STONE.get());
                        output.accept(PBMItems.DIAMOND_STONE.get());
                        output.accept(PBMItems.AMETHYST_STONE.get());
                        output.accept(PBMItems.HEALTH_STONE.get());
                        output.accept(PBMItems.EMERALD_STONE.get());
                        output.accept(PBMItems.AQUAMARINE_STONE.get());
                    })
                    .build());

    private PBMCreativeTabs() {
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
