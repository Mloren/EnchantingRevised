package net.mloren.enchant_revised.condition;

import com.mojang.serialization.MapCodec;
import net.mloren.enchant_revised.config.ConfigCommon;
import net.neoforged.neoforge.common.conditions.ICondition;

public record EnchantAltarEnabledCondition() implements ICondition
{
    public static final EnchantAltarEnabledCondition INSTANCE = new EnchantAltarEnabledCondition();
    public static final MapCodec<EnchantAltarEnabledCondition> CODEC = MapCodec.unit(INSTANCE).stable();

    @Override
    public boolean test(IContext context)
    {
        return ConfigCommon.getEnchantAltarEnabled();
    }

    @Override
    public MapCodec<? extends ICondition> codec()
    {
        return CODEC;
    }
}
