package com.diamssword.greenresurgence.systems;

import com.diamssword.greenresurgence.systems.faction.perimeter.FactionList;
import com.diamssword.greenresurgence.systems.faction.perimeter.IFactionList;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.util.Identifier;

public class Components implements EntityComponentInitializer, WorldComponentInitializer {
    public static final ComponentKey<IFactionList> BASE_LIST = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("green_resurgence:base_list"), IFactionList.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {

    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(BASE_LIST, FactionList::new);
    }
}
