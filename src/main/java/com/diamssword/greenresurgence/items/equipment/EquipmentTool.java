package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.items.StackBasedGeckoItem;
import com.diamssword.greenresurgence.systems.equipement.AdvEquipmentSlot;
import com.diamssword.greenresurgence.systems.equipement.IEquipementItem;
import com.diamssword.greenresurgence.systems.equipement.IUpgradableEquipment;
import com.diamssword.greenresurgence.systems.equipement.StackBasedEquipment;
import com.google.common.collect.Multimap;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EquipmentTool extends StackBasedGeckoItem implements FabricItem, IEquipementItem {

	public final String category;
	public final String subCategory;

	public EquipmentTool(String category, String subCategory) {
		super(new OwoItemSettings().maxCount(1).group(MItems.GROUP).tab(1), false);
		this.category = category;
		this.subCategory = subCategory;
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
		return getEquipmentStack(stack).getAttributeModifiers(AdvEquipmentSlot.fromVanilla(slot), null);
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		var equipment = getEquipmentStack(stack);
		var broken = equipment.onToolDamage(attacker, AdvEquipmentSlot.MAINHAND);
		if(broken) {
			attacker.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
			Item item = stack.getItem();
			stack.decrement(1);
			if(attacker instanceof PlayerEntity) {
				((PlayerEntity) attacker).incrementStat(Stats.BROKEN.getOrCreateStat(item));
			}
			stack.setDamage(0);
		} else
			equipment.save();

		return true;
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		//if needed for tools
		return true;
	}

	/*
		@Override
		public Text getName(ItemStack stack) {
			String skin = "Empty";
			if(stack.hasNbt()) {
				skin = getEquipment(stack).getSkin();
			}
			return Text.translatable(this.getTranslationKey(stack), skin);
		}
	*/
	@Override
	public IUpgradableEquipment getEquipment(ItemStack stack) {
		return new StackBasedEquipment(category, subCategory, stack);
	}

	public StackBasedEquipment getEquipmentStack(ItemStack stack) {
		return new StackBasedEquipment(category, subCategory, stack);
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getItemBarStep(ItemStack stack) {
		return Math.round(getDurabilityProgress(stack) * 13f);
	}

	public float getDurabilityProgress(ItemStack stack) {
		return getEquipmentStack(stack).getDurabilityProgress();
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		return MathHelper.hsvToRgb(getDurabilityProgress(stack) / 3.0F, 1.0F, 1.0F);
	}

}
