package org.bocchikessokuteam.dprmreborn.widget;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.registries.ForgeRegistries;
import org.bocchikessokuteam.dprmreborn.file.JsonManager;
import org.bocchikessokuteam.dprmreborn.network.Networking;
import org.bocchikessokuteam.dprmreborn.network.ScreenToggle;

import java.util.List;

public class DPRMRecipeWidget extends AbstractWidget {
    private static final ResourceLocation RECIPE_BOOK = new ResourceLocation("textures/gui/recipe_book.png");
    private final JSONObject recipe;
    private final JSONObject jsonPacket;
    private ItemStack resultItemStack;
    private Font font = Minecraft.getInstance().font;
    private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

    public DPRMRecipeWidget(int leftPos, int topPos, int recipeIndex, JSONObject recipe, JSONObject jsonPacket) {
        super(leftPos + 11 + 25 * (recipeIndex % 5), topPos + 31 + 25 * (recipeIndex / 5), 24, 24, new TranslatableComponent(""));
        this.recipe = recipe;
        this.jsonPacket = jsonPacket;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partTick) {
        super.renderButton(matrixStack,mouseX, mouseY, partTick);
        Minecraft.getInstance().getTextureManager().bind(RECIPE_BOOK);
        this.blit(matrixStack,this.x, this.y, 29, 206, 25, 25);
        String result_name = JsonManager.getResultName(recipe.getJSONObject("content"));
        Integer result_count = JsonManager.getResultCount(recipe.getJSONObject("content"));
        resultItemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(result_name)),result_count);
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(resultItemStack,this.x+4,this.y+4);
        if (result_count>1){
            Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(this.font,resultItemStack,this.x+4,this.y+4,result_count.toString());
        }
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        String recipe_name = recipe.getString("recipe_name");
        String type = recipe.getJSONObject("content").getString("type");

        if (type.equals("minecraft:crafting_shaped")||type.equals("minecraft:crafting_shapeless")){
            jsonPacket.put("select_recipe_name",recipe_name);
            jsonPacket.put("operate","open_crafting_screen");
            Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
        }
        if (type.equals("smelting")||type.equals("blasting")||type.equals("smoking")||type.equals("campfire_cooking")){
            jsonPacket.put("select_recipe_name",recipe_name);
            jsonPacket.put("operate","open_furnace_screen");
            Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
        }
        if (type.equals("stonecutting")){
            jsonPacket.put("select_recipe_name",recipe_name);
            jsonPacket.put("operate","open_stonecutting_screen");
            Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
        }
        if (type.equals("smithing")){
            jsonPacket.put("select_recipe_name",recipe_name);
            jsonPacket.put("operate","open_smithing_screen");
            Networking.INSTANCE.sendToServer(new ScreenToggle(jsonPacket.toJSONString()));
        }


        super.onClick(p_onClick_1_, p_onClick_3_);
    }

    @Override
    public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderToolTip(matrixStack, mouseX, mouseY);
        renderTooltip( mouseX,  mouseY);
    }


    public void renderTooltip(int mouseX, int mouseY) {
        Font font = resultItemStack.getItem().getFontRenderer(resultItemStack);
        net.minecraftforge.fml.client.gui.GuiUtils.preItemToolTip(resultItemStack);
//        this.renderTooltip(this.getTooltipFromItem(resultItemStack), mouseX, mouseY, (font == null ? this.font : font));
        net.minecraftforge.fml.client.gui.GuiUtils.postItemToolTip();
    }

    public List<String> getTooltipFromItem(ItemStack itemStack) {
        List<Component> list = itemStack.getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
        List<String> list1 = Lists.newArrayList();

        for(Component Component : list) {
            list1.add(Component.getString());
        }

        return list1;
    }

//    public void renderTooltip(List<String> toolTips, int mouseX, int mouseY, FontRenderer font) {
//        net.minecraftforge.fml.client.gui.GuiUtils.drawHoveringText(toolTips, mouseX, mouseY, width, height, -1, font);
//        if (false && !toolTips.isEmpty()) {
//            RenderSystem.disableRescaleNormal();
//            RenderSystem.disableDepthTest();
//            int i = 0;
//
//            for(String s : toolTips) {
//                int j = this.font.width(s);
//                if (j > i) {
//                    i = j;
//                }
//            }
//
//            int l1 = mouseX + 12;
//            int i2 = mouseY - 12;
//            int k = 8;
//            if (toolTips.size() > 1) {
//                k += 2 + (toolTips.size() - 1) * 10;
//            }
//
//            if (l1 + i > this.width) {
//                l1 -= 28 + i;
//            }
//
//            if (i2 + k + 6 > this.height) {
//                i2 = this.height - k - 6;
//            }
//
//            this.setBlitOffset(300);
//            this.itemRenderer.zLevel = 300.0F;
//            int l = -267386864;
//            this.fillGradient(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
//            this.fillGradient(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
//            this.fillGradient(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
//            this.fillGradient(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
//            this.fillGradient(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
//            int i1 = 1347420415;
//            int j1 = 1344798847;
//            this.fillGradient(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
//            this.fillGradient(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
//            this.fillGradient(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
//            this.fillGradient(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);
//            MatrixStack matrixstack = new MatrixStack();
//            IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
//            matrixstack.translate(0.0D, 0.0D, (double)this.itemRenderer.zLevel);
//            Matrix4f matrix4f = matrixstack.getLast().getMatrix();
//
//            for(int k1 = 0; k1 < toolTips.size(); ++k1) {
//                String s1 = toolTips.get(k1);
//                if (s1 != null) {
//                    this.font.renderString(s1, (float)l1, (float)i2, -1, true, matrix4f, irendertypebuffer$impl, false, 0, 15728880);
//                }
//
//                if (k1 == 0) {
//                    i2 += 2;
//                }
//
//                i2 += 10;
//            }
//
//            irendertypebuffer$impl.finish();
//            this.setBlitOffset(0);
//            this.itemRenderer.zLevel = 0.0F;
//            RenderSystem.enableDepthTest();
//            RenderSystem.enableRescaleNormal();
//        }
//    }
}
