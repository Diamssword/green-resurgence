package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.weapons.GeckoActivated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import software.bernie.example.entity.CoolKidEntity;
import software.bernie.geckolib.GeckoLibException;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.keyframe.event.ParticleKeyframeEvent;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class GeckoItemRenderer extends GeoItemRenderer<GeckoActivated> {
    private static final ItemGeoModel<GeoAnimatable> FALLBACKMODEL=new ItemGeoModel<>(GreenResurgence.asRessource("default"),true);
    public GeckoItemRenderer(String model,boolean emissive) {
        super(new ItemGeoModel<>(GreenResurgence.asRessource(model)));
        if(emissive)
            addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
    public static RenderProvider RendererProvider(String itemname,boolean emissive) {
        return new RenderProvider() {
            private GeckoItemRenderer renderer;

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new GeckoItemRenderer(itemname,emissive);
                return this.renderer;
            }
        };
    }
    public static class ItemGeoModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {

        public ItemGeoModel(Identifier assetSubpath) {this(assetSubpath,false);}
        private final boolean defaut;
        protected ItemGeoModel(Identifier assetSubpath,boolean defaut) {
            super(assetSubpath);
            this.defaut=defaut;
        }
        @Override
        public void addAdditionalStateData(T animatable, long instanceId, BiConsumer<DataTicket<T>, T> dataConsumer) {
            //System.out.println("yolo");
        }
        @Override
        public BakedGeoModel getBakedModel(Identifier location) {
            try {
                return super.getBakedModel(location);
            }catch (GeckoLibException ex){
                if(defaut)
                    throw ex;
                else
                    return FALLBACKMODEL.getBakedModel(FALLBACKMODEL.getModelResource(null));
            }
        }
        @Override
        public Animation getAnimation(T animatable, String name) {
            try {
                return super.getAnimation(animatable,name);
            }catch (GeckoLibException ex){
                if(defaut)
                    throw ex;
                else
                    return FALLBACKMODEL.getAnimation(animatable,name);
            }
        }
        @Override
        protected String subtype() {
            return "item";
        }
    }
}