package com.diamssword.greenresurgence.materials;

import com.diamssword.greenresurgence.items.BatteryItem;

public class Materials {
	public static MaterialSet wood;
	public static MaterialSet paper;
	public static MaterialSet metal;
	public static MaterialSet adesive;
	public static MaterialSet components;
	public static MaterialSet alloy;
	public static MaterialSet plastic;
	public static MaterialSet building;
	private static MaterialSet cables;
	private static MaterialSet energy;
	private static MaterialSet chemical;
	private static MaterialSet glass;
	private static MaterialSet bones;
	private static MaterialSet leather;
	private static MaterialSet fabric;

	public static void init() {
		//WOOD
		wood = MaterialSet.createSet("wood").setTierLabel(1, "Récupération").setTierLabel(2, "Écorce d’égide").setTierLabel(3, "Bois d’égide")
				.add(1, "log", "Bois Mort", "Du bois mort de l'ancien temps")
				.add(1, "furniture", "Bois de Mobilier", "Du bois récupéré sur un meuble")
				.add(2, "bark", "Écorce d'Égide", "L'écorce d'un arbre d'Égide")
				.add(3, "egide", "Bois d'Égide", "Un morceau de bois brute, tombé d'un arbre d'Égide");
		//PAPER
		paper = MaterialSet.createSet("paper")//.setTierLabel(1,"Récupération").setTierLabel(2,"Écorce d’égide").setTierLabel(3,"Bois d’égide")
				.add(1, "newspaper", "Journeaux déchirés", "De vieux journeaux abimés")
				.add(1, "poster", "Affiche déchirée", "Une affiche en lambeaux")
				.add(2, "cardboard", "Carton", "Du carton, tout simplement")
				.add(3, "paper", "Papier", "Du papier en bon état");
		//Metal
		metal = MaterialSet.createSet("metal")
				.add(1, "scrap", "Féraille Rouillé", "Divers morceaux de fer rouillés")
				.add(2, "ironbar", "Barre de Fer ", "Une barre de fer solide")
				.add(2, "ironingot", "Lingot de Fer", "Un lingot de faire brut")
				.add(2, "copperwire", "Fil de Cuivre", "Un fil de cuivre en bon état")
				.add(2, "copperplate", "Plaque de Cuivre", "Une plaque de cuivre brillante")
				.add(2, "lead", "Plombs", "Récuperé d'anciens équipements de pêches ou de chasse")
				.add(2, "lead1", "Morceaux de plomb", "D'anciens contrepoids en plomb")
				.add(3, "lithium", "Lithium", "De la poudre de lithium pure")
				.add(3, "graphite", "Graphite", "Une barre de graphite")
				.add(3, "goldjewelry", "Bijou en or", "Cette bague ne manquera plus à personne...")
				.add(3, "goldbar", "Lingot d'Or", "Jackpot!")
				.add(3, "silver", "Lingot d'Argent", "Jackpot!")
				.add(3, "silverjewelry", "Gourmette en Argent", "Cette gourmette ne manquera plus à personne...");
		//Alloy
		alloy = MaterialSet.createSet("alloy")
				.add(3, "brass", "Morceaux de Laitons", "Divers morceaux de laitons")
				.add(3, "bronze", "Lingot de Bronze", "Un lingot de Bronze")
				.add(3, "magnets", "Aimants", "Des aimants de cuisine")
				.add(3, "ferro", "Barre Ferromagnetique", "Une barre aimantée")
				.add(4, "steel", "Lingot d'Acier", "Un alliage de qualitée")
				.add(4, "electrum", "Lingot d'Electrum", "Un conducteur exceptionnel")
				.add(4, "aluminium", "Plaque d'Aluminium", "Leger et Résistant")
				.add(5, "unobtanium", "Unobtanium", "/gamemode creative");

		//Plastic
		plastic = MaterialSet.createSet("plastic")
				.add(1, "bottle", "Bouteille en Plastique", "Une simple bouteille")
				.add(1, "sheet", "Rideau de Douche", "Un vieux rideau en plastique")
				.add(1, "rubber_duck", "Canard en Plastique", "Coin Coin.")
				.add(1, "film", "Film Plastique", "Un film en plastique souple")
				.add(2, "bin", "Corbeille", "Une petite corbeille en plastique")
				.add(2, "bac", "Bac en Plastique", "Une bac en plastique solide")
				.add(4, "plastacier", "Lingot de Plastacier", "Un alliage de palstique rés solide!");

		//Building
		building = MaterialSet.createSet("build")
				.add(1, "stone", "Pierre", "Primitif mais résistant")
				.add(1, "brick", "Brique", "Une brique.")
				.add(1, "parpaing", "Parpaing", "Par monts et parpaings!")
				.add(1, "sand", "Sable", "Du sable fin")
				.add(3, "cement", "Béton", "Pilier du monde moderne")
				.add(4, "plaster", "Plâtre", "Pour une finition propre")
				.add(5, "marble", "Marbre", "Pour l'élégance");
		//Adesive
		adesive = MaterialSet.createSet("adesive")
				.add(1, "scotch", "Scotch", "Un rouleau de Scotch")
				.add(1, "glue", "Colle", "Un bon pot de colle")
				.add(1, "resin", "Résine", "Gluant et visqueux")
				.add(2, "nail", "Clous", "Si j'avais un marteaux...")
				.add(2, "screw", "Vis", "De la visserie en tout genre")
				.add(2, "nuts", "Boulon", "Un boulon.")
				.add(4, "epoxy", "Résine Epoxy", "Une résine puissante")
				.add(4, "glue1", "Colle Professionelle", "Maintenant 2 fois plus collant!")
				.add(4, "rivet", "Rivets en acier", "C'est du solide");

		//Components
		components = MaterialSet.createSet("component")
				.add(3, "board", "Carte Mère Abimée", "Plus de gaming sur cette carte...")
				.add(3, "socket", "Carte d'Extension", "Une petite carte éléctronique")
				.add(3, "diode", "Diodes Anciennes", "De vielles diodes peu puissantes")
				.add(3, "case", "Boitier Electrique", "Un boitier plein de petits composants")
				.add(4, "diode1", "Diode Moderne", "Une diode standard")
				.add(4, "board1", "Carte Mère Neuve", "(Presque) Neuve!")
				.add(4, "lens", "Lentille Graveuse", "Laser de categorie 2. Ne pas regarde directement!")
				.add(5, "diode2", "Diodes Crystalines", "De minuscules diodes High-Tech!")
				.add(5, "board2", "Carte Mère High Tech", "On dirait que ça pulse même éteint...")
				.add(5, "socket1", "Carte d'Extension Supraconductrice", "Les leds bleu rajoutent un charme");

		//Cables
		cables = MaterialSet.createSet("cables")
				.add(1, "tin", "Cable en Étain", "Une bobine de cable en étain")
				.add(1, "house", "Cable Électrique", "Un cable d'alimentation standard")
				.add(3, "high", "Cable Haut Voltage", "Un cable pour les hautes tensions")
				.add(3, "underground", "Cable souterrain", "Un cable haute tension spécialisé et robuste")
				.add(4, "supra", "Supraconducteur", "Un cable miltaire sans aucunes pertes!");

		//Energy
		energy = MaterialSet.createSet("energy")
				.add(1, "battery", "Pile", "Une pile à usage unique", false, (a, b, c, d) -> new BatteryItem(a.maxCount(16), b, c, d, BatteryTiers.BATTERY, 1f))
				.add(3, "cell", "Batterie", "Une batterie rechargable")
				.add(5, "cell1", "Batterie Haute Capacitée", "Une batterie rechargable de haute capacitée");
		//Chemical
		chemical = MaterialSet.createSet("chemical")
				.add(1, "vinegar", "Vinaigre", "Bon pour la plomberie et en salade")
				.add(1, "bleach", "Javel", "Bon pour le ménage, pas en salade")
				.add(1, "chlorine", "Chlore", "Blanchi un peu les doigts")
				.add(3, "alcool", "Alcool à Brulé", "Pique un peu la gorge...")
				.add(3, "water", "Eau Déminéralisée", "Peu rafraichissant")
				.add(3, "acid", "Acide", "De l'acide peut puissant")
				.add(4, "acid1", "Acide Sulfurique", "Un acide extrémement corrosif")
				.add(4, "solvant", "Solvant", "Un solvant industriel")
				.add(4, "amonia", "Ammoniac", "")
				.add(4, "acetone", "Acétone", "");
		leather = MaterialSet.createSet("leather")
				.add(1, "pig", "cuir de Cochon", "")
				.add(1, "cow", "Cuir de Vache", "")
				.add(2, "corcodile", "Cuir de Crocodile", "")
				.add(2, "bear", "Cuir d'Ours'", "")
				.add(3, "rino", "Cuir de Rhinocéros", "Le meilleur ami de l'homme...");
		fabric = MaterialSet.createSet("fabric")
				.add(1, "cotton", "Toile en Cotton", "Un tissu léger et doux")
				.add(1, "lin", "Drap en Lin", "Un tissu doux")
				.add(1, "poly", "Tissu Polyester", "Une matière en plastique")
				.add(1, "silk", "Tissu de Soie", "Un tissu trés léger")
				.add(2, "jute", "Carré de Jute", "Un tissu brut et résistant")
				.add(3, "jean", "Pantalon en Jean", "")
				.add(3, "chanvre", "Toile en Chanvre", "");
		//Glass
		glass = MaterialSet.createSet("glass")
				.add(1, "shards", "Éclats de Verre", "Des éclats de verre tranchants")
				.add(1, "bottle", "Bouteille en Verre", "Une bouteille vide en verre")
				.add(2, "window", "Vitre en Verre", "Une vitre plate en verre")
				.add(4, "window1", "Vitre Blindé", "Une vitre à l'épreuve des balles");
		//Bones
		bones = MaterialSet.createSet("bones")
				.add(1, "fish", "Arêtes", "Des os de poisson")
				.add(1, "small", "Petits os", "De petits os d'oiseau ou de rongeur")
				.add(2, "human", "Os Humains", "Les os de quelqu'un moins chanceux...")
				.add(2, "dog", "Os de chien", "Le meilleur ami de l'homme...")
				.add(3, "big", "Gros Os", "L'os d'une grosse créature")
				.add(3, "ivory", "Ivoir", "De l'ivoir ");
	}
}
