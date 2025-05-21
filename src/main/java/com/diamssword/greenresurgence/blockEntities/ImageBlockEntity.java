package com.diamssword.greenresurgence.blockEntities;

import com.diamssword.greenresurgence.MBlockEntities;
import com.diamssword.greenresurgence.containers.GridContainer;
import com.diamssword.greenresurgence.network.GuiPackets;
import io.wispforest.owo.nbt.NbtKey;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
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
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class ImageBlockEntity extends BlockEntity {
    private float rotation= 0;
    private Vec2f size= new Vec2f(1,1);
    private String content="";
    private boolean stretch=false;
    private boolean offsetX=false;
    private boolean offsetY=false;
    public ImageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        // Save the current value of the number to the nbt
        nbt.putFloat("sizeX",size.x);
        nbt.putFloat("sizeY",size.y);
        nbt.putFloat("rotation",rotation);
        nbt.putString("content",content);
        nbt.putBoolean("stretch",stretch);
        nbt.putBoolean("offsetX",offsetX);
        nbt.putBoolean("offsetY",offsetY);
        super.writeNbt(nbt);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        size=new Vec2f(Math.max(1,nbt.getFloat("sizeX")),Math.max(1,nbt.getFloat("sizeY")));
        rotation=nbt.getFloat("rotation");
        content=nbt.getString("content");
        stretch=nbt.getBoolean("stretch");
        offsetX=nbt.getBoolean("offsetX");
        offsetY=nbt.getBoolean("offsetY");

    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        saveAndUpdate();
    }




    public Vec2f getSize() {
        return size;
    }

    public void setSizeY(float size) {
        this.size = new Vec2f(this.size.x,size);
        saveAndUpdate();
    }
    public void setSizeX(float size) {
        this.size = new Vec2f(size,this.size.y);
        saveAndUpdate();
    }
    public void setSize(Vec2f size) {
        this.size = size;
        saveAndUpdate();
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content=content;
        saveAndUpdate();
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

    public void setStretch(boolean b) {
        this.stretch=b;
        saveAndUpdate();
    }

    public boolean isStretch() {
        return stretch;
    }
    public void setOffsetX(boolean b) {
        this.offsetX=b;
        saveAndUpdate();
    }

    public boolean isOffsetX() {
        return offsetX;
    }
    public void setOffsetY(boolean b) {
        this.offsetY=b;
        saveAndUpdate();
    }

    public boolean isOffsetY() {
        return offsetY;
    }
    public void receiveGuiPacket(GuiPackets.GuiTileValue msg)
    {
        switch (msg.key())
        {
            case "sizeX"->this.setSizeX(msg.asFloat());
            case "sizeY"->this.setSizeY(msg.asFloat());
            case "rotation"->this.setRotation(msg.asFloat());
            case "stretch"->this.setStretch(msg.asInt()==1);
            case "offsetX"->this.setOffsetX(msg.asInt()==1);
            case "offsetY"->this.setOffsetY(msg.asInt()==1);
            case "url"->this.setContent(msg.value());
        }
    }
}
