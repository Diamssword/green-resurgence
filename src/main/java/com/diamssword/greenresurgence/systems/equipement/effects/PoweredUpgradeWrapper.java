package com.diamssword.greenresurgence.systems.equipement.effects;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.equipement.AdvEquipmentSlot;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentEffect;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentUpgrade;
import com.diamssword.greenresurgence.systems.equipement.UpgradeActionContext;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class PoweredUpgradeWrapper implements IEquipmentEffect {
	private final IEquipmentEffect wrapped;

	public PoweredUpgradeWrapper(IEquipmentEffect wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public void getAttributeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> map, AdvEquipmentSlot slot, UpgradeActionContext ctx) {
		if(ctx.isPowered() || slot == AdvEquipmentSlot.DISPLAY) {
			this.wrapped.getAttributeModifiers(map, slot, ctx);
		}
	}

	@Override
	public void onInteraction(UpgradeActionContext ctx, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction) {
		if(ctx.isPowered()) {
			wrapped.onInteraction(ctx, slot, interaction);
		}
	}

	@Override
	public void addTooltips(UpgradeActionContext ctx, AdvEquipmentSlot slot, List<Text> tooltip) {
		var ls1 = new ArrayList<Text>();
		if(wrapped instanceof SimpleAttributeEffect atf && atf.attribute == EntityAttributes.GENERIC_ATTACK_DAMAGE) {
			var ex = ctx.needShowExtra();
			ctx = new UpgradeActionContext(ctx.getLivingSource(), ctx.getTarget(), UpgradeActionContext.ItemContext.UPGRADE, ctx.isMainSlot()).setLevels(ctx.getLevels()).setPowered(ctx.isPowered());
			if(ex)
				ctx.setShowExtra();

		}
		UpgradeActionContext finalCtx = ctx;
		wrapped.addTooltips(finalCtx, slot, ls1);
		ls1.forEach(l -> {
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.tooltip.powered_wrapper").formatted(finalCtx.isPowered() ? Formatting.GREEN : Formatting.RED).append(l));
		});
	}
}
