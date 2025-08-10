package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.gui.MultiInvHandledScreen;
import com.diamssword.greenresurgence.gui.components.EffectComponent;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import com.diamssword.greenresurgence.gui.components.SubScreenLayout;
import com.diamssword.greenresurgence.gui.playerContainers.inventoryPanel.CharacterStatsPanel;
import com.diamssword.greenresurgence.gui.playerContainers.inventoryPanel.SimpleSubPanel;
import com.diamssword.greenresurgence.gui.playerContainers.inventoryPanel.SubPanel;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerBasedGui<T extends MultiInvScreenHandler> extends MultiInvHandledScreen<T, FlowLayout> {
	public final String subscreen;
	private FreeRowGridLayout gridBonus;
	private FreeRowGridLayout gridMalus;
	private final Map<StatusEffect, EffectComponent> activeBonus = new HashMap<>();
	private final Map<StatusEffect, EffectComponent> activeMalus = new HashMap<>();
	public static List<SubPanel> subpanels = new ArrayList<>();

	static {
		subpanels.add(new CharacterStatsPanel());
		subpanels.add(new SimpleSubPanel("test", "test", "character/size") {
			@Override
			public void build(FlowLayout root, PlayerBasedGui<?> gui, boolean fullSize) {

			}
		});
	}

	public static SubPanel subPanelTop = subpanels.get(0);
	public static SubPanel subPanelBot;

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
		gridBonus.setInverted(true);
		var subBtPanel = rootComponent.childById(FlowLayout.class, "subPanelButtons");
		var subPan = rootComponent.childById(FlowLayout.class, "layoutSubPanels");
		subpanels.forEach(v -> {
			var d = new RButtonComponent(Text.literal(""), (z) -> {
				pickPanel(v, subPan);
			});
			d.tooltip(Text.literal(v.guiName()).append(Text.literal("\n [Maj + Clique] pour fermer une fenetre").formatted(Formatting.GRAY, Formatting.ITALIC)));
			d.icon(v.guiIcon());
			d.horizontalSizing(Sizing.fill(100));
			subBtPanel.child(d);
		});
		recreatePanels(subPan);
	}

	public void closePanel(boolean bottom, FlowLayout parent) {
		if (!bottom && subPanelTop != null) {
			subPanelTop = subPanelBot;
			subPanelBot = null;
		} else if (bottom && subPanelBot != null) {
			subPanelBot = null;
		}
		recreatePanels(parent);
	}

	private void recreatePanels(FlowLayout parent) {
		parent.clearChildren();
		if (subPanelTop != null) {
			var r1 = new SubScreenLayout(Sizing.fill(100), Sizing.fill(subPanelBot == null ? 100 : 50), FlowLayout.Algorithm.VERTICAL, subPanelTop.guiLocation());
			parent.child(r1);
			var b = Components.button(Text.literal("x"), (u) -> closePanel(false, parent));
			b.positioning(Positioning.across(90, 1)).sizing(Sizing.fixed(10)).zIndex(100);
			parent.child(b);
			subPanelTop.build(r1.getRoot(), this, subPanelBot == null);
			if (subPanelBot != null) {
				var r2 = new SubScreenLayout(Sizing.fill(100), Sizing.fill(50), FlowLayout.Algorithm.VERTICAL, subPanelBot.guiLocation());
				parent.child(r2);
				var b1 = Components.button(Text.literal("x"), (u) -> closePanel(true, parent));
				b1.positioning(Positioning.across(90, 51)).sizing(Sizing.fixed(10)).zIndex(100);
				parent.child(b1);
				subPanelBot.build(r2.getRoot(), this, false);
			}

		}
	}

	public void pickPanel(SubPanel panel, FlowLayout parent) {
		if (hasShiftDown()) {
			if (subPanelTop == panel) {
				subPanelTop = subPanelBot;
				subPanelBot = null;
			} else if (subPanelBot == panel) {
				subPanelBot = null;
			}
		} else if (subPanelTop == panel) {
			if (panel.isFullHeight()) {
				subPanelTop = null;
			} else {
				subPanelTop = panel;
			}
			subPanelBot = null;
		} else if (subPanelBot == panel) {
			subPanelTop = panel;
			subPanelBot = null;
		} else {
			if (subPanelTop == null)
				subPanelTop = panel;
			else if (subPanelBot == null && !panel.isFullHeight())
				subPanelBot = panel;
			else {
				subPanelBot = subPanelTop;
				subPanelTop = panel;
			}
		}
		recreatePanels(parent);

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
