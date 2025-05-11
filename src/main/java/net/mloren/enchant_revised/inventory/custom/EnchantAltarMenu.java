package net.mloren.enchant_revised.inventory.custom;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.mloren.enchant_revised.block.ModBlocks;
import net.mloren.enchant_revised.block.custom.EnchantAltarBlock;
import net.mloren.enchant_revised.block.entity.EnchantAltarBlockEntity;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipe;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipeInput;
import net.mloren.enchant_revised.recipe.ModRecipes;
import net.mloren.enchant_revised.inventory.ModMenuTypes;
import net.mloren.enchant_revised.util.EnchantAltar;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EnchantAltarMenu extends ModdedContainerMenu
{
    static final ResourceLocation EMPTY_SLOT_LAPIS_LAZULI = ResourceLocation.withDefaultNamespace("item/empty_slot_lapis_lazuli");
    public final EnchantAltarBlockEntity blockEntity;

    public final ItemStackHandler inputStackHandler = new ItemStackHandler(EnchantAltar.INPUT_SLOT_COUNT)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            if(level != null && !level.isClientSide())
                updateItemCrafting();
        }
    };
    public final ItemStackHandler resultStackHandler = new ItemStackHandler(EnchantAltar.OUTPUT_SLOT_COUNT)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            if(level != null && !level.isClientSide())
                completeItemCrafting();
        }
    };

    public EnchantAltarMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public EnchantAltarMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity)
    {
        super(ModMenuTypes.ENCHANT_ALTAR_MENU.get(), containerId, playerInventory, blockEntity);
        this.blockEntity = ((EnchantAltarBlockEntity) blockEntity);

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addSlots();
    }

    private void addSlots()
    {
        this.addSlot(new SlotItemHandler(this.inputStackHandler, EnchantAltar.LAPIS_SLOT, 27, 17)
        {
            @Override
            public boolean mayPlace(@NotNull ItemStack itemStack)
            {
                return itemStack.is(Items.LAPIS_LAZULI);
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon()
            {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_SLOT_LAPIS_LAZULI);
            }
        });

        this.addSlot(new SlotItemHandler(this.inputStackHandler, EnchantAltar.PRIMARY_INGREDIENT_SLOT, 27, 35));
        this.addSlot(new SlotItemHandler(this.inputStackHandler, EnchantAltar.SECONDARY_INGREDIENT_SLOT, 27, 53));
        this.addSlot(new SlotItemHandler(this.inputStackHandler, EnchantAltar.TARGET_ITEM_SLOT, 72, 35));

        this.addSlot(new SlotItemHandler(this.resultStackHandler, EnchantAltar.OUTPUT_SLOT, 130, 35)
        {
            @Override
            public boolean mayPlace(@NotNull ItemStack itemStack)
            {
                return false;
            }

            @Override
            public boolean mayPickup(Player playerIn)
            {
                return getBookshelvesValid() && super.mayPickup(playerIn);
            }

            @Override
            public boolean isHighlightable()
            {
                return getBookshelvesValid() && super.isHighlightable();
            }

            @Override
            public boolean isFake()
            {
                return true;
            }
        });
    }

    private void updateItemCrafting()
    {
        EnchantAltarRecipeInput recipeInput = GetRecipeInput();
        Optional<RecipeHolder<EnchantAltarRecipe>> recipe = getCurrentRecipe(recipeInput);
        if(recipe.isPresent())
        {
            ItemStack recipeOutput = recipe.get().value().assemble(recipeInput, level.registryAccess());
            resultStackHandler.setStackInSlot(EnchantAltar.OUTPUT_SLOT, recipeOutput);
        }
        else
        {
            ItemStack outputStack = resultStackHandler.getStackInSlot(EnchantAltar.OUTPUT_SLOT);
            if(!outputStack.isEmpty())
                resultStackHandler.setStackInSlot(EnchantAltar.OUTPUT_SLOT, ItemStack.EMPTY);
        }
    }

    private EnchantAltarRecipeInput GetRecipeInput()
    {
        return new EnchantAltarRecipeInput(
                inputStackHandler.getStackInSlot(EnchantAltar.PRIMARY_INGREDIENT_SLOT),
                inputStackHandler.getStackInSlot(EnchantAltar.SECONDARY_INGREDIENT_SLOT),
                inputStackHandler.getStackInSlot(EnchantAltar.LAPIS_SLOT),
                inputStackHandler.getStackInSlot(EnchantAltar.TARGET_ITEM_SLOT));
    }

    private Optional<RecipeHolder<EnchantAltarRecipe>> getCurrentRecipe(EnchantAltarRecipeInput recipeInput)
    {
        if(this.level == null)
            return Optional.empty();

        RecipeManager recipeManager = this.level.getRecipeManager();
        return recipeManager.getRecipeFor(ModRecipes.ENCHANT_ALTAR_TYPE.get(), recipeInput, level);
    }

    private void completeItemCrafting()
    {
        EnchantAltarRecipeInput recipeInput = GetRecipeInput();
        Optional<RecipeHolder<EnchantAltarRecipe>> recipe = getCurrentRecipe(recipeInput);
        if(recipe.isPresent())
        {
            ItemStack outputStack = resultStackHandler.getStackInSlot(EnchantAltar.OUTPUT_SLOT);
            if(outputStack.isEmpty())
            {
                SizedIngredient primary = recipe.get().value().primaryIngredient();
                int lapisCost = recipe.get().value().lapisCost();

                if(recipe.get().value().secondaryIngredient().isPresent())
                {
                    SizedIngredient secondary = recipe.get().value().secondaryIngredient().get();
                    inputStackHandler.extractItem(EnchantAltar.SECONDARY_INGREDIENT_SLOT, secondary.count(), false);
                }

                inputStackHandler.extractItem(EnchantAltar.PRIMARY_INGREDIENT_SLOT, primary.count(), false);
                inputStackHandler.extractItem(EnchantAltar.LAPIS_SLOT, lapisCost, false);
                inputStackHandler.extractItem(EnchantAltar.TARGET_ITEM_SLOT, 1, false);

                level.playSound(null, blockEntity.getBlockPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    private int getBookshelfCount()
    {
        int count = 0;
        for (BlockPos bookshelfPos : EnchantAltarBlock.BOOKSHELF_OFFSETS)
        {
            if (EnchantingTableBlock.isValidBookShelf(level, blockEntity.getBlockPos(), bookshelfPos))
            {
                ++count;
            }
        }
        return count;
    }

    public boolean getBookshelvesValid()
    {
        EnchantAltarRecipeInput recipeInput = GetRecipeInput();
        Optional<RecipeHolder<EnchantAltarRecipe>> recipe = getCurrentRecipe(recipeInput);
        if(recipe.isPresent())
        {
            int bookshelvesRequired = recipe.get().value().bookshelvesRequired();
            int bookshelfCount = getBookshelfCount();
            return bookshelfCount >= bookshelvesRequired;
        }
        return true;
    }

    public int getBookshelvesRequired()
    {
        EnchantAltarRecipeInput recipeInput = GetRecipeInput();
        Optional<RecipeHolder<EnchantAltarRecipe>> recipe = getCurrentRecipe(recipeInput);
        if(recipe.isPresent())
        {
            return recipe.get().value().bookshelvesRequired();
        }
        return 0;
    }

    @Override
    protected Block getBlock()
    {
        return ModBlocks.ENCHANT_ALTAR.get();
    }

    @Override
    protected int getInputSlotCount()
    {
        return EnchantAltar.INPUT_SLOT_COUNT;
    }

    @Override
    protected int getOutputSlotCount()
    {
        return EnchantAltar.OUTPUT_SLOT_COUNT;
    }

    @Override
    public void removed(Player player)
    {
        super.removed(player);
        this.clearItemStackHandler(player, this.inputStackHandler);
    }
}
