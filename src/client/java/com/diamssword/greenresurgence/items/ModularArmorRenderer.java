package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.systems.equipement.EquipmentSkins;
import com.diamssword.greenresurgence.systems.equipement.IEquipementItem;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.GeckoLibException;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.HashMap;
import java.util.Map;

public final class ModularArmorRenderer extends GeoArmorRenderer<ModularArmorItem> {
	private static final ModularArmorGeoModel<ModularArmorItem> FALLBACKMODEL = new ModularArmorGeoModel<>(GreenResurgence.asRessource("default"), true);
	private static final ModularArmorRenderer FALLBACKRENDERER = new ModularArmorRenderer(GreenResurgence.asRessource("default"), GreenResurgence.asRessource("default"));

	public ModularArmorRenderer(Identifier model, Identifier texture) {
		super((ModularArmorGeoModel) new ModularArmorGeoModel<>(model).withAltTexture(texture));
	}


	public static RenderProvider RendererProvider() {
		return new RenderProvider() {
			private final Map<PlayerEntity, ModularArmorRenderer> cachedRenders = new HashMap<>();
			private final Map<PlayerEntity, String> cachedIds = new HashMap<>();
			private GeckoToolEquipmentRenderer<?> itemRendered;

			@Override
			public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
				ModularArmorRenderer rend;
				if(itemStack.getItem() instanceof IEquipementItem tool) {
					var skin = tool.getEquipment(itemStack).getSkin();
					var model = EquipmentSkins.get(skin, itemStack.getItem());
					rend = model.map(m -> new ModularArmorRenderer(m.model, m.texture == null ? m.model : m.texture)).orElse(FALLBACKRENDERER);
				} else {
					rend = FALLBACKRENDERER;
				}
				rend.prepForRender(livingEntity, itemStack, equipmentSlot, original);
				return rend;
			}

			@Override
			public BuiltinModelItemRenderer getCustomRenderer() {
				if(this.itemRendered == null)
					this.itemRendered = new GeckoToolEquipmentRenderer<>(false);// new ExampleItemRenderer("makeshift_light");
				return itemRendered;
			}
		};
	}


	@Override
	public void actuallyRender(MatrixStack poseStack, ModularArmorItem animatable, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if(leftArm != null && rightArm != null && this.currentEntity instanceof ClientPlayerEntity pl) {
			if(pl.getModel().equals("slim")) {
				this.leftArm.setScaleX(0.8f);
				this.rightArm.setScaleX(0.8f);
				this.leftArm.setPosX(this.leftArm.getPosX() - 0.1f);
			} else
				this.leftArm.setPosX(this.leftArm.getPosX() - 0.25f);
			this.rightArm.setPosX(this.rightArm.getPosX() + 0.1f);
		}

		super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	/*
		protected void applyBaseTransformations(BipedEntityModel<?> baseModel) {
			if(this.head != null) {
				ModelPart headPart = baseModel.head;

				RenderUtils.matchModelPartRot(headPart, this.head);
				this.head.updatePosition(headPart.pivotX, -headPart.pivotY, headPart.pivotZ);
			}

			if(this.body != null) {
				ModelPart bodyPart = baseModel.body;

				RenderUtils.matchModelPartRot(bodyPart, this.body);
				this.body.updatePosition(bodyPart.pivotX, -bodyPart.pivotY, bodyPart.pivotZ);
			}

			if(this.rightArm != null) {
				ModelPart rightArmPart = baseModel.rightArm;

				RenderUtils.matchModelPartRot(rightArmPart, this.rightArm);
				this.rightArm.updatePosition(rightArmPart.pivotX + 5, 2 - rightArmPart.pivotY, rightArmPart.pivotZ);
			}

			if(this.leftArm != null) {
				ModelPart leftArmPart = baseModel.leftArm;

				RenderUtils.matchModelPartRot(leftArmPart, this.leftArm);
				this.leftArm.updatePosition(leftArmPart.pivotX - 5f, 2f - leftArmPart.pivotY, leftArmPart.pivotZ);
			}

			if(this.rightLeg != null) {
				ModelPart rightLegPart = baseModel.rightLeg;

				RenderUtils.matchModelPartRot(rightLegPart, this.rightLeg);
				this.rightLeg.updatePosition(rightLegPart.pivotX + 2, 12 - rightLegPart.pivotY, rightLegPart.pivotZ);

				if(this.rightBoot != null) {
					RenderUtils.matchModelPartRot(rightLegPart, this.rightBoot);
					this.rightBoot.updatePosition(rightLegPart.pivotX + 2, 12 - rightLegPart.pivotY, rightLegPart.pivotZ);
				}
			}

			if(this.leftLeg != null) {
				ModelPart leftLegPart = baseModel.leftLeg;

				RenderUtils.matchModelPartRot(leftLegPart, this.leftLeg);
				this.leftLeg.updatePosition(leftLegPart.pivotX - 2, 12 - leftLegPart.pivotY, leftLegPart.pivotZ);

				if(this.leftBoot != null) {
					RenderUtils.matchModelPartRot(leftLegPart, this.leftBoot);
					this.leftBoot.updatePosition(leftLegPart.pivotX - 2, 12 - leftLegPart.pivotY, leftLegPart.pivotZ);
				}
			}
		}
		*/
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
					return FALLBACKMODEL.getAnimation((ModularArmorItem) animatable, name);
			}
		}

		@Override
		protected String subtype() {
			return "equipments/skins/armors";
		}
	}
}