package com.diamssword.greenresurgence.gui.playerContainers;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.gui.MultiInvHandledScreen;
import com.diamssword.greenresurgence.gui.components.RButtonComponent;
import com.diamssword.greenresurgence.gui.components.SubScreenLayout;
import com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.PlayerInventoryBackgrounds;
import com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel.SubPanel;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.Map;

public class PlayerBasedGui<T extends MultiInvScreenHandler> extends MultiInvHandledScreen<T, FlowLayout> {
	public final String subscreen;
	private final Map<SubPanel, RButtonComponent> buttonsMap = new HashMap<>();
	protected SubScreenLayout rightPane;
	protected boolean openSubPanelOnLoad = false;
	private boolean needResize = false;
	protected int subScreenSize = 0;


	public PlayerBasedGui(T handler, String subscreen) {
		this(handler, subscreen, true);
	}

	public PlayerBasedGui(T handler, String subscreen, boolean showLeftMenu) {
		super(handler, FlowLayout.class, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource("survival/player_inventory")));
		this.subscreen = subscreen;
		this.handler.onReady(c -> {
			PlayerInventoryBackgrounds.editInventory(this.uiAdapter.rootComponent, handler);
		});
	}

	public void setSubScreenSize(int percent) {
		this.subScreenSize = percent;
		this.needResize = true;
	}


	@Override
	protected void build(FlowLayout rootComponent) {
		rightPane = rootComponent.childById(SubScreenLayout.class, "subcontainer");
		if(rightPane != null) {
			rightPane.setLayout(subscreen);
			rootComponent.onChildMutated(rightPane);
		}

		/*if(showLeftMenu) {
			var subBtPanel = rootComponent.childById(FlowLayout.class, "subPanelButtons");
			var subPan = rootComponent.childById(FlowLayout.class, "layoutSubPanels");
			PlayerInventoryBackgrounds.subpanels.forEach(v -> {
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
		}*/
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
		PlayerInventoryBackgrounds.editInventory(this.uiAdapter.rootComponent, handler);
		var inventory = rootComponent.childById(FlowLayout.class, "maingrid");
		var eqP = rootComponent.childById(FlowLayout.class, "equipmentsPanel");
		//TODO leftPanel should become another accessible subscreen container, create a custom subscreen for the subpanes, enable it only for the player inventory, and leave this space usable for something else if needed for other GUI
		//prefered alternative: single window with upside tabs button (creative inventory-like) that is resizable, multiple window can be created an resized-moved
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
			//if(leftM != null && eqP != null)
			//	leftPaneSpace = new Vector2i(eqP.x() - (eqP.width() > 30 ? 30 : 40), leftM.height());
		}
	}

	@Override
	protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {

	}


}
