package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.systems.equipement.*;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EquipmentUpgradeItem extends Item implements IEquipmentUpgrade {
	protected final String[] allowed;
	private final String slot;
	private final int durability;
	private final float weight;
	private List<String> effects = new ArrayList<>();

	public EquipmentUpgradeItem(String allowed, String slot, float wheight) {
		this(new OwoItemSettings().maxCount(8).group(MItems.GROUP).tab(1), allowed, slot, -1, wheight);
	}

	public EquipmentUpgradeItem(String allowed, String slot, int durability, float wheight) {
		this(new OwoItemSettings().maxCount(8).group(MItems.GROUP).tab(1), allowed, slot, durability, wheight);
	}

	public EquipmentUpgradeItem(String allowed, String slot, int durability, float wheight, String... effects) {
		this(new OwoItemSettings().maxCount(8).group(MItems.GROUP).tab(1), allowed, slot, durability, wheight, effects);
	}

	public EquipmentUpgradeItem(String allowed, String slot) {
		this(new OwoItemSettings().maxCount(8).group(MItems.GROUP).tab(1), allowed, slot, -1, 1);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		var lvls = getEffectsLevels();
		var ctx = new UpgradeActionContext(null, null, UpgradeActionContext.ItemContext.UPGRADE).setLevels(lvls);
		tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.slot").formatted(Formatting.GRAY).append(Text.translatable("equipment.green_resurgence.gui." + slot).formatted(Formatting.LIGHT_PURPLE)));
		List<MutableText> tools = new ArrayList<>();
		for(String s : allowed) {
			var sp = s.split("/");
			if(sp[0].equals("*")) {
				tools.add(Text.translatable("item." + GreenResurgence.ID + ".equipments.tool_any"));
				break;
			} else if(sp.length > 1 && sp[1].equals("*")) {
				tools.add(Text.translatable("item." + GreenResurgence.ID + ".equipments." + sp[0] + "_any"));
			} else if(sp.length > 1)
				tools.add(Text.translatable("item." + GreenResurgence.ID + ".equipments." + sp[0] + "_" + sp[1]));
		}
		if(!tools.isEmpty()) {
			var t = Text.empty();
			for(int i = 0; i < tools.size(); i++) {
				t = t.append(tools.get(i));
				if(i < tools.size() - 1)
					t = t.formatted(Formatting.LIGHT_PURPLE).append(Text.literal(", ").formatted(Formatting.GRAY));
			}

			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".upgrade.applied.to").formatted(Formatting.GRAY).append(t));
		}
		for(AdvEquipmentSlot value : AdvEquipmentSlot.values()) {
			List<Text> subList = new ArrayList<>();
			lvls.forEach((k, v) -> {
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
		if(this.maxDurability() > 0) {
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.max_durability", this.maxDurability()).formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("equipment." + GreenResurgence.ID + ".tooltip.damage_weight", this.damageWeight()).formatted(Formatting.GRAY));
		}

	}

	@Override
	public boolean isDamageable() {
		return durability > -1;
	}

	public int maxDurability() {
		return durability;
	}

	public EquipmentUpgradeItem(OwoItemSettings settings, String allowed, String slot, int durability, float wheight, String... effects) {
		super(settings);
		this.allowed = allowed.split(",");
		this.slot = slot;
		this.durability = durability;
		this.weight = wheight;
		this.effects = Arrays.asList(effects);
	}

	@Override
	public Map<String, EffectLevel> getEffectsLevels() {
		var map = new HashMap<String, EffectLevel>();
		for(String effect : effects) {
			map.put(effect, new EffectLevel(1));
		}
		return map;
	}

	@Override
	public boolean canBeApplied(IEquipmentDef equipment, ItemStack stack) {
		for(String s : allowed) {
			var sp = s.split("/");
			if(sp[0].equals("*") || sp[0].equals(equipment.getEquipmentType())) {
				if(sp[1].equals("*") || sp[1].equals(equipment.getEquipmentSubtype())) return true;
			}
		}
		return false;
	}

	@Override
	public float damageWeight() {
		return this.weight;
	}

	@Override
	public String slot(IEquipmentDef equipment) {
		return slot;
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		return stack.getDamage() > 0;
	}

	@Override
	public int getItemBarStep(ItemStack stack) {
		return Math.round(13.0F - (float) stack.getDamage() * 13.0F / (float) this.durability);
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		float f = Math.max(0.0F, ((float) this.durability - (float) stack.getDamage()) / (float) this.durability);
		return MathHelper.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}

}
