package org.bocchikessokuteam.dprmreborn.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import org.bocchikessokuteam.dprmreborn.hander.Registry;

public class StonecuttingContainer extends AbstractRecipeContainer {

    public CraftingContainer craftMatrix = new SingleSlot(this, 3, 3);
    public Container craftResult = new ResultContainer();
    public Slot[] stonecuttingSlots = new Slot[2];


    public StonecuttingContainer(int sycID, Inventory playerInventory, FriendlyByteBuf FriendlyByteBuf) {
        super(Registry.stonecuttingContainer.get(),sycID,playerInventory,FriendlyByteBuf);
        //烧制物品插槽
        stonecuttingSlots[1] = this.addSlot(new Slot(this.craftMatrix, 0, 20, 33));
        //产物插槽
        stonecuttingSlots[0] = this.addSlot(new Slot(this.craftResult, 0, 143, 33));

    }
}