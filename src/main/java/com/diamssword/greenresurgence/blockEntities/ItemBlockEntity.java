package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.MBlockEntities;
import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.containers.GridContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ItemBlockEntity extends BlockEntity {
    private Vec3d rotation= Vec3d.ZERO;
    private  Vec3d position= Vec3d.ZERO;
    private double size=10;
    private ItemStack item= new ItemStack(Items.APPLE);
    public Direction baseDir;
    public ItemBlockEntity(BlockPos pos, BlockState state) {
        super(MBlockEntities.ITEM_BLOCK, pos, state);
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        // Save the current value of the number to the nbt
        nbt.putDouble("positionX",position.x);
        nbt.putDouble("positionY",position.y);
        nbt.putDouble("positionZ",position.z);
        nbt.putDouble("rotationX",rotation.x);
        nbt.putDouble("rotationY",rotation.y);
        nbt.putDouble("rotationZ",rotation.z);
        nbt.put("item",item.writeNbt(new NbtCompound()));
        nbt.putDouble("size",size);
        if(this.baseDir ==null)
        {
            if(this.getCachedState().getProperties().contains(Properties.HORIZONTAL_FACING))
            {
                this.baseDir=this.getCachedState().get(Properties.HORIZONTAL_FACING);
            }
        }
        if(this.baseDir!=null)
            nbt.putInt("baseDir",this.baseDir.getId());
        super.writeNbt(nbt);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if(nbt.contains("baseDir"))
            this.baseDir=Direction.byId(nbt.getInt("baseDir"));
        else {
            if(this.getCachedState().getProperties().contains(Properties.HORIZONTAL_FACING))
            {
                this.baseDir=this.getCachedState().get(Properties.HORIZONTAL_FACING);
                this.markDirty();
            }
        }
        position=new Vec3d(nbt.getDouble("positionX"),nbt.getDouble("positionY"),nbt.getDouble("positionZ"));
        rotation=new Vec3d(nbt.getDouble("rotationX"),nbt.getDouble("rotationY"),nbt.getDouble("rotationZ"));
        size=Math.max(1,nbt.getDouble("size"));
        if(nbt.contains("item"))
        {
            item=ItemStack.fromNbt(nbt.getCompound("item"));

        }
        if(this.world !=null && !this.world.isClient) {
            rotateStates();
        }
    }

    public Vec3d getRotation() {
        return rotation;
    }

    public void setRotation(Vec3d rotation) {
        this.rotation = rotation;
        saveAndUpdate();
    }

    public Vec3d getPosition() {
        return position;
    }

    public void setPosition(Vec3d position) {
        this.position = position;
        saveAndUpdate();
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = Math.max(1,size);
        saveAndUpdate();
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
        saveAndUpdate();
    }
    public GridContainer getContainer()
    {
        SimpleInventory inv=new SimpleInventory(this.getItem());
        inv.addListener((c)->{
            this.setItem(c.getStack(0));
        });
        return new GridContainer("container",inv,1,1);
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
    private void saveAndUpdate()
    {
        this.markDirty();
        this.world.updateListeners(this.pos,this.getCachedState(),this.getCachedState(), Block.NOTIFY_ALL);
    }
    private void rotateStates()
    {

        if(this.getCachedState().getProperties().contains(Properties.HORIZONTAL_FACING)) {
            Direction newDir=this.getCachedState().get(Properties.HORIZONTAL_FACING);
            var p=this.getPosition();
            var p1=this.getRotation();
            if(this.baseDir.getOpposite()==newDir) {
                this.setPosition(new Vec3d(-p.getX(),p.getY(),-p.getZ()));
                this.setRotation(new Vec3d(p1.x,p1.y+180,p1.z));
                this.baseDir=newDir;
                saveAndUpdate();
            }
            else if(this.baseDir.rotateYClockwise()==newDir)
            {
                this.setPosition(new Vec3d(-p.getZ(),p.getY(),p.getX()));
                this.setRotation(new Vec3d(p1.x,p1.y-90,p1.z));
                this.baseDir=newDir;
                saveAndUpdate();
            }
            else if(this.baseDir.rotateYCounterclockwise()==newDir)
            {
                this.setPosition(new Vec3d(p.getZ(),p.getY(),-p.getX()));
                this.setRotation(new Vec3d(p1.x,p1.y+90,p1.z));
                this.baseDir=newDir;
                saveAndUpdate();
            }
        }
    }
}
