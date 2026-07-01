package com.rave.projectbabylonmaterials.client;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.client.gui.screen.MagicalInfuserScreen;
import com.rave.projectbabylonmaterials.client.overlay.CombatStatsOverlay;
import com.rave.projectbabylonmaterials.init.PBMMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class PBMClient {
    private static final ResourceLocation PULL_PROPERTY = ResourceLocation.withDefaultNamespace("pull");
    private static final ResourceLocation PULLING_PROPERTY = ResourceLocation.withDefaultNamespace("pulling");

    private PBMClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(PBMMenus.MAGICAL_INFUSER_MENU.get(), MagicalInfuserScreen::new);
            registerRangedDrawSpeedProperties();
        });
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("combat_stats", CombatStatsOverlay.HUD);
    }

    private static void registerRangedDrawSpeedProperties() {
        ItemProperties.register(Items.BOW, PULL_PROPERTY, (stack, level, entity, seed) ->
                entity == null ? 0.0F : RangedDrawSpeedClientProperties.getBowPull(stack, entity));
        ItemProperties.register(Items.BOW, PULLING_PROPERTY, (stack, level, entity, seed) ->
                entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

        ItemProperties.register(Items.CROSSBOW, PULL_PROPERTY, (stack, level, entity, seed) ->
                entity == null ? 0.0F : RangedDrawSpeedClientProperties.getCrossbowPull(stack, entity));
        ItemProperties.register(Items.CROSSBOW, PULLING_PROPERTY, (stack, level, entity, seed) ->
                entity != null && entity.isUsingItem() && entity.getUseItem() == stack && !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
    }
}
