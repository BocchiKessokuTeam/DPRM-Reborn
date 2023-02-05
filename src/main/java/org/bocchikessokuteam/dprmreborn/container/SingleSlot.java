package org.bocchikessokuteam.dprmreborn.container;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;

public class SingleSlot extends CraftingContainer {
    public SingleSlot(AbstractContainerMenu eventHandlerIn, int width, int height) {
        super(eventHandlerIn, width, height);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
