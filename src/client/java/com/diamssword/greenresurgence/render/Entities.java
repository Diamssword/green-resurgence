package com.diamssword.greenresurgence.render;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MEntities;
import com.diamssword.greenresurgence.render.cosmetics.CustomPlayerModel;
import com.diamssword.greenresurgence.render.entities.BackpackEntityRenderer;
import com.diamssword.greenresurgence.render.entities.CaddieEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class Entities {

	public static final EntityModelLayer PLAYER_MODEL = new EntityModelLayer(GreenResurgence.asRessource("player"), "main");
	public static final EntityModelLayer PLAYER_MODEL_S = new EntityModelLayer(GreenResurgence.asRessource("player_slim"), "main");

	public static void init() {
		EntityModelLayerRegistry.registerModelLayer(PLAYER_MODEL, () -> TexturedModelData.of(CustomPlayerModel.getTexturedModelData(Dilation.NONE, false), 64, 64));
		EntityModelLayerRegistry.registerModelLayer(PLAYER_MODEL_S, () -> TexturedModelData.of(CustomPlayerModel.getTexturedModelData(Dilation.NONE, true), 64, 64));

		EntityRendererRegistry.register(MEntities.CHAIR, Entities::emptyRender);
		EntityRendererRegistry.register(MEntities.BACKPACK, BackpackEntityRenderer::new);
		EntityRendererRegistry.register(MEntities.CADDIE, CaddieEntityRenderer::new);
	}

	public static EntityRenderer emptyRender(EntityRendererFactory.Context ctx) {
		return new EntityRenderer<Entity>(ctx) {
			@Override
			public Identifier getTexture(Entity entity) {
				return null;
			}
		};
	}
}
