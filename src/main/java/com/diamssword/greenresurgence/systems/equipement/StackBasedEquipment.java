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
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StackBasedEquipment implements IUpgradableEquipment {

	private final String subcategory;
	private final String category;
	private final Map<String, ItemStack> content = new HashMap<>();
	private String skin;
	public final ItemStack stack;
	private final IEquipmentDef equipment;
	private IEquipmentUpgrade[] baseUpgrades = new EquipmentUpgradeItem[0];

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

	public StackBasedEquipment setBaseToolUpgrades(IEquipmentUpgrade[] upgrades) {
		this.baseUpgrades = upgrades;
		return this;
	}

	protected void fromNBT(NbtCompound tag) {
		content.clear();
		if(tag.contains("upgrades")) {
			var ut = tag.getCompound("upgrades");
			for(var k : ut.getKeys()) {
				var st = ut.getCompound(k);
				content.put(k, ItemStack.fromNbt(st));
			}
		}
		if(tag.contains("skin"))
			skin = tag.getString("skin");
	}

	public ItemStack getUpgradeItem(String slot) {
		return content.getOrDefault(slot, ItemStack.EMPTY);
	}

	public void setUpgrade(ItemStack upgradeItem) {
		if(upgradeItem.getItem() instanceof EquipmentUpgradeItem up && equipment != null && up.canBeApplied(equipment, upgradeItem)) {
			this.content.put(up.slot(equipment), upgradeItem);
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

	public boolean isMinimalUpgradesSet() {
		for(String requiredSlot : this.equipment.getRequiredSlots()) {
			if(!this.content.containsKey(requiredSlot))
				return false;
		}
		return true;
	}

	public void clearUpgrade(String slot) {
		this.content.remove(slot);
		if(slot.equals(Equipments.P_SKIN))
			skin = "";
	}

	public void save() {
		var nbt = stack.getOrCreateNbt();
		var ups = new NbtCompound();
		content.forEach((k, v) -> {
			ups.put(k, v.writeNbt(new NbtCompound()));
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
		content.keySet().forEach(v -> {
			var eq = getAsEquipment(v);
			if(eq.isPresent()) {
				var mod = eq.get().getAttributeModifiers(slot, player);
				if(mod != null) {map.putAll(mod);}
			}
		});
		for(IEquipmentUpgrade baseUpgrade : baseUpgrades) {
			var mod = baseUpgrade.getAttributeModifiers(slot, player);
			if(mod != null) {map.putAll(mod);}
		}
		return map;
	}

	protected Optional<EquipmentUpgradeItem> getAsEquipment(String slot) {
		if(getUpgradeItem(slot).getItem() instanceof EquipmentUpgradeItem up) {
			return Optional.of(up);
		}
		return Optional.empty();
	}

	@Override
	public void onInteraction(PlayerEntity wearer, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction, HitResult context) {
		content.keySet().forEach(v -> {
			var eq = getAsEquipment(v);
			eq.ifPresent(equipmentUpgradeItem -> equipmentUpgradeItem.onInteraction(wearer, slot, interaction, context));
		});
		for(IEquipmentUpgrade baseUpgrade : baseUpgrades) {
			baseUpgrade.onInteraction(wearer, slot, interaction, context);
		}
	}

	public boolean onToolDamage(LivingEntity owner, AdvEquipmentSlot slot) {
		if(content.isEmpty()) return true;
		var keys = this.content.keySet().stream().filter(v -> getAsEquipment(v).map(EquipmentUpgradeItem::isDamageable).orElse(false));
		var picked = Utils.selectRandomWeighted(keys.toList(), k -> getAsEquipment(k).map(EquipmentUpgradeItem::damageWheight).orElse(0f));
		var dura = this.content.get(picked).getDamage() + 1;
		if(dura >= getAsEquipment(picked).get().maxDurability()) {
			if(owner instanceof PlayerEntity) {
				((PlayerEntity) owner).incrementStat(Stats.BROKEN.getOrCreateStat(getAsEquipment(picked).get()));
			}
			this.content.remove(picked);
			if(this.content.isEmpty()) {
				return true;
			} else {
				var vsl = slot.getParent();
				if(vsl != null) {owner.sendEquipmentBreakStatus(vsl);}
			}
		} else {
			this.content.get(picked).setDamage(dura);

		}
		return false;
	}

	@Override
	public void onTick(Entity parent) {
		this.content.forEach((k, v) -> {
			getAsEquipment(k).ifPresent(v1 -> v1.onTick(stack, parent));
		});
		for(IEquipmentUpgrade baseUpgrade : baseUpgrades) {
			baseUpgrade.onTick(stack, parent);
		}
	}

	public float getDurabilityProgress() {
		var ls = this.content.keySet().stream().filter(v -> getAsEquipment(v).map(EquipmentUpgradeItem::isDamageable).orElse(false)).toList();
		var act = 0;
		var max = 0;
		for(var k : ls) {
			max += getAsEquipment(k).get().maxDurability();
			act += content.get(k).getDamage();
		}
		if(max == 0)
			return 1;
		return 1f - ((float) act / (float) max);
	}

}
