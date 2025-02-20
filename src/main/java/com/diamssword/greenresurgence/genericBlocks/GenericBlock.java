package com.diamssword.greenresurgence.genericBlocks;

import com.diamssword.greenresurgence.blocks.SideShelfBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GenericBlock extends Block implements IChairable {
    private final GenericBlockSet.Transparency transparency;
    private final boolean noHitbox;
    private final GenericBlockSet.HitBox hitbox;
    private final float damage;
    private final boolean isChair;
    private final float chairLvl;
    public GenericBlock(Settings settings, GenericBlockSet.GenericBlockProp props) {
        super(settings);
        this.transparency=props.transparency;
        this.noHitbox=!props.solid;
        this.hitbox=props.hitbox;
        this.damage=props.damage;
        this.isChair=props.isSeat;
        this.chairLvl=props.seatLevel;
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if(noHitbox)
            return VoxelShapes.empty();
        return super.getCollisionShape(state,world,pos,context);
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return hitbox.shape;
    }
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        if(transparency== GenericBlockSet.Transparency.UNDEFINED||transparency== GenericBlockSet.Transparency.OPAQUE)
            return  super.getAmbientOcclusionLightLevel(state,world,pos);
        return 1.0F;
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if(damage>0)
            entity.damage(world.getDamageSources().cactus(), damage);
    }
    @Deprecated
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient && canUse()) {
            this.sit(player, pos);
            return ActionResult.SUCCESS;
        }
        else
            return ActionResult.PASS;
    }
    @Override
    public float sittingHeight() {
        return chairLvl;
    }

    @Override
    public boolean canUse() {
        return isChair;
    }
}
