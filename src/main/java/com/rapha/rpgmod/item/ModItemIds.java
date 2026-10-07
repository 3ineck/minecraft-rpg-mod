package com.rapha.rpgmod.item;

import com.rapha.rpgmod.RPGMod;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;

public class ModItemIds {

    public static ResourceKey<Item> create(String name) {
        return ResourceKey.create(
                Registries.ITEM,
                RPGMod.id(name)
        );
    }
}