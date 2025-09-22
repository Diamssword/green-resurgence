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
					if(!wep.customPoseId().equals(comp.getCustomPoseID())) {comp.setCustomPose(wep.customPoseId());}
				}
			}
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((old, newP, wasAlive) -> {
			newP.getComponent(Components.PLAYER_DATA).healthManager.onRespawn(wasAlive);
		});
	}
}
