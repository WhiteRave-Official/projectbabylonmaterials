package com.rave.projectbabylonmaterials.client.tooltip;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.setbonus.ArmorPieceTooltipData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public class ArmorPieceClientTooltip implements ClientTooltipComponent {

    private static final int ICON_SIZE = 16;
    private static final float ITEM_SCALE = 0.8F;
    private static final float ITEM_OFFSET = (ICON_SIZE - (ICON_SIZE * ITEM_SCALE)) / 2.0F;
    private static final int ICON_TEXT_GAP = 4;
    private static final ResourceLocation EQUIPPED_FRAME = ResourceLocation.fromNamespaceAndPath(
            ProjectBabylonMaterials.MODID, "textures/gui/tooltip/frame/set_bonus_frame_equiped.png");
    private static final ResourceLocation MISSING_FRAME = ResourceLocation.fromNamespaceAndPath(
            ProjectBabylonMaterials.MODID, "textures/gui/tooltip/frame/set_bonus_frame_missing.png");
    private final ArmorPieceTooltipData data;

    public ArmorPieceClientTooltip(ArmorPieceTooltipData data) {
        this.data = data;
    }

    @Override
    public int getHeight() {
        return ICON_SIZE;
    }

    @Override
    public int getWidth(Font font) {
        return ICON_SIZE + ICON_TEXT_GAP + font.width(createLineComponent());
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
        FormattedCharSequence text = createLineComponent().getVisualOrderText();
        font.drawInBatch(text, x + ICON_SIZE + ICON_TEXT_GAP, y + 4, 0xFFFFFF, false, matrix, bufferSource,
                Font.DisplayMode.NORMAL, 0, 15728880);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        guiGraphics.blit(data.equipped() ? EQUIPPED_FRAME : MISSING_FRAME, x, y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + ITEM_OFFSET, y + ITEM_OFFSET, 0.0F);
        guiGraphics.pose().scale(ITEM_SCALE, ITEM_SCALE, 1.0F);
        guiGraphics.renderItem(data.stack(), 0, 0);
        guiGraphics.pose().popPose();
    }

    private Component createLineComponent() {
        ChatFormatting stateColor = data.equipped() ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY;
        String stateKey = data.equipped() ? "tooltip.project_babylon_materials.equipped" : "tooltip.project_babylon_materials.missing";
        return data.label().copy().withStyle(ChatFormatting.GRAY)
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.translatable(stateKey).withStyle(stateColor));
    }
}
