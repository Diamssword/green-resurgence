package com.diamssword.greenresurgence.systems.character.stats;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class StatsRole {
    public final String name;
    public StatsRole(String name)
    {
        this.name=name;
    }
    protected Map<Integer,StatsRolePalier> map=new TreeMap<>();
    protected Map<EntityAttribute, Function<Integer,EntityAttributeModifier>> globalModifiers=new HashMap<>();
    public abstract void init();


    public int create(int level, Consumer<StatsRolePalier> init)
    {
        var c=new StatsRolePalier(this,level);
        map.put(level,c);
        init.accept(c);
        return level;
    }
    public static EntityAttributeModifier modifier(EntityAttribute attribut,UUID id, float value, EntityAttributeModifier.Operation operation)
    {
        return  new EntityAttributeModifier(id, GreenResurgence.ID+".role_modifier."+attribut.getTranslationKey(),value,operation);
    }
    public void addGlobalModifier(EntityAttribute attr,Function<Integer,EntityAttributeModifier> modifier)
    {
        this.globalModifiers.put(attr,modifier);
    }
    public void onLevelChange(PlayerEntity pl, int level)
    {
       changeModifiers(pl,level);
    }
    private void changeModifiers(PlayerEntity pl,int level)
    {
        for (var set:map.entrySet()) {
            set.getValue().clearModifier(pl);
        }
        for (var set:globalModifiers.entrySet()) {
            var max = pl.getAttributeInstance(set.getKey());
            var r=set.getValue().apply(level);
            max.tryRemoveModifier(r.getId());
            max.addPersistentModifier(r);
        }
        for (var set:map.entrySet()) {
            if(set.getKey()>level)
                break;
            set.getValue().changeModifiers(pl);
        }
    }

}
