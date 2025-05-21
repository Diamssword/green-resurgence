package com.diamssword.greenresurgence.systems.faction.perimeter.components;

public enum Perms {
	PLACE(1, true),
	BREAK(2, true),
	INVENTORY(3),
	INVITE(4),
	EDIT_ROLE(5),
	ADMIN(6);
	public final int id;
	public final boolean needSurvival;

	Perms(int id) {
		this.id = id;
		this.needSurvival = false;
	}

	Perms(int id, boolean needSurvival) {
		this.id = id;
		this.needSurvival = needSurvival;
	}

	public static Perms getByID(int id) {
		for (var v : Perms.values())
			if (v.id == id)
				return v;
		return null;
	}

}
