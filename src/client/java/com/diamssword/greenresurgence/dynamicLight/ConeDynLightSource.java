package com.diamssword.greenresurgence.dynamicLight;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class ConeDynLightSource extends CreateDynLightSource {
	private static final int ANGLE = 60;
	private static final int RANGE = 16;
	protected final Direction localDirection;
	private Vec3d direction;

	public ConeDynLightSource(int id, AbstractContraptionEntity entity, BlockPos localPos, int luminance, Direction localDirection) {
		super(id, entity, localPos, luminance);
		this.localDirection = localDirection;
		var vec = localDirection.getVector();
		this.direction = new Vec3d(vec.getX(), vec.getY(), vec.getZ());
	}

	@Override
	public void syncPositionAndLuminance() {
		if(contraptionEntity.getWorld() == MinecraftClient.getInstance().world) {
			var newPosition = contraptionEntity.toGlobalVector(VecHelper.getCenterOf(localPos), 1);
			setPosition(newPosition);
			Vec3d dirVec = Vec3d.of(localDirection.getVector());

			// Apply the contraption’s current rotation
			direction = contraptionEntity.applyRotation(dirVec, 1);
			var blockInfo = contraptionEntity.getContraption().getBlocks().get(localPos);
			if(blockInfo != null) {
				var newLuminance = blockInfo.state().getLuminance();
				if(newLuminance != luminance) {
					luminance = newLuminance;
				}
			}
		}
	}

	@Override
	public @NotNull BoundingBox getBoundingBox() {
		//return super.getBoundingBox();
		// Endpoint of the cone
		Vec3d tip = new Vec3d(
				position.x + direction.x * RANGE,
				position.y + direction.y * RANGE,
				position.z + direction.z * RANGE
		);

		// Radius of cone at far end
		double radius = RANGE * Math.tan(Math.toRadians(ANGLE / 2.0));

		// Min/max corners (source and tip ± radius)
		int minX = (int) (Math.min(position.x, tip.x) - radius);
		int minY = (int) (Math.min(position.y, tip.y) - radius);
		int minZ = (int) (Math.min(position.z, tip.z) - radius);
		int maxX = (int) (Math.max(position.x, tip.x) + radius);
		int maxY = (int) (Math.max(position.y, tip.y) + radius);
		int maxZ = (int) (Math.max(position.z, tip.z) + radius);
		return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);

	}

	@Override
	public @Range(from = 0L, to = 15L) double lightAtPos(BlockPos pos, double falloffRatio) {
		Vec3d sourcePos = this.position.add(-direction.x * 2, -direction.y * 2, -direction.z * 2); // Center of your contraption block
		Vec3d facingDir = this.direction; // From earlier (rotated direction)

		// Parameters

		double maxBrightness = this.luminance;

		// Compute target position center
		Vec3d targetPos = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

		// Vector from light to position
		Vec3d toTarget = new Vec3d(targetPos.x - sourcePos.x, targetPos.y - sourcePos.y, targetPos.z - sourcePos.z
		);
		double distance = Math.sqrt(toTarget.x * toTarget.x + toTarget.y * toTarget.y + toTarget.z * toTarget.z);
		if(distance < 1e-6 || distance > RANGE) return 0.0;

		// --- Normalize that vector manually ---
		Vec3d dirToTarget = new Vec3d(
				toTarget.x / distance,
				toTarget.y / distance,
				toTarget.z / distance
		);

		// Dot product = cosine of angle between the two vectors
		double alignment = facingDir.x * dirToTarget.x + facingDir.y * dirToTarget.y + facingDir.z * dirToTarget.z;
		// Clamp to valid range just in case of floating-point noise
		alignment = MathHelper.clamp(alignment, -1.0, 1.0);

		// Convert cone angle to cosine cutoff
		double cutoff = Math.cos(Math.toRadians(ANGLE / 2.0));

		// If outside the cone, no light
		if(alignment < cutoff)
			return 0.0;

		// Map alignment (cutoff → 1) to 0 → 1
		double dirFactor = (alignment - cutoff) / (1.0 - cutoff);
		dirFactor = MathHelper.clamp(dirFactor, 0.0, 1.0);

		double distFactor = 1.0 - (distance / RANGE);
		// Distance falloff — slower, smoother
		//double distFactor = 1.0 / (1.0 + distance * falloffRatio);
		distFactor = MathHelper.clamp(distFactor, 0.0, 1.0);

		// Combine and scale
		double lightLevel = maxBrightness * dirFactor * distFactor;
		// Clamp to [0, 15]
		return MathHelper.clamp(lightLevel, 0.0, maxBrightness);
	}
}
