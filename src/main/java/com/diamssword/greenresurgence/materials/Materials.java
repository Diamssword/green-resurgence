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
        //Building
        MaterialSet.createSet("build")
                .add(1,"stone","Pierre","Primitif mais résistant")
                .add(1,"brick","Brique","Une brique.")
                .add(1,"parpaing","Parpaing","Par monts et parpaings!")
                .add(1,"sand","Sable","Du sable fin")
                .add(3,"cement","Béton","Pilier du monde moderne")
                .add(4,"plaster","Plâtre","Pour une finition propre")
                .add(5,"marble","Marbre","Pour l'élégance");
        //Adesive
        MaterialSet.createSet("adesive")
                .add(1,"scotch","Scotch","Un rouleau de Scotch")
                .add(1,"glue","Colle","Un bon pot de colle")
                .add(1,"resin","Résine","Gluant et visqueux")
                .add(2,"nail","Clous","Si j'avais un marteaux...")
                .add(2,"screw","Vis","De la visserie en tout genre")
                .add(4,"epoxy","Résine Epoxy","Une résine puissante")
                .add(4,"glue1","Colle Professionelle","Maintenant 2 fois plus collant!")
                .add(4,"rivet","Rivets en acier","C'est du solide");

        //Components
        MaterialSet.createSet("component")
                .add(3,"board","Carte Mère Abimée","Plus de gaming sur cette carte...")
                .add(3,"socket","Carte d'Extension","Une petite carte éléctronique")
                .add(3,"diode","Diodes Anciennes","De vielles diodes peu puissantes")
                .add(3,"case","Boitier Electrique","Un boitier plein de petits composants")
                .add(4,"diode1","Diode Moderne","Une diode standard")
                .add(4,"board1","Carte Mère Neuve","(Presque) Neuve!")
                .add(4,"lens","Lentille Graveuse","Laser de categorie 2. Ne pas regarde directement!")
                .add(5,"diode2","Diodes Crystalines","De minuscules diodes High-Tech!")
                .add(5,"board2","Carte Mère High Tech","On dirait que ça pulse même éteint...")
                .add(5,"socket1","Carte d'Extension Supraconductrice","Les leds bleu rajoutent un charme");
    }
}
