package com.xir.NHUtilities.mixins.late.GregTech;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import gregtech.common.covers.CoverChest;

@SuppressWarnings("UnusedMixin")
@Mixin(value = CoverChest.class, remap = false)
public abstract class CoverChestStacksize_Mixin {

    /**
     * @author ChromaPIE
     * @reason Unleash the stacksize of items in Item Holders from ONE
     */
    @Shadow
    @Final
    @Mutable
    private int stackSizeLimit = 64;
}
