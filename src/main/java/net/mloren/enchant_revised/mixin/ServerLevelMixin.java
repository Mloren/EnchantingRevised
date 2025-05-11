package net.mloren.enchant_revised.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.mloren.enchant_revised.config.Config;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ServerLevel.class, priority = 1800)
public class ServerLevelMixin
{
    //Disable log warning spam caused by disabling XP orbs
    @WrapWithCondition(method = "addEntity", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private boolean enchant_revised$addEntity(Logger instance, String s, Object o, Entity entity)
    {
        return !Config.COMMON.disableXP.get() || !(entity instanceof ExperienceOrb);
    }
}
