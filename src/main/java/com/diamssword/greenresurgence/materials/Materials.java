package com.diamssword.greenresurgence.materials;

import com.diamssword.greenresurgence.GreenResurgence;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class Materials {

    public static void init() {
        //WOOD
        MaterialSet.createSet("wood").setTierLabel(1,"Récupération").setTierLabel(2,"Écorce d’égide").setTierLabel(3,"Bois d’égide")
                .add(1,"log","Bois Mort","Du bois mort de l'ancien temps")
                .add(1,"furniture","Bois de Mobilier","Du bois récupéré sur un meuble")
                .add(2,"bark","Écorce d'Égide","L'écorce d'un arbre d'Égide")
                .add(3,"egide","Bois d'Égide","Un morceau de bois brute, tombé d'un arbre d'Égide");
        //PAPER
        MaterialSet.createSet("paper")//.setTierLabel(1,"Récupération").setTierLabel(2,"Écorce d’égide").setTierLabel(3,"Bois d’égide")
                .add(1,"newspaper","Journeaux déchirés","De vieux journeaux abimés")
                .add(1,"poster","Affiche déchirée","Une affiche en lambeaux")
                .add(2,"cardboard","Carton","Du carton, tout simplement")
                .add(3,"paper","Papier","Du papier en bon état");
        //Metal
        MaterialSet.createSet("metal")
                .add(1,"scrap","Féraille Rouillé","Divers morceaux de fer rouillés")
                .add(2,"ironbar","Barre de Fer ","Une barre de fer solide")
                .add(2,"ironingot","Lingot de Fer","Un lingot de faire brut")
                .add(2,"copperwire","Fil de Cuivre","Un fil de cuivre en bon état")
                .add(2,"copperplate","Plaque de Cuivre","Une plaque de cuivre brillante")
                .add(2,"lead","Plombs","Récuperé d'anciens équipements de pêches ou de chasse")
                .add(2,"leadweight","Poids en plomb","D'anciens contrepoids en plomb")
                .add(3,"lithium","Lithium","De la poudre de lithium pure")
                .add(3,"graphite","Graphite","Une barre de graphite")
                .add(3,"goldjewelry","Bijou en or","Cette bague ne manquera plus à personne...")
                .add(3,"goldbar","Lingot d'Or","Jackpot!")
                .add(3,"silver","Lingot d'Argent","Jackpot!")
                .add(3,"silverjewelry","Gourmette en Argent","Cette gourmette ne manquera plus à personne...");
        //Alloy
        MaterialSet.createSet("alloy")
                .add(3,"brass","Morceaux de Laitons","Divers morceaux de laitons")
                .add(3,"bronze","Lingot de Bronze","Un lingot de Bronze")
                .add(3,"magnets","Aimants","Des aimants de cuisine")
                .add(3,"ferro","Barre Ferromagnetique","Une barre aimantée")
                .add(4,"steel","Lingot d'Acier","Un alliage de qualitée")
                .add(4,"electrum","Lingot d'Electrum","Un conducteur exceptionnel")
                .add(4,"aluminium","Plaque d'Aluminium","Leger et Résistant")
                .add(5,"unobtanium","Unobtanium","/gamemode creative");
    }
}
