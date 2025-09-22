package com.diamssword.greenresurgence.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.ui.component.EntityComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.util.pond.OwoEntityRenderDispatcherExtension;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.w3c.dom.Element;

public class BetterEntityComponent<E extends Entity> extends EntityComponent<E> {

	protected float mouseRotationY = 0;

	protected BetterEntityComponent(Sizing sizing, E entity) {
		super(sizing, entity);
	}

	protected BetterEntityComponent(Sizing sizing, EntityType<E> type, @Nullable NbtCompound nbt) {
		super(sizing, type, nbt);
	}

	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		var matrices = context.getMatrices();
		matrices.push();

		matrices.translate(x + this.width / 2f, y + this.height / 2f, 100);
		matrices.scale(75 * this.scale * this.width / 64f, -75 * this.scale * this.height / 64f, 75 * this.scale);

		matrices.translate(0, entity.getHeight() / -2f, 0);

		this.transform.accept(matrices);

		if(this.lookAtCursor) {
			float xRotation = (float) Math.toDegrees(Math.atan((mouseY - this.y - this.height / 2f) / 40f));
			float yRotation = (float) Math.toDegrees(Math.atan((mouseX - this.x - this.width / 2f) / 40f));

			if(this.entity instanceof LivingEntity living) {
				living.prevHeadYaw = -yRotation;
			}

			this.entity.prevYaw = -yRotation;
			this.entity.prevPitch = xRotation * .65f;

			// We make sure the xRotation never becomes 0, as the lighting otherwise becomes very unhappy
			if(xRotation == 0) xRotation = .1f;
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(xRotation * .15f));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yRotation * .15f));
		} else {
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(35 + this.mouseRotationY));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45 + this.mouseRotation));
		}

		var dispatcher = (OwoEntityRenderDispatcherExtension) this.dispatcher;
		dispatcher.owo$setCounterRotate(true);
		dispatcher.owo$setShowNametag(this.showNametag);

		RenderSystem.setShaderLights(new Vector3f(.15f, 1, 0), new Vector3f(.15f, -1, 0));
		this.dispatcher.setRenderShadows(false);
		this.dispatcher.render(this.entity, 0, 0, 0, 0, 0, matrices, this.entityBuffers, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		this.dispatcher.setRenderShadows(true);
		this.entityBuffers.draw();
		DiffuseLighting.enableGuiDepthLighting();

		matrices.pop();

		dispatcher.owo$setCounterRotate(false);
		dispatcher.owo$setShowNametag(true);
	}

	@Override
	public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
		if(this.allowMouseRotation && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			this.mouseRotation += deltaX;
			this.mouseRotationY += deltaY;
			super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
			return true;
		} else {
			return super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
		}
	}

	public static EntityComponent<?> parse(Element element) {
		UIParsing.expectAttributes(element, "type");
		var entityId = UIParsing.parseIdentifier(element.getAttributeNode("type"));
		var entityType = Registries.ENTITY_TYPE.getOrEmpty(entityId).orElseThrow(() -> new UIModelParsingException("Unknown entity type " + entityId));

		NbtCompound nbt = null;
		if(element.hasAttribute("nbt")) {
			try {
				nbt = StringNbtReader.parse(element.getAttribute("nbt"));
			} catch(CommandSyntaxException cse) {
				throw new UIModelParsingException("Invalid NBT compound", cse);
			}
		}

		return new BetterEntityComponent<>(Sizing.content(), entityType, nbt);
	}

}
