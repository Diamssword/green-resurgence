package com.diamssword.greenresurgence.render.entities;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.entities.BackpackEntity;
import com.diamssword.greenresurgence.entities.TwoPassengerVehicle;
import com.diamssword.greenresurgence.items.BackPackItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import software.bernie.example.client.renderer.entity.CoolKidRenderer;
import software.bernie.example.entity.BikeEntity;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CaddieEntityRenderer extends GeoEntityRenderer<TwoPassengerVehicle> {

    public CaddieEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new CaddieModele());
    }
    public static class CaddieModele extends DefaultedEntityGeoModel<TwoPassengerVehicle> {
        public CaddieModele() {
            super(GreenResurgence.asRessource("shopping_cart"));
        }

        @Override
        public RenderLayer getRenderType(TwoPassengerVehicle animatable, Identifier texture) {
            return RenderLayer.getEntityCutoutNoCull(getTextureResource(animatable));
        }

        @Override
        public void applyMolangQueries(TwoPassengerVehicle animatable, double animTime) {
            super.applyMolangQueries(animatable, animTime);
            MolangParser parser = MolangParser.INSTANCE;
            parser.setMemoizedValue("query.wobble",()->animatable.getDamageWobbleSide()*animatable.getDamageWobbleTicks());
            parser.setMemoizedValue("query.yaw", ()->animatable.prevYaw);
        }
    }
}
