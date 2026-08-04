package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.blocks.ClaimBlock;
import com.diamssword.greenresurgence.network.GuiPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionGuild;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionMember;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.Perms;
import com.diamssword.greenresurgence.systems.multiblock.DeployingMachines;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ClaimBlockEntity extends BlockEntity implements IGuiPacketReceiver {
	private UUID factionID;
	private FactionGuild faction;
	private int level = 0;

	public ClaimBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void receiveGuiPacket(ServerPlayerEntity player, GuiPackets.GuiTileValue msg) {
		if(msg.key().equals("remove") && msg.asBool()) {
			var guilds = world.getComponent(Components.BASE_LIST);
			guilds.getForPlayer(player.getUuid(), false).ifPresent(g -> {
				g.getTerrainAt(pos).ifPresent(t -> {
					if(g.getPermsOf(new FactionMember(player)).isAllowed(Perms.ADMIN)) {
						String mid = DeployingMachines.GENERATOR_T1;
						if(level == 1)
							mid = DeployingMachines.GENERATOR_T2;
						else if(level == 2)
							mid = DeployingMachines.GENERATOR_T3;
						var placer = DeployingMachines.instantiate(mid, level == 2 ? pos.down() : pos, getCachedState().get(ClaimBlock.FACING));
						placer.ifPresent(p -> {
							p.setDeconstructing(true);
							var fac = this.getFaction();
							if(fac != null) {
								fac.deprecateTerrain(pos, world);
							}
							DeployingMachines.placeMachine(world, level == 2 ? pos.down() : pos, p);

						});

					}
				});
			});

		} else if(msg.key().equals("upgrade") && msg.asBool() && level < 2) {
			var guilds = world.getComponent(Components.BASE_LIST);
			guilds.getForPlayer(player.getUuid(), false).ifPresent(g -> {
				g.getTerrainAt(pos).ifPresent(t -> {
					if(g.getPermsOf(new FactionMember(player)).isAllowed(Perms.ADMIN)) {
						if(Components.PLAYER_INVENTORY.get(player).getInventory().consumeItems(new ItemStack(Items.DIAMOND, 1))) {
							String mid = level == 0 ? DeployingMachines.GENERATOR_T1 : DeployingMachines.GENERATOR_T2;
							var placer = DeployingMachines.instantiate(mid, pos, getCachedState().get(ClaimBlock.FACING));
							System.out.println("from:" + mid);
							placer.ifPresent(p -> {
								p.setDeconstructing(true);
								System.out.println("to:" + (level == 0 ? DeployingMachines.GENERATOR_T2 : DeployingMachines.GENERATOR_T3));
								var placer1 = DeployingMachines.instantiate(level == 0 ? DeployingMachines.GENERATOR_T2 : DeployingMachines.GENERATOR_T3, pos, getCachedState().get(ClaimBlock.FACING));
								placer1.ifPresent(p1 -> {
									p1.getExtraDatas().putUuid("faction", this.factionID);
									p1.getExtraDatas().putInt("level", this.level + 1);
									DeployingMachines.placeMachine(world, pos, p).setNextMachine(p1);
								});

							});
						}
					}
				});
			});
		}
	}

	@Nullable
	public UUID getFactionID() {
		return factionID;
	}

	public FactionGuild getFaction() {
		if(faction == null && world != null && factionID != null) {
			var guilds = world.getComponent(Components.BASE_LIST);
			guilds.get(factionID).ifPresent(f -> faction = f);
		}
		return faction;
	}

	public void setLevel(int level) {
		this.level = level;
		this.markDirty();
	}

	public int getLevel() {
		return level;
	}

	public void setFaction(UUID factionId) {
		this.factionID = factionId;
		this.faction = null;
		this.markDirty();
	}

	public void setFaction(FactionGuild faction) {
		this.factionID = faction.getId();
		this.faction = faction;
		this.markDirty();
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if(nbt.containsUuid("faction"))
			factionID = nbt.getUuid("faction");
		this.level = nbt.getInt("generator_level");
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if(factionID != null)
			nbt.putUuid("faction", this.factionID);
		nbt.putInt("generator_level", this.level);
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return createNbt();
	}
}
