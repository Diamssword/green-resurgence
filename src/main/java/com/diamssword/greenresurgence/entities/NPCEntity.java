package com.diamssword.greenresurgence.entities;

import com.diamssword.characters.api.CharacterClothingApi;
import com.diamssword.characters.api.CharactersApi;
import com.diamssword.characters.api.IPlayerAppearanceProvider;
import com.diamssword.characters.api.appearence.LayerDef;
import com.diamssword.characters.api.clothing.ClothData;
import com.diamssword.characters.api.http.ApiSkinValues;
import com.diamssword.characters.api.http.SkinLayerValue;
import com.diamssword.characters.api.skin.BodyLayerCategory;
import com.diamssword.characters.api.skin.BodyLayerImageGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.*;

public class NPCEntity extends LivingEntity implements IPlayerAppearanceProvider {

	private static final TrackedData<NbtCompound> CLOTH = DataTracker.registerData(NPCEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	private static final TrackedData<NbtCompound> APPEARANCE = DataTracker.registerData(NPCEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	private Map<LayerDef, ClothData> cloths = new HashMap<>();

	public NPCEntity(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
		generateSkin();
		generateCloths();
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CLOTH, new NbtCompound());
		this.dataTracker.startTracking(APPEARANCE, new NbtCompound());
	}

	@Override
	public Iterable<ItemStack> getArmorItems() {
		return List.of();
	}

	@Override
	public ItemStack getEquippedStack(EquipmentSlot slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack) {

	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		var skin = this.getSkinDatas();
		if(skin.layers.length > 0)
			nbt.put("skin", skin.toNBT());
		nbt.put("cloths", clothsToNBT(cloths));
	}

	private NbtCompound clothsToNBT(Map<LayerDef, ClothData> cloths) {
		var tag = new NbtCompound();
		cloths.forEach((k, v) -> {
			var nb = new NbtCompound();
			if(v.texture() != null)
				nb.putString("texture", v.texture().toString());
			if(v.needColor())
				nb.putInt("color", v.color());
			tag.put(k.id, nb);
		});
		return tag;
	}

	private Map<LayerDef, ClothData> clothsFromNBT(NbtCompound tag) {
		Map<LayerDef, ClothData> res = new HashMap<>();
		tag.getKeys().forEach(k -> {
			CharactersApi.clothing().getLayer(k).ifPresent(l -> {
				if(!l.id.equals("full")) {
					var t = tag.getCompound(k);
					if(t.contains("texture")) {
						var bl = t.contains("color");
						res.put(l, new ClothData(new Identifier(t.getString("texture")), bl, bl ? t.getInt("color") : -1));
					}
				}
			});
		});

		return res;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if(nbt.contains("skin"))
			this.setSkinData(nbt.getCompound("skin"));
		if(nbt.contains("cloths")) {
			this.cloths = clothsFromNBT(nbt.getCompound("cloths"));
			this.syncCloths();
		}

	}

	@Override
	public Arm getMainArm() {
		return Arm.RIGHT;
	}

	public static DefaultAttributeContainer.Builder createAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
				.add(EntityAttributes.GENERIC_FLYING_SPEED, 0.1F)
				.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.2F)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1F)
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
	}

	public void setSkinData(NbtCompound nbt) {
		this.dataTracker.set(APPEARANCE, nbt);
	}

	public void syncCloths() {
		this.dataTracker.set(CLOTH, clothsToNBT(this.cloths));
	}

	public void generateSkin() {
		var val = new ApiSkinValues();
		var ls = new ArrayList<SkinLayerValue>();
		CharactersApi.bodyParts().getBodyLayers().keySet().forEach(s -> ls.add(createRandomFor(s)));
		val.layers = ls.toArray(new SkinLayerValue[0]);
		val.size = 145 + this.random.nextInt(45);
		val.slim = this.random.nextFloat() > 0.5f;

		setSkinData(val.toNBT());
	}

	public void generateCloths() {
		CharactersApi.clothing().getClothLayers().forEach(l -> {
			if(l.isForced() || this.random.nextFloat() > 0.33f) {
				var ls = CharactersApi.clothing().getClothsIn(CharacterClothingApi.ALL_COLLECTIONS, l);
				if(!ls.isEmpty()) {
					cloths.put(l, new ClothData(ls.get(random.nextInt(ls.size())).id(), false, -1));
				}

			}
		});
		syncCloths();
	}

	public static SkinLayerValue createRandomFor(String layer) {
		return CharactersApi.bodyParts().getBodyParts(layer).map((l) -> {
			SkinLayerValue res = new SkinLayerValue();
			res.layer = l.id();
			BodyLayerCategory cat = l.cats()[(int) (Math.random() * l.cats().length)];
			if(l.cats().length > 1) {
				res.category = cat.id();
			}

			BodyLayerImageGroup img = cat.images()[(int) (Math.random() * cat.images().length)];
			if(img.hasSubs()) {
				res.parent = img.id();
				res.id = img.subs()[(int) (Math.random() * img.subs().length)].id();
			} else {
				res.id = img.id();
			}
			return res;
		}).orElse(null);
	}

	@Override
	public ApiSkinValues getSkinDatas() {
		var app = this.dataTracker.get(APPEARANCE);
		if(!app.isEmpty()) {
			return new ApiSkinValues().fromNBT(app);
		}
		return new ApiSkinValues();
	}

	@Override
	public Optional<ClothData> getClothDatas(LayerDef layerDef) {
		return Optional.ofNullable(cloths.get(layerDef));
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if(CLOTH.equals(data)) {
			if(this.getWorld().isClient) {
				this.cloths = clothsFromNBT(this.dataTracker.get(CLOTH));
			}
		}
	}
}
