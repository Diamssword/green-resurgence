package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import com.diamssword.greenresurgence.systems.faction.perimeter.TerrainInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3i;

import java.util.*;

public class FactionGuild {
    private String name;
    private UUID id;
    private FactionMember owner;
    private Map<String,FactionRole> roles;
    /**
     * The string is the name of the linked role
     */
    private Map<FactionMember,String> members = new HashMap<>();
    private Map<FactionMember,FactionPerm> allies = new HashMap<>();
    private List<FactionZone> terrains = new ArrayList<>();

    public UUID getId()
    {
        return id;
    }

    public String getName() {
        return name;
    }

    public FactionMember getOwner() {
        return owner;
    }
    public boolean isIn(Vec3i pos)
    {
        for(FactionZone b : terrains)
        {
            if(b.isIn(pos))
                return true;
        }
        return false;
    }
    public Optional<FactionZone> getTerrainAt(Vec3i pos)
    {
        for (var b : terrains)
        {
            if(b.isIn(pos))
                return Optional.of(b);
        }
        return Optional.empty();
    }
    public boolean isAllowed(FactionMember member,Perms perm)
    {
        return true;
    }
}
