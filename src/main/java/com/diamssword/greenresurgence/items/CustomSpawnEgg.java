package com.diamssword.greenresurgence.items;

import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.containers.*;
import com.diamssword.greenresurgence.containers.grids.GridContainer;
import com.diamssword.greenresurgence.containers.grids.IGridContainer;
import com.diamssword.greenresurgence.containers.player.grids.OffHandGrid;
import com.diamssword.greenresurgence.items.helpers.IGuiStackPacketReceiver;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class CustomSpawnEgg extends Item implements IGuiStackPacketReceiver {
	public CustomSpawnEgg() {
		super(new OwoItemSettings().group(MItems.GROUP).tab(0));
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		if(!(world instanceof ServerWorld)) {
			return ActionResult.SUCCESS;
		} else {
			ItemStack itemStack = context.getStack();
			BlockPos blockPos = context.getBlockPos();
			Direction direction = context.getSide();
			BlockState blockState = world.getBlockState(blockPos);
			if(blockState.isOf(Blocks.SPAWNER)) {
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if(blockEntity instanceof MobSpawnerBlockEntity mobSpawnerBlockEntity) {
					Optional<EntityType<?>> entityType = this.getEntityType(itemStack.getNbt());
					entityType.ifPresent(e -> {
						mobSpawnerBlockEntity.setEntityType(e, world.getRandom());
						blockEntity.markDirty();
						world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
						world.emitGameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
						itemStack.decrement(1);
					});
					return ActionResult.CONSUME;
				}
			}

			BlockPos blockPos2;
			if(blockState.getCollisionShape(world, blockPos).isEmpty()) {
				blockPos2 = blockPos;
			} else {
				blockPos2 = blockPos.offset(direction);
			}

			Optional<EntityType<?>> entityType2 = this.getEntityType(itemStack.getNbt());
			entityType2.ifPresent(e -> {
				var e1 = e.spawnFromItemStack(
						(ServerWorld) world,
						itemStack,
						context.getPlayer(),
						blockPos2,
						SpawnReason.SPAWN_EGG,
						true,
						!Objects.equals(blockPos, blockPos2) && direction == Direction.UP
				);
				if(e1 != null) {
					setEntityCustomData(e1, itemStack.getOrCreateSubNbt("EntityTag"));
					itemStack.decrement(1);
					world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
				}
			});
			return ActionResult.CONSUME;
		}
	}

	private IGridContainer[] createContainers(ServerPlayerEntity player, ItemStack stack) {
		var sub = stack.getSubNbt("EntityTag");
		var egg = new FilteredInventory(1, (t, s) -> s.isEmpty() || s.getItem() instanceof SpawnEggItem).setSingleItem(true);
		getEntityType(stack.getNbt()).ifPresent(c -> {
			var e = SpawnEggItem.forEntity(c);
			if(e != null)
				egg.setStack(0, e.getDefaultStack().copyWithCount(1));
		});
		egg.addListener(c -> {
			var st = egg.getStack(0);
			if(st.getItem() instanceof SpawnEggItem e) {
				var nbt = stack.getOrCreateSubNbt("EntityTag");
				var id = Registries.ENTITY_TYPE.getId(e.getEntityType(st.getNbt()));
				nbt.putString("id", id.toString());
				updateStack(player, stack);
			}
		});
		var equipment = new SlotedSimpleInventory(6);
		if(sub != null && sub.contains("equipment", NbtElement.LIST_TYPE)) {
			equipment.readNbtList(sub.getList("equipment", NbtElement.COMPOUND_TYPE));
		}
		equipment.addListener(l -> {
			var nbt = stack.getOrCreateSubNbt("EntityTag");
			nbt.put("equipment", equipment.toNbtList());
			updateStack(player, stack);
		});
		return new IGridContainer[]{new GridContainer("egg", egg, 1, 1), new GridContainer("armor", new OffsetInventory(equipment, 0, 4), 1, 4), new OffHandGrid("hands", new OffsetInventory(equipment, 4, 2), 2, 1)};
	}

	private void updateStack(ServerPlayerEntity player, ItemStack stack) {
		player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(0, 1, 36 + player.getInventory().selectedSlot, stack));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
		if(blockHitResult.getType() != HitResult.Type.BLOCK) {
			if(!world.isClient && user.isCreative() && hand == Hand.MAIN_HAND) {
				Containers.createHandler(user, user.getBlockPos(), (sync, inv, p1) -> new CustomSpawnEgg.ScreenHandler(sync, user, createContainers((ServerPlayerEntity) p1, itemStack)));
				return TypedActionResult.success(itemStack);
			}
			return TypedActionResult.pass(itemStack);
		} else if(!(world instanceof ServerWorld)) {
			return TypedActionResult.success(itemStack);
		} else {
			BlockPos blockPos = blockHitResult.getBlockPos();
			if(!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
				return TypedActionResult.pass(itemStack);
			} else if(world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
				Optional<EntityType<?>> entityType = this.getEntityType(itemStack.getNbt());
				if(entityType.isPresent()) {
					Entity entity = entityType.get().spawnFromItemStack((ServerWorld) world, itemStack, user, blockPos, SpawnReason.SPAWN_EGG, false, false);
					setEntityCustomData(entity, itemStack.getOrCreateSubNbt("EntityTag"));
					if(entity == null) {
						return TypedActionResult.pass(itemStack);
					} else {
						if(!user.getAbilities().creativeMode) {
							itemStack.decrement(1);
						}

						user.incrementStat(Stats.USED.getOrCreateStat(this));
						world.emitGameEvent(user, GameEvent.ENTITY_PLACE, entity.getPos());
						return TypedActionResult.consume(itemStack);
					}
				}
				return TypedActionResult.pass(itemStack);
			} else {
				return TypedActionResult.fail(itemStack);
			}
		}
	}

	public int getColor(ItemStack stack, int tintIndex) {
		if(tintIndex == 2)
			return 0xFFFFFF;
		var type = getEntityType(stack.getNbt());
		if(type.isPresent()) {
			var egg = SpawnEggItem.forEntity(type.get());
			if(egg != null)
				return egg.getColor(tintIndex);
			var name = type.get().getName().getString();
			return tintIndex == 0 ? stringToColor(name.substring(0, name.length() / 2)) : stringToColor(name.substring(name.length() / 2));
		}
		return tintIndex == 0 ? 0XFF0045 : 0X00FFFF;
	}

	public static int stringToColor(String input) {
		int hash = input.hashCode();

		int color = hash & 0xFFFFFF;

		// Ensure it's not too dark
		if(color < 0x101010) {
			color += 0x202020; // brighten very dark colors slightly
		}
		return color;
	}

	public Optional<EntityType<?>> getEntityType(@Nullable NbtCompound nbt) {
		if(nbt != null && nbt.contains("EntityTag", NbtElement.COMPOUND_TYPE)) {
			NbtCompound nbtCompound = nbt.getCompound("EntityTag");
			if(nbtCompound.contains("id", NbtElement.STRING_TYPE)) {
				return EntityType.get(nbtCompound.getString("id"));
			}
		}
		return Optional.empty();
	}

	@Override
	public Text getName(ItemStack stack) {
		var b = super.getName(stack);
		var id = getEntityType(stack.getNbt());
		if(id.isPresent())
			return Text.empty().append(b).append(" (").append(id.get().getName()).append(")");
		return b;
	}

	public void setEntityCustomData(Entity entity, NbtCompound tag) {
		if(entity instanceof LivingEntity le) {
			if(tag.contains("customHealth", NbtElement.INT_TYPE)) {
				var h = tag.getInt("customHealth");
				var he = le.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
				if(he != null)
					he.addPersistentModifier(new EntityAttributeModifier("customEggModifier", h - he.getValue(), EntityAttributeModifier.Operation.ADDITION));
				le.setHealth(le.getMaxHealth());
			}
			if(tag.contains("equipment", NbtElement.LIST_TYPE)) {
				var in = new SlotedSimpleInventory(6);
				in.readNbtList(tag.getList("equipment", NbtElement.COMPOUND_TYPE));
				if(!in.getStack(4).isEmpty())
					le.equipStack(EquipmentSlot.MAINHAND, in.getStack(4));
				if(!in.getStack(5).isEmpty())
					le.equipStack(EquipmentSlot.OFFHAND, in.getStack(5));
				if(!in.getStack(0).isEmpty())
					le.equipStack(EquipmentSlot.HEAD, in.getStack(0));
				if(!in.getStack(1).isEmpty())
					le.equipStack(EquipmentSlot.CHEST, in.getStack(1));
				if(!in.getStack(2).isEmpty())
					le.equipStack(EquipmentSlot.LEGS, in.getStack(2));
				if(!in.getStack(3).isEmpty())
					le.equipStack(EquipmentSlot.FEET, in.getStack(3));
			}
			if(tag.contains("customPotions", NbtElement.STRING_TYPE)) {
				var ptStr = tag.getString("customPotions");
				var effs = ptStr.split(";");
				for(String eff : effs) {
					var parts = eff.split(",");
					if(parts.length == 2 || parts.length == 3) {
						try {
							var id = new Identifier(parts[0]);
							var time = Integer.parseInt(parts[1]) * 20;
							var pow = 0;
							if(parts.length > 2)
								pow = Integer.parseInt(parts[2]);
							var po = Registries.STATUS_EFFECT.get(id);
							if(po != null)
								le.addStatusEffect(new StatusEffectInstance(po, time, pow));
						} catch(InvalidIdentifierException | NumberFormatException ignored) {}
					}
				}
				entity.setCustomName(Text.literal(tag.getString("customName")));
				entity.setCustomNameVisible(true);
			}
		}
		if(tag.contains("customName", NbtElement.STRING_TYPE)) {
			entity.setCustomName(Text.literal(tag.getString("customName")));
			entity.setCustomNameVisible(true);
		}
	}

	@Override
	public void receiveGuiPacket(ServerPlayerEntity player, ItemStack handStack, NbtCompound received) {
		var tag = handStack.getOrCreateSubNbt("EntityTag");
		if(received.contains("id", NbtElement.STRING_TYPE)) {
			tag.putString("id", received.getString("id"));
			updateStack(player, handStack);
		}
		if(received.contains("potions", NbtElement.STRING_TYPE)) {
			tag.putString("customPotions", received.getString("potions"));
			updateStack(player, handStack);
		}
		if(received.contains("name", NbtElement.STRING_TYPE)) {
			tag.putString("customName", received.getString("name"));
			updateStack(player, handStack);
		}
		if(received.contains("vie", NbtElement.INT_TYPE)) {
			var v = received.getInt("vie");
			if(v <= 0)
				tag.remove("customHealth");
			else
				tag.putInt("customHealth", v);
			updateStack(player, handStack);
		}
	}

	public static class ScreenHandler extends CreativeMultiInvScreenHandler {

		public ScreenHandler(int syncId, PlayerInventory playerInventory) {
			super(syncId, playerInventory);
		}

		public ScreenHandler(int syncId, PlayerEntity player, IGridContainer... inventories) {
			super(syncId, player.getInventory(), inventories);
		}

		@Override
		public ScreenHandlerType<CustomSpawnEgg.ScreenHandler> type() {
			return Containers.SPAWN_EGG;
		}
	}
}
