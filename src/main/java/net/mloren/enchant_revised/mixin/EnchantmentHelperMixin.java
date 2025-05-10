package net.mloren.enchant_revised.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(value = EnchantmentHelper.class, priority = 1800)
public class EnchantmentHelperMixin
{
    //If an enchantment uses "repair_with_xp", it won't be selected for repair if its enchantment loot conditions are invalid
    @Inject(method = "getRandomItemWith", at = @At("HEAD"), cancellable = true)
    private static void enchant_revised$getRandomItemWith(DataComponentType<?> componentType, LivingEntity entity, Predicate<ItemStack> filter, CallbackInfoReturnable<Optional<EnchantedItemInUse>> callback)
    {
        if(componentType != EnchantmentEffectComponents.REPAIR_WITH_XP)
            return;

        if (entity.level() instanceof ServerLevel serverlevel)
        {
            List<EnchantedItemInUse> list = new ArrayList<>();

            for (EquipmentSlot equipmentslot : EquipmentSlot.values())
            {
                ItemStack itemstack = entity.getItemBySlot(equipmentslot);
                if (filter.test(itemstack))
                {
                    ItemEnchantments itemenchantments = itemstack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

                    for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemenchantments.entrySet())
                    {
                        Holder<Enchantment> holder = entry.getKey();

                        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(holder, entity);
                        LootContext context = Enchantment.entityContext(serverlevel, enchantmentLevel, entity, entity.position());

                        boolean matchesConditions = true;
                        for (ConditionalEffect<EnchantmentValueEffect> conditionaleffect : holder.value().getEffects(EnchantmentEffectComponents.REPAIR_WITH_XP))
                        {
                            if (!conditionaleffect.matches(context))
                            {
                                matchesConditions = false;
                                break;
                            }
                        }

                        if (holder.value().effects().has(componentType) && holder.value().matchingSlot(equipmentslot) && matchesConditions)
                            list.add(new EnchantedItemInUse(itemstack, equipmentslot, entity));
                    }
                }
            }

            callback.setReturnValue(Util.getRandomSafe(list, entity.getRandom()));
        }
    }
}
