package org.bocchikessokuteam.dprmreborn.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.bocchikessokuteam.dprmreborn.DPRMReborn;

public class ToggleFurnaceTypeButton extends Button {

  public Type type;

  public ToggleFurnaceTypeButton(int widthIn, int heightIn, int width, int height, Button.OnPress onPress, Type type) {
    super(widthIn, heightIn, width, height, new TextComponent(""), onPress);
    this.type = type;
  }

  public void toggle(){
    int ordinal = type.ordinal();
    ordinal++;
    if (ordinal > 3)ordinal = 0;
    type = Type.values()[ordinal];
  }

  public enum Type {
    smelting, blasting, smoking, campfire_cooking;

    public ItemStack getIcon(){
      switch (this){
        default:
        case smelting:return new ItemStack(Blocks.FURNACE);
        case smoking:return new ItemStack(Blocks.SMOKER);
        case blasting:return new ItemStack(Blocks.BLAST_FURNACE);
        case campfire_cooking:return new ItemStack(Blocks.CAMPFIRE);
      }
    }

    public String getTitle(){
      switch (this){
        default:
        case smelting:return I18n.get("gui."+ DPRMReborn.MODID+".smelting.title");
        case smoking:return I18n.get("gui."+ DPRMReborn.MODID+".smoking.title");
        case blasting:return I18n.get("gui."+ DPRMReborn.MODID+".blasting.title");
        case campfire_cooking:return I18n.get("gui."+ DPRMReborn.MODID+".campfire_cooking.title");
      }
    }
  }
}
