package com.diamssword.greenresurgence.gui.faction;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ClaimBlockEntity;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuiPackets;
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
	public void tick() {
		super.tick();
		if(blockEntity.getLevel() == 2) {
			var ch = this.uiAdapter.rootComponent.childById(ButtonComponent.class, "upgrade");
			if(ch != null)
				ch.remove();
		}

	}

	@Override
	protected void build(FlowLayout rootComponent) {
		var seeZone = rootComponent.childById(ButtonComponent.class, "seeZone");
		if(!viewBounds)
			seeZone.setMessage(Text.translatable("gui.green_resurgence.claim_antenna.show_border"));
		seeZone.onPress((b) -> {
			if(!viewBounds) {
				viewBounds = true;
				seeZone.setMessage(Text.translatable("gui.green_resurgence.claim_antenna.hide_border"));
				seeZone.parent().onChildMutated(seeZone);
			} else {
				viewBounds = false;
				seeZone.setMessage(Text.translatable("gui.green_resurgence.claim_antenna.show_border"));
				seeZone.parent().onChildMutated(seeZone);
			}

		});
		var unclaim = rootComponent.childById(ButtonComponent.class, "unclaim");
		unclaim.onPress(c -> {
			Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(blockEntity.getPos(), "remove", true));
			this.close();
		});
		var up = rootComponent.childById(ButtonComponent.class, "upgrade");
		if(blockEntity.getLevel() == 2) {
			up.remove();
		} else
			up.onPress(c -> {
				Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(blockEntity.getPos(), "upgrade", true));
				this.close();
			});
		var sizeS = rootComponent.childById(DiscreteSliderComponent.class, "size");


	}
}

