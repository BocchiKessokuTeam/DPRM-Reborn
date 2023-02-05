package org.bocchikessokuteam.dprmreborn.screen;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.bocchikessokuteam.dprmreborn.DPRMReborn;
import org.bocchikessokuteam.dprmreborn.container.CraftingTableContainer;
import org.bocchikessokuteam.dprmreborn.file.JsonManager;
import org.bocchikessokuteam.dprmreborn.network.Networking;
import org.bocchikessokuteam.dprmreborn.network.ScreenToggle;
import org.bocchikessokuteam.dprmreborn.network.CRUDRecipe;
import org.bocchikessokuteam.dprmreborn.widget.ToggleCraftingTypeButton;

public class CraftingScreen extends AbstractRecipeMakerScreen<CraftingTableContainer> {
    //提交按钮判断条件
    Boolean isResultSlotEmpty = true;
    Boolean isCraftingSlotEmpty = true;
    ToggleCraftingTypeButton.Type currentType = ToggleCraftingTypeButton.Type.crafting_shaped;
    ToggleCraftingTypeButton toggleCraftingBtn;


    public CraftingScreen(CraftingTableContainer craftingContainer, Inventory inv, Component titleIn) {
        super(craftingContainer, inv, titleIn);
        SCREEN_TEXTURE = new ResourceLocation(DPRMReborn.MODID, "textures/gui/crafting_table.png");
    }

    @Override
    protected void init() {
        super.init();
        this.toggleCraftingBtn = new ToggleCraftingTypeButton(leftPos+88,topPos+28,28,28,this::changeCraftingType,currentType);
        this.children.add(toggleCraftingBtn);
    }

    private void changeCraftingType(Button button) {
        ((ToggleCraftingTypeButton) button).toggle();
        int ordinal = currentType.ordinal();
        ordinal++;
        if (ordinal > 1) ordinal = 0;
        currentType = ToggleCraftingTypeButton.Type.values()[ordinal];
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float particleTick) {
        super.render(matrixStack,mouseX, mouseY, particleTick);

//        try {
//            String type = currentRecipe.getString("type");
//            if (type.equals("minecraft:crafting_shaped") || type.equals("minecraft:crafting_shapeless")) {
//            } else if (type.equals("smelting")||type.equals("smoking")||type.equals("blasting")||type.equals("campfire_cooking")||type.equals("stonecutting")){
//                this.font.draw(I18n.format("gui."+DPRMReborn.MODID+".tooltips.this_is_xx_recipe_type",JsonManager.translateRecipeType(type)), leftPos - 82, topPos - 15, 0XFFFFFF);
//            }
//        } catch (Exception e) {}

        if (isRecipeJsonExist){
            renderFakeRecipe();
            if (recipeNameInput!=null){
                if (recipeNameInput.isFocused()){
                    if (currentRecipe.getString("type").equals("minecraft:crafting_shaped"))currentType = ToggleCraftingTypeButton.Type.crafting_shaped;
                    if (currentRecipe.getString("type").equals("minecraft:crafting_shapeless"))currentType = ToggleCraftingTypeButton.Type.crafting_shapeless;
                }
            }
        }
    }

    Boolean isToggleCraftingBtnHovered = false;
    @Override
    protected void renderTooltip(PoseStack matrixStack,int mouseX, int mouseY) {
        super.renderTooltip(matrixStack,mouseX, mouseY);
        //判断是否悬停在切换按钮
        if ((mouseX>=leftPos+88)&&(mouseX<=leftPos+116)&&(mouseY>=topPos+28)&&(mouseY<=topPos+56)){
            this.renderTooltip(matrixStack,new TranslatableComponent("gui."+ DPRMReborn.MODID+".toggle_crafting_type"),mouseX,mouseY);
            isToggleCraftingBtnHovered = true;
        }else isToggleCraftingBtnHovered = false;
    }

    @Override
    public void onConfirmBtnPress(Button button) {
        JSONObject craftingRecipe = null;
        if (currentType == ToggleCraftingTypeButton.Type.crafting_shaped)craftingRecipe = JsonManager.genCraftingShapedRecipe(container.craftTableSlots, groupNameInput.getValue());
        if (currentType == ToggleCraftingTypeButton.Type.crafting_shapeless)craftingRecipe = JsonManager.genCraftingShapelessRecipe(container.craftTableSlots,groupNameInput.getValue());
        jsonPacket.put("json_recipe",craftingRecipe);
        jsonPacket.put("recipe_name",recipeNameInput.getValue());
        jsonPacket.put("crud","create");
        Networking.INSTANCE.sendToServer(new CRUDRecipe(jsonPacket.toJSONString()));

        jsonPacket.put("operate","open_crafting_screen");
        Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
    }

    public void tick() {
        super.tick();
        Slot[] slots = container.craftTableSlots;
        isResultSlotEmpty = slots[0].getItem().isEmpty();//判断合成槽是否为空
        isCraftingSlotEmpty = true;//判断
        for (int i = 1; i <= 9; i++) {
            if(!slots[i].getItem().isEmpty())isCraftingSlotEmpty = false;
        }
        this.confirmBtn.active = !isResultSlotEmpty && !isCraftingSlotEmpty && !isRecipeNameEmpty && !isGroupNameEmpty;
    }
    @Override
    protected void renderBg(PoseStack matrixStack,float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack,partialTicks,mouseX,mouseY);
        this.minecraft.getTextureManager().bind(this.SCREEN_TEXTURE);
        if (isToggleCraftingBtnHovered) blit(matrixStack,leftPos+88, topPos+28, 28, 198,28, 28, textureWidth, textureHeight);
        if (currentType == ToggleCraftingTypeButton.Type.crafting_shaped) blit(matrixStack,leftPos+88, topPos+28, 0, 226,28, 28, textureWidth, textureHeight);
        if (currentType == ToggleCraftingTypeButton.Type.crafting_shapeless) blit(matrixStack,leftPos+88, topPos+28, 0, 198,28, 28, textureWidth, textureHeight);
    }
    @Override
    protected void renderLabels(PoseStack matrixStack,int mouseX, int mouseY) {
        super.renderLabels(matrixStack,mouseX,mouseY);
        this.font.draw(matrixStack,currentType.getTitle(), 28.0F, 6.0F, 4210752);
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
        jsonPacket.put("operate","open_crafting_screen");
        Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
    }

    public void renderFakeRecipe(){
        if (currentRecipe==null) return;
        String type = currentRecipe.getString("type");
        if (type.equals("minecraft:crafting_shaped")){
            JSONObject key = currentRecipe.getJSONObject("key");
            ItemStack[] fakeItemStacks = new ItemStack[10];
            for (int i = 1; i <= 9; i++) {
                String itemRegistryName = (key.getJSONObject(i + "") == null) ? "minecraft:air" : key.getJSONObject(i + "").getString("item");
                fakeItemStacks[i] = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemRegistryName)));
            }
            Integer resultItemCount = 1;
            if (currentRecipe.getJSONObject("result")!=null){
                JSONObject result = currentRecipe.getJSONObject("result");
                if (result.getString("item")!=null){
                    String resultItemRegisryName = result.getString("item");
                    if (result.getInteger("count")!=null){
                        resultItemCount = result.getInteger("count");
                    }
                    fakeItemStacks[0] = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(resultItemRegisryName)),resultItemCount);
                }
            }
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[0],leftPos+124,topPos+35);
            this.minecraft.getItemRenderer().renderGuiItemDecorations(this.font,fakeItemStacks[0],leftPos+124,topPos+35,resultItemCount.toString());

            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[1],leftPos+30 + 0 * 18,topPos+17 + 0 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[2],leftPos+30 + 1 * 18,topPos+17 + 0 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[3],leftPos+30 + 2 * 18,topPos+17 + 0 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[4],leftPos+30 + 0 * 18,topPos+17 + 1 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[5],leftPos+30 + 1 * 18,topPos+17 + 1 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[6],leftPos+30 + 2 * 18,topPos+17 + 1 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[7],leftPos+30 + 0 * 18,topPos+17 + 2 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[8],leftPos+30 + 1 * 18,topPos+17 + 2 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[9],leftPos+30 + 2 * 18,topPos+17 + 2 * 18);

//                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:clay_ball"));
//            this.minecraft.getItemRenderer().renderAndDecorateItem(,leftPos+56,topPos+54);
        }
        if (type.equals("minecraft:crafting_shapeless")){
            JSONArray ingredients = currentRecipe.getJSONArray("ingredients");
            ItemStack[] fakeItemStacks = new ItemStack[10];

            for (int i = 1; i <= 9; i++) {
                if (i<=ingredients.size()){
                    String itemRegistryName = ingredients.getJSONObject(i-1).getString("item");
                    fakeItemStacks[i] = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemRegistryName)));
                }else {
                    fakeItemStacks[i] = new ItemStack(Items.AIR);
                }
            }
            if (currentRecipe.getJSONObject("result")!=null){
                JSONObject result = currentRecipe.getJSONObject("result");
                if (result.getString("item")!=null){
                    String resultItemRegisryName = result.getString("item");
                    Integer resultItemCount = 0;
                    if (result.getInteger("count")!=null){
                        resultItemCount = result.getInteger("count");
                    }
                    fakeItemStacks[0] = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(resultItemRegisryName)),resultItemCount);
                }
            }
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[0],leftPos+124,topPos+35);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[1],leftPos+30 + 0 * 18,topPos+17 + 0 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[2],leftPos+30 + 1 * 18,topPos+17 + 0 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[3],leftPos+30 + 2 * 18,topPos+17 + 0 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[4],leftPos+30 + 0 * 18,topPos+17 + 1 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[5],leftPos+30 + 1 * 18,topPos+17 + 1 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[6],leftPos+30 + 2 * 18,topPos+17 + 1 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[7],leftPos+30 + 0 * 18,topPos+17 + 2 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[8],leftPos+30 + 1 * 18,topPos+17 + 2 * 18);
            this.minecraft.getItemRenderer().renderAndDecorateItem(fakeItemStacks[9],leftPos+30 + 2 * 18,topPos+17 + 2 * 18);
        }
    }
}
