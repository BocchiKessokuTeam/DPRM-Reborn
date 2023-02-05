package org.bocchikessokuteam.dprmreborn.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import org.bocchikessokuteam.dprmreborn.hander.Registry;

public class FurnaceContainer extends AbstractRecipeContainer {
    public CraftingContainer craftMatrix = new SingleSlot(this, 3, 3);
    public Container craftResult = new SingleSlot(this,3,3);
    public Slot[] furnaceSlots = new Slot[2];

    public FurnaceContainer(int sycID, Inventory playerInventory, FriendlyByteBuf FriendlyByteBuf) {
        super(Registry.furnaceContainer.get(),sycID,playerInventory,FriendlyByteBuf);

        //烧制物品插槽
        furnaceSlots[1] = this.addSlot(new Slot(this.craftMatrix, 0, 56, 17));
        //产物插槽
        furnaceSlots[0] = this.addSlot(new Slot(this.craftResult, 0, 116, 35));
    }
}