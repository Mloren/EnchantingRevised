package net.mloren.enchant_revised.datagen;

import net.minecraft.data.PackOutput;
import net.mloren.enchant_revised.MainMod;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider
{
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper)
    {
        super(output, MainMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels()
    {
    }
}
