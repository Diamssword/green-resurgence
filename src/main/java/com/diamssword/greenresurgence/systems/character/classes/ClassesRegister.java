package com.diamssword.greenresurgence.systems.character.classes;

import com.diamssword.characters.api.CharactersApi;

public class ClassesRegister {

	public static void init() {
		CharactersApi.stats().registerRole("brute", ClasseBrute::new);
	}
}
