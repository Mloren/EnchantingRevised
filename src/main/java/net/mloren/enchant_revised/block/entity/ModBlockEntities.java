package net.mloren.enchant_revised.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.block.ModBlocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MainMod.MOD_ID);

    public static final Supplier<BlockEntityType<EnchantAltarBlockEntity>> ENCHANT_ALTAR_BE =
            BLOCK_ENTITIES.register("enchant_altar_be", () -> BlockEntityType.Builder.of(
                    EnchantAltarBlockEntity::new, ModBlocks.ENCHANT_ALTAR.get()).build(null));

    public static void register(IEventBus eventBus)
    {
        BLOCK_ENTITIES.register(eventBus);
    }
}
