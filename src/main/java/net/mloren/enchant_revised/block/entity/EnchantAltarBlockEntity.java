package net.mloren.enchant_revised.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipe;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipeInput;
import net.mloren.enchant_revised.recipe.ModRecipes;
import net.mloren.enchant_revised.screen.custom.EnchantAltarMenu;
import net.mloren.enchant_revised.util.Constants;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnchantAltarBlockEntity extends BlockEntity implements MenuProvider
{
    public final ItemStackHandler inputStackHandler = new ItemStackHandler(Constants.INPUT_SLOT_COUNT)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            setChanged();
            if(level != null && !level.isClientSide())
            {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), (Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS));
                updateItemCrafting();
            }
        }
    };
    public final ItemStackHandler resultStackHandler = new ItemStackHandler(Constants.OUTPUT_SLOT_COUNT)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            setChanged();
            if(level != null && !level.isClientSide())
            {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), (Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS));
                completeItemCrafting();
            }
        }
    };

    public EnchantAltarBlockEntity(BlockPos pos, BlockState blockState)
    {
        super(ModBlockEntities.ENCHANT_ALTAR_BE.get(), pos, blockState);
    }

    @Override
    public @NotNull Component getDisplayName()
    {
        return Component.translatable("block.enchant_revised.enchant_altar");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player)
    {
        return new EnchantAltarMenu(containerId, playerInventory, this);
    }

    private void updateItemCrafting()
    {
        EnchantAltarRecipeInput recipeInput = GetRecipeInput();
        Optional<RecipeHolder<EnchantAltarRecipe>> recipe = getCurrentRecipe(recipeInput);
        if(recipe.isPresent())
        {
            ItemStack outputStack = resultStackHandler.getStackInSlot(Constants.OUTPUT_SLOT);
            if(outputStack.isEmpty())
            {
                ItemStack recipeOutput = recipe.get().value().assemble(recipeInput, level.registryAccess());
                resultStackHandler.setStackInSlot(Constants.OUTPUT_SLOT, recipeOutput);
            }
        }
        else
        {
            ItemStack outputStack = resultStackHandler.getStackInSlot(Constants.OUTPUT_SLOT);
            if(!outputStack.isEmpty())
                resultStackHandler.setStackInSlot(Constants.OUTPUT_SLOT, ItemStack.EMPTY);
        }
    }

    private EnchantAltarRecipeInput GetRecipeInput()
    {
        return new EnchantAltarRecipeInput(
                inputStackHandler.getStackInSlot(Constants.PRIMARY_INGREDIENT_SLOT),
                inputStackHandler.getStackInSlot(Constants.SECONDARY_INGREDIENT_SLOT),
                inputStackHandler.getStackInSlot(Constants.LAPIS_SLOT),
                inputStackHandler.getStackInSlot(Constants.TARGET_ITEM_SLOT));
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
            ItemStack outputStack = resultStackHandler.getStackInSlot(Constants.OUTPUT_SLOT);
            if(outputStack.isEmpty())
            {
                //Ingredient lapis = recipe.get().value().fuel();
                //Ingredient primary = recipe.get().value().primaryIngredient();
                //Ingredient secondary = recipe.get().value().secondaryIngredient();

                inputStackHandler.extractItem(Constants.LAPIS_SLOT, 1, false);
                inputStackHandler.extractItem(Constants.PRIMARY_INGREDIENT_SLOT, 1, false);
                inputStackHandler.extractItem(Constants.SECONDARY_INGREDIENT_SLOT, 1, false);
                inputStackHandler.setStackInSlot(Constants.TARGET_ITEM_SLOT, ItemStack.EMPTY);
            }
        }
    }

    public void drops()
    {
        if (this.level == null)
            return;

        SimpleContainer inv = new SimpleContainer(inputStackHandler.getSlots());
        for(int i = 0; i < inputStackHandler.getSlots(); i++)
        {
            inv.setItem(i, inputStackHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries)
    {
        return saveWithoutMetadata(registries);
    }
}
