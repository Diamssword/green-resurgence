package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.utils.ClientSideHelper;
import net.minecraft.client.gui.screen.Screen;

public class ClientSideHelperImp extends ClientSideHelper {
	@Override
	public boolean isShiftPressed() {
		return Screen.hasShiftDown();
	}

	@Override
	public boolean isClient() {
		return true;
	}
}