package com.diamssword.greenresurgence.render.customPose.animated;

import net.minecraft.client.model.ModelPart;
import software.bernie.geckolib.cache.object.GeoBone;

public class ModelPartBackedGeoBone extends GeoBone {

	private final ModelPart part;
	private final boolean acceptZero;

	public ModelPartBackedGeoBone(String name, ModelPart part, boolean acceptZeroValues) {
		super(null, name, false, null, null, null);
		this.part = part;
		this.acceptZero = acceptZeroValues;
		this.setPivotX(this.part.pivotX);
		this.setPivotY(this.part.pivotY);
		this.setPivotZ(this.part.pivotZ);
	}

	@Override
	public void updatePivot(float pivotX, float pivotY, float pivotZ) {
		super.updatePivot(pivotX, pivotY, pivotZ);

	}

	@Override
	public void markPositionAsChanged() {
		super.markPositionAsChanged();
		//if(acceptZero || getPositionVector().length() != 0) {
		part.pivotX = this.getPivotX() + this.getPosX();
		part.pivotZ = this.getPivotZ() + this.getPosZ();
		part.pivotY = this.getPivotY() + this.getPosY();
		//	System.out.println(getPositionVector());
		//}
	}

	@Override
	public void markRotationAsChanged() {
		super.markRotationAsChanged();
		if(acceptZero || !(this.getRotX() == 0 && this.getRotY() == 0 && this.getRotZ() == 0)) {
			this.part.pitch = -this.getRotX();
			this.part.yaw = -this.getRotY();
			this.part.roll = this.getRotZ();
		}

	}

	@Override
	public void markScaleAsChanged() {
		super.markScaleAsChanged();
		this.part.xScale = this.getScaleX();
		this.part.yScale = this.getScaleY();
		this.part.zScale = this.getScaleZ();
		//this.part.scale(new Vector3f(this.getScaleX(), this.getScaleY(), this.getScaleZ()));
	}
}
