package com.rave.projectbabylonmaterials.tooltip;

import com.rave.projectbabylonmaterials.ProjectBabylonMaterials;
import net.minecraft.resources.ResourceLocation;

public record TooltipFrameStyle(ResourceLocation topLeftCorner, ResourceLocation topRightCorner,
                                ResourceLocation bottomLeftCorner, ResourceLocation bottomRightCorner,
                                ResourceLocation horizontalEdge, ResourceLocation verticalEdge,
                                ResourceLocation middle) {

    public static TooltipFrameStyle iron() {
        return material("iron");
    }

    public static TooltipFrameStyle material(String materialName) {
        String basePath = "textures/gui/tooltip/tooltip_frame/" + materialName + "_frame/" + materialName + "_frame_";
        return new TooltipFrameStyle(
                ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, basePath + "top_left_corner.png"),
                ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, basePath + "top_right_corner.png"),
                ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, basePath + "bottom_left_corner.png"),
                ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, basePath + "bottom_right_corner.png"),
                ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, basePath + "adjacent_horizontal.png"),
                ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, basePath + "adjacent_vertical.png"),
                ResourceLocation.fromNamespaceAndPath(ProjectBabylonMaterials.MODID, basePath + "middle.png")
        );
    }
}
