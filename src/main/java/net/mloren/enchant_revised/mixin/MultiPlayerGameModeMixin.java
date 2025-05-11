package net.mloren.enchant_revised.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.mloren.enchant_revised.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MultiPlayerGameMode.class, priority = 1800)
public class MultiPlayerGameModeMixin
{
    //Disable experience bar in survival
    @Inject(method = "hasExperience", at = @At("HEAD"), cancellable = true)
    private void enchant_revised$hasExperience(CallbackInfoReturnable<Boolean> callback)
    {
        if(!Config.CLIENT.enableXPBar.get())
            callback.setReturnValue(false);
    }
}
