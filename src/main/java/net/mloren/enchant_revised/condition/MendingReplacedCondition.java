package net.mloren.enchant_revised.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.mloren.enchant_revised.config.Config;
import net.neoforged.neoforge.common.conditions.ICondition;

public record MendingReplacedCondition() implements LootItemCondition
{
    public static final MendingReplacedCondition INSTANCE = new MendingReplacedCondition();
    public static final MapCodec<MendingReplacedCondition> CODEC = MapCodec.unit(INSTANCE).stable();

    @Override
    public LootItemConditionType getType() {
        return ModConditions.MENDING_REPLACED.get();
    }

    @Override
    public boolean test(LootContext lootContext) {
        return Config.COMMON.replaceMending.get();
    }
}
