package net.mloren.enchant_revised.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.mloren.enchant_revised.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ExperienceOrb.class, priority = 1800)
public class ExperienceOrbMixin
{
    //Disable XP orbs
    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDDI)V", at = @At("RETURN"))
    private void enchant_revised$Init1(Level level, double x, double y, double z, int value, CallbackInfo callback)
    {
        if(Config.SERVER.disableXP.get())
        {
            ExperienceOrb experienceOrb = (ExperienceOrb)(Object)this;
            experienceOrb.discard();
        }
    }

    //Disable XP orbs
    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("RETURN"))
    private void enchant_revised$Init2(EntityType entityType, Level level, CallbackInfo callback)
    {
        if(Config.SERVER.disableXP.get())
        {
            ExperienceOrb experienceOrb = (ExperienceOrb)(Object)this;
            experienceOrb.discard();
        }
    }
}
