package org.bocchikessokuteam.dprmreborn.command;

import com.alibaba.fastjson.JSONObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.network.NetworkHooks;
import org.bocchikessokuteam.dprmreborn.containerprovider.CraftingContainerProvider;
import org.bocchikessokuteam.dprmreborn.file.JsonManager;

import java.io.IOException;

public class CraftingCommand implements Command<CommandSourceStack> {
    public static CraftingCommand instance = new CraftingCommand();

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
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


        NetworkHooks.openGui(serverPlayer,new CraftingContainerProvider(), (FriendlyByteBuf) -> {
            FriendlyByteBuf.writeUtf(jsonPacket.toJSONString());
        });
        return 0;

    }
}
