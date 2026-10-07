package com.rapha.rpgmod.player;

import com.rapha.rpgmod.RPGMod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.player.Player;

public record RPGPlayerData(
        int level,
        int experience
) {

    public static final Codec<RPGPlayerData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("level")
                            .forGetter(RPGPlayerData::level),
                    Codec.INT.fieldOf("experience")
                            .forGetter(RPGPlayerData::experience)
            ).apply(instance, RPGPlayerData::new));

    public static final AttachmentType<RPGPlayerData> ATTACHMENT =
            AttachmentRegistry.create(
                    RPGMod.id("rpg_player_data"),
                    builder -> builder
                            .initializer(() -> new RPGPlayerData(1, 0))
                            .persistent(CODEC)
                            .copyOnDeath()
            );

    public static RPGPlayerData get(Player player) {
        return player.getAttachedOrCreate(ATTACHMENT);
    }

    public RPGPlayerData addExperience(int amount) {
        return new RPGPlayerData(
                level,
                experience + amount
        );
    }
}