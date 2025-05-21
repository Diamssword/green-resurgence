package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.crafting.CraftingProvider;
import com.diamssword.greenresurgence.systems.crafting.SimpleRecipe;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionZone;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CrafterBlockEntity extends BlockEntity {

	private final int progress = 0;
	private SimpleRecipe current;
	private FactionZone terrain;
	private final boolean canCraft = false;
	private CraftingProvider status;

	public CrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public boolean craftRecipe(SimpleRecipe recipe, @Nullable PlayerEntity player) {
		if (terrain != null) {
			status = new CraftingProvider().setForTerrain(terrain, player);
			var st = status.getRecipeStatus(recipe, player);
			return st.canCraft;
		}
		return false;
	}

	public static void tick(World world, BlockPos pos, BlockState state, CrafterBlockEntity blockEntity) {
		if (world.getTime() % 10 == 0) {
			var bl = world.getComponent(Components.BASE_LIST);
			bl.getTerrainAt(pos).ifPresent(p -> blockEntity.terrain = p);

		}
	}

	@Override
	public void writeNbt(NbtCompound nbt) {

		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
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
