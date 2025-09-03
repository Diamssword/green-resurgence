package com.diamssword.greenresurgence;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;

@Modmenu(modId = "green_resurgence")
@Config(name = "resurgence", wrapperName = "ResurgenceConfig")
public class ConfigModel {

	@Nest
	public Server serverOptions = new Server();

	public static class Server {
		@Nest
		public Cooldowns cooldowns = new Cooldowns();
	}


	public static class Cooldowns {
		public int respawnLootedBlockInSec = 60;
		public int respawnGroundLootInSec = 60;
		public int respawnShelvesLootInSec = 60;
		public int respawnCrumbelingBlockInSec = 30;
		public int deployableExpireInSec = 60;
	}
}
