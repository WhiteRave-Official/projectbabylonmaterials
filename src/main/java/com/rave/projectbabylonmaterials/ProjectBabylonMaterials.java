package com.rave.projectbabylonmaterials;

import com.mojang.logging.LogUtils;
import com.rave.projectbabylonmaterials.balance.PBMBalanceRegistries;
import com.rave.projectbabylonmaterials.balance.PBMBalances;
import com.rave.projectbabylonmaterials.hud.HudLayoutManager;
import com.rave.projectbabylonmaterials.hud.HudNetwork;
import com.rave.projectbabylonmaterials.config.PBMClientConfig;
import com.rave.projectbabylonmaterials.config.PBMServerConfig;
import com.rave.projectbabylonmaterials.handler.CritDamageHandler;
import com.rave.projectbabylonmaterials.handler.GemEffectHandler;
import com.rave.projectbabylonmaterials.handler.ItemRarityHandler;
import com.rave.projectbabylonmaterials.handler.LivingEntityHealthHandler;
import com.rave.projectbabylonmaterials.handler.PlayerHealthHandler;
import com.rave.projectbabylonmaterials.handler.VanillaEnchantmentRebalanceHandler;
import com.rave.projectbabylonmaterials.init.PBAttributes;
import com.rave.projectbabylonmaterials.init.PBMBlockEntities;
import com.rave.projectbabylonmaterials.init.PBMBlocks;
import com.rave.projectbabylonmaterials.init.PBMCreativeTabs;
import com.rave.projectbabylonmaterials.init.PBMDataComponents;
import com.rave.projectbabylonmaterials.init.PBMEffects;
import com.rave.projectbabylonmaterials.init.PBMItems;
import com.rave.projectbabylonmaterials.init.PBMLootModifiers;
import com.rave.projectbabylonmaterials.init.PBMMenus;
import com.rave.projectbabylonmaterials.init.PBMRecipes;
import com.rave.projectbabylonmaterials.network.PBNetwork;
import com.rave.projectbabylonmaterials.setbonus.ArmorSetBonusManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(ProjectBabylonMaterials.MODID)
public class ProjectBabylonMaterials {
    public static final String MODID = "project_babylon_materials";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ProjectBabylonMaterials(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, PBMClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, PBMServerConfig.SPEC);

        PBMDataComponents.register(modBus);
        PBAttributes.register(modBus);
        PBMBlocks.register(modBus);
        PBMItems.register(modBus);
        PBMCreativeTabs.register(modBus);
        PBMEffects.register(modBus);
        PBMBlockEntities.register(modBus);
        PBMMenus.register(modBus);
        PBMLootModifiers.register(modBus);
        PBMRecipes.register(modBus);

        // NeoForge payload registration happens on the mod bus during RegisterPayloadHandlersEvent.
        modBus.addListener(PBNetwork::register);
        modBus.addListener(PBMBalanceRegistries::registerDataPackRegistries);
        modBus.addListener(HudNetwork::register);

        IEventBus gameBus = NeoForge.EVENT_BUS;
        gameBus.register(ArmorSetBonusManager.class);
        gameBus.register(CritDamageHandler.class);
        gameBus.register(GemEffectHandler.class);
        gameBus.register(PBMBalances.class);
        gameBus.register(HudLayoutManager.class);
        gameBus.register(ItemRarityHandler.class);
        gameBus.register(LivingEntityHealthHandler.class);
        gameBus.register(PlayerHealthHandler.class);
        gameBus.register(VanillaEnchantmentRebalanceHandler.class);
    }
}
