package com.diamssword.greenresurgence.items;


import com.diamssword.greenresurgence.MBlocks;
import com.diamssword.greenresurgence.blockEntities.DeployableMachineBlockEntity;
import com.diamssword.greenresurgence.systems.multiblock.DeployingMachineInstance;
import com.diamssword.greenresurgence.systems.multiblock.DeployingMachines;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public abstract class MutliBlockDeployerPlacerItem extends Item {
	public MutliBlockDeployerPlacerItem(Settings properties) {
		super(properties);
	}

	public abstract String getMachineId(World world, BlockPos at, @Nullable PlayerEntity playerEntity);

	public Optional<DeployingMachineInstance> getMachineInstance(World world, BlockPos at, Direction dir, @Nullable PlayerEntity playerEntity) {
		return DeployingMachines.instantiate(getMachineId(world, at, playerEntity), at, dir);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		BlockHitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.ANY);
		if(hitResult.getType() == HitResult.Type.MISS) {
			return TypedActionResult.pass(itemStack);
		} else {
			if(hitResult.getType() == HitResult.Type.BLOCK) {
				useOnBlock(new ItemUsageContext(user, hand, hitResult));
			} else {
				return TypedActionResult.pass(itemStack);
			}
		}
		return TypedActionResult.pass(itemStack);
	}

	public abstract boolean canPlace(DeployingMachineInstance instance, World world, PlayerEntity player, BlockPos at, Direction dir);

	public abstract void onPlace(DeployingMachineInstance instance, World world, PlayerEntity player, BlockPos at, Direction dir);

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		var pos = context.getBlockPos().offset(context.getSide());

		if(isBlockReplacable(context.getWorld(), context.getBlockPos()))
			pos = context.getBlockPos();
		var dir = context.getHorizontalPlayerFacing().getOpposite();
		var machine = getMachineInstance(context.getWorld(), pos, dir, context.getPlayer());
		if(machine.isPresent() && !context.getWorld().isClient && canPlace(machine.get(), context.getWorld(), context.getPlayer(), context.getBlockPos(), dir)) {
			if(machine.get().canPlace(context.getWorld())) {

				context.getWorld().setBlockState(pos, MBlocks.DEPLOYABLE_MACHINE_BLOCK.getDefaultState());
				var ent = context.getWorld().getBlockEntity(pos);
				if(ent instanceof DeployableMachineBlockEntity be) {
					onPlace(machine.get(), context.getWorld(), context.getPlayer(), pos, dir);
					be.setMachine(machine.get());
				}
				context.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5f, 0.2f + context.getWorld().random.nextFloat() * 0.4f);
				context.getStack().decrement(1);
				return ActionResult.CONSUME;
			} else
				context.getPlayer().sendMessage(Text.translatable("tooltip.green_resurgence.multiblock_deployer.no_space"));
		}

		return ActionResult.FAIL;
	}

	private boolean isBlockReplacable(WorldAccess world, BlockPos pos) {
		return world.getBlockState(pos).isReplaceable();
	}

}