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
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerBasedGui<T extends MultiInvScreenHandler> extends MultiInvHandledScreen<T, FlowLayout> {
	public final String subscreen;
	public final boolean showLeftMenu;
	private FreeRowGridLayout gridBonus;
	private FreeRowGridLayout gridMalus;
	private Vector2i leftPaneSpace;
	private final Map<StatusEffect, EffectComponent> activeBonus = new HashMap<>();
	private final Map<StatusEffect, EffectComponent> activeMalus = new HashMap<>();
	private final Map<SubPanel, RButtonComponent> buttonsMap = new HashMap<>();
	public static List<SubPanel> subpanels = new ArrayList<>();
	protected boolean openSubPanelOnLoad = false;
	private boolean needResize = false;
	protected int subScreenSize = 0;

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
		this.needResize = true;
	}


	@Override
	protected void build(FlowLayout rootComponent) {
		var sub = rootComponent.childById(SubScreenLayout.class, "subcontainer");
		var leftMenu = rootComponent.childById(FlowLayout.class, "leftMenu");
		if(sub != null) {
			sub.setLayout(subscreen);
			rootComponent.onChildMutated(sub);
		}
		if(!showLeftMenu && leftMenu != null) {
			leftMenu.clearChildren();
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
				if(v == subPanelTop || v == subPanelBot)
					d.setActivated(true);
				d.tooltip(Text.literal(v.guiName()).append(Text.translatable("gui.green_resurgence.survival_inventory.left_panel.button.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC)));
				d.icon(v.guiIcon());
				d.horizontalSizing(Sizing.fill(100));
				buttonsMap.put(v, d);
				subBtPanel.child(d);
			});
		}
	}

	@Override
	protected void onBuilt(FlowLayout rootComponent) {
		needResize = true;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if(needResize) {
			updateLayout(uiAdapter.rootComponent);
			needResize = false;
		}
		super.render(context, mouseX, mouseY, delta);
	}

	protected void updateLayout(FlowLayout rootComponent) {
		var leftM = rootComponent.childById(FlowLayout.class, "leftMenu");
		var subPan = rootComponent.childById(FlowLayout.class, "layoutSubPanels");
		var inventory = rootComponent.childById(GridLayout.class, "maingrid");
		var subScr = rootComponent.childById(SubScreenLayout.class, "subcontainer");
		if(showLeftMenu) {
			if(inventory != null) {
				if(subScr != null) {
					var s = rootComponent.width() - (inventory.x() + inventory.width());
					if(subScreenSize > 0) {
						int m = (int) (rootComponent.width() * (subScreenSize * 0.01f));
						if(inventory.positioning().get().type == Positioning.Type.ABSOLUTE || m > s) {
							s = m;
							inventory.positioning(Positioning.absolute(rootComponent.width() - s - inventory.width(), (rootComponent.height() / 2) - (inventory.height() / 2)));
						}
					}
					subScr.horizontalSizing(Sizing.fixed(s));
				}
				if(leftM != null)
					leftPaneSpace = new Vector2i(inventory.x() - 25, leftM.height());
				if(subPan != null)
					recreatePanels(subPan);
			}
		}
		drawStatusEffect();
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
			var w = leftPaneSpace.x;
			var h = leftPaneSpace.y;
			var hasBot = subPanelBot != null && subPanelBot.minWidth() <= w && subPanelBot.minHeight() <= h / 2;
			if(subPanelTop.minWidth() <= w) {
				if(hasBot && subPanelTop.minHeight() > h / 2)
					hasBot = false;
				else if(hasBot)
					h /= 2;

				if(subPanelTop.desiredWidth() < w)
					w = subPanelTop.desiredWidth();
				if(hasBot && subPanelBot.desiredWidth() > w)
					w = subPanelBot.desiredWidth();
				if(subPanelTop.minHeight() <= h) {
					showPanel(parent, subPanelTop, false, w, h);
				}
				if(hasBot) {
					showPanel(parent, subPanelBot, true, w, h);
				}
				updatePanelButtonState(subPanelTop.minHeight() <= h ? subPanelTop : null, hasBot ? subPanelBot : null);
			} else if(hasBot) {
				if(subPanelBot.desiredWidth() < w)
					w = subPanelBot.desiredWidth();
				showPanel(parent, subPanelBot, false, w, h);
				updatePanelButtonState(null, subPanelBot);
			} else
				updatePanelButtonState(null, null);
		} else
			updatePanelButtonState(null, null);
	}

	private void showPanel(FlowLayout parent, SubPanel panel, boolean bottom, int width, int height) {

		var r1 = new SubScreenLayout(Sizing.fixed(width), Sizing.fixed(height), FlowLayout.Algorithm.VERTICAL, panel.guiLocation());
		parent.child(r1);
		var b = Components.button(Text.literal("x"), (u) -> closePanel(bottom, parent));
		b.positioning(Positioning.absolute(width - 10, bottom ? height : 0)).sizing(Sizing.fixed(10)).zIndex(100);
		parent.child(b);
		panel.build(r1.getRoot(), this, width, height);
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

	private void updatePanelButtonState(@Nullable SubPanel p1, @Nullable SubPanel p2) {
		buttonsMap.forEach((k, v) -> {
			v.setActivated(k == p1 || k == p2);
		});
	}

	private void drawStatusEffect() {
		if(gridBonus != null && gridMalus != null) {
			Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
			drawEffects(collection.stream().filter(v -> v.getEffectType().isBeneficial()).collect(Collectors.toList()), gridBonus, activeBonus);
			drawEffects(collection.stream().filter(v -> !v.getEffectType().isBeneficial()).collect(Collectors.toList()), gridMalus, activeMalus);
		}
	}

	@Override
	protected void handledScreenTick() {

		super.handledScreenTick();
		//resizeLeftPanel(uiAdapter.rootComponent);
		drawStatusEffect();
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
