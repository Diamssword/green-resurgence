package com.diamssword.greenresurgence.render.entities;

import com.diamssword.greenresurgence.entities.ThrownWeaponEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class ThrownWeaponRenderer extends EntityRenderer<ThrownWeaponEntity> {

	public ThrownWeaponRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(ThrownWeaponEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {

		matrixStack.push();
		var stack = itemEntity.asItemStack();
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, itemEntity.prevYaw, itemEntity.getYaw()) - 90.0F));
		matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(g, itemEntity.prevPitch, itemEntity.getPitch()) + 90.0F));
		//matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(itemEntity.getBodyYaw()));
		//matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(75f));
		matrixStack.translate(0, 0f, -0.05);
		MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, itemEntity.getWorld(), 0);

		matrixStack.pop();
		super.render(itemEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	@Override
	public Identifier getTexture(ThrownWeaponEntity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}
