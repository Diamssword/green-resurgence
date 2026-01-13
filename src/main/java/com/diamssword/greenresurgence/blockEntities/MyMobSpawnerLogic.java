package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.MItems;
import com.mojang.logging.LogUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

public abstract class MyMobSpawnerLogic {
	private static final Logger LOGGER = LogUtils.getLogger();
	private DataPool<MobSpawnerEntry> spawnPotentials = DataPool.<MobSpawnerEntry>empty();
	private int cooldown = 10000;
	private long lastSpawn = -1;
	private int spawnCount = 4;
	private int maxNearbyEntities = 6;
	private int requiredPlayerRange = 16;
	private int spawnRange = 4;
	private int spawnHeight = 1;
	private boolean floorCheck = true;

	public void setEntityId(EntityType<?> type, @Nullable World world, Random random, BlockPos pos) {
		this.getSpawnEntry(world, random, pos).getNbt().putString("id", Registries.ENTITY_TYPE.getId(type).toString());
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public long getLastSpawn() {
		return lastSpawn;
	}

	public void setLastSpawn(long lastSpawn) {
		this.lastSpawn = lastSpawn;
	}

	public int getSpawnCount() {
		return spawnCount;
	}

	public void setSpawnCount(int spawnCount) {
		this.spawnCount = spawnCount;
	}

	public int getSpawnRange() {
		return spawnRange;
	}

	public void setSpawnRange(int spawnRange) {
		this.spawnRange = spawnRange;
	}

	public int getSpawnHeight() {
		return spawnHeight;
	}

	public void setSpawnHeight(int height) {
		this.spawnHeight = height;
	}

	public int getRequiredPlayerRange() {
		return requiredPlayerRange;
	}

	public void setRequiredPlayerRange(int requiredPlayerRange) {
		this.requiredPlayerRange = requiredPlayerRange;
	}

	public int getMaxNearbyEntities() {
		return maxNearbyEntities;
	}

	public void setMaxNearbyEntities(int maxNearbyEntities) {
		this.maxNearbyEntities = maxNearbyEntities;
	}

	public boolean isFloorCheck() {
		return floorCheck;
	}

	public void setFloorCheck(boolean floorCheck) {
		this.floorCheck = floorCheck;
	}

	private boolean isPlayerInRange(World world, BlockPos pos) {
		return world.isPlayerInRange(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, this.requiredPlayerRange);
	}

	public void setEntries(DataPool<MobSpawnerEntry> mobs) {
		this.spawnPotentials = mobs;
	}

	private boolean closeToGround(World w, BlockPos pos) {

		if(w.isAir(pos)) {
			return !w.isAir(pos.down(2)) || !w.isAir(pos.down(1));
		} else return !w.getFluidState(pos).isEmpty();
	}

	public int findGround(double x, double y, double z, World world) {
		var bp = BlockPos.ofFloored(x, y, z);
		if(closeToGround(world, bp))
			return 0;
		for(var i = 1; i < 10; i += 2) {
			if(closeToGround(world, bp.up(i))) {
				return i;
			} else if(closeToGround(world, bp.down(i))) {
				return -i;
			}
		}
		return -100;
	}

	public void serverTick(ServerWorld world, BlockPos pos) {
		if(this.isPlayerInRange(world, pos)) {
			if(this.lastSpawn == -1) {
				this.updateSpawns(world, pos);
			}
			if(this.lastSpawn + this.cooldown < System.currentTimeMillis()) {
				boolean bl = false;
				Random random = world.getRandom();
				int spawned = 0;
				for(int i = 0; i < this.spawnCount * 10; i++) {
					MobSpawnerEntry mobSpawnerEntry = this.getSpawnEntry(world, random, pos);
					NbtCompound nbtCompound = mobSpawnerEntry.getNbt();
					Optional<EntityType<?>> optional = EntityType.fromNbt(nbtCompound);

					if(optional.isEmpty()) {
						this.updateSpawns(world, pos);
						return;
					}
					NbtList nbtList = nbtCompound.getList("Pos", NbtElement.DOUBLE_TYPE);
					int j = nbtList.size();
					double d = j >= 1 ? nbtList.getDouble(0) : pos.getX() + ((random.nextDouble() - random.nextDouble()) * this.spawnRange) + 0.5;
					double e1 = j >= 2 ? nbtList.getDouble(1) : pos.getY() + ((random.nextDouble() - random.nextDouble()) * this.spawnHeight) + 0.5;
					double f = j >= 3 ? nbtList.getDouble(2) : pos.getZ() + ((random.nextDouble() - random.nextDouble()) * this.spawnRange) + 0.5;
					double e;
					if(this.floorCheck) {
						double e2 = findGround(d, e1, f, world);

						if(e2 == -100)
							continue;
						e = e1 + e2;
					} else {e = e1;}
					if(world.isSpaceEmpty(((EntityType<?>) optional.get()).createSimpleBoundingBox(d, e, f))) {
						BlockPos blockPos = BlockPos.ofFloored(d, e, f);
						if(mobSpawnerEntry.getCustomSpawnRules().isPresent()) {
							if(!((EntityType<?>) optional.get()).getSpawnGroup().isPeaceful() && world.getDifficulty() == Difficulty.PEACEFUL) {

								continue;
							}
							MobSpawnerEntry.CustomSpawnRules customSpawnRules = (MobSpawnerEntry.CustomSpawnRules) mobSpawnerEntry.getCustomSpawnRules().get();
							if(!customSpawnRules.blockLightLimit().contains(world.getLightLevel(LightType.BLOCK, blockPos))
									|| !customSpawnRules.skyLightLimit().contains(world.getLightLevel(LightType.SKY, blockPos))) {
								continue;
							}
						}/* else if(!SpawnRestriction.canSpawn((EntityType) optional.get(), world, SpawnReason.SPAWNER, blockPos, world.getRandom())) {
							//NO RESTRICTIONS! MWUAHAHAH
									continue;
						}*/

						Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, world, entityx -> {
							entityx.refreshPositionAndAngles(d, e, f, entityx.getYaw(), entityx.getPitch());
							return entityx;
						});
						if(nbtCompound.contains("EntityTag") && nbtCompound.getCompound("EntityTag").getBoolean("customEgg")) {
							MItems.CUSTOM_SPAWN_EGG.setEntityCustomData(entity, nbtCompound.getCompound("EntityTag"));
						}
						if(entity == null) {
							this.updateSpawns(world, pos);
							return;
						}

						int k = world.getNonSpectatingEntities(entity.getClass(), new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).expand(this.spawnRange, this.spawnHeight, this.spawnRange)).size();
						if(k >= this.maxNearbyEntities) {
							continue;
						}
						entity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), random.nextFloat() * 360.0F, 0.0F);
						if(entity instanceof MobEntity mobEntity) {
							/*if(mobSpawnerEntry.getCustomSpawnRules().isEmpty() && !mobEntity.canSpawn(world, SpawnReason.SPAWNER) || !mobEntity.canSpawn(world)) {

								continue;
							}*/

							if(mobSpawnerEntry.getNbt().getSize() == 1 && mobSpawnerEntry.getNbt().contains("id", NbtElement.STRING_TYPE)) {
								((MobEntity) entity).initialize(world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.SPAWNER, null, null);
							}
						}

						if(!world.spawnNewEntityAndPassengers(entity)) {
							continue;
						}

						world.syncWorldEvent(WorldEvents.SPAWNER_SPAWNS_MOB, pos, 0);
						world.emitGameEvent(entity, GameEvent.ENTITY_PLACE, blockPos);
						if(entity instanceof MobEntity) {
							((MobEntity) entity).playSpawnEffects();
						}
						spawned++;
						if(spawned >= this.spawnCount) {
							this.updateSpawns(world, pos);
							return;
						}
						bl = true;
					}
				}

				if(bl) {
					this.updateSpawns(world, pos);
				}
			}
		}
	}

	private void updateSpawns(World world, BlockPos pos) {
		this.lastSpawn = System.currentTimeMillis();
		this.sendStatus(world, pos, 1);
	}

	public void readNbt(@Nullable World world, BlockPos pos, NbtCompound nbt) {
		this.lastSpawn = nbt.getLong("LastSpawn");
		if(nbt.contains("Cooldown", NbtElement.NUMBER_TYPE)) {
			this.cooldown = nbt.getInt("Cooldown");
			this.spawnCount = nbt.getInt("SpawnCount");
		}

		if(nbt.contains("MaxNearbyEntities", NbtElement.NUMBER_TYPE)) {
			this.maxNearbyEntities = nbt.getInt("MaxNearbyEntities");
			this.requiredPlayerRange = nbt.getInt("RequiredPlayerRange");
		}

		if(nbt.contains("SpawnRange", NbtElement.NUMBER_TYPE)) {
			this.spawnRange = nbt.getInt("SpawnRange");
		}
		if(nbt.contains("SpawnHeight", NbtElement.NUMBER_TYPE)) {
			this.spawnHeight = nbt.getInt("SpawnHeight");
		}
		this.floorCheck = nbt.getBoolean("FloorCheck");
	}

	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putInt("Cooldown", this.cooldown);
		nbt.putLong("lastSpawn", this.lastSpawn);
		nbt.putInt("SpawnCount", this.spawnCount);
		nbt.putInt("MaxNearbyEntities", this.maxNearbyEntities);
		nbt.putInt("RequiredPlayerRange", this.requiredPlayerRange);
		nbt.putInt("SpawnRange", this.spawnRange);
		nbt.putInt("SpawnHeight", this.spawnHeight);
		nbt.putBoolean("FloorCheck", this.floorCheck);

		//nbt.put("SpawnPotentials", (NbtElement) MobSpawnerEntry.DATA_POOL_CODEC.encodeStart(NbtOps.INSTANCE, this.spawnPotentials).result().orElseThrow());
		return nbt;
	}


	public boolean handleStatus(World world, int status) {
		if(status == 1) {
			if(world.isClient) {
				//	this.spawnDelay = this.minSpawnDelay;
			}

			return true;
		} else {
			return false;
		}
	}

	private MobSpawnerEntry getSpawnEntry(@Nullable World world, Random random, BlockPos pos) {
		return this.spawnPotentials.getOrEmpty(random).map(Weighted.Present::getData).orElseGet(MobSpawnerEntry::new);
	}

	public abstract void sendStatus(World world, BlockPos pos, int status);

}
