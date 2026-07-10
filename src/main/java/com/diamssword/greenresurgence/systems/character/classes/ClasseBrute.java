package com.diamssword.greenresurgence.systems.character.classes;

import com.diamssword.characters.api.ComponentManager;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.PosesPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.attributs.Attributes;
import com.diamssword.greenresurgence.systems.character.PosesManager;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class ClasseBrute extends com.diamssword.characters.api.stats.StatsRole {

	public ClasseBrute(String id, JsonObject data) {
		super(id, data);
	}


	@Override
	public void init() {
		create(2, (t) -> {
			t.addModifier(Attributes.PLAYER_KNOCKBACK, Attributes.modifier(Attributes.PLAYER_KNOCKBACK, 0.2f, EntityAttributeModifier.Operation.ADDITION, "a24691b4-f566-47b3-9b5f-87f2a75e7d26"));
			t.addModifier(Attributes.ALCOOL_RESISTANCE, Attributes.modifier(Attributes.ALCOOL_RESISTANCE, 0.1f, EntityAttributeModifier.Operation.ADDITION, "4b30e26f-7419-4804-8ebd-2774b8c05d16"));
		});
		create(4, (t) -> {
			t.addModifier(Attributes.PLAYER_KNOCKBACK, Attributes.modifier(Attributes.PLAYER_KNOCKBACK, 0.4f, EntityAttributeModifier.Operation.ADDITION, "dac758b2-6ebc-4130-b3ba-bce338847fc3"));
		});
		//150% base health at level 100
		addGlobalModifier(EntityAttributes.GENERIC_MAX_HEALTH, (l) -> Attributes.modifier(EntityAttributes.GENERIC_MAX_HEALTH, l * 0.005f, EntityAttributeModifier.Operation.MULTIPLY_BASE, "d74f136e-f21d-4655-87a6-356214a8695c"));
		addGlobalModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, (l) -> Attributes.modifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, l * 0.005f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL, "647fadf7-a149-4f30-9a31-21f4f9b04379"));
		addGlobalModifier(Attributes.CRAFT_SPEED, (l) -> Attributes.modifier(Attributes.CRAFT_SPEED, l * 0.01f, EntityAttributeModifier.Operation.ADDITION, "ece84c00-7a87-4d63-bb82-2e4af747904a"));
		eventsRegister();
	}

	@Override
	public void onLevelChange(PlayerEntity pl, int level) {
		super.onLevelChange(pl, level);
	}

	private void eventsRegister() {
		UseBlockCallback.EVENT.register((p, w, h, r) -> {
			if(p.hasPassengers()) {
				p.getFirstPassenger().dismountVehicle();
				return ActionResult.SUCCESS;
			} else if(!w.isClient) {
				var d = p.getComponent(Components.PLAYER_DATA);
				if(d.isCarryingEntity())
					d.placeCarriedEntity();
			}
			return ActionResult.PASS;
		});
		ServerLivingEntityEvents.AFTER_DEATH.register((e, i) -> {
			if(e instanceof ServerPlayerEntity player) {
				player.getComponent(Components.PLAYER_DATA).placeCarriedEntity();
			}

		});
		UseEntityCallback.EVENT.register((pl, world, hand, ent, hit) -> {

			if(!world.isClient && pl.isSneaking()) {
				var st = ComponentManager.getPlayerDatas(pl).getStats().getPalier(id);
				if(st >= 1) {
					if(ent instanceof PlayerEntity pl1) {
						pl1.startRiding(pl, true);
						Channels.MAIN.serverHandle(pl).send(new PosesPackets.LiftOtherPlayer(pl1.getUuid(), false));
						Channels.MAIN.serverHandle(pl1).send(new PosesPackets.LiftOtherPlayer(pl.getUuid(), true));
						pl1.getComponent(Components.PLAYER_DATA).setCustomPose("carried");
						return ActionResult.SUCCESS;
					} else if(ent.getWidth() < 1f && ent instanceof LivingEntity li) {
						if(li.getGroup() == EntityGroup.DEFAULT || li.getGroup() == EntityGroup.AQUATIC) {
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
