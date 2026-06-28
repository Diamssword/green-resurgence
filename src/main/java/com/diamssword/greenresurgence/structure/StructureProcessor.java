package com.diamssword.greenresurgence.structure;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Property;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StructureProcessor {
	public static final Processor INSTANCE = new Processor();
	public static final Codec<StructureProcessor.Processor> PROCESSOR_CODEC = Codec.unit(
			() -> StructureProcessor.INSTANCE
	);

	public static final StructureProcessorType<StructureProcessor.Processor> PROCESSOR_TYPE = Registry.register(
			Registries.STRUCTURE_PROCESSOR,
			GreenResurgence.asRessource("structure_processor"),
			() -> PROCESSOR_CODEC
	);


	public static void init() {
	}


	public static class Processor extends net.minecraft.structure.processor.StructureProcessor {
		@Nullable
		@Override
		public StructureTemplate.StructureBlockInfo process(
				@NotNull WorldView level, @NotNull BlockPos pos, @NotNull BlockPos pivot,
				@NotNull StructureTemplate.StructureBlockInfo blockInfo, @NotNull StructureTemplate.StructureBlockInfo relativeBlockInfo,
				@NotNull StructurePlacementData settings) {
			if(MBlocks.SPAWNER == relativeBlockInfo.state().getBlock()) {
				relativeBlockInfo.nbt().remove("locked");
			}
			if(MBlocks.CRUMBELING_BLOCK == relativeBlockInfo.state().getBlock()) {
				if(relativeBlockInfo.nbt().contains("block")) {
					var state = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), relativeBlockInfo.nbt().getCompound("block"));
					state.getProperties().forEach(p -> {
						if(p.getType() == Direction.class) {
							Direction dir = (Direction) state.get(p);
							var state1 = state.with((Property<Direction>) p, settings.getRotation().rotate(dir));
							relativeBlockInfo.nbt().put("block", NbtHelper.fromBlockState(state1));
						} else if(p.getType() == Direction.Axis.class) {
							Direction.Axis dir = (Direction.Axis) (Object) state.get(p);
							if(dir.isHorizontal() && settings.getRotation() == BlockRotation.CLOCKWISE_90 || settings.getRotation() == BlockRotation.COUNTERCLOCKWISE_90) {
								dir = dir == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
								var state1 = state.with((Property<Direction.Axis>) p, dir);
								relativeBlockInfo.nbt().put("block", NbtHelper.fromBlockState(state1));
							}
						}
					});
				}
			}

			/*if(predicate == null) // do nothing
				return relativeBlockInfo;

			NbtCompound nbt = relativeBlockInfo.nbt();
			if(nbt == null)
				return relativeBlockInfo;

			if(!predicate.shouldApplyProcess(structureId, FabricStructureProcessing.Process.FLUID_AMOUNTS))
				return relativeBlockInfo;

			if(AllBlocks.FLUID_TANK.has(relativeBlockInfo.state()) && nbt.contains("TankContent", NbtElement.COMPOUND_TYPE)) {
				NbtCompound copy = nbt.copy();
				fixTankContent(copy.getCompound("TankContent"));
				return new StructureTemplate.StructureBlockInfo(relativeBlockInfo.pos(), relativeBlockInfo.state(), copy);
			} else if(AllBlocks.BASIN.has(relativeBlockInfo.state())) {
				NbtCompound copy = nbt.copy();
				NbtList inputTanks = copy.getList("InputTanks", NbtElement.COMPOUND_TYPE);
				if(!inputTanks.isEmpty()) {
					for(int i = 0; i < inputTanks.size(); i++) {
						NbtCompound compound = inputTanks.getCompound(i);
						NbtCompound content = compound.getCompound("TankContent");
						fixTankContent(content);
					}
				}
				NbtList outputTanks = copy.getList("OutputTanks", NbtElement.COMPOUND_TYPE);
				if(!outputTanks.isEmpty()) {
					for(int i = 0; i < outputTanks.size(); i++) {
						NbtCompound compound = outputTanks.getCompound(i);
						NbtCompound content = compound.getCompound("TankContent");
						fixTankContent(content);
					}
				}

				return new StructureTemplate.StructureBlockInfo(relativeBlockInfo.pos(), relativeBlockInfo.state(), copy);
			}

			*/
			// no processes were applied
			return relativeBlockInfo;
		}

		@Override
		@NotNull
		protected StructureProcessorType<?> getType() {
			return PROCESSOR_TYPE;
		}
	}

}
