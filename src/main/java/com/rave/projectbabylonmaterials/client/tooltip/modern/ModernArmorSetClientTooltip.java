package com.rave.projectbabylonmaterials.client.tooltip.modern;

import com.rave.projectbabylonmaterials.setbonus.ArmorSetTooltipData;
import com.rave.projectbabylonmaterials.tooltip.TooltipFrameStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ModernArmorSetClientTooltip implements ClientTooltipComponent {
    private static final Set<String> FRAME_MATERIALS = Set.of("iron", "diamond", "netherite", "golden",
            "ice", "ethereal", "evergate", "arclight", "dragonsteel");
    private final ArmorSetTooltipData data;
    private final List<Component> title;
    private final List<BonusCard> cards = new ArrayList<>();
    private final int width;
    private final int height;
    private final int piecesY;
    private final TooltipFrameStyle frame;

    public ModernArmorSetClientTooltip(ArmorSetTooltipData data) {
        this.data = data;
        Font font = Minecraft.getInstance().font;
        width = ModernTooltipLayout.contentWidth(Minecraft.getInstance().getWindow().getGuiScaledWidth());
        title = ModernTooltipDrawing.wrap(font, data.setName(), width - 64);
        piecesY = 12 + title.size() * (font.lineHeight + 2);
        int y = piecesY + 38;
        for (var bonus : data.bonuses()) {
            List<Component> heading = ModernTooltipDrawing.wrap(font, bonus.type().getTitle(), width - 64);
            List<Component> name = ModernTooltipDrawing.wrap(font, bonus.displayName(), width - 64);
            List<Component> body = new ArrayList<>();
            for (Component line : bonus.descriptionLines()) {
                body.addAll(ModernTooltipDrawing.wrap(font, line.copy().withStyle(style -> style.withColor(ModernTooltipDrawing.TEXT)), width - 44));
            }
            int cardHeight = Math.max(48, 32 + (heading.size() + name.size() + body.size()) * (font.lineHeight + 2) + 5);
            cards.add(new BonusCard(bonus, heading, name, body, y, cardHeight));
            y += cardHeight + 6;
        }
        height = y;
        frame = selectFrame(data);
    }

    private static TooltipFrameStyle selectFrame(ArmorSetTooltipData data) {
        for (var bonus : data.bonuses()) {
            String path = bonus.frameTexture().getPath();
            String name = path.substring(path.lastIndexOf('/') + 1).replace("_material_frame.png", "");
            if (name.equals("gold")) {
                name = "golden";
            }
            if (FRAME_MATERIALS.contains(name)) {
                return TooltipFrameStyle.material(name);
            }
        }
        return TooltipFrameStyle.iron();
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth(Font font) {
        return width;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        ModernTooltipDrawing.divider(graphics, x, y + 2, width);
        ModernTooltipDrawing.text(graphics, font, title, x + 4, y + 9, ModernTooltipDrawing.ACCENT);
        Component count = Component.literal(data.matchedPieces() + " / " + data.armorPieces().size());
        graphics.drawString(font, count, x + width - font.width(count) - 4, y + 9,
                data.matchedPieces() == data.armorPieces().size() ? ModernTooltipDrawing.POSITIVE : ModernTooltipDrawing.MUTED, false);
        int slots = Math.max(1, data.armorPieces().size());
        int slotWidth = width / slots;
        for (int i = 0; i < data.armorPieces().size(); i++) {
            var piece = data.armorPieces().get(i);
            int slotX = x + i * slotWidth + (slotWidth - 26) / 2;
            int slotY = y + piecesY;
            graphics.fill(slotX, slotY, slotX + 26, slotY + 26, ModernTooltipDrawing.PANEL);
            ModernTooltipDrawing.outline(graphics, slotX, slotY, 26, 26,
                    piece.equipped() ? 0xFF6EDDD8 : ModernTooltipDrawing.BORDER);
            graphics.renderItem(piece.stack(), slotX + 5, slotY + 4);
            graphics.fill(slotX + 4, slotY + 23, slotX + 22, slotY + 24,
                    piece.equipped() ? 0xFF6EDDD8 : 0xFF454A52);
        }
        for (BonusCard card : cards) {
            int top = y + card.y();
            graphics.fill(x + 3, top + 3, x + width - 3, top + card.height() - 3, ModernTooltipDrawing.PANEL);
            graphics.flush();
            NineSliceTooltipFrame.render(graphics, x, top, width, card.height(), frame);
            graphics.blit(card.data().frameTexture(), x + 20, top + 16, 0, 0, 16, 16, 16, 16);
            graphics.blit(card.data().iconTexture(), x + 20, top + 16, 0, 0, 16, 16, 16, 16);
            int textY = ModernTooltipDrawing.text(graphics, font, card.heading(), x + 42, top + 16, ModernTooltipDrawing.ACCENT);
            textY = ModernTooltipDrawing.text(graphics, font, card.name(), x + 42, textY, ModernTooltipDrawing.POSITIVE);
            ModernTooltipDrawing.text(graphics, font, card.body(), x + 22, textY + 4, ModernTooltipDrawing.TEXT);
        }
    }

    private record BonusCard(ArmorSetTooltipData.BonusEntry data, List<Component> heading,
                             List<Component> name, List<Component> body, int y, int height) {
    }
}
