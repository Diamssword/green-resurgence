package com.diamssword.greenresurgence.render.entities;

import com.diamssword.greenresurgence.entities.deployable.DeployableEntity;
import com.diamssword.greenresurgence.render.deployables.DeployableRenders;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class DeployableEntityRenderer extends EntityRenderer<DeployableEntity> {

	public DeployableEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(DeployableEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		var dep = entity.getDeployable();
		if(dep != null) {
			matrixStack.push();
			DeployableRenders.getRenderer(dep.id).render(dep, f, g, matrixStack, vertexConsumerProvider, i);
			matrixStack.pop();
		}

		super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	@Override
	public Identifier getTexture(DeployableEntity entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}
