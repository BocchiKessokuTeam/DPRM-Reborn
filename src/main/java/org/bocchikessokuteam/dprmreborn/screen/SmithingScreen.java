package org.bocchikessokuteam.dprmreborn.screen;

import com.alibaba.fastjson.JSONObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.bocchikessokuteam.dprmreborn.DPRMReborn;
import org.bocchikessokuteam.dprmreborn.container.SmithingContainer;
import org.bocchikessokuteam.dprmreborn.file.JsonManager;
import org.bocchikessokuteam.dprmreborn.network.CRUDRecipe;
import org.bocchikessokuteam.dprmreborn.network.Networking;
import org.bocchikessokuteam.dprmreborn.network.ScreenToggle;

public class SmithingScreen extends AbstractRecipeMakerScreen<SmithingContainer>{

    Boolean isResultSlotEmpty = true;
    Boolean isSlot1Empty = true;
    Boolean isSlot2Empty = true;
    public SmithingScreen(SmithingContainer smithingContainer, Inventory inv, Component titleIn) {
        super(smithingContainer, inv, titleIn);
        SCREEN_TEXTURE = new ResourceLocation(DPRMReborn.MODID, "textures/gui/smithing.png");
    }

    @Override
    protected void init() {
        super.init();
    }
    public void tick() {
        super.tick();
        Slot[] slots = container.smithingSlots;
        isResultSlotEmpty = slots[0].getItem().isEmpty();//判断合成槽是否为空
        isSlot1Empty = slots[1].getItem().isEmpty();//判断插槽1是否为空
        isSlot2Empty = slots[2].getItem().isEmpty();//判断插槽2是否为空
        this.confirmBtn.active = !isResultSlotEmpty && !isSlot1Empty && !isSlot2Empty&& !isRecipeNameEmpty && !isGroupNameEmpty;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack,partialTicks,mouseX,mouseY);
        //锻造台图标
//        this.minecraft.getItemRenderer().renderAndDecorateItem(new ItemStack(Items.SMITHING_TABLE),leftPos+100,topPos+33);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack,int mouseX, int mouseY) {
        super.renderLabels(matrixStack,mouseX,mouseY);
        String s = this.title.getString();
        this.font.draw(matrixStack,s, (float)(this.imageWidth / 2 - this.font.width(s) / 2), 6.0F, 4210752);
    }

    @Override
    public void render(PoseStack matrixStack,int mouseX, int mouseY, float particleTick) {
        super.render(matrixStack,mouseX, mouseY, particleTick);

        if (isRecipeJsonExist){
            renderFakeRecipe();
        }

    }

    private void renderFakeRecipe() {
        if (currentRecipe==null) return;
        String type = currentRecipe.getString("type");
        if (type.equals("smithing")){
            String slot1ItemRegistryName = currentRecipe.getJSONObject("base").getString("item");
            String slot2ItemRegistryName = currentRecipe.getJSONObject("addition").getString("item");
            String resultRegistryName = currentRecipe.getJSONObject("result").getString("item");
            ItemStack slot1ItemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(slot1ItemRegistryName)));
            ItemStack slot2ItemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(slot2ItemRegistryName)));

            ItemStack resultItemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(resultRegistryName)));
            this.minecraft.getItemRenderer().renderAndDecorateItem(slot1ItemStack,leftPos+27,topPos+47);
            this.minecraft.getItemRenderer().renderAndDecorateItem(slot2ItemStack,leftPos+76,topPos+47);
            this.minecraft.getItemRenderer().renderAndDecorateItem(resultItemStack,leftPos+134,topPos+47);
        }
    }

    @Override
    public void onConfirmBtnPress(Button button) {
        JSONObject smithingRecipe = JsonManager.genSmithingRecipe(container.smithingSlots);
        jsonPacket.put("json_recipe",smithingRecipe);
        jsonPacket.put("recipe_name",recipeNameInput.getValue());
        jsonPacket.put("crud","create");
        Networking.INSTANCE.sendToServer(new CRUDRecipe(jsonPacket.toJSONString()));

        jsonPacket.put("operate","open_smithing_screen");
        Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
//        this.minecraft.player.closeContainer();
    }

    @Override
    public void removed() {
        super.removed();
    }

    public void deleteRecipe(Button button) {
        jsonPacket.put("crud","delete");
        jsonPacket.put("select_recipe_name",recipeNameInput.getValue());
        Networking.INSTANCE.sendToServer(new CRUDRecipe(jsonPacket.toJSONString()));
        jsonPacket.put("select_recipe_name","");
        jsonPacket.put("operate","open_smithing_screen");
        Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
    }
}
