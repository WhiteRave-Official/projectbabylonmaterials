package com.rave.projectbabylonmaterials.init;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.menu.JewelryTableMenu;
import com.rave.projectbabylonmaterials.menu.MagicalInfuserMenu;
import com.rave.projectbabylonmaterials.menu.RefinementTableMenu;
import com.rave.projectbabylonmaterials.menu.ReforgeTableMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PBMMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, ProjectBabylonMaterials.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<MagicalInfuserMenu>> MAGICAL_INFUSER_MENU =
            MENUS.register("magical_infuser", () -> IMenuTypeExtension.create(MagicalInfuserMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<JewelryTableMenu>> JEWELRY_TABLE_MENU =
            MENUS.register("jewelry_table", () -> IMenuTypeExtension.create(JewelryTableMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ReforgeTableMenu>> REFORGE_TABLE_MENU =
            MENUS.register("reforge_table", () -> IMenuTypeExtension.create(ReforgeTableMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RefinementTableMenu>> REFINEMENT_TABLE_MENU =
            MENUS.register("refinement_table", () -> IMenuTypeExtension.create(RefinementTableMenu::new));

    private PBMMenus() {
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
