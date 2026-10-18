package com.rave.projectbabylonmaterials.client.tooltip.modern;

import com.mojang.datafixers.util.Either;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.config.PBMClientConfig;
import com.rave.projectbabylonmaterials.setbonus.ArmorPieceTooltipData;
import com.rave.projectbabylonmaterials.setbonus.ArmorSetBonusManager;
import com.rave.projectbabylonmaterials.setbonus.ArmorSetBonusTooltipData;
import com.rave.projectbabylonmaterials.setbonus.ArmorSetTooltipData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModernTooltipEvents {
    private ModernTooltipEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void gather(RenderTooltipEvent.GatherComponents event) {
        if (!PBMClientConfig.modernTooltips() || event.getItemStack().isEmpty() || event.getTooltipElements().isEmpty()) {
            return;
        }
        var elements = event.getTooltipElements();
        if (elements.stream().anyMatch(element -> element.right().orElse(null) instanceof ModernItemTooltipData)) {
            return;
        }
        List<Either<FormattedText, TooltipComponent>> content = new ArrayList<>(elements);
        replaceArmorSection(event.getItemStack(), content);
        ModernItemTooltipData data = new ModernItemTooltipData(event.getItemStack(), content,
                event.getScreenWidth(), event.getScreenHeight());
        elements.clear();
        elements.add(Either.right(data));
    }

    @SubscribeEvent
    public static void colors(RenderTooltipEvent.Color event) {
        if (event.getComponents().stream().noneMatch(ModernItemClientTooltip.class::isInstance)) {
            return;
        }
        event.setBackgroundStart(0);
        event.setBackgroundEnd(0);
        event.setBorderStart(0);
        event.setBorderEnd(0);
    }

    static void replaceArmorSection(ItemStack stack, List<Either<FormattedText, TooltipComponent>> elements) {
        var player = Minecraft.getInstance().player;
        ArmorSetTooltipData data = ArmorSetBonusManager.createTooltipData(stack, player);
        if (data == null) {
            return;
        }
        List<Component> expected = new ArrayList<>();
        ArmorSetBonusManager.appendTooltip(stack, expected, true, player);
        int start = TooltipSectionMatcher.find(elements.stream().map(ModernTooltipEvents::line).toList(),
                expected.stream().map(component -> component.getString().strip()).toList());
        if (start >= 0) {
            elements.subList(start, start + expected.size()).clear();
            elements.add(start, Either.right(data));
        }
    }

    private static String line(Either<FormattedText, TooltipComponent> element) {
        if (element.left().isPresent()) {
            return element.left().get().getString().strip();
        }
        var data = element.right().orElse(null);
        if (data instanceof ArmorPieceTooltipData piece) {
            return piece.label().copy().append(": ").append(Component.translatable(piece.equipped()
                    ? "tooltip.project_babylon_materials.equipped" : "tooltip.project_babylon_materials.missing"))
                    .getString().strip();
        }
        return data instanceof ArmorSetBonusTooltipData bonus ? bonus.label().getString().strip() : null;
    }
}
