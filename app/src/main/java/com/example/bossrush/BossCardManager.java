package com.example.bossrush;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BossCardManager {
    private final List<BossCard> cards;
    private List<BossCard> attackCards;
    private List<BossCard> defenseCards;
    private List<BossCard> specialCards;

    public BossCardManager() {
        cards = new ArrayList<>();
        // Attack cards (Espada)
        attackCards = new ArrayList<>();
        attackCards.add(new BossCard(
                "Lâmina em Chamas",
                "Ataque: +12 de dano e aplica Dado de Fogo por 2 turnos.",
                "Espada",
                12,
                0,
                0,
                0,
                "Dado de Fogo",
                2
        ));
        attackCards.add(new BossCard(
                "Gume de Sangue",
                "Ataque: +8 de dano e rouba 6 de vida por ataque.",
                "Espada",
                8,
                0,
                0,
                6,
                null,
                0
        ));
        attackCards.add(new BossCard(
                "Venenos da Lâmina",
                "Ataque: +14 de dano e aplica Veneno por 3 turnos.",
                "Espada",
                14,
                0,
                0,
                0,
                "Veneno",
                3
        ));
        attackCards.add(new BossCard(
                "Choque de Stun",
                "Ataque: +6 de dano e Atordoou o inimigo por 1 turno.",
                "Espada",
                6,
                0,
                0,
                0,
                "Atordoou",
                1
        ));

        // Defense cards (Escudo)
        defenseCards = new ArrayList<>();
        defenseCards.add(new BossCard(
                "Escudo de Ferro",
                "Defesa: +10% de dano refletido ao atacante.",
                "Escudo",
                0,
                10,
                0,
                0,
                null,
                0
        ));
        defenseCards.add(new BossCard(
                "Barreira de Brasa",
                "Defesa: +8% de dano refletido e reduz dano recebido.",
                "Escudo",
                0,
                8,
                0,
                0,
                null,
                0
        ));
        defenseCards.add(new BossCard(
                "Reflexo Estável",
                "Defesa: +12% de dano refletido (melhora contra ataques fortes).",
                "Escudo",
                0,
                12,
                0,
                0,
                null,
                0
        ));

        // Special attack cards (Ataque especial)
        specialCards = new ArrayList<>();
        specialCards.add(new BossCard(
                "Fúria do Especial",
                "Especial: +18 de dano e aplica Dado de Fogo por 3 turnos.",
                "Ataque especial",
                0,
                0,
                18,
                0,
                "Dado de Fogo",
                3
        ));
        specialCards.add(new BossCard(
                "Void do Eclipse",
                "Especial: +20 de dano e aplica Dano de Void por 2 turnos.",
                "Ataque especial",
                0,
                0,
                20,
                0,
                "Dano de Void",
                2
        ));
        specialCards.add(new BossCard(
                "Presa do Inferno",
                "Especial: +16 de dano e aplica Veneno por 3 turnos.",
                "Ataque especial",
                0,
                0,
                16,
                0,
                "Veneno",
                3
        ));

        // Combine into master list for fallback but individual category lists drive drafts
        cards.addAll(attackCards);
        cards.addAll(defenseCards);
        cards.addAll(specialCards);
    }

    public List<BossCard> getDraftCards() {
        List<BossCard> draft = new ArrayList<>();
        // Try to include one card from each category for variety
        java.util.List<BossCard> pick;
        if (attackCards != null && !attackCards.isEmpty()) {
            pick = new ArrayList<>(attackCards);
            Collections.shuffle(pick);
            draft.add(pick.get(0));
        }
        if (defenseCards != null && !defenseCards.isEmpty()) {
            pick = new ArrayList<>(defenseCards);
            Collections.shuffle(pick);
            draft.add(pick.get(0));
        }
        if (specialCards != null && !specialCards.isEmpty()) {
            pick = new ArrayList<>(specialCards);
            Collections.shuffle(pick);
            draft.add(pick.get(0));
        }
        // If we have less than 3 (very unlikely), fill from combined pool with random order but avoid duplicates
        if (draft.size() < 3) {
            java.util.List<BossCard> pool = new ArrayList<>(cards);
            Collections.shuffle(pool);
            for (BossCard c : pool) {
                if (draft.size() >= 3) break;
                if (!draft.contains(c)) draft.add(c);
            }
        }
        // Final shuffle so order is not always category-sequence
        Collections.shuffle(draft);
        return draft;
    }

    public java.util.List<BossCard> getAllCards() {
        return new java.util.ArrayList<>(cards);
    }
}
