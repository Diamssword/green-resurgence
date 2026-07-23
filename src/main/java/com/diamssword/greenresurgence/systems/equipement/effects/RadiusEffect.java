package com.diamssword.greenresurgence.systems.equipement.effects;

import com.diamssword.greenresurgence.systems.equipement.AdvEquipmentSlot;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentEffect;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentUpgrade;
import com.diamssword.greenresurgence.systems.equipement.UpgradeActionContext;
import com.diamssword.greenresurgence.systems.equipement.utils.TooltipHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class RadiusEffect implements IEquipmentEffect {
	private final StatusEffect effect;
	private final float radius;
	private final String id;
	private boolean mainOnly = false;

	public RadiusEffect(String id, StatusEffect effect, float radius) {
		this.effect = effect;
		this.radius = radius;
		this.id = id;
	}

	public RadiusEffect workOnMainHandOnly() {
		mainOnly = true;
		return this;
	}

	@Override
	public boolean needTicking() {
		return true;
	}

	@Override
	public void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx) {

	}

	@Override
	public void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction) {
		if(interaction == IEquipmentUpgrade.InteractType.TICK && (!mainOnly || ctx.isMainSlot())) {
			var m = ctx.getLevel(id).getLevel();
			getPlayers(ctx.getPlayerSource(), radius).forEach(p -> {
				p.addStatusEffect(new StatusEffectInstance(effect, 80, (int) m, true, false));
			});
		}
	}

	public List<PlayerEntity> getPlayers(PlayerEntity source, float radius) {
		List<PlayerEntity> list = Lists.<PlayerEntity>newArrayList();

		for(PlayerEntity playerEntity : source.getWorld().getPlayers()) {
			if(playerEntity != source) {
				if(playerEntity.distanceTo(source) <= radius) {
					list.add(playerEntity);
				}
			}

		}
		return list;
	}

	@Override
	public void addTooltips(UpgradeActionContext ctx, AdvEquipmentSlot slot, List<Text> tooltip) {
		if(slot == AdvEquipmentSlot.MAINHAND || slot == AdvEquipmentSlot.DISPLAY) {
			if(ctx.getLevel(id).getLevel() > 0) {
				tooltip.add(TooltipHelper.tooltipEffectWithExtra("radius_effect." + id, ctx.needShowExtra(), TooltipHelper.formatlevel(ctx.getLevel(id).getLevel() + 1), TooltipHelper.formatlevel(ctx.getLevel(id).getLevel() + 1), TooltipHelper.formatlevel(radius)).formatted(Formatting.LIGHT_PURPLE));
			}
		}
	}
}
