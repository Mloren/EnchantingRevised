package net.mloren.enchant_revised.condition;

import com.mojang.serialization.MapCodec;
import net.mloren.enchant_revised.config.Config;
import net.neoforged.neoforge.common.conditions.ICondition;

public record StartingLootEnabledCondition() implements ICondition
{
    public static final StartingLootEnabledCondition INSTANCE = new StartingLootEnabledCondition();
    public static final MapCodec<StartingLootEnabledCondition> CODEC = MapCodec.unit(INSTANCE).stable();

    @Override
    public boolean test(IContext context)
    {
        return Config.COMMON.provideStartingLoot.get();
    }

    @Override
    public MapCodec<? extends ICondition> codec()
    {
        return CODEC;
    }
}
