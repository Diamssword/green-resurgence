package com.diamssword.greenresurgence.gui.faction;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ClaimBlockEntity;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuiPackets;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;

public class ClaimAntennaGui extends BaseUIModelScreen<FlowLayout> {

	public static boolean viewBounds = false;
	private final ClaimBlockEntity blockEntity;
	private final int level;

	public ClaimAntennaGui(ClaimBlockEntity be, int level) {
		super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("faction/claim_antenna")));
		blockEntity = be;
		this.level = level;


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
		var sc = rootComponent.childById(FlowLayout.class, "mainLay");

		if(sc != null && level > 0) {
			if(level == 3)
				sc.child(Components.label(Text.translatable("gui.green_resurgence.claim_antenna.error.zone")).horizontalSizing(Sizing.fill(95)));
			var unclaim = Components.button(Text.translatable("gui.green_resurgence.claim_antenna.remove"), _a -> {
				Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(blockEntity.getPos(), "remove", true));
				this.close();
			});
			sc.child(unclaim);


			if(blockEntity.getLevel() < 2) {
				var up = Components.button(Text.translatable("gui.green_resurgence.claim_antenna.upgrade"), c -> {
					Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(blockEntity.getPos(), "upgrade", true));
					this.close();
				});
				sc.child(up);
			}

			sc.child(Components.label(Text.translatable("gui.green_resurgence.claim_antenna.resize")));
			var s = Components.discreteSlider(Sizing.fill(90), (ClaimBlockEntity.minRange * 2) + 1, (blockEntity.getMaxRange() * 2) + 1);
			s.scrollStep(0.02);
			sc.child(s);

			s.setFromDiscreteValue((blockEntity.getSize() * 2) + 1);
			var b = Components.button(Text.translatable("gui.green_resurgence.claim_antenna.resize.confirm"), _c -> {
				Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(blockEntity.getPos(), "resize", (int) (s.discreteValue() - 1) / 2));
			});
			b.tooltip(Text.translatable("gui.green_resurgence.claim_antenna.resize.tip", (int) s.discreteValue()));
			s.onChanged().subscribe(v -> {
				b.tooltip(Text.translatable("gui.green_resurgence.claim_antenna.resize.tip", (int) s.discreteValue()));
			});
			sc.child(b);
		}

	}
}

