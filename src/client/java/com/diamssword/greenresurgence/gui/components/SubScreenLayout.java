package com.diamssword.greenresurgence.gui.components;

import com.diamssword.greenresurgence.GreenResurgence;
import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.UIErrorToast;
import io.wispforest.owo.util.Observable;
import net.minecraft.util.Identifier;
import org.w3c.dom.Element;

public class SubScreenLayout extends FlowLayout {
	private UIModel model;
	private Identifier modelId;
	private OwoUIAdapter<FlowLayout> uiAdapter;
	public final Observable<Size> size = Observable.of(Size.zero());

	public SubScreenLayout(Sizing horizontalSizing, Sizing verticalSizing, Algorithm algorithm, BaseUIModelScreen.DataSource source) {
		super(horizontalSizing, verticalSizing, algorithm);
		setLayout(source);
	}

	@Override
	protected void applySizing() {
		super.applySizing();
		size.set(Size.of(this.width, this.height));
	}

	public SubScreenLayout(Sizing horizontalSizing, Sizing verticalSizing, Algorithm algorithm, String source) {
		this(horizontalSizing, verticalSizing, algorithm, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource(source)));
	}

	public void setLayout(String screenID) {
		setLayout(BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource(screenID)));
	}

	public void setLayout(BaseUIModelScreen.DataSource source) {
		var providedModel = source.get();
		if (providedModel == null) {
			source.reportError();
		}
		this.model = providedModel;
		this.modelId = source instanceof BaseUIModelScreen.DataSource.AssetDataSource assetSource
				? assetSource.assetPath()
				: null;
		try {
			this.clearChildren();
			this.uiAdapter = this.createAdapter(this.x, this.y, this.width, this.height);
			this.child(this.uiAdapter.rootComponent);
			this.uiAdapter.rootComponent.mount(this, this.x, this.y);
			this.updateLayout();
		} catch (Exception error) {
			Owo.LOGGER.warn("Could not initialize sub screen", error);
			UIErrorToast.report(error);
		}
	}

	public FlowLayout getRoot() {
		return uiAdapter.rootComponent;
	}

	public UIModel getModel() {
		return model;
	}

	public Identifier getModelId() {
		return modelId;
	}

	public OwoUIAdapter<FlowLayout> getUiAdapter() {
		return uiAdapter;
	}

	protected OwoUIAdapter<FlowLayout> createAdapter(int x, int y, int width, int height) {
		return this.model.createAdapterWithoutScreen(x, y, width, height, FlowLayout.class);
	}

	@Override
	public void mount(ParentComponent parent, int x, int y) {
		super.mount(parent, x, y);
	}

	@Override
	public void inflate(Size space) {
		super.inflate(space);
	}


	public static FlowLayout parse(Element element) {
		UIParsing.expectAttributes(element, "source");
		return new SubScreenLayout(Sizing.content(), Sizing.content(), Algorithm.HORIZONTAL, BaseUIModelScreen.DataSource.asset(GreenResurgence.asRessource(element.getAttribute("source"))));
	}
}
