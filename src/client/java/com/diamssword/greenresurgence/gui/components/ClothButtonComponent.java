package com.diamssword.greenresurgence.gui.components;

import com.diamssword.characters.api.appearence.Cloth;
import com.diamssword.greenresurgence.render.cosmetics.ClothingModel;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.mixin.ui.access.ClickableWidgetAccessor;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import io.wispforest.owo.util.pond.OwoEntityRenderDispatcherExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
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
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;

public class ClothButtonComponent extends ButtonComponent {
	private static final String CLOTH_MOD_ID = "character_sheet";
	private Cloth cloth;
	private static final ClothingModel<AbstractClientPlayerEntity> model = new ClothingModel<>(false, 0, false);
	private static final ClothingModel<AbstractClientPlayerEntity> model1 = new ClothingModel<>(false, 1, true);
	protected final EntityRenderDispatcher dispatcher;
	protected final VertexConsumerProvider.Immediate entityBuffers;
	private boolean hoveredSent = false;
	private boolean selected = false;
	private final EventStream<ClothPicked> onHovered = ClothPicked.newPickStream();

	public ClothButtonComponent(Cloth cloth) {
		super(Text.literal(""), a -> {
		});
		setCloth(cloth);
		final var client = MinecraftClient.getInstance();
		this.dispatcher = client.getEntityRenderDispatcher();
		this.entityBuffers = client.getBufferBuilders().getEntityVertexConsumers();
	}

	public void setCloth(Cloth cloth) {
		this.cloth = cloth;
		var tool = Text.literal((cloth.name().replaceAll("/", " ").replaceAll("_", " ")));
		if(!cloth.collection().equals("default"))
			tool.append(Text.literal("\n" + cloth.collection()).formatted(Formatting.BLUE, Formatting.ITALIC));
		this.tooltip(tool);
	}

	public Cloth getCloth() {
		return cloth;
	}

	public boolean selected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public EventSource<ClothPicked> onClothHovered() {
		return this.onHovered.source();
	}

	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {

		if(selected)
			context.setShaderColor(0.8f, 1f, 0.8f, 1);
		Panels.drawOverlay(context, this.x(), this.y(), this.width, this.height);
		context.setShaderColor(1, 1, 1, 1);
		drawClothing(context, mouseX);
		this.hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
		if(this.hovered) {
			if(!this.hoveredSent)
				this.onHovered.sink().onPicked(cloth);
			this.hoveredSent = true;
			Tooltip tooltip = ((ClickableWidgetAccessor) this).owo$getTooltip();
			if(tooltip != null) {
				TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
				context.drawTooltip(textRenderer, tooltip.getLines(MinecraftClient.getInstance()), HoveredTooltipPositioner.INSTANCE, mouseX, mouseY);
			}
		} else if(this.hoveredSent) {
			this.onHovered.sink().onPicked(null);
			this.hoveredSent = false;
		}

	}

	private void drawClothing(OwoUIDrawContext context, int mouseX) {
		float off = cloth.layer().getDisplayMode() != 0 ? (cloth.layer().getDisplayMode() == 2 ? -(this.height * 0.8f) : this.height / 3f) : 0;

		float scale = cloth.layer().getDisplayMode() != 0 ? (cloth.layer().getDisplayMode() == 2 ? 40 : 30) : 20;
		float yRotation = (float) Math.toDegrees(Math.atan((mouseX - this.getX() - this.width / 2f) / 40f));
		drawClothing(context, x(), y(), width, height, scale, off, cloth, yRotation * .6f);

	}

	public static void drawClothing(DrawContext context, int x, int y, int width, int height, float scale, float offest, Cloth cloth, float rotation) {
		final var client = MinecraftClient.getInstance();
		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(x + (width / 2f), y + (height / 2f) - 8, 100);
		matrices.translate(0, offest, 0);
		if(cloth.layer().getDisplayMode() != 0) {
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-15f));
		}
		matrices.scale(scale, scale, scale);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 + rotation));
		var dispatcher = client.getEntityRenderDispatcher();
		var owodispatcher = (OwoEntityRenderDispatcherExtension) client.getEntityRenderDispatcher();
		owodispatcher.owo$setCounterRotate(true);
		owodispatcher.owo$setShowNametag(false);
		RenderSystem.setShaderLights(new Vector3f(.15f, 1, 0), new Vector3f(.15f, -1, 0));
		dispatcher.setRenderShadows(false);
		var entityBuffers = client.getBufferBuilders().getEntityVertexConsumers();
		renderLayer(cloth, matrices, entityBuffers, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		dispatcher.setRenderShadows(true);
		entityBuffers.draw();
		DiffuseLighting.enableGuiDepthLighting();

		matrices.pop();

		owodispatcher.owo$setCounterRotate(false);
		owodispatcher.owo$setShowNametag(true);
	}

	public static void renderLayer(Cloth cloth, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		model.child = false;
		model1.child = false;
		var pack = OverlayTexture.packUv(OverlayTexture.getU(0), OverlayTexture.getV(false));
		model.render(matrices, vertexConsumers.getBuffer(model.getLayer(new Identifier(cloth.id().getNamespace(), "textures/cloth/" + cloth.id().getPath() + ".png"))), light, pack, 1, 1, 1, 1);
		model1.render(matrices, vertexConsumers.getBuffer(model1.getLayer(new Identifier(cloth.id().getNamespace(), "textures/cloth/" + cloth.id().getPath() + ".png"))), light, pack, 1, 1, 1, 1);

	}

	public interface ClothPicked {
		void onPicked(Cloth cloth);

		static EventStream<ClothPicked> newPickStream() {
			return new EventStream<>(subscribers -> (Cloth cloth) -> {
				for(var subscriber : subscribers) {
					subscriber.onPicked(cloth);
				}
			});
		}
	}
}
