package net.mloren.enchant_revised.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mloren.enchant_revised.block.entity.EnchantAltarBlockEntity;
import net.mloren.enchant_revised.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnchantAltarBlock extends BaseEntityBlock
{
    public static final MapCodec<EnchantAltarBlock> CODEC = simpleCodec(EnchantAltarBlock::new);
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    public static final List<BlockPos> BOOKSHELF_OFFSETS = BlockPos.betweenClosedStream(-2, 0, -2, 2, 1, 2)
            .filter(blockPos -> Math.abs(blockPos.getX()) == 2 || Math.abs(blockPos.getZ()) == 2)
            .map(BlockPos::immutable)
            .toList();

    public EnchantAltarBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec()
    {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state)
    {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    //Called periodically clientside on blocks near the player to show effects (like furnace fire particles).
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        super.animateTick(state, level, pos, random);

        for (BlockPos blockpos : BOOKSHELF_OFFSETS)
        {
            if (random.nextInt(16) == 0 && isValidBookShelf(level, pos, blockpos))
            {
                level.addParticle(
                        ParticleTypes.ENCHANT,
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 2.0,
                        (double)pos.getZ() + 0.5,
                        (double)((float)blockpos.getX() + random.nextFloat()) - 0.5,
                        (double)((float)blockpos.getY() - random.nextFloat() - 1.0F),
                        (double)((float)blockpos.getZ() + random.nextFloat()) - 0.5
                );
            }
        }
    }

    public static boolean isValidBookShelf(Level level, BlockPos enchantingTablePos, BlockPos bookshelfPos)
    {
        return level.getBlockState(enchantingTablePos.offset(bookshelfPos)).getEnchantPowerBonus(level, enchantingTablePos.offset(bookshelfPos)) != 0
                && level.getBlockState(enchantingTablePos.offset(bookshelfPos.getX() / 2, bookshelfPos.getY(), bookshelfPos.getZ() / 2))
                .is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new EnchantAltarBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if(!level.isClientSide())
        {
            BlockEntity entity = level.getBlockEntity(pos);
            if(entity instanceof EnchantAltarBlockEntity enchantAltarBlockEntity)
            {
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(enchantAltarBlockEntity, Component.translatable("container.enchant")), pos);
            }
            else
            {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType)
    {
        return false;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType)
    {
        return level.isClientSide ? createTickerHelper(blockEntityType, ModBlockEntities.ENCHANT_ALTAR_BE.get(), EnchantAltarBlockEntity::bookAnimationTick) : null;
    }
}
