package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public class FactionMember {

    private PlayerEntity player;
    private String name;
    private UUID id;
    private FactionGuild guild;

    public FactionMember(FactionGuild guild )
    {
        this.id=guild.getId();
        this.guild=guild;
        this.name=guild.getName();
    }
    public FactionMember(PlayerEntity player )
    {
        this.id=player.getUuid();
        this.player=player;
        this.name=player.getName().getString();
    }
    public UUID getId()
    {
        return id;
    }
}
