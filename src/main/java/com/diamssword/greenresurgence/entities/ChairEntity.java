package com.diamssword.greenresurgence.entities;

import com.diamssword.greenresurgence.MEntities;
import com.diamssword.greenresurgence.genericBlocks.IChairable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChairEntity extends Entity {
    IChairable chair;
    public ChairEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }
    public ChairEntity(World world, BlockPos pos, IChairable chair) {
        this(MEntities.CHAIR, world);
        this.chair=chair;
        this.setPosition(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5);
        this.prevX = pos.getX()+0.5;
        this.prevY = pos.getY()+0.5;
        this.prevZ = pos.getZ()+0.5;
    }
    @Override
    public void tick()
    {
        super.tick();
        if(!hasPlayerRider() && !this.getWorld().isClient)
        {
           this.discard();
        }
        if(this.chair==null)
        {
            var st=this.getWorld().getBlockState(this.getBlockPos());
            if(st.getBlock() instanceof IChairable)
            {
                this.chair= (IChairable) st.getBlock();
            }
        }
    }
    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }
    protected Entity.MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }
    public boolean collidesWith(Entity other) {
        return false;
    }
    public boolean isCollidable() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }
    public double getMountedHeightOffset() {return chair !=null?chair.sittingHeight():0.0f;}
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

}
