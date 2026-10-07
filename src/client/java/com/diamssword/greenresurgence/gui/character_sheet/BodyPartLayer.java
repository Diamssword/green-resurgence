package com.diamssword.greenresurgence.gui.character_sheet;

import com.diamssword.characters.api.CharactersApi;
import com.diamssword.characters.api.http.SkinLayerValue;
import com.diamssword.characters.api.skin.BodyLayerInfo;
import com.diamssword.greenresurgence.render.cosmetics.ClothingModel;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BodyPartLayer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	private final FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> ctx;
	private final BodyLayerInfo layer;
	public final boolean altTexture;
	private final int index;
	public Identifier texture;
	private final ClothingModel<AbstractClientPlayerEntity> model;

	public BodyPartLayer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context, BodyLayerInfo layer, boolean altTexture, boolean thinArm, int index) {
		super(context);
		this.ctx = context;
		this.altTexture = altTexture;
		this.layer = layer;
		this.index = index;
		int scale = (int) (layer.size() * 1000f);
		model = new ClothingModel<>(thinArm, altTexture ? scale + 1 : scale, altTexture);
	}

	public void setPart(SkinLayerValue part) {
		texture = CharactersApi.bodyParts().loadSkinLayerAsTexture(part, this.index);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		if(texture != null) {
			ctx.getModel().copyBipedStateTo(model);
			model.animateModel(entity, limbAngle, limbDistance, tickDelta);
			model.render(matrices, vertexConsumers.getBuffer(model.getLayer(texture)), light, LivingEntityRenderer.getOverlay(entity, 0), 1, 1, 1, 1);
		}
	}

}