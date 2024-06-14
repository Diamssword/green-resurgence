package com.diamssword.greenresurgence.systems.character;

import net.minecraft.nbt.NbtCompound;

public class PlayerApparence {
    public float width=1;
    public float height=1.2f;
    public int hair_color=0;
    public final PlayerData parent;
    public PlayerApparence(PlayerData parent)
    {
        this.parent=parent;
    }
    public void readFromNbt(NbtCompound tag) {

        if(tag.contains("width"))
            width=Math.max(Math.min(1.4f,tag.getFloat("width")),0.7f);
        else
            width=1;
        if(tag.contains("height"))
            height=Math.max(Math.min(1.4f,tag.getFloat("height")),0.7f);
        else
            height=1;
        hair_color=tag.getInt("hair");
    }

    public void writeToNbt(NbtCompound tag) {
       tag.putFloat("width",width);
        tag.putFloat("height",height);
        tag.putInt("hair",hair_color);
    }
    public float getRestrainedHeight()
    {
        return Math.max(0.8f,Math.min(1.1f,this.height));
    }
    public float getRestrainedWidth()
    {
        return Math.max(0.8f,Math.min(1.2f,this.width));
    }
}
