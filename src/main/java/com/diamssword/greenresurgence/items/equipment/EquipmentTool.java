package com.diamssword.greenresurgence.items.equipment;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.entities.ThrownWeaponEntity;
import com.diamssword.greenresurgence.items.StackBasedGeckoItem;
import com.diamssword.greenresurgence.items.equipment.upgrades.EquipmentHidenSlotUpgrade;
import com.diamssword.greenresurgence.systems.equipement.*;
import com.diamssword.greenresurgence.systems.equipement.effects.ThrowableEffectUpgrade;
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
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class EquipmentTool extends StackBasedGeckoItem implements FabricItem, IEquipementItem {

	public final String category;
	public final String subCategory;
	protected Map<String, EffectLevel> baseUpgrades = new HashMap<>();
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

	public EquipmentTool(String category, String subCategory, Map<String, EffectLevel> baseEffects) {
		super(new OwoItemSettings().maxCount(1).group(MItems.GROUP).tab(1).stackGenerator(generator));
		this.category = category;
		this.subCategory = subCategory;
		this.baseUpgrades = baseEffects;
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return !miner.isCreative();
	}

	private float getEffectLevel(ItemStack stack, String effect) {
		var lvl = getEquipmentStack(stack).getEffects().get(effect);
		if(lvl != null)
			return lvl.getLevel();
		return 0;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		if(getEffectLevel(stack, EquipmentEffects.THROWABLE) > 0)
			return UseAction.SPEAR;
		return super.getUseAction(stack);
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return getEffectLevel(stack, EquipmentEffects.THROWABLE) > 0 ? 72000 : super.getMaxUseTime(stack);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		var effl = getEquipmentStack(stack).getEffects().get(EquipmentEffects.THROWABLE);

		if(user instanceof PlayerEntity playerEntity && effl.getLevel() > 0) {
			var loy = effl.getLevel(ThrowableEffectUpgrade.MAGNETISM, 0f);
			int i = this.getMaxUseTime(stack) - remainingUseTicks;
			if(i >= 10) {
				int speedBoost = 1;
				if(!world.isClient) {
					stack.damage(1, playerEntity, p -> p.sendToolBreakStatus(user.getActiveHand()));

					ThrownWeaponEntity tridentEntity = new ThrownWeaponEntity(world, playerEntity, stack, (int) loy);
					tridentEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 2.5F + speedBoost * 0.5F, 1.0F);
					if(playerEntity.getAbilities().creativeMode) {
						tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
					}

					world.spawnEntity(tridentEntity);
					world.playSoundFromEntity(null, tridentEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
					if(!playerEntity.getAbilities().creativeMode) {
						playerEntity.getInventory().removeOne(stack);
					}
				}
				playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

		ItemStack itemStack = user.getStackInHand(hand);
		if(getEffectLevel(itemStack, EquipmentEffects.THROWABLE) == 0)
			return super.use(world, user, hand);
		else {


			user.setCurrentHand(hand);
			return TypedActionResult.consume(itemStack);
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		if(!world.isClient && entity.age % 10 == 0) {
			var eslot = AdvEquipmentSlot.UNKNOWN;
			if(entity instanceof LivingEntity pl) {
				if(pl.getMainHandStack() == stack)
					eslot = AdvEquipmentSlot.MAINHAND;
				else if(pl.getOffHandStack() == stack)
					eslot = AdvEquipmentSlot.OFFHAND;

			}
			if(eslot != AdvEquipmentSlot.UNKNOWN)
				this.getEquipment(stack).onTick(entity, eslot);
		}
	}

	@Override
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		var binding = getEquipmentStack(stack).getUpgradeItem(Equipments.P_BINDING_EXTRA);
		if(binding.getItem() instanceof EquipmentHidenSlotUpgrade ids) {
			return ids.onStackClicked(binding, slot, clickType, player);
		} else
			return super.onStackClicked(stack, slot, clickType, player);
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
			equipment.onInteraction(pl, AdvEquipmentSlot.MAINHAND, IEquipmentUpgrade.InteractType.POST_ATTACK, new EntityHitResult(target));
		var broken = equipment.onToolDamage(attacker, AdvEquipmentSlot.MAINHAND, 1f);
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
		var broken = equipment.onToolDamage(miner, AdvEquipmentSlot.MAINHAND, 1f);
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

	public Map<String, EffectLevel> getBaseUpgrades() {
		return baseUpgrades;
	}

	@Override
	public IUpgradableEquipment createEquipmentInstance(ItemStack stack) {
		return new StackBasedEquipment(category, subCategory, stack, baseUpgrades);
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
