package com.diamssword.greenresurgence.datagen;

import com.diamssword.greenresurgence.genericBlocks.GenericBlockSet;
import com.diamssword.greenresurgence.genericBlocks.GenericBlocks;
import com.diamssword.greenresurgence.items.materials.MaterialSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LangGenerator extends FabricLanguageProvider {
	private final List<Path> list = new ArrayList<>();
	private final String lang;
	private static final String[] pathsPrefixes = new String[]{
			"",
			"stats",
			"equipment",
			"gui"
	};
	private static Map<String, Map<String, String>> auto_name = new HashMap<>();

	//public static Map<Identifier, String> auto_name = new HashMap<>();
	public static void addAutoName(String file, String lang, String defaut) {
		var map = auto_name.computeIfAbsent(file, d -> new HashMap<>());
		map.put(lang, defaut);
	}

	public LangGenerator(FabricDataOutput dataGenerator, String code) {
		super(dataGenerator, code);
		this.lang = code;
		for(var p : pathsPrefixes) {
			list.add(getDevPath("lang/" + p + "_" + code + ".json"));
		}
		var path = getDevPath("lang/generated/").toFile();
		if(!path.exists())
			path.mkdirs();
	}

	@Override
	public void generateTranslations(TranslationBuilder translationBuilder) {
		for(GenericBlockSet set : GenericBlocks.sets) {
			set.langGenerator(translationBuilder);
		}
		Map<Path, Path> links = new HashMap<>();
		Map<Path, Map<String, String>> map = new HashMap<>();

		auto_name.forEach((k, v) -> {
			var path = getDevPath("lang/generated/" + k + "_" + lang + ".json");
			links.put(path, getDevPath("lang/" + k + "_" + lang + ".json"));
			if(lang.equals("en_us"))
				map.put(path, v);
		});

		try {


			MaterialSet.registerLangs(translationBuilder);
			translationBuilder.add("materials.tier.1", "Tier I");
			translationBuilder.add("materials.tier.2", "Tier II");
			translationBuilder.add("materials.tier.3", "Tier III");
			translationBuilder.add("materials.tier.4", "Tier IV");
			translationBuilder.add("materials.tier.5", "Tier V");
			for(Path path : list) {
				try {
					translationBuilder.add(path);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			map.forEach((k, v) -> {

				try {
					JsonObject js = new JsonObject();
					if(links.get(k).toFile().exists()) {
						js = JsonParser.parseString(FileUtils.readFileToString(links.get(k).toFile())).getAsJsonObject();
					}

					var writer = new FileWriter(k.toFile());

					JsonObject finalJs = js;
					JsonObject res = new JsonObject();
					v.forEach((k1, v1) -> {
						if(!finalJs.has(k1))
							res.addProperty(k1, autoLocalizeString(v1));

					});
					Gson gson = new GsonBuilder()
							.setPrettyPrinting()
							.create();

					writer.write(gson.toJson(res));
					writer.close();

				} catch(IOException e) {
					throw new RuntimeException(e);
				}
			});

			links.forEach((k, v) -> {
				try {
					translationBuilder.add(v);
					translationBuilder.add(k);
				} catch(IOException e) {
					e.printStackTrace();
				}
			});


		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void autoLocalize(TranslationBuilder builder, Identifier id, String name) {
		var i = name.lastIndexOf("/");
		if(i > 0)
			name = name.substring(i + 1);
		builder.add("item." + id.getNamespace() + "." + id.getPath().replace("/", "."), capitalizeString(name.replace("_", " ")));
	}

	public static String autoLocalizeString(String name) {
		var i = name.lastIndexOf("/");
		if(i > 0)
			name = name.substring(i + 1);
		return capitalizeString(name.replace("_", " "));
	}

	public static String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for(int i = 0; i < chars.length; i++) {
			if(!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if(Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
				found = false;
			}
		}
		return String.valueOf(chars);
	}

	public static Path getDevPath(String path) {
		return Paths.get(Paths.get(System.getProperty("user.dir")).getParent().getParent().toString(), "src/main/devResources/" + path);
	}

}