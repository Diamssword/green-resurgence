package com.diamssword.greenresurgence.systems.faction.perimeter;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Optional;

public interface IFactionList extends ServerTickingComponent, ComponentV3 {
    public List<FactionInstance> getAll();
    public List<String> getNames();
    public Optional<FactionInstance> getAt(Vec3i pos);
    public Optional<TerrainInstance> getTerrainAt(Vec3i pos);
    public boolean canEditAt(PlayerEntity player, Vec3i pos);
    public List<FactionInstance> getNear(Vec3i pos, int distance);
    public Optional<FactionInstance> getClosest(Vec3i pos, int distance);
    public boolean add(FactionInstance base);
    public boolean delete(String name);
    public Optional<FactionInstance> get(String name);
    public List<Triple<String, String,BlockBox>> getBoxesForClient();
}
