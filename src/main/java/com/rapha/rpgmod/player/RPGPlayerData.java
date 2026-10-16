package com.rapha.rpgmod.player;

import com.rapha.rpgmod.RPGMod;
import com.rapha.rpgmod.level.LevelTable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.player.Player;

public record RPGPlayerData(int experience) {

    public static final Codec<RPGPlayerData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("experience")
                            .forGetter(RPGPlayerData::experience)
            ).apply(instance, RPGPlayerData::new));

    public static final AttachmentType<RPGPlayerData> ATTACHMENT =
            AttachmentRegistry.create(
                    RPGMod.id("rpg_player_data"),
                    builder -> builder
                            .initializer(() -> new RPGPlayerData(0))
                            .persistent(CODEC)
                            .copyOnDeath()
            );

    // Chamado no onInitialize só para forçar o carregamento da classe
    // (e com isso o registro do ATTACHMENT) antes de qualquer mundo abrir
    public static void initialize() {
        RPGMod.LOGGER.info("Registrando dados do jogador");
    }

    public static RPGPlayerData get(Player player) {
        return player.getAttachedOrCreate(ATTACHMENT);
    }

    public RPGPlayerData addExperience(int amount) {
        return new RPGPlayerData(experience + amount);
    }

    // Level é derivado, não salvo
    public int level() {
        return LevelTable.getLevel(experience);
    }
}
