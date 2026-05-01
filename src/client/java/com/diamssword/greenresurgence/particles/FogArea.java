package com.diamssword.greenresurgence.particles;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Box;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FogArea {
	private Vector4f color0;
	private Vector4f color1;
	private int maxCount0;
	private int maxCount1;
	private int tickFrequency;
	private int baseAge;
	private double scale0;
	private double scale1;
	private double speed0;
	private double speed1;
	private Box area;
	private boolean dead = false;
	private List<FogRenderer.FogLayer> layers0 = Collections.synchronizedList(new ArrayList<>());
	private List<FogRenderer.FogLayer> layers1 = Collections.synchronizedList(new ArrayList<>());

	public FogArea(Vector4f color0, Vector4f color1, double scale0, double scale1, double speed0, double speed1, int maxCount0, int maxCount1, int tickFrequency, int baseAge, Box area) {
		this.color0 = color0;
		this.color1 = color1;
		this.speed0 = speed0;
		this.speed1 = speed1;
		this.maxCount0 = maxCount0;
		this.maxCount1 = maxCount1;
		this.tickFrequency = tickFrequency;
		this.baseAge = baseAge;
		this.area = area;
		this.scale0 = scale0;
		this.scale1 = scale1;
	}

	public void tick(ClientWorld world) {
		if(world.getTime() % tickFrequency == 0) {
			if(layers0.size() < maxCount0) {
				var rx = area.minX + (Math.random() * (area.getXLength()));
				var ry = area.minY + (Math.random() * (area.getYLength()));
				var rz = area.minZ + (Math.random() * (area.getZLength()));
				var alfSpeed = speed0 / 2f;
				layers0.add(new FogRenderer.FogLayer(color0, new Vector3d(rx, ry, rz), new Vector3f((float) (-alfSpeed + (Math.random() * speed0)), (float) (-alfSpeed + (Math.random() * speed0)), (float) (-alfSpeed + (Math.random() * speed0))), (float) ((10f + (Math.random() * 5f)) * scale0), 1f, (int) (baseAge + (Math.random() * (baseAge / 5f)))));
			}
			if(layers1.size() < maxCount1) {
				var rx = area.minX - (area.getXLength() * 0.1) + (Math.random() * (area.getXLength() * 1.2));
				var ry = area.minY - (area.getYLength() * 0.1) + (Math.random() * (area.getYLength() * 1.2));
				var rz = area.minZ - (area.getZLength() * 0.1) + (Math.random() * (area.getZLength() * 1.2));
				var alfSpeed = speed1 / 2f;
				layers1.add(new FogRenderer.FogLayer(color1, new Vector3d(rx, ry, rz), new Vector3f((float) (-alfSpeed + (Math.random() * speed1)), (float) (-alfSpeed + (Math.random() * speed1)), (float) (-alfSpeed + (Math.random() * speed1))), (float) ((10f + (Math.random() * 5f)) * scale1), 1f, (int) (baseAge + (Math.random() * (baseAge / 5f)))));
			}
		}
		baseTick(layers0);
		baseTick(layers1);
	}

	public void reset() {
		layers0.clear();
		layers1.clear();
	}

	private void baseTick(List<FogRenderer.FogLayer> list) {
		List<FogRenderer.FogLayer> toRemove = new ArrayList<>();
		for(FogRenderer.FogLayer layer : list) {
			if(layer.age >= layer.maxAge)
				toRemove.add(layer);
			layer.position.x += layer.velocity.x;
			layer.position.y += layer.velocity.y;
			layer.position.z += layer.velocity.z;
			layer.age++;
		}
		for(FogRenderer.FogLayer fogLayer : toRemove) {
			list.remove(fogLayer);
		}
	}

	public List<FogRenderer.FogLayer> getLayers0() {
		return layers0;
	}

	public List<FogRenderer.FogLayer> getLayers1() {
		return layers1;
	}

	public Vector4f getColor0() {
		return color0;
	}

	public void setColor0(Vector4f color0) {
		this.color0 = color0;
	}

	public Vector4f getColor1() {
		return color1;
	}

	public void setColor1(Vector4f color1) {
		this.color1 = color1;
	}

	public int getMaxCount0() {
		return maxCount0;
	}

	public void setMaxCount0(int maxCount0) {
		this.maxCount0 = maxCount0;
	}

	public int getMaxCount1() {
		return maxCount1;
	}

	public void setMaxCount1(int maxCount1) {
		this.maxCount1 = maxCount1;
	}

	public int getTickFrequency() {
		return tickFrequency;
	}

	public void setTickFrequency(int tickFrequency) {
		this.tickFrequency = tickFrequency;
	}

	public int getBaseAge() {
		return baseAge;
	}

	public void setBaseAge(int baseAge) {
		this.baseAge = baseAge;
	}

	public Box getArea() {
		return area;
	}

	public void setArea(Box area) {
		this.area = area;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead() {
		this.reset();
		this.dead = true;
	}
}
