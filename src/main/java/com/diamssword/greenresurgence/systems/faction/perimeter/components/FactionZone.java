package com.diamssword.greenresurgence.systems.faction.perimeter.components;

import com.diamssword.greenresurgence.systems.faction.perimeter.TerrainInstance;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.List;

public class FactionZone {
    private FactionMember owner;
    private List<FactionZone> linkedZones;
    private BlockBox bounds;
    private BlockPos linkedGenerator;
    private boolean isMainZone =false;

    public boolean isIn(Vec3i pos)
    {
        return bounds.contains(pos);
    }

}
