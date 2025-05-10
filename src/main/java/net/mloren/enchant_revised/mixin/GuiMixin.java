package net.mloren.enchant_revised.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.mloren.enchant_revised.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class, priority = 1800)
public class GuiMixin
{
    @Shadow
    public int leftHeight;

    @Shadow
    public int rightHeight;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/gui/GuiLayerManager;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    public void enchant_revised$render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback)
    {
        int uiHeight = Config.CLIENT.guiHeight.get();
        leftHeight = uiHeight;
        rightHeight = uiHeight;
    }
}
