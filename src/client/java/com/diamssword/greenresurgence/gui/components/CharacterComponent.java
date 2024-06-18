package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.cosmetics.ClothingModel;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.util.pond.OwoEntityRenderDispatcherExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.*;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class CharacterComponent  extends BaseComponent {

    protected final EntityRenderDispatcher dispatcher;
    protected final VertexConsumerProvider.Immediate entityBuffers;

    protected float mouseRotation = 0;
    protected float scale = 1;
    protected boolean lookAtCursor = false;
    protected boolean allowMouseRotation = false;
    protected boolean scaleToFit = false;
    public OtherClientPlayerEntity entity;
    protected Consumer<MatrixStack> transform = matrixStack -> {};
    private Map<String, Layer> layers=new HashMap<>();
    public float charHeight=1;
    public float charWidth=1;

    public static  class Layer{
        public ClothingModel<AbstractClientPlayerEntity> model;
        public Identifier texture;
        public Layer(int order,boolean secondLayer,Identifier texture)
        {
            this.model=new ClothingModel<>(false,order,secondLayer);
            this.texture=texture;
        }
    }

    protected CharacterComponent(Sizing sizing) {
        final var client = MinecraftClient.getInstance();
        entity=new OtherClientPlayerEntity(client.world,client.player.getGameProfile());
        this.dispatcher = client.getEntityRenderDispatcher();
        this.entityBuffers = client.getBufferBuilders().getEntityVertexConsumers();
        this.sizing(sizing);
    }
    public Optional<Layer> getLayer(String name)
    {
        return Optional.ofNullable(layers.get(name));
    }
    public Layer createLayer(String name,int order,boolean secondLayer,Identifier texture)
    {
        var l=new Layer(order,secondLayer,texture);
        layers.put(name,l);
        return l;
    }
    public void renderLayer(Layer layer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress) {
        PlayerEntityRenderer r= (PlayerEntityRenderer) this.dispatcher.getRenderer(entity);

        r.getModel().copyBipedStateTo(layer.model);
        layer.model.child=false;
        layer.model.animateModel(entity,limbAngle,limbDistance,tickDelta);
        layer.model.setAngles(entity,limbAngle,limbDistance,animationProgress,entity.prevHeadYaw,entity.prevPitch);
        matrices.scale(charWidth,charHeight,charWidth);
        layer.model.render(matrices,vertexConsumers.getBuffer(layer.model.getLayer(layer.texture)),light, LivingEntityRenderer.getOverlay(entity,0),1,1,1,1);


    }
    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        var matrices = context.getMatrices();
        matrices.push();

        matrices.translate(x + this.width / 2f, y + this.height / 2f, 100);
        matrices.scale(75 * this.scale * this.width / 64f, -75 * this.scale * this.height / 64f, 75 * this.scale);

        matrices.translate(0, (entity.getHeight()*this.charHeight) / 4f, 0);

        this.transform.accept(matrices);

        if (this.lookAtCursor) {
            float xRotation = (float) Math.toDegrees(Math.atan((mouseY - this.y - this.height / 2f) / 40f));
            float yRotation = (float) Math.toDegrees(Math.atan((mouseX - this.x - this.width / 2f) / 40f));
            this.entity.prevHeadYaw = -yRotation;
            this.entity.prevYaw = -yRotation;
            this.entity.prevPitch = xRotation * .65f;

            // We make sure the xRotation never becomes 0, as the lighting otherwise becomes very unhappy
            if (xRotation == 0) xRotation = .1f;
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(xRotation * .15f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRotation * .15f));
        } else {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(35));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45 + this.mouseRotation));
        }
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        var dispatcher = (OwoEntityRenderDispatcherExtension) this.dispatcher;
        dispatcher.owo$setCounterRotate(true);
        dispatcher.owo$setShowNametag(false);
        RenderSystem.setShaderLights(new Vector3f(.15f, 1, 0), new Vector3f(.15f, -1, 0));
        this.dispatcher.setRenderShadows(false);
        layers.forEach((k,v)->{
            renderLayer(v,matrices,this.entityBuffers,LightmapTextureManager.MAX_LIGHT_COORDINATE,this.entity,0,0,partialTicks,0);
        });
      //  this.dispatcher.render(this.entity, 0, 0, 0, 0, 0, matrices, this.entityBuffers, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        this.dispatcher.setRenderShadows(true);
        this.entityBuffers.draw();
        DiffuseLighting.enableGuiDepthLighting();

        matrices.pop();

        dispatcher.owo$setCounterRotate(false);
        dispatcher.owo$setShowNametag(true);
    }

    @Override
    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
        if (this.allowMouseRotation && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.mouseRotation += deltaX;

            super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
            return true;
        } else {
            return super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
        }
    }

    public CharacterComponent allowMouseRotation(boolean allowMouseRotation) {
        this.allowMouseRotation = allowMouseRotation;
        return this;
    }

    public boolean allowMouseRotation() {
        return this.allowMouseRotation;
    }

    public CharacterComponent lookAtCursor(boolean lookAtCursor) {
        this.lookAtCursor = lookAtCursor;
        return this;
    }

    public boolean lookAtCursor() {
        return this.lookAtCursor;
    }

    public CharacterComponent scale(float scale) {
        this.scale = scale;
        return this;
    }

    public float scale() {
        return this.scale;
    }

    public CharacterComponent scaleToFit(boolean scaleToFit) {
        this.scaleToFit = scaleToFit;

        if (scaleToFit) {
            float xScale = .5f / entity.getWidth();
            float yScale = .5f / entity.getHeight();

            this.scale(Math.min(xScale, yScale));
        }

        return this;
    }

    public boolean scaleToFit() {
        return this.scaleToFit;
    }

    public CharacterComponent transform(Consumer<MatrixStack> transform) {
        this.transform = transform;
        return this;
    }

    public Consumer<MatrixStack> transform() {
        return transform;
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.MOUSE_CLICK;
    }


    @Override
    public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
        super.parseProperties(model, element, children);

        UIParsing.apply(children, "scale", UIParsing::parseFloat, this::scale);
        UIParsing.apply(children, "look-at-cursor", UIParsing::parseBool, this::lookAtCursor);
        UIParsing.apply(children, "mouse-rotation", UIParsing::parseBool, this::allowMouseRotation);
        UIParsing.apply(children, "scale-to-fit", UIParsing::parseBool, this::scaleToFit);
    }

    public static CharacterComponent parse(Element element) {

        return new CharacterComponent(Sizing.content());
    }

}
