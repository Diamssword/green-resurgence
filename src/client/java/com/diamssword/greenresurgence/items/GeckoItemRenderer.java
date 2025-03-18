package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.weapons.GeckoActivated;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.GeckoLibException;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

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