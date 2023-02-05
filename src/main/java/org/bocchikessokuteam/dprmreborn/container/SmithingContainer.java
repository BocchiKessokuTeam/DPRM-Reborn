package org.bocchikessokuteam.dprmreborn.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import org.bocchikessokuteam.dprmreborn.hander.Registry;

public class SmithingContainer extends AbstractRecipeContainer {

    public CraftingContainer craftMatrix = new SingleSlot(this, 3, 3);
    public Container craftResult = new SingleSlot(this,3,3);
    public Slot[] smithingSlots = new Slot[3];


    public SmithingContainer(int sycID, Inventory playerInventory, FriendlyByteBuf FriendlyByteBuf) {
        super(Registry.smithingContainer.get(),sycID,playerInventory,FriendlyByteBuf);
        //被锻造物品插槽
        smithingSlots[1] = this.addSlot(new Slot(this.craftMatrix,0,27,47));
        //锻造材料插槽
        smithingSlots[2] = this.addSlot(new Slot(this.craftMatrix,1,76,47));
        //产物插槽
        smithingSlots[0] = this.addSlot(new Slot(this.craftResult,0,134,47));
    }
}
