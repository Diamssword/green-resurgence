package com.diamssword.greenresurgence.render.blockEntityRenderer;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ArmorTinkererBlockEntity;
import com.diamssword.greenresurgence.blocks.ArmorTinkererBlock;
import com.diamssword.greenresurgence.items.ModularArmorItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Equipment;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.animatable.client.RenderProvider;

@Environment(EnvType.CLIENT)
public class ArmorTinkererBlockEntityRenderer implements BlockEntityRenderer<ArmorTinkererBlockEntity> {

	private final BlockRenderManager blockRenderManager;
	private final BipedEntityModel<LivingEntity> fakeModel = new BipedEntityModel<>(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.ARMOR_STAND));
	private final RenderLayer fakeTexture = RenderLayer.getArmorCutoutNoCull(GreenResurgence.asRessource("textures/modular/armor/default.png"));

	public ArmorTinkererBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.blockRenderManager = ctx.getRenderManager();
	}

	@Override
	public void render(ArmorTinkererBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		var bstate = blockEntity.getWorld().getBlockState(blockEntity.getPos());
		if (bstate.getBlock() instanceof ArmorTinkererBlock) {
			if (bstate.get(ArmorTinkererBlock.BOTTOM)) {
				matrices.push();
				var entity = new ArmorStandEntity(blockEntity.getWorld(), 0, 0, 0);
				var scale = 1.65f;
				matrices.translate(0.5f, 0.5f, 0.5f);
				matrices.scale(scale, scale, scale);
				var vert = vertexConsumers.getBuffer(fakeTexture);
				var dir = bstate.get(ArmorTinkererBlock.FACING);
				renderPiece(blockEntity, EquipmentSlot.HEAD, dir, -1.66f, entity, matrices, vert, light, overlay);
				renderPiece(blockEntity, EquipmentSlot.CHEST, dir, -1.35f, entity, matrices, vert, light, overlay);
				renderPiece(blockEntity, EquipmentSlot.LEGS, dir, -1.34f, entity, matrices, vert, light, overlay);
				renderPiece(blockEntity, EquipmentSlot.FEET, dir, -1.33f, entity, matrices, vert, light, overlay);
				matrices.pop();

			}
		}


	}

	private void renderPiece(ArmorTinkererBlockEntity blockEntity, EquipmentSlot slot, Direction facing, float offset, ArmorStandEntity fakeEntity, MatrixStack matrices, VertexConsumer vertexConsumers, int light, int overlay) {
		var item1 = blockEntity.getArmorStack(slot);
		if (item1.getItem() instanceof Equipment eq) {
			if (eq.getSlotType() != slot)
				return;
		}
		RenderProvider rend = RenderProvider.of(item1);
		var model = new BipedEntityModel<>(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.ARMOR_STAND));
		if (item1.getItem() instanceof ModularArmorItem && rend != null) {
			matrices.push();
			if (slot == EquipmentSlot.HEAD)
				matrices.scale(0.7f, 0.7f, 0.78f);
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(facing.asRotation()));
			var hum = rend.getHumanoidArmorModel(fakeEntity, item1, slot, model);
			matrices.translate(0, offset, 0);
			hum.render(matrices, vertexConsumers, light, overlay, 1, 1, 1, 1);
			matrices.pop();
		}
	}
}