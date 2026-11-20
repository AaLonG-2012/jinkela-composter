package io.github.aalong2012.jinkela;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JinKelaComposter implements ModInitializer {
	public static final String MOD_ID = "jinkela-composter";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModSounds.register();
		SneakDrawHandler.register();

		LOGGER.info("JinKela Composter loaded: sneak while standing on a composter to roll the draw.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
