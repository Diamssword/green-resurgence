package com.diamssword.greenresurgence.systems.crafting;

import net.minecraft.util.Identifier;

public class ComposedIdentifier {
	private final Identifier collection;
	private final String id;

	public ComposedIdentifier(Identifier collection, String id) {
		this.collection = collection;
		this.id = id;

	}

	public ComposedIdentifier(String composedString) {
		var comp = composedString.split(";");
		this.collection = new Identifier(comp[0]);
		this.id = comp.length > 1 ? comp[1] : "empty";

	}

	@Override
	public String toString() {
		return collection.getNamespace() + ":" + collection.getPath() + ";" + id;
	}

	public Identifier getCollection() {
		return collection;
	}

	public String getId() {
		return id;
	}
}
