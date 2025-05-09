package net.mloren.enchant_revised.condition;

import com.mojang.serialization.MapCodec;
import net.mloren.enchant_revised.config.Config;
import net.mloren.enchant_revised.config.types.CommonConfig;
import net.neoforged.neoforge.common.conditions.ICondition;

public record EnchantAltarEnabledCondition() implements ICondition
{
    public static final EnchantAltarEnabledCondition INSTANCE = new EnchantAltarEnabledCondition();
    public static final MapCodec<EnchantAltarEnabledCondition> CODEC = MapCodec.unit(INSTANCE).stable();

    @Override
    public boolean test(IContext context)
    {
        return Config.COMMON.enableEnchantAltar.get();
    }

    @Override
    public MapCodec<? extends ICondition> codec()
    {
        return CODEC;
    }
}
