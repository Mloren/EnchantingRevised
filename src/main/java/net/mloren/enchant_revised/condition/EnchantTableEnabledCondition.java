package net.mloren.enchant_revised.condition;

import com.mojang.serialization.MapCodec;
import net.mloren.enchant_revised.config.ConfigCommon;
import net.neoforged.neoforge.common.conditions.ICondition;

public record EnchantTableEnabledCondition() implements ICondition
{
    public static final EnchantTableEnabledCondition INSTANCE = new EnchantTableEnabledCondition();
    public static final MapCodec<EnchantTableEnabledCondition> CODEC = MapCodec.unit(INSTANCE).stable();

    @Override
    public boolean test(IContext context)
    {
        return ConfigCommon.getEnchantTableEnabled();
    }

    @Override
    public MapCodec<? extends ICondition> codec()
    {
        return CODEC;
    }
}
