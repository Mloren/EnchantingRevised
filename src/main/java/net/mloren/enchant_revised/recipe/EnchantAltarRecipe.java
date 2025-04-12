package net.mloren.enchant_revised.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
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

import java.util.Optional;

public record EnchantAltarRecipe(SizedIngredient primaryIngredient, Optional<SizedIngredient> secondaryIngredient, int lapisCost, int bookshelvesRequired, Holder<Enchantment> enchantment, int enchantLevel) implements Recipe<EnchantAltarRecipeInput>
{
    @Override
    public boolean matches(@NotNull EnchantAltarRecipeInput input, Level level)
    {
        ItemStack targetItem = input.targetItem();

        if(targetItem.isEmpty())
           return false;

        boolean enchantable = targetItem.getItem().isEnchantable(targetItem) || EnchantmentHelper.canStoreEnchantments(targetItem);
        if(!enchantable)
            return false;

        boolean supportsEnchantment = targetItem.supportsEnchantment(enchantment) || targetItem.is(Items.BOOK);
        if(!supportsEnchantment)
            return false;

        boolean enchantmentsCompatible = EnchantmentHelper.isEnchantmentCompatible(EnchantmentHelper.getEnchantmentsForCrafting(targetItem).keySet(), enchantment);
        if(!enchantmentsCompatible)
            return false;

        ItemStack lapisStack = input.getItem(EnchantAltar.LAPIS_SLOT);
        boolean lapisResult = (lapisCost == 0 || (lapisStack.is(Items.LAPIS_LAZULI) && lapisStack.getCount() >= lapisCost));
        boolean primaryResult = primaryIngredient.test(input.getItem(EnchantAltar.PRIMARY_INGREDIENT_SLOT));
        boolean secondaryResult;
        if(secondaryIngredient.isPresent())
            secondaryResult = secondaryIngredient.get().test(input.getItem(EnchantAltar.SECONDARY_INGREDIENT_SLOT));
        else
            secondaryResult = input.getItem(EnchantAltar.SECONDARY_INGREDIENT_SLOT).isEmpty();

        return lapisResult && primaryResult && secondaryResult;
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

    public @NotNull ItemStack getExampleResult()
    {
        // Returns an enchanted book as an example item
        ItemStack result = new ItemStack(Items.ENCHANTED_BOOK, 1);
        result.enchant(enchantment, enchantLevel);
        return result;
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
                SizedIngredient.FLAT_CODEC.optionalFieldOf("secondary").forGetter(EnchantAltarRecipe::secondaryIngredient),
                Codec.INT.optionalFieldOf("lapis_cost", 0).forGetter(EnchantAltarRecipe::lapisCost),
                Codec.INT.optionalFieldOf("bookshelves_required", 0).forGetter(EnchantAltarRecipe::bookshelvesRequired),
                Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantAltarRecipe::enchantment),
                Codec.INT.fieldOf("enchant_level").forGetter(EnchantAltarRecipe::enchantLevel)
        ).apply(inst, EnchantAltarRecipe::new));

        //synchronized over the network
        public static final StreamCodec<RegistryFriendlyByteBuf, EnchantAltarRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        SizedIngredient.STREAM_CODEC, EnchantAltarRecipe::primaryIngredient,
                        ByteBufCodecs.optional(SizedIngredient.STREAM_CODEC), EnchantAltarRecipe::secondaryIngredient,
                        ByteBufCodecs.INT, EnchantAltarRecipe::lapisCost,
                        ByteBufCodecs.INT, EnchantAltarRecipe::bookshelvesRequired,
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
