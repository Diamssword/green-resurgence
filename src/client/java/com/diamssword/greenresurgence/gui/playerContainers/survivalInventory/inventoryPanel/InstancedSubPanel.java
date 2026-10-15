package com.diamssword.greenresurgence.gui.playerContainers.survivalInventory.inventoryPanel;

import java.util.function.Supplier;

public abstract class InstancedSubPanel extends SimpleSubPanel {
	private final Supplier<SubPanelInstance> supplier;

	public InstancedSubPanel(String name, String icon, String path, Supplier<SubPanelInstance> instanceSupplier) {
		super(name, icon, path);
		this.supplier = instanceSupplier;
	}


	@Override
	public SubPanelInstance instantiate() {
		return supplier.get();
	}

}
