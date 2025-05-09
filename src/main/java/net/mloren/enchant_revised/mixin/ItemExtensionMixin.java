package net.mloren.enchant_revised.mixin;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = IItemExtension.class, priority = 1800)
public interface ItemExtensionMixin
{
    //Prevent the item change animation when items are repaired
    @Inject(method = "shouldCauseReequipAnimation", at = @At("HEAD"), cancellable = true)
    default void enchant_revised$shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged, CallbackInfoReturnable<Boolean> callback)
    {
        if(!slotChanged && !oldStack.isEmpty() && !newStack.isEmpty() && oldStack.getItem() == newStack.getItem())
            callback.setReturnValue(false);
    }
}
