package com.diamssword.greenresurgence.systems.character.classes;

import com.diamssword.characters.api.CharactersApi;

public class ClassesRegister {

	public static void init() {
		CharactersApi.stats().registerRole("brute", ClasseBrute::new);
		CharactersApi.stats().registerRole("avant_garde", ClasseAvantGarde::new);
		CharactersApi.stats().registerRole("survivant", ClasseSurvivant::new);
		CharactersApi.stats().registerRole("chasseur", ClasseChasseur::new);
		CharactersApi.stats().registerRole("fermier", ClasseFermier::new);
		CharactersApi.stats().registerRole("brikoleur", ClasseBrikoleur::new);
		CharactersApi.stats().registerRole("medecin", ClasseMedecin::new);
		CharactersApi.stats().registerRole("kuisto", ClasseKuisto::new);
		CharactersApi.stats().registerRole("ferrailleur", ClasseFerrailleur::new);

	}
}
