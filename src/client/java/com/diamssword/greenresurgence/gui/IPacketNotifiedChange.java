package com.diamssword.greenresurgence.gui;

import net.minecraft.text.Text;

public interface IPacketNotifiedChange {
	void onChangeReceived(String topic, String value);

	void onErrorReceived(String topic, Text message);
}
