package net.mloren.enchant_revised.screen.RecipeBook;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipe;
import net.mloren.enchant_revised.recipe.ModRecipes;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import javax.annotation.Nullable;
import java.util.*;

public class RecipeBookScreen implements Renderable, GuiEventListener
{
    public static final int RECIPES_PER_PAGE = 4;
    private static final ResourceLocation RECIPE_BOOK_BG = ResourceLocation.withDefaultNamespace("textures/gui/recipe_book.png");
    private static final Component SEARCH_HINT = Component.translatable("gui.recipebook.search_hint")
            .withStyle(ChatFormatting.ITALIC)
            .withStyle(ChatFormatting.GRAY);
    private static final WidgetSprites PAGE_FORWARD_SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("recipe_book/page_forward"), ResourceLocation.withDefaultNamespace("recipe_book/page_forward_highlighted")
    );
    private static final WidgetSprites PAGE_BACKWARD_SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("recipe_book/page_backward"), ResourceLocation.withDefaultNamespace("recipe_book/page_backward_highlighted")
    );
    private static final ResourceLocation SLOT_CRAFTABLE_SPRITE = ResourceLocation.fromNamespaceAndPath(MainMod.MOD_ID,"enchant_altar/recipe_slot");

    private static final ItemStack LAPIS_STACK = new ItemStack(Items.LAPIS_LAZULI, 1);

    private static final int TOP_MARGIN = 4;
    private static final int LEFT_MARGIN = 12;
    private static final int ROW_HEIGHT = 25;
    private static final int ITEM_SIZE = 18;

    private static final int SEARCH_BOX_LEFT_OFFSET = 25;
    private static final int SEARCH_BOX_TOP_OFFSET = 13;

    private static final int RECIPE_BUTTON_WIDTH = 124;
    private static final int RECIPE_BUTTON_HEIGHT = 21;

    private Minecraft minecraft;
    private int width;
    private int height;
    private int xOffset;
    private int leftPos;
    private int topPos;
    private boolean widthTooNarrow;
    private boolean visible;

    @Nullable
    private EditBox searchBox;
    private String lastSearch = "";
    private boolean ignoreTextInput;

    private TreeMap<String, List<EnchantAltarRecipe>> recipeMap = new TreeMap<>();

    private StateSwitchingButton forwardButton;
    private StateSwitchingButton backButton;
    private int totalPages;
    private int currentPage;

    public void init(int width, int height, Minecraft minecraft, boolean widthTooNarrow, Level level)
    {
        this.minecraft = minecraft;
        this.width = width;
        this.height = height;
        this.widthTooNarrow = widthTooNarrow;
        this.visible = false;
        this.xOffset = this.widthTooNarrow ? 0 : 86;
        this.leftPos = (this.width - 147) / 2 - this.xOffset;
        this.topPos = (this.height - 166) / 2;

        String s = this.searchBox != null ? this.searchBox.getValue() : "";
        this.searchBox = new EditBox(this.minecraft.font, this.leftPos + SEARCH_BOX_LEFT_OFFSET, this.topPos + SEARCH_BOX_TOP_OFFSET, 81, 14, Component.translatable("itemGroup.search"));
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
        update();
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

    public void update()
    {
        this.totalPages = recipeMap.size(); //(int)Math.ceil((double)recipeMap.size() / RECIPES_PER_PAGE);
        if (this.totalPages <= this.currentPage)
        {
            this.currentPage = 0;
        }
        this.updateArrowButtons();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.isVisible())
        {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);

            // Background
            guiGraphics.blit(RECIPE_BOOK_BG, leftPos, topPos, -11, 1, 1, 147, 166, 256, 256);

            // Search box
            this.searchBox.render(guiGraphics, mouseX, mouseY, partialTick);

            // Recipes
            //int y = this.topPos + SEARCH_BOX_TOP_OFFSET + this.searchBox.getHeight() + TOP_MARGIN;
            RenderRecipePage(guiGraphics, this.currentPage);

//            int pageStart = this.currentPage * RECIPES_PER_PAGE;
//            for(int i = pageStart; i < pageStart + RECIPES_PER_PAGE; ++i)
//            {
//                if(i < this.recipeList.size())
//                {
//                    EnchantAltarRecipe recipe = this.recipeList.get(i).value();
//                    renderRecipe(guiGraphics, recipe, y);
//                    y += ROW_HEIGHT;
//                }
//            }

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

    private void RenderRecipePage(GuiGraphics guiGraphics, int page)
    {
        // Find the page
        int i = 0;
        String enchantName = "";
        List<EnchantAltarRecipe> recipeList = null;
        for(Map.Entry<String, List<EnchantAltarRecipe>> entry : recipeMap.entrySet())
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

            //int centreX = (x + x + RECIPE_BUTTON_WIDTH) / 2;
            //x = centreX - (recipeList.size() * ITEM_SIZE) / 2;
            y += 9;
            for (EnchantAltarRecipe recipe : recipeList)
            {
                renderRecipe(guiGraphics, recipe, y + (recipe.enchantLevel() - 1) * ITEM_SIZE);
            }
        }
    }

    private void renderRecipe(GuiGraphics guiGraphics, EnchantAltarRecipe recipe, int y)
    {
        //int y = this.topPos + SEARCH_BOX_TOP_OFFSET + this.searchBox.getHeight() + TOP_MARGIN + 10;



        //x += (int)((float) ITEM_SIZE * 0.25f);
        //y += (int)((float) ITEM_SIZE * 0.25f);

        int enchantLevel = recipe.enchantLevel();
        int x = leftPos + LEFT_MARGIN;
        guiGraphics.blitSprite(SLOT_CRAFTABLE_SPRITE, x, y, -10 + enchantLevel, RECIPE_BUTTON_WIDTH, RECIPE_BUTTON_HEIGHT);

        y += 2;
        //String recipeName = Enchantment.getFullname(recipe.enchantment(), recipe.enchantLevel()).getString();
        String enchantLevelText = Component.translatable("enchantment.level." + enchantLevel).getString();
        guiGraphics.drawString(this.minecraft.font, enchantLevelText, x + 6, y + 4, 0xFFFFFFFF, true);

        //guiGraphics.drawString(this.minecraft.font, "Projectile", x + 2, y, 0xFF000000, false);
        //guiGraphics.drawString(this.minecraft.font, "Protection III", x + 2, y + 10, 0xFF000000, false);

        x = (leftPos + LEFT_MARGIN + RECIPE_BUTTON_WIDTH) - (ITEM_SIZE * 3) - 5;
        //y += 10;
        //x += 15;
        LAPIS_STACK.setCount(recipe.lapisCost());
        x = renderItem(guiGraphics, LAPIS_STACK, x, y);
        x = renderIngredient(guiGraphics, Optional.ofNullable(recipe.primaryIngredient()), x, y);
        renderIngredient(guiGraphics, recipe.secondaryIngredient(), x, y);
    }

    private int renderItem(GuiGraphics guiGraphics, ItemStack item, int x, int y)
    {
        guiGraphics.renderItem(item, x, y);
        guiGraphics.renderItemDecorations(this.minecraft.font, item, x, y);
        return x + ITEM_SIZE;
    }

    private int renderIngredient(GuiGraphics guiGraphics, Optional<SizedIngredient> ingredient, int x, int y)
    {
        if(ingredient.isEmpty())
            return x;

        for(ItemStack item : ingredient.get().getItems())
        {
            x = renderItem(guiGraphics, item, x, y);
        }
        return x;
    }

    public void renderTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.isVisible() && !this.minecraft.player.isSpectator())
        {
            if (this.searchBox.mouseClicked(mouseX, mouseY, button))
            {
                this.searchBox.setFocused(true);
                return true;
            }
            else if (this.forwardButton.mouseClicked(mouseX, mouseY, button))
            {
                this.currentPage++;
                this.update();
                return true;
            }
            else if (this.backButton.mouseClicked(mouseX, mouseY, button))
            {
                this.currentPage--;
                this.update();
                return true;
            }
            else
            {
                this.searchBox.setFocused(false);
                return false;
            }
        }
        else
        {
            return false;
        }
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
            this.checkSearchStringUpdate();
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
        {
            return false;
        }
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
            this.checkSearchStringUpdate();
            return true;
        }
        else
        {
            return GuiEventListener.super.charTyped(codePoint, modifiers);
        }
    }

    private void checkSearchStringUpdate()
    {
        String s = this.searchBox.getValue().toLowerCase(Locale.ROOT);
        if (!s.equals(this.lastSearch))
        {
            update();
            this.lastSearch = s;
        }
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
        {
            i = 177 + (width - imageWidth - 200) / 2;
        }
        else
        {
            i = (width - imageWidth) / 2;
        }

        return i;
    }

    private void updateArrowButtons()
    {
        this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
        this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
    }

    public void toggleVisibility()
    {
        this.visible = !this.visible;
    }

    public boolean isVisible()
    {
        return this.visible;
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
