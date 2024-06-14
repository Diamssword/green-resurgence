package com.diamssword.greenresurgence.cosmetics;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class GeckoCosmeticRenderer<T extends GeoAnimatable> extends GeoObjectRenderer<T>{

    protected BipedEntityModel<?> baseModel;

    protected BakedGeoModel lastModel = null;
    protected GeoBone head = null;
    protected GeoBone body = null;
    protected GeoBone rightArm = null;
    protected GeoBone leftArm = null;
    protected GeoBone rightLeg = null;
    protected GeoBone leftLeg = null;
    protected GeoBone rightBoot = null;
    protected GeoBone leftBoot = null;

    protected Entity currentEntity = null;
    private boolean child;
    private boolean sneaking;
    private boolean riding;
    private BipedEntityModel.ArmPose rightArmPose;
    private BipedEntityModel.ArmPose leftArmPose;
    private ItemStack currentStack;

    public GeckoCosmeticRenderer(GeoModel<T> model) {
        super(model);
    }


    /**
     * Returns the entity currently being rendered with armour equipped
     */
    public Entity getCurrentEntity() {
        return this.currentEntity;
    }

    /**
     * Gets the id that represents the current animatable's instance for animation purposes.
     * This is mostly useful for things like items, which have a single registered instance for all objects
     */
    @Override
    public long getInstanceId(T animatable) {
        return  this.currentEntity.getId();
    }

    /**
     * Gets the {@link RenderLayer} to render the given animatable with.<br>
     * Uses the {@link RenderLayer#getArmorCutoutNoCull} {@code RenderType} by default.<br>
     * Override this to change the way a model will render (such as translucent models, etc)
     */
    @Override
    public RenderLayer getRenderType(T animatable, Identifier texture, @org.jetbrains.annotations.Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getArmorCutoutNoCull(texture);
    }

    /**
     * Returns the 'head' GeoBone from this model.<br>
     * Override if your geo model has different bone names for these bones
     * @return The bone for the head model piece, or null if not using it
     */
    @Nullable
    public GeoBone getHeadBone() {
        return this.model.getBone("armorHead").orElse(null);
    }

    /**
     * Returns the 'body' GeoBone from this model.<br>
     * Override if your geo model has different bone names for these bones
     * @return The bone for the body model piece, or null if not using it
     */
    @Nullable
    public GeoBone getBodyBone() {
        return this.model.getBone("armorBody").orElse(null);
    }

    /**
     * Returns the 'right arm' GeoBone from this model.<br>
     * Override if your geo model has different bone names for these bones
     * @return The bone for the right arm model piece, or null if not using it
     */
    @Nullable
    public GeoBone getRightArmBone() {
        return this.model.getBone("armorRightArm").orElse(null);
    }

    /**
     * Returns the 'left arm' GeoBone from this model.<br>
     * Override if your geo model has different bone names for these bones
     * @return The bone for the left arm model piece, or null if not using it
     */
    @Nullable
    public GeoBone getLeftArmBone() {
        return this.model.getBone("armorLeftArm").orElse(null);
    }

    /**
     * Returns the 'right leg' GeoBone from this model.<br>
     * Override if your geo model has different bone names for these bones
     * @return The bone for the right leg model piece, or null if not using it
     */
    @Nullable
    public GeoBone getRightLegBone() {
        return this.model.getBone("armorRightLeg").orElse(null);
    }

    /**
     * Returns the 'left leg' GeoBone from this model.<br>
     * Override if your geo model has different bone names for these bones
     * @return The bone for the left leg model piece, or null if not using it
     */
    @Nullable
    public GeoBone getLeftLegBone() {
        return this.model.getBone("armorLeftLeg").orElse(null);
    }

    /**
     * Returns the 'right boot' GeoBone from this model.<br>
     * Override if your geo model has different bone names for these bones
     * @return The bone for the right boot model piece, or null if not using it
     */
    @Nullable
    public GeoBone getRightBootBone() {
        return this.model.getBone("armorRightBoot").orElse(null);
    }

    /**
     * Returns the 'left boot' GeoBone from this model.<br>
     * Override if your geo model has different bone names for these bones
     * @return The bone for the left boot model piece, or null if not using it
     */
    @Nullable
    public GeoBone getLeftBootBone() {
        return this.model.getBone("armorLeftBoot").orElse(null);
    }

    /**
     * Called before rendering the model to buffer. Allows for render modifications and preparatory
     * work such as scaling and translating.<br>
     * {@link MatrixStack} translations made here are kept until the end of the render process
     */
    @Override
    public void preRender(MatrixStack poseStack, T animatable, BakedGeoModel model, @Nullable VertexConsumerProvider bufferSource,
                          @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                          int packedOverlay, float red, float green, float blue, float alpha) {
        this.objectRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());

        applyBaseModel(this.baseModel);
        grabRelevantBones(getGeoModel().getBakedModel(getGeoModel().getModelResource(this.animatable)));
        applyBaseTransformations(this.baseModel);
        scaleModelForBaby(poseStack, animatable, partialTick, isReRender);
        scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);

    }



    /**
     * The actual render method that subtype renderers should override to handle their specific rendering tasks.<br>
     * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
     */
    @Override
    public void actuallyRender(MatrixStack poseStack, T animatable, BakedGeoModel model, RenderLayer renderType,
                               VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.push();
        poseStack.translate(0, 24 / 16f, 0);
        poseStack.scale(-1, -1, 1);

        if (!isReRender) {
            AnimationState<T> animationState = new AnimationState<>(animatable, 0, 0, partialTick, false);
            long instanceId = getInstanceId(animatable);

            animationState.setData(DataTickets.TICK, animatable.getTick(this.currentEntity));
            animationState.setData(DataTickets.ITEMSTACK, this.currentStack);
            animationState.setData(DataTickets.ENTITY, this.currentEntity);
           // animationState.setData(DataTickets.EQUIPMENT_SLOT, this.currentSlot);
            this.model.addAdditionalStateData(animatable, instanceId, animationState::setData);

            this.model.handleAnimations(animatable, instanceId, animationState);
        }

        this.modelRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());
        updateAnimatedTextureFrame(animatable);

        for (GeoBone group : model.topLevelBones()) {
            renderRecursively(poseStack, animatable, group, renderType, bufferSource, buffer, isReRender, partialTick, packedLight,
                    packedOverlay, red, green, blue, alpha);
        }
        poseStack.pop();
    }

    /**
     * Gets and caches the relevant armor model bones for this baked model if it hasn't been done already
     */
    protected void grabRelevantBones(BakedGeoModel bakedModel) {
        if (this.lastModel == bakedModel)
            return;

        this.lastModel = bakedModel;
        this.head = getHeadBone();
        this.body = getBodyBone();
        this.rightArm = getRightArmBone();
        this.leftArm = getLeftArmBone();
        this.rightLeg = getRightLegBone();
        this.leftLeg = getLeftLegBone();
        this.rightBoot = getRightBootBone();
        this.leftBoot = getLeftBootBone();
    }

    /**
     * Prepare the renderer for the current render cycle.<br>
     * Must be called prior to render as the default HumanoidModel doesn't give render context.<br>
     * Params have been left nullable so that the renderer can be called for model/texture purposes safely.
     * If you do grab the renderer using null parameters, you should not use it for actual rendering.
     * @param entity The entity being rendered with the armor on
     * @param stack The ItemStack being rendered
     * @param slot The slot being rendered
     * @param baseModel The default (vanilla) model that would have been rendered if this model hadn't replaced it
     */
    public void prepForRender(@Nullable Entity entity, ItemStack stack, @Nullable EquipmentSlot slot, @Nullable BipedEntityModel<?> baseModel,T animatable) {
        if (entity == null || slot == null || baseModel == null)
            return;

        this.baseModel = baseModel;
        this.currentEntity = entity;
        this.currentStack=stack;
        this.animatable =animatable;
    }

    /**
     * Applies settings and transformations pre-render based on the default model
     */
    protected void applyBaseModel(BipedEntityModel<?> baseModel) {
        this.child = baseModel.child;
        this.sneaking = baseModel.sneaking;
        this.riding = baseModel.riding;
        this.rightArmPose = baseModel.rightArmPose;
        this.leftArmPose = baseModel.leftArmPose;
    }

    /**
     * Resets the bone visibility for the model based on the currently rendering slot,
     * and then sets bones relevant to the current slot as visible for rendering.<br>
     * <br>
     * This is only called by default for non-geo entities (I.E. players or vanilla mobs)
     */
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        setVisible(false);

        switch (currentSlot) {
            case HEAD -> setBoneVisible(this.head, true);
            case CHEST -> {
                setBoneVisible(this.body, true);
                setBoneVisible(this.rightArm, true);
                setBoneVisible(this.leftArm, true);
            }
            case LEGS -> {
                setBoneVisible(this.rightLeg, true);
                setBoneVisible(this.leftLeg, true);
            }
            case FEET -> {
                setBoneVisible(this.rightBoot, true);
                setBoneVisible(this.leftBoot, true);
            }
            default -> {}
        }
    }

    /**
     * Resets the bone visibility for the model based on the current {@link ModelPart} and {@link EquipmentSlot},
     * and then sets the bones relevant to the current part as visible for rendering.<br>
     * <br>
     * If you are rendering a geo entity with armor, you should probably be calling this prior to rendering
     */
    public void applyBoneVisibilityByPart(EquipmentSlot currentSlot, ModelPart currentPart, BipedEntityModel<?> model) {
        setVisible(false);

        currentPart.visible = true;
        GeoBone bone = null;

        if (currentPart == model.hat || currentPart == model.head) {
            bone = this.head;
        }
        else if (currentPart == model.body) {
            bone = this.body;
        }
        else if (currentPart == model.leftArm) {
            bone = this.leftArm;
        }
        else if (currentPart == model.rightArm) {
            bone = this.rightArm;
        }
        else if (currentPart == model.leftLeg) {
            bone = currentSlot == EquipmentSlot.FEET ? this.leftBoot : this.leftLeg;
        }
        else if (currentPart == model.rightLeg) {
            bone = currentSlot == EquipmentSlot.FEET ? this.rightBoot : this.rightLeg;
        }

        if (bone != null)
            bone.setHidden(false);
    }

    /**
     * Transform the currently rendering {@link GeoModel} to match the positions and rotations of the base model
     */
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

    public void setVisible(boolean pVisible) {
        setBoneVisible(this.head, pVisible);
        setBoneVisible(this.body, pVisible);
        setBoneVisible(this.rightArm, pVisible);
        setBoneVisible(this.leftArm, pVisible);
        setBoneVisible(this.rightLeg, pVisible);
        setBoneVisible(this.leftLeg, pVisible);
        setBoneVisible(this.rightBoot, pVisible);
        setBoneVisible(this.leftBoot, pVisible);
    }

    /**
     * Apply custom scaling to account for {@link net.minecraft.client.render.entity.model.AnimalModel AgeableListModel} baby models
     */
    public void scaleModelForBaby(MatrixStack poseStack, T animatable, float partialTick, boolean isReRender) {
        if (!this.child || isReRender)
            return;


    }

    /**
     * Sets a bone as visible or hidden, with nullability
     */
    protected void setBoneVisible(@Nullable GeoBone bone, boolean visible) {
        if (bone == null)
            return;

        bone.setHidden(!visible);
    }

    /**
     * Update the current frame of a {@link AnimatableTexture potentially animated} texture used by this GeoRenderer.<br>
     * This should only be called immediately prior to rendering, and only
     * @see AnimatableTexture#setAndUpdate
     */
    @Override
    public void updateAnimatedTextureFrame(T animatable) {
        if (this.currentEntity != null)
            AnimatableTexture.setAndUpdate(getTextureLocation(animatable));
    }


}
