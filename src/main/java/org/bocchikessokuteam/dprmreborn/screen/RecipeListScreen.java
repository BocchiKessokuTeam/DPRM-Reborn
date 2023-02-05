package org.bocchikessokuteam.dprmreborn.screen;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.bocchikessokuteam.dprmreborn.DPRMReborn;
import org.bocchikessokuteam.dprmreborn.container.RecipeListContainer;
import org.bocchikessokuteam.dprmreborn.file.JsonManager;
import org.bocchikessokuteam.dprmreborn.network.Networking;
import org.bocchikessokuteam.dprmreborn.network.ScreenToggle;
import org.bocchikessokuteam.dprmreborn.widget.DPRMRecipeWidget;

import java.util.ArrayList;

public class RecipeListScreen extends AbstractContainerScreen<RecipeListContainer> {
    protected final RecipeListContainer container;
    protected final JSONObject jsonPacket;
    protected ResourceLocation SCREEN_TEXTURE = new ResourceLocation(DPRMReborn.MODID, "textures/gui/recipe_list.png");
    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation(DPRMReborn.MODID,"textures/gui/buttons.png");
    protected ImageButton addCraftingShapedBtn;
    protected ImageButton addFurnanceBtn;
    protected ImageButton addStoneCuttingBtn;
    protected ImageButton addSmithingBtn;
    protected ImageButton forwardPageBtn;
    protected ImageButton backPageBtn;
    Integer total_pages;
    Integer current_page;
    JSONArray current_page_recipe_list;
    JSONArray recipe_list;

    protected DPRMRecipeWidget[] dprmRecipeWidgets= new DPRMRecipeWidget[20];


    protected final Integer textureWidth = 300;
    protected final Integer textureHeight = 300;
    public RecipeListScreen instance = this;
//    protected final RecipeButtonPageScreen recipeButtonPageScreen;

    public RecipeListScreen(RecipeListContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.topPos = (this.height - this.imageHeight) / 2;
        this.container = container;
        this.jsonPacket = JSON.parseObject(container.getFriendlyByteBuf().readUtf());
        DPRMReborn.LOGGER.info("Send From Server:"+this.jsonPacket);
        this.imageWidth = 148;
        this.imageHeight = 167;
        this.leftPos = (this.width - this.imageWidth) / 2;

        this.current_page = jsonPacket.getInteger("current_page");
        this.total_pages = jsonPacket.getJSONArray("recipe_list").size()/20+1;
        this.current_page_recipe_list = new JSONArray();
        this.recipe_list = jsonPacket.getJSONArray("recipe_list");
        for (int i = (current_page-1)*20; i <= (current_page-1)*20 + 19; i++) {
            try {
                current_page_recipe_list.add(recipe_list.get(i));
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        
    }

    @Override
    protected void init() {
        super.init();
        addCraftingShapedBtn = new ImageButton(leftPos + 16 -5, topPos + 10, 26, 16, 0, 0, 17, BUTTON_TEXTURE, this::onAddCraftingShapedBtnPressed);
        addFurnanceBtn = new ImageButton(leftPos + 44 -5, topPos + 10, 26, 16, 26, 0, 17, BUTTON_TEXTURE, this::onAddFurnanceBtnPressed);
        addStoneCuttingBtn = new ImageButton(leftPos + 72 -5,topPos + 10, 26, 16, 52, 0, 17, BUTTON_TEXTURE,this::onAddStonecuttingBtnPressed);
        addSmithingBtn = new ImageButton(leftPos + 100 -5,topPos + 10, 26, 16, 0, 32, 17, BUTTON_TEXTURE,this::onAddSmithingBtnPressed);
        forwardPageBtn = new ImageButton(leftPos + 94,topPos + 134,10,18,78,0,18, BUTTON_TEXTURE,this::forwardPage);
        backPageBtn = new ImageButton(leftPos+ 43,topPos + 134,10,18,92,0,18, BUTTON_TEXTURE,this::backwardPage);

        this.addButton(addCraftingShapedBtn);
        this.addButton(addStoneCuttingBtn);
        this.addButton(addFurnanceBtn);
        this.addButton(addSmithingBtn);
        if (current_page<total_pages) this.addButton(forwardPageBtn);
        if (current_page>1) this.addButton(backPageBtn);


        for (int i = 0; i < current_page_recipe_list.size(); i++) {
            dprmRecipeWidgets[i] = new DPRMRecipeWidget(leftPos,topPos,i,current_page_recipe_list.getJSONObject(i),jsonPacket);
            this.addButton(dprmRecipeWidgets[i]);
        }

    }

    private void backwardPage(Button button) {
        jsonPacket.put("current_page",jsonPacket.getInteger("current_page")-1);
        jsonPacket.put("operate","open_recipe_list_screen");
        Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
    }

    private void forwardPage(Button button) {
        jsonPacket.put("current_page",jsonPacket.getInteger("current_page")+1);
        jsonPacket.put("operate","open_recipe_list_screen");
        Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
    }


    @Override
    protected void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack,mouseX, mouseY);

    }

    @Override
    public void render(PoseStack matrixStack,int mouseX, int mouseY, float particleTick) {
        super.render(matrixStack,mouseX, mouseY, particleTick);
        this.addCraftingShapedBtn.render(matrixStack,mouseX, mouseY, particleTick);
        this.addFurnanceBtn.render(matrixStack,mouseX,mouseY,particleTick);
        this.addStoneCuttingBtn.render(matrixStack,mouseX,mouseY,particleTick);
        this.addSmithingBtn.render(matrixStack,mouseX,mouseY,particleTick);
        if (current_page<total_pages)this.forwardPageBtn.render(matrixStack,mouseX,mouseY,particleTick);
        if (current_page>1)this.backPageBtn.render(matrixStack,mouseX,mouseY,particleTick);
        if (addCraftingShapedBtn.isHovered()){
            this.renderTooltip(matrixStack,new TranslatableComponent("gui."+ DPRMReborn.MODID+".add_crafting_recipe"),mouseX,mouseY);
        }
        if (addFurnanceBtn.isHovered()){
            this.renderTooltip(matrixStack,new TranslatableComponent("gui."+ DPRMReborn.MODID+".add_furnace_recipe"),mouseX,mouseY);
        }
        if (addStoneCuttingBtn.isHovered()){
            this.renderTooltip(matrixStack,new TranslatableComponent("gui."+ DPRMReborn.MODID+".add_stonecutting_recipe"),mouseX,mouseY);
        }
        if (addSmithingBtn.isHovered()){
            this.renderTooltip(matrixStack,new TranslatableComponent("gui."+ DPRMReborn.MODID+".add_smithing_recipe"),mouseX,mouseY);
        }
        renderDprmRecipeWidgetTooltip(matrixStack,mouseX,mouseY);
    }

    public void renderDprmRecipeWidgetTooltip(PoseStack matrixStack,int mouseX,int mouseY){
        for (int i = 0; i < current_page_recipe_list.size(); i++) {
            if (dprmRecipeWidgets[i].isHovered()){
                JSONObject recipe = current_page_recipe_list.getJSONObject(i);
                String recipe_name = recipe.getString("recipe_name");
                String type = recipe.getJSONObject("content").getString("type");
                String itemRegistryName = JsonManager.getResultName(recipe.getJSONObject("content"));

                ArrayList<Component> tooltips = new ArrayList<>();
                tooltips.add(JsonManager.translateRegisryName(itemRegistryName));
                tooltips.add(new TranslatableComponent("gui."+ DPRMReborn.MODID+".tooltips.recipe_name",recipe_name));
                tooltips.add(new TranslatableComponent("gui."+ DPRMReborn.MODID+".tooltips.recipe_type",JsonManager.translateRecipeType(type)));

//                this.renderTooltip(matrixStack,JsonManager.translateRegisryName(itemRegistryName),mouseX,mouseY);
                this.renderWrappedToolTip(matrixStack,tooltips,mouseX,mouseY,this.font);
//                this.renderTooltip(matrixStack,new TranslatableComponent("gui."+DPRMReborn.MODID+".tooltips.recipe_name",recipe_name),mouseX,mouseY);
//                this.renderTooltip(matrixStack,new TranslatableComponent("gui."+DPRMReborn.MODID+".tooltips.recipe_type",JsonManager.translateRecipeType(type)),mouseX,mouseY);
            }
        }
    }

    @Override
    protected void renderBg(PoseStack matrixStack,float partialTicks, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bind(SCREEN_TEXTURE);
        blit(matrixStack,leftPos, topPos, 0, 0,148, 167, textureWidth, textureHeight);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack,int mouseX, int mouseY) {
//        super.renderLabels(matrixStack,mouseX, mouseY);
        String page = current_page+"/"+total_pages;
        this.font.draw(matrixStack,page, (float)(this.imageWidth / 2 - this.font.width(page) / 2), 139.0F, 0XFFFFFF);
    }

    private void onAddCraftingShapedBtnPressed(Button button) {
        jsonPacket.put("select_recipe_name","");
        jsonPacket.put("operate","open_crafting_screen");
        Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
    }
    private void onAddFurnanceBtnPressed(Button button) {
        jsonPacket.put("select_recipe_name","");
        jsonPacket.put("operate","open_furnace_screen");
        Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
//        this.minecraft.player.closeContainer();
    }
    private void onAddStonecuttingBtnPressed(Button button) {
        jsonPacket.put("select_recipe_name","");
        jsonPacket.put("operate","open_stonecutting_screen");
        Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
    }
    private void onAddSmithingBtnPressed(Button button) {
        jsonPacket.put("select_recipe_name","");
        jsonPacket.put("operate","open_smithing_screen");
        Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
    }
}
