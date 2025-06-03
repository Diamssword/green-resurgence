package com.diamssword.greenresurgence;

import com.diamssword.greenresurgence.blockEntities.ModBlockEntity;
import io.wispforest.owo.registration.reflect.BlockEntityRegistryContainer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MBlockEntities implements BlockEntityRegistryContainer {

	public static final Map<Class<? extends BlockEntity>, List<ModBlockEntity<?>>> toRegisterBlocks = new HashMap<>();

	public static void addToRegister(ModBlockEntity<?> be) {
		var clazz = be.getBlockEntityClass();
		if (!toRegisterBlocks.containsKey(clazz))
			toRegisterBlocks.put(clazz, new ArrayList<>());
		toRegisterBlocks.get(clazz).add(be);
	}

	public static void registerAll() {
		toRegisterBlocks.forEach((k, v) -> {

			var blocks = v.toArray(new ModBlockEntity<?>[0]);
			var id = Registries.BLOCK.getId(blocks[0]);
			var type = blocks[0].registerEntityType(blocks);
			for (var b : blocks) {
				b.registerFromExternalType(type);
			}

			var id1 = blocks[0].getCustomBlockEntityName();
			if (id1 == null)
				id1 = new Identifier(id.getNamespace(), "be_" + id.getPath());

			Registry.register(Registries.BLOCK_ENTITY_TYPE, id1, type);
		});
	}
}