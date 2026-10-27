package com.example.bossrush;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // Base total; actual session total may increase if 'murilo' code is active
    private static final int BASE_TOTAL_BOSSES = GameState.TOTAL_BOSSES;
    private static final String PREFS_NAME = "boss_rush_save";
    private static final String KEY_BOSS_INDEX = "highest_boss_index";
    private static final String KEY_SOUND_ON = "sound_on";
    private static final String KEY_VOLUME = "volume";
    private static final String KEY_MUSIC_VOLUME = "music_volume";
    private static final String KEY_HERO_CLASS = "hero_class";

    private SharedPreferences preferences;
    private CodeManager codeManager;
    private GameState gameState;
    private LinearLayout menuLayout;
    private LinearLayout settingsLayout;
    private LinearLayout codesLayout;
    private LinearLayout effectsLayout;
    private LinearLayout battleLayout;
    private LinearLayout victoryLayout;
    private LinearLayout cardLayout;
    private LinearLayout characterLayout;
    private LinearLayout characterContainer;
    private Button characterBackButton;
    private HeroClass currentHeroClass = HeroClass.GUERREIRO;
    private LinearLayout unlockedCodesContainer;
    private LinearLayout effectsContainer;
    private TextView titleText;
    private TextView progressText;
    private TextView bossNameText;
    private TextView bossHpText;
    private TextView playerHpText;
    private TextView healText;
    private TextView statusText;
    private TextView victoryText;
    private TextView specialAttackText;
    private TextView specialAttackCooldownText;
    private TextView bossStatusText;
    private TextView playerStatusText;
    private ProgressBar playerHealthBar;
    private ProgressBar bossHealthBar;
    private Button startButton;
    private Button continueButton;
    private Button settingsButton;
    private Button codesButton;
    private Button effectsButton;
    private Button resetButton;
    private Button settingsBackButton;
    private Button settingsMenuButton;
    private Button codesBackButton;
    private Button effectsBackButton;
    private Button redeemCodeButton;
    private ImageButton swordButton;
    private ImageButton shieldButton;
    private ImageButton healButton;
    private ImageButton specialAttackButton;
    private ImageButton battleSettingsButton;
    // Pixel art da batalha
    private ImageView arenaBackground;
    private ImageView heroSprite;
    private ImageView bossSprite;
    // Cada pixel do desenho vira 2.4dp na tela; as imagens foram exportadas ampliadas 8x
    private static final float SPRITE_PIXEL_DP = 2.4f;
    private static final int SPRITE_EXPORT_SCALE = 8;
    private int currentAreaRes = 0;
    private int currentBossRes = 0;
    private Button nextBossButton;
    private Button menuButton;
    private Button cardButtonA;
    private Button cardButtonB;
    private Button cardButtonC;
    private Switch soundSwitch;
    private SeekBar volumeSeekBar;
    private SeekBar musicVolumeSeekBar;
    private TextView cardCountText;
    private TextView codeStatusText;
    private EditText codeInputEditText;
    private ScrollView codesScrollView;
    private Random random;
    private int highestBossIndex;
    private int currentVolume;
    private int currentMusicVolume;
    private boolean soundOn;
    private BossCardManager cardManager;
    private MediaPlayer battleMusicPlayer;
    private MediaPlayer menuMusicPlayer;
    private java.util.List<BossCard> currentDraft;
    private String battleStatusMessage = "";

    // Efeitos visuais por cima (corte, explosão, cura, escudo, números de dano, tela inicial)
    private BattleFx fx;
    private android.view.View menuLogo;
    private android.view.View menuPanel;
    private android.view.View battleArena;
    private final android.os.Handler uiHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    // Bloqueia os botões enquanto a animação do turno roda (evita apertar várias vezes)
    private boolean inputLocked;
    private boolean bossDying;
    private boolean heroDying;
    // Lembra se as Configurações foram abertas no meio da batalha (para o Voltar voltar pra ela)
    private boolean settingsFromBattle;
    private final Runnable unlockInput = () -> {
        if (!bossDying && !heroDying) {
            inputLocked = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        // Ensure layout resizes when keyboard appears so the code input is not covered
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // Tela cheia: esconde a barra de navegação e a de status (voltam ao deslizar da borda)
        enterImmersiveMode();
        setupScreenInsets();

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        codeManager = new CodeManager(getSharedPreferences(CodeManager.PREFS_NAME, MODE_PRIVATE));
        random = new Random();
        cardManager = new BossCardManager();
        // card counter UI element
        // (will be bound below after findViewById calls)

        menuLayout = findViewById(R.id.menuLayout);
        settingsLayout = findViewById(R.id.settingsLayout);
        codesLayout = findViewById(R.id.codesLayout);
        effectsLayout = findViewById(R.id.effectsLayout);
        battleLayout = findViewById(R.id.battleLayout);
        victoryLayout = findViewById(R.id.victoryLayout);
        cardLayout = findViewById(R.id.cardLayout);
        characterLayout = findViewById(R.id.characterLayout);
        characterContainer = findViewById(R.id.characterContainer);
        characterBackButton = findViewById(R.id.characterBackButton);
        characterBackButton.setBackgroundTintList(null);
        characterBackButton.setOnClickListener(v -> showMenu());
        titleText = findViewById(R.id.titleText);
        effectsContainer = findViewById(R.id.effectsContainer);
        codeStatusText = findViewById(R.id.codeStatusText);
        codeInputEditText = findViewById(R.id.codeInputEditText);
        codesScrollView = findViewById(R.id.codesScrollView);
        unlockedCodesContainer = findViewById(R.id.unlockedCodesContainer);
        progressText = findViewById(R.id.progressText);
        bossNameText = findViewById(R.id.bossNameText);
        bossHpText = findViewById(R.id.bossHpText);
        playerHpText = findViewById(R.id.playerHpText);
        healText = findViewById(R.id.healText);
        statusText = findViewById(R.id.statusText);
        victoryText = findViewById(R.id.victoryText);
        specialAttackText = findViewById(R.id.specialAttackText);
        specialAttackCooldownText = findViewById(R.id.specialAttackCooldownText);
        bossStatusText = findViewById(R.id.bossStatusText);
        playerStatusText = findViewById(R.id.playerStatusText);
        playerHealthBar = findViewById(R.id.playerHealthBar);
        bossHealthBar = findViewById(R.id.bossHealthBar);
        cardCountText = findViewById(R.id.cardCountText);

        startButton = findViewById(R.id.startButton);
        continueButton = findViewById(R.id.continueButton);
        settingsButton = findViewById(R.id.settingsButton);
        codesButton = findViewById(R.id.codesButton);
        effectsButton = findViewById(R.id.effectsButton);
        resetButton = findViewById(R.id.resetButton);
        settingsBackButton = findViewById(R.id.settingsBackButton);
        settingsMenuButton = findViewById(R.id.settingsMenuButton);
        codesBackButton = findViewById(R.id.codesBackButton);
        effectsBackButton = findViewById(R.id.effectsBackButton);
        redeemCodeButton = findViewById(R.id.redeemCodeButton);
        swordButton = findViewById(R.id.swordButton);
        shieldButton = findViewById(R.id.shieldButton);
        healButton = findViewById(R.id.healButton);
        specialAttackButton = findViewById(R.id.specialAttackButton);
        battleSettingsButton = findViewById(R.id.battleSettingsButton);
        arenaBackground = findViewById(R.id.arenaBackground);
        heroSprite = findViewById(R.id.heroSprite);
        bossSprite = findViewById(R.id.bossSprite);
        setSprite(heroSprite, R.drawable.heroi);
        nextBossButton = findViewById(R.id.nextBossButton);
        menuButton = findViewById(R.id.menuButton);
        cardButtonA = findViewById(R.id.cardButtonA);
        cardButtonB = findViewById(R.id.cardButtonB);
        cardButtonC = findViewById(R.id.cardButtonC);
        clearButtonTints(startButton, continueButton, settingsButton, codesButton, effectsButton, resetButton,
                settingsBackButton, settingsMenuButton, codesBackButton, effectsBackButton, redeemCodeButton, nextBossButton, menuButton,
                cardButtonA, cardButtonB, cardButtonC);
        soundSwitch = findViewById(R.id.soundSwitch);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        musicVolumeSeekBar = findViewById(R.id.musicVolumeSeekBar);

        menuLogo = findViewById(R.id.menuLogo);
        menuPanel = findViewById(R.id.menuPanel);
        battleArena = findViewById(R.id.battleArena);
        fx = new BattleFx(this, findViewById(R.id.fxLayer));
        // Puxadores em pixel art nas configurações (sem borrar)
        android.graphics.drawable.Drawable knob1 = fx.pixelDrawable(R.drawable.ui_puxador, 2.4f);
        android.graphics.drawable.Drawable knob2 = fx.pixelDrawable(R.drawable.ui_puxador, 2.4f);
        android.graphics.drawable.Drawable knob3 = fx.pixelDrawable(R.drawable.ui_puxador, 2.4f);
        if (knob1 != null) volumeSeekBar.setThumb(knob1);
        if (knob2 != null) musicVolumeSeekBar.setThumb(knob2);
        if (knob3 != null) soundSwitch.setThumbDrawable(knob3);

        highestBossIndex = preferences.getInt(KEY_BOSS_INDEX, 0);
        soundOn = preferences.getBoolean(KEY_SOUND_ON, true);
        currentVolume = preferences.getInt(KEY_VOLUME, 70);
        currentMusicVolume = preferences.getInt(KEY_MUSIC_VOLUME, 70);
        titleText.setText(getString(R.string.app_name));

        soundSwitch.setChecked(soundOn);
        volumeSeekBar.setProgress(currentVolume);
        musicVolumeSeekBar.setProgress(currentMusicVolume);

        startButton.setOnClickListener(v -> openNewGame());
        continueButton.setOnClickListener(v -> continueGame());
        codesButton.setOnClickListener(v -> showCodes());
        effectsButton.setOnClickListener(v -> showEffects());
        resetButton.setOnClickListener(v -> confirmReset());
        settingsBackButton.setOnClickListener(v -> closeSettings());
        settingsMenuButton.setOnClickListener(v -> confirmLeaveBattle());
        codesBackButton.setOnClickListener(v -> showMenu());
        effectsBackButton.setOnClickListener(v -> showMenu());
        redeemCodeButton.setOnClickListener(v -> redeemCodeInput());
        codeInputEditText.setOnEditorActionListener((v, actionId, event) -> {
            redeemCodeInput();
            return true;
        });
        swordButton.setOnClickListener(v -> handlePlayerAction("sword"));
        shieldButton.setOnClickListener(v -> useShield());
        healButton.setOnClickListener(v -> handlePlayerAction("heal"));
        specialAttackButton.setOnClickListener(v -> useSpecialAttack());
        battleSettingsButton.setOnClickListener(v -> {
            settingsFromBattle = gameState != null;
            showSettings();
        });
        settingsButton.setOnClickListener(v -> {
            settingsFromBattle = false;
            showSettings();
        });
        nextBossButton.setOnClickListener(v -> advanceToNextBoss());
        menuButton.setOnClickListener(v -> showMenu());
        cardButtonA.setOnClickListener(v -> applySelectedCard(0));
        cardButtonB.setOnClickListener(v -> applySelectedCard(1));
        cardButtonC.setOnClickListener(v -> applySelectedCard(2));

        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            soundOn = isChecked;
            preferences.edit().putBoolean(KEY_SOUND_ON, soundOn).apply();
            applyMusicVolume();
            if (!soundOn) {
                stopBattleMusic();
            }
        });

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentVolume = progress;
                preferences.edit().putInt(KEY_VOLUME, currentVolume).apply();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        musicVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentMusicVolume = progress;
                preferences.edit().putInt(KEY_MUSIC_VOLUME, currentMusicVolume).apply();
                applyMusicVolume();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        initializeBattleMusic();
        initializeMenuMusic();
        showMenu();
    }

    private int getCurrentTotalBosses() {
        return codeManager.getEffectiveTotalBosses(BASE_TOTAL_BOSSES);
    }

    // Nova partida: se tiver Mago/Tanque liberado, abre a escolha de personagem; senão vai de Guerreiro
    private void openNewGame() {
        boolean anyUnlocked = false;
        for (HeroClass c : HeroClass.values()) {
            if (c.unlockCode != null && codeManager.isUnlocked(c.unlockCode)) {
                anyUnlocked = true;
                break;
            }
        }
        if (anyUnlocked) {
            showCharacterSelect();
        } else {
            startNewGame(HeroClass.GUERREIRO);
        }
    }

    private boolean isHeroUnlocked(HeroClass c) {
        return c.unlockCode == null || codeManager.isUnlocked(c.unlockCode);
    }

    private void showCharacterSelect() {
        resetFxForScreenChange();
        menuLayout.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.GONE);
        codesLayout.setVisibility(View.GONE);
        effectsLayout.setVisibility(View.GONE);
        battleLayout.setVisibility(View.GONE);
        victoryLayout.setVisibility(View.GONE);
        cardLayout.setVisibility(View.GONE);
        characterLayout.setVisibility(View.VISIBLE);
        stopBattleMusic();
        startMenuMusic();
        characterContainer.removeAllViews();
        for (final HeroClass c : HeroClass.values()) {
            characterContainer.addView(buildCharacterCard(c, isHeroUnlocked(c)));
        }
    }

    // Cartão de um personagem na tela de escolha (bloqueado aparece como sombra)
    private View buildCharacterCard(final HeroClass c, boolean unlocked) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        card.setBackgroundResource(R.drawable.painel_item);
        card.setPadding(dp(14), dp(10), dp(14), dp(12));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(190), LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = dp(8);
        params.rightMargin = dp(8);
        card.setLayoutParams(params);

        ImageView img = new ImageView(this);
        img.setImageResource(c.getSpriteRes());
        img.setAdjustViewBounds(true);
        img.setContentDescription(c.displayName);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(110));
        img.setLayoutParams(imgParams);
        card.addView(img);

        TextView name = new TextView(this);
        name.setText(unlocked ? c.displayName : "???");
        name.setTextColor(android.graphics.Color.parseColor("#FFE08A"));
        name.setTextSize(22f);
        name.setTypeface(ResourcesCompat.getFont(this, R.font.jacquard));
        name.setGravity(android.view.Gravity.CENTER);
        name.setPadding(0, dp(6), 0, dp(2));
        card.addView(name);

        TextView stats = new TextView(this);
        stats.setText(unlocked ? c.getStatsText() : "Bloqueado\nResgate o código\nna tela de Códigos");
        stats.setTextColor(android.graphics.Color.parseColor("#F5F0E1"));
        stats.setTextSize(13f);
        stats.setGravity(android.view.Gravity.CENTER);
        stats.setLineSpacing(dp(1), 1f);
        card.addView(stats);

        if (unlocked) {
            card.setClickable(true);
            card.setFocusable(true);
            card.setOnClickListener(v -> startNewGame(c));
        } else {
            img.setColorFilter(android.graphics.Color.parseColor("#0B1026"), android.graphics.PorterDuff.Mode.SRC_ATOP);
            card.setAlpha(0.7f);
        }
        return card;
    }

    private void startNewGame(HeroClass heroClass) {
        int startingHp = 100;
        currentHeroClass = heroClass == null ? HeroClass.GUERREIRO : heroClass;
        preferences.edit().putString(KEY_HERO_CLASS, currentHeroClass.id).apply();
        setSprite(heroSprite, currentHeroClass.getSpriteRes());
        gameState = new GameState(0, startingHp);
        gameState.applyHeroClass(currentHeroClass);
        codeManager.applyActiveEffects(gameState);
        highestBossIndex = 0;
        saveProgress();
        battleStatusMessage = "Batalha contra " + gameState.getBossName() + ".";
        showBattle();
        updateBattleUi();
    }

    private void continueGame() {
        int resumeIndex = Math.min(getCurrentTotalBosses() - 1, Math.max(0, highestBossIndex));
        int startingHp = 100;
        // Continua com o personagem da última partida (se ele ainda estiver liberado)
        currentHeroClass = HeroClass.fromId(preferences.getString(KEY_HERO_CLASS, HeroClass.GUERREIRO.id));
        if (!isHeroUnlocked(currentHeroClass)) {
            currentHeroClass = HeroClass.GUERREIRO;
        }
        setSprite(heroSprite, currentHeroClass.getSpriteRes());
        gameState = new GameState(resumeIndex, startingHp);
        gameState.applyHeroClass(currentHeroClass);
        codeManager.applyActiveEffects(gameState);
        battleStatusMessage = "Batalha contra " + gameState.getBossName() + ".";
        showBattle();
        updateBattleUi();
    }

    private void confirmReset() {
        new AlertDialog.Builder(this)
                .setTitle("Reiniciar progresso")
                .setMessage("Deseja limpar o progresso salvo?")
                .setPositiveButton("Sim", (dialog, which) -> resetProgress())
                .setNegativeButton("Não", null)
                .show();
    }

    private void resetProgress() {
        preferences.edit()
                .remove(KEY_BOSS_INDEX)
                .putBoolean(KEY_SOUND_ON, true)
                .putInt(KEY_VOLUME, 70)
                .apply();
        highestBossIndex = 0;
        soundOn = true;
        currentVolume = 70;
        soundSwitch.setChecked(true);
        volumeSeekBar.setProgress(70);
        gameState = null;
        battleStatusMessage = "";
        showMenu();
    }

    private void showMenu() {
        stopBattleMusic();
        startMenuMusic();
        resetFxForScreenChange();
        fx.startMenu(menuLogo, menuPanel);
        menuLayout.setVisibility(View.VISIBLE);
        settingsLayout.setVisibility(View.GONE);
        codesLayout.setVisibility(View.GONE);
        effectsLayout.setVisibility(View.GONE);
        battleLayout.setVisibility(View.GONE);
        victoryLayout.setVisibility(View.GONE);
        cardLayout.setVisibility(View.GONE);
        characterLayout.setVisibility(View.GONE);
        int unlockedBosses = Math.min(getCurrentTotalBosses(), highestBossIndex);
        progressText.setText("Progresso: " + unlockedBosses + "/" + getCurrentTotalBosses() + " mestres");
        continueButton.setEnabled(unlockedBosses > 0);
    }

    // Voltar das Configurações: volta pra batalha se veio dela, senão vai pro menu
    private void closeSettings() {
        boolean backToBattle = settingsFromBattle && gameState != null;
        settingsFromBattle = false;
        if (!backToBattle) {
            showMenu();
            return;
        }
        if (gameState.isPlayerDefeated()) {
            showMenu();
            return;
        }
        if (gameState.isBossDefeated()) {
            // O chefe morreu enquanto as Configurações estavam abertas: segue pra tela de vitória
            showBossDefeatedScreen();
            return;
        }
        resumeBattle();
    }

    // Reabre a batalha do jeito que estava (sem animação de entrada do chefe, música continua)
    private void resumeBattle() {
        resetFxForScreenChange();
        menuLayout.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.GONE);
        codesLayout.setVisibility(View.GONE);
        effectsLayout.setVisibility(View.GONE);
        battleLayout.setVisibility(View.VISIBLE);
        victoryLayout.setVisibility(View.GONE);
        cardLayout.setVisibility(View.GONE);
        characterLayout.setVisibility(View.GONE);
        stopMenuMusic();
        startBattleMusic();
        fx.resetSprite(heroSprite);
        fx.resetSprite(bossSprite);
        fx.startIdle(heroSprite, 0);
        fx.startIdle(bossSprite, 450);
        updateBattleUi();
    }

    // Sair da batalha pelas Configurações (pede confirmação)
    private void confirmLeaveBattle() {
        new AlertDialog.Builder(this)
                .setTitle("Voltar ao menu inicial")
                .setMessage("A batalha atual será encerrada. Os chefes que você já venceu continuam salvos, mas as cartas desta partida serão perdidas.")
                .setPositiveButton("Sair", (dialog, which) -> {
                    settingsFromBattle = false;
                    showMenu();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showSettings() {
        boolean isInsideActiveGame = gameState != null && (battleLayout.getVisibility() == View.VISIBLE || victoryLayout.getVisibility() == View.VISIBLE || cardLayout.getVisibility() == View.VISIBLE);
        if (!isInsideActiveGame) {
            stopBattleMusic();
            startMenuMusic();
        }
        resetFxForScreenChange();
        settingsMenuButton.setVisibility(settingsFromBattle ? View.VISIBLE : View.GONE);
        menuLayout.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.VISIBLE);
        codesLayout.setVisibility(View.GONE);
        effectsLayout.setVisibility(View.GONE);
        battleLayout.setVisibility(View.GONE);
        victoryLayout.setVisibility(View.GONE);
        cardLayout.setVisibility(View.GONE);
        characterLayout.setVisibility(View.GONE);
    }

    private void showCodes() {
        stopBattleMusic();
        startMenuMusic();
        resetFxForScreenChange();
        menuLayout.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.GONE);
        codesLayout.setVisibility(View.VISIBLE);
        effectsLayout.setVisibility(View.GONE);
        battleLayout.setVisibility(View.GONE);
        victoryLayout.setVisibility(View.GONE);
        cardLayout.setVisibility(View.GONE);
        characterLayout.setVisibility(View.GONE);
        refreshCodeList();
        codeInputEditText.setText("");
        codeInputEditText.requestFocus();
    }

    private void showEffects() {
        stopBattleMusic();
        startMenuMusic();
        resetFxForScreenChange();
        menuLayout.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.GONE);
        codesLayout.setVisibility(View.GONE);
        effectsLayout.setVisibility(View.VISIBLE);
        battleLayout.setVisibility(View.GONE);
        victoryLayout.setVisibility(View.GONE);
        cardLayout.setVisibility(View.GONE);
        characterLayout.setVisibility(View.GONE);
        populateEffectsList();
    }

    private void showBattle() {
        stopMenuMusic();
        resetFxForScreenChange();
        fx.resetSprite(heroSprite);
        fx.bossEntrance(bossSprite);
        fx.startIdle(heroSprite, 0);
        fx.startIdle(bossSprite, 450);
        menuLayout.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.GONE);
        codesLayout.setVisibility(View.GONE);
        effectsLayout.setVisibility(View.GONE);
        battleLayout.setVisibility(View.VISIBLE);
        victoryLayout.setVisibility(View.GONE);
        cardLayout.setVisibility(View.GONE);
        characterLayout.setVisibility(View.GONE);
        startBattleMusic();
    }

    private void showVictory() {
        resetFxForScreenChange();
        menuLayout.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.GONE);
        codesLayout.setVisibility(View.GONE);
        effectsLayout.setVisibility(View.GONE);
        battleLayout.setVisibility(View.GONE);
        victoryLayout.setVisibility(View.VISIBLE);
        cardLayout.setVisibility(View.GONE);
        characterLayout.setVisibility(View.GONE);
        stopMenuMusic();
        startBattleMusic();
    }

    private void showCardSelection() {
        stopMenuMusic();
        startBattleMusic();
        resetFxForScreenChange();
        menuLayout.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.GONE);
        codesLayout.setVisibility(View.GONE);
        effectsLayout.setVisibility(View.GONE);
        battleLayout.setVisibility(View.GONE);
        victoryLayout.setVisibility(View.GONE);
        cardLayout.setVisibility(View.VISIBLE);
        characterLayout.setVisibility(View.GONE);

        // Build draft excluding cards that the player already has max copies of
        java.util.List<BossCard> all = cardManager.getAllCards();
        java.util.List<BossCard> attackPool = new java.util.ArrayList<>();
        java.util.List<BossCard> defensePool = new java.util.ArrayList<>();
        java.util.List<BossCard> specialPool = new java.util.ArrayList<>();
        for (BossCard c : all) {
            int copies = gameState == null ? 0 : gameState.getPlayerCardCopies(c.getName());
            if (copies >= GameState.MAX_COPIES_PER_CARD) continue; // exclude
            if ("Espada".equals(c.getTargetSkill())) attackPool.add(c);
            else if ("Escudo".equals(c.getTargetSkill())) defensePool.add(c);
            else if ("Ataque especial".equals(c.getTargetSkill())) specialPool.add(c);
        }
        currentDraft = new java.util.ArrayList<>();
        java.util.Random rnd = new java.util.Random();
        if (!attackPool.isEmpty()) currentDraft.add(attackPool.get(rnd.nextInt(attackPool.size())));
        if (!defensePool.isEmpty()) currentDraft.add(defensePool.get(rnd.nextInt(defensePool.size())));
        if (!specialPool.isEmpty()) currentDraft.add(specialPool.get(rnd.nextInt(specialPool.size())));
        // If we don't have three distinct category cards available, fill from remaining pool
        java.util.List<BossCard> remaining = new java.util.ArrayList<>();
        for (BossCard c : all) {
            if (!currentDraft.contains(c) && gameState.getPlayerCardCopies(c.getName()) < GameState.MAX_COPIES_PER_CARD) remaining.add(c);
        }
        java.util.Collections.shuffle(remaining);
        for (BossCard c : remaining) {
            if (currentDraft.size() >= 3) break;
            currentDraft.add(c);
        }
        // Ensure we have at least 3 options (if pool small, duplicate may occur)
        while (currentDraft.size() < 3 && !all.isEmpty()) {
            BossCard pick = all.get(rnd.nextInt(all.size()));
            if (!currentDraft.contains(pick)) currentDraft.add(pick);
            else break;
        }
        // Final shuffle
        java.util.Collections.shuffle(currentDraft);
        // Ensure size 3
        // If still lacking options, try to fill from all pool but avoid cards at max copies
        int safety = 0;
        while (currentDraft.size() < 3 && safety < 20) {
            BossCard pick = all.get(rnd.nextInt(all.size()));
            if (gameState.getPlayerCardCopies(pick.getName()) >= GameState.MAX_COPIES_PER_CARD) {
                safety++;
                continue;
            }
            if (!currentDraft.contains(pick)) currentDraft.add(pick);
            safety++;
        }
        // If still empty (all cards at max), fallback to showing any three (they will be disabled)
        while (currentDraft.size() < 3) currentDraft.add(all.get(0));
        // Show per-card copies and disable buttons for cards already at max copies (they were excluded already)
        BossCard c0 = currentDraft.get(0);
        BossCard c1 = currentDraft.get(1);
        BossCard c2 = currentDraft.get(2);
        int copies0 = gameState == null ? 0 : gameState.getPlayerCardCopies(c0.getName());
        int copies1 = gameState == null ? 0 : gameState.getPlayerCardCopies(c1.getName());
        int copies2 = gameState == null ? 0 : gameState.getPlayerCardCopies(c2.getName());
        cardButtonA.setText(c0.getName() + " (" + copies0 + "/" + GameState.MAX_COPIES_PER_CARD + " · nível " + getCardLevelLabel(c0) + "/3)\n" + c0.getDescription());
        cardButtonB.setText(c1.getName() + " (" + copies1 + "/" + GameState.MAX_COPIES_PER_CARD + " · nível " + getCardLevelLabel(c1) + "/3)\n" + c1.getDescription());
        cardButtonC.setText(c2.getName() + " (" + copies2 + "/" + GameState.MAX_COPIES_PER_CARD + " · nível " + getCardLevelLabel(c2) + "/3)\n" + c2.getDescription());
        styleCardButton(cardButtonA, c0);
        styleCardButton(cardButtonB, c1);
        styleCardButton(cardButtonC, c2);
        cardButtonA.setEnabled(copies0 < GameState.MAX_COPIES_PER_CARD);
        cardButtonB.setEnabled(copies1 < GameState.MAX_COPIES_PER_CARD);
        cardButtonC.setEnabled(copies2 < GameState.MAX_COPIES_PER_CARD);
        if (cardButtonA.isEnabled() || cardButtonB.isEnabled() || cardButtonC.isEnabled()) {
            statusText.setText("Escolha uma carta para adicionar ao seu baralho.");
        } else {
            statusText.setText("Nenhuma carta disponível: todas têm " + GameState.MAX_COPIES_PER_CARD + "/" + GameState.MAX_COPIES_PER_CARD + " cópias.");
        }
    }

    private void populateEffectsList() {
        effectsContainer.removeAllViews();

        TextView headerText = new TextView(this);
        headerText.setText("Efeitos disponíveis");
        headerText.setTextColor(android.graphics.Color.parseColor("#FFE08A"));
        headerText.setTextSize(34f);
        headerText.setTypeface(ResourcesCompat.getFont(this, R.font.jacquard));
        headerText.setShadowLayer(1f, 3f, 3f, android.graphics.Color.parseColor("#672040"));
        headerText.setGravity(android.view.Gravity.CENTER);
        headerText.setPadding(12, 12, 12, 12);
        effectsContainer.addView(headerText);

        String[][] effectCatalog = new String[][] {
                {"Queimadura", "Efeito ofensivo: causa dano por turno. Cada efeito individual pode acumular no máximo 2 vezes no mesmo alvo."},
                {"Veneno", "Efeito ofensivo: inflige dano contínuo por turno. O acúmulo máximo do mesmo efeito é 2."},
                {"Dano de Void", "Efeito especial: dano forte em sequência. A mesma aplicação não pode exceder 2 acúmulos no mesmo alvo."},
                {"Dado de Fogo", "Efeito de dano em área com duração por turno. O limite por tipo é de 2 aplicações no mesmo alvo."},
                {"Atordoou", "Efeito de controle: o alvo perde a ação quando a chance acerta. Chances: nível 1 = 25%, nível 2 = 50%, nível 3 = 75%. O efeito não acerta quando a rolagem falha."},
                {"Falha Crítica", "Efeito defensivo especial: quando o escudo falha, o jogador leva o dobro do dano e fica paralisado por 1 turno. A chance é de 10% a cada tentativa de defesa com escudo."},
                {"Escudo", "Efeito defensivo: aumenta a reflexão de dano contra o atacante. O limite por efeito do mesmo tipo também é 2."},
                {"Cura", "Efeito defensivo: recupera vida do jogador. Pode ser aplicado em conjunto com outros efeitos sem ultrapassar o limite individual."},
                {"Roubo de vida", "Efeito especial: restaura vida ao causar dano. Mantém o mesmo limite de 2 acúmulos por efeito e duração normal do jogo."},
                {"Ataque especial", "Efeito ofensivo: foco em dano bônus e status especial. A chance de ativação do status segue as regras da carta aplicada."}
        };

        for (String[] entry : effectCatalog) {
            TextView title = new TextView(this);
            title.setText(entry[0]);
            title.setTextColor(android.graphics.Color.parseColor("#FFE08A"));
            title.setTextSize(18f);
            title.setTypeface(ResourcesCompat.getFont(this, R.font.pixelify), android.graphics.Typeface.BOLD);
            title.setPadding(12, 12, 12, 0);

            TextView description = new TextView(this);
            description.setText(entry[1]);
            description.setTextColor(android.graphics.Color.parseColor("#F5F0E1"));
            description.setTextSize(15f);
            description.setPadding(12, 4, 12, 12);
            description.setSingleLine(false);
            description.setMaxLines(10);

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setBackgroundResource(R.drawable.painel_item);
            item.setPadding(dp(10), dp(6), dp(10), dp(6));
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            itemParams.topMargin = dp(6);
            item.setLayoutParams(itemParams);
            item.setFocusable(false);
            item.addView(title);
            item.addView(description);
            effectsContainer.addView(item);
        }
    }

    private void refreshCodeList() {
        unlockedCodesContainer.removeAllViews();
        java.util.List<String> unlockedCodes = codeManager.getUnlockedCodes();
        if (unlockedCodes.isEmpty()) {
            TextView emptyState = new TextView(this);
            emptyState.setText("Nenhum código desbloqueado.");
            emptyState.setTextColor(android.graphics.Color.parseColor("#F5F0E1"));
            emptyState.setPadding(16, 16, 16, 16);
            unlockedCodesContainer.addView(emptyState);
            return;
        }

        for (final String code : unlockedCodes) {
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setPadding(dp(12), dp(6), dp(8), dp(6));
            itemLayout.setBackgroundResource(R.drawable.painel_item);
            itemLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams codeItemParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            codeItemParams.topMargin = dp(6);
            itemLayout.setLayoutParams(codeItemParams);

            TextView codeTitle = new TextView(this);
            codeTitle.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            codeTitle.setText(code.toUpperCase() + "\n" + CodeManager.getCodeDescription(code));
            codeTitle.setTextColor(android.graphics.Color.parseColor("#F5F0E1"));
            codeTitle.setSingleLine(false);

            Button toggleButton = new Button(this);
            toggleButton.setText(codeManager.isActive(code) ? "Desativar" : "Ativar");
            toggleButton.setBackgroundResource(R.drawable.botao_madeira_sel);
            toggleButton.setBackgroundTintList(null);
            toggleButton.setStateListAnimator(null);
            toggleButton.setAllCaps(false);
            toggleButton.setTextColor(android.graphics.Color.parseColor("#FFF3D6"));
            toggleButton.setTypeface(ResourcesCompat.getFont(this, R.font.pixelify), android.graphics.Typeface.BOLD);
            toggleButton.setMinWidth(dp(120));
            toggleButton.setOnClickListener(v -> {
                boolean toggled = codeManager.toggleActivation(code);
                if (toggled) {
                    if (gameState != null) {
                        codeManager.applyActiveEffects(gameState);
                    }
                    codeStatusText.setText("Código " + (codeManager.isActive(code) ? "ativado" : "desativado") + ": " + code.toUpperCase());
                }
                refreshCodeList();
            });

            itemLayout.addView(codeTitle);
            if (!CodeManager.isCharacterCode(code)) {
                itemLayout.addView(toggleButton);
            }
            unlockedCodesContainer.addView(itemLayout);
        }
    }

    private void redeemCodeInput() {
        String rawInput = codeInputEditText.getText() == null ? "" : codeInputEditText.getText().toString();
        String normalized = CodeManager.normalize(rawInput);
        if (normalized.isEmpty()) {
            codeStatusText.setText("Digite um código antes de resgatar.");
            return;
        }

        boolean alreadyUnlocked = codeManager.isUnlocked(normalized);
        boolean valid = codeManager.redeemCode(rawInput);
        if (!valid) {
            codeStatusText.setText("Código inexistente.");
            return;
        }

        codeInputEditText.setText("");
        HeroClass unlockedHero = HeroClass.forCode(normalized);
        if (unlockedHero != null) {
            codeStatusText.setText(alreadyUnlocked
                    ? unlockedHero.displayName + " já estava liberado."
                    : "Novo personagem liberado: " + unlockedHero.displayName + "! Escolha em Nova partida.");
            refreshCodeList();
            return;
        }
        if (alreadyUnlocked) {
            codeStatusText.setText("Código já desbloqueado: " + normalized.toUpperCase());
        } else {
            codeStatusText.setText("Código desbloqueado: " + normalized.toUpperCase());
        }
        refreshCodeList();
    }

    private void updateBattleUi() {
        if (gameState == null) {
            return;
        }

        bossNameText.setText(gameState.getAreaName() + " — " + gameState.getBossName());
        updateArenaVisuals();
        bossHpText.setText(gameState.getBossHp() + "/" + gameState.getBossMaxHp());
        playerHpText.setText(gameState.getPlayerHp() + "/" + gameState.getPlayerMaxHp());
        healText.setText("Curas: " + gameState.getHealCharges() + "/3");
        animateBar(playerHealthBar, gameState.getPlayerMaxHp(), gameState.getPlayerHp());
        animateBar(bossHealthBar, gameState.getBossMaxHp(), gameState.getBossHp());
        // update card counter UI
        cardCountText.setText("Cartas totais: " + (gameState == null ? 0 : gameState.getPlayerCardCount()));
        // apply boss color to boss name label so bosses look distinct
        try {
            bossNameText.setTextColor(android.graphics.Color.parseColor(gameState.getBossColorHex()));
        } catch (Exception ex) {
            // ignore parsing errors and leave default color
        }
        boolean stunned = gameState.isPlayerStunned();
        swordButton.setEnabled(true);
        shieldButton.setEnabled(!stunned && !gameState.isPlayerParalyzed());
        healButton.setEnabled(!stunned && !gameState.isPlayerParalyzed() && gameState.getHealCharges() > 0);
        specialAttackButton.setEnabled(!stunned && !gameState.isPlayerParalyzed() && gameState.isSpecialAttackReady());
        // O botão é uma imagem: fica cinza quando desativado e com aro azul quando o escudo está ativo
        shieldButton.setActivated(!stunned && !gameState.isPlayerParalyzed() && gameState.isShieldActive());
        if (stunned) {
            shieldButton.setContentDescription("Escudo bloqueado");
        } else if (gameState.isPlayerParalyzed()) {
            shieldButton.setContentDescription("Paralisado");
        } else {
            shieldButton.setContentDescription(gameState.isShieldActive() ? "Escudo ativo" : "Escudo");
        }
        if (gameState.isSpecialAttackReady()) {
            specialAttackText.setText("Ataque especial: disponível");
            specialAttackCooldownText.setText("Disponível para uso");
        } else {
            specialAttackText.setText("Ataque especial: " + gameState.getSpecialAttackCooldown() + " turno(s) até ficar disponível");
            specialAttackCooldownText.setText(gameState.getSpecialAttackCooldown() + " turno(s) restantes");
        }

        if (battleStatusMessage == null || battleStatusMessage.isEmpty()) {
            battleStatusMessage = "Batalha contra " + gameState.getBossName() + ".";
        }
        statusText.setText(battleStatusMessage);
        updateEffectStatusUi();
        updateBattleFx();
    }

    // Escudo em volta do herói e partículas dos efeitos ativos (queimadura, veneno, void, atordoado)
    private void updateBattleFx() {
        if (gameState == null || battleLayout.getVisibility() != View.VISIBLE) {
            return;
        }
        if (gameState.isShieldActive()) {
            fx.setShield(heroSprite, true);
        } else if (!inputLocked) {
            fx.setShield(heroSprite, false);
        }
        fx.setAuras(bossSprite, gameState.getBossEffects().keySet(),
                heroSprite, gameState.getPlayerEffects().keySet(),
                gameState.isPlayerStunned() || gameState.isPlayerParalyzed());
    }

    // A barra de vida desce/sobe aos poucos em vez de pular
    private void animateBar(ProgressBar bar, int max, int value) {
        if (bar.getMax() != max) {
            bar.setMax(max);
        }
        Object running = bar.getTag();
        if (running instanceof android.animation.Animator) {
            ((android.animation.Animator) running).cancel();
        }
        int from = Math.min(bar.getProgress(), max);
        if (from == value || battleLayout.getVisibility() != View.VISIBLE) {
            bar.setProgress(value);
            return;
        }
        android.animation.ObjectAnimator anim = android.animation.ObjectAnimator.ofInt(bar, "progress", from, value);
        anim.setDuration(450);
        anim.setStartDelay(value < from ? 150 : 0);
        bar.setTag(anim);
        anim.start();
    }

    private void resetFxForScreenChange() {
        uiHandler.removeCallbacksAndMessages(null);
        inputLocked = false;
        bossDying = false;
        heroDying = false;
        fx.clearAll();
        fx.stopAllIdle();
    }

    private void lockInput(long ms) {
        inputLocked = true;
        uiHandler.removeCallbacks(unlockInput);
        uiHandler.postDelayed(unlockInput, ms);
    }

    /** Efeitos do turno do chefe, um pouco depois do golpe do herói. */
    private void playBossPhaseFx(final int bossHpBefore, final int heroHpBefore, final boolean shieldBefore) {
        if (gameState == null) {
            return;
        }
        final int heroLoss = heroHpBefore - gameState.getPlayerHp();
        final int bossDelta = gameState.getBossHp() - bossHpBefore;
        final boolean shieldUsed = shieldBefore && !gameState.isShieldActive();
        final boolean bossHealed = bossDelta > 0;
        final boolean critical = gameState.wasLastShieldCriticalFailure();
        final boolean bossDead = gameState.isBossDefeated();
        final String bossHint = gameState.getBossEffects().keySet().toString();
        final String heroHint = gameState.getPlayerEffects().keySet().toString();
        lockInput(1100);
        uiHandler.postDelayed(() -> {
            if (battleLayout.getVisibility() != View.VISIBLE) {
                return;
            }
            boolean attacked = !bossHealed && !bossDead && (heroLoss > 0 || shieldUsed);
            if (attacked) {
                int reflected = shieldUsed && bossDelta < 0 ? -bossDelta : 0;
                fx.bossAttack(bossSprite, heroSprite, heroLoss, critical && heroLoss > 0, reflected);
                if (!shieldUsed && bossDelta < 0) {
                    fx.hpChange(bossSprite, bossDelta, bossHint);
                }
            } else {
                if (bossDelta != 0) {
                    fx.hpChange(bossSprite, bossDelta, bossHint);
                }
                if (heroLoss > 0) {
                    fx.hpChange(heroSprite, -heroLoss, heroHint);
                }
            }
            // Escudo que acabou neste turno some depois do golpe
            uiHandler.postDelayed(() -> {
                if (gameState != null && !gameState.isShieldActive()) {
                    fx.setShield(heroSprite, false);
                }
            }, 200);
        }, 420);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Reaplica ao voltar de diálogos/notificações
            enterImmersiveMode();
        }
    }

    private void enterImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    // Afasta o conteúdo do entalhe da câmera e do teclado (no lugar do antigo adjustResize)
    private void setupScreenInsets() {
        View root = findViewById(R.id.rootLayout);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(cutout.left, cutout.top, cutout.right, Math.max(cutout.bottom, ime.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    // O tema Material pinta o fundo dos botões; aqui garantimos que a pixel art apareça sem tinta
    private void clearButtonTints(Button... buttons) {
        for (Button button : buttons) {
            if (button != null) {
                button.setBackgroundTintList(null);
            }
        }
    }

    // Carta de pergaminho: cor da faixa e ícone conforme a habilidade (espada, escudo ou especial)
    private void styleCardButton(Button button, BossCard card) {
        int background = R.drawable.carta_especial_sel;
        int icon = R.drawable.icone_carta_especial;
        if ("Espada".equals(card.getTargetSkill())) {
            background = R.drawable.carta_espada_sel;
            icon = R.drawable.icone_carta_espada;
        } else if ("Escudo".equals(card.getTargetSkill())) {
            background = R.drawable.carta_escudo_sel;
            icon = R.drawable.icone_carta_escudo;
        }
        // setBackgroundResource apaga o padding; guardamos e reaplicamos o do layout
        int left = button.getPaddingLeft(), top = button.getPaddingTop(), right = button.getPaddingRight(), bottom = button.getPaddingBottom();
        button.setBackgroundResource(background);
        button.setPadding(left, top, right, bottom);
        button.setBackgroundTintList(null);
        android.graphics.drawable.Drawable iconDrawable = ResourcesCompat.getDrawable(getResources(), icon, getTheme());
        if (iconDrawable != null) {
            int size = dp(40);
            int width = Math.round(size * iconDrawable.getIntrinsicWidth() / (float) Math.max(1, iconDrawable.getIntrinsicHeight()));
            iconDrawable.setBounds(0, 0, width, size);
            button.setCompoundDrawables(null, iconDrawable, null, null);
            button.setCompoundDrawablePadding(dp(6));
        }
        // Primeira linha (nome da carta) em destaque
        CharSequence text = button.getText();
        if (text != null) {
            String full = text.toString();
            int lineEnd = full.indexOf('\n');
            if (lineEnd > 0) {
                android.text.SpannableString styled = new android.text.SpannableString(full);
                styled.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, lineEnd, 0);
                styled.setSpan(new android.text.style.RelativeSizeSpan(1.15f), 0, lineEnd, 0);
                styled.setSpan(new android.text.style.ForegroundColorSpan(android.graphics.Color.parseColor("#672040")), 0, lineEnd, 0);
                button.setText(styled);
            }
        }
    }

    private void updateArenaVisuals() {
        if (gameState == null) {
            return;
        }
        // Só troca as imagens quando muda de área/chefe (evita recarregar a cada turno)
        int areaRes = getAreaDrawable(gameState.getAreaIndex());
        if (areaRes != currentAreaRes) {
            arenaBackground.setImageResource(areaRes);
            currentAreaRes = areaRes;
        }
        int bossRes = getBossDrawable(gameState.getBossSpriteKey());
        if (bossRes != currentBossRes) {
            setSprite(bossSprite, bossRes);
            currentBossRes = bossRes;
        }
    }

    // Ajusta a altura da imagem para todos os personagens terem o mesmo tamanho de pixel
    private void setSprite(ImageView view, int drawableRes) {
        view.setImageResource(drawableRes);
        android.graphics.drawable.Drawable drawable = view.getDrawable();
        if (drawable == null) {
            return;
        }
        int nativeHeight = drawable.getIntrinsicHeight() / SPRITE_EXPORT_SCALE;
        float density = getResources().getDisplayMetrics().density;
        android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = Math.round(nativeHeight * SPRITE_PIXEL_DP * density);
        view.setLayoutParams(params);
    }

    private int getAreaDrawable(int areaIndex) {
        switch (areaIndex) {
            case 1: return R.drawable.area_planicie;
            case 2: return R.drawable.area_floresta;
            case 3: return R.drawable.area_caverna;
            default: return R.drawable.area_reino;
        }
    }

    private int getBossDrawable(String spriteKey) {
        if (spriteKey == null) {
            return R.drawable.chefe_bandido;
        }
        switch (spriteKey) {
            case "lobo": return R.drawable.chefe_lobo;
            case "goblin": return R.drawable.chefe_goblin;
            case "orc": return R.drawable.chefe_orc;
            case "bandido_lider": return R.drawable.chefe_bandido_lider;
            case "esqueleto_forte": return R.drawable.chefe_esqueleto_forte;
            case "goblin_rei": return R.drawable.chefe_goblin_rei;
            case "esqueleto_rei": return R.drawable.chefe_esqueleto_rei;
            case "dragao": return R.drawable.chefe_dragao;
            default: return R.drawable.chefe_bandido;
        }
    }

    private void updateEffectStatusUi() {
        if (gameState == null) {
            return;
        }

        bossStatusText.setText(buildCompactEffectText("Boss", gameState.getBossEffects()));
        String playerStatus = buildCompactEffectText("Você", gameState.getPlayerEffects());
        if (gameState.isPlayerStunned()) {
            playerStatus += " | Atordoado: apenas ataque disponível";
        }
        if (gameState.isPlayerParalyzed()) {
            playerStatus += " | Paralisado: " + gameState.getParalysisTurns() + " turno(s) restante(s)";
        }
        playerStatusText.setText(playerStatus);
    }

    private int getCardLevelLabel(BossCard card) {
        if (gameState == null || card == null) {
            return 0;
        }
        return gameState.getPlayerCardLevel(card.getName());
    }

    private String buildPlayerCardSummary() {
        if (gameState == null) return "Cartas: 0";
        java.util.List<BossCard> owned = gameState.getPlayerCards();
        if (owned.isEmpty()) return "Cartas: nenhuma";
        java.util.Map<String, Integer> counts = new java.util.LinkedHashMap<>();
        for (BossCard c : owned) {
            counts.put(c.getName(), counts.getOrDefault(c.getName(), 0) + 1);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Cartas:\n");
        for (java.util.Map.Entry<String, Integer> e : counts.entrySet()) {
            int level = gameState.getPlayerCardLevel(e.getKey());
            sb.append(e.getKey()).append(": ").append(e.getValue()).append("/").append(GameState.MAX_COPIES_PER_CARD)
                    .append(" (nível ").append(level).append("/3)").append("\n");
        }
        return sb.toString();
    }

    private String buildHpTextWithEffects(String label, int hp, int maxHp, java.util.Map<String, Integer> effects) {
        String base = label + ": " + hp + "/" + maxHp;
        if (effects == null || effects.isEmpty()) {
            return base;
        }
        StringBuilder sb = new StringBuilder(base);
        sb.append("  [");
        boolean first = true;
        for (java.util.Map.Entry<String, Integer> e : effects.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(e.getKey()).append(": ").append(e.getValue()).append("T");
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private String buildCompactEffectText(String label, java.util.Map<String, Integer> effects) {
        if (effects == null || effects.isEmpty()) {
            return label + ": sem efeitos ativos";
        }

        StringBuilder builder = new StringBuilder();
        for (java.util.Map.Entry<String, Integer> entry : effects.entrySet()) {
            if (builder.length() > 0) {
                builder.append(" | ");
            }
            builder.append(entry.getKey()).append(" (").append(entry.getValue()).append("T)");
        }
        return builder.toString();
    }

    private void handlePlayerAction(String action) {
        if (gameState == null || inputLocked) {
            return;
        }

        if (gameState.isPlayerParalyzed()) {
            battleStatusMessage = "Você está paralisado por Falha Crítica e não pode agir neste turno.";
            updateBattleUi();
            return;
        }

        boolean stunned = gameState.isPlayerStunned();
        if (stunned && !"sword".equals(action)) {
            battleStatusMessage = "Você está atordoado e só pode atacar neste turno.";
            updateBattleUi();
            return;
        }

        final int bossHp0 = gameState.getBossHp();
        final int heroHp0 = gameState.getPlayerHp();
        final boolean shield0 = gameState.isShieldActive();

        if ("sword".equals(action)) {
            int baseDamage = 15;
            int damage = gameState.getEffectiveSwordDamage(baseDamage);
            if (stunned) {
                damage = gameState.getStunnedAttackDamage(damage);
                gameState.setBossHp(gameState.getBossHp() - damage);
                battleStatusMessage = "Você está atordoado e atacou com dano reduzido: " + damage + " de dano.";
            } else {
                gameState.setBossHp(gameState.getBossHp() - damage);
                int healAmount = gameState.getLifeStealBonus();
                if (healAmount > 0) {
                    gameState.restorePlayerHp(healAmount);
                }
                battleStatusMessage = "Você usou espada e causou " + damage + " de dano.";
                if (healAmount > 0) {
                    battleStatusMessage += "\nRoubo de vida: recuperou " + healAmount + " de vida.";
                }
                if (gameState.hasSwordStatusEffect()) {
                    boolean statusTriggered = gameState.triggerSwordStatusEffect(random);
                    if (statusTriggered) {
                        battleStatusMessage += "\nEfeito aplicado: " + gameState.getSwordStatusEffect() + " por " + gameState.getSwordStatusDuration() + " turno(s).";
                    } else {
                        battleStatusMessage += "\nEfeito " + gameState.getSwordStatusEffect() + " não acertou (chance " + gameState.getStatusTriggerChance(gameState.getSwordStatusLevel()) + "%).";
                    }
                }
                if (random.nextInt(100) < 25 && !gameState.hasSwordStatusEffect()) {
                    String[] effects = {"Queimadura", "Veneno", "Void"};
                    String effect = effects[random.nextInt(effects.length)];
                    gameState.applyEnemyEffect(effect, 2);
                    battleStatusMessage += "\nEfeito aplicado: " + effect + " por 2 turnos.";
                }
            }
        } else if ("heal".equals(action)) {
            if (gameState.getHealCharges() <= 0) {
                battleStatusMessage = "Você não tem mais curas disponíveis.";
                updateBattleUi();
                return;
            }
            gameState.spendHeal();
            int healedHp = 20 + random.nextInt(8);
            gameState.restorePlayerHp(healedHp);
            battleStatusMessage = "Você usou cura e recuperou " + healedHp + " de vida.";
        }

        // Efeito do golpe/cura do herói
        final int bossHpMid = gameState.getBossHp();
        final int heroHpMid = gameState.getPlayerHp();
        if ("sword".equals(action)) {
            if (currentHeroClass == HeroClass.MAGO) {
                fx.magic(heroSprite, bossSprite, bossHp0 - bossHpMid, Math.max(0, heroHpMid - heroHp0));
            } else {
                fx.sword(heroSprite, bossSprite, bossHp0 - bossHpMid, Math.max(0, heroHpMid - heroHp0));
            }
        } else {
            fx.heal(heroSprite, heroHpMid - heroHp0);
        }
        lockInput(500);

        if (gameState.isBossDefeated()) {
            onBossDefeated();
            return;
        }

        bossTurn();
        updateBattleUi();
        playBossPhaseFx(bossHpMid, heroHpMid, shield0);
    }

    private void useShield() {
        if (gameState == null || inputLocked) {
            return;
        }
        if (gameState.isPlayerParalyzed()) {
            battleStatusMessage = "Você está paralisado por Falha Crítica e não pode agir neste turno.";
            updateBattleUi();
            return;
        }
        if (gameState.isPlayerStunned()) {
            battleStatusMessage = "Você está atordoado e não pode usar escudo neste turno.";
            updateBattleUi();
            return;
        }
        boolean wasActive = gameState.isShieldActive();
        gameState.activateShield();
        battleStatusMessage = "Você ativou o escudo para reduzir o próximo ataque.";
        if (!wasActive) {
            fx.shieldUp(heroSprite);
        }
        updateBattleUi();
    }

    private void useSpecialAttack() {
        if (gameState == null || inputLocked) {
            return;
        }
        if (gameState.isPlayerParalyzed()) {
            battleStatusMessage = "Você está paralisado por Falha Crítica e não pode agir neste turno.";
            updateBattleUi();
            return;
        }
        if (gameState.isPlayerStunned()) {
            battleStatusMessage = "Você está atordoado e não pode usar ataque especial neste turno.";
            updateBattleUi();
            return;
        }
        if (!gameState.isSpecialAttackReady()) {
            battleStatusMessage = "Ataque especial indisponível por " + gameState.getSpecialAttackCooldown() + " turno(s).";
            updateBattleUi();
            return;
        }

        final int bossHp0 = gameState.getBossHp();
        final boolean shield0 = gameState.isShieldActive();
        int baseDamage = 30 + random.nextInt(18);
        int damage = gameState.getEffectiveSpecialDamage(baseDamage);
        gameState.useSpecialAttack();
        gameState.setBossHp(gameState.getBossHp() - damage);
        if (gameState.hasSpecialStatusEffect()) {
            boolean stunned = gameState.triggerSpecialStatusEffect(random);
            if (stunned) {
                battleStatusMessage = "Ataque especial! Você causou " + damage + " de dano e aplicou " + gameState.getSpecialStatusEffect() + " por " + gameState.getSpecialStatusDuration() + " turno(s).";
            } else {
                battleStatusMessage = "Ataque especial! Você causou " + damage + " de dano, mas o efeito " + gameState.getSpecialStatusEffect() + " não acertou (chance " + gameState.getStatusTriggerChance(gameState.getSpecialStatusLevel()) + "%).";
            }
        } else {
            gameState.applyEnemyEffect("Queimadura", 3);
            battleStatusMessage = "Ataque especial! Você causou " + damage + " de dano e aplicou Queimadura por 3 turnos.";
        }

        final int bossHpMid = gameState.getBossHp();
        final int heroHpMid = gameState.getPlayerHp();
        fx.special(heroSprite, bossSprite, battleArena, bossHp0 - bossHpMid,
                currentHeroClass == HeroClass.MAGO ? BattleFx.COR_VOID : android.graphics.Color.WHITE);
        lockInput(600);

        if (gameState.isBossDefeated()) {
            onBossDefeated();
            return;
        }

        bossTurn();
        updateBattleUi();
        playBossPhaseFx(bossHpMid, heroHpMid, shield0);
    }

    private void bossTurn() {
        if (gameState == null || gameState.isPlayerDefeated()) {
            return;
        }

        // If boss is stunned (Atordoou), it should skip its action this turn
        if (gameState.hasBossEffect("Atordoou") || gameState.getBossEffectDuration("Atordoou") > 0) {
            battleStatusMessage = (battleStatusMessage == null ? "" : battleStatusMessage) + "\nChefe atordoado e não agiu.";
            gameState.advanceTurn();
            updateBattleUi();
            if (gameState.isBossDefeated()) {
                onBossDefeated();
                return;
            }
            if (gameState.isPlayerDefeated()) {
                battleStatusMessage = "Você foi derrotado por efeitos. Tente novamente.";
                onPlayerDefeated();
                saveProgress();
                return;
            }
            return;
        }

        if (gameState.canBossUseHeal() && gameState.getBossHp() <= gameState.getBossMaxHp() / 2 && random.nextInt(100) < 35) {
            int healed = gameState.useBossHeal(random);
            if (healed > 0) {
                battleStatusMessage = (battleStatusMessage == null ? "" : battleStatusMessage) + "\nBoss usou Cura e recuperou " + healed + " de vida.";
                gameState.advanceTurn();
                updateBattleUi();
                return;
            }
        }

        if (random.nextInt(100) < 25) {
            String defenseEffect = gameState.getBossDefenseEffectName();
            if (defenseEffect != null && !defenseEffect.trim().isEmpty()) {
                gameState.useBossDefense();
                battleStatusMessage = (battleStatusMessage == null ? "" : battleStatusMessage) + "\nBoss ativou defesa e aplicou " + defenseEffect + " por " + gameState.getBossDefenseEffectDuration() + " turno(s).";
            }
        }

        int base = 8 + random.nextInt(10) + gameState.getBossIndex() * 2;
        int damage = base;
        switch (gameState.getBossAttackPattern()) {
            case 1:
                if (random.nextInt(100) < 30) damage = (int)Math.round(base * 1.8); break;
            case 2:
                int single = Math.max(1, base / 2);
                int reflected1 = gameState.applyEnemyAttack(single, random);
                int reflected2 = gameState.applyEnemyAttack(single, random);
                int totalReflected = reflected1 + reflected2;
                if (totalReflected > 0) {
                    battleStatusMessage = (battleStatusMessage == null ? "" : battleStatusMessage) + "\nEscudo refletiu " + totalReflected + " de dano ao oponente. O chefe causou " + (single + single - totalReflected) + " de dano ao jogador.";
                } else {
                    battleStatusMessage = (battleStatusMessage == null ? "" : battleStatusMessage) + "\nO chefe atacou duas vezes e causou " + (single + single) + " de dano.";
                }
                gameState.advanceTurn();
                if (gameState.isBossDefeated()) { onBossDefeated(); return; }
                if (gameState.isPlayerDefeated()) { battleStatusMessage = "Você foi derrotado. Tente novamente."; onPlayerDefeated(); saveProgress(); return; }
                return;
            case 3:
                if (random.nextInt(100) < 40) damage = base + 6; break;
            case 4:
                damage = base + 3; break;
            default:
                damage = base; break;
        }

        int reflectedDamage = gameState.applyEnemyAttack(damage, random);
        if (gameState.isPlayerDefeated()) {
            battleStatusMessage = "Você foi derrotado. Tente novamente.";
            onPlayerDefeated();
            highestBossIndex = Math.max(0, highestBossIndex);
            saveProgress();
            return;
        }

        String attackEffect = gameState.getBossAttackEffectName();
        if (attackEffect != null && !attackEffect.trim().isEmpty()) {
            gameState.applyPlayerEffect(attackEffect, gameState.getBossAttackEffectDuration());
            battleStatusMessage = (battleStatusMessage == null ? "" : battleStatusMessage) + "\nBoss usou " + attackEffect + " e aplicou o efeito ao jogador por " + gameState.getBossAttackEffectDuration() + " turno(s).";
        }

        if (gameState.wasLastShieldCriticalFailure()) {
            battleStatusMessage = battleStatusMessage + "\nFalha Crítica! O escudo falhou e você recebeu " + (damage * 2) + " de dano. Você ficou paralisado por 1 turno.";
        } else if (reflectedDamage > 0) {
            battleStatusMessage = battleStatusMessage + "\nEscudo refletiu " + reflectedDamage + " de dano ao oponente. O chefe causou " + (damage - reflectedDamage) + " de dano ao jogador.";
        } else {
            battleStatusMessage = battleStatusMessage + "\nO chefe atacou e causou " + damage + " de dano.";
        }

        gameState.advanceTurn();
        if (gameState.isBossDefeated()) {
            onBossDefeated();
            return;
        }
        if (gameState.isPlayerDefeated()) {
            battleStatusMessage = "Você foi derrotado por efeitos. Tente novamente.";
            onPlayerDefeated();
            saveProgress();
            return;
        }
    }

    private void onBossDefeated() {
        if (bossDying || gameState == null) {
            return;
        }
        bossDying = true;
        inputLocked = true;
        uiHandler.removeCallbacks(unlockInput);
        // Salva já, antes da animação (se o app fechar no meio, o progresso não se perde)
        highestBossIndex = Math.max(highestBossIndex, gameState.getBossIndex() + 1);
        saveProgress();
        updateBattleUi();
        uiHandler.postDelayed(() -> fx.bossDeath(bossSprite, () -> {
            bossDying = false;
            showBossDefeatedScreen();
            fx.victory();
        }), 300);
    }

    // Herói caiu: mostra "Derrota" e depois volta ao menu
    private void onPlayerDefeated() {
        if (heroDying) {
            return;
        }
        heroDying = true;
        inputLocked = true;
        uiHandler.removeCallbacks(unlockInput);
        // Perdeu a luta: recomeça do primeiro chefe e sem as cartas (salva já, antes da animação)
        highestBossIndex = 0;
        saveProgress();
        uiHandler.postDelayed(() -> fx.heroDefeat(heroSprite, () -> {
            heroDying = false;
            gameState = null;
            currentDraft = null;
            showMenu();
        }), 450);
    }

    private void showBossDefeatedScreen() {
        if (gameState == null) {
            showMenu();
            return;
        }
        int clearedBoss = gameState.getBossIndex() + 1;
        highestBossIndex = Math.max(highestBossIndex, clearedBoss);
        saveProgress();

        // Update UI state so effect indicators show correct remaining durations before showing victory
        updateBattleUi();

        // Always show a victory screen that confirms the boss was defeated and offers to continue
        if (clearedBoss >= getCurrentTotalBosses()) {
            // Final boss defeated for this session
            victoryText.setText("Vitória! Você derrotou os " + getCurrentTotalBosses() + " chefes e concluiu a sequência final.");
            showVictory();
            nextBossButton.setText("Nova partida");
            nextBossButton.setOnClickListener(v -> {
                // Start a fresh run from scratch
                openNewGame();
            });
        } else {
            victoryText.setText("Chefe derrotado! Você venceu esse chefe.\nSua vida será restaurada para a próxima luta.");
            showVictory();
            nextBossButton.setText("Continuar");
            nextBossButton.setOnClickListener(v -> {
                // Continue to card selection before next boss
                showCardSelection();
            });
        }
    }

    private void applySelectedCard(int index) {
        if (currentDraft == null || currentDraft.size() <= index) {
            // fallback to fresh draft if something went wrong
            currentDraft = cardManager.getDraftCards();
        }
        BossCard card = currentDraft.get(index);
        // Prevent applying a card to a wrong skill category - applyCard already maps by targetSkill,
        // but ensure the card belongs to one of allowed categories
        String target = card.getTargetSkill();
        if (!("Espada".equals(target) || "Escudo".equals(target) || "Ataque especial".equals(target))) {
            battleStatusMessage = "Carta incompatível. Nenhuma ação aplicada.";
            showBattle();
            updateBattleUi();
            return;
        }
        // Check per-card copies limit (max 3 copies per same card)
        if (!gameState.canAddPlayerCard(card)) {
            int copies = gameState.getPlayerCardCopies(card.getName());
            new AlertDialog.Builder(this)
                    .setTitle("Limite de cópias")
                    .setMessage("Você já possui " + copies + "/" + GameState.MAX_COPIES_PER_CARD + " cópias da carta \"" + card.getName() + "\". Não é possível adquirir mais dessa carta.")
                    .setPositiveButton("OK", null)
                    .show();
            // stay on victory/selection screen
            showVictory();
            updateBattleUi();
            return;
        }

        boolean added = gameState.addPlayerCard(card);
        if (added) {
            // apply its immediate effects (bonuses/status durations)
            gameState.applyCard(card);
            battleStatusMessage = "Carta adquirida: " + card.getName() + " (" + card.getTargetSkill() + "). " + card.getDescription();
            if (card.getStatusEffectName() != null && !card.getStatusEffectName().trim().isEmpty()) {
                battleStatusMessage += "\nEfeito adicionado: " + card.getStatusEffectName() + " por " + card.getStatusEffectDuration() + " turno(s).";
            }
            // Update counter immediately in UI
            updateBattleUi();
        } else {
            battleStatusMessage = "Falha ao adquirir carta.";
        }
        // Clear current draft and proceed to next boss
        currentDraft = null;
        advanceToNextBoss();
    }

    private void pauseBattle() {
        if (gameState == null) {
            return;
        }
        String msg = "A batalha foi pausada.\n" + buildPlayerCardSummary();
        new AlertDialog.Builder(this)
                .setTitle("Pausa")
                .setMessage(msg)
                .setPositiveButton("Continuar", null)
                .setNeutralButton("Menu principal", (dialog, which) -> showMenu())
                .show();
    }

    private void advanceToNextBoss() {
        if (gameState == null) {
            return;
        }

        if (gameState.getBossIndex() >= getCurrentTotalBosses() - 1) {
            showMenu();
            return;
        }

        final int hpBeforeRegen = gameState.getPlayerHp();
        gameState.prepareNextBoss();
        // ensure GameState knows current session total
        gameState.setSessionTotalBosses(getCurrentTotalBosses());
        battleStatusMessage = "Batalha contra " + gameState.getBossName() + ".\n" + buildPlayerCardSummary();
        showBattle();
        updateBattleUi();
        // Vida regenerada ao vencer: mostra a cura no herói quando a luta começa
        final int regen = gameState.getPlayerHp() - hpBeforeRegen;
        if (regen > 0) {
            uiHandler.postDelayed(() -> fx.heal(heroSprite, regen), 550);
        }
    }

    private void saveProgress() {
        preferences.edit().putInt(KEY_BOSS_INDEX, highestBossIndex).apply();
    }

    // As músicas ficam fora do repositório público (direitos autorais).
    // Busca o arquivo em res/raw pelo nome; se não existir, o jogo roda sem música.
    private MediaPlayer createMusicPlayer(String rawName) {
        int resId = getResources().getIdentifier(rawName, "raw", getPackageName());
        if (resId == 0) {
            return null;
        }
        return MediaPlayer.create(this, resId);
    }

    private void initializeBattleMusic() {
        if (battleMusicPlayer != null) {
            applyMusicVolume();
            return;
        }
        try {
            battleMusicPlayer = createMusicPlayer("megalovania");
            if (battleMusicPlayer != null) {
                battleMusicPlayer.setLooping(true);
                battleMusicPlayer.setVolume(0.0f, 0.0f);
                applyMusicVolume();
            }
        } catch (Exception ignored) {
            battleMusicPlayer = null;
        }
    }

    private void initializeMenuMusic() {
        if (menuMusicPlayer != null) {
            applyMusicVolume();
            return;
        }
        try {
            menuMusicPlayer = createMusicPlayer("mugetsu");
            if (menuMusicPlayer != null) {
                menuMusicPlayer.setLooping(true);
                menuMusicPlayer.setVolume(0.0f, 0.0f);
                applyMusicVolume();
            }
        } catch (Exception ignored) {
            menuMusicPlayer = null;
        }
    }

    private void applyMusicVolume() {
        if (battleMusicPlayer != null) {
            float volume = 0f;
            if (soundOn && currentMusicVolume > 0) {
                volume = currentMusicVolume / 100f;
            }
            battleMusicPlayer.setVolume(volume, volume);
        }
        if (menuMusicPlayer != null) {
            float volume = 0f;
            if (soundOn && currentMusicVolume > 0) {
                volume = currentMusicVolume / 100f;
            }
            menuMusicPlayer.setVolume(volume, volume);
        }
    }

    private void startBattleMusic() {
        stopMenuMusic();
        initializeBattleMusic();
        if (battleMusicPlayer == null) {
            return;
        }
        if (!soundOn || currentMusicVolume <= 0) {
            battleMusicPlayer.setVolume(0f, 0f);
            return;
        }
        if (battleMusicPlayer.isPlaying()) {
            return;
        }
        battleMusicPlayer.seekTo(0);
        battleMusicPlayer.start();
        applyMusicVolume();
    }

    private void startMenuMusic() {
        stopBattleMusic();
        initializeMenuMusic();
        if (menuMusicPlayer == null) {
            return;
        }
        if (!soundOn || currentMusicVolume <= 0) {
            menuMusicPlayer.setVolume(0f, 0f);
            return;
        }
        if (menuMusicPlayer.isPlaying()) {
            return;
        }
        menuMusicPlayer.seekTo(0);
        menuMusicPlayer.start();
        applyMusicVolume();
    }

    private void stopBattleMusic() {
        if (battleMusicPlayer == null) {
            return;
        }
        if (battleMusicPlayer.isPlaying()) {
            battleMusicPlayer.pause();
        }
        battleMusicPlayer.seekTo(0);
        battleMusicPlayer.setVolume(0f, 0f);
    }

    private void stopMenuMusic() {
        if (menuMusicPlayer == null) {
            return;
        }
        if (menuMusicPlayer.isPlaying()) {
            menuMusicPlayer.pause();
        }
        menuMusicPlayer.seekTo(0);
        menuMusicPlayer.setVolume(0f, 0f);
    }

    private void releaseBattleMusic() {
        if (battleMusicPlayer == null) {
            return;
        }
        stopBattleMusic();
        battleMusicPlayer.release();
        battleMusicPlayer = null;
    }

    private void releaseMenuMusic() {
        if (menuMusicPlayer == null) {
            return;
        }
        stopMenuMusic();
        menuMusicPlayer.release();
        menuMusicPlayer = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopBattleMusic();
        stopMenuMusic();
        if (fx != null) {
            fx.stopMenu();
            fx.stopAuras();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fx == null) {
            return;
        }
        if (menuLayout.getVisibility() == View.VISIBLE) {
            fx.startMenu(menuLogo, null);
        } else if (battleLayout.getVisibility() == View.VISIBLE) {
            updateBattleFx();
        }
    }

    @Override
    protected void onDestroy() {
        releaseBattleMusic();
        releaseMenuMusic();
        super.onDestroy();
    }
}
