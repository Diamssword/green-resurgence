package com.diamssword.greenresurgence.network;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionMember;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionPerm;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.Perms;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuildPackets {
	private static final Map<PlayerEntity, String> currentRoleEdit = new HashMap<>();


	public record RequestGui(String gui, UUID guild) {
	}

	public record InviteMember(FactionMember member) {
	}

	public record ChangeRole(FactionMember member, String newRole) {
	}

	public record PermEdit(UUID id, FactionPerm role) {
	}

	public record PermEditRequest(String role) {
	}

	public record OpenFactionGui(UUID guildID, String guildName) {
	}

	public record OpenFriendsGui(UUID guildID, Map<FactionMember, String> members, Map<String, Integer> roles, boolean canAdd, boolean canEdit) {
	}

	public record OpenFriendsAddGui(UUID guildID, Map<UUID, String> guilds) {
	}

	public record OpenRolesGui(UUID guildID, Map<String, Integer> roles) {
	}

	public static void init() {
		Channels.MAIN.registerServerbound(RequestGui.class, (msg, ctx) -> {
			Record packet = null;
			var guilds = ctx.player().getWorld().getComponent(Components.BASE_LIST);
			var guild = guilds.get(msg.guild);
			if (guild.isPresent()) {
				var m = new FactionMember(ctx.player());
				var isOp = ctx.player().hasPermissionLevel(2);
				if (isOp || guild.get().getMembers().contains(m)) {
					packet = switch (msg.gui) {
						case "main" -> new OpenFactionGui(msg.guild, guild.get().getName());
						case "friends" -> new OpenFriendsGui(msg.guild, guild.get().getMembersAndRoles(), guild.get().getRolesAndPriorities(),
								isOp || guild.get().isAllowed(m, Perms.INVITE), isOp || guild.get().isAllowed(m, Perms.EDIT_ROLE));
						case "addFriends" -> new OpenFriendsAddGui(msg.guild, guilds.getNames());
						case "roles" -> new OpenRolesGui(msg.guild, guild.get().getRolesAndPriorities());
						default -> null;
					};
					if (packet != null)
						Channels.MAIN.serverHandle(ctx.player()).send(packet);
				}

			}

		});
		Channels.MAIN.registerClientboundDeferred(OpenFactionGui.class);
		Channels.MAIN.registerClientboundDeferred(OpenFriendsGui.class);
		Channels.MAIN.registerClientboundDeferred(OpenFriendsAddGui.class);
		Channels.MAIN.registerClientboundDeferred(OpenRolesGui.class);
		Channels.MAIN.registerServerbound(InviteMember.class, (msg, ctx) -> {
			var guild = ctx.player().getWorld().getComponent(Components.BASE_LIST).getForPlayer(ctx.player().getUuid(), false);
			if (guild.isPresent()) {
				if (guild.get().getPermsOf(new FactionMember(ctx.player())).isAllowed(Perms.INVITE)) {
					if (guild.get().addMember(msg.member, guild.get().getStartingRole(), ctx.player().getWorld()))
						Channels.MAIN.serverHandle(ctx.player()).send(new GuiPackets.ReturnValue("addMember", msg.member.getName()));
					else
						Channels.MAIN.serverHandle(ctx.player()).send(new GuiPackets.ReturnError("addMember", Text.literal(msg.member.getName() + " ne peut pas être invité")));

				} else
					Channels.MAIN.serverHandle(ctx.player()).send(new GuiPackets.ReturnError("addMember", Text.literal("Vous n'avez pas la permission 'INVITE'")));
			} else
				Channels.MAIN.serverHandle(ctx.player()).send(new GuiPackets.ReturnError("addMember", Text.literal("No Guild")));
		});
		Channels.MAIN.registerServerbound(ChangeRole.class, (msg, ctx) -> {
			var g1 = ctx.player().getWorld().getComponent(Components.BASE_LIST).getForPlayer(ctx.player().getUuid(), false);
			if (g1.isPresent()) {
				var guild = g1.get();
				var m = new FactionMember(ctx.player());
				if (guild.isAllowed(m, Perms.EDIT_ROLE)) {
					var role1 = guild.getRoleFor(m);
					var i = 0;
					if (guild.getOwner().equals(m))
						i = Integer.MAX_VALUE;
					else if (role1 != null)
						i = guild.getPriorityOfRole(role1);
					var role2 = guild.getRoleFor(msg.member);
					var i1 = 0;
					if (guild.getOwner().equals(msg.member))
						i1 = Integer.MAX_VALUE;
					else if (role2 != null)
						i1 = guild.getPriorityOfRole(role2);
					if (i1 < i)
						guild.changeRole(msg.member, msg.newRole, ctx.player().getWorld());

				}

			}
		});
		Channels.MAIN.registerClientboundDeferred(PermEdit.class);
		Channels.MAIN.registerServerbound(PermEdit.class, (msg, ctx) -> {
			var guilds = ctx.player().getWorld().getComponent(Components.BASE_LIST);
			var g = guilds.getForPlayer(ctx.player().getUuid(), false);
			var r = currentRoleEdit.get(ctx.player());
			if (g.isPresent()) {
				if (r != null) {
					var role = g.get().getRole(r);
					var m = new FactionMember(ctx.player());
					if (role != null && g.get().isAllowed(m, Perms.EDIT_ROLE)) {
						var a = g.get().getPriorityOfRole(r);
						var b = g.get().getPriorityOfRole(g.get().getRoleFor(m));
						if (g.get().getOwner().equals(m))
							b = Integer.MAX_VALUE;
						if (a < b) {
							currentRoleEdit.remove(ctx.player());
							if (g.get().replacePerm(r, msg.role))
								Channels.MAIN.serverHandle(ctx.player()).send(new GuiPackets.ReturnValue("editRole", msg.role.getName()));
							else
								Channels.MAIN.serverHandle(ctx.player()).send(new GuiPackets.ReturnError("editRole", Text.literal("Conflicting name with another role")));
						}

					}
				} else {
					if (g.get().addRole(msg.role))
						Channels.MAIN.serverHandle(ctx.player()).send(new GuiPackets.ReturnValue("addRole", msg.role.getName()));
					else
						Channels.MAIN.serverHandle(ctx.player()).send(new GuiPackets.ReturnError("editRole", Text.literal("Conflicting name with another role")));
				}

			} else
				Channels.MAIN.serverHandle(ctx.player()).send(new GuiPackets.ReturnError("editRole", Text.literal("No Guild")));
		});
		Channels.MAIN.registerServerbound(PermEditRequest.class, (msg, ctx) -> {
			var guilds = ctx.player().getWorld().getComponent(Components.BASE_LIST);
			var g = guilds.getForPlayer(ctx.player().getUuid(), false);
			if (g.isPresent()) {
				currentRoleEdit.remove(ctx.player());
				var m = new FactionMember(ctx.player());
				if (msg.role.isEmpty() && g.get().isAllowed(m, Perms.EDIT_ROLE)) {
					var p = new FactionPerm("Nouveau Role");
					Channels.MAIN.serverHandle(ctx.player()).send(new PermEdit(g.get().getId(), p));
				}
				var role = g.get().getRole(msg.role);
				if (role != null && g.get().isAllowed(m, Perms.EDIT_ROLE)) {
					var a = g.get().getPriorityOfRole(msg.role);
					var b = g.get().getPriorityOfRole(g.get().getRoleFor(m));
					if (g.get().getOwner().equals(m))
						b = Integer.MAX_VALUE;
					if (a < b) {
						currentRoleEdit.put(ctx.player(), msg.role);
						Channels.MAIN.serverHandle(ctx.player()).send(new PermEdit(g.get().getId(), role));
					}

				}
			}
		});
	}
}
