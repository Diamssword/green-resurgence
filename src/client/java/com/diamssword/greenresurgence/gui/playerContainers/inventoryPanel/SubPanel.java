package com.diamssword.greenresurgence.gui.playerContainers.inventoryPanel;

import com.diamssword.greenresurgence.gui.playerContainers.PlayerBasedGui;
import io.wispforest.owo.ui.container.FlowLayout;

public interface SubPanel {
	String guiLocation();

	String guiName();

	String guiIcon();

	boolean isFullHeight();

	void build(FlowLayout root, PlayerBasedGui<?> gui, boolean fullSize);
}