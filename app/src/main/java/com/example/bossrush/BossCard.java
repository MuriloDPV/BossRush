package com.example.bossrush;

public class BossCard {
    private final String name;
    private final String description;
    private final String targetSkill;
    private final int damageBonus;
    private final int shieldBonus;
    private final int specialDamageBonus;
    private final int lifeStealBonus;
    private final String statusEffectName;
    private final int statusEffectDuration;

    public BossCard(String name, String description, String targetSkill, int damageBonus,
                    int shieldBonus, int specialDamageBonus, int lifeStealBonus,
                    String statusEffectName, int statusEffectDuration) {
        this.name = name;
        this.description = description;
        this.targetSkill = targetSkill;
        this.damageBonus = damageBonus;
        this.shieldBonus = shieldBonus;
        this.specialDamageBonus = specialDamageBonus;
        this.lifeStealBonus = lifeStealBonus;
        this.statusEffectName = statusEffectName;
        this.statusEffectDuration = statusEffectDuration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTargetSkill() {
        return targetSkill;
    }

    public int getDamageBonus() {
        return damageBonus;
    }

    public int getShieldBonus() {
        return shieldBonus;
    }

    public int getSpecialDamageBonus() {
        return specialDamageBonus;
    }

    public int getLifeStealBonus() {
        return lifeStealBonus;
    }

    public String getStatusEffectName() {
        return statusEffectName;
    }

    public int getStatusEffectDuration() {
        return statusEffectDuration;
    }
}
