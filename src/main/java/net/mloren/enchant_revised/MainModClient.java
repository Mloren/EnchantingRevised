package net.mloren.enchant_revised;

import net.mloren.enchant_revised.config.ConfigClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = MainMod.MOD_ID, dist = Dist.CLIENT)
public class MainModClient
{
    public MainModClient(IEventBus bus, ModContainer modContainer)
    {
        // Load screens
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // Load configs
        modContainer.registerConfig(ModConfig.Type.CLIENT, ConfigClient.SPEC);
    }
}
