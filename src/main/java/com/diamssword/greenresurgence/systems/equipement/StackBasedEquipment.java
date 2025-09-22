package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.items.equipment.EquipmentUpgradeItem;
import com.diamssword.greenresurgence.utils.Utils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class StackBasedEquipment implements IUpgradableEquipment {

	private final String subcategory;
	private final String category;
	private final Map<String, EquipmentUpgradeItem> upgrades = new HashMap<>();
	private String skin;
	private final Map<String, Integer> durability = new HashMap<>();
	private final ItemStack stack;
	private final IEquipmentDef equipment;

	public StackBasedEquipment(String category, String subcategory, ItemStack stack) {
		this.category = category;
		this.subcategory = subcategory;
		this.stack = stack;
		this.equipment = Equipments.getEquipment(category, subcategory).get();
		fromNBT(stack.getOrCreateNbt());
	}

	public StackBasedEquipment(IEquipmentDef def, ItemStack stack) {
		this.category = def.getEquipmentType();
		this.subcategory = def.getEquipmentSubtype();
		this.stack = stack;
		this.equipment = def;
		fromNBT(stack.getOrCreateNbt());
	}

	protected void fromNBT(NbtCompound tag) {
		upgrades.clear();
		if(tag.contains("upgrades")) {
			var ut = tag.getCompound("upgrades");
			for(var k : ut.getKeys()) {
				var st = ut.getCompound(k);
				var item = Registries.ITEM.get(new Identifier(st.getString("id")));
				if(item instanceof EquipmentUpgradeItem up) {
					upgrades.put(k, up);
					if(st.contains("damage"))
						durability.put(k, st.getInt("damage"));
					else
						durability.put(k, up.maxDurability());
				}
			}
		}
		if(tag.contains("skin"))
			skin = tag.getString("skin");
	}

	public ItemStack getUpgradeItem(String slot) {
		if(upgrades.containsKey(slot)) {
			var st = new ItemStack(upgrades.get(slot), 1);
			st.setDamage(durability.get(slot));
			if(slot.equals(Equipments.P_SKIN)) {
				st.getOrCreateNbt().putString("skin", skin);
			}
			return st;
		}
		return ItemStack.EMPTY;
	}

	public void setUpgrade(ItemStack upgradeItem) {
		if(upgradeItem.getItem() instanceof EquipmentUpgradeItem up && equipment != null && up.canBeApplied(equipment, upgradeItem)) {
			this.upgrades.put(up.slot(equipment), up);
			this.durability.put(up.slot(equipment), upgradeItem.getDamage());
			if(up.slot(equipment).equals(Equipments.P_SKIN)) {
				skin = upgradeItem.getOrCreateNbt().getString("skin");
			}
		}
	}

	public IEquipmentDef getEquipment() {
		return equipment;
	}

	public void setUpgrade(ItemStack upgradeItem, String slot) {
		if(upgradeItem.isEmpty())
			clearUpgrade(slot);
		else
			setUpgrade(upgradeItem);
	}

	public void clearUpgrade(String slot) {
		this.upgrades.remove(slot);
		this.durability.remove(slot);

		if(slot.equals(Equipments.P_SKIN))
			skin = "";
	}

	public void save() {
		var nbt = stack.getOrCreateNbt();
		var ups = new NbtCompound();
		upgrades.forEach((k, v) -> {
			var up = new NbtCompound();
			up.putString("id", Registries.ITEM.getId(v).toString());
			up.putInt("damage", durability.get(k));
			ups.put(k, up);
		});
		nbt.put("upgrades", ups);
		if(skin != null)
			nbt.putString("skin", skin);
	}

	@Override
	public String getEquipmentType() {
		return category;
	}

	@Override
	public String getEquipmentSubtype() {
		return subcategory;
	}

	@Override
	public String getSkin() {

		return skin;
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(AdvEquipmentSlot slot, @Nullable PlayerEntity player) {
		Multimap<EntityAttribute, EntityAttributeModifier> map = ArrayListMultimap.create();
		upgrades.values().forEach(v -> {
			var mod = v.getAttributeModifiers(slot, player);
			if(mod != null) {map.putAll(mod);}
		});
		return map;
	}

	@Override
	public void onInteraction(PlayerEntity wearer, AdvEquipmentSlot slot, EquipmentUpgrade.InteractType interaction, HitResult context) {
		upgrades.values().forEach(v -> {
			v.onInteraction(wearer, slot, interaction, context);
		});
	}

	public boolean onToolDamage(LivingEntity owner, AdvEquipmentSlot slot) {
		if(upgrades.isEmpty()) return true;
		var keys = this.upgrades.keySet().stream().filter(v -> upgrades.get(v).isDamageable());
		var picked = Utils.selectRandomWeighted(keys.toList(), k -> this.upgrades.get(k).damageWheight());
		var dura = this.durability.get(picked) + 1;
		if(dura >= this.upgrades.get(picked).maxDurability()) {
			this.durability.remove(picked);
			if(owner instanceof PlayerEntity) {
				((PlayerEntity) owner).incrementStat(Stats.BROKEN.getOrCreateStat(this.upgrades.get(picked)));
			}
			this.upgrades.remove(picked);
			if(this.upgrades.isEmpty()) {
				return true;
			} else {
				var vsl = slot.getParent();
				if(vsl != null) {owner.sendEquipmentBreakStatus(vsl);}
			}
		} else {
			this.durability.put(picked, dura);

		}
		return false;
	}

	@Override
	public void onTick(Entity parent) {

	}

	public float getDurabilityProgress() {
		var ls = this.upgrades.keySet().stream().filter(v -> upgrades.get(v).isDamageable()).toList();
		var act = 0;
		var max = 0;
		for(var k : ls) {
			max += this.upgrades.get(k).maxDurability();
			act += this.durability.get(k);
		}
		if(max == 0)
			return 1;
		return 1f - ((float) act / (float) max);
	}

}
