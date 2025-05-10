package net.mloren.enchant_revised;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = MainMod.MOD_ID, dist = Dist.CLIENT)
public class MainModClient
{
    public MainModClient(IEventBus bus, ModContainer modContainer)
    {
        // Load screens
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
