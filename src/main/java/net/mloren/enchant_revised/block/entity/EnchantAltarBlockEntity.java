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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipe;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipeInput;
import net.mloren.enchant_revised.recipe.ModRecipes;
import net.mloren.enchant_revised.screen.custom.EnchantAltarMenu;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnchantAltarBlockEntity extends BlockEntity implements MenuProvider
{
    public final ItemStackHandler inventory = new ItemStackHandler(2)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            setChanged();
            if(!level.isClientSide())
            {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

    public EnchantAltarBlockEntity(BlockPos pos, BlockState blockState)
    {
        super(ModBlockEntities.ENCHANT_ALTAR_BE.get(), pos, blockState);
        data = new ContainerData()
        {
            @Override
            public int get(int index)
            {
                return switch(index)
                {
                    case 0 -> EnchantAltarBlockEntity.this.progress;
                    case 1 -> EnchantAltarBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value)
            {
                switch(index)
                {
                    case 0: EnchantAltarBlockEntity.this.progress = value;
                    case 1: EnchantAltarBlockEntity.this.maxProgress = value;
                };
            }

            @Override
            public int getCount()
            {
                return 2;
            }
        };
    }

    public void drops()
    {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++)
        {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    public Component getDisplayName()
    {
        return Component.translatable("block.enchant_revised.enchant_altar");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player)
    {
        return new EnchantAltarMenu(containerId, playerInventory, this, this.data);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState)
    {
        if(hasRecipe())
        {
            increaseCraftingProgress();
            setChanged(level, blockPos, blockState);

            if(hasCraftingFinished())
            {
                creaftItem();
                resetProgress();
            }
        }
        else
        {
            resetProgress();
        }
    }

    private void creaftItem()
    {
        Optional<RecipeHolder<EnchantAltarRecipe>> recipe = getCurrentRecipe();
        ItemStack output = recipe.get().value().output();

        inventory.extractItem(INPUT_SLOT, 1, false);
        inventory.setStackInSlot(OUTPUT_SLOT, new ItemStack(output.getItem(), inventory.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount()));
    }

    private void resetProgress()
    {
        progress = 0;
        maxProgress = 72;
    }

    private boolean hasCraftingFinished()
    {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress()
    {
        progress++;
    }

    private boolean hasRecipe()
    {
        Optional<RecipeHolder<EnchantAltarRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty())
            return false;

        ItemStack output = recipe.get().value().output();
        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
    }

    private Optional<RecipeHolder<EnchantAltarRecipe>> getCurrentRecipe()
    {
        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.ENCHANT_ALTAR_TYPE.get(), new EnchantAltarRecipeInput(inventory.getStackInSlot(INPUT_SLOT)), level);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output)
    {
        return inventory.getStackInSlot(OUTPUT_SLOT).isEmpty()
                || inventory.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count)
    {
        int maxCount = inventory.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : inventory.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = inventory.getStackInSlot(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + count;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("enchant_altar.progress", progress);
        tag.putInt("enchant_altar.max_progress", maxProgress);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);

        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("enchant_altar.progress");
        maxProgress = tag.getInt("enchant_altar.max_progress");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        return saveWithoutMetadata(registries);
    }
}
