package com.diamssword.greenresurgence.systems.character;

import com.diamssword.greenresurgence.systems.Components;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class PlayerData implements ComponentV3, ServerTickingComponent, AutoSyncedComponent {
    private EntityPose forcedPose;
    public final PlayerEntity player;
    public PlayerApparence appearance;
    public PlayerData(PlayerEntity e){
    this.player=e;
    this.appearance=new PlayerApparence(this);
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
    @Override
    public void serverTick() {

    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if(tag.contains("pose"))
            forcedPose= EntityPose.valueOf(tag.getString("pose"));
        if(tag.contains("appearance"))
            appearance.readFromNbt(tag.getCompound("appearance"));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        if(forcedPose!=null)
            tag.putString("pose",forcedPose.toString());
        var ap=new NbtCompound();
        appearance.writeToNbt(ap);
        tag.put("appearance",ap);
    }
}
