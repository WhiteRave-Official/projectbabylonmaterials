package com.rave.projectbabylonmaterials.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.menu.MagicalInfuserMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MagicalInfuserScreen extends AbstractContainerScreen<MagicalInfuserMenu> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProjectBabylonMaterials.MODID, "textures/gui/container/magical_infuser.png");
    private static final int FUEL_U = 176;
    private static final int FUEL_V = 17;
    private static final int FUEL_WIDTH = 6;
    private static final int FUEL_HEIGHT = 25;

    public MagicalInfuserScreen(MagicalInfuserMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.titleLabelY = 5;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = this.leftPos;
        int y = this.topPos;
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        int fuel = Math.min(20, this.menu.getFuelOperations());
        if (fuel > 0) {
            int fuelHeight = fuel * FUEL_HEIGHT / 20;
            guiGraphics.blit(
                    GUI_TEXTURE,
                    x + 40,
                    y + 13 + (FUEL_HEIGHT - fuelHeight),
                    FUEL_U,
                    FUEL_V + (FUEL_HEIGHT - fuelHeight),
                    FUEL_WIDTH,
                    fuelHeight
            );
        }

        if (this.menu.hasProgress()) {
            int progress = this.menu.getScaledProgress(24);
            guiGraphics.blit(GUI_TEXTURE, x + 95, y + 34, 176, 0, progress + 1, 16);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
