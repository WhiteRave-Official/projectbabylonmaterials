package com.rave.projectbabylonmaterials.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import com.rave.projectbabylonmaterials.client.gui.help.ContainerHelpButtonRenderer;
import com.rave.projectbabylonmaterials.client.gui.help.ContainerSlotHelpEntry;
import com.rave.projectbabylonmaterials.init.PBMItems;
import com.rave.projectbabylonmaterials.menu.MagicalInfuserMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class MagicalInfuserScreen extends AbstractContainerScreen<MagicalInfuserMenu> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ProjectBabylonMaterials.MODID, "textures/gui/container/magical_infuser.png");
    private static final ResourceLocation DUST_SLOT_ICON = new ResourceLocation(ProjectBabylonMaterials.MODID, "textures/gui/container/slots/dust_slot_icon.png");
    private static final ResourceLocation INGOT_SLOT_ICON = new ResourceLocation(ProjectBabylonMaterials.MODID, "textures/gui/container/slots/ingot_slot_icon.png");
    private static final int FUEL_U = 176;
    private static final int FUEL_V = 17;
    private static final int FUEL_WIDTH = 6;
    private static final int FUEL_HEIGHT = 25;
    private static final int SLOT_ICON_SIZE = 16;
    private static final int HELP_BUTTON_OFFSET_X = 6;
    private static final int HELP_BUTTON_OFFSET_Y = 6;
    private static final Component ACCEPTS_LABEL = Component.translatable("tooltip.project_babylon_materials.container_help.accepts_label");

    private static final Map<Integer, ContainerSlotHelpEntry> HELP_ENTRIES = Map.of(
            0, new ContainerSlotHelpEntry(
                    0,
                    Component.translatable("tooltip.project_babylon_materials.container.magical_infuser.slot.fuel.title"),
                    ACCEPTS_LABEL,
                    new ItemStack(PBMItems.MAGIC_DUST.get()),
                    List.of(
                            Component.translatable("tooltip.project_babylon_materials.container.magical_infuser.slot.fuel.desc.1"),
                            Component.translatable("tooltip.project_babylon_materials.container.magical_infuser.slot.fuel.desc.2")
                    )
            ),
            1, new ContainerSlotHelpEntry(
                    1,
                    Component.translatable("tooltip.project_babylon_materials.container.magical_infuser.slot.top_input.title"),
                    ACCEPTS_LABEL,
                    new ItemStack(Items.IRON_INGOT),
                    List.of(
                            Component.translatable("tooltip.project_babylon_materials.container.magical_infuser.slot.top_input.desc.1"),
                            Component.translatable("tooltip.project_babylon_materials.container.magical_infuser.slot.top_input.desc.2")
                    )
            ),
            2, new ContainerSlotHelpEntry(
                    2,
                    Component.translatable("tooltip.project_babylon_materials.container.magical_infuser.slot.bottom_input.title"),
                    ACCEPTS_LABEL,
                    new ItemStack(PBMItems.DIAMOND_DUST.get()),
                    List.of(
                            Component.translatable("tooltip.project_babylon_materials.container.magical_infuser.slot.bottom_input.desc.1"),
                            Component.translatable("tooltip.project_babylon_materials.container.magical_infuser.slot.bottom_input.desc.2")
                    )
            ),
            3, new ContainerSlotHelpEntry(
                    3,
                    Component.translatable("tooltip.project_babylon_materials.container.magical_infuser.slot.output.title"),
                    ACCEPTS_LABEL,
                    new ItemStack(PBMItems.DIAMOND_INGOT.get()),
                    List.of(
                            Component.translatable("tooltip.project_babylon_materials.container.magical_infuser.slot.output.desc.1"),
                            Component.translatable("tooltip.project_babylon_materials.container.magical_infuser.slot.output.desc.2")
                    )
            )
    );

    private boolean helpModeEnabled;

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
        renderSlotPlaceholder(guiGraphics, 0, DUST_SLOT_ICON);
        renderSlotPlaceholder(guiGraphics, 1, INGOT_SLOT_ICON);
        renderSlotPlaceholder(guiGraphics, 2, DUST_SLOT_ICON);

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

        ContainerHelpButtonRenderer.render(guiGraphics, getHelpButtonX(), getHelpButtonY(), helpModeEnabled,
                ContainerHelpButtonRenderer.isHovered(mouseX, mouseY, getHelpButtonX(), getHelpButtonY()));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (!renderHelpTooltip(guiGraphics, mouseX, mouseY)) {
            this.renderTooltip(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && ContainerHelpButtonRenderer.isHovered((int) mouseX, (int) mouseY, getHelpButtonX(), getHelpButtonY())) {
            this.helpModeEnabled = !this.helpModeEnabled;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean renderHelpTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (ContainerHelpButtonRenderer.isHovered(mouseX, mouseY, getHelpButtonX(), getHelpButtonY())) {
            guiGraphics.renderTooltip(this.font, List.of(Component.translatable(helpModeEnabled
                    ? "tooltip.project_babylon_materials.container_help.disable"
                    : "tooltip.project_babylon_materials.container_help.enable")), java.util.Optional.empty(), mouseX, mouseY);
            return true;
        }

        if (!helpModeEnabled) {
            return false;
        }

        for (ContainerSlotHelpEntry entry : HELP_ENTRIES.values()) {
            Slot slot = this.menu.slots.get(entry.slotIndex());
            if (isHovering(slot.x, slot.y, SLOT_ICON_SIZE, SLOT_ICON_SIZE, mouseX, mouseY)) {
                guiGraphics.renderTooltip(this.font, List.of(entry.title()), java.util.Optional.of(entry.createTooltipData()), mouseX, mouseY);
                return true;
            }
        }

        return false;
    }

    private int getHelpButtonX() {
        return this.leftPos + this.imageWidth + HELP_BUTTON_OFFSET_X;
    }

    private int getHelpButtonY() {
        return this.topPos + HELP_BUTTON_OFFSET_Y;
    }

    private void renderSlotPlaceholder(GuiGraphics guiGraphics, int slotIndex, ResourceLocation iconTexture) {
        Slot slot = this.menu.slots.get(slotIndex);
        if (slot.hasItem()) {
            return;
        }

        guiGraphics.blit(iconTexture, this.leftPos + slot.x, this.topPos + slot.y, 0, 0, SLOT_ICON_SIZE, SLOT_ICON_SIZE, SLOT_ICON_SIZE, SLOT_ICON_SIZE);
    }
}
