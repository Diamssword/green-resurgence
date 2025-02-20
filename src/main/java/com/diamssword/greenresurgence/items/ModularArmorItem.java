package com.diamssword.greenresurgence.items;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModularArmorItem extends Item implements Equipment, GeoItem {
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    public static final RawAnimation FULL = RawAnimation.begin().thenLoop("full");
    private static final EnumMap<ArmorItem.Type,UUID> MODIFIERS = Util.make(new EnumMap<>(ArmorItem.Type.class), (uuidMap) -> {
        uuidMap.put(ArmorItem.Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
        uuidMap.put(ArmorItem.Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
        uuidMap.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
        uuidMap.put(ArmorItem.Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
    });
    public static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
        protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            return ArmorItem.dispenseArmor(pointer, stack) ? stack : super.dispenseSilently(pointer, stack);
        }
    };
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
    public static Supplier<Object> ProviderFunction;


    protected final ArmorItem.Type type;
    private final int protection;
    private final float toughness;
    protected final float knockbackResistance;
    protected final ArmorMaterial material;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;


    public ModularArmorItem(ArmorMaterial material, ArmorItem.Type type, Item.Settings settings) {
        super(settings.maxDamageIfAbsent(material.getDurability(type)));
        this.material = material;
        this.type = type;
        this.protection = material.getProtection(type);
        this.toughness = material.getToughness();
        this.knockbackResistance = material.getKnockbackResistance();
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        UUID uUID = (UUID)MODIFIERS.get(type);
        builder.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uUID, "Armor modifier", (double)this.protection, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(uUID, "Armor toughness", (double)this.toughness, EntityAttributeModifier.Operation.ADDITION));
        if (material == ArmorMaterials.NETHERITE) {
            builder.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(uUID, "Armor knockback resistance", (double)this.knockbackResistance, EntityAttributeModifier.Operation.ADDITION));
        }

        this.attributeModifiers = builder.build();
    }

    public ArmorItem.Type getType() {
        return this.type;
    }

    public int getEnchantability() {
        return this.material.getEnchantability();
    }

    public ArmorMaterial getMaterial() {
        return this.material;
    }

    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return this.material.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return this.equipAndSwap(this, world, user, hand);
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == this.type.getEquipmentSlot() ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    public int getProtection() {
        return this.protection;
    }

    public float getToughness() {
        return this.toughness;
    }

    public EquipmentSlot getSlotType() {
        return this.type.getEquipmentSlot();
    }

    public SoundEvent getEquipSound() {
        return this.getMaterial().getEquipSound();
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
            if(entity ==null)
                return PlayState.STOP;
            // We'll just have ArmorStands always animate, so we can return here
            if (entity instanceof ArmorStandEntity)
                return PlayState.CONTINUE;

            var count=0;
            for (ItemStack stack : entity.getArmorItems()) {
                // We can stop immediately if any of the slots are empty
                if (!stack.isEmpty() && (stack.getItem() instanceof ModularArmorItem))
                    count++;
            }
            if(count>3)
                state.getController().setAnimation(FULL);

            // Play the animation if the full set is being worn, otherwise stop
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
