package com.rave.projectbabylonmaterials.client.photon;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class LightBreachModels {
    static final ResourceLocation CYLINDER = ResourceLocation.fromNamespaceAndPath(
            ProjectBabylonMaterials.MODID, "particle/light_breach_cylinder");

    private LightBreachModels() {}

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        event.register(CYLINDER);
    }
}
