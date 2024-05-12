package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.blockEntities.ImageBlockEntity;
import com.diamssword.greenresurgence.blockEntities.ItemBlockEntity;
import com.diamssword.greenresurgence.containers.Containers;
import com.diamssword.greenresurgence.containers.IGridContainer;
import com.diamssword.greenresurgence.containers.MultiInvScreenHandler;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuiPackets;
import com.google.common.graph.Network;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ImageBlock extends Block implements BlockEntityProvider,Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final DirectionProperty FACING=Properties.FACING;
    public static final VoxelShape SHAPE_D= Block.createCuboidShape(0,0,0,16,1,16);
    public static final VoxelShape SHAPE_U=Block.createCuboidShape(0,15,0,16,16,16);
    public static final VoxelShape SHAPE_N=Block.createCuboidShape(0,0,15,16,16,16);
    public static final VoxelShape SHAPE_S=Block.createCuboidShape(0,0,0,16,16,1);
    public static final VoxelShape SHAPE_E= Block.createCuboidShape(0,0,0,1,16,16);
    public static final VoxelShape SHAPE_W= Block.createCuboidShape(15,0,0,16,16,16);
    public ImageBlock(Settings settings) {
        super(settings);
        this.getDefaultState().with(FACING,Direction.NORTH).with(WATERLOGGED,false);

    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if(nbtCompound !=null) {
            if (nbtCompound.contains("rotation")) {
                tooltip.add(Text.literal("Rotation: "+nbtCompound.getFloat("rotation")));
            }
            if (nbtCompound.contains("sizeX")) {
                tooltip.add(Text.literal("Taille: "+nbtCompound.getFloat("sizeX")+"x"+nbtCompound.getFloat("sizeY")));
            }
            if (nbtCompound.contains("content")) {
                String str=nbtCompound.getString("content");
                if(str.length()>50)
                    str=str.substring(0,47)+"...";
                tooltip.add(Text.literal("URL: "+str));
            }
        }

    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());

        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(FACING, ctx.getSide());
    }
    @Deprecated
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING))
        {
            case DOWN -> {
                return  SHAPE_U;
            }
            case UP -> {
                return SHAPE_D;
            }
            case NORTH -> {
                return SHAPE_N;
            }
            case SOUTH -> {
                return SHAPE_S;
            }
            case WEST -> {
                return SHAPE_W;

            }
            case EAST -> {
                return SHAPE_E;
            }
        }
        return VoxelShapes.fullCube();
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ImageBlockEntity(pos,state);
    }
    public ImageBlockEntity getBlockEntity(BlockPos pos, BlockView world)
    {
        return (ImageBlockEntity) world.getBlockEntity(pos);
    }
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient && player.isCreative())
        {
            Channels.MAIN.serverHandle(player).send(new GuiPackets.GuiPacket(GuiPackets.GUI.ImageBlock,pos));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    @Deprecated
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

}
