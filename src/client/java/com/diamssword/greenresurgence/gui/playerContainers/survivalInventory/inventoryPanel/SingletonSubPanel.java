package com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel;

import com.diamssword.greenresurgence.gui.playerContainers.PlayerBasedGui;
import io.wispforest.owo.ui.container.FlowLayout;

public abstract class SingletonSubPanel extends SimpleSubPanel {

	public SingletonSubPanel(String name, String icon, String path) {
		super(name, icon, path);
	}


	@Override
	public SubPanelInstance instantiate() {
		return new SubPanelInstance() {
			@Override
			public void panelTick() {

			}

			@Override
			public void onResize(FlowLayout root, PlayerBasedGui<?> gui) {
				SingletonSubPanel.this.onResize(root, gui);
			}

			@Override
			public void build(FlowLayout root, PlayerBasedGui<?> gui) {
				SingletonSubPanel.this.build(root, gui);
			}
		};
	}

	abstract void onResize(FlowLayout root, PlayerBasedGui<?> gui);

	abstract void build(FlowLayout root, PlayerBasedGui<?> gui);

}
