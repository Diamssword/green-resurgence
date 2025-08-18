package com.diamssword.greenresurgence.gui.faction;

import com.diamssword.characters.api.CharactersApi;
import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.IPacketNotifiedChange;
import com.diamssword.greenresurgence.gui.components.ClickableLayoutComponent;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuildPackets;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionMember;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class FactionFriendAddGui extends BaseUIModelScreen<FlowLayout> implements IPacketNotifiedChange {

	public static final Identifier MISSING_HEAD = GreenResurgence.asRessource("textures/gui/missing_head.png");

	public final static Identifier GUILD_HEAD = GreenResurgence.asRessource("textures/gui/guild_head.png");
	private int needChange = -1;
	private FlowLayout list;
	private FlowLayout menu;
	private String filterText = "";
	private final UUID id;
	private final Map<UUID, String> guilds;

	public FactionFriendAddGui(UUID uuid, Map<UUID, String> guilds) {
		super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("faction/friend_add")));
		this.id = uuid;
		this.guilds = guilds;
	}

	@Override
	public void tick() {
		super.tick();
		if (needChange > -1) {
			needChange--;
			if (needChange == 0) {
				addOffline(filterText, list);
				needChange = -1;
			}
		}
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		var search = rootComponent.childById(TextBoxComponent.class, "search");
		rootComponent.childById(ButtonComponent.class, "back").onPress(v -> Channels.MAIN.clientHandle().send(new GuildPackets.RequestGui("friends", id)));
		menu = rootComponent.childById(FlowLayout.class, "contextMenu");
		search.setPlaceholder(Text.literal("Recherche"));
		list = rootComponent.childById(FlowLayout.class, "list");
		search.onChanged().subscribe(v -> refreshSearch(v, list));
		refreshSearch(search.getText(), list);
	}

	private void refreshSearch(String text, FlowLayout parent) {
		if (!text.isBlank() && !text.isEmpty()) {
			filterText = text;
			needChange = 30;
		}
		parent.clearChildren();
		addPlayers(text.toLowerCase(), parent);
		if (text.startsWith("#"))
			addGuilds(text.substring(1).toLowerCase(), parent);
	}

	private void updateSelectedMenu(FactionMember member, Identifier texture) {
		menu.clearChildren();
		menu.gap(4);
		menu.sizing(Sizing.fill(40), Sizing.content());
		menu.horizontalAlignment(HorizontalAlignment.CENTER);
		menu.child(Components.texture(texture, 0, 0, 8, 8, 8, 8).sizing(Sizing.fixed(32)));
		var t = Components.label(Text.literal(member.getName()));
		if (member.getName().length() > 15)
			t.horizontalSizing(Sizing.fill(100));
		menu.child(t);
		menu.child(Components.button(Text.literal("Inviter"), (v) -> {
			Channels.MAIN.clientHandle().send(new GuildPackets.InviteMember(member));
		}));
	}

	private void addPlayers(String filter, FlowLayout parent) {
		var pls = client.player.networkHandler.getListedPlayerListEntries().stream().filter(v -> !v.getProfile().getId().equals(client.player.getUuid()));
		if (!filter.isEmpty() && !filter.isBlank())
			pls = pls.filter(v -> v.getProfile().getName().toLowerCase().contains(filter));
		for (var n : pls.toList()) {
			var l = new ClickableLayoutComponent(Sizing.fill(100), Sizing.fixed(20), FlowLayout.Algorithm.HORIZONTAL);
			AtomicReference<Identifier> texture = new AtomicReference<>(MISSING_HEAD);
			l.onPress(v -> updateSelectedMenu(new FactionMember(n.getProfile().getId(), n.getProfile().getName(), false), texture.get()));
			var icon = Components.texture(texture.get(), 0, 0, 8, 8, 8, 8).sizing(Sizing.fixed(16));
			l.child(icon);
			l.gap(4).padding(Insets.horizontal(2));
			l.child(Components.label(Text.literal(n.getProfile().getName())));
			l.surface(Surface.PANEL);
			l.surface2(Surface.DARK_PANEL);
			l.alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
			CharactersApi.skin().getHeadTexture(n.getProfile().getId(), v -> {
				texture.set(v);
				l.removeChild(icon);
				l.child(0, Components.texture(texture.get(), 0, 0, 8, 8, 8, 8).sizing(Sizing.fixed(16)));
			});
			parent.child(l);
		}
	}

	private void addGuilds(String filter, FlowLayout parent) {
		var pls = guilds.entrySet().stream();
		if (!filter.isEmpty() && !filter.isBlank())
			pls = pls.filter(v -> v.getValue().toLowerCase().contains(filter));
		for (var n : pls.toList()) {
			var l = new ClickableLayoutComponent(Sizing.fill(100), Sizing.fixed(20), FlowLayout.Algorithm.HORIZONTAL);
			l.child(Components.texture(GUILD_HEAD, 0, 0, 8, 8, 8, 8).sizing(Sizing.fixed(16)));
			l.onPress(v -> updateSelectedMenu(new FactionMember(n.getKey(), n.getValue(), true), GUILD_HEAD));
			l.gap(4).padding(Insets.horizontal(2));
			l.child(Components.label(Text.literal("#" + n.getValue())));
			l.surface(Surface.PANEL);
			l.surface2(Surface.DARK_PANEL);
			l.alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
			parent.child(l);
		}
	}

	private void addOffline(String filter, FlowLayout parent) {
		CharactersApi.skin().requestPlayerProfiles(filter).thenAccept(v -> {
			for (var d : v.entrySet()) {
				AtomicReference<Identifier> texture = new AtomicReference<>(MISSING_HEAD);
				var l = new ClickableLayoutComponent(Sizing.fill(100), Sizing.fixed(20), FlowLayout.Algorithm.HORIZONTAL);
				var icon = Components.texture(MISSING_HEAD, 0, 0, 8, 8, 8, 8).sizing(Sizing.fixed(16));
				l.child(icon);
				l.onPress(v1 -> updateSelectedMenu(new FactionMember(d.getKey(), d.getValue().username(), false), texture.get()));
				l.gap(4).padding(Insets.horizontal(2));
				l.child(Components.label(Text.literal(d.getValue().characterName())));
				l.surface(Surface.PANEL);
				l.surface2(Surface.DARK_PANEL);
				l.alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
				CharactersApi.skin().getHeadTexture(d.getKey(), v1 -> {
					texture.set(v1);
					l.removeChild(icon);
					l.child(0, Components.texture(v1, 0, 0, 8, 8, 8, 8).sizing(Sizing.fixed(16)));
				});
				parent.child(0, l);

			}
		});
	}


	@Override
	public void onChangeReceived(String topic, String value) {

		if (topic.equals("addMember")) {
			menu.clearChildren();
			menu.child(Components.label(Text.literal(value + " à été ajouté!")));
		}
	}

	@Override
	public void onErrorReceived(String topic, Text message) {
		if (topic.equals("addMember")) {
			menu.clearChildren();
			menu.child(Components.label(message));
		}
	}
}

