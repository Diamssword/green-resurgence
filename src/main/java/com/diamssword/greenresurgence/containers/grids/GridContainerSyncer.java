package com.diamssword.greenresurgence.containers.grids;

import com.diamssword.greenresurgence.containers.player.grids.ArmorGrid;
import com.diamssword.greenresurgence.containers.player.grids.BagsGrid;
import com.diamssword.greenresurgence.containers.player.grids.OffHandGrid;
import com.diamssword.greenresurgence.containers.player.grids.PlayerGrid;
import com.diamssword.greenresurgence.utils.TriFunction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridContainerSyncer {

	private static final Map<String, TriFunction<String, Integer, Integer, IGridContainer>> factories = new HashMap<>();
	private static final Map<Class<? extends IGridContainer>, String> serializers = new HashMap<>();

	private static void registerGridSerializer(Class<? extends IGridContainer> clazz, String id, TriFunction<String, Integer, Integer, IGridContainer> factory) {
		serializers.put(clazz, id);
		factories.put(id, factory);
	}

	static {
		registerGridSerializer(GridContainer.class, "basic", GridContainer::new);
		registerGridSerializer(PlayerGrid.class, "player", PlayerGrid::new);
		registerGridSerializer(ArmorGrid.class, "playerArmor", ArmorGrid::new);
		registerGridSerializer(OffHandGrid.class, "playerOffhand", OffHandGrid::new);
		registerGridSerializer(BagsGrid.class, "bagSlots", BagsGrid::new);
		registerGridSerializer(ContainerArmorGrid.class, "containerArmor", ContainerArmorGrid::new);
		registerGridSerializer(ExtractOnlyGrid.class, "extractOnly", ExtractOnlyGrid::new);
	}

	public int count;
	public String[] names;
	public String[] serializingId;
	public int[] sizes;
	public BlockPos inventoryPos;

	public GridContainerSyncer(@Nullable BlockPos inventoryPos, IGridContainer... containers) {
		this.inventoryPos = inventoryPos;
		count = containers.length;
		List<Integer> ls = new ArrayList<>();
		List<String> ls1 = new ArrayList<>();
		List<String> ls2 = new ArrayList<>();
		for(IGridContainer container : containers) {
			ls1.add(container.getName());

			ls2.add(serializers.getOrDefault(container.getClass(), "basic"));
			ls.add(container.getWidth());
			ls.add(container.getHeight());
		}
		names = ls1.toArray(new String[0]);
		serializingId = ls2.toArray(new String[0]);
		sizes = new int[ls.size()];
		for(int i = 0; i < ls.size(); i++) {
			sizes[i] = ls.get(i);
		}
	}

	public GridContainerSyncer() {
		count = 0;
		names = new String[0];
		serializingId = new String[0];
		sizes = new int[0];
	}

	public IGridContainer[] getContainers() {
		IGridContainer[] res = new IGridContainer[count];
		for(int i = 0; i < count; i++) {
			var fac = factories.get(serializingId[i]);
			if(fac != null)
				res[i] = fac.accept(names[i], sizes[i * 2], sizes[(i * 2) + 1]);
			else
				res[i] = new GridContainer(names[i], sizes[i * 2], sizes[(i * 2) + 1]);

		}

		return res;
	}

	public static void serializer(PacketByteBuf write, GridContainerSyncer val) {
		if(val.inventoryPos == null) {val.inventoryPos = BlockPos.ORIGIN;}
		write.writeInt(val.count);
		write.writeBlockPos(val.inventoryPos);
		write.writeIntArray(val.sizes);
		for(String name : val.names) {
			write.writeString(name);
		}
		for(String serial : val.serializingId) {
			write.writeString(serial);
		}
	}

	public static GridContainerSyncer unserializer(PacketByteBuf read) {
		var p = new GridContainerSyncer();
		p.count = read.readInt();
		p.inventoryPos = read.readBlockPos();
		p.sizes = read.readIntArray();
		p.names = new String[p.count];
		for(int i = 0; i < p.count; i++) {
			p.names[i] = read.readString();
		}
		p.serializingId = new String[p.count];
		for(int i = 0; i < p.count; i++) {
			p.serializingId[i] = read.readString();
		}
		return p;
	}

}
