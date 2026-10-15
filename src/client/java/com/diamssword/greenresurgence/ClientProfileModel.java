package com.diamssword.greenresurgence;

import io.wispforest.owo.config.annotation.Config;

@Config(name = "resurgenceHoloSettings", wrapperName = "ClientProfile")
public class ClientProfileModel {
	public HoloSettings holos = new HoloSettings();

	public static class HoloSettings {
		public HoloColors colors = new HoloColors();
		public String survivalPanels = "[]";
		public int lastGuiSizeW = 0;
		public int lastGuiSizeH = 0;
	}

	public static class HoloColors {
		public int text;
	}

}
