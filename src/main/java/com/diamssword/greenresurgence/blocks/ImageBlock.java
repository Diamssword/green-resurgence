package com.diamssword.greenresurgence.blocks;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ImageBlockEntity;
import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuiPackets;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
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

public class ImageBlock extends ModBlockEntity<ImageBlockEntity> implements Waterloggable {
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
    public Identifier getCustomBlockEntityName()
    {
        return GreenResurgence.asRessource("image_block");
    }
    @Override
    public Class<ImageBlockEntity> getBlockEntityClass() {
        return ImageBlockEntity.class;
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

}
