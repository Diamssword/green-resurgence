package com.diamssword.greenresurgence.render.deployables;

import com.diamssword.greenresurgence.systems.deployable.AbstractDeployableInstance;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public interface IDeployableRenderer {
	public void render(AbstractDeployableInstance instance, float yaw, float tickDeltas, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light);
}
