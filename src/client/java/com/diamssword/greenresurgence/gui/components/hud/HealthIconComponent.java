package com.diamssword.greenresurgence.gui.components.hud;

import com.diamssword.greenresurgence.DrawUtils;
import com.diamssword.greenresurgence.containers.player.HoloStyle;
import com.diamssword.greenresurgence.gui.components.panels.HoloPanels;
import com.diamssword.greenresurgence.gui.components.panels.HoloRenders;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.utils.ColorUtils;
import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.w3c.dom.Element;

public class HealthIconComponent extends TextureComponent implements IHideableComponent {
	private static final float SHIELD_TRANSPARENCY = 0.6f;
	private static final int HEART_COUNT = 20;
	private static final float HEART_SCALE = 0.8f;
	private boolean hidden;
	private int hideTimer = 200;
	public boolean blink = false;
	public boolean shieldblink = false;
	public boolean sideBlink = false;
	private int blinkTime = 0;
	private int blinkTimeShield = 0;
	public float filling = 1f;
	public float fillingShield = 1f;
	public float goal = 1f;
	public float goalShield = 1f;
	private HoloRenders.RenderSurface shieldRender = HoloRenders.crtCircle(1);
	private HeartType type = HeartType.NORMAL;
	private HoloStyle style;

	protected HealthIconComponent(Identifier texture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
		super(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
		this.style = HoloPanels.getCurrentStyle();
	}

	@Override
	public void update(float delta, int mouseX, int mouseY) {
		super.update(delta, mouseX, mouseY);

	}

	@Override
	protected int determineHorizontalContentSize(Sizing sizing) {
		var v = verticalSizing.get();
		if(v != null && !v.isContent() && height > 0) {
			return (int) (height * ((float) this.regionWidth / (float) this.regionHeight));
		}
		return this.regionWidth;
	}

	@Override
	protected int determineVerticalContentSize(Sizing sizing) {
		var v = horizontalSizing.get();
		if(v != null && !v.isContent() && width > 0) {
			return (int) (width * ((float) this.regionHeight / (float) this.regionWidth));
		}
		return this.regionHeight;
	}

	@Override
	protected void applySizing() {
		final var horizontalSizing = this.horizontalSizing.get();
		final var verticalSizing = this.verticalSizing.get();
		final var margins = this.margins.get();
		if(verticalSizing.isContent()) {
			this.width = horizontalSizing.inflate(this.space.width() - margins.horizontal(), this::determineHorizontalContentSize);
			this.height = verticalSizing.inflate(this.space.height() - margins.vertical(), this::determineVerticalContentSize);
		} else {
			this.height = verticalSizing.inflate(this.space.height() - margins.vertical(), this::determineVerticalContentSize);
			this.width = horizontalSizing.inflate(this.space.width() - margins.horizontal(), this::determineHorizontalContentSize);
		}
	}

	public void animateForHealth(PlayerEntity pl) {

		var v = pl.getHealth() / pl.getMaxHealth();
		if(v > goal)
			blinkTime = 5;
		this.blink = blinkTime > 0;
		if(blinkTime > 0)
			blinkTime--;
		this.sideBlink = pl.hurtTime > 0 && v < goal;
		this.goal = v;
		if(this.filling < goal)
			this.filling = Math.min(goal, filling + 0.1f);
		else if(this.filling > goal)
			this.filling = Math.max(goal, filling - 0.1f);
		if(filling < 1f || fillingShield < 1f) {
			hideTimer = 200;
		} else if(hideTimer > 0)
			hideTimer--;
		this.type = HeartType.fromPlayerState(pl);
	}

	public void animateForShield(PlayerEntity pl) {
		var man = pl.getComponent(Components.PLAYER_DATA).healthManager;
		var v = man.getShieldAmount() / man.getMaxShieldAmount();
		if(v > goalShield)
			blinkTimeShield = 5;
		this.shieldblink = blinkTimeShield > 0;
		if(blinkTimeShield > 0)
			blinkTimeShield--;
		this.goalShield = (float) v;
		if(this.fillingShield < goalShield)
			this.fillingShield = Math.min(goalShield, fillingShield + 0.1f);
		else if(this.fillingShield > goalShield)
			this.fillingShield = Math.max(goalShield, fillingShield - 0.1f);
	}

	public static double[] fillingToAngles(float filling) {
		filling = Math.max(0f, Math.min(1f, filling));

		if(filling >= 1f) {
			return new double[]{0, 360};
		}

		double halfAngle = filling * 180.0;
		return new double[]{180.0 - halfAngle, 180.0 + halfAngle};
	}

	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		RenderSystem.enableDepthTest();
		if(hidden || hideTimer == 0)
			return;
		if(this.blend) {
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		}
		if(this.blend)
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, Math.min((this.hideTimer / 100f) * SHIELD_TRANSPARENCY, SHIELD_TRANSPARENCY));
		shieldRender.draw(context, this.x, this.y, this.width, this.height);
		var matrices = context.getMatrices();
		matrices.push();
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		matrices.translate(x, y, 0);
		var cl = MinecraftClient.getInstance();
		if(cl.player != null) {


			float a = (int) (this.width * HEART_SCALE);
			matrices.push();

			matrices.translate((width - a) / 2f, ((width - a) / 2f) + 1, 0);
			drawGauge(context, getU(type, blink), 9f, filling, a, a);
			DrawUtils.drawTexturedQuadWithAlpha(context, this.texture, 0,
					0,
					(int) a,
					(int) a,
					getU(HeartType.CONTAINER, sideBlink),
					this.v,
					this.regionWidth,
					this.regionHeight,
					this.textureWidth, this.textureHeight, this.hideTimer / 100f);

			matrices.pop();
			if(this.blend)
				RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, Math.min((this.hideTimer / 100f) * SHIELD_TRANSPARENCY, SHIELD_TRANSPARENCY));
			var color = style.getMainColor().solid();
			if(shieldblink)
				color = ColorUtils.lighten(color, 0.2f);
			drawCircleFill(context, width / 2, height / 2, (width / 2f) - 0.99, fillingShield, color);

			if(this.blend)
				RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.hideTimer / 100f);
			//ColorUtils.whithAlpha(HoloPanels.defaultColor, 0xAF)
			drawCircleOutline(context, width / 2, height / 2, 0, 360, 40, width / 2f, 1.8, style.getMainColor().border());
		}


		matrices.pop();

		if(this.blend) {
			RenderSystem.disableBlend();
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);
		}
	}

	private float getU(HeartType type, boolean blinking) {
		int i = blinking ? 1 : 0;
		float d = this.textureWidth / (float) HEART_COUNT;
		return (type.textureIndex * 2 + i) * d;
	}

	public void drawCircleFill(OwoUIDrawContext context, int centerX, int centerY,
							   double radius, float filling, int color) {
		filling = Math.max(0f, Math.min(1f, filling));
		if(radius <= 0 || filling <= 0) return;

		int yBottom = (int) Math.ceil(centerY + radius);
		int yTop = (int) Math.ceil(centerY + radius - 2 * radius * filling);

		for(int y = yTop; y < yBottom; y++) {
			double dy = (y + 0.5) - centerY;
			double halfWidth = Math.sqrt(Math.max(0, radius * radius - dy * dy));

			int x1 = (int) Math.ceil(centerX - halfWidth);
			int x2 = (int) Math.ceil(centerX + halfWidth);

			if(x1 < x2) {
				context.fill(x1, y, x2, y + 1, color);
			}
		}
	}


	public void drawCircleOutline(OwoUIDrawContext context, int centerX, int centerY, double angleFrom, double angleTo,
								  int segments, double radius, double thickness, int color) {
		Preconditions.checkArgument(angleFrom < angleTo, "angleFrom must be less than angleTo");
		Preconditions.checkArgument(segments > 0, "segments must be greater than 0");
		Preconditions.checkArgument(thickness > 0 && thickness <= radius,
				"thickness must be positive and no greater than radius");

		var buffer = Tessellator.getInstance().getBuffer();
		var matrix = context.getMatrices().peek().getPositionMatrix();

		double angleStep = Math.toRadians(angleTo - angleFrom) / segments;
		double innerRadius = radius - thickness;

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);

		buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

		for(int i = 0; i <= segments; i++) {
			double theta = Math.toRadians(angleFrom) + i * angleStep;
			double cos = Math.cos(theta);
			double sin = Math.sin(theta);

			buffer.vertex(matrix,
					(float) (centerX - cos * radius),
					(float) (centerY - sin * radius),
					0).color(color).next();

			buffer.vertex(matrix,
					(float) (centerX - cos * innerRadius),
					(float) (centerY - sin * innerRadius),
					0).color(color).next();
		}

		Tessellator.getInstance().draw();
	}

	private void drawGauge(DrawContext ctx, float uIndex, float pxSpace, float fill, float width, float height) {
		float ipx = textureHeight / pxSpace;
		float ih = (height / pxSpace);
		fill = MathHelper.clamp(fill, 0.0f, 1.0f);
		var rh = regionHeight - (ipx * 2);
		float uvHeight = (rh * fill);

		float uvYOffset = (regionHeight - (ipx)) - uvHeight;
		float drawHeightVisible = ((height - (ih * 2f)) * fill);
		float yOffset = ((height - ih) - drawHeightVisible);
		DrawUtils.drawTexturedQuadWithAlpha(
				ctx,
				this.texture,
				0, yOffset,
				width, drawHeightVisible,
				uIndex, uvYOffset,
				regionWidth, uvHeight,           // UV size
				this.textureWidth, this.textureHeight, this.hideTimer / 100f
		);
	}

	public static HealthIconComponent parse(Element element) {
		UIParsing.expectAttributes(element, "texture");
		var textureId = UIParsing.parseIdentifier(element.getAttributeNode("texture"));
		int u = 0, v = 0, regionWidth = 0, regionHeight = 0, textureWidth = 256, textureHeight = 256;
		if(element.hasAttribute("u")) {
			u = UIParsing.parseSignedInt(element.getAttributeNode("u"));
		}

		if(element.hasAttribute("v")) {
			v = UIParsing.parseSignedInt(element.getAttributeNode("v"));
		}
		if(element.hasAttribute("region-width")) {
			regionWidth = UIParsing.parseSignedInt(element.getAttributeNode("region-width"));
		}

		if(element.hasAttribute("region-height")) {
			regionHeight = UIParsing.parseSignedInt(element.getAttributeNode("region-height"));
		}

		if(element.hasAttribute("texture-width")) {
			textureWidth = UIParsing.parseSignedInt(element.getAttributeNode("texture-width"));
		}

		if(element.hasAttribute("texture-height")) {
			textureHeight = UIParsing.parseSignedInt(element.getAttributeNode("texture-height"));
		}

		return new HealthIconComponent(new Identifier(textureId.getNamespace(), "textures/gui/" + textureId.getPath()), u, v, regionWidth, regionHeight, textureWidth, textureHeight);
	}

	@Override
	public void hidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public boolean isHidden() {
		return this.hidden;
	}

	enum HeartType {
		SHIELD(0),
		CONTAINER(1),
		NORMAL(2),
		POISONED(3),
		WITHERED(4),
		ABSORBING(5),
		TRUE(6),
		FROZEN(7);

		private final int textureIndex;

		HeartType(int textureIndex) {
			this.textureIndex = textureIndex;
		}

		static HeartType fromPlayerState(PlayerEntity player) {
			HeartType heartType;
			if(player.hasStatusEffect(StatusEffects.POISON)) {
				heartType = POISONED;
			} else if(player.hasStatusEffect(StatusEffects.WITHER)) {
				heartType = WITHERED;
			} else if(player.isFrozen()) {
				heartType = FROZEN;
			} else if(player.hasStatusEffect(StatusEffects.ABSORPTION) && player.getAbsorptionAmount() > 0) {
				heartType = ABSORBING;
			} else {
				heartType = NORMAL;
			}
			return heartType;
		}
	}
}
