package com.datasiqn.arcadia.player;

public class Experience {
    private long amount;
    private int level;
    private double progress;

    public Experience() {
        this.amount = 0;
        this.level = 0;
        this.progress = 0;
    }
    public Experience(long amount) {
        this.amount = amount;
        calculateLevel();
        calculateProgress();
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
        calculateLevel();
        calculateProgress();
    }

    public int getLevel() {
        return level;
    }

    public double getProgress() {
        return progress;
    }

    public void calculateLevel() {
        level = (int) Math.floor(Math.sqrt(amount / 100d));
    }

    public void calculateProgress() {
        int level = getLevel();
        progress = (amount - getTotalXpNeeded(level)) / (double) getXpForLevel(level + 1);
    }

    public static long getTotalXpNeeded(int level) {
        return (long) (100 * Math.pow(level, 2));
    }

    public static long getXpForLevel(int level) {
        return (long) 200 * level - 100;
    }
}
