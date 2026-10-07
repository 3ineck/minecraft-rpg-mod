package com.rapha.rpgmod.client;

import com.rapha.rpgmod.RPGMod;
import net.fabricmc.api.ClientModInitializer;

public class RPGModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
    RPGMod.LOGGER.info("RPG Mod: código CLIENT iniciado!");
	}
}