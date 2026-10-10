package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.gui.MultiInvHandledScreen;
import com.diamssword.greenresurgence.gui.components.EffectComponent;
import com.diamssword.greenresurgence.gui.components.FreeRowGridLayout;
import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import com.diamssword.greenresurgence.gui.components.SubScreenLayout;
import com.diamssword.greenresurgence.gui.components.panels.HoloPanels;
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
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlayerBasedGui<T extends MultiInvScreenHandler> extends MultiInvHandledScreen<T, FlowLayout> {
	public final String subscreen;
	public final boolean showLeftMenu;
	private Vector2i leftPaneSpace;
	private final Map<SubPanel, RButtonComponent> buttonsMap = new HashMap<>();
	protected SubScreenLayout rightPane;
	protected boolean openSubPanelOnLoad = false;
	private boolean needResize = false;
	protected int subScreenSize = 0;


	public static SubPanel subPanelTop = PlayerInventoryCustomization.subpanels.get(0);
	public static SubPanel subPanelBot;

	public PlayerBasedGui(T handler, String subscreen) {
		this(handler, subscreen, true);
	}

	public PlayerBasedGui(T handler, String subscreen, boolean showLeftMenu) {
		super(handler, FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("survival/player_inventory")));
		this.subscreen = subscreen;
		this.showLeftMenu = showLeftMenu;
		this.handler.onReady(c -> {
			PlayerInventoryCustomization.editInventory(this.uiAdapter.rootComponent, handler);
		});
	}

	public void setSubScreenSize(int percent) {
		this.subScreenSize = percent;
		this.needResize = true;
	}


	@Override
	protected void build(FlowLayout rootComponent) {
		rightPane = rootComponent.childById(SubScreenLayout.class, "subcontainer");
		var leftMenu = rootComponent.childById(FlowLayout.class, "leftMenu");
		if(rightPane != null) {
			rightPane.setLayout(subscreen);
			rootComponent.onChildMutated(rightPane);
		}
		if(!showLeftMenu && leftMenu != null) {
			leftMenu.clearChildren();
		}

		if(showLeftMenu) {
			var subBtPanel = rootComponent.childById(FlowLayout.class, "subPanelButtons");
			var subPan = rootComponent.childById(FlowLayout.class, "layoutSubPanels");
			PlayerInventoryCustomization.subpanels.forEach(v -> {
				var d = new RButtonComponent(Text.literal(""), (z) -> {
					pickPanel(v, subPan);
				});
				if(v == subPanelTop || v == subPanelBot)
					d.setActivated(true);
				d.tooltip(v.guiName().append(Text.translatable("gui.green_resurgence.survival_inventory.left_panel.button.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC)));
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
		PlayerInventoryCustomization.editInventory(this.uiAdapter.rootComponent, handler);
		var leftM = rootComponent.childById(FlowLayout.class, "leftMenu");
		var subPan = rootComponent.childById(FlowLayout.class, "layoutSubPanels");
		var inventory = rootComponent.childById(FlowLayout.class, "maingrid");
		var eqP = rootComponent.childById(FlowLayout.class, "equipmentsPanel");
		//TODO leftPanel should become another accessible subscreen container, create a custom subscreen for the subpanes, enable it only for the player inventory, and leave this space usable for something else if needed for other GUI
		if(inventory != null) {
			if(rightPane != null) {
				var s = rootComponent.width() - (inventory.x() + inventory.width());
				if(subScreenSize > 0) {
					int m = (int) (rootComponent.width() * (subScreenSize * 0.01f));
					if(inventory.positioning().get().type == Positioning.Type.ABSOLUTE || m > s) {
						s = m;
						var y = (rootComponent.height() / 2) - (inventory.height() / 2);
						var old = inventory.positioning().get();
						if(old.type == Positioning.Type.ABSOLUTE)
							y = old.y;
						inventory.positioning(Positioning.absolute(rootComponent.width() - s - inventory.width(), y));
					}
				}
				rightPane.horizontalSizing(Sizing.fixed(s));
			}
			if(leftM != null && eqP != null)
				leftPaneSpace = new Vector2i(eqP.x() - (eqP.width() > 30 ? 30 : 40), leftM.height());
			if(subPan != null)
				recreatePanels(subPan);
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
		b.renderer(HoloPanels.button());
		b.positioning(Positioning.absolute(width - 10, bottom ? height : 0)).sizing(Sizing.fixed(10)).zIndex(100);
		parent.child(b);
		try {
			panel.build(r1.getRoot(), this, width, height);
		} catch(Exception e) {
			e.printStackTrace();
		}
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


	@Override
	protected void handledScreenTick() {

		super.handledScreenTick();
		//resizeLeftPanel(uiAdapter.rootComponent);
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
