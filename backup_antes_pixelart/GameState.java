package com.example.bossrush;

import java.util.Map;

public class GameState {
    public static final int TOTAL_BOSSES = 9;
    public static final int MAX_COPIES_PER_CARD = 3;

    private final int difficultyBase;
    private int bossIndex;
    private int playerHp;
    private int playerMaxHp;
    private int bossHp;
    private int bossMaxHp;
    private String bossName;
    private boolean finalBossExtraPhase;
    private int healCharges;
    private int bossHealCooldown;
    private int bossLastHealAmount;
    private String bossAttackEffectName;
    private int bossAttackEffectDuration;
    private String bossDefenseEffectName;
    private int bossDefenseEffectDuration;
    private boolean shieldActive;
    private boolean lastShieldCriticalFailure;
    private int paralysisTurns;
    private boolean shieldCriticalTriggeredThisDefense;
    private int specialAttackCooldown;
    private int swordDamageBonus;
    private int shieldReflectBonus;
    private int specialDamageBonus;
    private int lifeStealBonus;
    private String swordStatusEffect;
    private int swordStatusDuration;
    private String swordStatusSourceCardName;
    private int swordStatusLevel;
    private String specialStatusEffect;
    private int specialStatusDuration;
    private String specialStatusSourceCardName;
    private int specialStatusLevel;
    private boolean melissaLifeBonusApplied;
    private static final int MAX_EFFECT_STACKS = 2;
    private final java.util.Map<String, Integer> bossEffects = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, Integer> bossEffectStacks = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, Integer> playerEffects = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, Integer> playerEffectStacks = new java.util.LinkedHashMap<>();
    private final java.util.List<BossCard> playerCards = new java.util.ArrayList<>();
    private String bossColorHex = "#FCA5A5"; // default boss color
    private int bossAttackPattern = 0; // pattern id used by MainActivity to vary attack behavior
    // Session-level total bosses allows runtime expansion (e.g., via special codes)
    private int sessionTotalBosses = TOTAL_BOSSES;

    public GameState(int startingBossIndex, int startingPlayerMaxHp) {
        this.difficultyBase = startingBossIndex;
        this.bossIndex = startingBossIndex;
        this.playerMaxHp = Math.max(1, startingPlayerMaxHp);
        this.playerHp = playerMaxHp;
        this.healCharges = 3;
        this.shieldActive = false;
        this.specialAttackCooldown = 0;
        // sessionTotalBosses defaults to TOTAL_BOSSES; can be overridden by caller
        setupBoss();
    }

    // Backwards-compatible constructor (default starting HP = 100)
    public GameState(int startingBossIndex) {
        this(startingBossIndex, 100);
    }

    public void prepareNextBoss() {
        bossIndex += 1;
        playerHp = Math.min(playerMaxHp, playerHp + 18);
        healCharges = 3;
        bossHealCooldown = 0;
        bossLastHealAmount = 0;
        shieldActive = false;
        specialAttackCooldown = 0;
        clearBossEffects();
        clearPlayerEffects();
        setupBoss();
    }

    private void setupBoss() {
        // Use sessionTotalBosses to determine which boss is the session final boss
        finalBossExtraPhase = (bossIndex == sessionTotalBosses - 1);
        bossName = "Boss " + (bossIndex + 1);
        bossMaxHp = 90 + (bossIndex * 35);

        // Assign color and attack pattern per boss index to make them distinct
        switch (bossIndex) {
            case 0:
                bossColorHex = "#FCA5A5"; bossAttackPattern = 0; break;
            case 1:
                bossColorHex = "#FDE68A"; bossAttackPattern = 1; break;
            case 2:
                bossColorHex = "#93C5FD"; bossAttackPattern = 2; break;
            case 3:
                bossColorHex = "#C7F9CC"; bossAttackPattern = 3; break;
            case 4:
                bossColorHex = "#FBCFE8"; bossAttackPattern = 1; break;
            case 5:
                bossColorHex = "#E6E6FA"; bossAttackPattern = 2; break;
            case 6:
                bossColorHex = "#FFD1A4"; bossAttackPattern = 3; break;
            case 7:
                bossColorHex = "#D1FAE5"; bossAttackPattern = 4; break;
            default:
                bossColorHex = "#F87171"; bossAttackPattern = 5; break;
        }

        if (finalBossExtraPhase) {
            bossName = "Boss " + (bossIndex + 1) + " (Fase Extra)";
            bossMaxHp += 120;
        }

        configureBossSkills();
        bossHp = bossMaxHp;
        bossHealCooldown = 0;
        bossLastHealAmount = 0;
        clearBossEffects();
    }

    public int getBossIndex() {
        return bossIndex;
    }

    public int getPlayerHp() {
        return playerHp;
    }

    public int getPlayerMaxHp() {
        return playerMaxHp;
    }

    public int getBossHp() {
        return bossHp;
    }

    public int getBossMaxHp() {
        return bossMaxHp;
    }

    public String getBossName() {
        return bossName;
    }

    public boolean isFinalBoss() {
        return finalBossExtraPhase;
    }

    public boolean hasExtraPhase() {
        return finalBossExtraPhase;
    }

    public int getHealCharges() {
        return healCharges;
    }

    public boolean isShieldActive() {
        return shieldActive;
    }

    public boolean wasLastShieldCriticalFailure() {
        return lastShieldCriticalFailure;
    }

    public boolean isPlayerParalyzed() {
        return paralysisTurns > 0;
    }

    public int getParalysisTurns() {
        return paralysisTurns;
    }

    public void consumeParalysisTurn() {
        if (paralysisTurns <= 0) {
            return;
        }
        paralysisTurns -= 1;
        if (paralysisTurns <= 0) {
            removePlayerEffect("Falha Crítica");
        }
    }

    public void activateShield() {
        shieldActive = true;
        lastShieldCriticalFailure = false;
        shieldCriticalTriggeredThisDefense = false;
    }

    public void deactivateShield() {
        shieldActive = false;
        lastShieldCriticalFailure = false;
        shieldCriticalTriggeredThisDefense = false;
    }

    public int getSpecialAttackCooldown() {
        return specialAttackCooldown;
    }

    public boolean isSpecialAttackReady() {
        return specialAttackCooldown == 0;
    }

    public boolean useSpecialAttack() {
        if (!isSpecialAttackReady()) {
            return false;
        }
        specialAttackCooldown = 5;
        return true;
    }

    public void advanceTurn() {
        if (specialAttackCooldown > 0) {
            specialAttackCooldown -= 1;
        }
        if (bossHealCooldown > 0) {
            bossHealCooldown -= 1;
        }
        shieldActive = false;
        if (paralysisTurns > 0) {
            paralysisTurns -= 1;
            if (paralysisTurns <= 0) {
                removePlayerEffect("Falha Crítica");
            }
        }
        tickBossEffects();
        tickPlayerEffects();
    }

    public int applyEnemyAttack(int rawDamage) {
        return applyEnemyAttack(rawDamage, null);
    }

    public int applyEnemyAttack(int rawDamage, java.util.Random random) {
        int reflectedDamage = 0;
        int damageToPlayer = rawDamage;
        lastShieldCriticalFailure = false;

        if (shieldActive) {
            if (!shieldCriticalTriggeredThisDefense && random != null && random.nextInt(100) < 10) {
                shieldCriticalTriggeredThisDefense = true;
                lastShieldCriticalFailure = true;
                shieldActive = false;
                damageToPlayer = rawDamage * 2;
                playerHp = Math.max(0, playerHp - damageToPlayer);
                paralysisTurns = 1;
                applyPlayerEffect("Falha Crítica", 1);
                return 0;
            }

            int reflectPercent = Math.min(100, Math.max(20, 20 + shieldReflectBonus));
            reflectedDamage = Math.max(0, (int) Math.round(rawDamage * (reflectPercent / 100.0)));
            damageToPlayer = Math.max(0, rawDamage - reflectedDamage);
            bossHp = Math.max(0, bossHp - reflectedDamage);
            shieldActive = false;
        }

        playerHp = Math.max(0, playerHp - damageToPlayer);
        return reflectedDamage;
    }

    public void applyCard(BossCard card) {
        if (card == null) {
            return;
        }

        if ("Espada".equals(card.getTargetSkill())) {
            swordDamageBonus += card.getDamageBonus();
            lifeStealBonus += card.getLifeStealBonus();
            if (card.getStatusEffectName() != null && !card.getStatusEffectName().trim().isEmpty()) {
                swordStatusEffect = card.getStatusEffectName();
                swordStatusSourceCardName = card.getName();
                swordStatusLevel = getPlayerCardLevel(card.getName());
                // When acquiring multiple cards that affect the same status, add durations instead of replacing smaller ones
                if (swordStatusDuration > 0) {
                    swordStatusDuration += Math.max(1, card.getStatusEffectDuration());
                } else {
                    swordStatusDuration = Math.max(1, card.getStatusEffectDuration());
                }
            }
        } else if ("Escudo".equals(card.getTargetSkill())) {
            shieldReflectBonus += card.getShieldBonus();
        } else if ("Ataque especial".equals(card.getTargetSkill())) {
            specialDamageBonus += card.getSpecialDamageBonus();
            if (card.getStatusEffectName() != null && !card.getStatusEffectName().trim().isEmpty()) {
                specialStatusEffect = card.getStatusEffectName();
                specialStatusSourceCardName = card.getName();
                specialStatusLevel = getPlayerCardLevel(card.getName());
                if (specialStatusDuration > 0) {
                    specialStatusDuration += Math.max(1, card.getStatusEffectDuration());
                } else {
                    specialStatusDuration = Math.max(1, card.getStatusEffectDuration());
                }
            }
        }
    }

    public int getSwordDamageBonus() {
        return swordDamageBonus;
    }

    public int getShieldReflectBonus() {
        return shieldReflectBonus;
    }

    public int getSpecialDamageBonus() {
        return specialDamageBonus;
    }

    public int getLifeStealBonus() {
        return lifeStealBonus;
    }

    public int getEffectiveSwordDamage(int baseDamage) {
        return baseDamage + swordDamageBonus;
    }

    public int getEffectiveSpecialDamage(int baseDamage) {
        return baseDamage + specialDamageBonus;
    }

    public int getShieldReflectPercent() {
        return Math.min(100, Math.max(20, 20 + shieldReflectBonus));
    }

    public boolean hasSwordStatusEffect() {
        return swordStatusEffect != null && !swordStatusEffect.trim().isEmpty();
    }

    public boolean hasSpecialStatusEffect() {
        return specialStatusEffect != null && !specialStatusEffect.trim().isEmpty();
    }

    public String getSwordStatusEffect() {
        return swordStatusEffect;
    }

    public String getSpecialStatusEffect() {
        return specialStatusEffect;
    }

    public int getSwordStatusDuration() {
        return swordStatusDuration;
    }

    public int getSpecialStatusDuration() {
        return specialStatusDuration;
    }

    public int getStatusTriggerChance(int cardLevel) {
        int normalizedLevel = Math.max(0, Math.min(3, cardLevel));
        return Math.min(30, normalizedLevel * 10);
    }

    public int getPlayerCardLevel(String cardName) {
        return Math.min(MAX_COPIES_PER_CARD, getPlayerCardCopies(cardName));
    }

    public int getSwordStatusLevel() {
        if (swordStatusSourceCardName == null || swordStatusSourceCardName.trim().isEmpty()) {
            return 0;
        }
        return Math.min(MAX_COPIES_PER_CARD, getPlayerCardCopies(swordStatusSourceCardName));
    }

    public int getSpecialStatusLevel() {
        if (specialStatusSourceCardName == null || specialStatusSourceCardName.trim().isEmpty()) {
            return 0;
        }
        return Math.min(MAX_COPIES_PER_CARD, getPlayerCardCopies(specialStatusSourceCardName));
    }

    public boolean triggerSwordStatusEffect() {
        return triggerSwordStatusEffect(new java.util.Random());
    }

    public boolean triggerSwordStatusEffect(java.util.Random random) {
        if (hasSwordStatusEffect()) {
            int level = getSwordStatusLevel();
            int chance = getStatusTriggerChance(level);
            if (random == null) {
                random = new java.util.Random();
            }
            if (random.nextInt(100) < chance) {
                applyBossEffect(swordStatusEffect, swordStatusDuration);
                return true;
            }
        }
        return false;
    }

    public boolean triggerSpecialStatusEffect() {
        return triggerSpecialStatusEffect(new java.util.Random());
    }

    public boolean triggerSpecialStatusEffect(java.util.Random random) {
        if (hasSpecialStatusEffect()) {
            int level = getSpecialStatusLevel();
            int chance = getStatusTriggerChance(level);
            if (random == null) {
                random = new java.util.Random();
            }
            if (random.nextInt(100) < chance) {
                applyBossEffect(specialStatusEffect, specialStatusDuration);
                return true;
            }
        }
        return false;
    }

    public void applyEnemyEffect(String effectName, int duration) {
        applyBossEffect(effectName, duration);
    }

    // Allow activity to override the session total bosses so final boss detection adapts
    public void setSessionTotalBosses(int total) {
        if (total < TOTAL_BOSSES) {
            this.sessionTotalBosses = TOTAL_BOSSES;
        } else {
            this.sessionTotalBosses = total;
        }
        // When session total changes, re-evaluate final boss flag for current boss
        this.finalBossExtraPhase = (bossIndex == this.sessionTotalBosses - 1);
        // If current boss became the session final, ensure HP bonus is applied
        if (this.finalBossExtraPhase) {
            // ensure extra phase HP bump is present (idempotent)
            this.bossName = "Boss " + (bossIndex + 1) + " (Fase Extra)";
            this.bossMaxHp = Math.max(this.bossMaxHp, 90 + (bossIndex * 35) + 120);
            this.bossHp = Math.max(this.bossHp, this.bossMaxHp);
        }
    }

    public int getSessionTotalBosses() {
        return this.sessionTotalBosses;
    }

    public void applyBossEffect(String effectName, int duration) {
        if (effectName == null || effectName.trim().isEmpty()) {
            return;
        }
        String normalized = effectName.trim();
        int currentStacks = bossEffectStacks.getOrDefault(normalized, 0);
        if (currentStacks >= MAX_EFFECT_STACKS) {
            return;
        }
        bossEffectStacks.put(normalized, currentStacks + 1);

        int add = Math.max(1, duration);
        int prev = bossEffects.getOrDefault(normalized, 0);
        bossEffects.put(normalized, prev + add);
    }

    public void removeBossEffect(String effectName) {
        if (effectName == null) {
            return;
        }
        String normalized = effectName.trim();
        bossEffects.remove(normalized);
        bossEffectStacks.remove(normalized);
    }

    public void clearBossEffects() {
        bossEffects.clear();
        bossEffectStacks.clear();
    }

    public java.util.Map<String, Integer> getBossEffects() {
        return new java.util.LinkedHashMap<>(bossEffects);
    }

    public int getBossEffectDuration(String effectName) {
        return bossEffects.getOrDefault(effectName, 0);
    }

    public int getBossEffectStackCount(String effectName) {
        if (effectName == null) {
            return 0;
        }
        return bossEffectStacks.getOrDefault(effectName.trim(), 0);
    }

    public boolean hasBossEffect(String effectName) {
        return bossEffects.containsKey(effectName);
    }

    public java.util.Map<String, Integer> getEnemyEffects() {
        return getBossEffects();
    }

    public int getEnemyEffectDuration(String effectName) {
        return getBossEffectDuration(effectName);
    }

    public boolean hasEnemyEffect(String effectName) {
        return hasBossEffect(effectName);
    }

    public void clearEnemyEffects() {
        clearBossEffects();
    }

    public String getBossColorHex() {
        return bossColorHex;
    }

    public int getBossAttackPattern() {
        return bossAttackPattern;
    }

    public String getBossAttackEffectName() {
        return bossAttackEffectName;
    }

    public int getBossAttackEffectDuration() {
        return bossAttackEffectDuration;
    }

    public String getBossDefenseEffectName() {
        return bossDefenseEffectName;
    }

    public int getBossDefenseEffectDuration() {
        return bossDefenseEffectDuration;
    }

    public int getBossHealCooldown() {
        return bossHealCooldown;
    }

    public int getBossLastHealAmount() {
        return bossLastHealAmount;
    }

    public boolean canBossUseHeal() {
        return bossHealCooldown <= 0 && bossHp > 0 && bossHp < bossMaxHp;
    }

    public void configureBossSkills() {
        switch (bossIndex) {
            case 0:
                bossAttackEffectName = "Queimadura"; bossAttackEffectDuration = 2; bossDefenseEffectName = "Escudo"; bossDefenseEffectDuration = 2; break;
            case 1:
                bossAttackEffectName = "Veneno"; bossAttackEffectDuration = 2; bossDefenseEffectName = "Cura"; bossDefenseEffectDuration = 2; break;
            case 2:
                bossAttackEffectName = "Dano de Void"; bossAttackEffectDuration = 2; bossDefenseEffectName = "Escudo"; bossDefenseEffectDuration = 2; break;
            case 3:
                bossAttackEffectName = "Roubo de vida"; bossAttackEffectDuration = 2; bossDefenseEffectName = "Cura"; bossDefenseEffectDuration = 2; break;
            case 4:
                bossAttackEffectName = "Queimadura"; bossAttackEffectDuration = 3; bossDefenseEffectName = "Escudo"; bossDefenseEffectDuration = 2; break;
            case 5:
                bossAttackEffectName = "Veneno"; bossAttackEffectDuration = 3; bossDefenseEffectName = "Cura"; bossDefenseEffectDuration = 2; break;
            case 6:
                bossAttackEffectName = "Dano de Void"; bossAttackEffectDuration = 3; bossDefenseEffectName = "Escudo"; bossDefenseEffectDuration = 3; break;
            case 7:
                bossAttackEffectName = "Roubo de vida"; bossAttackEffectDuration = 3; bossDefenseEffectName = "Cura"; bossDefenseEffectDuration = 3; break;
            default:
                bossAttackEffectName = "Queimadura"; bossAttackEffectDuration = 2; bossDefenseEffectName = "Escudo"; bossDefenseEffectDuration = 2; break;
        }
    }

    public void useBossDefense() {
        if (bossDefenseEffectName == null || bossDefenseEffectName.trim().isEmpty()) {
            return;
        }
        if (bossDefenseEffectName.equals("Cura")) {
            int heal = Math.min(10, bossMaxHp - bossHp);
            if (heal > 0) {
                bossHp = Math.min(bossMaxHp, bossHp + heal);
            }
            applyBossEffect("Cura", bossDefenseEffectDuration);
        } else {
            applyBossEffect(bossDefenseEffectName, bossDefenseEffectDuration);
        }
    }

    public void applyRandomBossEffects(java.util.Random random) {
        clearBossEffects();
        if (random == null) {
            random = new java.util.Random();
        }

        java.util.List<String> pool = new java.util.ArrayList<>();
        pool.add("Queimadura");
        pool.add("Veneno");
        pool.add("Escudo");
        pool.add("Cura");
        pool.add("Roubo de vida");

        java.util.Collections.shuffle(pool, random);
        int count = 1 + (random.nextInt(100) < 50 ? 1 : 0);
        count = Math.min(count, pool.size());
        for (int i = 0; i < count; i++) {
            String effect = pool.get(i);
            int duration = 2 + random.nextInt(2);
            applyBossEffect(effect, duration);
        }
    }

    public int useBossHeal(java.util.Random random) {
        if (!canBossUseHeal()) {
            return 0;
        }
        if (random == null) {
            random = new java.util.Random();
        }
        int healAmount = 8 + random.nextInt(12);
        healAmount = Math.min(healAmount, bossMaxHp - bossHp);
        if (healAmount <= 0) {
            return 0;
        }
        bossHp = Math.min(bossMaxHp, bossHp + healAmount);
        bossHealCooldown = 3;
        bossLastHealAmount = healAmount;
        return healAmount;
    }

    public void applyPlayerEffect(String effectName, int duration) {
        if (effectName == null || effectName.trim().isEmpty()) {
            return;
        }
        String normalized = effectName.trim();
        int currentStacks = playerEffectStacks.getOrDefault(normalized, 0);
        if (currentStacks >= MAX_EFFECT_STACKS) {
            return;
        }
        playerEffectStacks.put(normalized, currentStacks + 1);

        int add = Math.max(1, duration);
        int prev = playerEffects.getOrDefault(normalized, 0);
        playerEffects.put(normalized, prev + add);
    }

    public void addSwordDamageBonus(int amount) {
        swordDamageBonus += Math.max(0, amount);
    }

    public void addShieldReflectBonus(int amount) {
        shieldReflectBonus += Math.max(0, amount);
    }

    public void addSpecialDamageBonus(int amount) {
        specialDamageBonus += Math.max(0, amount);
    }

    public void increasePlayerMaxHp(int amount) {
        int delta = amount;
        if (delta > 0) {
            playerMaxHp += delta;
            playerHp = Math.min(playerMaxHp, playerHp + delta);
        } else if (delta < 0) {
            playerMaxHp = Math.max(1, playerMaxHp + delta);
            playerHp = Math.min(playerHp, playerMaxHp);
        }
    }

    public void applyMelissaLifeBonusOnce() {
        if (melissaLifeBonusApplied) {
            return;
        }
        increasePlayerMaxHp(100);
        melissaLifeBonusApplied = true;
    }

    public void addHealCharge(int amount) {
        healCharges = Math.min(3, Math.max(0, healCharges + amount));
    }

    public void removePlayerEffect(String effectName) {
        if (effectName == null) {
            return;
        }
        String normalized = effectName.trim();
        playerEffects.remove(normalized);
        playerEffectStacks.remove(normalized);
    }

    public void clearPlayerEffects() {
        playerEffects.clear();
        playerEffectStacks.clear();
        paralysisTurns = 0;
    }

    public java.util.Map<String, Integer> getPlayerEffects() {
        return new java.util.LinkedHashMap<>(playerEffects);
    }

    public int getPlayerEffectDuration(String effectName) {
        return playerEffects.getOrDefault(effectName, 0);
    }

    public int getPlayerEffectStackCount(String effectName) {
        if (effectName == null) {
            return 0;
        }
        return playerEffectStacks.getOrDefault(effectName.trim(), 0);
    }

    public boolean hasPlayerEffect(String effectName) {
        return playerEffects.containsKey(effectName);
    }

    public boolean isPlayerStunned() {
        return hasPlayerEffect("Atordoou") || getPlayerEffectDuration("Atordoou") > 0;
    }

    public int getStunnedAttackDamage(int baseDamage) {
        return Math.max(0, (int)Math.round(Math.max(0, baseDamage) * 0.4));
    }

    // Player accumulated cards management (limit enforced)
    public int getPlayerCardCount() {
        return playerCards.size();
    }

    public int getMaxCopiesPerCard() {
        return MAX_COPIES_PER_CARD;
    }

    public int getPlayerCardCopies(String cardName) {
        if (cardName == null) return 0;
        int count = 0;
        for (BossCard c : playerCards) {
            if (cardName.equals(c.getName())) count++;
        }
        return count;
    }

    public boolean canAddPlayerCard(BossCard card) {
        if (card == null) return false;
        return getPlayerCardCopies(card.getName()) < MAX_COPIES_PER_CARD;
    }

    public boolean addPlayerCard(BossCard card) {
        if (card == null) return false;
        if (!canAddPlayerCard(card)) return false;
        playerCards.add(card);
        return true;
    }

    public boolean removePlayerCard(BossCard card) {
        if (card == null) return false;
        return playerCards.remove(card);
    }

    public void clearPlayerCards() {
        playerCards.clear();
    }

    public java.util.List<BossCard> getPlayerCards() {
        return new java.util.ArrayList<>(playerCards);
    }

    private void tickBossEffects() {
        // Apply per-turn consequences for boss effects (damage over time etc.)
        java.util.Iterator<Map.Entry<String, Integer>> iterator = bossEffects.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            String name = entry.getKey();
            String lower = name == null ? "" : name.toLowerCase();
            if (lower.contains("queim") || lower.contains("fogo")) {
                bossHp = Math.max(0, bossHp - 6);
            } else if (lower.contains("veneno") || lower.contains("ven") ) {
                bossHp = Math.max(0, bossHp - 5);
            } else if (lower.contains("void") || lower.contains("dano de void")) {
                bossHp = Math.max(0, bossHp - 8);
            }
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                iterator.remove();
                bossEffectStacks.remove(name);
            } else {
                entry.setValue(remaining);
            }
        }
    }

    private void tickPlayerEffects() {
        // Apply per-turn consequences for player effects (damage over time etc.)
        java.util.Iterator<Map.Entry<String, Integer>> iterator = playerEffects.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            String name = entry.getKey();
            String lower = name == null ? "" : name.toLowerCase();
            if (lower.contains("queim") || lower.contains("fogo")) {
                playerHp = Math.max(0, playerHp - 6);
            } else if (lower.contains("veneno") || lower.contains("ven")) {
                playerHp = Math.max(0, playerHp - 5);
            } else if (lower.contains("void") || lower.contains("dano de void")) {
                playerHp = Math.max(0, playerHp - 8);
            }
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                iterator.remove();
                playerEffectStacks.remove(name);
            } else {
                entry.setValue(remaining);
            }
        }
    }

    private void tickEffects(java.util.Map<String, Integer> effects) {
        // Kept for compatibility; not used directly for ticking now.
        java.util.Iterator<Map.Entry<String, Integer>> iterator = effects.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                iterator.remove();
            } else {
                entry.setValue(remaining);
            }
        }
    }

    public void setPlayerHp(int playerHp) {
        this.playerHp = Math.max(0, Math.min(playerMaxHp, playerHp));
    }

    public void setBossHp(int bossHp) {
        this.bossHp = Math.max(0, Math.min(bossMaxHp, bossHp));
    }

    public void spendHeal() {
        if (healCharges > 0) {
            healCharges -= 1;
        }
    }

    public void restoreHealCharge() {
        healCharges = Math.min(3, healCharges + 1);
    }

    public void restorePlayerHp(int amount) {
        this.playerHp = Math.min(playerMaxHp, this.playerHp + amount);
    }

    public boolean isBossDefeated() {
        return bossHp <= 0;
    }

    public boolean isPlayerDefeated() {
        return playerHp <= 0;
    }

    public int getDifficultyBase() {
        return difficultyBase;
    }
}
