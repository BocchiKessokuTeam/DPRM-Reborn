package org.bocchikessokuteam.dprmreborn.screen;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import org.bocchikessokuteam.dprmreborn.DPRMReborn;
import org.bocchikessokuteam.dprmreborn.container.AbstractRecipeContainer;
import org.bocchikessokuteam.dprmreborn.network.Networking;
import org.bocchikessokuteam.dprmreborn.network.ScreenToggle;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class AbstractRecipeMakerScreen<T extends AbstractRecipeContainer> extends AbstractContainerScreen<T> implements ContainerListener {
    protected final T container;
    protected final JSONObject jsonPacket;
    protected ResourceLocation SCREEN_TEXTURE = new ResourceLocation(DPRMReborn.MODID, "textures/gui/empty_recipe_screen.png");
    protected final ResourceLocation BACK_TO_RECIPE_LIST_BTN = new ResourceLocation(DPRMReborn.MODID,"textures/gui/back_to_recipe_list_btn.png");
    protected final ResourceLocation DELETE_BTN = new ResourceLocation(DPRMReborn.MODID,"textures/gui/delete_btn.png");
    protected final Integer textureWidth = 300;
    protected final Integer textureHeight = 300;
    public JSONObject currentRecipe = new JSONObject();

    //组件
    EditBox recipeNameInput;//配方名输入组件
    EditBox groupNameInput;//组名输入组件
    Button confirmBtn;//按钮
    protected ImageButton backBtn;
    protected ImageButton deleteBtn;

    //提交按钮判断条件
    Boolean isRecipeNameEmpty = true;
    Boolean isGroupNameEmpty = true;
    Boolean isRecipeJsonExist = false;

    public AbstractRecipeMakerScreen(T container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.container = container;
        this.jsonPacket = JSON.parseObject(container.getPacketBuffier().readUtf());
        DPRMReborn.LOGGER.info("Send From Server:"+this.jsonPacket);
    }

    @Override
    protected void init() {
        super.init();
        //配方名输入框
        this.recipeNameInput = new EditBox(this.font, leftPos - 82, topPos + 15, 80, 12, new TranslatableComponent("gui."+ DPRMReborn.MODID+".recipe_info.recipe_name"));//字体，位置，宽高，信息
        this.recipeNameInput.setTextColor(-1);
        this.recipeNameInput.setTextColorUneditable(-1);
        this.recipeNameInput.setBordered(false);
        this.recipeNameInput.setMaxLength(35);//最大输入长度
        this.recipeNameInput.setCanLoseFocus(true);
        this.recipeNameInput.setFocus(true);
        this.recipeNameInput.setValue(jsonPacket.getString("select_recipe_name"));
        this.recipeNameInput.setResponder(this::recipeNameinputResponder);//每次输入后的回调函数
        //组名输入框
        this.groupNameInput = new EditBox(this.font, leftPos - 82, topPos + 31, 80, 12, new TranslatableComponent("gui."+ DPRMReborn.MODID+".recipe_info.group_name"));//字体，位置，宽高，信息
        this.groupNameInput.setTextColor(-1);
        this.groupNameInput.setTextColorUneditable(-1);
        this.groupNameInput.setBordered(false);
        this.groupNameInput.setMaxLength(35);//最大输入长度
        this.groupNameInput.setCanLoseFocus(true);
        this.groupNameInput.setResponder(this::groupNameinputResponder);//每次输入后的回调函数
        //监听器
        this.container.addSlotListener(this);
        //按钮初始化
        this.confirmBtn = new Button(this.leftPos - 85, this.topPos + 85, 80, 20,new TranslatableComponent("gui."+ DPRMReborn.MODID+".recipe_info.add_recipe"), this::onConfirmBtnPress);//位置，宽高，文字，按下后回调函数
        this.backBtn = new ImageButton(leftPos + 145, topPos + 5, 26, 16, 0, 0, 16, BACK_TO_RECIPE_LIST_BTN, this::backToRecipeList);
        this.deleteBtn = new ImageButton(leftPos + 5, topPos + 5, 16, 16, 0, 0, 16, DELETE_BTN, this::deleteRecipe);


        this.children.add(this.recipeNameInput);
        this.children.add(this.groupNameInput);
        this.addButton(confirmBtn);
        this.addButton(backBtn);
        this.addButton(deleteBtn);
    }

    public void deleteRecipe(Button button) {

    }

    private void backToRecipeList(Button button) {
        jsonPacket.put("operate","open_recipe_list_screen");
        Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
    }

    public void tick() {
        super.tick();
        isRecipeJsonExist = false;
        JSONArray recipe_list = jsonPacket.getJSONArray("recipe_list");
        for (int i = 0; i < recipe_list.size(); i++) {
            JSONObject recipe = recipe_list.getJSONObject(i);
            String recipe_name = recipe.getString("recipe_name");
            if (recipe_name.equals(recipeNameInput.getValue())){
                isRecipeJsonExist = true;
                currentRecipe = recipe.getJSONObject("content");
            }
        }
        if (isRecipeJsonExist) {
            this.confirmBtn.setMessage(new TranslatableComponent("gui."+ DPRMReborn.MODID+".recipe_info.update_recipe"));

        } else {
            this.confirmBtn.setMessage(new TranslatableComponent("gui."+ DPRMReborn.MODID+".recipe_info.add_recipe"));
        }
        deleteBtn.visible = isRecipeJsonExist;
        deleteBtn.active = isRecipeJsonExist;
    }
    private void recipeNameinputResponder(String inputText) {
        isRecipeNameEmpty = recipeNameInput.getValue().isEmpty();
    }
    private void groupNameinputResponder(String inputText) {
        isGroupNameEmpty = groupNameInput.getValue().isEmpty();
    }

    public void removed() {
//        super.removed();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        this.container.removeSlotListener(this);
    }
    //调整游戏界面大小时重新初始化界面
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        String s = this.recipeNameInput.getValue();
        String g = this.groupNameInput.getValue();
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
        this.recipeNameInput.setValue(s);
        this.groupNameInput.setValue(g);
    }
    //关闭除Esc以及文字输入之外的其他键盘按键触发事件
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
        }
        return this.recipeNameInput.keyPressed(keyCode, scanCode, modifier) || this.recipeNameInput.canConsumeInput()
            || this.groupNameInput.keyPressed(keyCode, scanCode, modifier) || this.groupNameInput.canConsumeInput()
            || super.keyPressed(keyCode, scanCode, modifier);
    }

    //渲染背景
    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bind(SCREEN_TEXTURE);
        //背景渲染
        blit(matrixStack,leftPos, topPos, 0, 0,176, 166, textureWidth, textureHeight);
        //侧边栏背景渲染
        blit(matrixStack,leftPos - 98, topPos + 4,202, 0, 98, 107, textureWidth, textureHeight);
        //配方名输入框背景渲染
        blit(matrixStack,leftPos - 85, topPos + 11, 0, 166 + (this.recipeNameInput.isFocused() ? 0 : 16), 80, 15, textureWidth, textureHeight);
        //组名输入框背景渲染
        blit(matrixStack,leftPos - 85, topPos + 27, 0, 166 + (this.groupNameInput.isFocused() ? 0 : 16), 80, 15, textureWidth, textureHeight);
        //输入框placeholder
        if (isRecipeNameEmpty)this.font.draw(matrixStack,recipeNameInput.getMessage().getString(),leftPos - 82,topPos + 14, 0xFF777777);
        if (isGroupNameEmpty)this.font.draw(matrixStack,groupNameInput.getMessage().getString(),leftPos - 82,topPos + 30,0xFF777777);
    }
    //渲染GUI内静态文字
    protected void renderLabels(PoseStack matrixStack,int mouseX, int mouseY) {
        this.font.draw(matrixStack,this.inventory.getDisplayName().getString(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
    }

    @Override
    public void render(PoseStack matrixStack,int mouseX, int mouseY, float particleTick) {
        super.render(matrixStack,mouseX, mouseY, particleTick);
        //渲染组件
        this.recipeNameInput.render(matrixStack,mouseX, mouseY, particleTick);
        this.groupNameInput.render(matrixStack,mouseX, mouseY, particleTick);
        this.confirmBtn.render(matrixStack,mouseX,mouseY,particleTick);
        this.backBtn.render(matrixStack,mouseX,mouseY,particleTick);
        this.deleteBtn.render(matrixStack,mouseX,mouseY,particleTick);
        //渲染Tooltips
        this.renderTooltip(matrixStack,mouseX, mouseY);
        if (this.confirmBtn.isHovered()&&!groupNameInput.isFocused()){
            if(this.confirmBtn.active){
                this.renderTooltip(matrixStack,new TranslatableComponent("gui."+ DPRMReborn.MODID+".recipe_info.add_to_datapack"),mouseX,mouseY);
            }else this.renderTooltip(matrixStack,new TranslatableComponent("gui."+ DPRMReborn.MODID+".recipe_info.please_complete_recipe_info"),mouseX,mouseY);
        }
        if (this.recipeNameInput.isHovered()&&!recipeNameInput.isFocused()){
            this.renderTooltip(matrixStack,recipeNameInput.getMessage(),mouseX,mouseY);
        }else if (this.recipeNameInput.isHovered()&&recipeNameInput.isFocused()){
//            if (isRecipeJsonExist){
//                String currentType = currentRecipe.getString("type");
//                this.renderTooltip(getCurrentTypeTooltips(currentType),mouseX,mouseY);
//            }
        }
        if (this.groupNameInput.isHovered()&&!groupNameInput.isFocused()){
            this.renderTooltip(matrixStack,groupNameInput.getMessage(),mouseX,mouseY);
        }

        if (this.backBtn.isHovered()){
            this.renderTooltip(matrixStack,new TranslatableComponent("gui."+ DPRMReborn.MODID+".recipe_info.back_to_recipe_list"),mouseX,mouseY);
        }

        if (this.deleteBtn.isHovered()){
            this.renderTooltip(matrixStack,new TranslatableComponent("gui."+ DPRMReborn.MODID+".recipe_info.delete"),mouseX,mouseY);
        }
    }

    private List<String> getCurrentTypeTooltips(String currentType) {
        List<String> tooltips = new ArrayList<>();
        switch (currentType) {
            case "minecraft:crafting_shaped":
            case "minecraft:crafting_shapeless":
                tooltips.add(I18n.get("gui." + DPRMReborn.MODID + ".this_is_a_crafting_recipe"));
                return tooltips;
            case "smelting":
            case "smoking":
            case "blasting":
            case "campfire_cooking":
                tooltips.add(I18n.get("gui." + DPRMReborn.MODID + ".this_is_a_furnace_recipe"));
                return tooltips;
            case "stonecutting":
                tooltips.add(I18n.get("gui." + DPRMReborn.MODID + ".this_is_a_stonecutting_recipe"));
                return tooltips;
            default:
                tooltips.add("other");
                break;
        }
        return tooltips;
    }


    //确认按钮
    public void onConfirmBtnPress(Button button){

    };

    @Override
    public void refreshContainer(AbstractContainerMenu containerToSend, NonNullList<ItemStack> itemsList) {

    }

    @Override
    public void slotChanged(AbstractContainerMenu containerToSend, int slotInd, ItemStack stack) {

    }

    @Override
    public void setContainerData(AbstractContainerMenu containerIn, int varToUpdate, int newValue) {

    }
}
