package org.bocchikessokuteam.dprmreborn.hander;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.bocchikessokuteam.dprmreborn.command.*;

@Mod.EventBusSubscriber
public class CommandEventHander {
    @SubscribeEvent
    public static void onServerStarting(RegisterCommandsEvent event){

        Boolean onlyOperatorCanUse = Config.ONLY_OP_CAN_USE.get();
        Integer permissionLevel = onlyOperatorCanUse?2:0;

        //命令节点"dprm"
        LiteralArgumentBuilder<CommandSourceStack> dprm = Commands.literal("dprm");
        dprm.requires((commandSource)-> commandSource.hasPermission(permissionLevel));
        dprm.executes(RecipeListCommand.instance);
        //命令节点"crafting"
        LiteralArgumentBuilder<CommandSourceStack> crafting = Commands.literal("crafting");
        crafting.requires((commandSource)-> commandSource.hasPermission(permissionLevel));
        crafting.executes(CraftingCommand.instance);
        dprm.then(crafting);
        //命令节点"furnace"
        LiteralArgumentBuilder<CommandSourceStack> blasting = Commands.literal("furnace");
        blasting.requires((commandSource -> commandSource.hasPermission(permissionLevel)));
        blasting.executes(FurnaceCommand.instance);//命令功能
        dprm.then(blasting);
        //命令节点"stonecutting"
        LiteralArgumentBuilder<CommandSourceStack> stonecutting = Commands.literal("stonecutting");
        stonecutting.requires((commandSource -> commandSource.hasPermission(permissionLevel)));
        stonecutting.executes(StonecuttingCommand.instance);
        dprm.then(stonecutting);
        //命令节点"smithing"
        LiteralArgumentBuilder<CommandSourceStack> smithing = Commands.literal("smithing");
        smithing.requires((commandSource -> commandSource.hasPermission(permissionLevel)));
        smithing.executes(SmithingCommand.instance);
        dprm.then(smithing);
        //TODO



        //注册命令
        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
        commandDispatcher.register(dprm);
    }
}
