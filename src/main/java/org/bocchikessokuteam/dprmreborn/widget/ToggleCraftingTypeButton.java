package org.bocchikessokuteam.dprmreborn.widget;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import org.bocchikessokuteam.dprmreborn.DPRMReborn;

public class ToggleCraftingTypeButton extends Button {

  public Type type;

  public ToggleCraftingTypeButton(int widthIn, int heightIn, int width, int height, Button.OnPress onPress, Type type) {
    super(widthIn, heightIn, width, height, new TextComponent(""), onPress);
    this.type = type;
  }

  public void toggle(){
    int ordinal = type.ordinal();
    ordinal++;
    if (ordinal > 1)ordinal = 0;
    type = Type.values()[ordinal];
  }

  public enum Type {
    crafting_shaped, crafting_shapeless;


    public String getTitle(){
      switch (this){
        default:
        case crafting_shaped:return I18n.get("gui."+ DPRMReborn.MODID+".crafting_shaped.title");
        case crafting_shapeless:return I18n.get("gui."+ DPRMReborn.MODID+".crafting_shapeless.title");
      }
    }
  }
}
