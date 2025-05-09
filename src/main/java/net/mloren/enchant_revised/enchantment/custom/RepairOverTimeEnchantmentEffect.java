package net.mloren.enchant_revised.enchantment.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import net.mloren.enchant_revised.config.Config;

public record RepairOverTimeEnchantmentEffect() implements EnchantmentEntityEffect
{
    public static final MapCodec<RepairOverTimeEnchantmentEffect> CODEC = MapCodec.unit(RepairOverTimeEnchantmentEffect::new);

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin)
    {
        if(item.owner() instanceof ServerPlayer serverPlayer)
        {
            //Repair tools over time
            int tickDelay = Config.SERVER.repairDelayTicks.get();
            int tickDelayPerLevel = Config.SERVER.repairDelayTicksPerLevel.get();
            int ticksPerRepair = Math.max(tickDelay - ((enchantmentLevel - 1) * tickDelayPerLevel), 1);
            if(serverPlayer.tickCount % ticksPerRepair == 0)
            {
                int itemDamage = item.itemStack().getDamageValue();
                itemDamage = Math.max(itemDamage - 1, 0);
                item.itemStack().setDamageValue(itemDamage);
            }
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec()
    {
        return CODEC;
    }
}
