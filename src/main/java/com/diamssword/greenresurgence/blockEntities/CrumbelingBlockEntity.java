package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class CrumbelingBlockEntity extends BlockEntity {
	public boolean broken = false;
	public BlockState block = Blocks.AMETHYST_BLOCK.getDefaultState();
	public long restoreAt = 0;

	public CrumbelingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public BlockState getRealBlock() {
		return this.block != null ? this.block : Blocks.AIR.getDefaultState();
	}

	public BlockState getDisplayBlock() {


		if (block != null && block.getBlock() == MBlocks.CRUMBELING_BLOCK)
			block = Blocks.AMETHYST_BLOCK.getDefaultState();
		if (broken)
			return Blocks.AIR.getDefaultState();
		else
			return getRealBlock();

	}

	public void setRealBlock(BlockState state) {
		this.block = state;
		markChange();
	}

	private void markChange() {
		this.markDirty();
		this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
	}

	public void restoreDurability() {
		this.broken = false;
		markChange();
	}

	public void triggerBlock() {
		if (!this.broken) {
			getWorld().playSound(null, this.pos, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 0.5f, 1f + (float) Math.random());
			getWorld().emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
			this.broken = true;
			this.restoreAt = System.currentTimeMillis() + (GreenResurgence.CONFIG.serverOptions.cooldowns.respawnCrumbelingBlockInSec() * 1000L);
			markChange();
		}
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		// Save the current value of the number to the nbt
		nbt.putBoolean("broken", broken);
		nbt.putLong("restore", restoreAt);
		if (block == null)
			block = Blocks.AIR.getDefaultState();
		nbt.put("block", NbtHelper.fromBlockState(block));
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		broken = nbt.getBoolean("broken");
		restoreAt = nbt.getLong("restore");
		block = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound("block"));
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

	public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState blockState, CrumbelingBlockEntity t) {
		if (world.getTime() % 200 == 0) {
			if (System.currentTimeMillis() > t.restoreAt) {
				if (t.broken)
					t.restoreDurability();
			}
		}
	}


}
