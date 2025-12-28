package com.diamssword.greenresurgence.render.entities;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.entities.BikeEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BikeEntityRenderer extends GeoEntityRenderer<BikeEntity> {

	public BikeEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new BikeModele());
	}

	@Override
	public int getPackedOverlay(BikeEntity animatable, float u, float partialTick) {
		return OverlayTexture.DEFAULT_UV;
	}

	public static class BikeModele extends DefaultedEntityGeoModel<BikeEntity> {

		private final Identifier[] texturesPath = new Identifier[16];

		public BikeModele() {
			super(GreenResurgence.asRessource("bike"));
			for(var col : DyeColor.values()) {
				texturesPath[col.getId()] = buildFormattedTexturePath(GreenResurgence.asRessource("bike_" + col.getName()));
			}
		}

		@Override
		public Identifier getTextureResource(BikeEntity animatable) {
			return this.getTexture(animatable);
		}

		@Override
		public Identifier getTexture(BikeEntity animatable) {
			if(animatable != null) {
				return texturesPath[Math.max(0, Math.min(animatable.getColor(), 15))];
			}
			return super.getTexture(null);
		}

		@Override
		public void setCustomAnimations(BikeEntity animatable, long instanceId, AnimationState<BikeEntity> animationState) {
			super.setCustomAnimations(animatable, instanceId, animationState);
			CoreGeoBone sac = getAnimationProcessor().getBone("Backpack");

			if(sac != null) {
				sac.setHidden(!animatable.hasChest());
			}
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
