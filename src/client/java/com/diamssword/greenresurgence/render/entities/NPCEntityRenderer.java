package com.diamssword.greenresurgence.render.entities;

import com.diamssword.characters.api.CharactersApi;
import com.diamssword.greenresurgence.entities.NPCEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCEntityRenderer extends LivingEntityRenderer<NPCEntity, PlayerEntityModel<NPCEntity>> {

	private Map<UUID, Identifier> textures = new HashMap<>();

	public NPCEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new PlayerEntityModel<>(ctx.getPart(Math.random() > 0.5f ? new EntityModelLayer(new Identifier("character_sheet:player"), "main") : new EntityModelLayer(new Identifier("character_sheet:player_slim"), "main")), false), 1f);
		initClothLayers();

	}

	public void initClothLayers() {
		this.features.clear();
		CharactersApi.skin().getClothLayersForEntity(this).forEach(this::addFeature);
	}

	@Override
	public Identifier getTexture(NPCEntity entity) {
		var t = textures.get(entity.getUuid());
		if(t == null) {
			if(entity.getSkinDatas().layers.length > 0) {
				CharactersApi.skin().getEntityTexture(entity, cl -> {
					textures.put(entity.getUuid(), cl);
				});
			}
			return new Identifier("empty");
		}
		return t;
	}
}
