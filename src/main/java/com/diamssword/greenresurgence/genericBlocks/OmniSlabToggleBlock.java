package com.diamssword.greenresurgence.genericBlocks;

import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.light.BlockLightStorage;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class OmniSlabToggleBlock extends OmniSlabBlock {
    public static final EnumProperty<Direction> TYPE = Properties.FACING;
    public static final BooleanProperty TOGGLE = Properties.OPEN;
    public OmniSlabToggleBlock(Settings settings, boolean carpet, boolean passthrough) {
        super(settings,carpet,passthrough);
        this.setDefaultState((BlockState)((BlockState)this.getDefaultState().with(TOGGLE, false)));
    }
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TOGGLE,TYPE, WATERLOGGED);
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.setBlockState(pos, state.with(TOGGLE, !state.get(TOGGLE)));
        boolean b=state.get(TOGGLE);
        world.playSound(player, pos, b ? SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE: SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
        world.emitGameEvent(player, b ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        return ActionResult.SUCCESS;
    }

}

