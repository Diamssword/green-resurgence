package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.equipment.upgrades.EquipmentSkinItem;
import com.diamssword.greenresurgence.systems.equipement.EquipmentSkins;
import com.diamssword.greenresurgence.systems.equipement.IEquipementItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibException;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import java.util.Optional;

public final class GeckoToolEquipmentRenderer<T extends Item & GeoAnimatable> extends GeoItemRenderer<T> {
	private static final ItemGeoModel<GeoAnimatable> FALLBACKMODEL = new ItemGeoModel<>(GreenResurgence.asRessource("default"), true);
	public static Identifier BP_BG = GreenResurgence.asRessource("equipments/blueprint_background");

	public GeckoToolEquipmentRenderer(boolean emissive) {
		super(new ItemGeoModel<>(GreenResurgence.asRessource("default")));
		if(emissive)
			addRenderLayer(new AutoGlowingGeoLayer<>(this));
	}

	public static RenderProvider RendererProvider(boolean emissive) {
		return new RenderProvider() {
			private GeckoToolEquipmentRenderer<?> renderer;

			@Override
			public BuiltinModelItemRenderer getCustomRenderer() {
				if(this.renderer == null)
					this.renderer = new GeckoToolEquipmentRenderer<>(emissive);
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

			model.ifPresent(m -> isArmor = m.isArmor);
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
					var pair = EquipmentSkins.getPair(skname, MinecraftClient.getInstance().world.getTime());
					pair.ifPresent(p -> bpArmorItem = p.getLeft());
					model = pair.map(Pair::getRight);
					model.ifPresent(m -> isArmor = m.isArmor);
					if(model.isPresent() && !model.get().isGecko) {
						poseStack.push();
						poseStack.translate(0f, 0f, 0.1f);
						if(transformType != ModelTransformationMode.GUI) {
							poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
							poseStack.scale(0.5f, 0.5f, 0.5f);
							poseStack.translate(0.5f, -2f, 0.9f);
						}
						renderSkin(poseStack, model.get(), stack, transformType == ModelTransformationMode.GUI ? ModelTransformationMode.GUI : ModelTransformationMode.FIXED, bufferSource, packedLight, packedOverlay, 0.7f);
						poseStack.pop();
					} else {
						isBP = true;
						model.ifPresent(itemSkinModelDef -> renderSkin(poseStack, itemSkinModelDef, stack, transformType, bufferSource, packedLight, packedOverlay, 0.7f));
					}

				}
			}
			poseStack.pop();
		}


	}

	private DefaultedGeoModel<T> model;
	private BakedModel vanillaBaked;
	private Item bpArmorItem;
	private boolean isBP = false;
	private boolean isArmor = false;

	@Override
	public GeoModel<T> getGeoModel() {

		if(model != null)
			return model;
		return super.getGeoModel();
	}

	@Override
	public void doPostRenderCleanup() {
		super.doPostRenderCleanup();
		vanillaBaked = null;
		setVisible(true);
		isBP = false;
		bpArmorItem = null;
		isArmor = false;
	}

	private void renderSkin(MatrixStack poseStack, EquipmentSkins.ItemSkinModelDef model, ItemStack stack, ModelTransformationMode transformType, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay, float scale) {
		if(model.isGecko) {

			if(model.isArmor)
				this.model = new ModularArmorRenderer.ModularArmorGeoModel<>(model.model);
			else
				this.model = new ItemGeoModel<>(model.model);
			if(model.texture != null)
				this.model.withAltTexture(model.texture);
			MinecraftClient client = MinecraftClient.getInstance();
			BakedModelManager modelManager = client.getBakedModelManager();
			vanillaBaked = modelManager.getModel(new ModelIdentifier(model.getVanillaPath(), "inventory"));

			super.render(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);

		} else {
			renderVanillaModel(poseStack, model.model, stack, transformType, bufferSource, packedLight, packedOverlay, scale);
		}
	}

	@Override
	public void preRender(MatrixStack poseStack, T animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

		if(vanillaBaked != null) {
			var l = false;
			if(MinecraftClient.getInstance().player != null)
				l = MinecraftClient.getInstance().player.getMainArm() == Arm.LEFT;
			vanillaBaked.getTransformation().getTransformation(this.renderPerspective).apply(l, poseStack);
		}
		if(isArmor) {
			Equipment mod = null;
			if(isBP && bpArmorItem instanceof Equipment m) {
				mod = m;
			} else if(animatable instanceof Equipment m) {
				mod = m;
			}
			poseStack.translate(0, 0, 1);
			if(this.renderPerspective == ModelTransformationMode.HEAD)
				applyBoneVisibilityBySlot(EquipmentSlot.OFFHAND);
			else if(mod != null) {
				poseStack.translate(0, 0, -1);
				if(mod.getSlotType() == EquipmentSlot.CHEST) {
					poseStack.scale(0.7f, 0.7f, 0.7f);
					poseStack.translate(0, -1, 0);
				} else if(mod.getSlotType() == EquipmentSlot.HEAD) {
					poseStack.scale(0.8f, 0.8f, 0.8f);
					poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(140));
					poseStack.translate(0, -1.85, 0);
				} else if(mod.getSlotType() == EquipmentSlot.FEET)
					poseStack.translate(0, -0.3, 0);
				else if(mod.getSlotType() == EquipmentSlot.LEGS) {
					poseStack.scale(0.7f, 0.7f, 0.7f);
					poseStack.translate(0, -0.15, 0);
				}

				applyBoneVisibilityBySlot(mod.getSlotType());
			}

		}
		if(isBP) {
			if(this.renderPerspective == ModelTransformationMode.GUI)
				poseStack.translate(0, 0, 0.1f);
			else {
				poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
				poseStack.scale(0.5f, 0.5f, 0.5f);
			}
		}
	}

	@Nullable
	public GeoBone getHeadBone() {
		return this.getGeoModel().getBone("armorHead").orElse(null);
	}

	@Nullable
	public GeoBone getBodyBone() {
		return this.getGeoModel().getBone("armorBody").orElse(null);
	}

	@Nullable
	public GeoBone getRightArmBone() {
		return this.getGeoModel().getBone("armorRightArm").orElse(null);
	}

	@Nullable
	public GeoBone getLeftArmBone() {
		return this.getGeoModel().getBone("armorLeftArm").orElse(null);
	}

	@Nullable
	public GeoBone getRightLegBone() {
		return this.getGeoModel().getBone("armorRightLeg").orElse(null);
	}

	@Nullable
	public GeoBone getLeftLegBone() {
		return this.getGeoModel().getBone("armorLeftLeg").orElse(null);
	}

	@Nullable
	public GeoBone getRightBootBone() {
		return this.getGeoModel().getBone("armorRightBoot").orElse(null);
	}

	@Nullable
	public GeoBone getLeftBootBone() {
		return this.getGeoModel().getBone("armorLeftBoot").orElse(null);
	}

	public void setVisible(boolean pVisible) {
		setBoneVisible(this.getHeadBone(), pVisible);
		setBoneVisible(this.getBodyBone(), pVisible);
		setBoneVisible(this.getRightArmBone(), pVisible);
		setBoneVisible(this.getLeftArmBone(), pVisible);
		setBoneVisible(this.getRightLegBone(), pVisible);
		setBoneVisible(this.getLeftLegBone(), pVisible);
		setBoneVisible(this.getRightBootBone(), pVisible);
		setBoneVisible(this.getLeftBootBone(), pVisible);
	}

	private void setBoneVisible(@Nullable GeoBone bone, boolean visible) {
		if(bone == null)
			return;
		bone.setHidden(!visible);
	}

	private void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
		setVisible(false);
		switch(currentSlot) {
			case HEAD -> setBoneVisible(this.getHeadBone(), true);
			case CHEST -> {
				setBoneVisible(this.getBodyBone(), true);
				setBoneVisible(this.getRightArmBone(), true);
				setBoneVisible(this.getLeftArmBone(), true);
			}
			case LEGS -> {
				setBoneVisible(this.getRightLegBone(), true);
				setBoneVisible(this.getLeftLegBone(), true);
			}
			case FEET -> {
				setBoneVisible(this.getRightBootBone(), true);
				setBoneVisible(this.getLeftBootBone(), true);
			}
			default -> {
			}
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