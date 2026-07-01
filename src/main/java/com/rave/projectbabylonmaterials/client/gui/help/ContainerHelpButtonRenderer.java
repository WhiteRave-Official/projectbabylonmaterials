package com.rave.projectbabylonmaterials.client.gui.help;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class ContainerHelpButtonRenderer {
    private static final ResourceLocation BUTTON_TEXTURE =
            new ResourceLocation(ProjectBabylonMaterials.MODID, "textures/gui/container/container_help_button.png");
    private static final ResourceLocation BUTTON_HOVER_TEXTURE =
            new ResourceLocation(ProjectBabylonMaterials.MODID, "textures/gui/container/container_help_button_hover.png");

    public static final int SIZE = 16;

    private ContainerHelpButtonRenderer() {
    }

    public static void render(GuiGraphics guiGraphics, int x, int y, boolean active, boolean hovered) {
        ResourceLocation texture = active || hovered ? BUTTON_HOVER_TEXTURE : BUTTON_TEXTURE;
        guiGraphics.blit(texture, x, y, 0, 0, SIZE, SIZE, SIZE, SIZE);
    }

    public static boolean isHovered(int mouseX, int mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + SIZE && mouseY >= y && mouseY < y + SIZE;
    }
}
