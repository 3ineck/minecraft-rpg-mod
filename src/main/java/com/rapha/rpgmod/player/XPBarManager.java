package com.rapha.rpgmod.player;

import com.rapha.rpgmod.level.LevelTable;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class XPBarManager {

    // Uma barra para cada jogador conectado, indexada pelo UUID do jogador
    private static final Map<UUID, ServerBossEvent> BARS = new HashMap<>();

    private XPBarManager() {
    }

    public static void initialize() {
        // Jogador entrou no servidor: cria a barra
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                show(handler.getPlayer()));

        // Jogador saiu: remove a barra (evita vazamento de memória)
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                remove(handler.getPlayer()));

        // Ao renascer, o Minecraft cria um NOVO objeto ServerPlayer,
        // então a barra precisa passar a apontar para ele
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            remove(oldPlayer);
            show(newPlayer);
        });
    }

    public static void update(ServerPlayer player) {
        ServerBossEvent bar = BARS.get(player.getUUID());
        if (bar == null) {
            return;
        }

        int xp = RPGPlayerData.get(player).experience();
        int level = LevelTable.getLevel(xp);

        bar.setProgress(LevelTable.getProgress(xp));
        bar.setName(buildName(xp, level));
    }

    private static void show(ServerPlayer player) {
        ServerBossEvent bar = new ServerBossEvent(
                Mth.createInsecureUUID(player.getRandom()), // id da barra, não do jogador
                Component.empty(),
                BossEvent.BossBarColor.GREEN,
                BossEvent.BossBarOverlay.NOTCHED_10
        );

        bar.addPlayer(player);
        BARS.put(player.getUUID(), bar);
        update(player);
    }

    private static void remove(ServerPlayer player) {
        ServerBossEvent bar = BARS.remove(player.getUUID());
        if (bar != null) {
            bar.removeAllPlayers();
        }
    }

    private static Component buildName(int xp, int level) {
        if (level >= LevelTable.getMaxLevel()) {
            return Component.literal("Level " + level + " — MAX");
        }

        int nextLevelXp = LevelTable.getXpRequired(level + 1);
        return Component.literal("Level " + level + " — " + xp + " / " + nextLevelXp + " XP");
    }
}
