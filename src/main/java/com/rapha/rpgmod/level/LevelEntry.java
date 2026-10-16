package com.rapha.rpgmod.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LevelEntry(int level, int xp) {

    public static final Codec<LevelEntry> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("level")
                            .forGetter(LevelEntry::level),
                    Codec.INT.fieldOf("xp")
                            .forGetter(LevelEntry::xp)
            ).apply(instance, LevelEntry::new));
}