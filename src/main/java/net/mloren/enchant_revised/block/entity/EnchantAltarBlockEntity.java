package net.mloren.enchant_revised.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mloren.enchant_revised.block.custom.EnchantAltarBlock;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipe;
import net.mloren.enchant_revised.recipe.EnchantAltarRecipeInput;
import net.mloren.enchant_revised.recipe.ModRecipes;
import net.mloren.enchant_revised.screen.custom.EnchantAltarMenu;
import net.mloren.enchant_revised.util.EnchantAltar;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnchantAltarBlockEntity extends BlockEntity implements MenuProvider
{
    public int time;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    public float rot;
    public float oRot;
    public float tRot;
    private static final RandomSource RANDOM = RandomSource.create();

    public final ItemStackHandler inputStackHandler = new ItemStackHandler(EnchantAltar.INPUT_SLOT_COUNT)
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
    public final ItemStackHandler resultStackHandler = new ItemStackHandler(EnchantAltar.OUTPUT_SLOT_COUNT)
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
                SizedIngredient secondary = recipe.get().value().secondaryIngredient();
                int lapisCost = recipe.get().value().lapisCost();

                inputStackHandler.extractItem(EnchantAltar.PRIMARY_INGREDIENT_SLOT, primary.count(), false);
                inputStackHandler.extractItem(EnchantAltar.SECONDARY_INGREDIENT_SLOT, secondary.count(), false);
                inputStackHandler.extractItem(EnchantAltar.LAPIS_SLOT, lapisCost, false);
                inputStackHandler.extractItem(EnchantAltar.TARGET_ITEM_SLOT, 1, false);

                level.playSound(null, getBlockPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    private int getBookshelfCount()
    {
        int count = 0;
        for (BlockPos bookshelfPos : EnchantAltarBlock.BOOKSHELF_OFFSETS)
        {
            if (EnchantingTableBlock.isValidBookShelf(level, getBlockPos(), bookshelfPos))
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

    //Client-side tick
    public static void bookAnimationTick(Level level, BlockPos pos, BlockState state, EnchantAltarBlockEntity enchantAltar)
    {
        enchantAltar.oOpen = enchantAltar.open;
        enchantAltar.oRot = enchantAltar.rot;
        Player player = level.getNearestPlayer((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 3.0, false);
        if (player != null) {
            double d0 = player.getX() - ((double)pos.getX() + 0.5);
            double d1 = player.getZ() - ((double)pos.getZ() + 0.5);
            enchantAltar.tRot = (float) Mth.atan2(d1, d0);
            enchantAltar.open += 0.1F;
            if (enchantAltar.open < 0.5F || RANDOM.nextInt(40) == 0)
            {
                float f1 = enchantAltar.flipT;

                do
                {
                    enchantAltar.flipT = enchantAltar.flipT + (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
                }
                while (f1 == enchantAltar.flipT);
            }
        }
        else
        {
            enchantAltar.tRot += 0.02F;
            enchantAltar.open -= 0.1F;
        }

        while (enchantAltar.rot >= (float) Math.PI)
        {
            enchantAltar.rot -= (float) (Math.PI * 2);
        }

        while (enchantAltar.rot < (float) -Math.PI)
        {
            enchantAltar.rot += (float) (Math.PI * 2);
        }

        while (enchantAltar.tRot >= (float) Math.PI)
        {
            enchantAltar.tRot -= (float) (Math.PI * 2);
        }

        while (enchantAltar.tRot < (float) -Math.PI)
        {
            enchantAltar.tRot += (float) (Math.PI * 2);
        }

        float f2 = enchantAltar.tRot - enchantAltar.rot;

        while (f2 >= (float) Math.PI)
        {
            f2 -= (float) (Math.PI * 2);
        }

        while (f2 < (float) -Math.PI)
        {
            f2 += (float) (Math.PI * 2);
        }

        enchantAltar.rot += f2 * 0.4F;
        enchantAltar.open = Mth.clamp(enchantAltar.open, 0.0F, 1.0F);
        enchantAltar.time++;
        enchantAltar.oFlip = enchantAltar.flip;
        float f = (enchantAltar.flipT - enchantAltar.flip) * 0.4F;
        float f3 = 0.2F;
        f = Mth.clamp(f, -0.2F, 0.2F);
        enchantAltar.flipA = enchantAltar.flipA + (f - enchantAltar.flipA) * 0.9F;
        enchantAltar.flip = enchantAltar.flip + enchantAltar.flipA;
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
