package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.customPoses.IPlayerCustomPose;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class PlayerData implements ComponentV3, ServerTickingComponent, ClientTickingComponent, AutoSyncedComponent {
	public static int SYNC_MODE_FULL = 0;
	public static int SYNC_MODE_HUD = 1;
	public static int SYNC_MODE_CLOTH = 2;
	private EntityPose forcedPose;
	private String customPoseID;
	private IPlayerCustomPose customPose;
	public final PlayerEntity player;
	private NbtCompound carriedEntity;
	public final HealthManager healthManager;

	public PlayerData(PlayerEntity e) {
		this.player = e;
		this.healthManager = new HealthManager(e);
	}

	public boolean isForcedPose() {
		return forcedPose != null && forcedPose != EntityPose.STANDING;
	}

	public EntityPose getPose() {
		return forcedPose;
	}

	public void setForcedPose(EntityPose pose) {
		forcedPose = pose;
		player.setPose(pose);
		PlayerData.syncApparence(player);
	}

	public void placeCarriedEntity() {
		if (this.carriedEntity != null) {
			getCarriedEntity().ifPresent(et -> player.getWorld().spawnEntity(et));
			this.carriedEntity = null;
		}
	}

	public boolean isCarryingEntity() {
		return this.carriedEntity != null;
	}

	public Optional<Entity> getCarriedEntity() {
		if (this.carriedEntity != null) {
			return EntityType.getEntityFromNbt(this.carriedEntity, player.getWorld()).map(e -> {
				e.updatePosition(player.getX(), player.getY(), player.getZ());
				return e;
			});
		}
		return Optional.empty();
	}

	public void setCarriedEntity(Entity e) {

		placeCarriedEntity();
		var tag = new NbtCompound();
		e.saveSelfNbt(tag);
		this.carriedEntity = tag;
		e.remove(Entity.RemovalReason.DISCARDED);
		PlayerData.syncApparence(player);
	}

	public IPlayerCustomPose getCustomPose() {
		return customPose;
	}

	public String getCustomPoseID() {
		return customPoseID;
	}

	public void setCustomPose(String id) {
		if (id != null) {
			customPose = PosesManager.createPose(id, player);
			if (customPose != null)
				customPoseID = id;
			else
				customPoseID = null;
		} else {
			customPoseID = null;
			customPose = null;
		}
		player.calculateDimensions();
		PlayerData.syncApparence(player);
	}

	@Override
	public void serverTick() {
		if (customPose != null) {
			if (customPose.shouldExitPose(player)) {
				setCustomPose(null);
			} else {
				customPose.tick(player);
			}
		}
		healthManager.update();
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		if (tag.contains("health"))
			healthManager.readNbt(tag.getCompound("health"));
		if (tag.contains("pose"))
			forcedPose = EntityPose.valueOf(tag.getString("pose"));
		if (tag.contains("carriedEntity"))
			this.carriedEntity = tag.getCompound("carriedEntity");
		if (tag.contains("customPoseID")) {
			var d = tag.getString("customPoseID");
			if (d.equals("null")) {
				customPose = null;
				customPoseID = null;
				player.calculateDimensions();
			} else if (!d.equals(customPoseID) || customPose == null) {
				customPose = PosesManager.createPose(d, player);
				player.calculateDimensions();
			}
			customPoseID = d;
		}
		if (tag.contains("shieldAmount"))
			healthManager.setShieldAmount(tag.getDouble("shieldAmount"));
		if (tag.contains("energyAmount"))
			healthManager.setEnergyAmount(tag.getDouble("energyAmount"));
	}

	@Override
	public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity player) {
		this.writeSyncPacket(buf, player, SYNC_MODE_FULL);
	}

	public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient, int mode) {

		NbtCompound tag = new NbtCompound();
		if (mode == SYNC_MODE_FULL || mode == SYNC_MODE_CLOTH) {
			if (forcedPose != null)
				tag.putString("pose", forcedPose.toString());
			if (carriedEntity != null)
				tag.put("carriedEntity", carriedEntity);
			if (customPoseID != null)
				tag.putString("customPoseID", customPoseID);
			else
				tag.putString("customPoseID", "null");
		}
		if (mode == SYNC_MODE_FULL || mode == SYNC_MODE_HUD) {
			tag.putDouble("shieldAmount", healthManager.getShieldAmount());
			tag.putDouble("energyAmount", healthManager.getEnergyAmount());
		}
		buf.writeNbt(tag);

	}

	@Override
	public void applySyncPacket(PacketByteBuf buf) {
		NbtCompound tag = buf.readNbt();
		if (tag != null) {
			this.readFromNbt(tag);
		}
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		if (forcedPose != null)
			tag.putString("pose", forcedPose.toString());
		if (customPoseID != null)
			tag.putString("customPoseID", customPoseID);
		if (carriedEntity != null)
			tag.put("carriedEntity", carriedEntity);
		var t1 = new NbtCompound();
		healthManager.writeNbt(t1);
		tag.put("health", t1);
	}

	@Override
	public void clientTick() {
		if (customPose != null) {
			customPose.tick(player);
		}
	}

	public static void syncFull(PlayerEntity player) {
		var dt = player.getComponent(Components.PLAYER_DATA);
		Components.PLAYER_DATA.sync(player, (b, p) -> dt.writeSyncPacket(b, p, SYNC_MODE_FULL), (p) -> p == dt.player);
	}

	public static void syncHUD(PlayerEntity player) {
		var dt = player.getComponent(Components.PLAYER_DATA);
		Components.PLAYER_DATA.sync(player, (b, p) -> dt.writeSyncPacket(b, p, SYNC_MODE_HUD), (p) -> p == dt.player);
	}

	public static void syncApparence(PlayerEntity player) {
		var dt = player.getComponent(Components.PLAYER_DATA);
		Components.PLAYER_DATA.sync(player, (b, p) -> dt.writeSyncPacket(b, p, SYNC_MODE_CLOTH));
	}
}
