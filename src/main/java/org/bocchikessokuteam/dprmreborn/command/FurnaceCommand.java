package org.bocchikessokuteam.dprmreborn.command;

import com.alibaba.fastjson.JSONObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.network.NetworkHooks;
import org.bocchikessokuteam.dprmreborn.containerprovider.FurnaceContainerProvider;
import org.bocchikessokuteam.dprmreborn.file.JsonManager;

import java.io.IOException;

public class FurnaceCommand implements Command<CommandSourceStack> {
    public static FurnaceCommand instance = new FurnaceCommand();

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
//        String datapacksDirPath = serverPlayer.getServerWorld().getServer().getDataDirectory().getPath() + "\\datapacks";
        String datapacksDirPath = JsonManager.getWorldFolder(serverPlayer.getLevel()).getPath() + "\\datapacks";
        JSONObject jsonPacket = new JSONObject(true);
        jsonPacket.put("player_name",serverPlayer.getName().getString());
        jsonPacket.put("datapacks_dir_path",datapacksDirPath);
        jsonPacket.put("select_recipe_name","");
        jsonPacket.put("current_page",1);
        try {
            jsonPacket.put("recipe_list", JsonManager.getAllRecipes(jsonPacket));
        } catch (IOException e) {
            e.printStackTrace();
        }

        NetworkHooks.openGui(serverPlayer,new FurnaceContainerProvider(), (FriendlyByteBuf FriendlyByteBuf) -> {
            FriendlyByteBuf.writeUtf(jsonPacket.toJSONString());
        });
        return 0;
    }
}
