package com.diamssword.greenresurgence.render.entities;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.entities.BikeEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BikeEntityRenderer extends GeoEntityRenderer<BikeEntity> {

	public BikeEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new BikeModele());
	}

	@Override
	public int getPackedOverlay(BikeEntity animatable, float u, float partialTick) {
		//	if (!(animatable instanceof LivingEntity entity))
		return OverlayTexture.DEFAULT_UV;
	}

	public static class BikeModele extends DefaultedEntityGeoModel<BikeEntity> {
		public BikeModele() {
			super(GreenResurgence.asRessource("bike"));
		}

		@Override
		public RenderLayer getRenderType(BikeEntity animatable, Identifier texture) {
			return RenderLayer.getEntityCutoutNoCull(getTextureResource(animatable));
		}

		@Override
		public void applyMolangQueries(BikeEntity animatable, double animTime) {
			super.applyMolangQueries(animatable, animTime);
			MolangParser parser = MolangParser.INSTANCE;
			parser.setMemoizedValue("query.wobble", () -> animatable.hurtTime < 5 ? animatable.hurtTime : -animatable.hurtTime + 5);
			parser.setMemoizedValue("query.yaw_change", animatable::bikeRodDir);
			//parser.setMemoizedValue("query.yaw", () -> animatable.prevYaw);
		}

	}
}
