package net.mloren.enchant_revised.loot_modifiers.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.mloren.enchant_revised.config.Config;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class RemoveEnchantBottles extends LootModifier
{
    public static final MapCodec<RemoveEnchantBottles> CODEC = RecordCodecBuilder.mapCodec(inst ->
            LootModifier.codecStart(inst).apply(inst, RemoveEnchantBottles::new));

    protected RemoveEnchantBottles(LootItemCondition[] conditionsIn)
    {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        if(!Config.COMMON.enableBottleOfEnchant.get())
        {
            //Remove Bottle of Enchanting from loot tables
            for (int i = generatedLoot.size() - 1; i >= 0; i--)
            {
                ItemStack itemStack = generatedLoot.get(i);
                if (!itemStack.isEmpty() && itemStack.getItem() == Items.EXPERIENCE_BOTTLE)
                    generatedLoot.remove(i);
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec()
    {
        return CODEC;
    }
}
