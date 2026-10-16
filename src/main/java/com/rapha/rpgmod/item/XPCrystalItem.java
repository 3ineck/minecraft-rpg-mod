package com.rapha.rpgmod.item;

import com.rapha.rpgmod.level.LevelTable;
import com.rapha.rpgmod.player.RPGPlayerData;
import com.rapha.rpgmod.player.XPBarManager;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.OptionalInt;

public class XPCrystalItem extends Item {

    private static final int XP_AMOUNT = 10;

    public XPCrystalItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(
            Level level,
            Player player,
            InteractionHand hand) {

        if (!level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);

            RPGPlayerData data = RPGPlayerData.get(player);
            int levelBefore = data.level();

            RPGPlayerData newData = data.addExperience(XP_AMOUNT);
            player.setAttached(RPGPlayerData.ATTACHMENT, newData);

            if (player instanceof ServerPlayer serverPlayer) {
                XPBarManager.update(serverPlayer);
            }

            // Remove 1 do stack (não remove no modo criativo)
            stack.consume(1, player);

            int levelAfter = newData.level();

            if (levelAfter > levelBefore) {
                player.sendSystemMessage(Component.literal(
                        "Você subiu para o level " + levelAfter + "!"));
            } else {
                OptionalInt remaining = LevelTable.getXpToNextLevel(newData.experience());

                if (remaining.isPresent()) {
                    player.sendSystemMessage(Component.literal(
                            "+" + XP_AMOUNT + " XP (faltam " + remaining.getAsInt()
                                    + " para o level " + (levelAfter + 1) + ")"));
                } else {
                    player.sendSystemMessage(Component.literal(
                            "+" + XP_AMOUNT + " XP (level máximo)"));
                }
            }
        }

        return InteractionResult.SUCCESS;
    }
}
