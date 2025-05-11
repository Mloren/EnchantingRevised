package net.mloren.enchant_revised.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.mloren.enchant_revised.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnchantmentMenu.class, priority = 1800)
public class EnchantmentMenuMixin
{
    //Increase the players levels so the enchanting screen buttons can be clicked
    @Inject(method = "clickMenuButton", at = @At("HEAD"))
    public void enchant_revised$clickMenuButton1(Player player, int id, CallbackInfoReturnable<Boolean> cir)
    {
        if(!Config.COMMON.enableEnchantTableXPCosts.get())
            player.experienceLevel = (player.experienceLevel << 16) + 9999;
    }

    //Restore the player's level to what it was before they interacted with the enchanting screen
    @Inject(method = "clickMenuButton", at = @At("RETURN"))
    public void enchant_revised$clickMenuButton2(Player player, int id, CallbackInfoReturnable<Boolean> cir)
    {
        if(!Config.COMMON.enableEnchantTableXPCosts.get())
            player.experienceLevel = player.experienceLevel >> 16;
    }
}
