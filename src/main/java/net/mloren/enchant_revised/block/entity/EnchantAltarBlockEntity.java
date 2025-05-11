package net.mloren.enchant_revised.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.mloren.enchant_revised.inventory.custom.EnchantAltarMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
