package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.events.PlayerTickEvent;
import com.diamssword.greenresurgence.items.equipment.ICustomPoseWeapon;
import com.diamssword.greenresurgence.items.helpers.ISimpleEnergyItemTiered;
import com.diamssword.greenresurgence.systems.Components;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class PlayerEvents {

	public static void init() {
		PlayerTickEvent.onTick.register((pl, end) -> {
			if(!end) {
				var comp = pl.getComponent(Components.PLAYER_DATA);
				if(!pl.getWorld().isClient) {
					var stack = pl.getMainHandStack();
					if(stack.getItem() instanceof ICustomPoseWeapon wep) {
						if(wep.shouldRemoveOffHand() && !pl.getOffHandStack().isEmpty() && !(pl.getOffHandStack().getItem() instanceof ISimpleEnergyItemTiered)) {
							var st = pl.getOffHandStack().copyAndEmpty();
							if(!pl.giveItemStack(st)) {pl.dropStack(st);}
						}
						var pose = wep.customPoseId(stack);
						if(pose != null)
							comp.addCustomPose(pose);
					}
				}

			}

		});
		ServerPlayerEvents.AFTER_RESPAWN.register((old, newP, wasAlive) -> {
			newP.getComponent(Components.PLAYER_DATA).healthManager.onRespawn(wasAlive);
		});
	}
}
