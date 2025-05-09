package net.mloren.enchant_revised.mixin;

import net.minecraft.world.item.*;
import net.mloren.enchant_revised.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Item.class, priority = 1800)
public class ItemMixin
{
    //Allow diamonds or netherite scrap to repair netherite items
    @Inject(method = "isValidRepairItem", at = @At("HEAD"), cancellable = true)
    public void enchant_revised$isValidRepairItem(ItemStack toRepair, ItemStack repair, CallbackInfoReturnable<Boolean> callback)
    {
        if(enchantingRevised$IsNetherite(toRepair))
        {
            if(Config.SERVER.repairNetheriteWithDiamonds.get() && repair.getItem() == Items.DIAMOND)
                callback.setReturnValue(true);
            if(Config.SERVER.repairNetheriteWithScrap.get() && repair.getItem() == Items.NETHERITE_SCRAP)
                callback.setReturnValue(true);
        }
    }

    @Unique
    private boolean enchantingRevised$IsNetherite(ItemStack itemStack)
    {
        if(itemStack.isEmpty())
            return false;

        if(itemStack.getItem() instanceof TieredItem tieredItem)
            return tieredItem.getTier() == Tiers.NETHERITE;

        if(itemStack.getItem() instanceof ArmorItem armorItem)
            return armorItem.getMaterial() == ArmorMaterials.NETHERITE;

        return false;
    }
}
