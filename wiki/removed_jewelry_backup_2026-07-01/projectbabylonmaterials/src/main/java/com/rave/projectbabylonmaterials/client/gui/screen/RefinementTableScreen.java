package com.rave.projectbabylonmaterials.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.menu.RefinementTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class RefinementTableScreen extends AbstractContainerScreen<RefinementTableMenu> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProjectBabylonMaterials.MODID, "textures/gui/container/refinement_table.png");
    private static final ResourceLocation HAMMER_ICON = new ResourceLocation(ProjectBabylonMaterials.MODID, "textures/gui/container/hammer_icon.png");
    private static final ResourceLocation HAMMER_ICON_HOVER = new ResourceLocation(ProjectBabylonMaterials.MODID, "textures/gui/container/hammer_icon_chosen.png");
    private static final ResourceLocation EXP_ICON = new ResourceLocation(ProjectBabylonMaterials.MODID, "textures/gui/container/exp_icon.png");
    private static final ResourceLocation WEAPON_SLOT_ICON = new ResourceLocation(ProjectBabylonMaterials.MODID, "textures/gui/container/slots/weapon_slot_icon.png");
    private static final int HAMMER_X = 79;
    private static final int HAMMER_Y = 73;
    private static final int HAMMER_SIZE = 16;
    private static final int XP_TEXT_X = 81;
    private static final int XP_TEXT_Y = 40;
    private static final int XP_ICON_X = 85;
    private static final int XP_ICON_Y = 36;
    private static final int XP_ICON_SIZE = 16;
    private static final float XP_SCALE = 0.8F;
    private static final int SLOT_ICON_SIZE = 16;

    public RefinementTableScreen(RefinementTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 176;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelY = 5;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = this.leftPos;
        int y = this.topPos;
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
        renderSlotPlaceholder(guiGraphics, 2, WEAPON_SLOT_ICON);
        renderHammerButton(guiGraphics, mouseX, mouseY);
        renderUpgradeCost(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.menu.canAttemptExtraction() && isHammerHovered(mouseX, mouseY)) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderHammerButton(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = this.leftPos + HAMMER_X;
        int y = this.topPos + HAMMER_Y;
        boolean hovered = isHammerHovered(mouseX, mouseY);
        guiGraphics.blit(hovered ? HAMMER_ICON_HOVER : HAMMER_ICON, x, y, 0, 0, HAMMER_SIZE, HAMMER_SIZE, HAMMER_SIZE, HAMMER_SIZE);
    }

    private void renderUpgradeCost(GuiGraphics guiGraphics) {
        if (!this.menu.canAttemptExtraction()) {
            return;
        }

        int textX = this.leftPos + XP_TEXT_X;
        int textY = this.topPos + XP_TEXT_Y;
        int iconX = this.leftPos + XP_ICON_X;
        int iconY = this.topPos + XP_ICON_Y;
        int color = this.minecraft != null && this.minecraft.player != null && this.minecraft.player.experienceLevel >= this.menu.getRequiredXp()
                ? 0x80FF20
                : 0xFF5555;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(textX, textY, 0.0F);
        guiGraphics.pose().scale(XP_SCALE, XP_SCALE, 1.0F);
        guiGraphics.drawString(this.font, Integer.toString(this.menu.getRequiredXp()), 0, 0, color, false);
        guiGraphics.pose().popPose();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(iconX, iconY, 0.0F);
        guiGraphics.pose().scale(XP_SCALE, XP_SCALE, 1.0F);
        guiGraphics.blit(EXP_ICON, 0, 0, 0, 0, XP_ICON_SIZE, XP_ICON_SIZE, XP_ICON_SIZE, XP_ICON_SIZE);
        guiGraphics.pose().popPose();
    }

    private boolean isHammerHovered(double mouseX, double mouseY) {
        int x = this.leftPos + HAMMER_X;
        int y = this.topPos + HAMMER_Y;
        return mouseX >= x && mouseX < x + HAMMER_SIZE && mouseY >= y && mouseY < y + HAMMER_SIZE;
    }

    private void renderSlotPlaceholder(GuiGraphics guiGraphics, int slotIndex, ResourceLocation iconTexture) {
        Slot slot = this.menu.slots.get(slotIndex);
        if (slot.hasItem()) {
            return;
        }

        guiGraphics.blit(iconTexture, this.leftPos + slot.x, this.topPos + slot.y, 0, 0, SLOT_ICON_SIZE, SLOT_ICON_SIZE, SLOT_ICON_SIZE, SLOT_ICON_SIZE);
    }
}
