package com.diamssword.greenresurgence.utils;


import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.keyframe.event.SoundKeyframeEvent;

/**
 * Built-in helper for a {@link software.bernie.geckolib.animation.AnimationController.SoundKeyframeHandler SoundKeyframeHandler} that automatically plays the sound defined in the keyframe data
 * <p>
 * Due to an inability to determine the position of the sound for all animatables, this handler only supports {@link software.bernie.geckolib.animatable.GeoEntity GeoEntity} and {@link software.bernie.geckolib.animatable.GeoBlockEntity GeoBlockEntity}
 * <p>
 * The expected keyframe data format is one of the below:
 * <pre>{@code
 * namespace:soundid
 * namespace:soundid|volume|pitch
 * }</pre>
 */
public class AutoPlayingSoundKeyframeHandler<A extends GeoAnimatable> implements AnimationController.SoundKeyframeHandler<A> {
	@Override
	public void handle(SoundKeyframeEvent<A> event) {
		String[] segments = event.getKeyframeData().getSound().split("\\|");
		SoundEvent sound = Registries.SOUND_EVENT.get(Identifier.validate(segments[0]).getOrThrow(false, System.err::println));
		if(sound != null) {
			Entity entity = event.getAnimatable() instanceof Entity e ? e : null;
			Vec3d position = entity != null ? entity.getPos() : event.getAnimatable() instanceof BlockEntity blockEntity ? blockEntity.getPos().toCenterPos() : null;

			if(position != null) {
				float volume = segments.length > 1 ? Float.parseFloat(segments[1]) : 1;
				float pitch = segments.length > 2 ? Float.parseFloat(segments[2]) : 1;
				SoundCategory source = entity == null ? SoundCategory.BLOCKS : entity instanceof Monster ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
				GreenResurgence.clientHelper.getPlayer().getWorld().playSound(position.x, position.y, position.z, sound, source, volume, pitch, false);
			}
		}
	}
}