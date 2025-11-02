package com.diamssword.greenresurgence.dynamicLight;

import com.diamssword.greenresurgence.GreenResurgence;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CreateDynLightSourceHolder {

	public static final CreateDynLightSourceHolder INSTANCE = new CreateDynLightSourceHolder();
	public static final List<Identifier> coneLights = Arrays.asList(new Identifier[]{GreenResurgence.asRessource("diams_lamp_marine_big_black"), GreenResurgence.asRessource("diams_lamp_marine_big_copper"), GreenResurgence.asRessource("diams_lamp_marine_big_gold"), GreenResurgence.asRessource("diams_lamp_marine_big_metal")});

	private CreateDynLightSourceHolder() {
	}

	AtomicInteger atomicInt = new AtomicInteger(0);
	Map<LightSourceKey, CreateDynLightSource> lightSources = new HashMap<>();
	private final ReentrantReadWriteLock lightSourcesLock = new ReentrantReadWriteLock();

	public CreateDynLightSource create(AbstractContraptionEntity entity, BlockPos blockPos, BlockState state) {
		var id = atomicInt.incrementAndGet();
		var bid = Registries.BLOCK.getId(state.getBlock());
		CreateDynLightSource lightSource = null;
		if(coneLights.contains(bid)) {
			lightSource = new ConeDynLightSource(id, entity, blockPos, state.getLuminance(), getDir(state));
		} else
			lightSource = new CreateDynLightSource(id, entity, blockPos, state.getLuminance());
		lightSourcesLock.writeLock().lock();
		lightSources.put(new LightSourceKey(entity.getId(), blockPos), lightSource);
		lightSourcesLock.writeLock().unlock();
		DynamicLightsManager.INSTANCE.addLightSource(lightSource);
		return lightSource;
	}

	private Direction getDir(BlockState state) {
		for(Property<?> property : state.getProperties()) {
			if(property instanceof DirectionProperty dir) {
				return state.get(dir).getOpposite();
			}
		}
		return Direction.UP;
	}

	@SuppressWarnings("unused")
	public void remove(int entityId, BlockPos blockPos) {
		lightSourcesLock.writeLock().lock();
		var lightSource = lightSources.remove(new LightSourceKey(entityId, blockPos));
		if(lightSource != null) {
			DynamicLightsManager.INSTANCE.removeLightSource(lightSource);
		}
		lightSourcesLock.writeLock().unlock();
	}

	public void removeAll(AbstractContraptionEntity contraptionEntity) {
		var contraption = contraptionEntity.getContraption();
		if(contraption == null)
			return;
		lightSourcesLock.writeLock().lock();
		for(BlockPos blockPos : contraptionEntity.getContraption().getBlocks().keySet()) {
			var lightSource = lightSources.remove(new LightSourceKey(contraptionEntity.getId(), blockPos));
			if(lightSource != null) {
				DynamicLightsManager.INSTANCE.removeLightSource(lightSource);
			}
		}
		lightSourcesLock.writeLock().unlock();
	}

	public void update() {

		lightSourcesLock.writeLock().lock();
		lightSources.forEach((k, l) -> {
			l.update();
		});
		lightSourcesLock.writeLock().unlock();
	}

	public Optional<CreateDynLightSource> get(int entityId, BlockPos blockPos) {
		return get(new LightSourceKey(entityId, blockPos));
	}

	@SuppressWarnings("UnusedReturnValue")
	public CreateDynLightSource getOrCreate(AbstractContraptionEntity entity, BlockPos blockPos, BlockState state) {
		return get(entity.getId(), blockPos).orElseGet(() -> create(entity, blockPos, state));
	}


	public Optional<CreateDynLightSource> get(LightSourceKey key) {
		lightSourcesLock.readLock().lock();
		var lightSource = lightSources.get(key);
		lightSourcesLock.readLock().unlock();
		return Optional.ofNullable(lightSource);
	}

	public static class LightSourceKey {
		private final int entityId;
		private final BlockPos blockPos;

		public LightSourceKey(int entityId, BlockPos blockPos) {
			this.entityId = entityId;
			this.blockPos = blockPos;
		}

		@Override
		public boolean equals(Object o) {
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			LightSourceKey that = (LightSourceKey) o;
			return entityId == that.entityId && Objects.equals(blockPos, that.blockPos);
		}

		@Override
		public int hashCode() {
			return Objects.hash(entityId, blockPos.asLong());
		}
	}
}
