package org.bocchikessokuteam.dprmreborn.network;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.bocchikessokuteam.dprmreborn.DPRMReborn;
import org.bocchikessokuteam.dprmreborn.file.JsonManager;
import java.util.function.Supplier;

public class CRUDRecipe {
    String jsonString;

    public CRUDRecipe(FriendlyByteBuf buffer) {
        this.jsonString = buffer.readUtf(3000000);
    }

    //反序列化
    public CRUDRecipe(String jsonString) {
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
        //还原接受包
        JSONObject jsonPacket = JSON.parseObject(jsonString, Feature.OrderedField);
        JSONObject json_recipe = jsonPacket.getJSONObject("json_recipe");
        String recipe_name = jsonPacket.getString("recipe_name");
        //创建json文件
        if (jsonPacket.getString("crud").equals("create")){
            JSONObject result = JsonManager.createJsonFile(jsonPacket,json_recipe,recipe_name);
            if (result.getBoolean("success")){
                serverPlayer.sendMessage(new TranslatableComponent("gui."+ DPRMReborn.MODID+".chat.recipe_generate_successed",result.getString("dir")),serverPlayer.getUUID());
            }
            else { serverPlayer.sendMessage(new TranslatableComponent("gui."+ DPRMReborn.MODID+".chat.recipe_generate_failed",result.getString("dir")),serverPlayer.getUUID()); }
        }
        if (jsonPacket.getString("crud").equals("delete")) {
            String result = JsonManager.deleteJsonFile(jsonPacket);
            serverPlayer.sendMessage(new TextComponent(result),serverPlayer.getUUID());
        }

        //向玩家反馈创建信息

        ctx.get().setPacketHandled(true);
    }
}
