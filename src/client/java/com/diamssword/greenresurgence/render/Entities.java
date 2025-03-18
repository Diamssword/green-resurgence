package com.diamssword.greenresurgence.render;

import com.diamssword.greenresurgence.MEntities;
import com.diamssword.greenresurgence.render.entities.BackpackEntityRenderer;
import com.diamssword.greenresurgence.render.entities.CaddieEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class Entities {
    public static void init()
    {
        EntityRendererRegistry.register(MEntities.CHAIR, Entities::emptyRender);
        EntityRendererRegistry.register( MEntities.BACKPACK, BackpackEntityRenderer::new);
        EntityRendererRegistry.register( MEntities.CADDIE, CaddieEntityRenderer::new);
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
