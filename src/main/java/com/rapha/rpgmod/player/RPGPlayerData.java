package com.rapha.rpgmod.player;

public class RPGPlayerData {

    private int experience;

    public RPGPlayerData() {
        this.experience = 0;
    }

    public int getExperience() {
        return experience;
    }

    public void addExperience(int amount) {
        experience += amount;
    }
}