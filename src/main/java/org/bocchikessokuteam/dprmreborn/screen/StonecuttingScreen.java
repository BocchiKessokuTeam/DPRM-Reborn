package org.bocchikessokuteam.dprmreborn.screen;

import com.alibaba.fastjson.JSONObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.bocchikessokuteam.dprmreborn.DPRMReborn;
import org.bocchikessokuteam.dprmreborn.container.StonecuttingContainer;
import org.bocchikessokuteam.dprmreborn.file.JsonManager;
import org.bocchikessokuteam.dprmreborn.network.ScreenToggle;
import org.bocchikessokuteam.dprmreborn.network.Networking;
import org.bocchikessokuteam.dprmreborn.network.CRUDRecipe;

public class StonecuttingScreen extends AbstractRecipeMakerScreen<StonecuttingContainer> {

    Boolean isResultSlotEmpty = true;
    Boolean isCraftingSlotEmpty = true;

    public StonecuttingScreen(StonecuttingContainer stonecuttingContainer, Inventory inv, Component titleIn) {
        super(stonecuttingContainer, inv, titleIn);
        SCREEN_TEXTURE = new ResourceLocation(DPRMReborn.MODID, "textures/gui/stonecutting.png");
    }

    @Override
    protected void init() {
        super.init();
    }
    public void tick() {
        super.tick();
        Slot[] slots = container.stonecuttingSlots;
        isResultSlotEmpty = slots[0].getItem().isEmpty();//判断合成槽是否为空
        isCraftingSlotEmpty = slots[1].getItem().isEmpty();//判断

        this.confirmBtn.active = !isResultSlotEmpty && !isCraftingSlotEmpty && !isRecipeNameEmpty && !isGroupNameEmpty;
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack,partialTicks,mouseX,mouseY);
        //切石机
        this.minecraft.getItemRenderer().renderAndDecorateItem(new ItemStack(Items.STONECUTTER),leftPos+79,topPos+33);
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
        if (type.equals("stonecutting")){
            String ingredientItemRegistryName = currentRecipe.getJSONObject("ingredient").getString("item");
            String resultRegistryName = currentRecipe.getString("result");
            ItemStack ingredientItemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ingredientItemRegistryName)));
            Integer resultcount = currentRecipe.getInteger("count");
            ItemStack resultItemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(resultRegistryName)),resultcount);
            this.minecraft.getItemRenderer().renderAndDecorateItem(ingredientItemStack,leftPos+20,topPos+33);
            this.minecraft.getItemRenderer().renderAndDecorateItem(resultItemStack,leftPos+143,topPos+33);
            if (resultcount>1){
                this.minecraft.getItemRenderer().renderGuiItemDecorations(this.font,resultItemStack,leftPos+143,topPos+33,resultcount.toString());
            }
        }
    }

    @Override
    public void onConfirmBtnPress(Button button) {
        JSONObject stonecuttingRecipe = JsonManager.genStonecuttingRecipe(container.stonecuttingSlots, groupNameInput.getValue());
        jsonPacket.put("json_recipe",stonecuttingRecipe);
        jsonPacket.put("recipe_name",recipeNameInput.getValue());
        jsonPacket.put("crud","create");
        Networking.INSTANCE.sendToServer(new CRUDRecipe(jsonPacket.toJSONString()));

        jsonPacket.put("operate","open_stonecutting_screen");
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
        jsonPacket.put("operate","open_stonecutting_screen");
        Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
    }
}
