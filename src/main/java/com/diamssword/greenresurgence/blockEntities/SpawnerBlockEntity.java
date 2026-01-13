package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.containers.FilteredInventory;
import com.diamssword.greenresurgence.containers.grids.GridContainer;
import com.diamssword.greenresurgence.items.CustomSpawnEgg;
import com.diamssword.greenresurgence.network.GuiPackets;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class SpawnerBlockEntity extends BlockEntity implements IGuiPacketReceiver {
	private FilteredInventory eggs = new FilteredInventory(27, (t, s) -> s.getItem() instanceof SpawnEggItem || s.getItem() instanceof CustomSpawnEgg).setSingleItem(true);
	private BlockState camo = Blocks.COPPER_BLOCK.getDefaultState();
	private boolean locked = true;
	private final MyMobSpawnerLogic logic = new MyMobSpawnerLogic() {
		@Override
		public void sendStatus(World world, BlockPos pos, int status) {
			world.addSyncedBlockEvent(pos, Blocks.SPAWNER, status, 0);
		}


		@Override
		public void serverTick(ServerWorld world, BlockPos pos) {
			super.serverTick(world, pos);
		}
	};

	public SpawnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		eggs.addListener(c -> {
			if(world != null && !world.isClient) {
				eggsToSpawnList(eggs.stacks);
				this.markDirty();
			}
		});
	}

	public BlockState getCamo() {
		return camo;
	}

	public void setCamo(BlockState camo) {
		this.camo = camo;
		this.markDirty();
		if(this.world instanceof ServerWorld sw) {sw.getChunkManager().markForUpdate(pos);}
	}

	public void receiveGuiPacket(ServerPlayerEntity player, GuiPackets.GuiTileValue msg) {
		if(!player.isCreative())
			return;
		switch(msg.key()) {
			case "max_entity" -> this.logic.setMaxNearbyEntities(msg.asInt());
			case "player_range" -> this.logic.setRequiredPlayerRange(msg.asInt());
			case "spawncount" -> this.logic.setSpawnCount(msg.asInt());
			case "cooldown" -> this.logic.setCooldown(msg.asInt() * 1000);
			case "radius" -> this.logic.setSpawnRange(msg.asInt());
			case "height" -> this.logic.setSpawnHeight(msg.asInt());
			case "floor" -> this.logic.setFloorCheck(msg.asBool());
			case "lock" -> this.setLocked(msg.asBool());
		}
		this.markDirty();
		if(this.world instanceof ServerWorld sw) {sw.getChunkManager().markForUpdate(pos);}
	}

	public GridContainer getCamoContainer() {
		SimpleInventory inv = new FilteredInventory(1, (t, s) -> s.isEmpty() || s.getItem() instanceof BlockItem).setSingleItem(true);

		var bi = BlockItem.BLOCK_ITEMS.get(this.getCamo().getBlock());
		if(bi != null)
			inv.setStack(0, bi.getDefaultStack().copyWithCount(1));
		inv.addListener(l -> {
			if(l.getStack(0).getItem() instanceof BlockItem b) {
				this.setCamo(b.getBlock().getDefaultState());
			}
		});
		return new GridContainer("camo", inv, 1, 1);
	}

	public GridContainer getContainer() {
		return new GridContainer("container", eggs, 9, 3);
	}

	protected void eggsToSpawnList(List<ItemStack> eggs) {
		var builder = DataPool.<MobSpawnerEntry>builder();
		for(ItemStack egg : eggs) {
			if(egg.getItem() instanceof SpawnEggItem eg) {
				var type = eg.getEntityType(egg.getNbt());
				var id = Registries.ENTITY_TYPE.getId(type);
				var nbt = egg.hasNbt() ? egg.getNbt().copy() : new NbtCompound();
				nbt.putString("id", id.toString());
				builder.add(new MobSpawnerEntry(nbt, Optional.empty()), 1);
			} else if(egg.getItem() instanceof CustomSpawnEgg eg) {
				var type = eg.getEntityType(egg.getNbt());
				type.ifPresent(v -> {
					var id = Registries.ENTITY_TYPE.getId(v);
					var nbt = egg.hasNbt() ? egg.getNbt().copy() : new NbtCompound();
					nbt.putString("id", id.toString());
					nbt.getCompound("EntityTag").putBoolean("customEgg", true);
					builder.add(new MobSpawnerEntry(nbt, Optional.empty()), 1);
				});

			}
		}
		this.logic.setEntries(builder.build());
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.logic.readNbt(this.world, this.pos, nbt);
		camo = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound("block"));
		eggs.readNbtList(nbt.getList("eggs", NbtElement.COMPOUND_TYPE));
		locked = nbt.getBoolean("locked");
		if(world != null && !world.isClient)
			eggsToSpawnList(eggs.stacks);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		this.logic.writeNbt(nbt);
		nbt.put("eggs", eggs.toNbtList());
		nbt.put("block", NbtHelper.fromBlockState(camo));
		nbt.putBoolean("locked", locked);
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
		this.markDirty();
	}

	public static void serverTick(World world, BlockPos pos, BlockState state, SpawnerBlockEntity blockEntity) {
		if(!blockEntity.locked)
			blockEntity.logic.serverTick((ServerWorld) world, pos);
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound nbtCompound = this.createNbt();
		nbtCompound.remove("SpawnPotentials");
		return nbtCompound;
	}

	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		return this.logic.handleStatus(this.world, type) || super.onSyncedBlockEvent(type, data);
	}

	@Override
	public boolean copyItemDataRequiresOperator() {
		return true;
	}

	public MyMobSpawnerLogic getLogic() {
		return this.logic;
	}
}
