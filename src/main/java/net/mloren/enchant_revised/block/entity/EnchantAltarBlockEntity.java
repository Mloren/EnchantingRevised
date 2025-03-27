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
import net.minecraft.world.inventory.ContainerLevelAccess;
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
    private static BlockPos blockPos;

//    public final ItemStackHandler itemStackHandler = new ItemStackHandler(Constants.TOTAL_SLOT_COUNT)
//    {
//        @Override
//        protected void onContentsChanged(int slot)
//        {
//            setChanged();
//            if(level != null && !level.isClientSide())
//            {
//                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), (Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS));
//                onSlotChanged(slot);
//            }
//        }
//    };

    public EnchantAltarBlockEntity(BlockPos pos, BlockState blockState)
    {
        super(ModBlockEntities.ENCHANT_ALTAR_BE.get(), pos, blockState);
        blockPos = pos;
    }

    @Override
    public @NotNull Component getDisplayName()
    {
        return Component.translatable("block.enchant_revised.enchant_altar");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player)
    {
        return new EnchantAltarMenu(containerId, playerInventory, ContainerLevelAccess.create(level, blockPos), this);
    }

//    public void onSlotChanged(int slot)
//    {
//        if(slot == Constants.OUTPUT_SLOT)
//            completeItemCrafting();
//        else
//            updateItemCrafting();
//    }

//    private void updateItemCrafting()
//    {
//        Optional<RecipeHolder<EnchantAltarRecipe>> recipe = getCurrentRecipe();
//        if(recipe.isPresent())
//        {
//            ItemStack outputStack = itemStackHandler.getStackInSlot(Constants.OUTPUT_SLOT);
//            if(outputStack.isEmpty())
//            {
//                ItemStack recipeOutput = recipe.get().value().output();
//                itemStackHandler.setStackInSlot(Constants.OUTPUT_SLOT, new ItemStack(recipeOutput.getItem(), 1));
//            }
//        }
//        else
//        {
//            ItemStack outputStack = itemStackHandler.getStackInSlot(Constants.OUTPUT_SLOT);
//            if(!outputStack.isEmpty())
//                itemStackHandler.setStackInSlot(Constants.OUTPUT_SLOT, ItemStack.EMPTY);
//        }
//    }

//    private Optional<RecipeHolder<EnchantAltarRecipe>> getCurrentRecipe()
//    {
//        if(this.level == null)
//            return Optional.empty();
//
//        RecipeManager recipeManager = this.level.getRecipeManager();
//        EnchantAltarRecipeInput recipeInput = new EnchantAltarRecipeInput(itemStackHandler.getStackInSlot(Constants.PRIMARY_INGREDIENT_SLOT));
//
//        return recipeManager.getRecipeFor(ModRecipes.ENCHANT_ALTAR_TYPE.get(), recipeInput, level);
//    }
//
//    private void completeItemCrafting()
//    {
//        Optional<RecipeHolder<EnchantAltarRecipe>> recipe = getCurrentRecipe();
//        if(recipe.isPresent())
//        {
//            ItemStack outputStack = itemStackHandler.getStackInSlot(Constants.OUTPUT_SLOT);
//            if(outputStack.isEmpty())
//            {
//                //Ingredient lapis = recipe.get().value().lapisInput();
//                Ingredient primary = recipe.get().value().primaryIngredient();
//                //Ingredient secondary = recipe.get().value().secondaryIngredient();
//                //Ingredient item = recipe.get().value().targetItem();
//
//                //itemStackHandler.extractItem(Constants.LAPIS_SLOT, 1, false);
//                itemStackHandler.extractItem(Constants.PRIMARY_INGREDIENT_SLOT, 1, false);
//                //itemStackHandler.extractItem(Constants.SECONDARY_INGREDIENT_SLOT, 1, false);
//                //itemStackHandler.extractItem(Constants.TARGET_ITEM_SLOT, 1, false);
//            }
//        }
//    }
//
//    public void drops()
//    {
//        if (this.level == null)
//            return;
//
//        SimpleContainer inv = new SimpleContainer(itemStackHandler.getSlots());
//        for(int i = 0; i < itemStackHandler.getSlots(); i++)
//        {
//            inv.setItem(i, itemStackHandler.getStackInSlot(i));
//        }
//
//        Containers.dropContents(this.level, this.worldPosition, inv);
//    }

//    private boolean hasRecipe()
//    {
//        Optional<RecipeHolder<EnchantAltarRecipe>> recipe = getCurrentRecipe();
//        if(recipe.isEmpty())
//            return false;
//
//        ItemStack output = recipe.get().value().output();
//        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
//    }



//    private boolean canInsertItemIntoOutputSlot(ItemStack output)
//    {
//        return itemStackHandler.getStackInSlot(OUTPUT_SLOT).isEmpty()
//                || itemStackHandler.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
//    }

//    private boolean canInsertAmountIntoOutputSlot(int count)
//    {
//        int maxCount = itemStackHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemStackHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
//        int currentCount = itemStackHandler.getStackInSlot(OUTPUT_SLOT).getCount();
//
//        return maxCount >= currentCount + count;
//    }

//    @Override
//    protected void saveAdditional(CompoundTag tag, HolderLookup.@NotNull Provider registries)
//    {
//        tag.put("inventory", itemStackHandler.serializeNBT(registries));
//
//        super.saveAdditional(tag, registries);
//    }
//
//    @Override
//    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries)
//    {
//        super.loadAdditional(tag, registries);
//
//        itemStackHandler.deserializeNBT(registries, tag.getCompound("inventory"));
//    }

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
