package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.character.customPoses.IPlayerCustomPose;
import com.diamssword.greenresurgence.systems.character.stats.PlayerStats;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class PlayerData implements ComponentV3, ServerTickingComponent, ClientTickingComponent, AutoSyncedComponent {
    private EntityPose forcedPose;
    private String customPoseID;
    private IPlayerCustomPose customPose;
    public final PlayerEntity player;
    public PlayerApparence appearance;
    public PlayerStats stats;
    private NbtCompound carriedEntity;
    public PlayerData(PlayerEntity e){
    this.player=e;
    this.appearance=new PlayerApparence(this);
    this.stats=new PlayerStats(this);
    }
    public boolean isForcedPose()
    {
        return forcedPose!=null && forcedPose != EntityPose.STANDING;
    }
    public EntityPose getPose()
    {
        return forcedPose;
    }
    public void setForcedPose(EntityPose pose)
    {
        forcedPose=pose;
        player.setPose(pose);
        Components.PLAYER_DATA.sync(player);
    }
    public void placeCarriedEntity()
    {
        if(this.carriedEntity!=null)
        {
            getCarriedEntity().ifPresent(et-> player.getWorld().spawnEntity(et));
            this.carriedEntity=null;
        }
    }
    public boolean isCarryingEntity()
    {
        return this.carriedEntity !=null;
    }
    public Optional<Entity> getCarriedEntity()
    {
        if(this.carriedEntity!=null)
        {
            return EntityType.getEntityFromNbt(this.carriedEntity,player.getWorld()).map(e->{
                e.updatePosition(player.getX(),player.getY(),player.getZ());
                return  e;
            });
        }
        return Optional.empty();
    }
    public void setCarriedEntity(Entity e)
    {

        placeCarriedEntity();
        var tag=new NbtCompound();
        e.saveSelfNbt(tag);
        this.carriedEntity=tag;
        e.remove(Entity.RemovalReason.DISCARDED);
        Components.PLAYER_DATA.sync(player);
    }
    public IPlayerCustomPose getCustomPose()
    {
        return customPose;
    }
    public String getCustomPoseID()
    {
        return customPoseID;
    }
    public void setCustomPose(String id)
    {
        if(id !=null) {
            customPose = PosesManager.createPose(id, player);
            if (customPose != null)
                customPoseID = id;
            else
                customPoseID = null;
        }
        else {
            customPoseID = null;
            customPose=null;
        }
        player.calculateDimensions();
        Components.PLAYER_DATA.sync(player);
    }

    @Override
    public void serverTick() {
        this.appearance.tick();
        if(customPose !=null)
        {
            if(customPose.shouldExitPose(player))
            {
                setCustomPose(null);
            }
            else
            {
                customPose.tick(player);
            }
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if(tag.contains("pose"))
            forcedPose= EntityPose.valueOf(tag.getString("pose"));
        if(tag.contains("appearance"))
            appearance.readFromNbt(tag.getCompound("appearance"));
        if(tag.contains("stats"))
            stats.read(tag.getCompound("stats"));
        if(tag.contains("carriedEntity"))
            this.carriedEntity=tag.getCompound("carriedEntity");
        if(tag.contains("customPoseID"))
        {
            var d=tag.getString("customPoseID");
            if(!d.equals(customPoseID)|| customPose ==null) {
                customPose = PosesManager.createPose(d, player);
                player.calculateDimensions();
            }
            customPoseID=d;
        }
        else
        {
            customPose=null;
            customPoseID=null;
            player.calculateDimensions();
        }
    }
    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        NbtCompound tag = new NbtCompound();
        if(forcedPose!=null)
            tag.putString("pose",forcedPose.toString());
        var ap=new NbtCompound();
        appearance.writeToNbt(ap,true);
        tag.put("appearance",ap);
        tag.put("stats",stats.write());
        if(carriedEntity !=null)
            tag.put("carriedEntity",carriedEntity);
        if(customPoseID !=null)
            tag.putString("customPoseID",customPoseID);
        buf.writeNbt(tag);
    }
    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        NbtCompound tag = buf.readNbt();
        if (tag != null) {
            this.readFromNbt(tag);
        }
    }
    @Override
    public void writeToNbt(NbtCompound tag) {
        if(forcedPose!=null)
            tag.putString("pose",forcedPose.toString());
        var ap=new NbtCompound();
        appearance.writeToNbt(ap,false);
        tag.put("appearance",ap);
        tag.put("stats",stats.write());
        if(customPoseID !=null)
            tag.putString("customPoseID",customPoseID);
        if(carriedEntity !=null)
            tag.put("carriedEntity",carriedEntity);
    }

    @Override
    public void clientTick() {
        if(customPose !=null)
        {
            customPose.tick(player);
        }
    }
}
