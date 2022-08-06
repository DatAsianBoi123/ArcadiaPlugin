package com.datasiqn.arcadia.entities;

public enum EntityType {
    ZOMBIE(new EntityZombie()),
    UNDEAD_GUARDIAN(new EntityUndeadGuardian()),
    IRON_GIANT(new EntityIronGiant()),
    DUMMY(new EntityDummy());

    private final ArcadiaEntity entity;

    EntityType(ArcadiaEntity entity) {
        this.entity = entity;
    }

    public ArcadiaEntity getEntity() {
        return entity;
    }
}
