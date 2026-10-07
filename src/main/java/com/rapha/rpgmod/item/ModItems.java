package com.rapha.rpgmod.item;

import com.rapha.rpgmod.RPGMod;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import java.util.function.Function;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class ModItems {

  // Helper para criar e registrar um Item
  public static Item register(
          ResourceKey<Item> itemKey,
          Function<Item.Properties, Item> itemFactory,
          Item.Properties settings) {

      // Cria a instância do item.
      Item item = itemFactory.apply(settings.setId(itemKey));

      // Registra o item no Minecraft.
      Registry.register(BuiltInRegistries.ITEM, itemKey, item);

      return item;
  }

  public static final ResourceKey<Item> XP_CRYSTAL = ModItemIds.create("xp_crystal");

  public static final Item XP_CRYSTAL_ITEM =
    register(
            XP_CRYSTAL,
            XPCrystalItem::new,
            new Item.Properties()
    );

	public static void initialize() {
		RPGMod.LOGGER.info("Registrando itens do RPG Mod");

    CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(entries -> entries.accept(XP_CRYSTAL_ITEM));
	}
}