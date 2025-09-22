package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.equipment.EquipmentTool;
import com.diamssword.greenresurgence.items.equipment.upgrades.EquipmentSkinItem;
import com.diamssword.greenresurgence.systems.equipement.EquipmentSkins;
import com.diamssword.greenresurgence.systems.equipement.IEquipementItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.GeckoLibException;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import java.util.Optional;

public final class GeckoToolEquipmentRenderer extends GeoItemRenderer<EquipmentTool> {
	private static final ItemGeoModel<GeoAnimatable> FALLBACKMODEL = new ItemGeoModel<>(GreenResurgence.asRessource("default"), true);
	public static Identifier BP_BG = GreenResurgence.asRessource("equipments/blueprint_background");

	public GeckoToolEquipmentRenderer(boolean emissive) {
		super(new ItemGeoModel<>(GreenResurgence.asRessource("default")));
		if(emissive)
			addRenderLayer(new AutoGlowingGeoLayer<>(this));
	}

	public static RenderProvider RendererProvider(boolean emissive) {
		return new RenderProvider() {
			private GeckoToolEquipmentRenderer renderer;

			@Override
			public BuiltinModelItemRenderer getCustomRenderer() {
				if(this.renderer == null)
					this.renderer = new GeckoToolEquipmentRenderer(emissive);
				return this.renderer;
			}
		};
	}

	private void renderVanillaModel(MatrixStack poseStack, Identifier model, ItemStack stack, ModelTransformationMode transformType, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay, float scale) {

		poseStack.push();
		poseStack.translate(0.5f, 0.51f, 0.5f);
		poseStack.scale(scale, scale, 1);

		MinecraftClient client = MinecraftClient.getInstance();
		ItemRenderer itemRenderer = client.getItemRenderer();
		BakedModelManager modelManager = client.getBakedModelManager();
		BakedModel bk = modelManager.getModel(new ModelIdentifier(model, "inventory"));
		var l = false;
		if(MinecraftClient.getInstance().player != null)
			l = MinecraftClient.getInstance().player.getMainArm() == Arm.LEFT;

		itemRenderer.renderItem(
				stack,
				transformType,
				l, // left-handed
				poseStack,
				bufferSource,
				packedLight,
				packedOverlay,
				bk
		);

		poseStack.pop();

	}

	@Override
	public void render(ItemStack stack, ModelTransformationMode transformType, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
		Optional<EquipmentSkins.ItemSkinModelDef> model;
		if(stack.getItem() instanceof IEquipementItem tool) {
			var skin = tool.getEquipment(stack).getSkin();
			model = EquipmentSkins.get(skin, stack.getItem());
			model.ifPresentOrElse(itemSkinModelDef -> renderSkin(poseStack, itemSkinModelDef, stack, transformType, bufferSource, packedLight, packedOverlay, 1),
					() -> renderVanillaModel(poseStack, new Identifier("minecraft:barrier"), stack, transformType, bufferSource, packedLight, packedOverlay, 1));
		} else if(stack.getItem() instanceof EquipmentSkinItem sk) {
			poseStack.push();
			if(transformType == ModelTransformationMode.GROUND) {
				poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
				poseStack.translate(-0, -1, 0);
			}

			renderVanillaModel(poseStack, BP_BG, stack, transformType, bufferSource, packedLight, packedOverlay, 1f);
			if(!transformType.isFirstPerson()) {
				var skname = sk.getSkin(stack);
				if(!skname.isEmpty()) {
					model = EquipmentSkins.get(skname, MinecraftClient.getInstance().world.getTime());
					if(model.isPresent()) {
						poseStack.push();
						poseStack.translate(0f, 0f, 0.1f);
						if(transformType != ModelTransformationMode.GUI) {
							poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
							poseStack.scale(0.5f, 0.5f, 0.5f);
							poseStack.translate(0.5f, -2f, 0.9f);
						}
						renderSkin(poseStack, model.get(), stack, transformType == ModelTransformationMode.GUI ? ModelTransformationMode.GUI : ModelTransformationMode.FIXED, bufferSource, packedLight, packedOverlay, 0.7f);
						poseStack.pop();
					}
				}
			}
			//}
			poseStack.pop();
		}


	}

	private void renderSkin(MatrixStack poseStack, EquipmentSkins.ItemSkinModelDef model, ItemStack stack, ModelTransformationMode transformType, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay, float scale) {
		if(model.isGecko) {
			super.render(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
		} else {
			renderVanillaModel(poseStack, model.model, stack, transformType, bufferSource, packedLight, packedOverlay, scale);
		}
	}

	public static class ItemGeoModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {

		public ItemGeoModel(Identifier assetSubpath) {this(assetSubpath, false);}

		private final boolean defaut;

		protected ItemGeoModel(Identifier assetSubpath, boolean defaut) {
			super(assetSubpath);
			this.defaut = defaut;
		}

		@Override
		public BakedGeoModel getBakedModel(Identifier location) {
			try {
				return super.getBakedModel(location);
			} catch(GeckoLibException ex) {
				if(defaut)
					throw ex;
				else
					return FALLBACKMODEL.getBakedModel(FALLBACKMODEL.getModelResource(null));
			}
		}

		@Override
		public Animation getAnimation(T animatable, String name) {
			try {
				return super.getAnimation(animatable, name);
			} catch(GeckoLibException ex) {
				if(defaut)
					throw ex;
				else
					return FALLBACKMODEL.getAnimation(animatable, name);
			}
		}

		@Override
		protected String subtype() {
			return "equipments/skins";
		}
	}
}