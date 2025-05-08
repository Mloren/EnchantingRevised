package net.mloren.enchant_revised.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.mloren.enchant_revised.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AnvilMenu.class, priority = 1900)
abstract class AnvilMenuMixin extends ItemCombinerMenu
{
    @Shadow private final DataSlot cost = DataSlot.standalone();

    public AnvilMenuMixin(int containerId, Inventory playerInventory, ContainerLevelAccess access)
    {
        super(MenuType.ANVIL, containerId, playerInventory, access);
    }

    //Allow anvil recipes with zero XP cost to work
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    public void enchant_revised$mayPickup(Player player, boolean hasStack, CallbackInfoReturnable<Boolean> callback)
    {
        callback.setReturnValue((player.hasInfiniteMaterials() || player.experienceLevel >= this.cost.get()) && this.cost.get() >= 0);
    }

    //Prevent the anvil clearing the first inventory slot if the second contains an Empty Enchanted Book (i.e. we are duplicating the first slot)
    @Inject(method = "onTake", at = @At("HEAD"), cancellable = true)
    protected void enchant_revised$onTake(Player player, ItemStack stack, CallbackInfo callback)
    {
        if(this.inputSlots.getItem(1).getItem() == ModItems.EMPTY_ENCHANTED_BOOK.get())
        {
            float breakChance = net.neoforged.neoforge.common.CommonHooks.onAnvilRepair(player, stack, this.inputSlots.getItem(0), this.inputSlots.getItem(1));

            ItemStack itemstack = this.inputSlots.getItem(1);
            if (!itemstack.isEmpty())
                itemstack.shrink(1);

            this.cost.set(0);

            this.access.execute((level, blockPos) ->
            {
                BlockState blockstate = level.getBlockState(blockPos);
                if (!player.getAbilities().instabuild && blockstate.is(BlockTags.ANVIL) && player.getRandom().nextFloat() < breakChance)
                {
                    BlockState blockstate1 = AnvilBlock.damage(blockstate);
                    if (blockstate1 == null)
                    {
                        level.removeBlock(blockPos, false);
                        level.levelEvent(1029, blockPos, 0);
                    }
                    else
                    {
                        level.setBlock(blockPos, blockstate1, 2);
                        level.levelEvent(1030, blockPos, 0);
                    }
                }
                else
                {
                    level.levelEvent(1030, blockPos, 0);
                }
            });

            this.createResult();

            callback.cancel();
        }
    }

    //Set the XP cost of anvil recipes to zero.
    //When repairing or enchanting an item in the anvil, it will no longer increase in cost until it becomes impossible to use further
    @Inject(method = "createResult", at = @At("TAIL"), cancellable = true)
    public void enchant_revised$createResult(CallbackInfo callback)
    {
        this.cost.set(0);

        ItemStack result = this.resultSlots.getItem(0);
        if(!result.isEmpty())
            result.set(DataComponents.REPAIR_COST, 0);
    }

    //Make the AnvilMenu always return an XP cost of zero
    @Inject(method = "getCost", at = @At("HEAD"), cancellable = true)
    public void enchant_revised$getCost(CallbackInfoReturnable<Integer> callback)
    {
        callback.setReturnValue(0);
    }

    //Overwrite the value of j2 at line 182 of AnvilMenu
    //i2 and j2 are the levels of the two enchanted books being compared
    //This removes the vanilla behaviour of increasing the enchant level by 1 if the two enchantments match
    @ModifyVariable(method = "createResult", at = @At(
            value  = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;supportsEnchantment(Lnet/minecraft/core/Holder;)Z"), ordinal=3)
    public int enchant_revised$replaceEnchantLevel(int value, @Local(ordinal=0) Object2IntMap.Entry<Holder<Enchantment>> entry, @Local(ordinal=2) int i2)
    {
        int j2 = entry.getIntValue();
        return Math.max(j2, i2);
    }
}