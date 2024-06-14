package com.diamssword.greenresurgence.render;

import com.diamssword.greenresurgence.MEntities;
import com.diamssword.greenresurgence.entities.ChairEntity;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class Entities {
    public static void init()
    {
        EntityRendererRegistry.INSTANCE.register(MEntities.chair, Entities::emptyRender);
    }
    public static EntityRenderer emptyRender(EntityRendererFactory.Context ctx){
       return new EntityRenderer<Entity>(ctx) {
            @Override
            public Identifier getTexture(Entity entity) {
                return null;
            }
        };
    }
}
