package com.diamssword.greenresurgence.render.deployables;

import com.diamssword.greenresurgence.systems.deployable.AbstractDeployableInstance;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BlockstatesDeployableRenderer implements IDeployableRenderer {
	private Map<Vec3d, Function<AbstractDeployableInstance, BlockState>> blocks = new HashMap<>();

	public BlockstatesDeployableRenderer addBlock(Vec3d offset, Function<AbstractDeployableInstance, BlockState> function) {
		blocks.put(offset, function);
		return this;
	}

	public BlockstatesDeployableRenderer addBlock(Vec3d offset, BlockState state) {
		Function<AbstractDeployableInstance, BlockState> fac = a -> state;
	/*	for(Property<?> p : state.getProperties()) {
			if(p.getType() == Direction.class) {
				fac = (a) -> state.with((Property<Direction>) p, a.getDirection());
				break;
			} else if(p.getType() == Direction.Axis.class) {

				fac = (a) -> state.with((Property<Direction.Axis>) p, a.getDirection().getAxis());
				break;
			}
		}*/
		blocks.put(offset, fac);
		return this;
	}

	@Override
	public void render(AbstractDeployableInstance instance, float yaw, float tickDeltas, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
		var blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
		World world = instance.getEntity().getWorld();
		Random rand = world != null ? world.random : Random.create();
		var pos = instance.getEntity().getBlockPos();
		blocks.forEach((p, s) -> {
			var state = s.apply(instance);
			matrixStack.push();
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
			matrixStack.translate(p.getX() - 0.5f, p.getY(), p.getZ() - 0.5f);
			blockRenderManager.getModelRenderer().render(world, blockRenderManager.getModel(state), state, pos.add((int) p.x, (int) p.y, (int) p.z), matrixStack, vertexConsumerProvider.getBuffer(RenderLayers.getBlockLayer(state)), false, rand, state.getRenderingSeed(pos.add((int) p.x, (int) p.y, (int) p.z)), OverlayTexture.DEFAULT_UV);
			matrixStack.pop();
		});
	}
}
