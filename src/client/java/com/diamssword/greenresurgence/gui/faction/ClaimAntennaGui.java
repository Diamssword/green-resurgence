package com.diamssword.greenresurgence.gui.faction;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ClaimBlockEntity;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ClaimAntennaGui extends BaseUIModelScreen<FlowLayout> {

	public static BlockPos viewedZone;
	private final ClaimBlockEntity blockEntity;

	public ClaimAntennaGui(ClaimBlockEntity be) {
		super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("faction/claim_antenna")));
		blockEntity = be;

	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		var seeZone = rootComponent.childById(ButtonComponent.class, "seeZone");
		if (viewedZone != null && viewedZone.equals(blockEntity.getPos()))
			seeZone.setMessage(Text.literal("Cacher la Zone"));
		seeZone.onPress((b) -> {
			if (viewedZone != null && viewedZone.equals(blockEntity.getPos())) {
				viewedZone = null;
				seeZone.setMessage(Text.literal("Voir la zone"));
				seeZone.parent().onChildMutated(seeZone);
			} else {
				viewedZone = blockEntity.getPos();
				seeZone.setMessage(Text.literal("Cacher la zone"));
				seeZone.parent().onChildMutated(seeZone);
			}

		});
		var unclaim = rootComponent.childById(ButtonComponent.class, "unclaim");
		var sizeS = rootComponent.childById(DiscreteSliderComponent.class, "size");
		

	}
}

