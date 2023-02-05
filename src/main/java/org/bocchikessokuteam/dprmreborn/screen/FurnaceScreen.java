package org.bocchikessokuteam.dprmreborn.screen;

import com.alibaba.fastjson.JSONObject;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.bocchikessokuteam.dprmreborn.DPRMReborn;
import org.bocchikessokuteam.dprmreborn.container.FurnaceContainer;
import org.bocchikessokuteam.dprmreborn.file.JsonManager;
import org.bocchikessokuteam.dprmreborn.network.ScreenToggle;
import org.bocchikessokuteam.dprmreborn.network.Networking;
import org.bocchikessokuteam.dprmreborn.network.CRUDRecipe;
import org.bocchikessokuteam.dprmreborn.widget.ToggleFurnaceTypeButton;
import org.lwjgl.glfw.GLFW;

import static net.minecraft.client.gui.GuiComponent.blit;

public class FurnaceScreen extends AbstractRecipeMakerScreen<FurnaceContainer>{
    Boolean isResultSlotEmpty = true;
    Boolean isCraftingSlotEmpty = true;
    Boolean isExperienceInputValid = false;
    Boolean isCookingTimeInputValid = false;

    EditBox experienceInput;
    EditBox cookingTimeInput;
    ToggleFurnaceTypeButton.Type currentType = ToggleFurnaceTypeButton.Type.smelting;
    ToggleFurnaceTypeButton toggleFurnaceBtn;

    public FurnaceScreen(FurnaceContainer furnaceContainer, Inventory inv, Component titleIn) {
        super(furnaceContainer, inv, titleIn);
        this.SCREEN_TEXTURE = new ResourceLocation(DPRMReborn.MODID, "textures/gui/furnance.png");
    }
    @Override
    protected void init() {
        //侧边栏组件初始化
        super.init();
        //经验
        this.experienceInput = new EditBox(this.font, leftPos - 82, topPos + 47, 24, 12,new TranslatableComponent("gui."+ DPRMReborn.MODID+".furnance.experience"));
        this.experienceInput.setCanLoseFocus(true);
        this.experienceInput.setTextColor(-1);
        this.experienceInput.setTextColorUneditable(0x808080);
        this.experienceInput.setBordered(false);
        this.experienceInput.setMaxLength(10);
        this.experienceInput.setResponder(this::experienceInputOnWrite);
        experienceInput.setValue("0.35");
        this.children.add(this.experienceInput);
        //时间
        this.cookingTimeInput = new EditBox(this.font, leftPos - 82, topPos + 63, 24, 12,new TranslatableComponent("gui."+ DPRMReborn.MODID+".furnance.cooking_time"));
        this.cookingTimeInput.setCanLoseFocus(true);
        this.cookingTimeInput.setTextColor(-1);
        this.cookingTimeInput.setTextColorUneditable(0x808080);
        this.cookingTimeInput.setBordered(false);
        this.cookingTimeInput.setMaxLength(8);
        this.cookingTimeInput.setResponder(this::cookingTimeInputOnWrite);
        cookingTimeInput.setValue("200");
        this.children.add(this.cookingTimeInput);

        this.toggleFurnaceBtn = new ToggleFurnaceTypeButton(leftPos+56,topPos+54,20,20,this::changeFurnaceType, currentType);
        this.children.add(toggleFurnaceBtn);
    }

    private void changeFurnaceType(Button button) {
        ((ToggleFurnaceTypeButton) button).toggle();
        int ordinal = currentType.ordinal();
        ordinal++;
        if (ordinal > 3) ordinal = 0;
        currentType = ToggleFurnaceTypeButton.Type.values()[ordinal];
    }

    private void cookingTimeInputOnWrite(String s) {
        isCookingTimeInputValid = Ints.tryParse(s) != null;
        if (isCookingTimeInputValid) {
            cookingTimeInput.setTextColor(-1);
        } else {
            cookingTimeInput.setTextColor(0xff0000);
        }
    }

    private void experienceInputOnWrite(String s) {
        isExperienceInputValid = Doubles.tryParse(s) != null;
        if (isExperienceInputValid) {
            experienceInput.setTextColor(-1);
        } else {
            experienceInput.setTextColor(0xff0000);
        }
    }

    public void tick() {
        //侧边栏isRecipeJsonExist,isRecipeNameEmpty,isGroupNameEmpty判断
        super.tick();
        //Container插槽判断
        Slot[] slots = container.furnaceSlots;
        isResultSlotEmpty = slots[0].getItem().isEmpty();//判断合成槽是否为空
        isCraftingSlotEmpty = true;//判断
        for (int i = 1; i <= 1; i++) {
            if(!slots[i].getItem().isEmpty())isCraftingSlotEmpty = false;
        }
        //按钮是否激活
        this.confirmBtn.active = !isResultSlotEmpty && !isCraftingSlotEmpty && !isRecipeNameEmpty && !isGroupNameEmpty &&isCookingTimeInputValid&&isExperienceInputValid;
    }

    Boolean isToggleBtnHovered = false;
    @Override
    protected void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack,mouseX, mouseY);
        //判断是否悬停在切换按钮
        if ((mouseX>=leftPos+55)&&(mouseX<=leftPos+55+18)&&(mouseY>=topPos+53)&&(mouseY<=topPos+53+18)){
            this.renderTooltip(matrixStack,new TranslatableComponent("gui."+ DPRMReborn.MODID+".toggle_furnace_type"),mouseX,mouseY);
            isToggleBtnHovered = true;
        }else isToggleBtnHovered =false;
    }

    @Override
    protected void renderBg(PoseStack matrixStack,float partialTicks, int mouseX, int mouseY) {
        //绘制Container背景,侧边栏组件
        super.renderBg(matrixStack,partialTicks,mouseX,mouseY);
        this.minecraft.getTextureManager().bind(this.SCREEN_TEXTURE);
        //烧制时间、经验背景框
        blit(matrixStack, leftPos - 85,  topPos + 43, 0, 198, 24, 15, textureWidth, textureHeight);
        blit(matrixStack, leftPos - 85,  topPos + 59, 0, 198, 24, 15, textureWidth, textureHeight);
        //经验,时间文字
        if (isToggleBtnHovered)blit(matrixStack,leftPos+54,topPos+52,176,0,20,20,textureWidth,textureHeight);
        this.font.draw(matrixStack,new TranslatableComponent("gui."+ DPRMReborn.MODID+".furnance.experience").getString(), leftPos-59, topPos+46, 0xFF222222);
        this.font.draw(matrixStack,new TranslatableComponent("gui."+ DPRMReborn.MODID+".furnance.cooking_time").getString(), leftPos-59, topPos+62,0xFF222222);
        //图标
        this.minecraft.getItemRenderer().renderAndDecorateItem(currentType.getIcon(),leftPos+56,topPos+54);
    }
    protected void renderLabels(PoseStack matrixStack,int mouseX, int mouseY) {
        //绘制背包名称
        super.renderLabels(matrixStack,mouseX,mouseY);
        //标题文字
        String s = currentType.getTitle();
        this.font.draw(matrixStack,s, (float)(this.imageWidth / 2 - this.font.width(s) / 2), 6.0F, 4210752);
    }
    @Override
    public void render(PoseStack matrixStack,int mouseX, int mouseY, float particleTick) {
        super.render(matrixStack,mouseX, mouseY, particleTick);
        this.experienceInput.render(matrixStack,mouseX,mouseY,particleTick);
        this.cookingTimeInput.render(matrixStack,mouseX,mouseY,particleTick);
        if (isRecipeJsonExist){
            renderFakeRecipe();
            if (recipeNameInput!=null){
                if (recipeNameInput.isFocused()){
                    if (currentRecipe.getString("type").equals("smelting")) currentType = ToggleFurnaceTypeButton.Type.smelting;
                    if (currentRecipe.getString("type").equals("blasting")) currentType = ToggleFurnaceTypeButton.Type.blasting;
                    if (currentRecipe.getString("type").equals("smoking")) currentType = ToggleFurnaceTypeButton.Type.smoking;
                    if (currentRecipe.getString("type").equals("campfire_cooking")) currentType = ToggleFurnaceTypeButton.Type.campfire_cooking;
                }
            }
        }

    }

    private void renderFakeRecipe() {
        if (currentRecipe==null) return;
        String type = currentRecipe.getString("type");
        if (type.equals("smelting")||type.equals("blasting")||type.equals("smoking")||type.equals("campfire_cooking")){
            String ingredientItemRegistryName = currentRecipe.getJSONObject("ingredient").getString("item");
            String resultRegistryName = currentRecipe.getString("result");
            ItemStack ingredientItemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ingredientItemRegistryName)));
            ItemStack resultItemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(resultRegistryName)));
            this.minecraft.getItemRenderer().renderAndDecorateItem(ingredientItemStack,leftPos+56,topPos+17);
            this.minecraft.getItemRenderer().renderAndDecorateItem(resultItemStack,leftPos+116,topPos+35);
        }
    }

    @Override
    public void onConfirmBtnPress(Button button) {
        String furnaceType = currentType.name();
        JSONObject blastingRecipe = JsonManager.genFurnaceRecipe(container.furnaceSlots, groupNameInput.getValue(),Doubles.tryParse(experienceInput.getValue()),Ints.tryParse(cookingTimeInput.getValue()),furnaceType);
        jsonPacket.put("json_recipe",blastingRecipe);
        jsonPacket.put("recipe_name",recipeNameInput.getValue());
        jsonPacket.put("crud","create");
        Networking.INSTANCE.sendToServer(new CRUDRecipe(jsonPacket.toJSONString()));

        jsonPacket.put("operate","open_furnace_screen");
        Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
//        this.minecraft.player.closeContainer();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
        }
        return this.recipeNameInput.keyPressed(keyCode, scanCode, modifier) || this.recipeNameInput.canConsumeInput()
                || this.groupNameInput.keyPressed(keyCode, scanCode, modifier) || this.groupNameInput.canConsumeInput()
                || this.experienceInput.keyPressed(keyCode, scanCode, modifier) || this.experienceInput.canConsumeInput()
                || this.cookingTimeInput.keyPressed(keyCode, scanCode, modifier) || this.cookingTimeInput.canConsumeInput()
                || super.keyPressed(keyCode, scanCode, modifier);
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
        jsonPacket.put("operate","open_furnace_screen");
        Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
    }
}
