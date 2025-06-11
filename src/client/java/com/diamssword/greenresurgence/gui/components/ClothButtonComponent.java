package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.render.cosmetics.ClothingModel;
import com.diamssword.greenresurgence.systems.clothing.ClothingLoader;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.mixin.ui.access.ClickableWidgetAccessor;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import io.wispforest.owo.util.pond.OwoEntityRenderDispatcherExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;

public class ClothButtonComponent extends ButtonComponent {
    private ClothingLoader.Cloth cloth;
    private final ClothingModel<AbstractClientPlayerEntity> model = new ClothingModel<>(false, 0, false);
    private final ClothingModel<AbstractClientPlayerEntity> model1 = new ClothingModel<>(false, 1, true);
    protected final EntityRenderDispatcher dispatcher;
    protected final VertexConsumerProvider.Immediate entityBuffers;
    private boolean hoveredSent = false;
    private boolean selected = false;
    private final EventStream<ClothInventoryComponent.ClothPicked> onHovered = ClothInventoryComponent.ClothPicked.newPickStream();

    public ClothButtonComponent(ClothingLoader.Cloth cloth) {
        super(Text.literal(""), a -> {
        });
        setCloth(cloth);
        final var client = MinecraftClient.getInstance();
        this.dispatcher = client.getEntityRenderDispatcher();
        this.entityBuffers = client.getBufferBuilders().getEntityVertexConsumers();
    }

    public void setCloth(ClothingLoader.Cloth cloth) {
        this.cloth = cloth;
        this.tooltip(Text.literal((cloth.name())));
    }

    public ClothingLoader.Cloth getCloth() {
        return cloth;
    }

    public boolean selected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public EventSource<ClothInventoryComponent.ClothPicked> onClothHovered() {
        return this.onHovered.source();
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {

        if (selected)
            context.setShaderColor(0.8f, 1f, 0.8f, 1);
        Panels.drawOverlay(context, this.x(), this.y(), this.width, this.height);
        context.setShaderColor(1, 1, 1, 1);
        drawClothing(context, mouseX);
        this.hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        if (this.hovered) {
            if (!this.hoveredSent)
                this.onHovered.sink().onPicked(cloth);
            this.hoveredSent = true;
            Tooltip tooltip = ((ClickableWidgetAccessor) this).owo$getTooltip();
            if (tooltip != null) {
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                context.drawTooltip(textRenderer, tooltip.getLines(MinecraftClient.getInstance()), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);
            }
        } else if (this.hoveredSent) {
            this.onHovered.sink().onPicked(null);
            this.hoveredSent = false;
        }

    }

    public void drawClothing(OwoUIDrawContext context, int mouseX) {
        var matrices = context.getMatrices();
        matrices.push();
        var bigger = cloth.layer() == ClothingLoader.Layer.shoes || cloth.layer() == ClothingLoader.Layer.hat || cloth.layer() == ClothingLoader.Layer.glasses || cloth.layer() == ClothingLoader.Layer.accessories;
        float scale = bigger ? (cloth.layer() == ClothingLoader.Layer.shoes ? 40 : 30) : 20;
        matrices.translate(this.x() + (this.width / 2f), this.y() + (this.height / 2f) - 8, 100);
        if (bigger) {
            if (cloth.layer() == ClothingLoader.Layer.shoes) {
                matrices.translate(0, -(this.height * 0.8f), 0);
            } else {
                matrices.translate(0, this.height / 3f, 0);
            }
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-15f));
        }
        matrices.scale(scale, scale, scale);
        // matrices.translate(1, 0, 0);
        float yRotation = (float) Math.toDegrees(Math.atan((mouseX - this.getX() - this.width / 2f) / 40f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 + (yRotation * .6f)));

        //   matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(10));
        var dispatcher = (OwoEntityRenderDispatcherExtension) this.dispatcher;
        dispatcher.owo$setCounterRotate(true);
        dispatcher.owo$setShowNametag(false);
        RenderSystem.setShaderLights(new Vector3f(.15f, 1, 0), new Vector3f(.15f, -1, 0));
        this.dispatcher.setRenderShadows(false);
        renderLayer(matrices, this.entityBuffers, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        this.dispatcher.setRenderShadows(true);
        this.entityBuffers.draw();
        DiffuseLighting.enableGuiDepthLighting();

        matrices.pop();

        dispatcher.owo$setCounterRotate(false);
        dispatcher.owo$setShowNametag(true);
    }

    public void renderLayer(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        model.child = false;
        model1.child = false;
        var pack = OverlayTexture.packUv(OverlayTexture.getU(0), OverlayTexture.getV(false));
        model.render(matrices, vertexConsumers.getBuffer(model.getLayer(GreenResurgence.asRessource("textures/cloth/" + cloth.layer() + "/" + cloth.id() + ".png"))), light, pack, 1, 1, 1, 1);
        model1.render(matrices, vertexConsumers.getBuffer(model1.getLayer(GreenResurgence.asRessource("textures/cloth/" + cloth.layer() + "/" + cloth.id() + ".png"))), light, pack, 1, 1, 1, 1);

    }
}
