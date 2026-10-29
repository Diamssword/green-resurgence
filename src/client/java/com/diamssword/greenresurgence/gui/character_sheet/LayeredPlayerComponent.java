package com.diamssword.greenresurgence.gui.character_sheet;

import com.diamssword.greenresurgence.gui.components.PlayerComponent;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.util.pond.OwoEntityRenderDispatcherExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class LayeredPlayerComponent extends PlayerComponent {
	protected final LayeredPlayer renderer;

	protected LayeredPlayerComponent(Sizing sizing, AbstractClientPlayerEntity entity) {
		super(sizing, entity);
		var mc = MinecraftClient.getInstance();
		EntityRendererFactory.Context context = new EntityRendererFactory.Context(mc.getEntityRenderDispatcher(), mc.getItemRenderer(), mc.getBlockRenderManager(), mc.getEntityRenderDispatcher().getHeldItemRenderer(), mc.getResourceManager(), mc.getEntityModelLoader(), mc.textRenderer);
		renderer = new LayeredPlayer(context, true);
	}

	protected LayeredPlayerComponent(Sizing sizing) {
		this(sizing, new OtherClientPlayerEntity(MinecraftClient.getInstance().world, new GameProfile(MinecraftClient.getInstance().player.getGameProfile().getId(), "")) {
			@Override
			public boolean isSpectator() {
				return false;
			}

			@Override
			public boolean isCreative() {
				return false;
			}
		});
	}


	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		var matrices = context.getMatrices();
		matrices.push();

		matrices.translate(x + this.width / 2f, y + this.height * 0.6f, 100);
		var val = 75 * this.scale * Math.max(this.width, this.height) / 64f;
		val = val + (float) zoom;
		matrices.scale(val, -val, 75 * this.scale);
		matrices.translate(0, (entity.getHeight() / -2f) + xOffsetFactor, 0);

		this.transform.accept(matrices);

		if(this.lookAtCursor) {
			float xRotation = (float) Math.toDegrees(Math.atan((mouseY - this.y - this.height / 2f) / 40f));
			float yRotation = (float) Math.toDegrees(Math.atan((mouseX - this.x - this.width / 2f) / 40f));
			this.entity.prevHeadYaw = -yRotation;
			this.entity.prevYaw = -yRotation;
			this.entity.prevPitch = xRotation * .65f;

			// We make sure the xRotation never becomes 0, as the lighting otherwise becomes very unhappy
			if(xRotation == 0) xRotation = .1f;
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(xRotation * .15f));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRotation * .15f));
		} else {
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(35));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45 + this.mouseRotation));
		}
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
		var dispatcher = (OwoEntityRenderDispatcherExtension) this.dispatcher;
		dispatcher.owo$setCounterRotate(true);
		dispatcher.owo$setShowNametag(this.showNametag);

		RenderSystem.setShaderLights(new Vector3f(.15f, 1, 0), new Vector3f(.15f, -1, 0));
		this.dispatcher.setRenderShadows(false);
		this.render(0, 0, 0, 0, 0, matrices, this.entityBuffers, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		this.dispatcher.setRenderShadows(true);
		this.entityBuffers.draw();
		DiffuseLighting.enableGuiDepthLighting();

		matrices.pop();

		dispatcher.owo$setCounterRotate(false);
		dispatcher.owo$setShowNametag(true);
	}

	public void render(double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {


		try {
			Vec3d vec3d = renderer.getPositionOffset((AbstractClientPlayerEntity) entity, tickDelta);
			double d = x + vec3d.getX();
			double e = y + vec3d.getY();
			double f = z + vec3d.getZ();
			matrices.push();
			matrices.translate(d, e, f);
			renderer.render((AbstractClientPlayerEntity) entity, yaw, tickDelta, matrices, vertexConsumers, light);


			matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());


			matrices.pop();
		} catch(Throwable var24) {
			CrashReport crashReport = CrashReport.create(var24, "Rendering entity in gui");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being rendered");
			entity.populateCrashReport(crashReportSection);
			CrashReportSection crashReportSection2 = crashReport.addElement("Renderer details");
			crashReportSection2.add("Assigned renderer", renderer);
			crashReportSection2.add("Rotation", yaw);
			crashReportSection2.add("Delta", tickDelta);
			throw new CrashException(crashReport);
		}
	}

	public LayeredPlayer getRenderer() {
		return renderer;
	}
}
