package com.rapha.rpgmod.level;

import com.rapha.rpgmod.RPGMod;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.OptionalInt;

public final class LevelTable {

    private static final String PATH = "/data/rpg-mod/levels.json";

    // Lê o objeto { "levels": [ ... ] } como uma List<LevelEntry>
    private static final Codec<List<LevelEntry>> FILE_CODEC =
            LevelEntry.CODEC.listOf().fieldOf("levels").codec();

    private static List<LevelEntry> entries = List.of();

    // Classe utilitária: ninguém deve instanciar
    private LevelTable() {
    }

    public static void load() {
        try (InputStream stream = LevelTable.class.getResourceAsStream(PATH)) {
            if (stream == null) {
                throw new IllegalStateException("Arquivo não encontrado: " + PATH);
            }

            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonElement json = JsonParser.parseReader(reader);

                List<LevelEntry> loaded = FILE_CODEC
                        .parse(JsonOps.INSTANCE, json)
                        .getOrThrow(error -> new IllegalStateException(
                                "levels.json inválido: " + error));

                validate(loaded);
                entries = List.copyOf(loaded);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler " + PATH, e);
        }

        RPGMod.LOGGER.info("Tabela de levels carregada: {} levels", entries.size());
    }

    private static void validate(List<LevelEntry> list) {
        if (list.isEmpty()) {
            throw new IllegalStateException("levels.json está vazio");
        }

        LevelEntry first = list.get(0);
        if (first.level() != 1 || first.xp() != 0) {
            throw new IllegalStateException("A primeira entrada deve ser level 1 com xp 0");
        }

        for (int i = 1; i < list.size(); i++) {
            LevelEntry previous = list.get(i - 1);
            LevelEntry current = list.get(i);

            if (current.level() != previous.level() + 1) {
                throw new IllegalStateException(
                        "Level pulado ou fora de ordem após o level " + previous.level());
            }
            if (current.xp() <= previous.xp()) {
                throw new IllegalStateException(
                        "XP do level " + current.level() + " deve ser maior que a do anterior");
            }
        }
    }

    public static int getLevel(int xp) {
        int result = 1;
        for (LevelEntry entry : entries) {
            if (xp >= entry.xp()) {
                result = entry.level();
            } else {
                break; // lista crescente: os próximos também não alcançam
            }
        }
        return result;
    }

    public static int getMaxLevel() {
        return entries.get(entries.size() - 1).level();
    }

    public static int getXpRequired(int level) {
        // Como a validação garante levels sequenciais começando em 1,
        // o level N está no índice N - 1
        return entries.get(level - 1).xp();
    }

    // Vazio quando o jogador já está no level máximo
    public static OptionalInt getXpToNextLevel(int xp) {
        int level = getLevel(xp);
        if (level >= getMaxLevel()) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(getXpRequired(level + 1) - xp);
    }

    // Progresso dentro do level atual, de 0.0 a 1.0
    public static float getProgress(int xp) {
        int level = getLevel(xp);
        if (level >= getMaxLevel()) {
            return 1.0f;
        }

        int currentLevelXp = getXpRequired(level);
        int nextLevelXp = getXpRequired(level + 1);

        // O cast para float é essencial: int / int em Java descarta a parte decimal
        return (float) (xp - currentLevelXp) / (nextLevelXp - currentLevelXp);
    }
}