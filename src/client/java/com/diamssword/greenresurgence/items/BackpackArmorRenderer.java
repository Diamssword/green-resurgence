package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.entities.BackpackEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibException;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.HashMap;
import java.util.Map;

public final class BackpackArmorRenderer extends GeoArmorRenderer<BackPackItem> {
    private static final BackpackGeoModel<GeoAnimatable> FALLBACKMODEL=new BackpackGeoModel<>(GreenResurgence.asRessource("default"),true);
    public BackpackArmorRenderer(String model) {
        super(new BackpackGeoModel<>(GreenResurgence.asRessource(model)));
    }
    public static RenderProvider RendererProvider() {
        return new RenderProvider() {
            private final Map<Item, BackpackArmorRenderer> cachedRenders = new HashMap<>();
            private BackpackItemRenderer itemRendered;
            @Override
            public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
                BackpackArmorRenderer rend;
                if (!(cachedRenders.containsKey(itemStack.getItem()))) {
                    cachedRenders.put(itemStack.getItem(), rend = new BackpackArmorRenderer( Registries.ITEM.getId(itemStack.getItem()).getPath()));
                } else
                    rend = cachedRenders.get(itemStack.getItem());
                rend.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return rend;
            }
            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                if (this.itemRendered == null)
                    this.itemRendered = new BackpackItemRenderer("default");
                return itemRendered;
            }
        };
    }
    public static class BackpackItemRenderer extends GeoItemRenderer<BackPackItem> {
        private final Map<Item, BackpackGeoModel<BackPackItem>> cachedRenders = new HashMap<>();
        public BackpackItemRenderer(String model) {
            super(new BackpackGeoModel<>(GreenResurgence.asRessource(model)));

        }
        @Override
        public GeoModel<BackPackItem> getGeoModel() {
            if(this.currentItemStack !=null)
            {
                BackpackGeoModel<BackPackItem> rend;
                if (!(cachedRenders.containsKey(this.currentItemStack.getItem()))) {
                    rend=new BackpackGeoModel<>(GreenResurgence.asRessource(Registries.ITEM.getId(currentItemStack.getItem()).getPath()));
                    cachedRenders.put(currentItemStack.getItem(), rend);

                } else
                    rend = cachedRenders.get(currentItemStack.getItem());
                return rend;
            }
                return this.model;
        }
    }
    public static class BackpackGeoModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {
        public BackpackGeoModel(Identifier assetSubpath) {this(assetSubpath,false);}
        private final boolean defaut;
        protected BackpackGeoModel(Identifier assetSubpath, boolean defaut) {
            super(assetSubpath);
            this.defaut=defaut;
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
            return "modular/backpack";
        }
    }
}