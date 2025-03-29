package net.mloren.enchant_revised.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.mloren.enchant_revised.util.EnchantAltar;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record EnchantAltarRecipe(SizedIngredient primaryIngredient, SizedIngredient secondaryIngredient, int lapisCost, Holder<Enchantment> enchantment, int enchantLevel) implements Recipe<EnchantAltarRecipeInput>
{
    public @NotNull NonNullList<SizedIngredient> getIngredientList()
    {
        NonNullList<SizedIngredient> list = NonNullList.create();
        list.add(primaryIngredient);
        list.add(secondaryIngredient);
        return list;
    }

    @Override
    public boolean matches(@NotNull EnchantAltarRecipeInput input, Level level)
    {
        if(level.isClientSide())
            return false;

        ItemStack targetItem = input.targetItem();

        if(targetItem.isEmpty())
           return false;

        boolean enchantable = targetItem.getItem().isEnchantable(targetItem) || EnchantmentHelper.canStoreEnchantments(targetItem);
        if(!enchantable)
            return false;

        boolean supportsEnchantment = targetItem.supportsEnchantment(enchantment) || targetItem.is(Items.BOOK);
        if(!supportsEnchantment)
            return false;

        return input.getItem(EnchantAltar.LAPIS_SLOT).is(Items.LAPIS_LAZULI) &&
                input.getItem(EnchantAltar.LAPIS_SLOT).getCount() >= lapisCost &&
                primaryIngredient.test(input.getItem(EnchantAltar.PRIMARY_INGREDIENT_SLOT)) &&
                secondaryIngredient.test(input.getItem(EnchantAltar.SECONDARY_INGREDIENT_SLOT));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull EnchantAltarRecipeInput input, HolderLookup.@NotNull Provider registries)
    {
        ItemStack targetItem = input.targetItem();
        ItemStack outputItem;

        if(targetItem.is(Items.BOOK))
            outputItem = new ItemStack(Items.ENCHANTED_BOOK);
        else
            outputItem = targetItem.copy();

        outputItem.enchant(enchantment, enchantLevel);

        return outputItem;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@Nullable Provider registries)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer()
    {
        return ModRecipes.ENCHANT_ALTAR_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType()
    {
        return ModRecipes.ENCHANT_ALTAR_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<EnchantAltarRecipe>
    {
        //format of the JSON file
        public static final MapCodec<EnchantAltarRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                SizedIngredient.FLAT_CODEC.fieldOf("primary").forGetter(EnchantAltarRecipe::primaryIngredient),
                SizedIngredient.FLAT_CODEC.fieldOf("secondary").forGetter(EnchantAltarRecipe::secondaryIngredient),
                Codec.INT.fieldOf("lapis_cost").forGetter(EnchantAltarRecipe::lapisCost),
                Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantAltarRecipe::enchantment),
                Codec.INT.fieldOf("enchant_level").forGetter(EnchantAltarRecipe::enchantLevel)
        ).apply(inst, EnchantAltarRecipe::new));

        //synchronized over the network
        public static final StreamCodec<RegistryFriendlyByteBuf, EnchantAltarRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        SizedIngredient.STREAM_CODEC, EnchantAltarRecipe::primaryIngredient,
                        SizedIngredient.STREAM_CODEC, EnchantAltarRecipe::secondaryIngredient,
                        ByteBufCodecs.INT, EnchantAltarRecipe::lapisCost,
                        Enchantment.STREAM_CODEC, EnchantAltarRecipe::enchantment,
                        ByteBufCodecs.INT, EnchantAltarRecipe::enchantLevel,
                        EnchantAltarRecipe::new);

        @Override
        public MapCodec<EnchantAltarRecipe> codec()
        {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EnchantAltarRecipe> streamCodec()
        {
            return STREAM_CODEC;
        }
    }
}
