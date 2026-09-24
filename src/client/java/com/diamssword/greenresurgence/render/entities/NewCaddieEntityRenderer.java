package com.diamssword.greenresurgence.render.entities;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.entities.NewCaddieEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NewCaddieEntityRenderer extends GeoEntityRenderer<NewCaddieEntity> {

	public NewCaddieEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new CaddieModele());
	}

	public static class CaddieModele extends DefaultedEntityGeoModel<NewCaddieEntity> {
		public CaddieModele() {
			super(GreenResurgence.asRessource("shopping_cart"));
		}

		@Override
		public RenderLayer getRenderType(NewCaddieEntity animatable, Identifier texture) {
			return RenderLayer.getEntityCutoutNoCull(getTextureResource(animatable));
		}

		@Override
		public void applyMolangQueries(NewCaddieEntity animatable, double animTime) {
			super.applyMolangQueries(animatable, animTime);
			MolangParser parser = MolangParser.INSTANCE;
			parser.setMemoizedValue("query.wobble", () -> animatable.hurtTime < 5 ? animatable.hurtTime : -animatable.hurtTime + 5);
			parser.setMemoizedValue("query.yaw", () -> 0);
		}
	}

}
