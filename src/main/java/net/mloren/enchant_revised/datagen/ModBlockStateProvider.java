package net.mloren.enchant_revised.datagen;

import net.minecraft.data.PackOutput;
import net.mloren.enchant_revised.MainMod;
import net.mloren.enchant_revised.block.ModBlocks;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider
{
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper)
    {
        super(output, MainMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        blockWithItem(ModBlocks.ENCHANT_ALTAR);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock)
    {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

//    private void blockItem(DeferredBlock<?> deferredBlock)
//    {
//        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("enchant_revised:block/" + deferredBlock.getId().getPath()));
//    }
//
//    private void blockItem(DeferredBlock<?> deferredBlock, String appendix)
//    {
//        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("enchant_revised:block/" + deferredBlock.getId().getPath() + appendix));
//    }
}
