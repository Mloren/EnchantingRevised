package net.mloren.enchant_revised.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.block.ModBlocks;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipe;
import net.mloren.enchant_revised.recipe.ModRecipes;
import net.mloren.enchant_revised.gui.EnchantAltarScreen;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIEnchantAltarPlugin implements IModPlugin
{
    @Override
    public ResourceLocation getPluginUid()
    {
        return ResourceLocation.fromNamespaceAndPath(MainMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        registration.addRecipeCategories(new EnchantAltarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        ArrayList<EnchantAltarRecipe> enchantAltarRecipes = new ArrayList<>();
        List<RecipeHolder<EnchantAltarRecipe>> recipeList = recipeManager.getAllRecipesFor(ModRecipes.ENCHANT_ALTAR_TYPE.get());

        for(RecipeHolder<EnchantAltarRecipe> recipeHolder : recipeList)
        {
            enchantAltarRecipes.add(recipeHolder.value());
        }

        SortRecipes(enchantAltarRecipes);
        registration.addRecipes(EnchantAltarRecipeCategory.ENCHANT_ALTAR_RECIPE_RECIPE_TYPE, enchantAltarRecipes);
    }

    private void SortRecipes(ArrayList<EnchantAltarRecipe> enchantAltarRecipes)
    {
        enchantAltarRecipes.sort((a, b) ->
        {
            String descriptionA = a.enchantment().value().description().getString();
            String descriptionB = b.enchantment().value().description().getString();

            int compareResult = descriptionA.compareTo(descriptionB);
            if(compareResult == 0)
                return Integer.compare(a.enchantLevel(), b.enchantLevel());

            return compareResult;
        });
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        registration.addRecipeClickArea(EnchantAltarScreen.class, 96, 36, 21, 14,
        EnchantAltarRecipeCategory.ENCHANT_ALTAR_RECIPE_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ENCHANT_ALTAR.asItem()),
                EnchantAltarRecipeCategory.ENCHANT_ALTAR_RECIPE_RECIPE_TYPE);
    }
}
