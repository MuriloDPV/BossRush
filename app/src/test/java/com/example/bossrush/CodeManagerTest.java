package com.example.bossrush;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.SharedPreferences;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CodeManagerTest {

    @Test
    public void redeemCodeUnlocksAndToggleWorks() {
        SharedPreferences prefs = new FakeSharedPreferences();
        CodeManager manager = new CodeManager(prefs);

        assertTrue(manager.redeemCode("zatta"));
        assertTrue(manager.isUnlocked("zatta"));
        assertFalse(manager.isActive("zatta"));

        assertTrue(manager.toggleActivation("zatta"));
        assertTrue(manager.isActive("zatta"));

        assertTrue(manager.toggleActivation("zatta"));
        assertFalse(manager.isActive("zatta"));
    }

    @Test
    public void invalidCodeIsRejected() {
        SharedPreferences prefs = new FakeSharedPreferences();
        CodeManager manager = new CodeManager(prefs);

        assertFalse(manager.redeemCode("codigo_inexistente"));
        assertFalse(manager.isUnlocked("codigo_inexistente"));
    }

    @Test
    public void muriloCodeExpandsBossTotal() {
        SharedPreferences prefs = new FakeSharedPreferences();
        CodeManager manager = new CodeManager(prefs);

        manager.redeemCode("murilo");
        manager.toggleActivation("murilo");

        assertEquals(12, manager.getEffectiveTotalBosses(9));
        GameState state = new GameState(0, 100);
        manager.applyActiveEffects(state);
        assertEquals(12, state.getSessionTotalBosses());
    }

    @Test
    public void stunChanceScalesWithCardLevel() {
        GameState state = new GameState(0, 100);
        BossCard stunCard = new BossCard(
                "Choque de Stun",
                "Ataque: +6 de dano e Atordoou o inimigo por 1 turno.",
                "Espada",
                6,
                0,
                0,
                0,
                "Atordoou",
                1
        );

        state.addPlayerCard(stunCard);
        state.applyCard(stunCard);
        assertEquals(10, state.getStatusTriggerChance(1));
        assertEquals(10, state.getStatusTriggerChance(state.getPlayerCardLevel(stunCard.getName())));

        state.addPlayerCard(stunCard);
        state.applyCard(stunCard);
        assertEquals(20, state.getStatusTriggerChance(state.getPlayerCardLevel(stunCard.getName())));

        state.addPlayerCard(stunCard);
        state.applyCard(stunCard);
        assertEquals(30, state.getStatusTriggerChance(state.getPlayerCardLevel(stunCard.getName())));
    }

    @Test
    public void normalAttackUsesBaseDamageOfFifteenBeforeBonuses() {
        GameState state = new GameState(0, 100);

        assertEquals(15, state.getEffectiveSwordDamage(15));
        state.addSwordDamageBonus(4);
        assertEquals(19, state.getEffectiveSwordDamage(15));
    }

    @Test
    public void melissaCodeAddsHealthAndDamage() {
        SharedPreferences prefs = new FakeSharedPreferences();
        CodeManager manager = new CodeManager(prefs);
        GameState state = new GameState(0, 100);

        manager.redeemCode("melissa");
        manager.toggleActivation("melissa");
        manager.applyActiveEffects(state);

        assertEquals(200, state.getPlayerMaxHp());
        assertEquals(200, state.getPlayerHp());
        assertTrue(state.getSwordDamageBonus() > 0);
        assertTrue(state.getSpecialDamageBonus() > 0);

        GameState secondState = new GameState(0, 100);
        manager.applyActiveEffects(secondState);
        assertEquals(200, secondState.getPlayerMaxHp());
    }

    @Test
    public void shieldCriticalFailureAppliesDoubleDamageAndParalysis() {
        GameState state = new GameState(0, 100);
        state.activateShield();

        int reflected = state.applyEnemyAttack(10, new java.util.Random() {
            @Override
            public int nextInt(int bound) {
                return 0;
            }
        });

        assertEquals(0, reflected);
        assertTrue(state.isPlayerParalyzed());
        assertEquals(1, state.getParalysisTurns());
        assertEquals(80, state.getPlayerHp());
        assertEquals(1, state.getPlayerEffectStackCount("Falha Crítica"));

        state.consumeParalysisTurn();
        assertFalse(state.isPlayerParalyzed());
        assertEquals(0, state.getPlayerEffectStackCount("Falha Crítica"));
    }

    @Test
    public void shieldNormalDefenseReflectsWithoutCriticalFailure() {
        GameState state = new GameState(0, 100);
        state.activateShield();

        int reflected = state.applyEnemyAttack(10, new java.util.Random() {
            @Override
            public int nextInt(int bound) {
                return 99;
            }
        });

        assertEquals(2, reflected);
        assertFalse(state.isPlayerParalyzed());
        assertEquals(92, state.getPlayerHp());
        assertEquals(0, state.getPlayerEffectStackCount("Falha Crítica"));
    }

    @Test
    public void stunnedPlayerAttackIsReducedAndNoExtraEffectsApply() {
        GameState state = new GameState(0, 100);
        state.applyPlayerEffect("Atordoou", 2);

        assertTrue(state.isPlayerStunned());
        assertEquals(10, state.getStunnedAttackDamage(25));

        state.applyPlayerEffect("Queimadura", 1);
        assertEquals(1, state.getPlayerEffectStackCount("Queimadura"));
    }

    @Test
    public void bossRandomEffectsRespectStackLimitAndDurations() {
        GameState state = new GameState(0, 100);
        state.applyRandomBossEffects(new java.util.Random(7));

        assertTrue(!state.getBossEffects().isEmpty());
        for (String effectName : state.getBossEffects().keySet()) {
            assertTrue(state.getBossEffectStackCount(effectName) <= 2);
            assertTrue(state.getBossEffectDuration(effectName) > 0);
        }
    }

    @Test
    public void bossHealUsesCooldownAndRespectsMaxHp() {
        GameState state = new GameState(0, 100);
        state.setBossHp(30);

        int healed = state.useBossHeal(new java.util.Random(2));
        assertTrue(healed > 0);
        assertEquals(3, state.getBossHealCooldown());
        assertTrue(state.getBossHp() <= state.getBossMaxHp());

        int second = state.useBossHeal(new java.util.Random(2));
        assertEquals(0, second);

        state.advanceTurn();
        assertEquals(2, state.getBossHealCooldown());
    }

    @Test
    public void sameEffectDoesNotExceedTwoStacks() {
        GameState state = new GameState(0, 100);

        state.applyBossEffect("Queimadura", 2);
        state.applyBossEffect("Queimadura", 2);
        state.applyBossEffect("Queimadura", 2);

        assertEquals(2, state.getBossEffectStackCount("Queimadura"));

        state.applyPlayerEffect("Veneno", 3);
        state.applyPlayerEffect("Veneno", 3);
        state.applyPlayerEffect("Veneno", 3);

        assertEquals(2, state.getPlayerEffectStackCount("Veneno"));

        state.applyBossEffect("Atordoou", 1);
        state.applyBossEffect("Dano de Void", 2);
        assertEquals(1, state.getBossEffectStackCount("Atordoou"));
        assertEquals(1, state.getBossEffectStackCount("Dano de Void"));
    }

    private static class FakeSharedPreferences implements SharedPreferences {
        private final Map<String, Object> values = new HashMap<>();

        @Override
        public Map<String, ?> getAll() {
            return new HashMap<>(values);
        }

        @Override
        public String getString(String key, String defValue) {
            Object value = values.get(key);
            return value instanceof String ? (String) value : defValue;
        }

        @Override
        public Set<String> getStringSet(String key, Set<String> defValues) {
            Object value = values.get(key);
            if (value instanceof Set) {
                return new HashSet<>((Set<String>) value);
            }
            return new HashSet<>(defValues);
        }

        @Override
        public int getInt(String key, int defValue) {
            Object value = values.get(key);
            return value instanceof Integer ? (Integer) value : defValue;
        }

        @Override
        public long getLong(String key, long defValue) {
            Object value = values.get(key);
            return value instanceof Long ? (Long) value : defValue;
        }

        @Override
        public float getFloat(String key, float defValue) {
            Object value = values.get(key);
            return value instanceof Float ? (Float) value : defValue;
        }

        @Override
        public boolean getBoolean(String key, boolean defValue) {
            Object value = values.get(key);
            return value instanceof Boolean ? (Boolean) value : defValue;
        }

        @Override
        public boolean contains(String key) {
            return values.containsKey(key);
        }

        @Override
        public Editor edit() {
            return new FakeEditor();
        }

        @Override
        public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        }

        @Override
        public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        }

        private final class FakeEditor implements Editor {
            @Override
            public Editor putString(String key, String value) {
                values.put(key, value);
                return this;
            }

            @Override
            public Editor putStringSet(String key, Set<String> values) {
                FakeSharedPreferences.this.values.put(key, new HashSet<>(values));
                return this;
            }

            @Override
            public Editor putInt(String key, int value) {
                values.put(key, value);
                return this;
            }

            @Override
            public Editor putLong(String key, long value) {
                values.put(key, value);
                return this;
            }

            @Override
            public Editor putFloat(String key, float value) {
                values.put(key, value);
                return this;
            }

            @Override
            public Editor putBoolean(String key, boolean value) {
                values.put(key, value);
                return this;
            }

            @Override
            public Editor remove(String key) {
                values.remove(key);
                return this;
            }

            @Override
            public Editor clear() {
                values.clear();
                return this;
            }

            @Override
            public boolean commit() {
                return true;
            }

            @Override
            public void apply() {
            }
        }
    }
}
