package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.blocks.ClaimBlock;
import com.diamssword.greenresurgence.network.GuiPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionGuild;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionMember;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionZone;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.Perms;
import com.diamssword.greenresurgence.systems.multiblock.DeployingMachines;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class ClaimBlockEntity extends BlockEntity implements IGuiPacketReceiver {
	public final static int maxRangeT1 = 8;
	public final static int maxRangeT2 = 16;
	public final static int maxRangeT3 = 32;
	public final static int minRange = 4;
	private UUID factionID;
	private FactionGuild faction;
	private int level = 0;
	private int range = minRange;

	public ClaimBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void receiveGuiPacket(ServerPlayerEntity player, GuiPackets.GuiTileValue msg) {
		var fac = getFaction();
		if(fac != null && fac.getPermsOf(new FactionMember(player)).isAllowed(Perms.ADMIN)) {
			if(msg.key().equals("remove") && msg.asBool()) {
				var guilds = world.getComponent(Components.BASE_LIST);
				guilds.getForPlayer(player.getUuid(), false).ifPresent(g -> {
					getRelatedZone().ifPresent(t -> {
						String mid = DeployingMachines.GENERATOR_T1;
						if(level == 1)
							mid = DeployingMachines.GENERATOR_T2;
						else if(level == 2)
							mid = DeployingMachines.GENERATOR_T3;
						var placer = DeployingMachines.instantiate(mid, level == 2 ? pos.down() : pos, getCachedState().get(ClaimBlock.FACING));
						placer.ifPresent(p -> {
							p.setDeconstructing(true);
							fac.deprecateTerrain(t, world);
							DeployingMachines.placeMachine(world, level == 2 ? pos.down() : pos, p);

						});

					});
				});

			} else if(msg.key().equals("upgrade") && msg.asBool() && level < 2) {
				var guilds = world.getComponent(Components.BASE_LIST);
				guilds.getForPlayer(player.getUuid(), false).ifPresent(g -> {
					getRelatedZone().ifPresent(t -> {
						if(g.getPermsOf(new FactionMember(player)).isAllowed(Perms.ADMIN)) {
							if(Components.PLAYER_INVENTORY.get(player).getInventory().consumeItems(new ItemStack(Items.DIAMOND, 1))) {
								String mid = level == 0 ? DeployingMachines.GENERATOR_T1 : DeployingMachines.GENERATOR_T2;
								var placer = DeployingMachines.instantiate(mid, pos, getCachedState().get(ClaimBlock.FACING));

								placer.ifPresent(p -> {
									p.setDeconstructing(true);
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
			} else if(msg.key().equals("resize")) {
				changeSize(player, msg.asInt());
			}
		}
	}

	public Optional<FactionZone> getRelatedZone() {
		var fac = getFaction();
		if(fac != null) {
			var ls = fac.getTerrainsAt(pos);
			if(ls.size() == 1)
				return Optional.of(ls.get(0));
			else if(!ls.isEmpty()) {
				for(FactionZone z : ls) {
					if(z.getBounds().getCenter().equals(pos))
						return Optional.of(z);
				}
				var smallest = ls.get(0);
				for(FactionZone z : ls) {
					if(z != smallest && z.fullyContainsBox(smallest.getBounds()))
						smallest = z;
				}
				return Optional.of(smallest);
			}

		}
		return Optional.empty();
	}

	public int getMaxRange() {
		return this.level < 2 ? (this.level == 0 ? maxRangeT1 : maxRangeT2) : maxRangeT3;

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

	public void changeSize(PlayerEntity player, int newSize) {
		if(newSize < range) {
			if(newSize < minRange)
				newSize = minRange;
			if(newSize != range) {
				this.range = newSize;
				var t = getRelatedZone();
				getFaction().addTerrain(pos, range, world);
				t.ifPresent(t1 -> getFaction().deprecateTerrain(t1, world));
			}
		} else if(newSize > range) {

			if(Components.BASE_LIST.get(world).doBoxIntersectWithOtherFaction(getFaction().getOwner().getId(), new BlockBox(pos).expand(newSize))) {
				player.sendMessage(Text.translatable("gui.green_resurgence.claim_antenna.resize.fail"));
			} else {
				this.range = newSize;
				var t = getRelatedZone();
				getFaction().addTerrain(pos, range, world);
				t.ifPresent(t1 -> getFaction().removeTerrain(t1, world));
			}
		}
		this.saveAndUpdate();
	}

	protected void saveAndUpdate() {
		this.markDirty();
		if(this.world instanceof ServerWorld sw) {sw.getChunkManager().markForUpdate(pos);}
	}

	public int getSize() {
		return range;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if(nbt.containsUuid("faction"))
			factionID = nbt.getUuid("faction");
		this.level = nbt.getInt("generator_level");
		this.range = nbt.getInt("range");
		if(range < minRange)
			range = minRange;
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if(factionID != null)
			nbt.putUuid("faction", this.factionID);
		nbt.putInt("generator_level", this.level);
		nbt.putInt("range", range);
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
