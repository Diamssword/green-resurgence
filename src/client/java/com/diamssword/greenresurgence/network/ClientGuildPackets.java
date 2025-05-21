package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.gui.faction.*;
import net.minecraft.client.MinecraftClient;

public class ClientGuildPackets {
	public static void init() {
		Channels.MAIN.registerClientbound(GuildPackets.PermEdit.class, (msg, ctx) -> MinecraftClient.getInstance().setScreen(new FactionEditRoleGui(msg.id(), msg.role())));
		Channels.MAIN.registerClientbound(GuildPackets.OpenFactionGui.class, (msg, ctx) -> MinecraftClient.getInstance().setScreen(new FactionMainGui(msg.guildID(), msg.guildName())));
		Channels.MAIN.registerClientbound(GuildPackets.OpenFriendsGui.class, (msg, ctx) -> MinecraftClient.getInstance().setScreen(new FactionFriendsGui(msg.guildID(), msg.members(), msg.roles(), msg.canAdd(), msg.canEdit())));
		Channels.MAIN.registerClientbound(GuildPackets.OpenFriendsAddGui.class, (msg, ctx) -> MinecraftClient.getInstance().setScreen(new FactionFriendAddGui(msg.guildID(), msg.guilds())));
		Channels.MAIN.registerClientbound(GuildPackets.OpenRolesGui.class, (msg, ctx) -> MinecraftClient.getInstance().setScreen(new FactionRolesGui(msg.guildID(), msg.roles())));

	}
}
