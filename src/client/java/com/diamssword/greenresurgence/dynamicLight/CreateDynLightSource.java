package com.diamssword.greenresurgence.dynamicLight;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.utility.VecHelper;
import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class CreateDynLightSource implements DynamicLightBehavior {
	protected final AbstractContraptionEntity contraptionEntity;
	protected final BlockPos localPos;
	protected Vec3d position;
	protected int luminance;

	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private final int id;

	private long lambDynLightsLastUpdate;

	protected BlockPos blockPos;

	public CreateDynLightSource(int id, AbstractContraptionEntity entity, BlockPos localPos, int luminance) {
		this.contraptionEntity = entity;
		this.id = id;
		this.luminance = luminance;
		this.localPos = localPos;
		var worldPos = entity.toGlobalVector(VecHelper.getCenterOf(localPos), 1);
		setPosition(worldPos);
	}

	protected void setPosition(Vec3d position) {
		this.position = position;

		int x = MathHelper.floor(position.x);
		int y = MathHelper.floor(position.y);
		int z = MathHelper.floor(position.z);
		blockPos = new BlockPos(x, y, z);
	}

	public BlockPos blockPosition() {
		return blockPos;
	}

	public int getLuminance() {
		return luminance;
	}


	public void syncPositionAndLuminance() {
		if(contraptionEntity.getWorld() == MinecraftClient.getInstance().world) {
			var newPosition = contraptionEntity.toGlobalVector(VecHelper.getCenterOf(localPos), 1);
			setPosition(newPosition);

			var blockInfo = contraptionEntity.getContraption().getBlocks().get(localPos);
			if(blockInfo != null) {
				var newLuminance = blockInfo.state().getLuminance();
				if(newLuminance != luminance) {
					luminance = newLuminance;
				}
			}
		}
	}

	public boolean shouldUpdateDynamicLight() {

		int delay = 0;
		if(delay > 0) {
			long currentTime = System.currentTimeMillis();
			if(currentTime < this.lambDynLightsLastUpdate + delay) {
				return false;
			}

			this.lambDynLightsLastUpdate = currentTime;
		}
		return true;
	}


	public boolean update() {
		if(!this.shouldUpdateDynamicLight())
			return false;
		syncPositionAndLuminance();
		return false;
	}

	@Override
	public @Range(from = 0L, to = 15L) double lightAtPos(BlockPos pos, double falloffRatio) {
		double dx = pos.getX() - position.x;
		double dy = pos.getY() - position.y;
		double dz = pos.getZ() - position.z;
		int lum = Math.min(15, Math.max(0, getLuminance()));
		double distanceSquared = dx * dx + dy * dy + dz * dz;
		return Math.max(lum - Math.sqrt(distanceSquared) * falloffRatio, 0.0);
	}

	@Override
	public @NotNull BoundingBox getBoundingBox() {
		return new BoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1);
	}

	@Override
	public boolean hasChanged() {
		return shouldUpdateDynamicLight();
	}

	@Override
	public boolean isRemoved() {
		return contraptionEntity.isRemoved();
	}
}
