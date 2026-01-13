package com.diamssword.greenresurgence;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;

@Modmenu(modId = "green_resurgence")
@Config(name = "resurgence", wrapperName = "ResurgenceConfig")
public class ConfigModel {
	@Nest
	public Client clientOptions = new Client();
	@Nest
	public Server serverOptions = new Server();

	public static class Server {
		public int __ = 0;
		@Nest
		public Cooldowns cooldowns = new Cooldowns();
	}

	public static class Client {
		public int __ = 0;
		@Nest
		public Renders renders = new Renders();
	}

	public static class Renders {
		public int wireAnchorMaxRenderDistance = 64;
	}

	public static class Cooldowns {
		public int respawnLootedBlockInSec = 60;
		public int respawnGroundLootInSec = 60;
		public int respawnShelvesLootInSec = 60;
		public int respawnCrumbelingBlockInSec = 30;
		public int deployableExpireInSec = 60;
	}
}
