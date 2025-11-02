package com.diamssword.greenresurgence.systems.equipement;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.items.equipment.EquipmentUpgradeItem;
import com.diamssword.greenresurgence.utils.Utils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StackBasedEquipment implements IUpgradableEquipment {

	private final String subcategory;
	private final String category;
	private final Map<String, ItemStack> content = new HashMap<>();
	private final Map<String, Item> broken = new HashMap<>();
	private String skin;
	public final ItemStack stack;
	private final IEquipmentDef equipment;
	private Map<String, EffectLevel> baseUpgrades = new HashMap<>();
	private final Map<IEquipmentEffect, EffectLevel> combinedEffects = new HashMap<>();
	private final Map<String, EffectLevel> combinedEffectsString = new HashMap<>();
	private boolean isComputed = false;

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

	public StackBasedEquipment(String category, String subcategory, ItemStack stack, Map<String, EffectLevel> upgrades) {
		this.category = category;
		this.subcategory = subcategory;
		this.stack = stack;
		this.equipment = Equipments.getEquipment(category, subcategory).get();
		this.baseUpgrades = upgrades;
		fromNBT(stack.getOrCreateNbt());
	}

	public StackBasedEquipment(IEquipmentDef def, ItemStack stack, Map<String, EffectLevel> upgrades) {
		this.category = def.getEquipmentType();
		this.subcategory = def.getEquipmentSubtype();
		this.stack = stack;
		this.equipment = def;
		this.baseUpgrades = upgrades;
		fromNBT(stack.getOrCreateNbt());
	}

	public StackBasedEquipment setBaseToolUpgrades(Map<String, EffectLevel> upgrades) {
		this.baseUpgrades = upgrades;
		return this;
	}

	protected void fromNBT(NbtCompound tag) {
		content.clear();
		broken.clear();
		if(tag.contains("upgrades")) {
			var ut = tag.getCompound("upgrades");
			for(var k : ut.getKeys()) {
				var st = ut.getCompound(k);
				content.put(k, ItemStack.fromNbt(st));
			}
		}
		if(tag.contains("broken")) {
			var ut = tag.getCompound("broken");
			for(var k : ut.getKeys()) {
				var st = ut.getString(k);
				broken.put(k, Registries.ITEM.get(new Identifier(st)));
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
			this.broken.remove(up.slot(equipment));
			if(up.slot(equipment).equals(Equipments.P_SKIN)) {
				skin = upgradeItem.getOrCreateNbt().getString("skin");
			}
			computeEffects();
		}

	}

	public IEquipmentDef getEquipment() {
		return equipment;
	}

	@Override
	public List<TagKey<Item>> getTags() {
		if(!isComputed)
			computeEffects();
		var ctx = new UpgradeActionContext(null, null, UpgradeActionContext.ItemContext.TOOL).setLevels(combinedEffectsString);
		var res = new ArrayList<TagKey<Item>>();
		combinedEffects.forEach((k, v) -> {
			res.addAll(k.getTags(ctx));
		});
		return res;
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
		computeEffects();
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
		var brokens = new NbtCompound();
		broken.forEach((k, v) -> {
			brokens.putString(k, Registries.ITEM.getId(v).toString());
		});
		nbt.put("broken", brokens);
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
		if(!isComputed)
			computeEffects();

		Multimap<EntityAttribute, EntityAttributeModifier> map = ArrayListMultimap.create();
		var ctx = new UpgradeActionContext(player, null, UpgradeActionContext.ItemContext.TOOL).setLevels(combinedEffectsString);

		combinedEffects.forEach((k, v) -> {
			k.getAttributeModifiers(map, slot, ctx);
		});
		return map;
	}

	/**
	 * Compute all bases tools upgrades and itemstack inserted upgrades for upgrades effects cumulation
	 * We try to call this function only when necessary, so it is only called when changing upgrades or in the related functions IF not already computed
	 */
	protected void computeEffects() {
		isComputed = true;
		combinedEffects.clear();
		combinedEffectsString.clear();
		content.keySet().forEach(v -> {
			var eq = getAsEquipment(v);
			eq.ifPresent(equipmentUpgradeItem -> equipmentUpgradeItem.getEffectsLevels().forEach((k1, v1) -> {
				EquipmentEffects.get(k1).ifPresent(a -> {
					if(!combinedEffects.containsKey(a))
						combinedEffects.put(a, v1);
					else
						combinedEffects.get(a).add(v1);
					if(!combinedEffectsString.containsKey(k1))
						combinedEffectsString.put(k1, v1);
				});
			}));
		});
		baseUpgrades.forEach((k, v) -> {
			EquipmentEffects.get(k).ifPresent(a -> {

				if(!combinedEffects.containsKey(a))
					combinedEffects.put(a, v);
				else
					combinedEffects.get(a).add(v);
				if(!combinedEffectsString.containsKey(k))
					combinedEffectsString.put(k, v);
			});
		});
	}

	protected Optional<EquipmentUpgradeItem> getAsEquipment(String slot) {
		if(getUpgradeItem(slot).getItem() instanceof EquipmentUpgradeItem up) {
			return Optional.of(up);
		}
		return Optional.empty();
	}

	@Override
	public void onInteraction(PlayerEntity wearer, AdvEquipmentSlot slot, IEquipmentUpgrade.InteractType interaction, HitResult context) {

		if(!isComputed)
			computeEffects();
		if(context instanceof EntityHitResult res && res.getEntity() instanceof LivingEntity li) {
			var ctx = new UpgradeActionContext(wearer, li, UpgradeActionContext.ItemContext.TOOL).setLevels(combinedEffectsString);
			combinedEffects.forEach((k, v) -> {

				k.onInteraction(ctx, slot, interaction);
			});
		}

	}

	public boolean onToolDamage(LivingEntity owner, AdvEquipmentSlot slot) {
		if(content.isEmpty()) return true;
		var keys = this.content.keySet().stream().filter(v -> getAsEquipment(v).map(EquipmentUpgradeItem::isDamageable).orElse(false));
		var picked = Utils.selectRandomWeighted(keys.toList(), k -> getAsEquipment(k).map(EquipmentUpgradeItem::damageWeight).orElse(0f));
		var dura = this.content.get(picked).getDamage() + 1;
		if(dura >= getAsEquipment(picked).get().maxDurability()) {
			if(owner instanceof PlayerEntity) {
				((PlayerEntity) owner).incrementStat(Stats.BROKEN.getOrCreateStat(getAsEquipment(picked).get()));
			}
			this.broken.put(picked, this.content.remove(picked).getItem());
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

	public void appendTooltip(List<Text> tooltip) {
		if(!isComputed)
			computeEffects();
		var ctx = new UpgradeActionContext(null, null, UpgradeActionContext.ItemContext.TOOL).setLevels(combinedEffectsString);
		for(AdvEquipmentSlot value : AdvEquipmentSlot.values()) {
			List<Text> subList = new ArrayList<>();
			combinedEffectsString.forEach((k, v) -> {
				var eff = EquipmentEffects.get(k);
				eff.ifPresent(p -> {
					p.addTooltips(ctx, value, subList);
				});
			});
			if(!subList.isEmpty()) {
				TooltipHelper.appendUpgradeHeader(value, ctx.context == UpgradeActionContext.ItemContext.UPGRADE, tooltip);
				tooltip.addAll(subList);
			}
		}
		if(!GreenResurgence.clientHelper.isShiftPressed()) {
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.press_shift").formatted(Formatting.GRAY, Formatting.ITALIC));
		} else {
			if(!broken.isEmpty()) {
				tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.broken_parts").formatted(Formatting.RED));
				for(String s : broken.keySet()) {

					tooltip.add(Text.literal(" - ").append(Text.translatable("equipment.green_resurgence.gui." + s)).formatted(Formatting.LIGHT_PURPLE));
				}
			}
		}
	}

	//TODO this is probably the least optimized call right now, might want to rework it if we don't need all the computed levels infos
	@Override
	public void onTick(Entity parent, AdvEquipmentSlot slot) {
		if(!isComputed)
			computeEffects();
		UpgradeActionContext ctx;
		if(parent instanceof PlayerEntity pl) {
			ctx = new UpgradeActionContext(pl, null, UpgradeActionContext.ItemContext.TOOL).setLevels(combinedEffectsString);
		} else {
			ctx = new UpgradeGroundActionContext(parent, UpgradeActionContext.ItemContext.TOOL).setLevels(combinedEffectsString);
		}
		combinedEffects.forEach((k, v) -> {
			k.onInteraction(ctx, slot, IEquipmentUpgrade.InteractType.TICK);
		});
	}

	public float getDurabilityProgress() {
		var ls = this.content.keySet().stream().filter(v -> getAsEquipment(v).map(EquipmentUpgradeItem::isDamageable).orElse(false)).toList();
		var act = 0;
		var max = 0;
		for(var k : ls) {
			max += getAsEquipment(k).get().maxDurability();
			act += content.get(k).getDamage();
		}
		for(var k : this.broken.keySet()) {
			var v = this.broken.get(k);
			if(v instanceof EquipmentUpgradeItem up) {
				max += up.maxDurability();
				act += up.maxDurability();
			}
		}
		if(max == 0)
			return 1;
		return 1f - ((float) act / (float) max);
	}

}
