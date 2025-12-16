package com.diamssword.greenresurgence.items;


import com.diamssword.greenresurgence.structure.StructureInfos;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;


public class StructurePlacerItem extends Item implements IStructureProvider {
	private final Identifier structureName;
	private final boolean centered;

	public StructurePlacerItem(Settings properties, Identifier structurename, boolean centered) {
		super(properties);
		this.centered = centered;
		this.structureName = structurename;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if(stack.hasNbt()) {
			if(stack.getNbt().contains("pos")) {
				BlockPos p = BlockPos.fromLong(stack.getNbt().getLong("pos"));
				tooltip.add(Text.of(p.getX() + " " + p.getY() + " " + p.getZ()));
			}
			if(stack.getNbt().contains("dir")) {

				tooltip.add(Text.of(Direction.byId(stack.getNbt().getInt("dir")).toString()));
			}
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if(context.getWorld().isClient || context.getPlayer() == null) return ActionResult.PASS;
		//BlockPos pos1 = context.getBlockPos().add(0, 1, 0);
		if(structureName == null) return ActionResult.CONSUME;

		NbtCompound tag = context.getStack().getOrCreateNbt();
		if(tag.contains("pos")) {
			BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));
			var dir = Direction.byId(tag.getInt("dir"));
			if(pos.equals(context.getBlockPos().offset(context.getSide()))) {
				boolean res = loadStructure((ServerWorld) context.getWorld(), structureName, pos, dir, context.getPlayer().isSneaking());
				tag.remove("pos");
				tag.remove("dir");
				if(!res) {
					context.getPlayer().sendMessage(Text.of("Impossible de charger " + this.structureName), true);
				}
				return res ? ActionResult.SUCCESS : ActionResult.FAIL;
			} else {
				tag.putLong("pos", context.getBlockPos().offset(context.getSide()).asLong());
				tag.putInt("dir", context.getHorizontalPlayerFacing().getId());
				return ActionResult.SUCCESS;
			}
		} else {
			tag.putLong("pos", context.getBlockPos().offset(context.getSide()).asLong());
			tag.putInt("dir", context.getHorizontalPlayerFacing().getId());
			return ActionResult.SUCCESS;
		}

	}

	public boolean loadStructure(ServerWorld serverLevel, Identifier structureName, BlockPos blockPos, Direction facing, boolean mirror) {
		if(structureName != null) {
			StructureTemplateManager structureManager = serverLevel.getStructureTemplateManager();
			Optional<StructureTemplate> structure2;
			structure2 = structureManager.getTemplate(structureName);
			return structure2.filter(structureTemplate -> this.place(serverLevel, structureTemplate, blockPos, facing, mirror)).isPresent();
		} else {return false;}
	}

	public boolean place(ServerWorld serverLevel, StructureTemplate structure, BlockPos blockPos, Direction facing, boolean mirror) {
		StructurePlacementData structurePlacementData = new StructurePlacementData().setMirror(BlockMirror.values()[mirror ? 1 : 0]).setRotation(StructureInfos.getRotation(facing));
		int[] off = StructureInfos.getOffsetSide(facing, this.centered);
		BlockPos p1 = blockPos.add(off[0] * (structure.getSize().getX() / 2), 0, off[1] * (structure.getSize().getZ() / 2));
		structure.place(serverLevel, p1, p1, structurePlacementData, serverLevel.getRandom(), 2);
		return true;

	}

	@Override
	public BlockPos getPosition(ItemStack stack, World w) {
		NbtCompound tag = stack.getOrCreateNbt();
		if(tag.contains("pos")) {
			return BlockPos.fromLong(tag.getLong("pos"));
		}
		return null;
	}

	@Override
	public Direction getDirection(ItemStack stack, World w) {
		NbtCompound tag = stack.getOrCreateNbt();
		if(tag.contains("dir")) {
			return Direction.byId(tag.getInt("dir"));
		}
		return Direction.NORTH;
	}

	@Override
	public Identifier getStructureName(ItemStack stack, World w) {
		return this.structureName;
	}

	@Override
	public StructureType strutctureType(ItemStack stack, World w) {
		return this.centered ? StructureType.centered : StructureType.normal;
	}


}