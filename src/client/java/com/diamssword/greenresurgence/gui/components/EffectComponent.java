package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.GreenResurgence;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public class EffectComponent extends TextureComponent {
	public final StatusEffectInstance effect;


	public EffectComponent(StatusEffectInstance effect, Identifier texture) {
		super(texture, 0, 0, 32, 32, 32, 32);
		this.effect = effect;
	}

	public EffectComponent(StatusEffectInstance effect) {
		this(effect, GreenResurgence.asRessource("textures/gui/effect_bg.png"));
	}

	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		RenderSystem.enableDepthTest();

		if (this.blend) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
		}

		var matrices = context.getMatrices();
		matrices.push();
		matrices.translate(x, y, 0);
		matrices.scale(this.width / (float) this.regionWidth, this.height / (float) this.regionHeight, 0);

		var visibleArea = this.visibleArea.get();

		int bottomEdge = Math.min(visibleArea.y() + visibleArea.height(), regionHeight);
		int rightEdge = Math.min(visibleArea.x() + visibleArea.width(), regionWidth);

		context.drawTexture(this.texture,
				visibleArea.x(),
				visibleArea.y(),
				rightEdge - visibleArea.x(),
				bottomEdge - visibleArea.y(),
				this.u + visibleArea.x(),
				this.v + visibleArea.y(),
				rightEdge - visibleArea.x(),
				bottomEdge - visibleArea.y(),
				this.textureWidth, this.textureHeight
		);
		var client = MinecraftClient.getInstance();
		StatusEffectSpriteManager statusEffectSpriteManager = client.getStatusEffectSpriteManager();
		StatusEffect statusEffect = effect.getEffectType();
		Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
		int l = visibleArea.width() - 8;
		context.drawSprite(visibleArea.x() + 4, visibleArea.y() + 4, 0, l, l, sprite);
		if (this.blend) {
			RenderSystem.disableBlend();

		}
		matrices.pop();
		this.tooltip = Arrays.asList(TooltipComponent.of(getStatusEffectDescription(effect).asOrderedText()), TooltipComponent.of(StatusEffectUtil.getDurationText(effect, 1).asOrderedText()));


	}

	private Text getStatusEffectDescription(StatusEffectInstance statusEffect) {
		MutableText mutableText = statusEffect.getEffectType().getName().copy();
		if (statusEffect.getAmplifier() >= 1 && statusEffect.getAmplifier() <= 9) {
			mutableText.append(ScreenTexts.SPACE).append(Text.translatable("enchantment.level." + (statusEffect.getAmplifier() + 1)));
		}

		return mutableText;
	}

}
