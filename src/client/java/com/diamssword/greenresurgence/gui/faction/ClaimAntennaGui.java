package com.diamssword.greenresurgence.gui.faction;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ClaimBlockEntity;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.text.Text;

public class ClaimAntennaGui extends BaseUIModelScreen<FlowLayout> {

	public static boolean viewBounds = false;
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
		if(!viewBounds)
			seeZone.setMessage(Text.literal("Voir les bordures"));
		seeZone.onPress((b) -> {
			if(!viewBounds) {
				viewBounds = true;
				seeZone.setMessage(Text.literal("Cacher les bordures"));
				seeZone.parent().onChildMutated(seeZone);
			} else {
				viewBounds = false;
				seeZone.setMessage(Text.literal("Voir les bordures"));
				seeZone.parent().onChildMutated(seeZone);
			}

		});
		var unclaim = rootComponent.childById(ButtonComponent.class, "unclaim");
		var sizeS = rootComponent.childById(DiscreteSliderComponent.class, "size");


	}
}

