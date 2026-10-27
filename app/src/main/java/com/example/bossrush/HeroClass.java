package com.example.bossrush;

/**
 * Personagens jogáveis. O Guerreiro é o padrão; Mago e Tanque só aparecem
 * depois de resgatar o código de cada um na tela de Códigos.
 */
public enum HeroClass {
    //          id           nome          código                      vida  espada  especial  reflexo extra
    GUERREIRO("guerreiro", "Guerreiro", null,                        100,    0,      0,       0),
    MAGO     ("mago",      "Mago",      CodeManager.CODE_MAGO,        85,   +8,    +15,       0),
    TANQUE   ("tanque",    "Tanque",    CodeManager.CODE_TANQUE,     160,   -4,    -10,     +30);

    public final String id;
    public final String displayName;
    /** Código que libera o personagem (null = sempre liberado). */
    public final String unlockCode;
    public final int maxHp;
    /** Somado ao dano base da espada (15). */
    public final int swordDelta;
    /** Somado ao dano base do ataque especial (30 a 47). */
    public final int specialDelta;
    /** Somado ao reflexo do escudo (base 20% do dano devolvido ao chefe). */
    public final int reflectBonus;

    HeroClass(String id, String displayName, String unlockCode, int maxHp, int swordDelta, int specialDelta, int reflectBonus) {
        this.id = id;
        this.displayName = displayName;
        this.unlockCode = unlockCode;
        this.maxHp = maxHp;
        this.swordDelta = swordDelta;
        this.specialDelta = specialDelta;
        this.reflectBonus = reflectBonus;
    }

    public int getSpriteRes() {
        switch (this) {
            case MAGO: return R.drawable.heroi_mago;
            case TANQUE: return R.drawable.heroi_tanque;
            default: return R.drawable.heroi;
        }
    }

    /** Resumo curto mostrado na tela de escolha. */
    public String getStatsText() {
        int sword = Math.max(1, 15 + swordDelta);
        int reflect = 20 + reflectBonus;
        return "Vida: " + maxHp
                + "\nAtaque: " + sword
                + "\nEspecial: " + (30 + specialDelta) + "–" + (47 + specialDelta)
                + "\nEscudo devolve: " + reflect + "%";
    }

    public static HeroClass fromId(String id) {
        if (id != null) {
            for (HeroClass c : values()) {
                if (c.id.equals(id)) return c;
            }
        }
        return GUERREIRO;
    }

    /** Qual personagem um código libera (null se não for código de personagem). */
    public static HeroClass forCode(String code) {
        String normalized = CodeManager.normalize(code);
        for (HeroClass c : values()) {
            if (c.unlockCode != null && c.unlockCode.equals(normalized)) return c;
        }
        return null;
    }
}
