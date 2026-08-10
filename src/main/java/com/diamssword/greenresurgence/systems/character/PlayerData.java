package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.items.equipment.IOffHandAttack;
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
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerData implements ComponentV3, ServerTickingComponent, ClientTickingComponent, AutoSyncedComponent {
	public static int SYNC_MODE_FULL = 0;
	public static int SYNC_MODE_HUD = 1;
	public static int SYNC_MODE_CLOTH = 2;
	private EntityPose forcedPose;
	private Map<String, IPlayerCustomPose> customPoses = new HashMap<>();
	public final PlayerEntity player;
	private NbtCompound carriedEntity;
	public final HealthManager healthManager;
	public float lastCooldownProgress;
	public Hand nextHandSwing = Hand.OFF_HAND;

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
		if(this.carriedEntity != null) {
			getCarriedEntity().ifPresent(et -> player.getWorld().spawnEntity(et));
			this.carriedEntity = null;
		}
	}

	public boolean isCarryingEntity() {
		return this.carriedEntity != null;
	}

	public Optional<Entity> getCarriedEntity() {
		if(this.carriedEntity != null) {
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

	public Collection<IPlayerCustomPose> getCustomPoses() {
		return customPoses.values();
	}

	public Map<String, IPlayerCustomPose> getCustomPosesMap() {
		return customPoses;
	}

	public boolean removeCustomPose(String id) {
		var c = customPoses.remove(id) != null;
		player.calculateDimensions();
		PlayerData.syncApparence(player);
		return c;
	}

	public boolean addCustomPose(String id, boolean resetIfPresent) {
		if(resetIfPresent)
			customPoses.remove(id);
		return addCustomPose(id);
	}

	public boolean addCustomPose(String id) {

		if(!customPoses.containsKey(id)) {

			var n = PosesManager.createPose(id, player);
			if(n != null) {
				List<String> toRemove = new ArrayList<>();
				for(Map.Entry<String, IPlayerCustomPose> pair : customPoses.entrySet()) {
					if(!pair.getValue().canStillPlayWith(n)) {

						if(pair.getValue().priority() <= n.priority()) {
							toRemove.add(pair.getKey());
						} else {
							return false;
						}
					}
				}
				toRemove.forEach(r -> {
					customPoses.remove(r);
				});
				customPoses.put(id, n);

				customPoses = customPoses.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.comparingInt(IPlayerCustomPose::priority))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
				player.calculateDimensions();
				PlayerData.syncApparence(player);
				return true;

			}
		}
		return false;
	}


	@Override
	public void serverTick() {
		healthManager.update();
		List<String> toRemove = new ArrayList<>();
		customPoses.forEach((k, v) -> {
			if(v.shouldExitPose()) {
				toRemove.add(k);
			} else
				v.tick();
		});
		toRemove.forEach(this::removeCustomPose);
	}

	private void recreatePosesMap(List<String> positions) {
		Map<String, IPlayerCustomPose> res = new HashMap<>();
		positions.forEach(p -> {
			var n = PosesManager.createPose(p, this.player);
			if(n != null)
				res.put(p, n);
		});
		customPoses = res.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.comparingInt(IPlayerCustomPose::priority))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
		player.calculateDimensions();
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		if(tag.contains("health"))
			healthManager.readNbt(tag.getCompound("health"));
		nextHandSwing = tag.getBoolean("offHandNext") ? Hand.MAIN_HAND : Hand.OFF_HAND;
		if(tag.contains("pose"))
			forcedPose = EntityPose.valueOf(tag.getString("pose"));
		if(tag.contains("carriedEntity"))
			this.carriedEntity = tag.getCompound("carriedEntity");
		if(tag.contains("customPoses")) {
			var ls = tag.getList("customPoses", NbtElement.STRING_TYPE);
			List<String> poses = new ArrayList<>();
			ls.forEach(k -> {
				poses.add(k.asString());
			});
			recreatePosesMap(poses);

		}
		if(tag.contains("shieldAmount"))
			healthManager.setShieldAmount(tag.getDouble("shieldAmount"));
		if(tag.contains("energyAmount"))
			healthManager.setEnergyAmount(tag.getDouble("energyAmount"));
		if(tag.contains("radiationAmount"))
			healthManager.setcontaminationAmount(tag.getDouble("radiationAmount"));
	}

	@Override
	public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity player) {
		this.writeSyncPacket(buf, player, SYNC_MODE_FULL);
	}

	public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient, int mode) {

		NbtCompound tag = new NbtCompound();
		if(mode == SYNC_MODE_FULL || mode == SYNC_MODE_CLOTH) {
			tag.putBoolean("offHandNext", nextHandSwing == Hand.OFF_HAND);
			if(forcedPose != null)
				tag.putString("pose", forcedPose.toString());
			if(carriedEntity != null)
				tag.put("carriedEntity", carriedEntity);
			var ls = new NbtList();
			customPoses.keySet().forEach(k -> ls.add(NbtString.of(k)));
			tag.put("customPoses", ls);

		}
		if(mode == SYNC_MODE_FULL || mode == SYNC_MODE_HUD) {
			tag.putDouble("shieldAmount", healthManager.getShieldAmount());
			tag.putDouble("energyAmount", healthManager.getEnergyAmount());
			tag.putDouble("radiationAmount", healthManager.getContaminationAmount());
		}
		buf.writeNbt(tag);

	}

	@Override
	public void applySyncPacket(PacketByteBuf buf) {
		NbtCompound tag = buf.readNbt();
		if(tag != null) {
			this.readFromNbt(tag);
		}
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		if(forcedPose != null)
			tag.putString("pose", forcedPose.toString());
		var ls = new NbtList();
		customPoses.keySet().forEach(k -> ls.add(NbtString.of(k)));
		tag.put("customPoses", ls);
		if(carriedEntity != null)
			tag.put("carriedEntity", carriedEntity);
		var t1 = new NbtCompound();
		healthManager.writeNbt(t1);
		tag.put("health", t1);
	}

	@Override
	public void clientTick() {
		healthManager.update();
		customPoses.values().forEach(IPlayerCustomPose::tick);
		if(player.getOffHandStack().getItem() instanceof IOffHandAttack)
			player.preferredHand = nextHandSwing;
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
