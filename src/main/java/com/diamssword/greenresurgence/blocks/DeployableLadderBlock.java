package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.MBlockEntities;
import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.blockEntities.DeployableLadderEntity;
import com.diamssword.greenresurgence.blockEntities.LootableItemBlockEntity;
import com.diamssword.greenresurgence.containers.Containers;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
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
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class DeployableLadderBlock extends BlockWithEntity {
    public static final DirectionProperty FACING=Properties.FACING;
    public static final BooleanProperty MASTER=BooleanProperty.of("master");
    public static final VoxelShape SHAPE_N= Block.createCuboidShape(0,0,5,5,16,11);
    public static final VoxelShape SHAPE_S= Block.createCuboidShape(11,0,5,16,16,11);
    public static final VoxelShape SHAPE_E= Block.createCuboidShape(5,0,0,11,16,5);
    public static final VoxelShape SHAPE_W= Block.createCuboidShape(5,0,11,11,16,16);
    public static final VoxelShape SHAPE_D= Block.createCuboidShape(5,0,5,11,16,11);
    public DeployableLadderBlock(Settings settings) {
        super(settings);
        this.getDefaultState().with(FACING,Direction.NORTH).with(MASTER,false);
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, MBlockEntities.DEPLOYABLE_LADDER, DeployableLadderEntity::tick);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(player.isSneaking() && state.get(MASTER))
        {

            if(!world.isClient)
            {
                var st1=getBlockEntity(pos,world).getOriginalState();
                world.setBlockState(pos,st1);

                if(!player.giveItemStack(new ItemStack(MItems.REMOVABLE_LADDER)))
                    player.dropStack(new ItemStack(MItems.REMOVABLE_LADDER));
            }
            BlockSoundGroup blockSoundGroup = MBlocks.DEPLOYABLE_LADDER.getDefaultState().getSoundGroup();
            world.playSound(player, pos, blockSoundGroup.getPlaceSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
            world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(player,MBlocks.DEPLOYABLE_LADDER.getDefaultState()));

            return ActionResult.CONSUME;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    @Deprecated
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case SOUTH -> {
                return SHAPE_W;
            }
            case WEST -> {
                return SHAPE_N;
            }
            case EAST -> {
                return SHAPE_S;
            }
            case DOWN -> {
                return SHAPE_D;
            }
            default -> {
                return SHAPE_E;
            }
        }
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(MASTER,false).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, MASTER);
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DeployableLadderEntity(pos,state);
    }
    public DeployableLadderEntity getBlockEntity(BlockPos pos, BlockView world)
    {
        return (DeployableLadderEntity) world.getBlockEntity(pos);
    }
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
    @Deprecated
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
