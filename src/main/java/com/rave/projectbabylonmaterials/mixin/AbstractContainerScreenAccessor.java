package com.rave.projectbabylonmaterials.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor("leftPos")
    int pbm$getLeftPos();

    @Accessor("topPos")
    int pbm$getTopPos();

    @Accessor("imageWidth")
    int pbm$getImageWidth();
}
