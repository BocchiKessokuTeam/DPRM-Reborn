package org.bocchikessokuteam.dprmreborn.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import org.bocchikessokuteam.dprmreborn.hander.Registry;

public class CraftingTableContainer extends AbstractRecipeContainer {

    public CraftingContainer craftMatrix = new SingleSlot(this, 3, 3);
    public Container craftResult = new ResultContainer();
    public Slot[] craftTableSlots = new Slot[10];
    public CraftingTableContainer(int id, Inventory playerInventory, FriendlyByteBuf FriendlyByteBuf) {
        super(Registry.craftingContainer.get(),id,playerInventory,FriendlyByteBuf);

        craftTableSlots[0] = this.addSlot(new Slot(this.craftResult, 0, 124, 35));

        craftTableSlots[1] = this.addSlot(new Slot(this.craftMatrix, 0, 30 + 0 * 18, 17 + 0 * 18));
        craftTableSlots[2] = this.addSlot(new Slot(this.craftMatrix, 1, 30 + 1 * 18, 17 + 0 * 18));
        craftTableSlots[3] = this.addSlot(new Slot(this.craftMatrix, 2, 30 + 2 * 18, 17 + 0 * 18));
        craftTableSlots[4] = this.addSlot(new Slot(this.craftMatrix, 3, 30 + 0 * 18, 17 + 1 * 18));
        craftTableSlots[5] = this.addSlot(new Slot(this.craftMatrix, 4, 30 + 1 * 18, 17 + 1 * 18));
        craftTableSlots[6] = this.addSlot(new Slot(this.craftMatrix, 5, 30 + 2 * 18, 17 + 1 * 18));
        craftTableSlots[7] = this.addSlot(new Slot(this.craftMatrix, 6, 30 + 0 * 18, 17 + 2 * 18));
        craftTableSlots[8] = this.addSlot(new Slot(this.craftMatrix, 7, 30 + 1 * 18, 17 + 2 * 18));
        craftTableSlots[9] = this.addSlot(new Slot(this.craftMatrix, 8, 30 + 2 * 18, 17 + 2 * 18));
    }
}
