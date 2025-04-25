package net.mloren.enchant_revised.screen.RecipeBook;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipe;
import net.mloren.enchant_revised.recipe.ModRecipes;
import net.mloren.enchant_revised.util.SearchBar;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import javax.annotation.Nullable;
import java.util.*;

public class RecipeBookScreen implements Renderable, GuiEventListener
{
    private static final ResourceLocation RECIPE_BOOK_BG = ResourceLocation.withDefaultNamespace("textures/gui/recipe_book.png");
    private static final Component SEARCH_HINT = Component.translatable("gui.recipebook.search_hint")
            .withStyle(ChatFormatting.ITALIC)
            .withStyle(ChatFormatting.GRAY);
    private static final WidgetSprites PAGE_FORWARD_SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("recipe_book/page_forward"),
            ResourceLocation.withDefaultNamespace("recipe_book/page_forward_highlighted")
    );
    private static final WidgetSprites PAGE_BACKWARD_SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("recipe_book/page_backward"),
            ResourceLocation.withDefaultNamespace("recipe_book/page_backward_highlighted")
    );
    private static final ResourceLocation SLOT_BACKGROUND = ResourceLocation.fromNamespaceAndPath(MainMod.MOD_ID,"enchant_altar/recipe_slot");

    private static final ItemStack LAPIS_STACK = new ItemStack(Items.LAPIS_LAZULI, 1);

    private static final int TOP_MARGIN = 4;
    private static final int LEFT_MARGIN = 12;
    private static final int ITEM_SIZE = 18;

    private static final int SEARCH_BOX_LEFT_OFFSET = 25;
    private static final int SEARCH_BOX_TOP_OFFSET = 13;

    private static final int RECIPE_BUTTON_WIDTH = 124;
    private static final int RECIPE_BUTTON_HEIGHT = 21;

    private Minecraft minecraft;
    private int leftPos;
    private int topPos;
    private boolean widthTooNarrow;
    private static boolean visible = false;

    @Nullable
    private SearchBar searchBox;
    private String lastSearch = "";
    private boolean ignoreTextInput;

    private final TreeMap<String, List<EnchantAltarRecipe>> recipeMap = new TreeMap<>();
    private final TreeMap<String, List<EnchantAltarRecipe>> searchResults = new TreeMap<>();

    private ItemStack hoveredItem;

    private StateSwitchingButton forwardButton;
    private StateSwitchingButton backButton;
    private int totalPages;
    private int currentPage;

    public void init(int width, int height, Minecraft minecraft, boolean widthTooNarrow, Level level)
    {
        this.minecraft = minecraft;
        this.widthTooNarrow = widthTooNarrow;
        this.leftPos = (width - 147) / 2 - (this.widthTooNarrow ? 0 : 86);
        this.topPos = (height - 166) / 2;

        String s = this.searchBox != null ? this.searchBox.getValue() : "";
        this.searchBox = new SearchBar(this.minecraft.font, this.leftPos + SEARCH_BOX_LEFT_OFFSET, this.topPos + SEARCH_BOX_TOP_OFFSET, 81, 14, Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(16777215);
        this.searchBox.setValue(s);
        this.searchBox.setHint(SEARCH_HINT);

        this.forwardButton = new StateSwitchingButton(this.leftPos + 93, this.topPos + 137, 12, 17, false);
        this.forwardButton.initTextureValues(PAGE_FORWARD_SPRITES);
        this.backButton = new StateSwitchingButton(this.leftPos + 38, this.topPos + 137, 12, 17, true);
        this.backButton.initTextureValues(PAGE_BACKWARD_SPRITES);

        BuildRecipeMap(level.getRecipeManager());
        this.checkSearchStringUpdate(true);
        updatePages();
    }

    private void BuildRecipeMap(RecipeManager recipeManager)
    {
        List<RecipeHolder<EnchantAltarRecipe>> recipeList = recipeManager.getAllRecipesFor(ModRecipes.ENCHANT_ALTAR_TYPE.get());
        for(RecipeHolder<EnchantAltarRecipe> recipeHolder : recipeList)
        {
            EnchantAltarRecipe recipe = recipeHolder.value();
            String name = recipe.enchantment().value().description().getString();

            List<EnchantAltarRecipe> levelList = recipeMap.get(name);
            if(levelList == null)
            {
                levelList = new ArrayList<>();
                recipeMap.put(name, levelList);
            }

            levelList.add(recipe);
        }
    }

    public void updatePages()
    {
        this.totalPages = searchResults.size();
        if (this.currentPage > this.totalPages)
            this.currentPage = 0;

        this.updateArrowButtons();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        hoveredItem = null;

        if (this.isVisible())
        {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);

            // Background
            guiGraphics.blit(RECIPE_BOOK_BG, leftPos, topPos, -11, 1, 1, 147, 166, 256, 256);

            // Search box
            this.searchBox.render(guiGraphics, mouseX, mouseY, partialTick);

            // Recipes
            RenderRecipePage(guiGraphics, mouseX, mouseY, this.currentPage);

            // Forward and Back buttons
            this.backButton.render(guiGraphics, mouseX, mouseY, partialTick);
            this.forwardButton.render(guiGraphics, mouseX, mouseY, partialTick);

            // Page number
            if (this.totalPages > 1)
            {
                Component component = Component.translatable("gui.recipebook.page", this.currentPage + 1, this.totalPages);
                int fontWidth = this.minecraft.font.width(component);
                guiGraphics.drawString(this.minecraft.font, component, leftPos - fontWidth / 2 + 73, topPos + 141, 0xFFFFFFFF, false);
            }

            guiGraphics.pose().popPose();
        }
    }

    private void RenderRecipePage(GuiGraphics guiGraphics, int mouseX, int mouseY, int page)
    {
        // Find the page
        int i = 0;
        String enchantName = "";
        List<EnchantAltarRecipe> recipeList = null;
        for(Map.Entry<String, List<EnchantAltarRecipe>> entry : searchResults.entrySet())
        {
            if(i == page)
            {
                enchantName = entry.getKey();
                recipeList = entry.getValue();
                break;
            }
            i++;
        }

        if(recipeList != null)
        {
            int x = leftPos + LEFT_MARGIN;
            int y = this.topPos + SEARCH_BOX_TOP_OFFSET + this.searchBox.getHeight() + TOP_MARGIN;
            guiGraphics.drawString(this.minecraft.font, enchantName, x, y, 0xFFFFFFFF, false);

            y += 9;
            for (EnchantAltarRecipe recipe : recipeList)
            {
                renderRecipe(guiGraphics, mouseX, mouseY, recipe, y + (recipe.enchantLevel() - 1) * ITEM_SIZE);
            }
        }
    }

    private void renderRecipe(GuiGraphics guiGraphics, int mouseX, int mouseY, EnchantAltarRecipe recipe, int y)
    {
        int enchantLevel = recipe.enchantLevel();
        int x = leftPos + LEFT_MARGIN;
        guiGraphics.blitSprite(SLOT_BACKGROUND, x, y, -10 + enchantLevel, RECIPE_BUTTON_WIDTH, RECIPE_BUTTON_HEIGHT);

        String enchantLevelText = Component.translatable("enchantment.level." + enchantLevel).getString();
        guiGraphics.drawString(this.minecraft.font, enchantLevelText, x + 6, y + 6, 0xFFFFFFFF, true);

        // The three item icons are aligned to the right edge of the panel
        x = (leftPos + LEFT_MARGIN + RECIPE_BUTTON_WIDTH) - (ITEM_SIZE * 3) - 5;
        y += 2;

        LAPIS_STACK.setCount(recipe.lapisCost());
        x = renderItem(guiGraphics, mouseX, mouseY, LAPIS_STACK, x, y);
        x = renderIngredient(guiGraphics, mouseX, mouseY, Optional.ofNullable(recipe.primaryIngredient()), x, y);
        renderIngredient(guiGraphics, mouseX, mouseY, recipe.secondaryIngredient(), x, y);
    }

    private int renderItem(GuiGraphics guiGraphics, int mouseX, int mouseY, ItemStack item, int x, int y)
    {
        guiGraphics.renderFakeItem(item, x, y);
        guiGraphics.renderItemDecorations(this.minecraft.font, item, x, y);

        // Check if mouse is hovering an item
        if(mouseX > x && mouseX < x + (ITEM_SIZE - 3) && mouseY > y && mouseY < y + (ITEM_SIZE - 3))
            hoveredItem = item;

        return x + ITEM_SIZE;
    }

    private int renderIngredient(GuiGraphics guiGraphics, int mouseX, int mouseY, Optional<SizedIngredient> ingredient, int x, int y)
    {
        if(ingredient.isEmpty())
            return x;

        for(ItemStack item : ingredient.get().getItems())
        {
            x = renderItem(guiGraphics, mouseX, mouseY, item, x, y);
        }
        return x;
    }

    public void renderTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        if(hoveredItem != null)
            guiGraphics.renderComponentTooltip(this.minecraft.font, Screen.getTooltipFromItem(minecraft, this.hoveredItem), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.isVisible() && !this.minecraft.player.isSpectator())
        {
            if (this.searchBox.mouseClicked(mouseX, mouseY, button))
            {
                this.searchBox.setFocused(true);
                if (button == 1)
                {
                    // Right click clears the search
                    this.searchBox.setValue("");
                    if (this.checkSearchStringUpdate(false))
                        this.currentPage = 0;
                }
                return true;
            }
            else if (this.forwardButton.mouseClicked(mouseX, mouseY, button))
            {
                this.searchBox.setFocused(false);
                this.currentPage++;
                this.updatePages();
                return true;
            }
            else if (this.backButton.mouseClicked(mouseX, mouseY, button))
            {
                this.searchBox.setFocused(false);
                this.currentPage--;
                this.updatePages();
                return true;
            }
            else
            {
                this.searchBox.setFocused(false);
                return false;
            }
        }
        else
            return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        this.ignoreTextInput = false;
        if (!this.isVisible() || this.minecraft.player.isSpectator())
        {
            return false;
        }
        else if (this.searchBox.keyPressed(keyCode, scanCode, modifiers))
        {
            this.checkSearchStringUpdate(false);
            return true;
        }
        else if (this.searchBox.isFocused() && this.searchBox.isVisible() && keyCode != 256)
        {
            return true;
        }
        else if (this.minecraft.options.keyChat.matches(keyCode, scanCode) && !this.searchBox.isFocused())
        {
            this.ignoreTextInput = true;
            this.searchBox.setFocused(true);
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        this.ignoreTextInput = false;
        return GuiEventListener.super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        if (this.ignoreTextInput)
        {
            return false;
        }
        else if (!this.isVisible() || this.minecraft.player.isSpectator())
        {
            return false;
        }
        else if (this.searchBox.charTyped(codePoint, modifiers))
        {
            this.checkSearchStringUpdate(false);
            return true;
        }
        else
        {
            return GuiEventListener.super.charTyped(codePoint, modifiers);
        }
    }

    private boolean checkSearchStringUpdate(boolean force)
    {
        String search = this.searchBox.getValue().toLowerCase(Locale.ROOT);
        if (force || !search.equals(this.lastSearch))
        {
            searchResults.clear();
            for(Map.Entry<String, List<EnchantAltarRecipe>> entry : recipeMap.entrySet())
            {
                if(search.isEmpty() || entry.getKey().toLowerCase().contains(search))
                    searchResults.put(entry.getKey(), entry.getValue());
            }

            this.lastSearch = search;
            updatePages();
            return true;
        }
        return false;
    }

    public boolean hasClickedOutside(double mouseX, double mouseY, int x, int y, int width, int height, int mouseButton)
    {
        if (!this.isVisible())
        {
            return true;
        }
        else
        {
            boolean flag = mouseX < (double)x
                    || mouseY < (double)y
                    || mouseX >= (double)(x + width)
                    || mouseY >= (double)(y + height);
            boolean flag1 = (double)(x - 147) < mouseX
                    && mouseX < (double)x
                    && (double)y < mouseY
                    && mouseY < (double)(y + height);
            return flag && !flag1;
        }
    }

    public int updateScreenPosition(int width, int imageWidth)
    {
        int i;
        if (this.isVisible() && !this.widthTooNarrow)
            i = 177 + (width - imageWidth - 200) / 2;
        else
            i = (width - imageWidth) / 2;

        return i;
    }

    private void updateArrowButtons()
    {
        this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
        this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
    }

    public void toggleVisibility()
    {
        visible = !visible;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean isVisible)
    {
        visible = isVisible;
    }

    @Override
    public void setFocused(boolean focused)
    {
    }

    @Override
    public boolean isFocused()
    {
        return false;
    }
}
