package com.diamssword.greenresurgence.gui.playerContainers.survivalInventory;

import com.diamssword.greenresurgence.containers.player.CustomPlayerInventory;
import com.diamssword.greenresurgence.gui.components.AdaptiveLabelComponent;
import com.diamssword.greenresurgence.gui.components.EffectComponent;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.playerContainers.PlayerBasedGui;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerInventoryGui extends PlayerBasedGui<CustomPlayerInventory.VanillaPlayerInvMokup> {

	private FreeRowGridLayout gridBonus;
	private FreeRowGridLayout gridMalus;
	private final Map<StatusEffect, EffectComponent> activeBonus = new HashMap<>();
	private final Map<StatusEffect, EffectComponent> activeMalus = new HashMap<>();
	private final DynamicPanels panels = new DynamicPanels();

	public PlayerInventoryGui(CustomPlayerInventory.VanillaPlayerInvMokup handler, PlayerInventory inv, Text title) {
		super(handler, "survival/player_stats", true);
		openSubPanelOnLoad = true;
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		super.build(rootComponent);
		panels.init(rootComponent, this);
		gridBonus = rootComponent.childById(FreeRowGridLayout.class, "bonusLayout");
		gridMalus = rootComponent.childById(FreeRowGridLayout.class, "malusLayout");
		rootComponent.child(panels.getControlPanel().positioning(Positioning.relative(0, 100)));

		var offHandFlow = rootComponent.childById(FlowLayout.class, "flowOffhand");
		var extraSLay = rootComponent.childById(FlowLayout.class, "extraSlotsLayout");
		if(extraSLay != null)
			extraSLay.padding().set(Insets.of(18, 0, 0, 2));

		if(offHandFlow != null) {
			offHandFlow.horizontalSizing(Sizing.fixed(48));
		}
	}

	private AdaptiveLabelComponent.SyncedScale scaler = new AdaptiveLabelComponent.SyncedScale();
	private AdaptiveLabelComponent.SyncedScale scaler1 = new AdaptiveLabelComponent.SyncedScale();

	private void drawStatusEffect() {
		if(gridBonus != null && gridMalus != null) {
			Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
			drawEffects(collection.stream().filter(v -> v.getEffectType().isBeneficial()).collect(Collectors.toList()), gridBonus, activeBonus);
			drawEffects(collection.stream().filter(v -> !v.getEffectType().isBeneficial()).collect(Collectors.toList()), gridMalus, activeMalus);
		}
	}

	private void drawEffects(Collection<StatusEffectInstance> effects, FreeRowGridLayout grid, Map<StatusEffect, EffectComponent> tracker) {
		var d1 = effects.stream().map(StatusEffectInstance::getEffectType).toList();
		var it = tracker.entrySet().iterator();
		while(it.hasNext()) {
			var a = it.next();
			if(!d1.contains(a.getKey())) {
				a.getValue().remove();
				it.remove();
			}
		}
		for(var eff : effects) {
			if(!tracker.containsKey(eff.getEffectType())) {
				var d = new EffectComponent(eff);
				d.sizing(Sizing.fixed(22));
				d.margins(Insets.of(1));
				tracker.put(eff.getEffectType(), d);
				grid.child(d);
			}

			//	grid.removeChild();
		}
		if(grid.children().isEmpty())
			grid.verticalSizing(Sizing.fixed(24));
		else
			grid.verticalSizing(Sizing.content());
	}

	@Override
	protected void updateLayout(FlowLayout rootComponent) {
		super.updateLayout(rootComponent);
		if(rightPane != null) {
			var w = rightPane.horizontalSizing().get().value;
			var cols = Math.floor(w / 24f);
			if(gridMalus != null)
				gridMalus.setColumns((int) cols);
			if(gridBonus != null)
				gridBonus.setColumns((int) cols);
			drawStatusEffect();
		}

	}

	@Override
	protected void handledScreenTick() {
		super.handledScreenTick();
		drawStatusEffect();
		panels.tick();

	}

	@Override
	public void close() {
		super.close();
		this.panels.onClose();
	}

	@Override
	protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {
	}
}
