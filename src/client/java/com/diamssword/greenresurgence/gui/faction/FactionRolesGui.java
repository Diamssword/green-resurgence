package com.diamssword.greenresurgence.gui.faction;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuildPackets;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

public class FactionRolesGui extends BaseUIModelScreen<FlowLayout> {

	public UUID id;
	public Map<String, Integer> roles;

	public FactionRolesGui(UUID uuid, Map<String, Integer> roles) {
		super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("faction/roles")));
		this.id = uuid;
		this.roles = roles;
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		rootComponent.childById(ButtonComponent.class, "back").onPress(v -> Channels.MAIN.clientHandle().send(new GuildPackets.RequestGui("main", id)));
		rootComponent.childById(ButtonComponent.class, "add").onPress(v -> Channels.MAIN.clientHandle().send(new GuildPackets.PermEditRequest("")));
		var c = rootComponent.childById(FlowLayout.class, "list");
		updateList(c);


	}

	private void updateList(FlowLayout parent) {
		parent.clearChildren();
		var ls = roles.keySet();
		var sorted = ls.stream().sorted(Comparator.comparingInt(a -> roles.getOrDefault(a, 1))).toList();
		for (var n : sorted) {
			var l = Containers.horizontalFlow(Sizing.fixed(160), Sizing.fixed(30));
			var l1 = Containers.verticalFlow(Sizing.fixed(12), Sizing.content());
			l1.child(Components.button(Text.literal("\uD83D\uDF81"), b -> {
			}).sizing(Sizing.fixed(12), Sizing.fixed(12)).tooltip(Text.literal("Monter la prioritée")));
			l1.child(Components.button(Text.literal("\uD83D\uDF83"), b -> {
			}).sizing(Sizing.fixed(12), Sizing.fixed(12)).tooltip(Text.literal("Baisser la prioritée")));
			l.child(l1);
			l.gap(2).padding(Insets.horizontal(4));
			l.child(Components.label(Text.literal(n)).horizontalTextAlignment(HorizontalAlignment.CENTER).horizontalSizing(Sizing.fixed(110)));
			l.surface(Surface.PANEL);
			l.child(Components.button(Text.literal("\uD83D\uDD89"), (v) -> Channels.MAIN.clientHandle().send(new GuildPackets.PermEditRequest(n)))
					.tooltip(Text.literal("Modifier")).sizing(Sizing.fixed(20)));
			l.alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
			parent.child(l);
		}
	}
}

