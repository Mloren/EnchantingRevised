package net.mloren.enchant_revised.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.mloren.enchant_revised.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = EnchantmentScreen.class, priority = 1800)
public class EnchantmentScreenMixin extends Screen
{
    protected EnchantmentScreenMixin(Component title)
    {
        super(title);
    }

    //Set the players level high for the duration of the enchanting screen so you can always afford the enchantment
    @Inject(method = "render", at = @At("HEAD"))
    public void enchant_revised$render1(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo callback)
    {
        if(!Config.COMMON.enableEnchantTableXPCosts.get())
            this.minecraft.player.experienceLevel = (this.minecraft.player.experienceLevel << 16) + 9999;
    }

    //Restore player's level to what it was before entering the enchantment screen
    @Inject(method = "render", at = @At("RETURN"))
    public void enchant_revised$render2(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo callback)
    {
        if(!Config.COMMON.enableEnchantTableXPCosts.get())
            this.minecraft.player.experienceLevel = this.minecraft.player.experienceLevel >> 16;
    }

    //Remove required level text from tooltip
    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 6, remap = false))
    private boolean enchant_revised$render3(List instance, Object e)
    {
        return !Config.COMMON.enableEnchantTableXPCosts.get();
    }

    //Hide UI that displays levels required
    @WrapWithCondition(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 2, remap = false))
    private boolean enchant_revised$renderBg_blitSprite1(GuiGraphics instance, ResourceLocation sprite, int x, int y, int width, int height)
    {
        return !Config.COMMON.enableEnchantTableXPCosts.get();
    }

    @WrapWithCondition(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 5, remap = false))
    private boolean enchant_revised$renderBg_blitSprite2(GuiGraphics instance, ResourceLocation sprite, int x, int y, int width, int height)
    {
        return !Config.COMMON.enableEnchantTableXPCosts.get();
    }

    @WrapWithCondition(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I", remap = false))
    private boolean enchant_revised$renderBg_drawString(GuiGraphics instance, Font font, String text, int x, int y, int color)
    {
        return !Config.COMMON.enableEnchantTableXPCosts.get();
    }

    //Move the text on the enchanting table buttons to fill the space left by removed XP icons
    @ModifyVariable(method = "renderBg", at = @At(
            value  = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/EnchantmentNames;getRandomName(Lnet/minecraft/client/gui/Font;I)Lnet/minecraft/network/chat/FormattedText;"),
            ordinal = 7)
    public int enchant_revised$renderBg_moveText(int value, @Local(ordinal = 6) int i1)
    {
        if(!Config.COMMON.enableEnchantTableXPCosts.get())
            return i1 + 8;
        else
            return value;
    }
}
