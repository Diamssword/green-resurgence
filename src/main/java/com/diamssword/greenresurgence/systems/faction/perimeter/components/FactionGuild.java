package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import com.diamssword.greenresurgence.events.BaseEventCallBack;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.CurrentZonePacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FactionGuild {
	private String name;
	private final UUID id;
	private FactionMember owner;
	private List<ServerPlayerEntity> inBase = new ArrayList<>();
	private final Map<String, FactionPerm> roles = new HashMap<>();
	private final Map<String, Integer> rolesPriority = new HashMap<>();
	private String startingRole;
	/**
	 * The string is the name of the linked role
	 */
	private Map<FactionMember, String> members = new HashMap<>();
	private final Map<FactionMember, FactionPerm> allies = new HashMap<>();
	private final List<FactionZone> terrains = new ArrayList<>();

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public FactionTerrainStorage storage = new FactionTerrainStorage();
	public TerrainEnergyStorage energyStorage = new TerrainEnergyStorage();

	public FactionMember getOwner() {
		return owner;
	}

	public List<FactionMember> getMembers() {
		return members.keySet().stream().toList();
	}

	public boolean changeRole(FactionMember member, String role, World world) {
		if (roles.containsKey(role)) {
			members.put(member, role);
			onMemberUpdate(member, world);
		}
		return false;
	}

	public Map<FactionMember, String> getMembersAndRoles() {
		var m = new HashMap<FactionMember, String>();
		m.putAll(members);
		m.put(owner, "Chef");
		return m;
	}

	public Map<String, FactionPerm> getRoles() {
		return roles;
	}

	public Map<String, Integer> getRolesAndPriorities() {
		return rolesPriority;
	}

	public FactionPerm getRole(String name) {
		return roles.get(name);
	}

	public int getPriorityOfRole(String role) {
		return rolesPriority.getOrDefault(role, 1);
	}

	private void onMemberUpdate(FactionMember member, World world) {
		if (!world.isClient) {
			if (member.isPlayer()) {
				member.asPlayer(world).ifPresent(v -> {
					CurrentZonePacket.sendDebugZone(world, v);
					Channels.MAIN.serverHandle(v).send(new CurrentZonePacket.MyGuild(this.getId(), this.getName()));
				});

			}
		}
	}

	public boolean replacePerm(String oldName, FactionPerm newPerm) {
		if (oldName.equals(newPerm.getName())) {
			roles.put(oldName, newPerm);
			return true;
		} else if (!roles.containsKey(newPerm.getName())) {
			var list = new HashMap<FactionMember, String>();
			members.forEach((k, v) -> {
				if (v.equals(oldName))
					list.put(k, newPerm.getName());
				else
					list.put(k, v);
			});
			roles.put(newPerm.getName(), newPerm);
			rolesPriority.put(newPerm.getName(), rolesPriority.get(oldName));
			rolesPriority.remove(oldName);
			members = list;
			roles.remove(oldName);
			return true;
		}
		return false;

	}

	public boolean addMember(FactionMember member, String role, World world) {
		if (roles.containsKey(role)) {
			if (!members.containsKey(member)) {
				members.put(member, role);
				onMemberUpdate(member, world);
				return true;
			}
		}
		return false;
	}

	public void addZone(BlockPos center, int size, @Nullable World world) {
		terrains.add(new FactionZone(this, center, size));
		this.getOwner().asPlayer(world).ifPresent(p -> {
			if (!p.getWorld().isClient)
				CurrentZonePacket.sendDebugZone(p.getWorld(), null);
		});
	}

	public static FactionGuild createForPlayer(PlayerEntity player, BlockPos pos) {
		var r = new FactionGuild();
		r.owner = new FactionMember(player);
		r.terrains.add(new FactionZone(r, pos, 16));
		r.name = player.getName().getString() + "'s Claim" + r.getId().toString().substring(r.getId().toString().length() - 5);
		r.addDefaultRole();
		return r;
	}

	private void addDefaultRole() {
		startingRole = "Membre";
		var f = new FactionPerm(startingRole);
		f.setPerm(Perms.BREAK, true);
		f.setPerm(Perms.PLACE, true);
		f.setPerm(Perms.INVENTORY, true);
		roles.put(startingRole, f);
		rolesPriority.put(startingRole, 1);
	}

	protected FactionGuild(UUID id) {
		this.id = id;
	}

	protected FactionGuild() {
		this.id = UUID.randomUUID();
	}

	public boolean isIn(Vec3i pos) {
		for (FactionZone b : terrains) {
			if (b.isIn(pos))
				return true;
		}
		return false;
	}

	public List<FactionZone> getAllTerrains() {
		return terrains;
	}

	public Optional<FactionZone> getTerrainAt(Vec3i pos) {
		for (var b : terrains) {
			if (b.isIn(pos))
				return Optional.of(b);
		}
		return Optional.empty();
	}

	public FactionPerm getPermsOf(FactionMember member) {
		if (this.owner.equals(member))
			return FactionPerm.ALL;
		var p = this.members.get(member);

		if (p != null)
			return this.roles.get(p);
		var p1 = this.allies.get(member);
		if (p1 != null)
			return p1;
		return FactionPerm.NONE;
	}

	public boolean needSurvival(FactionMember member) {
		if (this.owner.equals(member))
			return true;
		var p = this.members.get(member);

		if (p != null)
			return this.roles.get(p).needSurvival();
		var p1 = this.allies.get(member);
		if (p1 != null)
			return p1.needSurvival();
		return false;
	}

	public boolean isAllowed(FactionMember member, Perms perm) {
		if (this.owner.equals(member))
			return true;
		var p = this.members.get(member);

		if (p != null)
			return this.roles.get(p).isAllowed(perm);
		var p1 = this.allies.get(member);
		if (p1 != null)
			return p1.isAllowed(perm);
		return false;
	}

	public void tick(ServerWorld world) {
		world.getPlayers().forEach(p -> {
			if (inBase.contains(p)) {
				if (!this.isIn(p.getBlockPos())) {
					inBase.remove(p);
					BaseEventCallBack.LEAVE.invoker().enterOrLeave(p, this);
				}

			} else {
				if (this.isIn(p.getBlockPos())) {
					inBase.add(p);
					BaseEventCallBack.ENTER.invoker().enterOrLeave(p, this);
				}
			}
		});
		inBase = new ArrayList<>(inBase.stream().filter(v -> !v.isDead()).toList());
	}

	public String getStartingRole() {
		return this.startingRole;
	}

	public static FactionGuild fromNBT(NbtCompound tag) {
		var id = tag.getUuid("id");
		if (id != null) {
			var res = new FactionGuild(id);
			res.name = tag.getString("name");
			NbtList ls = tag.getList("terrains", NbtList.COMPOUND_TYPE);
			ls.forEach(c -> {
				FactionZone b = new FactionZone(res, (NbtCompound) c);
				res.terrains.add(b);
			});
			res.owner = new FactionMember(tag.getCompound("owner"));
			var lsRoles = tag.getList("roles", NbtElement.COMPOUND_TYPE);
			res.roles.clear();
			res.members.clear();
			res.rolesPriority.clear();
			for (var el : lsRoles) {
				var perm = FactionPerm.fromNBT((NbtCompound) el);
				if (perm != null) {
					res.roles.put(perm.getName(), perm);
					res.rolesPriority.put(perm.getName(), ((NbtCompound) el).getInt("priority"));
				}
			}
			var br = tag.getString("defaultRole");
			if (res.roles.containsKey(br))
				res.startingRole = br;
			else
				res.startingRole = res.roles.keySet().stream().findFirst().get();
			var ml = tag.getCompound("members");
			for (var k : ml.getKeys()) {
				if (res.roles.containsKey(k)) {
					var tm = ml.getList(k, NbtElement.COMPOUND_TYPE);
					tm.forEach(v -> {
						res.members.put(new FactionMember((NbtCompound) v), k);
					});
				}
			}
			var lsAllies = tag.getList("allies", NbtElement.COMPOUND_TYPE);
			res.allies.clear();
			lsAllies.forEach(v -> {
				NbtCompound tc = (NbtCompound) v;
				res.allies.put(new FactionMember(tc.getCompound("member")), FactionPerm.fromNBT(tc.getCompound("perms")));
			});
			return res;
		}
		return null;
	}

	public void writeNbt(NbtCompound tag) {
		tag.putUuid("id", id);
		tag.putString("name", name);
		var t3 = new NbtCompound();
		owner.writeNbt(t3);
		tag.put("owner", t3);
		NbtList zones = new NbtList();
		this.terrains.forEach(b -> {
			var tg = new NbtCompound();
			b.writeNbt(tg);
			zones.add(tg);
		});
		tag.put("terrains", zones);
		var t1 = new NbtCompound();
		storage.toNBT(t1);
		tag.put("storage", t1);
		var t2 = new NbtCompound();
		energyStorage.toNBT(t2);
		tag.put("energy", t1);
		var permLS = new NbtList();
		var memberLs = new NbtCompound();
		for (var p : this.roles.values()) {
			var nb = p.toNBT();
			nb.putInt("priority", this.rolesPriority.getOrDefault(p.getName(), 1));
			permLS.add(nb);
			var membs = getMembersWithRole(p.getName());
			if (!membs.isEmpty()) {
				var ls = new NbtList();
				membs.forEach(c -> {
					var e = new NbtCompound();
					c.writeNbt(e);
					ls.add(e);
				});
				memberLs.put(p.getName(), ls);
			}
		}
		tag.put("roles", permLS);
		tag.putString("defaultRole", this.startingRole);
		tag.put("members", memberLs);
		var lsAl = new NbtList();
		allies.forEach((k, v) -> {
			var e = new NbtCompound();
			var ne = new NbtCompound();
			k.writeNbt(ne);
			e.put("member", ne);
			e.put("perms", v.toNBT());
			lsAl.add(e);
		});
		tag.put("allies", lsAl);
	}

	public List<FactionMember> getMembersWithRole(String role) {
		var res = new ArrayList<FactionMember>();
		this.members.forEach((a, b) -> {
			if (b.equals(role))
				res.add(a);
		});
		return res;
	}

	public List<FactionMember> getMembersWithPerms(Perms... perms) {
		var m1 = new ArrayList<FactionMember>();
		roles.forEach((k, v) -> {
			if (v.isAllowed(perms))
				m1.addAll(this.getMembersWithRole(k));
		});
		m1.add(owner);
		return m1;
	}

	public String getRoleFor(FactionMember m) {
		return members.get(m);
	}

	public boolean addRole(FactionPerm role) {
		if (!roles.containsKey(role.getName())) {
			roles.put(role.getName(), role);
			rolesPriority.put(role.getName(), 1);
			return true;
		}
		return false;
	}

	public boolean removeTerrainAt(BlockPos p, World world) {
		var t = getTerrainAt(p);
		return t.map(v -> {
			this.terrains.remove(t.get());
			this.getOwner().asPlayer(world).ifPresent(p1 -> {
				if (!p1.getWorld().isClient)
					CurrentZonePacket.sendDebugZone(p1.getWorld(), null);
			});
			return true;
		}).orElse(false);

	}
}
