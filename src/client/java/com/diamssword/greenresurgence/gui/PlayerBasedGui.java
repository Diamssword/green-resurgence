package com.diamssword.greenresurgence.gui;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.gui.components.EffectComponent;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.PlayerComponent;
import com.diamssword.greenresurgence.gui.components.SubScreenLayout;
import com.diamssword.greenresurgence.systems.Components;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerBasedGui<T extends MultiInvScreenHandler> extends MultiInvHandledScreen<T, FlowLayout> {
	public final String subscreen;
	private FreeRowGridLayout gridBonus;
	private FreeRowGridLayout gridMalus;
	private final Map<StatusEffect, EffectComponent> activeBonus = new HashMap<>();
	private final Map<StatusEffect, EffectComponent> activeMalus = new HashMap<>();

	public PlayerBasedGui(T handler, String subscreen) {
		super(handler, FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("survival/player_inventory")));
		this.subscreen = subscreen;
	}


	@Override
	protected void build(FlowLayout rootComponent) {
		var sub = rootComponent.childById(SubScreenLayout.class, "subcontainer");
		if (sub != null) {
			sub.setLayout(subscreen);
			rootComponent.onChildMutated(sub);
		}
		gridBonus = rootComponent.childById(FreeRowGridLayout.class, "bonusLayout");
		gridMalus = rootComponent.childById(FreeRowGridLayout.class, "malusLayout");
		var playerComp = rootComponent.childById(PlayerComponent.class, "playerSkin");
		var player = playerComp.entity();
		var cp = new NbtCompound();
		MinecraftClient.getInstance().player.getComponent(Components.PLAYER_DATA).writeToNbt(cp);
		var dt = player.getComponent(Components.PLAYER_DATA);
		dt.readFromNbt(cp);
		var dt1 = player.getComponent(Components.PLAYER_INVENTORY);
		dt1.setBackpackStack(MinecraftClient.getInstance().player.getComponent(Components.PLAYER_INVENTORY).getBackpackStack());
		dt.appearance.refreshSkinData();
	}

	@Override
	protected void handledScreenTick() {
		super.handledScreenTick();
		if (gridBonus != null && gridMalus != null) {
			Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
			drawEffects(collection.stream().filter(v -> v.getEffectType().isBeneficial()).collect(Collectors.toList()), gridBonus, activeBonus);
			drawEffects(collection.stream().filter(v -> !v.getEffectType().isBeneficial()).collect(Collectors.toList()), gridMalus, activeMalus);

		}
	}

	private void drawEffects(Collection<StatusEffectInstance> effects, FreeRowGridLayout grid, Map<StatusEffect, EffectComponent> tracker) {
		var d1 = effects.stream().map(StatusEffectInstance::getEffectType).toList();
		var it = tracker.entrySet().iterator();
		while (it.hasNext()) {
			var a = it.next();
			if (!d1.contains(a.getKey())) {
				a.getValue().remove();
				it.remove();
			}
		}
		for (var eff : effects) {
			if (!tracker.containsKey(eff.getEffectType())) {
				var d = new EffectComponent(eff);
				d.sizing(Sizing.fixed(22));
				d.margins(Insets.of(1));
				tracker.put(eff.getEffectType(), d);
				grid.child(d);
			}

			//	grid.removeChild();
		}
	}

	@Override
	protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {

	}
}
