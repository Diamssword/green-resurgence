package com.diamssword.greenresurgence.gui.faction;

import com.diamssword.greenresurgence.GreenResurgence;
import com.diamssword.greenresurgence.blockEntities.ClaimBlockEntity;
import com.diamssword.greenresurgence.blockEntities.IGuiPacketReceiver;
import com.diamssword.greenresurgence.gui.components.OddSlider;
import com.diamssword.greenresurgence.network.Channels;
import com.diamssword.greenresurgence.network.GuiPackets;
import com.diamssword.greenresurgence.utils.TextUtils;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClaimAntennaGui extends BaseUIModelScreen<FlowLayout> implements IGuiPacketReceiver {

	public static boolean viewBounds = false;
	private final ClaimBlockEntity blockEntity;
	private final int level;
	private LabelComponent power;

	public ClaimAntennaGui(ClaimBlockEntity be, int level) {
		super(FlowLayout.class, DataSource.asset(GreenResurgence.asRessource("faction/claim_antenna")));
		blockEntity = be;
		this.level = level;


	}

	@Override
	public void tick() {
		super.tick();
		if(blockEntity != null && blockEntity.getWorld().getTime() % 60 == 0)
			Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(blockEntity.getPos(), "askEnergy", true));
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
		List<Text> messages = new ArrayList<>();
		var sc = rootComponent.childById(FlowLayout.class, "center");
		if(level > 0) {
			if(level == 3)
				messages.add(Text.translatable("gui.green_resurgence.claim_antenna.error.zone"));
			var l = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
			l.gap(1).padding(Insets.of(5)).horizontalAlignment(HorizontalAlignment.CENTER);
			l.surface(Surface.flat(TextUtils.whithAlpha(TextUtils.ORANGE, 0x6f)));
			l.child(Components.label(Text.translatable("gui.green_resurgence.claim_antenna.resize")));
			var s = new OddSlider(Sizing.fill(90), (ClaimBlockEntity.minRange * 2) + 1, (blockEntity.getMaxRange() * 2) + 1, false);
			s.scrollStep(0.02);
			l.child(s);
			s.setFromDiscreteValue((blockEntity.getSize() * 2) + 1);
			var b = Components.button(Text.translatable("gui.green_resurgence.claim_antenna.resize.confirm"), _c -> {
				Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(blockEntity.getPos(), "resize", (int) (s.discreteValue() - 1) / 2));
			});
			b.tooltip(Text.translatable("gui.green_resurgence.claim_antenna.resize.tip", (int) s.discreteValue()));
			s.onChanged().subscribe(v -> {
				b.tooltip(Text.translatable("gui.green_resurgence.claim_antenna.resize.tip", (int) s.discreteValue()));
			});
			l.child(b);
			sc.child(l);
			var l1 = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
			l1.gap(1).padding(Insets.of(5)).horizontalAlignment(HorizontalAlignment.CENTER);
			l1.surface(Surface.flat(TextUtils.whithAlpha(TextUtils.ORANGE, 0x6f)));
			if(blockEntity.getLevel() < 2) {
				var up = Components.button(Text.translatable("gui.green_resurgence.claim_antenna.upgrade"), c -> {
					Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(blockEntity.getPos(), "upgrade", true));
					this.close();
				});
				l1.child(up);
			}
			var unclaim = Components.button(Text.translatable("gui.green_resurgence.claim_antenna.remove"), _a -> {
				Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(blockEntity.getPos(), "remove", true));
				this.close();
			});
			l1.child(unclaim);
			sc.child(l1);

		}
		var main = rootComponent.childById(FlowLayout.class, "mainLay");
		var right = rootComponent.childById(FlowLayout.class, "right");
		right.child(power = Components.label(Text.literal("..")));
		if(messages.isEmpty()) {
			var msgL = rootComponent.childById(ScrollContainer.class, "leftP");
			if(level > 0) {
				sc.horizontalSizing(Sizing.fill(50));
				right.horizontalSizing(Sizing.fill(50));
			} else {
				sc.horizontalSizing(Sizing.fixed(0));
				right.horizontalSizing(Sizing.fixed(100));
			}
			main.horizontalSizing(Sizing.fill(50));
			msgL.sizing(Sizing.fixed(0));
		} else {
			var msgL = rootComponent.childById(FlowLayout.class, "left");
			if(level == 0) {
				sc.horizontalSizing(Sizing.fixed(0));
				right.horizontalSizing(Sizing.fixed(60));
				msgL.horizontalSizing(Sizing.fixed(40));
				main.horizontalSizing(Sizing.fill(40));
			}
			messages.forEach(m -> msgL.child(Components.label(m).horizontalSizing(Sizing.fill(95))));
		}
		Channels.MAIN.clientHandle().send(new GuiPackets.GuiTileValue(blockEntity.getPos(), "askEnergy", true));
	}

	@Override
	public void receiveGuiPacket(PlayerEntity player, GuiPackets.GuiTileValue msg) {
		if(msg.key().equals("power")) {
			var vals = msg.value().split(";");
			if(vals.length >= 4) {
				power.text(Text.translatable("gui.green_resurgence.claim_antenna.power.detail", vals[1], vals[0], vals[2], vals[3]));

			}

		}
	}
}

