package com.rave.projectbabylonmaterials.client.tooltip.epicfight;

import com.mojang.datafixers.util.Either;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.client.input.InputManager;
import yesman.epicfight.api.client.input.action.EpicFightInputAction;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EpicFightWeaponTooltipEvents {
    private EpicFightWeaponTooltipEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void gather(RenderTooltipEvent.GatherComponents event) {
        if (event.getItemStack().isEmpty() || event.getTooltipElements().isEmpty()) {
            return;
        }
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        var capability = EpicFightCapabilities.getItemStackCapabilityOr(event.getItemStack(), null);
        if (capability == null || capability == CapabilityItem.EMPTY) {
            return;
        }
        EpicFightCapabilities.getLocalPlayerPatchAsOptional(player).ifPresent(patch -> {
            var elements = event.getTooltipElements();
            if (InputManager.isActionPhysicallyActive(EpicFightInputAction.WEAPON_INNATE_SKILL_TOOLTIP)
                    && capability.getInnateSkill(patch, event.getItemStack()) != null) {
                return;
            }
            if (Screen.hasAltDown() || Screen.hasControlDown()) {
                return;
            }
            List<Component> nativeTooltip = new ArrayList<>();
            nativeTooltip.add(event.getItemStack().getHoverName());
            capability.modifyItemTooltip(event.getItemStack(), nativeTooltip, patch);
            List<Component> stats = nativeTooltip.stream().filter(EpicFightTooltipLines::isWeaponStat).toList();
            if (stats.isEmpty()) {
                return;
            }
            elements.removeIf(EpicFightWeaponTooltipEvents::isWeaponStat);
            int index = insertionIndex(elements);
            for (Component stat : stats) {
                elements.add(index++, Either.left(stat));
            }
        });
    }

    private static boolean isWeaponStat(Either<FormattedText, TooltipComponent> element) {
        return element.left().orElse(null) instanceof Component line && EpicFightTooltipLines.isWeaponStat(line);
    }

    private static int insertionIndex(List<Either<FormattedText, TooltipComponent>> elements) {
        int index = -1;
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).left().orElse(null) instanceof Component line
                    && (EpicFightTooltipLines.containsKey(line, "attribute.name.generic.attack_damage")
                    || EpicFightTooltipLines.containsKey(line, "attribute.name.generic.attack_speed"))) {
                index = i + 1;
            }
        }
        if (index >= 0) {
            return index;
        }
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).left().orElse(null) instanceof Component line
                    && EpicFightTooltipLines.containsKey(line, "inventory.epicfight.guide_innate_tooltip")) {
                return i;
            }
        }
        return Math.min(1, elements.size());
    }
}
