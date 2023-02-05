package org.bocchikessokuteam.dprmreborn.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bocchikessokuteam.dprmreborn.hander.Registry;


public class RecipeListContainer extends AbstractContainerMenu {
    private final FriendlyByteBuf FriendlyByteBuf;

    public RecipeListContainer(int sycID, Inventory playerInventory, FriendlyByteBuf FriendlyByteBuf) {
        super(Registry.recipeListContainer.get(),sycID);
        this.FriendlyByteBuf = FriendlyByteBuf;

    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }

    public FriendlyByteBuf getFriendlyByteBuf() {
        return FriendlyByteBuf;
    }
}
