package net.mloren.enchant_revised.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.mloren.enchant_revised.util.EnchantAltar;
import org.jetbrains.annotations.NotNull;

public record EnchantAltarRecipeInput(ItemStack primaryIngredient, ItemStack secondaryIngredient, ItemStack lapis, ItemStack targetItem) implements RecipeInput
{
    @Override
    public @NotNull ItemStack getItem(int index)
    {
        if(index == EnchantAltar.PRIMARY_INGREDIENT_SLOT)
            return this.primaryIngredient;
        else if(index == EnchantAltar.SECONDARY_INGREDIENT_SLOT)
            return this.secondaryIngredient;
        else if(index == EnchantAltar.LAPIS_SLOT)
            return this.lapis;
        else if(index == EnchantAltar.TARGET_ITEM_SLOT)
            return this.targetItem;
        else
            throw new IllegalArgumentException("Recipe does not contain slot " + index);
    }

    @Override
    public int size()
    {
        return EnchantAltar.INPUT_SLOT_COUNT;
    }

    public boolean isEmpty()
    {
        return this.primaryIngredient.isEmpty() &&
                this.secondaryIngredient.isEmpty() &&
                this.lapis.isEmpty() &&
                this.targetItem.isEmpty();
    }
}
