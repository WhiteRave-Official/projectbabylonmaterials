package com.rave.projectbabylonmaterials;

import com.mojang.logging.LogUtils;
import com.rave.projectbabylonmaterials.init.PBMBlocks;
import com.rave.projectbabylonmaterials.init.PBMBlockEntities;
import com.rave.projectbabylonmaterials.init.PBMCreativeTabs;
import com.rave.projectbabylonmaterials.init.PBMItems;
import com.rave.projectbabylonmaterials.init.PBMMenus;
import com.rave.projectbabylonmaterials.init.PBMRecipes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ProjectBabylonMaterials.MODID)
public class ProjectBabylonMaterials {
    public static final String MODID = "project_babylon_materials";
    public static final Logger LOGGER = LogUtils.getLogger();


    public ProjectBabylonMaterials(FMLJavaModLoadingContext context) {
        var modEventBus = context.getModEventBus();
        PBMBlocks.register(modEventBus);
        PBMItems.register(modEventBus);
        PBMCreativeTabs.register(modEventBus);
        PBMBlockEntities.register(modEventBus);
        PBMMenus.register(modEventBus);
        PBMRecipes.register(modEventBus);
    }
}
