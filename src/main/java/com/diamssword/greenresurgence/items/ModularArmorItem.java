package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.systems.equipement.*;
import com.google.common.collect.Multimap;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModularArmorItem extends Item implements Equipment, FabricItem, GeoItem, IEquipementItem {
	public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
	public static final RawAnimation FULL = RawAnimation.begin().thenLoop("full");
	public static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
		protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
			return ArmorItem.dispenseArmor(pointer, stack) ? stack : super.dispenseSilently(pointer, stack);
		}
	};
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
	public static Supplier<Object> ProviderFunction;

	protected final ArmorItem.Type type;
	public final String category;
	public final String subCategory;
	protected Map<String, EffectLevel> baseUpgrades = new HashMap<>();
	private static final BiConsumer<Item, ItemGroup.Entries> generator = (i, e) -> {

		var st = i.getDefaultStack();
		var skin = EquipmentSkins.getDefault(i);
		skin.ifPresent(s -> st.getOrCreateNbt().putString("skin", s));
		e.add(st);
	};

	public ModularArmorItem(String category, String subCategory, ArmorItem.Type type) {
		super(new OwoItemSettings().maxCount(1).group(MItems.GROUP).tab(1).stackGenerator(generator));
		this.type = type;
		this.category = category;
		this.subCategory = subCategory;
		DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
	}

	public ModularArmorItem(String category, String subCategory, ArmorItem.Type type, Map<String, EffectLevel> baseEffects) {
		super(new OwoItemSettings().maxCount(1).group(MItems.GROUP).tab(1).stackGenerator(generator));
		;
		this.type = type;
		this.category = category;
		this.subCategory = subCategory;
		this.baseUpgrades = baseEffects;
		DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
	}


	/*	public ModularArmorItem(ArmorMaterial material, ArmorItem.Type type, Item.Settings settings) {
			super(settings.maxDamageIfAbsent(material.getDurability(type)));
			this.material = material;
			this.type = type;
			this.protection = material.getProtection(type);
			this.toughness = material.getToughness();
			this.knockbackResistance = material.getKnockbackResistance();
			DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
			ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
			UUID uUID = (UUID) MODIFIERS.get(type);
			builder.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uUID, "Armor modifier", (double) this.protection, EntityAttributeModifier.Operation.ADDITION));
			builder.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(uUID, "Armor toughness", (double) this.toughness, EntityAttributeModifier.Operation.ADDITION));
			if(material == ArmorMaterials.NETHERITE) {
				builder.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(uUID, "Armor knockback resistance", (double) this.knockbackResistance, EntityAttributeModifier.Operation.ADDITION));
			}

			this.attributeModifiers = builder.build();
		}
	*/
	public ArmorItem.Type getType() {
		return this.type;
	}

	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return false;//this.material.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return this.equipAndSwap(this, world, user, hand);
	}

	public EquipmentSlot getSlotType() {
		return this.type.getEquipmentSlot();
	}

	public SoundEvent getEquipSound() {
		return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
		return getEquipmentStack(stack).getAttributeModifiers(AdvEquipmentSlot.fromVanilla(slot), null);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		getEquipmentStack(stack).appendTooltip(tooltip);

		if(context.isAdvanced()) {
			var pair = getEquipmentStack(stack).getDurabilityAndMax();
			tooltip.add(Text.translatable("item.durability", pair.getRight() - pair.getLeft(), pair.getRight()));

		}
	}

	public Map<String, EffectLevel> getBaseUpgrades() {
		return baseUpgrades;
	}


	@Override
	public IUpgradableEquipment createEquipmentInstance(ItemStack stack) {
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

	@Override
	public void createRenderer(Consumer<Object> consumer) {
		consumer.accept(ProviderFunction.get());
	}

	@Override
	public Supplier<Object> getRenderProvider() {
		return this.renderProvider;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, 20, state -> {
			// Apply our generic idle animation.
			// Whether it plays or not is decided down below.
			state.getController().setAnimation(IDLE);

			// Let's gather some data from the state to use below
			// This is the entity that is currently wearing/holding the item
			Entity entity = state.getData(DataTickets.ENTITY);
			if(entity == null)
				return PlayState.STOP;
			// We'll just have ArmorStands always animate, so we can return here
			if(entity instanceof ArmorStandEntity)
				return PlayState.CONTINUE;

			var count = 0;
			for(ItemStack stack : entity.getArmorItems()) {
				// We can stop immediately if any of the slots are empty
				if(!stack.isEmpty() && (stack.getItem() instanceof ModularArmorItem))
					count++;
			}
			if(count > 3)
				state.getController().setAnimation(FULL);

			// Play the animation if the full set is being worn, otherwise stop
			return PlayState.CONTINUE;
		}));
	}

	public void onWearerDamaged(ItemStack stack, LivingEntity owner, DamageSource source, float amount) {
		var equipment = getEquipmentStack(stack);
		var sl = AdvEquipmentSlot.fromVanilla(this.getSlotType());
		if(owner instanceof PlayerEntity pl)
			equipment.onInteraction(pl, sl, IEquipmentUpgrade.InteractType.ATTACKED, source.getAttacker() != null ? new EntityHitResult(source.getAttacker()) : null);
		var broken = equipment.onToolDamage(owner, sl, amount);
		if(broken) {
			owner.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
			Item item = stack.getItem();
			stack.decrement(1);
			if(owner instanceof PlayerEntity) {
				((PlayerEntity) owner).incrementStat(Stats.BROKEN.getOrCreateStat(item));
			}
			stack.setDamage(0);
		} else
			equipment.save();
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack stack = new ItemStack(this);
		stack.addHideFlag(ItemStack.TooltipSection.MODIFIERS);
		return stack;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

}
