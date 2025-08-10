package com.diamssword.greenresurgence.systems.character.stats.classes;

import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.PosesPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import com.diamssword.greenresurgence.systems.character.stats.StatsRole;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

public class ClasseBrute extends StatsRole {

	public ClasseBrute(String id, JsonObject data) {
		super(id, data);
	}


	@Override
	public void init() {
		create(2, (t) -> {
			t.addModifier(Attributes.PLAYER_KNOCKBACK, 0.2f, EntityAttributeModifier.Operation.ADDITION);
			t.addModifier(Attributes.ALCOOL_RESISTANCE, 0.1f, EntityAttributeModifier.Operation.ADDITION);
		});
		create(4, (t) -> {
			t.addModifier(Attributes.PLAYER_KNOCKBACK, 0.4f, EntityAttributeModifier.Operation.ADDITION);
		});
		addGlobalModifier(EntityAttributes.GENERIC_MAX_HEALTH, (l) -> StatsRole.modifier(EntityAttributes.GENERIC_MAX_HEALTH, l * 0.005f, EntityAttributeModifier.Operation.MULTIPLY_BASE));
		addGlobalModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, (l) -> StatsRole.modifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, l * 0.005f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
		addGlobalModifier(Attributes.CRAFT_SPEED, (l) -> StatsRole.modifier(Attributes.CRAFT_SPEED, l * 0.01f, EntityAttributeModifier.Operation.ADDITION));
		eventsRegister();
	}


	private void eventsRegister() {
		UseBlockCallback.EVENT.register((p, w, h, r) -> {
			if (p.hasPassengers()) {
				p.getFirstPassenger().dismountVehicle();
				return ActionResult.SUCCESS;
			} else if (!w.isClient) {
				var d = p.getComponent(Components.PLAYER_DATA);
				if (d.isCarryingEntity())
					d.placeCarriedEntity();
			}
			return ActionResult.PASS;
		});
		UseEntityCallback.EVENT.register((pl, world, hand, ent, hit) -> {

			if (!world.isClient && pl.isSneaking()) {
				var st = pl.getComponent(Components.PLAYER_DATA).stats.getPalier(id);
				if (st >= 1) {
					if (ent instanceof PlayerEntity pl1) {
						pl1.startRiding(pl, true);
						Channels.MAIN.serverHandle(pl).send(new PosesPackets.LiftOtherPlayer(pl1.getUuid(), false));
						Channels.MAIN.serverHandle(pl1).send(new PosesPackets.LiftOtherPlayer(pl.getUuid(), true));
						pl1.getComponent(Components.PLAYER_DATA).setCustomPose("carried");
						return ActionResult.SUCCESS;
					} else if (ent.getWidth() < 1f && ent instanceof LivingEntity li) {
						if (li.getGroup() == EntityGroup.DEFAULT || li.getGroup() == EntityGroup.AQUATIC) {
							var c = pl.getComponent(Components.PLAYER_DATA);
							c.setCarriedEntity(ent);
							c.setCustomPose(PosesManager.CARRYINGENTITY);
							return ActionResult.SUCCESS;
						}
					}
				}
			}
			return ActionResult.PASS;
		});
	}
}
