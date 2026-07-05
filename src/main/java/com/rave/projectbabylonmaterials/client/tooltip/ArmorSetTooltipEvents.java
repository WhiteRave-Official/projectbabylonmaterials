package com.rave.projectbabylonmaterials.client.tooltip;
import com.mojang.datafixers.util.Either;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.setbonus.ArmorPieceTooltipData;
import com.rave.projectbabylonmaterials.setbonus.ArmorSetBonusManager;
import com.rave.projectbabylonmaterials.setbonus.ArmorSetBonusTooltipData;
import com.rave.projectbabylonmaterials.setbonus.ArmorSetTooltipData;
import com.rave.projectbabylonmaterials.tooltip.DescriptionBoxTooltipData;
import com.rave.projectbabylonmaterials.tooltip.EnchantmentDetailsTooltipData;
import com.rave.projectbabylonmaterials.tooltip.GemDetailsTooltipData;
import com.rave.projectbabylonmaterials.tooltip.IconLabelTooltipData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import java.util.List;
@EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class ArmorSetTooltipEvents {
    private ArmorSetTooltipEvents() {
    }
    @SubscribeEvent
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        if (!Screen.hasShiftDown()) {
            return;
        }
        ArmorSetTooltipData data = ArmorSetBonusManager.createTooltipData(event.getItemStack(), Minecraft.getInstance().player);
        if (data == null) {
            return;
        }
        List<Either<net.minecraft.network.chat.FormattedText, net.minecraft.world.inventory.tooltip.TooltipComponent>> elements = event.getTooltipElements();
        for (int i = 0; i < elements.size(); i++) {
            Either<net.minecraft.network.chat.FormattedText, net.minecraft.world.inventory.tooltip.TooltipComponent> element = elements.get(i);
            if (element.left().isEmpty()) {
                continue;
            }
            String line = normalizeLine(element.left().get().getString());
            ArmorSetTooltipData.ArmorPieceEntry piece = findArmorPieceForLine(data, line);
            if (piece != null) {
                elements.set(i, Either.right(new ArmorPieceTooltipData(piece.stack(), piece.label(), piece.equipped())));
                continue;
            }
            ArmorSetTooltipData.BonusEntry bonus = findBonusForLine(data, line);
            if (bonus != null) {
                Component label = bonus.type().getTitle().copy().withStyle(ChatFormatting.GOLD)
                        .append(Component.literal(": ").withStyle(ChatFormatting.GOLD))
                        .append(bonus.displayName().copy().withStyle(ChatFormatting.AQUA));
                elements.set(i, Either.right(new ArmorSetBonusTooltipData(label, bonus.frameTexture(), bonus.iconTexture())));
            }
        }
    }
    @EventBusSubscriber(modid = ProjectBabylonMaterials.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static final class ModBusEvents {
        private ModBusEvents() {
        }
        @SubscribeEvent
        public static void onRegisterTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(ArmorPieceTooltipData.class, ArmorPieceClientTooltip::new);
            event.register(ArmorSetBonusTooltipData.class, ArmorSetBonusClientTooltip::new);
            event.register(IconLabelTooltipData.class, IconLabelClientTooltip::new);
            event.register(DescriptionBoxTooltipData.class, DescriptionBoxClientTooltip::new);
            event.register(EnchantmentDetailsTooltipData.class, EnchantmentDetailsClientTooltip::new);
            event.register(GemDetailsTooltipData.class, GemDetailsClientTooltip::new);
        }
    }
    private static ArmorSetTooltipData.ArmorPieceEntry findArmorPieceForLine(ArmorSetTooltipData data, String line) {
        for (ArmorSetTooltipData.ArmorPieceEntry piece : data.armorPieces()) {
            String expected = normalizeLine(createArmorPieceLine(piece).getString());
            if (line.equals(expected)) {
                return piece;
            }
        }
        return null;
    }
    private static ArmorSetTooltipData.BonusEntry findBonusForLine(ArmorSetTooltipData data, String line) {
        for (ArmorSetTooltipData.BonusEntry bonus : data.bonuses()) {
            String expected = normalizeLine(createBonusLine(bonus).getString());
            if (line.equals(expected)) {
                return bonus;
            }
        }
        return null;
    }
    private static Component createArmorPieceLine(ArmorSetTooltipData.ArmorPieceEntry piece) {
        ChatFormatting stateColor = piece.equipped() ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY;
        String stateKey = piece.equipped() ? "tooltip.project_babylon_materials.equipped" : "tooltip.project_babylon_materials.missing";
        return piece.label().copy().withStyle(ChatFormatting.GRAY)
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.translatable(stateKey).withStyle(stateColor));
    }
    private static Component createBonusLine(ArmorSetTooltipData.BonusEntry bonus) {
        return bonus.type().getTitle().copy().withStyle(ChatFormatting.GOLD)
                .append(Component.literal(": ").withStyle(ChatFormatting.GOLD))
                .append(bonus.displayName().copy().withStyle(ChatFormatting.AQUA));
    }
    private static String normalizeLine(String line) {
        return line.stripLeading().stripTrailing();
    }
}