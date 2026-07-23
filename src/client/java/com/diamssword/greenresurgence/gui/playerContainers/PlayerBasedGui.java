package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.gui.MultiInvHandledScreen;
import com.diamssword.greenresurgence.gui.components.EffectComponent;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import com.diamssword.greenresurgence.gui.components.SubScreenLayout;
import com.diamssword.greenresurgence.gui.playerContainers.inventoryPanel.CharacterStatsPanel;
import com.diamssword.greenresurgence.gui.playerContainers.inventoryPanel.PlayerCraftPanel;
import com.diamssword.greenresurgence.gui.playerContainers.inventoryPanel.SubPanel;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
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
	public final boolean showLeftMenu;
	private FreeRowGridLayout gridBonus;
	private FreeRowGridLayout gridMalus;
	private int leftPaneMaxSizing = 0;
	private int leftPaneAvailableSpace = 0;
	private final Map<StatusEffect, EffectComponent> activeBonus = new HashMap<>();
	private final Map<StatusEffect, EffectComponent> activeMalus = new HashMap<>();
	public static List<SubPanel> subpanels = new ArrayList<>();
	protected boolean openSubPanelOnLoad = false;
	protected int subScreenSize = 30;

	static {
		subpanels.add(new PlayerCraftPanel());
		subpanels.add(new CharacterStatsPanel());

	}

	public static SubPanel subPanelTop = subpanels.get(0);
	public static SubPanel subPanelBot;

	public PlayerBasedGui(T handler, String subscreen) {
		this(handler, subscreen, true);
	}

	public PlayerBasedGui(T handler, String subscreen, boolean showLeftMenu) {
		super(handler, FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("survival/player_inventory")));
		this.subscreen = subscreen;
		this.showLeftMenu = showLeftMenu;
	}

	public void setSubScreenSize(int percent) {
		this.subScreenSize = percent;
	}


	@Override
	protected void build(FlowLayout rootComponent) {
		var sub = rootComponent.childById(SubScreenLayout.class, "subcontainer");
		var leftMenu = rootComponent.childById(FlowLayout.class, "leftMenu");
		if(sub != null) {
			sub.setLayout(subscreen);
			sub.horizontalSizing(Sizing.fill(subScreenSize));
			rootComponent.onChildMutated(sub);
		}
		if(!showLeftMenu && leftMenu != null) {
			leftMenu.clearChildren();
		}
		var bl = rootComponent.childById(FlowLayout.class, "blank_space");
		if(bl != null) {
			bl.horizontalSizing(Sizing.fill(30 - (subScreenSize - 30)));
		}
		gridBonus = rootComponent.childById(FreeRowGridLayout.class, "bonusLayout");
		gridMalus = rootComponent.childById(FreeRowGridLayout.class, "malusLayout");
		gridBonus.setInverted(true);
		if(showLeftMenu) {
			var subBtPanel = rootComponent.childById(FlowLayout.class, "subPanelButtons");
			var subPan = rootComponent.childById(FlowLayout.class, "layoutSubPanels");
			subpanels.forEach(v -> {
				var d = new RButtonComponent(Text.literal(""), (z) -> {
					pickPanel(v, subPan);
				});
				d.tooltip(Text.literal(v.guiName()).append(Text.literal("\n [Maj + Clique] pour afficher en scinder").formatted(Formatting.GRAY, Formatting.ITALIC)));
				d.icon(v.guiIcon());
				d.horizontalSizing(Sizing.fill(100));
				subBtPanel.child(d);
			});
		}
	}

	@Override
	protected void onBuilt(FlowLayout rootComponent) {
		var subPan = rootComponent.childById(FlowLayout.class, "layoutSubPanels");
		if(subPan != null) {
			this.leftPaneMaxSizing = subPan.width();
		}
	}

	protected void resizeLeftPanel(FlowLayout rootComponent) {
		var sub = rootComponent.childById(SubScreenLayout.class, "subcontainer");
		var subPan = rootComponent.childById(FlowLayout.class, "layoutSubPanels");
		var blank = rootComponent.childById(FlowLayout.class, "blank_space");
		var grid = rootComponent.childById(GridLayout.class, "maingrid");
		if(subPan != null) {
			var space = rootComponent.width() - (sub.width() + grid.width());
			if(space - 20 < leftPaneMaxSizing && space - 20 != subPan.width()) {
				leftPaneAvailableSpace = Math.min(leftPaneMaxSizing, space - 20);
				subPan.horizontalSizing(Sizing.fixed(leftPaneAvailableSpace));
				blank.horizontalSizing(Sizing.fixed(Math.min(leftPaneMaxSizing, space)));
				recreatePanels(subPan);
			} else if(leftPaneAvailableSpace == 0) {
				leftPaneAvailableSpace = leftPaneMaxSizing;
				recreatePanels(subPan);
			}
		}
	}

	public void closePanel(boolean bottom, FlowLayout parent) {
		if(!bottom && subPanelTop != null) {
			subPanelTop = subPanelBot;
			subPanelBot = null;
		} else if(bottom && subPanelBot != null) {
			subPanelBot = null;
		}
		recreatePanels(parent);
	}

	private void recreatePanels(FlowLayout parent) {
		parent.clearChildren();
		if(subPanelTop != null) {
			var hasBot = subPanelBot != null && subPanelBot.canOpen(leftPaneAvailableSpace);
			if(!showPanel(parent, subPanelTop, !hasBot, false)) {
				if(hasBot)
					showPanel(parent, subPanelBot, true, false);
			} else if(hasBot)
				showPanel(parent, subPanelBot, false, true);
		}
	}

	private boolean showPanel(FlowLayout parent, SubPanel panel, boolean full, boolean bottom) {
		if(!panel.canOpen(leftPaneAvailableSpace))
			return false;
		var r1 = new SubScreenLayout(Sizing.fill(100), Sizing.fill(full && !bottom ? 100 : 50), FlowLayout.Algorithm.VERTICAL, panel.guiLocation());
		parent.child(r1);
		var b = Components.button(Text.literal("x"), (u) -> closePanel(bottom, parent));
		b.positioning(Positioning.across(94, bottom ? 50 : 0)).sizing(Sizing.fixed(10)).zIndex(100);
		parent.child(b);
		panel.build(r1.getRoot(), this, full, leftPaneAvailableSpace);
		return true;
	}

	public void pickPanel(SubPanel panel, FlowLayout parent) {
		if(subPanelTop == panel) {
			subPanelTop = subPanelBot;
			subPanelBot = null;
		} else if(subPanelBot == panel) {
			subPanelBot = null;
		} else if(subPanelTop == null) {
			subPanelTop = panel;
		} else if(hasShiftDown()) {
			subPanelBot = panel;
		} else {
			subPanelTop = panel;
			subPanelBot = null;
		}
		recreatePanels(parent);

	}

	@Override
	protected void handledScreenTick() {
		super.handledScreenTick();
		resizeLeftPanel(uiAdapter.rootComponent);
		if(gridBonus != null && gridMalus != null) {
			Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
			drawEffects(collection.stream().filter(v -> v.getEffectType().isBeneficial()).collect(Collectors.toList()), gridBonus, activeBonus);
			drawEffects(collection.stream().filter(v -> !v.getEffectType().isBeneficial()).collect(Collectors.toList()), gridMalus, activeMalus);

		}
		if(subPanelTop != null)
			subPanelTop.panelTick();
		if(subPanelBot != null)
			subPanelBot.panelTick();
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
	}

	@Override
	protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {

	}


}
