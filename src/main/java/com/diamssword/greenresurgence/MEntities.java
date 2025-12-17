package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.entities.BackpackEntity;
import com.diamssword.greenresurgence.entities.BikeEntity;
import com.diamssword.greenresurgence.entities.ChairEntity;
import com.diamssword.greenresurgence.entities.TwoPassengerVehicle;
import io.wispforest.owo.registration.reflect.EntityRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

public class MEntities implements EntityRegistryContainer {
	public static final EntityType<Entity> CHAIR = FabricEntityTypeBuilder.create(SpawnGroup.MISC, ChairEntity::new).dimensions(EntityDimensions.fixed(0.1f, 0.1f)).build();
	public static final EntityType<BackpackEntity> BACKPACK = FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<BackpackEntity>) BackpackEntity::new).dimensions(EntityDimensions.fixed(0.8f, 0.4f)).build();
	public static final EntityType<TwoPassengerVehicle> CADDIE = FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<TwoPassengerVehicle>) TwoPassengerVehicle::new).dimensions(EntityDimensions.fixed(1f, 1.2f)).build();
	public static final EntityType<BikeEntity> BIKE = FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<BikeEntity>) BikeEntity::new).fireImmune().dimensions(EntityDimensions.fixed(1f, 0.99f)).build();

	public static void addAtributs() {
		FabricDefaultAttributeRegistry.register(BIKE, BikeEntity.createAttributes());
	}


}
