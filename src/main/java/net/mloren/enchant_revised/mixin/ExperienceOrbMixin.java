package net.mloren.enchant_revised.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.mloren.enchant_revised.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ExperienceOrb.class, priority = 1800)
public class ExperienceOrbMixin
{
    //Remove XP orbs
//    @Inject(method = "award", at = @At("HEAD"), cancellable = true)
//    private static void enchant_revised$award(ServerLevel level, Vec3 pos, int amount, CallbackInfo callback)
//    {
//        callback.cancel();
//    }
}
