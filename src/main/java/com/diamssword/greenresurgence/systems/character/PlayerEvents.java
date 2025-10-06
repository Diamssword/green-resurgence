package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.events.PlayerTickEvent;
import com.diamssword.greenresurgence.items.SimpleEnergyItemTiered;
import com.diamssword.greenresurgence.items.weapons.ICustomPoseWeapon;
import com.diamssword.greenresurgence.systems.Components;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class PlayerEvents {

	public static void init() {
		PlayerTickEvent.onTick.register((pl, end) -> {
			if(!end && !pl.getWorld().isClient) {
				if(pl.getMainHandStack().getItem() instanceof ICustomPoseWeapon wep) {
					if(wep.shouldRemoveOffHand() && !pl.getOffHandStack().isEmpty() && !(pl.getOffHandStack().getItem() instanceof SimpleEnergyItemTiered)) {
						var st = pl.getOffHandStack().copyAndEmpty();
						if(!pl.giveItemStack(st)) {pl.dropStack(st);}
					}
					var comp = pl.getComponent(Components.PLAYER_DATA);
					var pose = wep.customPoseId(pl.getMainHandStack());
					if(pose != null && !pose.equals(comp.getCustomPoseID())) {comp.setCustomPose(pose);}
				}
			}
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((old, newP, wasAlive) -> {
			newP.getComponent(Components.PLAYER_DATA).healthManager.onRespawn(wasAlive);
		});
	}
}
