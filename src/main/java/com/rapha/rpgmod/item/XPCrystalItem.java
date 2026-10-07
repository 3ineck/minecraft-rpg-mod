package com.rapha.rpgmod.item;

import com.rapha.rpgmod.player.RPGPlayerData;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class XPCrystalItem extends Item {

    public XPCrystalItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(
            Level level,
            Player player,
            InteractionHand hand) {

        if (!level.isClientSide()) {

            RPGPlayerData data = RPGPlayerData.get(player);

            RPGPlayerData newData = data.addExperience(10);

            player.setAttached(
                    RPGPlayerData.ATTACHMENT,
                    newData
            );

            player.sendSystemMessage(
                    Component.literal(
                            "Você ganhou 10 XP! XP atual: "
                                    + newData.experience()
                    )
            );
        }

        return InteractionResult.SUCCESS;
    }
}