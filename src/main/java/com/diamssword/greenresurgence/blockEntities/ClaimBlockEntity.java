package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.network.GuiPackets;
import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionGuild;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ClaimBlockEntity extends BlockEntity implements IGuiPacketReceiver {
	private UUID factionID;
	private FactionGuild faction;

	public ClaimBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void receiveGuiPacket(ServerPlayerEntity player, GuiPackets.GuiTileValue msg) {

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
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if(factionID != null)
			nbt.putUuid("faction", this.factionID);
	}
}
