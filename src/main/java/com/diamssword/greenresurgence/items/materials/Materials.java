package com.diamssword.greenresurgence.items.materials;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class Materials {
	public static MaterialSet wood;
	public static MaterialSet paper;
	public static MaterialSet metal;
	public static MaterialSet adesive;
	public static MaterialSet components;
	public static MaterialSet water;
	public static MaterialSet alloy;
	public static MaterialSet plastic;
	public static MaterialSet building;
	public static MaterialSet nanite;
	public static MaterialSet fuel;
	public static MaterialSet cables;
	public static MaterialSet energy;
	public static MaterialSet chemical;
	public static MaterialSet compost;
	public static MaterialSet glass;
	public static MaterialSet bones;
	public static MaterialSet leather;
	public static MaterialSet fabric;
	public static MaterialSet medicine;
	public static MaterialSet plants;

	public static void init() {
		//WOOD
		wood = MaterialSet.createSet("wood").setTierLabel(1, "Récupération").setTierLabel(2, "Écorce d’égide").setTierLabel(3, "Bois d’égide")
				.add(1, "log")
				.add(1, "furniture")
				.add(2, "bark")
				.add(3, "egide");
		//Alloy
		plants = MaterialSet.createSet("plant")
				.add(1, "fiber");
		//PAPER
		paper = MaterialSet.createSet("paper")//.setTierLabel(1,"Récupération").setTierLabel(2,"Écorce d’égide").setTierLabel(3,"Bois d’égide")
				.add(1, "newspaper")
				.add(1, "poster")
				.add(1, "toilet")
				.add(1, "money")
				.add(2, "cardboard")
				.add(3, "paper");
		//Metal
		metal = MaterialSet.createSet("metal")
				.add(1, "scrap")
				.add(1, "can")
				.add(1, "can_1")
				.add(1, "coin")
				.add(2, "ironbar")
				.add(2, "ironingot")
				.add(2, "copperwire")
				.add(2, "copperplate")
				.add(2, "lead")
				.add(2, "lead1")
				.add(3, "lithium")
				.add(3, "graphite")
				.add(3, "goldjewelry")
				.add(3, "goldbar")
				.add(3, "silver")
				.add(3, "silverjewelry");
		//Alloy
		alloy = MaterialSet.createSet("alloy")
				.add(3, "brass")
				.add(3, "bronze")
				.add(3, "magnets")
				.add(3, "ferro")
				.add(4, "steel")
				.add(4, "electrum")
				.add(4, "aluminium")
				.add(5, "unobtanium");

		//Plastic
		plastic = MaterialSet.createSet("plastic")
				.add(1, "bottle")
				.add(1, "sheet")
				.add(1, "rubber_duck")
				.add(1, "soft")
				.add(1, "film")
				.add(2, "bin")
				.add(2, "bac")
				.add(2, "litter")
				.add(2, "tupperware")
				.add(2, "hard")
				.add(4, "plastacier");

		//Building
		building = MaterialSet.createSet("build")
				.add(1, "stone")
				.add(1, "brick")
				.add(1, "clay")
				.add(1, "parpaing")
				.add(1, "sand")
				.add(2, "ceramic")
				.add(3, "cement")
				.add(4, "plaster")
				.add(5, "marble");
		//Adesive
		adesive = MaterialSet.createSet("adesive")
				.add(1, "scotch")
				.add(1, "glue")
				.add(1, "resin")
				.add(2, "nail")
				.add(2, "screw")
				.add(2, "nuts")
				.add(4, "epoxy")
				.add(4, "glue1")
				.add(4, "rivet");

		//Components
		components = MaterialSet.createSet("component")
				.add(3, "board")
				.add(3, "socket")
				.add(3, "diode")
				.add(3, "case")
				.add(4, "diode1")
				.add(4, "board1")
				.add(4, "lens")
				.add(5, "diode2")
				.add(5, "board2")
				.add(5, "socket1");

		//Cables
		cables = MaterialSet.createSet("cables")
				.add(1, "tin")
				.add(1, "house")
				.add(3, "high")
				.add(3, "underground")
				.add(4, "supra");

		//Energy
		energy = MaterialSet.createSet("energy")
				.add(1, "battery", true, (a, b, c, d) -> new BatteryItem(a.maxCount(16), b, c, d, BatteryTiers.BATTERY, 1f))
				.add(3, "cell", true, (a, b, c, d) -> new BatteryItem(a.maxCount(8), b, c, d, BatteryTiers.LIPO, 1f))
				.add(5, "cell1", true, (a, b, c, d) -> new BatteryItem(a.maxCount(4), b, c, d, BatteryTiers.HIGH_TECH, 1f));
		//Chemical
		chemical = MaterialSet.createSet("chemical")
				.add(1, "vinegar")
				.add(1, "bleach")
				.add(1, "chlorine")
				.add(1, "house")
				.add(3, "alcool")
				.add(3, "water")
				.add(3, "acid")
				.add(4, "acid1")
				.add(4, "solvant")
				.add(4, "amonia")
				.add(4, "acetone");
		leather = MaterialSet.createSet("leather")
				.add(1, "pig")
				.add(1, "cow")
				.add(2, "crocodile")
				.add(2, "bear")
				.add(3, "rino");
		fabric = MaterialSet.createSet("fabric")
				.add(1, "cotton")
				.add(1, "lin")
				.add(1, "poly")
				.add(1, "silk")
				.add(2, "jute")
				.add(2, "jean")
				.add(2, "chanvre")
				.add(3, "wool")
				.add(3, "fur");
		//Glass
		glass = MaterialSet.createSet("glass")
				.add(1, "shards")
				.add(1, "bottle")
				.add(1, "cup")
				.add(2, "window")
				.add(4, "window1");
		//Bones
		bones = MaterialSet.createSet("bones")
				.add(1, "fish")
				.add(1, "small")
				.add(2, "human")
				.add(2, "dog")
				.add(2, "medium")
				.add(3, "big")
				.add(3, "ivory");
		//Bones
		medicine = MaterialSet.createSet("medicine")
				.add(1, "plant", false, makeFoodEffect(StatusEffects.INSTANT_HEALTH, 1, 0))
				.add(1, "paracetamol", false, makeFoodEffect(StatusEffects.REGENERATION, 1200, 0));
		//Bones
		water = MaterialSet.createSet("water");

		nanite = MaterialSet.createSet("nanotek")
				.add(1, "gen_1")
				.add(3, "gen_2")
				.add(4, "gen_3");
		compost = MaterialSet.createSet("compost")
				.add(1, "manure")
				.add(1, "compost")
				.add(1, "loam")
				.add(2, "bonemeal");
		fuel = MaterialSet.createSet("fuel")
				.add(2, "bio_fuel")
				.add(2, "coal")
				.add(3, "gas")
				.add(3, "gasoline");
	}

	private static OwoItemSettings makeFoodEffect(StatusEffect effect, int duration, int amplifier) {
		var food = new FoodComponent.Builder().alwaysEdible().statusEffect(new StatusEffectInstance(effect, duration, amplifier), 1).build();
		return new OwoItemSettings().food(food);
	}
}
