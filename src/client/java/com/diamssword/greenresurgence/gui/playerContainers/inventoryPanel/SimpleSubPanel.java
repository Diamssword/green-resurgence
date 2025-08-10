package com.diamssword.greenresurgence.gui.playerContainers.inventoryPanel;

public abstract class SimpleSubPanel implements SubPanel {
	private final String name;
	private final String icon;
	private final String path;

	public SimpleSubPanel(String name, String icon, String path) {
		this.name = name;
		this.icon = icon;
		this.path = path;
	}

	@Override
	public String guiLocation() {
		return path;
	}

	@Override
	public String guiName() {
		return name;
	}

	@Override
	public String guiIcon() {
		return icon;
	}

	@Override
	public boolean isFullHeight() {
		return false;
	}
}
