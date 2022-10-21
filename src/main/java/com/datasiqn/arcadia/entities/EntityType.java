package com.datasiqn.arcadia.entities;

public enum EntityType {
    ZOMBIE(new EntityZombie("ZOMBIE")),
    UNDEAD_GUARDIAN(new EntityUndeadGuardian("UNDEAD_GUARDIAN")),
    IRON_GIANT(new EntityIronGiant("IRON_GIANT")),
    DUMMY(new EntityDummy("DUMMY"));

    private final ArcadiaEntitySummoner entity;

    EntityType(ArcadiaEntitySummoner summoner) {
        this.entity = summoner;
    }

    public ArcadiaEntitySummoner getSummoner() {
        return entity;
    }
}
