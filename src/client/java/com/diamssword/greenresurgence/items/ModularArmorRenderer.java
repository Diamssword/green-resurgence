package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.GreenResurgence;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
import software.bernie.geckolib.util.RenderUtils;

import java.util.HashMap;
import java.util.Map;

public final class ModularArmorRenderer extends GeoArmorRenderer<ModularArmorItem> {
	private static final ModularArmorGeoModel<GeoAnimatable> FALLBACKMODEL = new ModularArmorGeoModel<>(GreenResurgence.asRessource("default"), true);

	public ModularArmorRenderer(String model) {
		super(new ModularArmorGeoModel<>(GreenResurgence.asRessource(model)));
	}

	public static RenderProvider RendererProvider() {
		return new RenderProvider() {
			private final Map<PlayerEntity, ModularArmorRenderer> cachedRenders = new HashMap<>();
			private final Map<PlayerEntity, String> cachedIds = new HashMap<>();
			private ExampleItemRenderer itemRendered;

			@Override
			public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
				var model = itemStack.getNbt().getString("model");
				if (model == null || model.isEmpty())
					model = "default";

				if (livingEntity instanceof PlayerEntity pl) {
					if (cachedRenders.size() > 500) {
						cachedRenders.clear();
						cachedIds.clear();
					}
					ModularArmorRenderer rend;
					if (!(cachedIds.containsKey(pl) && cachedIds.get(pl).equals(model))) {
						cachedIds.put(pl, model);
						cachedRenders.put(pl, rend = new ModularArmorRenderer(model));
					} else
						rend = cachedRenders.get(pl);
					rend.prepForRender(livingEntity, itemStack, equipmentSlot, original);
					return rend;
				}
				var rend = new ModularArmorRenderer(model);
				rend.prepForRender(livingEntity, itemStack, equipmentSlot, original);
				return rend;
			}

			@Override
			public BuiltinModelItemRenderer getCustomRenderer() {
				if (this.itemRendered == null)
					this.itemRendered = new ExampleItemRenderer("makeshift_light");
				return itemRendered;
			}
		};
	}

	@Override
	public void actuallyRender(MatrixStack poseStack, ModularArmorItem animatable, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (leftArm != null && rightArm != null && this.currentEntity instanceof ClientPlayerEntity pl) {
			if (pl.getModel().equals("slim")) {
				this.leftArm.setScaleX(0.8f);
				this.rightArm.setScaleX(0.8f);
				this.leftArm.setPosX(this.leftArm.getPosX() - 0.1f);
			} else
				this.leftArm.setPosX(this.leftArm.getPosX() - 0.25f);
			this.rightArm.setPosX(this.rightArm.getPosX() + 0.1f);
		}

		super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	protected void applyBaseTransformations(BipedEntityModel<?> baseModel) {
		if (this.head != null) {
			ModelPart headPart = baseModel.head;

			RenderUtils.matchModelPartRot(headPart, this.head);
			this.head.updatePosition(headPart.pivotX, -headPart.pivotY, headPart.pivotZ);
		}

		if (this.body != null) {
			ModelPart bodyPart = baseModel.body;

			RenderUtils.matchModelPartRot(bodyPart, this.body);
			this.body.updatePosition(bodyPart.pivotX, -bodyPart.pivotY, bodyPart.pivotZ);
		}

		if (this.rightArm != null) {
			ModelPart rightArmPart = baseModel.rightArm;

			RenderUtils.matchModelPartRot(rightArmPart, this.rightArm);
			this.rightArm.updatePosition(rightArmPart.pivotX + 5, 2 - rightArmPart.pivotY, rightArmPart.pivotZ);
		}

		if (this.leftArm != null) {
			ModelPart leftArmPart = baseModel.leftArm;

			RenderUtils.matchModelPartRot(leftArmPart, this.leftArm);
			this.leftArm.updatePosition(leftArmPart.pivotX - 5f, 2f - leftArmPart.pivotY, leftArmPart.pivotZ);
		}

		if (this.rightLeg != null) {
			ModelPart rightLegPart = baseModel.rightLeg;

			RenderUtils.matchModelPartRot(rightLegPart, this.rightLeg);
			this.rightLeg.updatePosition(rightLegPart.pivotX + 2, 12 - rightLegPart.pivotY, rightLegPart.pivotZ);

			if (this.rightBoot != null) {
				RenderUtils.matchModelPartRot(rightLegPart, this.rightBoot);
				this.rightBoot.updatePosition(rightLegPart.pivotX + 2, 12 - rightLegPart.pivotY, rightLegPart.pivotZ);
			}
		}

		if (this.leftLeg != null) {
			ModelPart leftLegPart = baseModel.leftLeg;

			RenderUtils.matchModelPartRot(leftLegPart, this.leftLeg);
			this.leftLeg.updatePosition(leftLegPart.pivotX - 2, 12 - leftLegPart.pivotY, leftLegPart.pivotZ);

			if (this.leftBoot != null) {
				RenderUtils.matchModelPartRot(leftLegPart, this.leftBoot);
				this.leftBoot.updatePosition(leftLegPart.pivotX - 2, 12 - leftLegPart.pivotY, leftLegPart.pivotZ);
			}
		}
	}

	public static class ExampleItemRenderer extends GeoItemRenderer<ModularArmorItem> {
		private final Map<String, GeoModel<ModularArmorItem>> cachedRenders = new HashMap<>();

		public ExampleItemRenderer(String model) {
			super(new ModularArmorGeoModel<>(GreenResurgence.asRessource(model)));

		}

		@Override
		public GeoModel<ModularArmorItem> getGeoModel() {
			if (this.currentItemStack != null) {
				var model = this.currentItemStack.getNbt().getString("model");
				if (model == null || model.isEmpty())
					model = "default";
				if (!cachedRenders.containsKey(model))
					cachedRenders.put(model, new ModularArmorGeoModel<>(GreenResurgence.asRessource(model)));
				return cachedRenders.get(model);
			}
			return this.model;
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

		@Override
		public void preRender(MatrixStack poseStack, ModularArmorItem animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
			super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

			if (this.renderPerspective == ModelTransformationMode.HEAD)
				applyBoneVisibilityBySlot(EquipmentSlot.OFFHAND);
			else
				applyBoneVisibilityBySlot(animatable.getSlotType());
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

		protected void setBoneVisible(@Nullable GeoBone bone, boolean visible) {
			if (bone == null)
				return;
			bone.setHidden(!visible);
		}

		protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
			setVisible(false);
			switch (currentSlot) {
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
	}

	public static class ModularArmorGeoModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {
		public ModularArmorGeoModel(Identifier assetSubpath) {
			this(assetSubpath, false);
		}

		private final boolean defaut;

		protected ModularArmorGeoModel(Identifier assetSubpath, boolean defaut) {
			super(assetSubpath);
			this.defaut = defaut;
		}

		@Override
		public BakedGeoModel getBakedModel(Identifier location) {
			try {
				return super.getBakedModel(location);
			} catch (GeckoLibException ex) {
				if (defaut)
					throw ex;
				else
					return FALLBACKMODEL.getBakedModel(FALLBACKMODEL.getModelResource(null));
			}
		}

		@Override
		public Animation getAnimation(T animatable, String name) {
			try {
				return super.getAnimation(animatable, name);
			} catch (GeckoLibException ex) {
				if (defaut)
					throw ex;
				else
					return FALLBACKMODEL.getAnimation(animatable, name);
			}
		}

		@Override
		protected String subtype() {
			return "modular/armor";
		}
	}
}