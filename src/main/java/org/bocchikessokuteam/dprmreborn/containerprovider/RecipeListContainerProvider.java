package org.bocchikessokuteam.dprmreborn.containerprovider;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bocchikessokuteam.dprmreborn.container.RecipeListContainer;

import javax.annotation.Nullable;

public class RecipeListContainerProvider implements MenuProvider {

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int sycID, Inventory playerInventory, Player player) {
        FriendlyByteBuf FriendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
        return new RecipeListContainer(sycID,playerInventory,FriendlyByteBuf);
    }
}
