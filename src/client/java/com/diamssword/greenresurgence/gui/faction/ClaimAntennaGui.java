package com.diamssword.greenresurgence.gui.faction;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ClaimBlockEntity;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuiPackets;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
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


		if(blockEntity.getLevel() < 2) {
			var up = Components.button(Text.translatable("gui.green_resurgence.claim_antenna.upgrade"), c -> {
				Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(blockEntity.getPos(), "upgrade", true));
				this.close();
			});
			rootComponent.childById(FlowLayout.class, "mainLay").child(up);
		}

	}
}

