package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.items.StackBasedGeckoItem;
import com.diamssword.greenresurgence.systems.equipement.*;
import com.google.common.collect.Multimap;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class EquipmentTool extends StackBasedGeckoItem implements FabricItem, IEquipementItem {

	public final String category;
	public final String subCategory;
	private static final BiConsumer<Item, ItemGroup.Entries> generator = (i, e) -> {

		var st = i.getDefaultStack();
		var skin = EquipmentSkins.getDefault(i);
		skin.ifPresent(s -> st.getOrCreateNbt().putString("skin", s));
		e.add(st);
	};

	public EquipmentTool(String category, String subCategory) {
		super(new OwoItemSettings().maxCount(1).group(MItems.GROUP).tab(1).stackGenerator(generator));
		this.category = category;
		this.subCategory = subCategory;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		var eslot = AdvEquipmentSlot.UNKNOWN;
		if(entity instanceof LivingEntity pl) {
			if(pl.getMainHandStack() == stack)
				eslot = AdvEquipmentSlot.MAINHAND;
			else if(pl.getOffHandStack() == stack)
				eslot = AdvEquipmentSlot.OFFHAND;

		}
		this.getEquipment(stack).onTick(entity, eslot);

	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack stack = new ItemStack(this);
		stack.addHideFlag(ItemStack.TooltipSection.MODIFIERS);
		return stack;
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
		return getEquipmentStack(stack).getAttributeModifiers(AdvEquipmentSlot.fromVanilla(slot), null);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		getEquipmentStack(stack).appendTooltip(tooltip);
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		var equipment = getEquipmentStack(stack);
		if(attacker instanceof PlayerEntity pl)
			equipment.onInteraction(pl, AdvEquipmentSlot.MAINHAND, IEquipmentUpgrade.InteractType.ATTACK, new EntityHitResult(target));
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
		var equipment = getEquipmentStack(stack);
		if(miner instanceof PlayerEntity pl)
			equipment.onInteraction(pl, AdvEquipmentSlot.MAINHAND, IEquipmentUpgrade.InteractType.INTERACT, new BlockHitResult(pos.toCenterPos(), Direction.UP, pos, true));
		var broken = equipment.onToolDamage(miner, AdvEquipmentSlot.MAINHAND);
		if(broken) {
			miner.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
			Item item = stack.getItem();
			stack.decrement(1);
			if(miner instanceof PlayerEntity) {
				((PlayerEntity) miner).incrementStat(Stats.BROKEN.getOrCreateStat(item));
			}
			stack.setDamage(0);
		} else
			equipment.save();

		return true;
	}

	public abstract Map<String, EffectLevel> getBaseUpgrades();

	@Override
	public IUpgradableEquipment getEquipment(ItemStack stack) {
		return new StackBasedEquipment(category, subCategory, stack, getBaseUpgrades());
	}

	public StackBasedEquipment getEquipmentStack(ItemStack stack) {
		return (StackBasedEquipment) getEquipment(stack);
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
