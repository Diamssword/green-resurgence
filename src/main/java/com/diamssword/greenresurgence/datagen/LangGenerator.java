package com.diamssword.greenresurgence.datagen;

import com.diamssword.greenresurgence.genericBlocks.GenericBlockSet;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.nio.file.Path;

public class LangGenerator extends FabricLanguageProvider {
	private final Path existingFilePath;
	public LangGenerator(FabricDataOutput dataGenerator) {
		super(dataGenerator, "en_us");
		existingFilePath = dataGenerator.getModContainer().findPath("assets/green_resurgence/lang/fragment.json").get();
	}
 
	@Override
	public void generateTranslations(TranslationBuilder translationBuilder) {
		try {

			for (GenericBlockSet set : GenericBlocks.sets) {
				set.langGenerator(translationBuilder);
			}
			translationBuilder.add(existingFilePath);
		} catch (Exception e) {
			throw new RuntimeException("Failed to add existing language file!", e);
		}
	}
	public static String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
				found = false;
			}
		}
		return String.valueOf(chars);
	}
}