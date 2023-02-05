package org.bocchikessokuteam.dprmreborn.network;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import org.bocchikessokuteam.dprmreborn.DPRMReborn;
import org.bocchikessokuteam.dprmreborn.containerprovider.*;
import org.bocchikessokuteam.dprmreborn.file.JsonManager;

import java.io.IOException;
import java.util.function.Supplier;

public class ScreenToggle {
    String jsonString;

    public ScreenToggle(FriendlyByteBuf buffer) {
        this.jsonString = buffer.readUtf(3000000);
    }

    //反序列化
    public ScreenToggle(String jsonString) {
        this.jsonString = jsonString;
    }

    //序列化
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.jsonString,3000000);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DPRMReborn.LOGGER.info("Send From Client:"+this.jsonString);
        });
        ServerPlayer serverPlayer = null;
        try {
            serverPlayer = ctx.get().getSender().createCommandSourceStack().getPlayerOrException();
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        JSONObject jsonPacket = JSON.parseObject(jsonString);
        try {
            jsonPacket.put("recipe_list", JsonManager.getAllRecipes(jsonPacket));
        } catch (IOException e) {
            e.printStackTrace();
        }


        //TODO
        //追加json信息

        if (jsonPacket.getString("operate").equals("open_crafting_screen")){
            NetworkHooks.openGui(serverPlayer,new CraftingTableContainerProvider(), (FriendlyByteBuf FriendlyByteBuf) -> {
                FriendlyByteBuf.writeUtf(jsonPacket.toJSONString());
            });
        }
        if (jsonPacket.getString("operate").equals("open_furnace_screen")){
            NetworkHooks.openGui(serverPlayer,new FurnaceContainerProvider(), (FriendlyByteBuf FriendlyByteBuf) -> {
                FriendlyByteBuf.writeUtf(jsonPacket.toJSONString());
            });
        }
        if (jsonPacket.getString("operate").equals("open_stonecutting_screen")){
            NetworkHooks.openGui(serverPlayer,new StonecuttingContainerProvider(), (FriendlyByteBuf FriendlyByteBuf) -> {
                FriendlyByteBuf.writeUtf(jsonPacket.toJSONString());
            });
        }
        if (jsonPacket.getString("operate").equals("open_smithing_screen")){
            NetworkHooks.openGui(serverPlayer,new SmithingContainerProvider(), (FriendlyByteBuf FriendlyByteBuf) -> {
                FriendlyByteBuf.writeUtf(jsonPacket.toJSONString());
            });
        }
        if (jsonPacket.getString("operate").equals("open_recipe_list_screen")){
            NetworkHooks.openGui(serverPlayer,new RecipeListContainerProvider(), (FriendlyByteBuf FriendlyByteBuf) -> {
                FriendlyByteBuf.writeUtf(jsonPacket.toJSONString());
            });
        }
        ctx.get().setPacketHandled(true);
    }
}
