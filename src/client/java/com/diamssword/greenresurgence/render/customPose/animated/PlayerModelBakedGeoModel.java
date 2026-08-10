package com.diamssword.greenresurgence.render.customPose.animated;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreBakedGeoModel;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerModelBakedGeoModel implements CoreBakedGeoModel {

	List<GeoBone> topLevelBones = new ArrayList<>();

	public PlayerModelBakedGeoModel(PlayerEntityModel<AbstractClientPlayerEntity> model) {
		topLevelBones.add(new ModelPartBackedGeoBone("bipedHead", model.head, false));
		topLevelBones.add(new ModelPartBackedGeoBone("bipedBody", model.body, true));
		topLevelBones.add(new ModelPartBackedGeoBone("bipedRightArm", model.rightArm, false));
		topLevelBones.add(new ModelPartBackedGeoBone("bipedLeftArm", model.leftArm, false));
		topLevelBones.add(new ModelPartBackedGeoBone("bipedLeftLeg", model.leftLeg, false));
		topLevelBones.add(new ModelPartBackedGeoBone("bipedRightLeg", model.rightLeg, false));
	}

	@Override
	public List<? extends CoreGeoBone> getBones() {
		return topLevelBones;
	}

	@Override
	public Optional<GeoBone> getBone(String name) {
		for(GeoBone bone : this.topLevelBones) {
			CoreGeoBone childBone = searchForChildBone(bone, name);

			if(childBone != null)
				return Optional.of((GeoBone) childBone);
		}

		return Optional.empty();
	}
}
