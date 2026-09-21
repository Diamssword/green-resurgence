package com.diamssword.greenresurgence.render.entities;

import com.diamssword.characters.api.CharactersApi;
import com.diamssword.characters.api.http.ApiSkinValues;
import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.entities.NPCEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class NPCEntityRenderer extends LivingEntityRenderer<NPCEntity, PlayerEntityModel<NPCEntity>> {
	public final static Identifier DEFAULT_SKIN = GreenResurgence.asRessource("textures/entity/npc.png");
	private final PlayerEntityModel<NPCEntity> slimModel;
	private final PlayerEntityModel<NPCEntity> normalModel;

	public NPCEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new PlayerEntityModel<>(ctx.getPart(new EntityModelLayer(new Identifier("character_sheet:player"), "main")), false), 0.4f);
		slimModel = new PlayerEntityModel<>(ctx.getPart(new EntityModelLayer(new Identifier("character_sheet:player_slim"), "main")), true);
		normalModel = model;
		initClothLayers();
	}

	public void initClothLayers() {
		this.features.clear();
		CharactersApi.skin().getClothLayersForEntity(this).forEach(this::addFeature);
	}

	@Override
	protected void scale(NPCEntity entity, MatrixStack matrices, float amount) {
		var sc = ApiSkinValues.HeightMToMCScale(1, entity.getSkinDatas().size);
		matrices.scale(sc, sc, sc);
	}

	@Override
	public void render(NPCEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		if(livingEntity.getSkinDatas().slim)
			this.model = slimModel;
		else
			this.model = normalModel;
		super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	@Override
	protected boolean hasLabel(NPCEntity livingEntity) {
		return false;
	}

	@Override
	public Identifier getTexture(NPCEntity entity) {
		var t = entity.getCachedSkin();
		if(t == null) {
			if(entity.getSkinDatas().layers.length > 0) {
				CharactersApi.skin().getEntityTexture(entity, entity::setCachedSkin);
			}
			return DEFAULT_SKIN;
		}
		return t;
	}
}
