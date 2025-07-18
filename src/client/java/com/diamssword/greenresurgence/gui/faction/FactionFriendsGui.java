package com.diamssword.greenresurgence.gui.faction;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.gui.ClickableLayoutComponent;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuildPackets;
import com.diamssword.greenresurgence.render.cosmetics.SkinsLoader;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionMember;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class FactionFriendsGui extends BaseUIModelScreen<FlowLayout> {

	private FlowLayout menu;
	private final UUID guild;
	private final Map<FactionMember, String> members;
	private final Map<String, Integer> roles;
	private final boolean canAdd;
	private final boolean canEdit;

	public FactionFriendsGui(UUID id, Map<FactionMember, String> members, Map<String, Integer> roles, boolean canAdd, boolean canEdit) {
		super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("faction/friends")));
		this.roles = roles;
		this.members = members;
		this.guild = id;
		this.canAdd = canAdd;
		this.canEdit = canEdit;
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		var search = rootComponent.childById(TextBoxComponent.class, "search");
		rootComponent.childById(ButtonComponent.class, "back").onPress(v -> Channels.MAIN.clientHandle().send(new GuildPackets.RequestGui("main", guild)));
		var addBt = rootComponent.childById(ButtonComponent.class, "add");
		if (canAdd)
			addBt.onPress(v -> Channels.MAIN.clientHandle().send(new GuildPackets.RequestGui("addFriends", guild)));
		else
			addBt.remove();
		menu = rootComponent.childById(FlowLayout.class, "menu");
		search.setPlaceholder(Text.literal("Recherche"));
		var c = rootComponent.childById(FlowLayout.class, "list");
		search.onChanged().subscribe(v -> {
			List<FactionMember> list = members.keySet().stream().toList();
			if (v.isBlank() || v.isEmpty()) {
				updateList(list, c);
			} else {
				if (v.startsWith("#")) {
					list = list.stream().filter(m -> m.isGuild() && m.getName().toLowerCase().contains(v.substring(1).toLowerCase())).toList();
				} else
					list = list.stream().filter(m -> m.getName().toLowerCase().contains(v.toLowerCase())).toList();
				updateList(list, c);

			}
		});
		updateList(members.keySet().stream().toList(), c);


	}

	private void updateSelectedMenu(FactionMember member, Identifier texture) {
		menu.clearChildren();
		menu.gap(4);
		menu.horizontalAlignment(HorizontalAlignment.CENTER);
		menu.child(Components.texture(texture, 0, 0, 8, 8, 8, 8).sizing(Sizing.fixed(32)));
		var t = Components.label(Text.literal(member.getName()));
		if (member.getName().length() > 15)
			t.horizontalSizing(Sizing.fill(100));
		menu.child(t);
		menu.allowOverflow(true);
		menu.child(cutLongText(members.get(member), 20, (v) -> roleMenu(menu, v, member)).tooltip(Text.literal("Role")));
	}

	public void roleMenu(FlowLayout menu, ButtonComponent bt, FactionMember member) {
		var flow = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
		var scroll = Containers.verticalScroll(Sizing.fill(100), Sizing.fill(50), flow);
		var txt = bt.getMessage();
		flow.surface(Surface.DARK_PANEL).padding(Insets.of(2));
		bt.setMessage(Text.literal("[Annuler]"));
		bt.onPress((b) -> {
			scroll.remove();
			bt.setMessage(txt);
			bt.onPress(v -> roleMenu(menu, bt, member));
		});
		for (var d : roles.keySet()) {
			flow.child(cutLongText(d, 20, (b) -> {
				scroll.remove();
				bt.setMessage(Text.literal(d));
				bt.onPress(v -> roleMenu(menu, bt, member));
				Channels.MAIN.clientHandle().send(new GuildPackets.ChangeRole(member, d));
			}).horizontalSizing(Sizing.fill(100)));
		}
		menu.child(scroll);
	}

	public static ButtonComponent cutLongText(String text, int max, Consumer<ButtonComponent> consumer) {
		var comp = Components.button(Text.literal(text), consumer);
		if (text.length() > max) {
			var t1 = text.substring(0, max - 3) + "...";
			comp.setMessage(Text.literal(t1));
			comp.tooltip(Text.literal(text));
			return comp;
		}
		return comp;
	}

	private void updateList(List<FactionMember> names, FlowLayout parent) {
		parent.clearChildren();
		for (var n : names) {
			var l = new ClickableLayoutComponent(Sizing.fill(100), Sizing.fixed(24), FlowLayout.Algorithm.HORIZONTAL);
			AtomicReference<Identifier> texture = new AtomicReference<>(n.isGuild() ? FactionFriendAddGui.GUILD_HEAD : FactionFriendAddGui.MISSING_HEAD);
			var icon = Components.texture(texture.get(), 0, 0, 8, 8, 8, 8).sizing(Sizing.fixed(16));
			l.child(icon);

			l.gap(4).padding(Insets.horizontal(4));
			l.child(Components.label(Text.literal((n.isGuild() ? "#" : "") + n.getName())).horizontalSizing(Sizing.fill(50)));
			l.surface(Surface.PANEL);
			l.surface2(Surface.DARK_PANEL);
			l.child(Components.label(Text.literal(members.get(n))).horizontalTextAlignment(HorizontalAlignment.RIGHT).horizontalSizing(Sizing.fill(30)));
			l.alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
			l.onPress(v -> updateSelectedMenu(n, texture.get()));
			if (n.isPlayer()) {
				SkinsLoader.loadHead(n.getId(), v -> {
					texture.set(v);
					l.removeChild(icon);
					l.child(0, Components.texture(texture.get(), 0, 0, 8, 8, 8, 8).sizing(Sizing.fixed(16)));
				});
			}
			parent.child(l);
		}
	}
}

