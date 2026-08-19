package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.blockEntities.ClaimBlockEntity;
import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuiPackets;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionMember;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.Perms;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ClaimBlock extends ModBlockEntity<ClaimBlockEntity> {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final VoxelShape SMALL = Block.createCuboidShape(2, 0, 2, 14, 16, 14);

	public ClaimBlock(Settings settings) {
		super(settings.strength(-1.0f, 3600000.0f).dropsNothing().allowsSpawning(Blocks::never));
	}

	@Deprecated
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SMALL;
	}

	@Override
	public Class<ClaimBlockEntity> getBlockEntityClass() {
		return ClaimBlockEntity.class;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1f;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(!world.isClient && !player.isCreative()) {
			var fac = getBlockEntity(pos, world).getFaction();
			if(fac != null) {
				if(fac.isAllowed(new FactionMember(player), Perms.PLACE)) {
					var ls = fac.getTerrainsAt(pos);
					if(ls.size() > 1) {
						Channels.MAIN.serverHandle(player).send(new GuiPackets.GuiPacket(GuiPackets.GUI.FactionClaimAntenna, pos, 3));
					} else
						Channels.MAIN.serverHandle(player).send(new GuiPackets.GuiPacket(GuiPackets.GUI.FactionClaimAntenna, pos, fac.isAllowed(new FactionMember(player), Perms.ADMIN) ? 1 : 0));
				} else
					player.sendMessage(Text.translatable("gui.green_resurgence.claim_antenna.open.denied.perm"));
			} else
				player.sendMessage(Text.translatable("gui.green_resurgence.claim_antenna.open.denied.empty"));

			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak(world, pos, state, player);
		var be = this.getBlockEntity(pos, world);
		var fac = be.getFaction();
		if(fac != null) {
			fac.deprecateTerrain(pos, world);
		}
	}

}
