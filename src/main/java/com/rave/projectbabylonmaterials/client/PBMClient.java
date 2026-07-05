package com.rave.projectbabylonmaterials.client;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.client.gem.GemClientProperties;
import com.rave.projectbabylonmaterials.client.gui.screen.JewelryTableScreen;
import com.rave.projectbabylonmaterials.client.gui.screen.MagicalInfuserScreen;
import com.rave.projectbabylonmaterials.client.gui.screen.RefinementTableScreen;
import com.rave.projectbabylonmaterials.client.gui.screen.ReforgeTableScreen;
import com.rave.projectbabylonmaterials.client.overlay.CombatStatsOverlay;
import com.rave.projectbabylonmaterials.init.PBMItems;
import com.rave.projectbabylonmaterials.init.PBMMenus;
import com.rave.projectbabylonmaterials.item.gem.GemItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PBMClient {
    private static final ResourceLocation GEM_VISUAL_PROPERTY = ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "gem_visual");
    private static final ResourceLocation COMBAT_STATS_LAYER = ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, "combat_stats");

    private PBMClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(PBMClient::registerGemProperties);
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(PBMMenus.MAGICAL_INFUSER_MENU.get(), MagicalInfuserScreen::new);
        event.register(PBMMenus.JEWELRY_TABLE_MENU.get(), JewelryTableScreen::new);
        event.register(PBMMenus.REFORGE_TABLE_MENU.get(), ReforgeTableScreen::new);
        event.register(PBMMenus.REFINEMENT_TABLE_MENU.get(), RefinementTableScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(COMBAT_STATS_LAYER, CombatStatsOverlay.HUD);
    }

    private static void registerGemProperties() {
        for (DeferredHolder<Item, ? extends Item> itemHolder : PBMItems.ITEMS.getEntries()) {
            Item item = itemHolder.get();
            if (item instanceof GemItem) {
                ItemProperties.register(item, GEM_VISUAL_PROPERTY, (stack, level, entity, seed) -> GemClientProperties.getVisualProperty(stack));
            }
        }
    }
}
