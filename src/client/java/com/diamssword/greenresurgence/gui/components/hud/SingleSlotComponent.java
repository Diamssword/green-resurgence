package com.diamssword.greenresurgence.gui.components.hud;

import com.diamssword.greenresurgence.GreenResurgence;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

public class SingleSlotComponent extends BaseComponent implements IHideableComponent{

    private final int textureSize=46;
    private ItemStack stack=ItemStack.EMPTY;
    protected boolean blend = false;
    protected final Identifier texture;

    protected boolean indicatorMode=false;
    protected static final Identifier textureAtt= GreenResurgence.asRessource("textures/gui/hud/attack.png");
    private boolean hidden;

    protected SingleSlotComponent(Identifier texture) {
        super();
        this.texture = texture;
    }

    public SingleSlotComponent blend(boolean blend) {
        this.blend = blend;
        return this;
    }
    @Override
    protected void applySizing() {
        final var horizontalSizing = this.horizontalSizing.get();
        final var verticalSizing = this.verticalSizing.get();
        final var margins = this.margins.get();
        this.height = verticalSizing.inflate(this.space.height() - margins.vertical(), this::determineVerticalContentSize);
        this.width = horizontalSizing.inflate(this.space.width() - margins.horizontal(), this::determineHorizontalContentSize);
    }
    public boolean blend() {
        return this.blend;
    }
    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);

    }
    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return 22;
    }
    @Override
    public void hidden(boolean hidden) {
        this.hidden=hidden;
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }
    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return 22;
    }
    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        if(hidden)
            return;
        RenderSystem.enableDepthTest();

        if (this.blend) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }

        var matrices = context.getMatrices();

        var mc=MinecraftClient.getInstance();
        if(!indicatorMode) {
            matrices.push();
            matrices.translate(x, y, 0);
            context.drawTexture(this.texture, 0, 0, 22, 22, 0, 22, 22, 22, textureSize, textureSize);

            matrices.pop();
            matrices.push();
            matrices.translate(x, y, 0);
            renderHotbarItem(mc, context, 3, 3, delta, mc.player, stack, 42);
            matrices.pop();
        }
        else
        {
            matrices.push();
            matrices.translate(x, y, 0);
            if (mc.options.getAttackIndicator().getValue() == AttackIndicator.HOTBAR) {
                float f = mc.player.getAttackCooldownProgress(0.0F);
                if (f < 1.0F) {
                    int p = (int)(f * 19.0F);
                    context.drawTexture(textureAtt, 2, 2, 18, 18, 0, 0, 18, 18, 36, 18);
                    context.drawTexture(textureAtt, 2, 2+(18-p), 18, p, 18, 18-p, 18, p, 36, 18);

                }
            }
            matrices.pop();
        }
        if (this.blend) {
            RenderSystem.disableBlend();
        }

    }
    public static void renderHotbarItem(MinecraftClient client,OwoUIDrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed) {
        if (!stack.isEmpty()) {
            float g = (float)stack.getBobbingAnimationTime() - f;
            if (g > 0.0F) {
                float h = 1.0F + g / 5.0F;
                context.getMatrices().push();
                context.getMatrices().translate((float)(x + 8), (float)(y + 12), 0.0F);
                context.getMatrices().scale(1.0F / h, (h + 1.0F) / 2.0F, 1.0F);
                context.getMatrices().translate((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
            }

            context.drawItem(player, stack, x, y, seed);
            if (g > 0.0F) {
                context.getMatrices().pop();
            }

            context.drawItemInSlot(client.textRenderer, stack, x, y);
        }
    }
    public ItemStack getStack() {
        return stack;
    }

    public void setStacks(ItemStack stack) {
        this.stack=stack;
    }

    public boolean isIndicatorMode() {
        return indicatorMode;
    }

    public void setIndicatorMode(boolean indicatorMode) {
        this.indicatorMode = indicatorMode;
    }

    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);
        UIParsing.apply(children, "blend", UIParsing::parseBool, this::blend);

    }
    public static SingleSlotComponent parse(Element element) {
        UIParsing.expectAttributes(element, "texture");
        var textureId = UIParsing.parseIdentifier(element.getAttributeNode("texture"));
        return new SingleSlotComponent(new Identifier(textureId.getNamespace(),"textures/gui/"+textureId.getPath()));
    }
}
