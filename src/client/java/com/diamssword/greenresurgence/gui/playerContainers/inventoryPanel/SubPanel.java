package com.diamssword.greenresurgence.gui.playerContainers.inventoryPanel;

import com.diamssword.greenresurgence.gui.playerContainers.PlayerBasedGui;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.text.MutableText;

public interface SubPanel {
	String guiLocation();

	MutableText guiName();

	String guiIcon();

	default int minHeight() {
		return 100;
	}

	default int minWidth() {
		return 120;
	}

	default int desiredWidth() {
		return 180;
	}


	default void panelTick() {}

	void build(FlowLayout root, PlayerBasedGui<?> gui, int width, int height);
}