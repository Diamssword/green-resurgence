package com.diamssword.greenresurgence.datagen;

import com.diamssword.greenresurgence.genericBlocks.GenericBlockSet;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.materials.MaterialSet;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LangGenerator extends FabricLanguageProvider {
	private final Path existingFilePath;
	public static Map<Identifier, String> auto_name = new HashMap<>();

	public LangGenerator(FabricDataOutput dataGenerator, String code) {
		super(dataGenerator, code);
		existingFilePath = dataGenerator.getModContainer().findPath("assets/green_resurgence/lang/_" + code + ".json").get();
	}

	@Override
	public void generateTranslations(TranslationBuilder translationBuilder) {
		try {

			for (GenericBlockSet set : GenericBlocks.sets) {
				set.langGenerator(translationBuilder);
			}

			MaterialSet.registerLangs(translationBuilder);
			translationBuilder.add("materials.tier.1", "Tier I");
			translationBuilder.add("materials.tier.2", "Tier II");
			translationBuilder.add("materials.tier.3", "Tier III");
			translationBuilder.add("materials.tier.4", "Tier IV");
			translationBuilder.add("materials.tier.5", "Tier V");
			auto_name.forEach((k, v) -> autoLocalize(translationBuilder, k, v));
			translationBuilder.add(existingFilePath);
		} catch (Exception e) {
			throw new RuntimeException("Failed to add existing language file!", e);
		}
	}

	private void autoLocalize(TranslationBuilder builder, Identifier id, String name) {
		var i = name.lastIndexOf("/");
		if (i > 0)
			name = name.substring(i + 1);
		builder.add("item." + id.getNamespace() + "." + id.getPath().replaceAll("/", "."), capitalizeString(name.replaceAll("_", " ")));
	}

	public static String autoLocalizeString(String name) {
		var i = name.lastIndexOf("/");
		if (i > 0)
			name = name.substring(i + 1);
		return capitalizeString(name.replaceAll("_", " "));
	}

	public static String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
				found = false;
			}
		}
		return String.valueOf(chars);
	}
}