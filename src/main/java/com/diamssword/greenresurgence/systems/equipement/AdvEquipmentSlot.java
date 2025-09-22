package com.diamssword.greenresurgence.systems.equipement;

import net.minecraft.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

/**
 * Provides enum types for several key slots found within an entity {@link net.minecraft.inventory.Inventory}.
 * <p>
 * Each equipment slot has a type, which represents what inventory category it is contained within.
 * The {@code HAND} category covers the mainhand and offhand slots, while the {@code ARMOR} category covers the 4
 * types of armor slots found in {@link net.minecraft.entity.LivingEntity}.
 * <p>
 * Each equipment slot contains information on where that slot should be located within a parent {@link net.minecraft.inventory.Inventory}.
 * {@link #getEntitySlotId()} will provide the base slot index a slot should occupy (starting from {@code 0}),
 * while {@link #getOffsetEntitySlotId(int)} will return the same value added to an offset index.
 * <p>
 * An equipment slot can be used to quickly access the item held by an inventory slot in a {@link LivingEntity} through
 * methods such as {@link LivingEntity#getEquippedStack(EquipmentSlot)}, which will return the {@link net.minecraft.item.ItemStack}
 * held in the entity's inventory slot pointed at by the target slot.
 */
public enum AdvEquipmentSlot {
	MAINHAND(AdvEquipmentSlot.Type.HAND, EquipmentSlot.MAINHAND),
	OFFHAND(AdvEquipmentSlot.Type.HAND, EquipmentSlot.OFFHAND),
	FEET(AdvEquipmentSlot.Type.ARMOR, EquipmentSlot.FEET),
	LEGS(AdvEquipmentSlot.Type.ARMOR, EquipmentSlot.LEGS),
	CHEST(AdvEquipmentSlot.Type.ARMOR, EquipmentSlot.CHEST),
	HEAD(AdvEquipmentSlot.Type.ARMOR, EquipmentSlot.HEAD),
	BACKPACK(Type.BACKPACK, 0, "backpack"),
	SATCHEL_LEFT(Type.SATCHEL, 0, "satchel_left"),
	SATCHEL_RIGHT(Type.SATCHEL, 1, "satchel_right");


	private final AdvEquipmentSlot.Type type;
	private final EquipmentSlot parent;
	private final int entityId;
	private final String name;

	AdvEquipmentSlot(AdvEquipmentSlot.Type type, int entityId, String name) {
		this.type = type;
		this.entityId = entityId;
		this.name = name;
		this.parent = null;
	}

	AdvEquipmentSlot(AdvEquipmentSlot.Type type, EquipmentSlot parent) {
		this.type = type;
		this.entityId = parent.getEntitySlotId();
		this.name = parent.getName();
		this.parent = parent;
	}

	/**
	 *
	 * @return the vanilla equivalent if it exist
	 */
	@Nullable
	public EquipmentSlot getParent() {
		return this.parent;
	}

	/**
	 * {@return the target {@link EquipmentSlot.Type} that this slot targets}
	 *
	 * <p>
	 * An equipment slot either targets the hand or body type, which can be used to determine whether a request
	 * to manipulate slot data on an entity should be applied to an armor inventory or general item inventory.
	 */
	public AdvEquipmentSlot.Type getType() {
		return this.type;
	}

	/**
	 * {@return the index of the inventory slot this slot should occupy}
	 *
	 * <p>
	 * In the case of {@link #MAINHAND} and {@link #OFFHAND}, this method will return 0 and 1, respectively.
	 * The remaining armor slots re-start at index 0 and end at index 3.
	 *
	 * <p>
	 * To calculate the target index of an inventory slot for a slot relative to the offset index of an entire
	 * inventory, visit {@link #getOffsetEntitySlotId(int)}.
	 */
	public int getEntitySlotId() {
		return this.entityId;
	}

	/**
	 * {@return the index of the inventory slot this slot  should occupy, plus the passed in {@code offset} amount}
	 */
	public int getOffsetEntitySlotId(int offset) {
		return offset + this.entityId;
	}


	/**
	 * {@return the unique name of this equipment slot}
	 *
	 * <p>The returned value will be a lower-case string (such as "chest" for {@link #CHEST}).
	 */
	public String getName() {
		return this.name;
	}

	public boolean isArmorSlot() {
		return this.type == AdvEquipmentSlot.Type.ARMOR;
	}

	/**
	 * {@return the slot where {@linkplain #getName the name} is equal to {@code name}}
	 * If no slot matching the input name is found, this throws {@link IllegalArgumentException}.
	 *
	 * @throws IllegalArgumentException if no slot type could be found matching {@code name}
	 */
	public static AdvEquipmentSlot byName(String name) {
		for(AdvEquipmentSlot equipmentSlot : values()) {
			if(equipmentSlot.getName().equals(name)) {
				return equipmentSlot;
			}
		}

		throw new IllegalArgumentException("Invalid slot '" + name + "'");
	}

	public static AdvEquipmentSlot fromVanilla(EquipmentSlot slot) {
		for(AdvEquipmentSlot equipmentSlot : values()) {
			if(equipmentSlot.getParent() == slot) {
				return equipmentSlot;
			}
		}
		return null;
	}

	/**
	 * {@return the equipment slot where {@linkplain #getEntitySlotId() the slot ID} is equal to {@code index} and the type of the slot is equal to {@code type}}
	 * If no slot could be found matching the input {@code type} and {@code index}, throws {@link IllegalArgumentException}.
	 *
	 * @throws IllegalArgumentException if no slot type could be found matching {@code type} and {@code index}
	 */
	public static AdvEquipmentSlot fromTypeIndex(AdvEquipmentSlot.Type type, int index) {
		for(AdvEquipmentSlot equipmentSlot : values()) {
			if(equipmentSlot.getType() == type && equipmentSlot.getEntitySlotId() == index) {
				return equipmentSlot;
			}
		}

		throw new IllegalArgumentException("Invalid slot '" + type + "': " + index);
	}

	/**
	 * The type of body item slot an {@link EquipmentSlot} targets.
	 */
	public enum Type {
		HAND,
		ARMOR,
		BACKPACK,
		SATCHEL
	}
}
