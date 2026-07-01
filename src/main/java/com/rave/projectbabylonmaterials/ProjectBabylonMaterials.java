package com.rave.projectbabylonmaterials;

import com.mojang.logging.LogUtils;
import com.rave.projectbabylonmaterials.config.PBMClientConfig;
import com.rave.projectbabylonmaterials.config.PBMServerConfig;
import com.rave.projectbabylonmaterials.handler.CritDamageHandler;
import com.rave.projectbabylonmaterials.handler.LivingEntityHealthHandler;
import com.rave.projectbabylonmaterials.handler.PlayerHealthHandler;
import com.rave.projectbabylonmaterials.handler.VanillaEnchantmentRebalanceHandler;
import com.rave.projectbabylonmaterials.init.PBAttributes;
import com.rave.projectbabylonmaterials.init.PBMBlockEntities;
import com.rave.projectbabylonmaterials.init.PBMBlocks;
import com.rave.projectbabylonmaterials.init.PBMCreativeTabs;
import com.rave.projectbabylonmaterials.init.PBMEffects;
import com.rave.projectbabylonmaterials.init.PBMEnchantments;
import com.rave.projectbabylonmaterials.init.PBMItems;
import com.rave.projectbabylonmaterials.init.PBMMenus;
import com.rave.projectbabylonmaterials.init.PBMRecipes;
import com.rave.projectbabylonmaterials.network.PBNetwork;
import com.rave.projectbabylonmaterials.setbonus.ArmorSetBonusManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ProjectBabylonMaterials.MODID)
public class ProjectBabylonMaterials {
    public static final String MODID = "project_babylon_materials";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ProjectBabylonMaterials(FMLJavaModLoadingContext context) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, PBMClientConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, PBMServerConfig.SPEC);
        IEventBus modBus = context.getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        PBNetwork.register();
        PBAttributes.register(modBus);
        PBMBlocks.register(modBus);
        PBMItems.register(modBus);
        PBMCreativeTabs.register(modBus);
        PBMEffects.register(modBus);
        PBMEnchantments.register(modBus);
        PBMBlockEntities.register(modBus);
        PBMMenus.register(modBus);
        PBMRecipes.register(modBus);
        forgeBus.register(ArmorSetBonusManager.class);
        forgeBus.register(CritDamageHandler.class);
        forgeBus.register(LivingEntityHealthHandler.class);
        forgeBus.register(PlayerHealthHandler.class);
        forgeBus.register(VanillaEnchantmentRebalanceHandler.class);
    }
}