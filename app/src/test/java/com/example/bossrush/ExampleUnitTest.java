package com.example.bossrush;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExampleUnitTest {
    @Test
    public void progressionAdvancesOnlyAfterBossDefeat() {
        GameState state = new GameState(0);
        state.setBossHp(15);
        assertFalse(state.isBossDefeated());

        state.setBossHp(0);
        assertTrue(state.isBossDefeated());

        state.prepareNextBoss();
        assertEquals(1, state.getBossIndex());
    }

    @Test
    public void finalBossUsesExtraPhaseAndNineBossSequence() {
        assertEquals(9, GameState.TOTAL_BOSSES);

        GameState state = new GameState(8);
        assertTrue(state.isFinalBoss());
        assertTrue(state.getBossName().contains("Fase Extra"));
    }

    @Test
    public void shieldReflectsTwentyPercentOfIncomingDamage() {
        GameState state = new GameState(0);
        state.activateShield();

        int reflected = state.applyEnemyAttack(50);

        assertEquals(10, reflected);
        assertEquals(60, state.getPlayerHp());
        assertEquals(80, state.getBossHp());
        assertFalse(state.isShieldActive());
    }

    @Test
    public void specialAttackRechargesEveryFiveTurns() {
        GameState state = new GameState(0);
        assertTrue(state.isSpecialAttackReady());

        assertTrue(state.useSpecialAttack());
        assertFalse(state.isSpecialAttackReady());
        assertEquals(5, state.getSpecialAttackCooldown());

        for (int i = 0; i < 4; i++) {
            state.advanceTurn();
        }

        assertFalse(state.isSpecialAttackReady());

        state.advanceTurn();
        assertTrue(state.isSpecialAttackReady());
    }
}
