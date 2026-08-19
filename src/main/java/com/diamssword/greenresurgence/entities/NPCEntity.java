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
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.*;

public class NPCEntity extends PathAwareEntity implements IPlayerAppearanceProvider {

	private static final TrackedData<NbtCompound> CLOTH = DataTracker.registerData(NPCEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	private static final TrackedData<NbtCompound> APPEARANCE = DataTracker.registerData(NPCEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
	private Map<LayerDef, ClothData> cloths = new HashMap<>();

	public NPCEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
		super(entityType, world);
		generateSkin();
		generateCloths();
		calculateDimensions();
	}

	@Override
	public float getScaleFactor() {
		return ApiSkinValues.HeightMToMCScale(1, this.getSkinDatas().size);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
		this.goalSelector.add(4, new TemptGoal(this, 1.2, Ingredient.ofItems(Items.CARROT_ON_A_STICK), false));
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
		this.goalSelector.add(8, new LookAroundGoal(this));
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
		calculateDimensions();

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
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F)
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
		var lays = CharactersApi.bodyParts().getBodyLayers().values()
				.stream()
				.sorted((a, b) -> (int) ((a.size() * 100000) - (int) (b.size() * 100000))).toList();
		lays.forEach((v) -> {
			if(!v.external()) {
				if(!v.clearable() || random.nextFloat() > 0.5f) {

					var r = createRandomFor(v.id());
					if(v.splited()) {
						r.side = "left";
						var r1 = new SkinLayerValue();
						r1.side = "right";
						r1.id = r.id;
						r1.category = r.category;
						r1.layer = r.layer;
						r1.parent = r.parent;
						ls.add(r1);
					} else if(v.multi() && random.nextFloat() > 0.3f) {
						var c = random.nextInt(2);
						for(int i = 0; i < c; i++) {
							ls.add(createRandomFor(v.id()));
						}
					}
					ls.add(r);
				}
			}

		});
		val.layers = ls.toArray(new SkinLayerValue[0]);
		val.size = 35 + this.random.nextInt(55);
		val.slim = this.random.nextFloat() > 0.5f;
		setSkinData(val.toNBT());
	}

	public void generateCloths() {
		CharactersApi.clothing().getClothLayers().forEach(l -> {
			if((!l.id.equals("glasses") || random.nextFloat() > 0.8f) && (!l.id.equals("hat") || random.nextFloat() > 0.7f) && (l.isForced() || this.random.nextFloat() > 0.33f)) {
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
	public boolean isPersistent() {
		return true;
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
		} else if(APPEARANCE.equals(data))
			if(this.getWorld().isClient) {
				calculateDimensions();
			}
	}
}
