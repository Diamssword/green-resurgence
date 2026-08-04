package com.diamssword.greenresurgence.items;


import com.diamssword.greenresurgence.systems.Components;
import com.diamssword.greenresurgence.systems.faction.perimeter.components.FactionGuild;
import com.diamssword.greenresurgence.systems.multiblock.DeployingMachineInstance;
import com.diamssword.greenresurgence.systems.multiblock.DeployingMachines;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;


public class ClaimBlockPlacerItem extends MutliBlockDeployerPlacerItem {

	public ClaimBlockPlacerItem(Settings properties) {
		super(properties);
	}

	@Override
	public String getMachineId(World world, BlockPos at, @Nullable PlayerEntity playerEntity) {
		return DeployingMachines.GENERATOR_T1;
	}


	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.of("Permet de creer un campement autour de l'emplacement du générateur"));
	}


	@Override
	public boolean canPlace(DeployingMachineInstance instance, World world, PlayerEntity player, BlockPos at, Direction dir) {
		var guilds = world.getComponent(Components.BASE_LIST);
		if(!guilds.doBoxIntersectWithOtherFaction(player.getUuid(), new BlockBox(at).expand(4))) {
			var currg = guilds.getForPlayer(player.getUuid(), false);
			if(currg.isEmpty()) {
				return true;
			} else if(currg.get().getTerrainAt(at).isEmpty())
				return true;
			else
				player.sendMessage(Text.literal("Cette zone est déja protégée par un de vos générateur"));

		} else
			player.sendMessage(Text.literal("Un autre générateur interfére avec cette zone"));
		return false;
	}

	@Override
	public void onPlace(DeployingMachineInstance instance, World world, PlayerEntity player, BlockPos at, Direction dir) {
		var guilds = world.getComponent(Components.BASE_LIST);
		var currg = guilds.getForPlayer(player.getUuid(), false);
		if(currg.isEmpty()) {
			currg = Optional.of(FactionGuild.createForPlayer(player, at, 4));
			guilds.addGuild(currg.get());
		}
		if(currg.get().getTerrainAt(at).isEmpty()) {
			currg.get().addZone(at, 4, world);
			instance.getExtraDatas().putUuid("faction", currg.get().getId());
		}
	}


}