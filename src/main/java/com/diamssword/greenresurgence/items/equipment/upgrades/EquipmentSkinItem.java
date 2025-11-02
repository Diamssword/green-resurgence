package com.diamssword.greenresurgence.items.equipment.upgrades;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.MItems;
import com.diamssword.greenresurgence.items.equipment.EquipmentUpgradeItem;
import com.diamssword.greenresurgence.systems.equipement.EquipmentSkins;
import com.diamssword.greenresurgence.systems.equipement.Equipments;
import com.diamssword.greenresurgence.systems.equipement.IEquipmentDef;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class EquipmentSkinItem extends EquipmentUpgradeItem implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public static Function<Boolean, Object> ProviderFunction;
	private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

	private static final BiConsumer<Item, ItemGroup.Entries> generator = (i, e) -> {
		EquipmentSkins.skins.keySet().forEach(c -> {
			var st = new ItemStack(i, 1);
			st.getOrCreateNbt().putString("skin", c);
			e.add(st);
		});
	};

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		var skin = getSkin(stack);
		if(!skin.isBlank())
			return Optional.of(new EquipmentSkinTooltipData(skin));
		return super.getTooltipData(stack);
	}

	public EquipmentSkinItem() {
		super(new OwoItemSettings().maxCount(8).group(MItems.GROUP).tab(1).stackGenerator(generator), "", Equipments.P_SKIN, 0, 0);
	}

	public static String getSkin(ItemStack stack) {
		if(stack.hasNbt()) {
			return stack.getNbt().getString("skin");
		}
		return "";
	}

	@Override
	public boolean canBeApplied(IEquipmentDef equipment, ItemStack stack) {
		var eq = EquipmentSkins.get(this.getSkin(stack), equipment.getEquipmentItem());
		return eq.isPresent();
	}

	@Override
	public Text getName(ItemStack stack) {
		String skin = getSkin(stack);
		if(skin.isEmpty()) {
			skin = "empty";
		}
		return Text.translatable(this.getTranslationKey(stack)).append(Text.literal(" (")).append(Text.translatable(Util.createTranslationKey("skin", GreenResurgence.asRessource(skin)))).append(Text.literal(")"));
	}

	@Override
	public void createRenderer(Consumer<Object> consumer) {
		consumer.accept(ProviderFunction.apply(false));
	}

	@Override
	public Supplier<Object> getRenderProvider() {
		return renderProvider;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}
}
