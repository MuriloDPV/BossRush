package com.example.bossrush;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CodeManager {
    public static final String PREFS_NAME = "boss_rush_codes";
    public static final String KEY_UNLOCKED_CODES = "unlocked_codes";
    public static final String KEY_ACTIVE_CODES = "active_codes";

    public static final String CODE_ZATTA = "zatta";
    public static final String CODE_MELISSA = "melissa";
    public static final String CODE_MANUSCA = "manusca";
    public static final String CODE_MURILO = "murilo";
    // Códigos de personagem: só liberam o personagem (não têm Ativar/Desativar)
    // De propósito trocado: o código "tanque" libera o Mago e o código "mago" libera o Tanque
    public static final String CODE_MAGO = "tanque";
    public static final String CODE_TANQUE = "mago";

    private static final Map<String, String> CODE_INFO = new LinkedHashMap<>();

    static {
        CODE_INFO.put(CODE_ZATTA, "Bônus de dano em espada");
        CODE_INFO.put(CODE_MELISSA, "Aumenta a vida máxima");
        CODE_INFO.put(CODE_MANUSCA, "Melhora o escudo e as curas");
        CODE_INFO.put(CODE_MURILO, "Expande o total de chefes");
        CODE_INFO.put(CODE_MAGO, "Personagem liberado: Mago (escolha em Nova partida)");
        CODE_INFO.put(CODE_TANQUE, "Personagem liberado: Tanque (escolha em Nova partida)");
    }

    private final SharedPreferences preferences;

    public CodeManager(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public static List<String> getAvailableCodes() {
        return new ArrayList<>(CODE_INFO.keySet());
    }

    public static String getCodeDescription(String code) {
        String normalized = normalize(code);
        return CODE_INFO.getOrDefault(normalized, "Código especial");
    }

    public static String normalize(String rawCode) {
        if (rawCode == null) return "";
        return rawCode.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
    }

    public boolean isKnownCode(String rawCode) {
        return CODE_INFO.containsKey(normalize(rawCode));
    }

    public boolean redeemCode(String rawCode) {
        String code = normalize(rawCode);
        if (!isKnownCode(code)) {
            return false;
        }

        Set<String> unlocked = new HashSet<>(preferences.getStringSet(KEY_UNLOCKED_CODES, new HashSet<>()));
        unlocked.add(code);
        preferences.edit().putStringSet(KEY_UNLOCKED_CODES, unlocked).apply();
        return true;
    }

    public boolean isUnlocked(String rawCode) {
        String code = normalize(rawCode);
        if (code.isEmpty()) {
            return false;
        }
        Set<String> unlocked = preferences.getStringSet(KEY_UNLOCKED_CODES, new HashSet<>());
        return unlocked.contains(code);
    }

    public static boolean isCharacterCode(String rawCode) {
        String code = normalize(rawCode);
        return CODE_MAGO.equals(code) || CODE_TANQUE.equals(code);
    }

    public boolean toggleActivation(String rawCode) {
        String code = normalize(rawCode);
        if (!isUnlocked(code) || !isKnownCode(code) || isCharacterCode(code)) {
            return false;
        }

        Set<String> active = new HashSet<>(preferences.getStringSet(KEY_ACTIVE_CODES, new HashSet<>()));
        if (active.contains(code)) {
            active.remove(code);
        } else {
            active.add(code);
        }
        preferences.edit().putStringSet(KEY_ACTIVE_CODES, active).apply();
        return true;
    }

    public boolean isActive(String rawCode) {
        String code = normalize(rawCode);
        if (code.isEmpty()) {
            return false;
        }
        Set<String> active = preferences.getStringSet(KEY_ACTIVE_CODES, new HashSet<>());
        return active.contains(code);
    }

    public List<String> getUnlockedCodes() {
        List<String> unlocked = new ArrayList<>();
        for (String code : getAvailableCodes()) {
            if (isUnlocked(code)) {
                unlocked.add(code);
            }
        }
        return unlocked;
    }

    public int getEffectiveTotalBosses(int baseBosses) {
        int total = baseBosses;
        if (isActive(CODE_MURILO)) {
            total += 3;
        }
        return total;
    }

    public void applyActiveEffects(GameState gameState) {
        if (gameState == null) {
            return;
        }

        if (isActive(CODE_ZATTA)) {
            gameState.addSwordDamageBonus(4);
            gameState.restoreHealCharge();
        }

        if (isActive(CODE_MELISSA)) {
            gameState.applyMelissaLifeBonusOnce();
            gameState.addSwordDamageBonus(10);
            gameState.addSpecialDamageBonus(10);
        }

        if (isActive(CODE_MANUSCA)) {
            gameState.addShieldReflectBonus(12);
            gameState.addHealCharge(1);
        }

        if (isActive(CODE_MURILO)) {
            gameState.setSessionTotalBosses(Math.max(gameState.getSessionTotalBosses(), gameState.getSessionTotalBosses() + 3));
        }
    }

    public void applyUnlockedBonusDefaults(GameState gameState) {
        applyActiveEffects(gameState);
    }

    public boolean hasAnyUnlockedCode() {
        return !getUnlockedCodes().isEmpty();
    }
}
