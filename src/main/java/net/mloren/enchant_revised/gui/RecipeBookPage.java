package net.mloren.enchant_revised.gui;

import net.mloren.enchant_revised.recipe.EnchantAltarRecipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeBookPage
{
    private final int pageNumber;
    private final String enchantName;
    private final List<EnchantAltarRecipe> recipeList;

    public RecipeBookPage(int page, String name)
    {
        pageNumber = page;
        enchantName = name;
        recipeList = new ArrayList<>();
    }

    public void addRecipe(EnchantAltarRecipe recipe)
    {
        recipeList.add(recipe);
    }

    public int getRecipeCount()
    {
        return recipeList.size();
    }

    public EnchantAltarRecipe getRecipe(int index)
    {
        return recipeList.get(index);
    }

    public int getPageNumber()
    {
        return pageNumber;
    }

    public String getEnchantName()
    {
        return enchantName;
    }
}
