package com.diamssword.greenresurgence.gui.faction;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.IPacketNotifiedChange;
import com.diamssword.greenresurgence.gui.components.ClickableLayoutComponent;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuildPackets;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionPerm;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.Perms;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;

import java.util.UUID;

public class FactionEditRoleGui extends BaseUIModelScreen<FlowLayout> implements IPacketNotifiedChange {

	private final FactionPerm perms;
	private final boolean shoudlClose = false;
	private final String oname;
	private final UUID id;

	public FactionEditRoleGui(UUID id, FactionPerm perm) {
		super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("faction/role")));
		this.perms = perm;
		this.oname = perm.getName();
		this.id = id;
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		rootComponent.childById(ButtonComponent.class, "back").onPress(v -> Channels.MAIN.clientHandle().send(new GuildPackets.RequestGui("roles", id)));
		rootComponent.childById(ButtonComponent.class, "confirm").onPress(v -> Channels.MAIN.clientHandle().send(new GuildPackets.PermEdit(id, this.perms)));
		var name = rootComponent.childById(TextBoxComponent.class, "name");
		name.setText(this.perms.getName());
		name.onChanged().subscribe(this.perms::setName);
		var c = rootComponent.childById(FlowLayout.class, "list");
		updateList(c);
	}

	private void updateList(FlowLayout parent) {
		parent.clearChildren();
		for (var n : Perms.values()) {
			var l = new ClickableLayoutComponent(Sizing.fill(100), Sizing.fixed(20), FlowLayout.Algorithm.HORIZONTAL);
			l.surface2(Surface.DARK_PANEL);

			var c = Components.smallCheckbox(Text.literal(""));
			l.onPress(a -> c.checked(!c.checked()));
			c.sizing(Sizing.fixed(15), Sizing.fixed(15));
			c.onChanged().subscribe(v -> this.perms.setPerm(n, v));
			l.child(c.checked(this.perms.isAllowed(n)));
			l.gap(2).padding(Insets.horizontal(4));
			l.child(Components.label(Text.literal(n.toString())).horizontalTextAlignment(HorizontalAlignment.CENTER).horizontalSizing(Sizing.fill(100)));
			l.surface(Surface.PANEL);
			l.alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
			parent.child(l);
		}
	}

	@Override
	public void onChangeReceived(String topic, String value) {
		Channels.MAIN.clientHandle().send(new GuildPackets.RequestGui("roles", id));
	}

	@Override
	public void onErrorReceived(String topic, Text value) {
		System.err.println(value.toString());
	}
}

