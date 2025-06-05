package com.diamssword.greenresurgence.gui.components.hud;

import com.diamssword.greenresurgence.DrawUtils;
import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.Components;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.w3c.dom.Element;

public class HealthIconComponent extends TextureComponent implements IHideableComponent {
	private static final int HEART_COUNT = 20;
	private static final float HEART_SCALE = 0.7f;
	private static final int SHIELD_T_W = 192;
	private static final int SHIELD_T_H = 64;
	private static final Identifier SHIELD_TEXTURE = GreenResurgence.asRessource("textures/gui/hud/shield.png");
	private boolean hidden;
	public boolean blink = false;
	public boolean shieldblink = false;
	public boolean sideBlink = false;
	private int blinkTime = 0;
	private int blinkTimeShield = 0;
	public float filling = 1f;
	public float fillingShield = 1f;
	public float goal = 1f;
	public float goalShield = 1f;
	public int ticks;
	private HeartType type = HeartType.NORMAL;

	protected HealthIconComponent(Identifier texture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
		super(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
	}

	@Override
	public void update(float delta, int mouseX, int mouseY) {
		super.update(delta, mouseX, mouseY);

	}

	@Override
	protected int determineHorizontalContentSize(Sizing sizing) {
		var v = verticalSizing.get();
		if (v != null && !v.isContent() && height > 0) {
			return (int) (height * ((float) this.regionWidth / (float) this.regionHeight));
		}
		return this.regionWidth;
	}

	@Override
	protected int determineVerticalContentSize(Sizing sizing) {
		var v = horizontalSizing.get();
		if (v != null && !v.isContent() && width > 0) {
			return (int) (width * ((float) this.regionHeight / (float) this.regionWidth));
		}
		return this.regionHeight;
	}

	@Override
	protected void applySizing() {
		final var horizontalSizing = this.horizontalSizing.get();
		final var verticalSizing = this.verticalSizing.get();
		final var margins = this.margins.get();
		if (verticalSizing.isContent()) {
			this.width = horizontalSizing.inflate(this.space.width() - margins.horizontal(), this::determineHorizontalContentSize);
			this.height = verticalSizing.inflate(this.space.height() - margins.vertical(), this::determineVerticalContentSize);
		} else {
			this.height = verticalSizing.inflate(this.space.height() - margins.vertical(), this::determineVerticalContentSize);
			this.width = horizontalSizing.inflate(this.space.width() - margins.horizontal(), this::determineHorizontalContentSize);
		}
	}

	public void animateForHealth(PlayerEntity pl) {

		var v = pl.getHealth() / pl.getMaxHealth();
		if (v > goal)
			blinkTime = 5;
		this.blink = blinkTime > 0;
		if (blinkTime > 0)
			blinkTime--;
		this.sideBlink = pl.hurtTime > 0 && v < goal;
		this.goal = v;
		if (this.filling < goal)
			this.filling = Math.min(goal, filling + 0.1f);
		else if (this.filling > goal)
			this.filling = Math.max(goal, filling - 0.1f);
		type = HeartType.fromPlayerState(pl);
	}

	public void animateForShield(PlayerEntity pl) {
		var man = pl.getComponent(Components.PLAYER_DATA).healthManager;
		var v = man.getShieldAmount() / 20f;
		if (v > goalShield)
			blinkTimeShield = 5;
		this.shieldblink = blinkTimeShield > 0;
		if (blinkTimeShield > 0)
			blinkTimeShield--;
		this.goalShield = v;
		if (this.fillingShield < goalShield)
			this.fillingShield = Math.min(goalShield, fillingShield + 0.1f);
		else if (this.fillingShield > goalShield)
			this.fillingShield = Math.max(goalShield, fillingShield - 0.1f);
	}

	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		RenderSystem.enableDepthTest();
		if (hidden)
			return;

		if (this.blend) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
		}

		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(x, y, 0);
		var cl = MinecraftClient.getInstance();
		if (cl.player != null) {
			drawShield(context, shieldblink, 64f, fillingShield);
			DrawUtils.drawTexture(
					context,
					SHIELD_TEXTURE,
					0, 0,
					width, height,
					0, 0,
					SHIELD_T_H, SHIELD_T_H,           // UV size
					SHIELD_T_W, SHIELD_T_H
			);
			float a = (int) (this.width * HEART_SCALE);
			matrices.translate((width - a) / 2f, (width - a) / 2f, 0);
			drawGauge(context, getU(type, blink), 9f, filling, a, a);
			context.drawTexture(this.texture,
					0,
					0,
					(int) a,
					(int) a,
					getU(HeartType.CONTAINER, sideBlink),
					this.v,
					this.regionWidth,
					this.regionHeight,
					this.textureWidth, this.textureHeight
			);
		}
		if (this.blend) {
			RenderSystem.disableBlend();
		}

		matrices.pop();
	}

	private float getU(HeartType type, boolean blinking) {
		int i = blinking ? 1 : 0;
		float d = this.textureWidth / (float) HEART_COUNT;
		return (type.textureIndex * 2 + i) * d;
	}

	private void drawShield(DrawContext ctx, boolean blink, float pxSpace, float fill) {
		float ipx = SHIELD_T_H / pxSpace;
		int ih = (int) (height / pxSpace);
		fill = MathHelper.clamp(fill, 0.0f, 1.0f);
		var rh = SHIELD_T_H - (ipx * 2);
		float uvHeight = (rh * fill);

		float uvYOffset = (SHIELD_T_H - (ipx)) - uvHeight;
		int drawHeightVisible = (int) ((height - (ih * 2)) * fill);
		int yOffset = (height - (ih)) - (drawHeightVisible);
		DrawUtils.drawTexture(
				ctx,
				SHIELD_TEXTURE,
				0, yOffset,
				width, drawHeightVisible,
				SHIELD_T_H * (blink ? 2 : 1), uvYOffset,
				SHIELD_T_H, uvHeight,           // UV size
				SHIELD_T_W, SHIELD_T_H
		);
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
		DrawUtils.drawTexture(
				ctx,
				this.texture,
				0, yOffset,
				width, drawHeightVisible,
				uIndex, uvYOffset,
				regionWidth, uvHeight,           // UV size
				this.textureWidth, this.textureHeight
		);
	}

	public static HealthIconComponent parse(Element element) {
		UIParsing.expectAttributes(element, "texture");
		var textureId = UIParsing.parseIdentifier(element.getAttributeNode("texture"));
		int u = 0, v = 0, regionWidth = 0, regionHeight = 0, textureWidth = 256, textureHeight = 256;
		if (element.hasAttribute("u")) {
			u = UIParsing.parseSignedInt(element.getAttributeNode("u"));
		}

		if (element.hasAttribute("v")) {
			v = UIParsing.parseSignedInt(element.getAttributeNode("v"));
		}
		if (element.hasAttribute("region-width")) {
			regionWidth = UIParsing.parseSignedInt(element.getAttributeNode("region-width"));
		}

		if (element.hasAttribute("region-height")) {
			regionHeight = UIParsing.parseSignedInt(element.getAttributeNode("region-height"));
		}

		if (element.hasAttribute("texture-width")) {
			textureWidth = UIParsing.parseSignedInt(element.getAttributeNode("texture-width"));
		}

		if (element.hasAttribute("texture-height")) {
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
			if (player.hasStatusEffect(StatusEffects.POISON)) {
				heartType = POISONED;
			} else if (player.hasStatusEffect(StatusEffects.WITHER)) {
				heartType = WITHERED;
			} else if (player.isFrozen()) {
				heartType = FROZEN;
			} else if (player.hasStatusEffect(StatusEffects.ABSORPTION) && player.getAbsorptionAmount() > 0) {
				heartType = ABSORBING;
			} else {
				heartType = TRUE;
			}
			return heartType;
		}
	}
}
