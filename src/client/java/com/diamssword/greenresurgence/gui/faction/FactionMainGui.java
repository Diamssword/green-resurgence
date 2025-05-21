package com.diamssword.greenresurgence.gui.faction;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuildPackets;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;

import java.util.UUID;

public class FactionMainGui extends BaseUIModelScreen<FlowLayout> {

	private final UUID faction;
	private final String name;

	public FactionMainGui(UUID uuid, String name) {
		super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("faction/main")));
		this.faction = uuid;
		this.name = name;
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		rootComponent.childById(ButtonComponent.class, "members").onPress(v -> Channels.MAIN.clientHandle().send(new GuildPackets.RequestGui("friends", faction)));
		rootComponent.childById(ButtonComponent.class, "roles").onPress(v -> Channels.MAIN.clientHandle().send(new GuildPackets.RequestGui("roles", faction)));
	}
}

