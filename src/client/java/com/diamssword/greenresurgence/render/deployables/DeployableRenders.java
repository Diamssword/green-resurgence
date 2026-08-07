package com.diamssword.greenresurgence.render.deployables;

import com.diamssword.greenresurgence.systems.deployable.AbstractDeployableInstance;
import com.diamssword.greenresurgence.systems.deployable.DeployableRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

public class DeployableRenders {
	static Map<String, IDeployableRenderer> registry = new HashMap<>();

	public static void register(String id, IDeployableRenderer renderer) {
		registry.put(id, renderer);
	}

	public static IDeployableRenderer getRenderer(String id) {
		return registry.getOrDefault(id, new IDeployableRenderer() {
			@Override
			public void render(AbstractDeployableInstance instance, float yaw, float tickDeltas, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {

			}
		});
	}

	public static void init() {
		register(DeployableRegistry.CAMPFIRE, new BlockstatesDeployableRenderer()
				.addBlock(Vec3d.ZERO, Blocks.CAMPFIRE.getDefaultState().with(CampfireBlock.LIT, true)).addBlock(new Vec3d(1.5, 0, 0), Blocks.ACACIA_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST)));
	}
}
