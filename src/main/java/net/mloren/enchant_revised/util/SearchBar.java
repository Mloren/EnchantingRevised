package net.mloren.enchant_revised.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class SearchBar extends EditBox
{
    public SearchBar(Font font, int x, int y, int width, int height, Component message)
    {
        super(font, x, y, width, height, message);
    }

    @Override
    protected boolean isValidClickButton(int button)
    {
        return button == 0 || button == 1;
    }
}
