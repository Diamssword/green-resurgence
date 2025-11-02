package com.diamssword.greenresurgence.dynamicLight;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ContraptionEntityEventHandler {
	private static final List<AbstractContraptionEntity> scheduledToAddContraptionEntities = new ArrayList<>();
	private static boolean lastDynamicLightEnabled = true;//CreateDynLightAllConfigs.client.enableDynamicLight.get();
	private static final List<AbstractContraptionEntity> contraptionEntities = new ArrayList<>();

	public static void onContraptionEntityJoin(AbstractContraptionEntity contraptionEntity) {
		contraptionEntities.add(contraptionEntity);
		Contraption contraption = contraptionEntity.getContraption();
		if(contraption != null) {
			addLightSourcesOfContraption(contraption);
			return;
		}

		scheduledToAddContraptionEntities.add(contraptionEntity);
	}

	public static void onContraptionEntityLeave(AbstractContraptionEntity contraptionEntity) {
		contraptionEntities.remove(contraptionEntity);
		CreateDynLightSourceHolder.INSTANCE.removeAll(contraptionEntity);
	}

	private static void addLightSourcesOfContraption(Contraption contraption) {
		var blocks = contraption.getBlocks();
		blocks.forEach((pos, blockInfo) -> {
			if(blockInfo.state().getLuminance() > 0) {
				CreateDynLightSourceHolder.INSTANCE.getOrCreate(contraption.entity, blockInfo.pos(), blockInfo.state());
			}
		});
	}

	public static void onTick(World level) {
		var toRemove = new ArrayList<AbstractContraptionEntity>();
		for(AbstractContraptionEntity entity : scheduledToAddContraptionEntities) {
			Contraption contraption = entity.getContraption();
			if(contraption != null) {
				addLightSourcesOfContraption(contraption);
				toRemove.add(entity);
			}
		}
		scheduledToAddContraptionEntities.removeAll(toRemove);
		CreateDynLightSourceHolder.INSTANCE.update();
		/*var curEnabled = CreateDynLightAllConfigs.client.enableDynamicLight.get();
		if(curEnabled != lastDynamicLightEnabled) {
			onDynamicLightEnabledChanged();
			lastDynamicLightEnabled = curEnabled;
		}*/
	}
/*
	public static void onDynamicLightEnabledChanged() {
		var curEnabled = CreateDynLightAllConfigs.client.enableDynamicLight.get();
		if(curEnabled) {
			for(AbstractContraptionEntity entity : contraptionEntities) {
				Contraption contraption = entity.getContraption();
				if(contraption != null) {
					addLightSourcesOfContraption(contraption);
				}
			}
		} else {
			scheduledToAddContraptionEntities.clear();
			for(AbstractContraptionEntity entity : contraptionEntities) {
				CreateDynLightSourceHolder.INSTANCE.removeAll(entity);
			}
		}
	}
 */
}
