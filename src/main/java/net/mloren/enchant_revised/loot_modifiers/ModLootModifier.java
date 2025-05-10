package net.mloren.enchant_revised.loot_modifiers;

import com.mojang.serialization.MapCodec;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.loot_modifiers.custom.RemoveEnchantBottles;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModLootModifier
{
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MainMod.MOD_ID);

    public static final Supplier<MapCodec<RemoveEnchantBottles>> REMOVE_ENCHANT_BOTTLES =
            GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("glm_remove_enchant_bottle", () -> RemoveEnchantBottles.CODEC);

    public static void register(IEventBus eventBus)
    {
        GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(eventBus);
    }
}
