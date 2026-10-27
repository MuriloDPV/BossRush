package com.example.bossrush;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Efeitos visuais "por cima" do jogo (corte, explosão, cura, escudo, números de dano,
 * partículas da tela inicial...). Tudo é desenhado numa camada transparente que fica
 * acima de todas as telas e não bloqueia os toques. Não mexe nas regras do jogo.
 */
final class BattleFx {

    // Mesmo padrão dos personagens: cada pixel do desenho vira 2.4dp; PNGs exportados 8x
    private static final float PIXEL_DP = 2.4f;
    private static final int EXPORT_SCALE = 8;
    private static final int MAX_CHILDREN = 70;

    // Cores da paleta do jogo
    static final int COR_DANO_CHEFE = Color.parseColor("#FFE08A");
    static final int COR_DANO_ESPECIAL = Color.parseColor("#FF7A2E");
    static final int COR_DANO_HEROI = Color.parseColor("#E04A4A");
    static final int COR_CURA = Color.parseColor("#7CDB6A");
    static final int COR_ESCUDO = Color.parseColor("#77B3E6");
    static final int COR_VENENO = Color.parseColor("#9BD46A");
    static final int COR_VOID = Color.parseColor("#B99AF2");
    private static final int COR_CONTORNO = Color.parseColor("#16142E");

    private static final int[] CORTE = {R.drawable.fx_corte_1, R.drawable.fx_corte_2, R.drawable.fx_corte_3, R.drawable.fx_corte_4};
    private static final int[] EXPLOSAO = {R.drawable.fx_explosao_1, R.drawable.fx_explosao_2, R.drawable.fx_explosao_3, R.drawable.fx_explosao_4, R.drawable.fx_explosao_5};
    private static final int[] IMPACTO = {R.drawable.fx_impacto_1, R.drawable.fx_impacto_2, R.drawable.fx_impacto_3};

    private final Activity activity;
    private final FrameLayout layer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private final float density;
    private Typeface numberFont;
    private Typeface titleFont;

    // Escudo que fica em volta do herói enquanto está ativo
    private ImageView shieldView;
    private ObjectAnimator shieldPulse;

    // Partículas de status (queimadura, veneno, void, atordoado)
    private final Set<String> bossAura = new HashSet<>();
    private final Set<String> heroAura = new HashSet<>();
    private View auraBoss;
    private View auraHero;
    private boolean auraRunning;

    // Tela inicial
    private boolean menuRunning;
    private ObjectAnimator logoFloat;
    private long nextShootingStar;

    // Respiração dos personagens
    private final Map<View, ObjectAnimator> idle = new HashMap<>();

    BattleFx(Activity activity, FrameLayout layer) {
        this.activity = activity;
        this.layer = layer;
        this.density = activity.getResources().getDisplayMetrics().density;
        layer.setClickable(false);
        layer.setFocusable(false);
        try {
            numberFont = ResourcesCompat.getFont(activity, R.font.pixelify_bold);
            titleFont = ResourcesCompat.getFont(activity, R.font.jacquard);
        } catch (Exception ignored) {
            numberFont = Typeface.DEFAULT_BOLD;
            titleFont = Typeface.DEFAULT_BOLD;
        }
    }

    // ------------------------------------------------------------------ utilidades

    private float dp(float v) {
        return v * density;
    }

    /** Centro de uma view em coordenadas da camada de efeitos. */
    private float[] centerOf(View v) {
        int[] a = new int[2];
        int[] b = new int[2];
        v.getLocationInWindow(a);
        layer.getLocationInWindow(b);
        return new float[]{a[0] - b[0] + v.getWidth() / 2f, a[1] - b[1] + v.getHeight() / 2f};
    }

    private boolean full() {
        return layer.getChildCount() >= MAX_CHILDREN;
    }

    private ImageView sprite(int res, float scale) {
        ImageView iv = new ImageView(activity);
        iv.setImageResource(res);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        Drawable d = iv.getDrawable();
        int w = 1, h = 1;
        if (d != null) {
            w = Math.max(1, Math.round(d.getIntrinsicWidth() / (float) EXPORT_SCALE * dp(PIXEL_DP) * scale));
            h = Math.max(1, Math.round(d.getIntrinsicHeight() / (float) EXPORT_SCALE * dp(PIXEL_DP) * scale));
        }
        layer.addView(iv, new FrameLayout.LayoutParams(w, h));
        return iv;
    }

    private void placeCenter(View v, float cx, float cy) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        v.setX(cx - lp.width / 2f);
        v.setY(cy - lp.height / 2f);
    }

    private void remove(View v) {
        if (v != null && v.getParent() == layer) {
            v.animate().cancel();
            layer.removeView(v);
        }
    }

    /** Desenho em pixel art com tamanho fixo e sem borrar (para botões de configurações). */
    Drawable pixelDrawable(int res, float pixelDp) {
        Bitmap src = BitmapFactory.decodeResource(activity.getResources(), res);
        if (src == null) {
            return null;
        }
        int w = Math.max(1, Math.round(src.getWidth() / (float) EXPORT_SCALE * dp(pixelDp)));
        int h = Math.max(1, Math.round(src.getHeight() / (float) EXPORT_SCALE * dp(pixelDp)));
        Bitmap scaled = Bitmap.createScaledBitmap(src, w, h, false);
        BitmapDrawable drawable = new BitmapDrawable(activity.getResources(), scaled);
        drawable.setFilterBitmap(false);
        return drawable;
    }

    /** Anima uma sequência de quadros no ponto (cx, cy). */
    private void frames(int[] res, float cx, float cy, float scale, long frameMs, boolean flipX) {
        if (full()) {
            return;
        }
        final ImageView iv = sprite(res[0], scale);
        placeCenter(iv, cx, cy);
        if (flipX) {
            iv.setScaleX(-1f);
        }
        for (int i = 1; i < res.length; i++) {
            final int frame = res[i];
            handler.postDelayed(() -> iv.setImageResource(frame), i * frameMs);
        }
        handler.postDelayed(() -> remove(iv), res.length * frameMs);
    }

    private void particle(int res, float x, float y, float dx, float dy, long duration, float scale) {
        if (full()) {
            return;
        }
        final ImageView iv = sprite(res, scale);
        placeCenter(iv, x, y);
        iv.animate()
                .translationXBy(dx)
                .translationYBy(dy)
                .alpha(0f)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> remove(iv))
                .start();
    }

    /** Pedacinhos quadrados (1 pixel do desenho) que voam para fora. */
    private void debris(float x, float y, int color, float distance, long duration) {
        if (full()) {
            return;
        }
        final View px = new View(activity);
        px.setBackgroundColor(color);
        int size = Math.max(2, Math.round(dp(PIXEL_DP)));
        layer.addView(px, new FrameLayout.LayoutParams(size, size));
        px.setX(x - size / 2f);
        px.setY(y - size / 2f);
        double ang = random.nextDouble() * Math.PI * 2;
        float dist = distance * (0.5f + random.nextFloat() * 0.5f);
        px.animate()
                .translationXBy((float) Math.cos(ang) * dist)
                .translationYBy((float) Math.sin(ang) * dist + dp(14))
                .alpha(0f)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> remove(px))
                .start();
    }

    private void burst(int res, float cx, float cy, int count, float distance, long duration, float scale) {
        for (int i = 0; i < count; i++) {
            double ang = random.nextDouble() * Math.PI * 2;
            float dist = distance * (0.5f + random.nextFloat() * 0.5f);
            particle(res, cx, cy, (float) Math.cos(ang) * dist, (float) Math.sin(ang) * dist, duration, scale);
        }
    }

    /** Número que pula e sobe (dano, cura, reflexo...). */
    void number(View target, String text, int color, float sizeSp) {
        if (target == null || full()) {
            return;
        }
        float[] c = centerOf(target);
        final TextView tv = new TextView(activity);
        tv.setText(text);
        tv.setTextColor(color);
        tv.setTextSize(sizeSp);
        tv.setTypeface(numberFont);
        tv.setShadowLayer(1f, dp(2), dp(2), COR_CONTORNO);
        tv.setGravity(Gravity.CENTER);
        tv.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        layer.addView(tv, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        float jitter = (random.nextFloat() - 0.5f) * dp(24);
        tv.setX(c[0] - tv.getMeasuredWidth() / 2f + jitter);
        tv.setY(c[1] - target.getHeight() / 2f - tv.getMeasuredHeight() * 0.4f);
        tv.setScaleX(1.7f);
        tv.setScaleY(1.7f);
        tv.animate().scaleX(1f).scaleY(1f).setDuration(160).setInterpolator(new OvershootInterpolator()).start();
        tv.animate()
                .translationYBy(-dp(46))
                .alpha(0f)
                .setStartDelay(260)
                .setDuration(820)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> remove(tv))
                .start();
    }

    /** O personagem pisca numa cor (branco = levou golpe). */
    void flash(final ImageView v, final int color) {
        if (v == null) {
            return;
        }
        v.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        handler.postDelayed(v::clearColorFilter, 70);
        handler.postDelayed(() -> v.setColorFilter(color, PorterDuff.Mode.SRC_ATOP), 140);
        handler.postDelayed(v::clearColorFilter, 210);
    }

    void shake(View v, float amountDp) {
        if (v == null) {
            return;
        }
        float d = dp(amountDp);
        ObjectAnimator a = ObjectAnimator.ofFloat(v, "translationX", 0f, d, -d, d * 0.6f, -d * 0.6f, d * 0.3f, 0f);
        a.setDuration(300);
        a.start();
    }

    void screenFlash(int color, float alpha, long duration) {
        final View f = new View(activity);
        f.setBackgroundColor(color);
        layer.addView(f, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        f.setAlpha(alpha);
        f.animate().alpha(0f).setDuration(duration).withEndAction(() -> remove(f)).start();
    }

    /** Avança na direção do alvo e volta; no meio do avanço acontece o golpe. */
    private void lunge(final View v, float dxDp, final Runnable atHit) {
        if (v == null) {
            if (atHit != null) atHit.run();
            return;
        }
        v.animate().cancel();
        v.setAlpha(1f);
        v.animate()
                .translationX(dp(dxDp))
                .setDuration(110)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> {
                    if (atHit != null) atHit.run();
                    v.animate().translationX(0f).setDuration(200).setInterpolator(new DecelerateInterpolator()).start();
                })
                .start();
    }

    // ------------------------------------------------------------------ ações do herói

    void sword(final ImageView hero, final ImageView boss, final int damage, final int lifeSteal) {
        lunge(hero, 30, () -> {
            float[] c = centerOf(boss);
            frames(CORTE, c[0], c[1] - dp(6), 1.25f, 55, false);
            burst(R.drawable.fx_faisca, c[0], c[1], 6, dp(46), 420, 1f);
            flash(boss, Color.WHITE);
            shake(boss, 4);
            if (damage > 0) number(boss, "-" + damage, COR_DANO_CHEFE, 24f);
            if (lifeSteal > 0) {
                heal(hero, lifeSteal);
            }
        });
    }

    /** Ataque do Mago: uma bola de magia voa até o chefe e explode em partículas roxas. */
    void magic(final ImageView hero, final ImageView boss, final int damage, final int lifeSteal) {
        lunge(hero, 12, () -> {
            final float[] h = centerOf(hero);
            final float[] c = centerOf(boss);
            if (!full()) {
                final ImageView orb = sprite(R.drawable.fx_void, 2.4f);
                placeCenter(orb, h[0] + hero.getWidth() * 0.35f, h[1] - hero.getHeight() * 0.25f);
                orb.animate()
                        .translationXBy(c[0] - h[0] - hero.getWidth() * 0.35f)
                        .translationYBy(c[1] - h[1] + hero.getHeight() * 0.25f)
                        .rotation(360f)
                        .setDuration(240)
                        .setInterpolator(new AccelerateInterpolator())
                        .withEndAction(() -> remove(orb))
                        .start();
            }
            handler.postDelayed(() -> {
                burst(R.drawable.fx_void, c[0], c[1], 9, dp(60), 520, 1.3f);
                burst(R.drawable.fx_brilho, c[0], c[1], 5, dp(40), 420, 1f);
                flash(boss, COR_VOID);
                shake(boss, 4);
                if (damage > 0) number(boss, "-" + damage, COR_VOID, 24f);
                if (lifeSteal > 0) heal(hero, lifeSteal);
            }, 240);
        });
    }

    void special(final ImageView hero, final ImageView boss, final View arena, final int damage, final int flashColor) {
        lunge(hero, 38, () -> {
            float[] c = centerOf(boss);
            screenFlash(flashColor, 0.75f, 280);
            frames(EXPLOSAO, c[0], c[1], 1.6f, 70, false);
            burst(R.drawable.fx_faisca, c[0], c[1], 10, dp(80), 560, 1.1f);
            burst(R.drawable.fx_brasa, c[0], c[1], 8, dp(64), 700, 1.2f);
            flash(boss, Color.WHITE);
            shake(arena, 9);
            if (damage > 0) number(boss, "-" + damage + "!", COR_DANO_ESPECIAL, 32f);
        });
    }

    void heal(ImageView target, int amount) {
        if (target == null) {
            return;
        }
        float[] c = centerOf(target);
        for (int i = 0; i < 8; i++) {
            final float x = c[0] + (random.nextFloat() - 0.5f) * target.getWidth() * 0.8f;
            final float y = c[1] + target.getHeight() * (0.1f + random.nextFloat() * 0.3f);
            handler.postDelayed(() -> particle(R.drawable.fx_cura, x, y, 0f, -dp(50 + random.nextInt(30)), 900, 0.9f), i * 70L);
        }
        flash(target, Color.argb(150, 124, 219, 106));
        if (amount > 0) number(target, "+" + amount, COR_CURA, 24f);
    }

    void shieldUp(ImageView hero) {
        float[] c = centerOf(hero);
        burst(R.drawable.fx_faisca_azul, c[0], c[1], 8, dp(56), 500, 1f);
        setShield(hero, true);
        if (shieldView != null) {
            shieldView.setScaleX(0.5f);
            shieldView.setScaleY(0.5f);
            shieldView.animate().scaleX(1f).scaleY(1f).setDuration(260).setInterpolator(new OvershootInterpolator()).start();
        }
    }

    /** Mostra/esconde a bolha do escudo; ao sumir ela se quebra em faíscas azuis. */
    void setShield(ImageView hero, boolean active) {
        if (active) {
            if (shieldView != null || hero == null) {
                return;
            }
            float[] c = centerOf(hero);
            float scale = Math.max(1f, hero.getHeight() / dp(PIXEL_DP * 54) * 1.05f);
            shieldView = sprite(R.drawable.fx_escudo, scale);
            placeCenter(shieldView, c[0], c[1]);
            shieldPulse = ObjectAnimator.ofFloat(shieldView, "alpha", 1f, 0.55f);
            shieldPulse.setDuration(650);
            shieldPulse.setRepeatMode(ValueAnimator.REVERSE);
            shieldPulse.setRepeatCount(ValueAnimator.INFINITE);
            shieldPulse.start();
        } else if (shieldView != null) {
            float[] c = {shieldView.getX() + shieldView.getLayoutParams().width / 2f, shieldView.getY() + shieldView.getLayoutParams().height / 2f};
            if (shieldPulse != null) shieldPulse.cancel();
            remove(shieldView);
            shieldView = null;
            shieldPulse = null;
            burst(R.drawable.fx_faisca_azul, c[0], c[1], 10, dp(60), 450, 1f);
        }
    }

    // ------------------------------------------------------------------ turno do chefe

    void bossAttack(final ImageView boss, final ImageView hero, final int damageToHero, final boolean criticalFailure, final int reflected) {
        lunge(boss, -30, () -> {
            float[] c = centerOf(hero);
            setShield(hero, false);
            if (damageToHero > 0) {
                frames(IMPACTO, c[0], c[1], 1.1f, 60, false);
                flash(hero, Color.argb(200, 224, 74, 74));
                shake(hero, 5);
                number(hero, "-" + damageToHero, COR_DANO_HEROI, 24f);
            }
            if (criticalFailure) {
                screenFlash(COR_DANO_HEROI, 0.35f, 450);
                number(hero, "FALHA!", COR_DANO_HEROI, 20f);
            }
            if (reflected > 0) {
                float[] b = centerOf(boss);
                for (int i = 0; i < 6; i++) {
                    particle(R.drawable.fx_faisca_azul, c[0], c[1], b[0] - c[0], b[1] - c[1], 380 + i * 30L, 1f);
                }
                handler.postDelayed(() -> {
                    flash(boss, COR_ESCUDO);
                    number(boss, "-" + reflected, COR_ESCUDO, 22f);
                }, 360);
            }
        });
    }

    /** Dano por efeito (queimadura, veneno, void) ou cura do chefe. */
    void hpChange(ImageView target, int delta, String effectsHint) {
        if (target == null || delta == 0) {
            return;
        }
        if (delta > 0) {
            heal(target, delta);
            return;
        }
        String h = effectsHint == null ? "" : effectsHint.toLowerCase();
        int color = COR_DANO_ESPECIAL;
        int res = R.drawable.fx_brasa;
        if (h.contains("ven")) {
            color = COR_VENENO;
            res = R.drawable.fx_bolha_veneno;
        } else if (h.contains("void")) {
            color = COR_VOID;
            res = R.drawable.fx_void;
        }
        float[] c = centerOf(target);
        burst(res, c[0], c[1], 5, dp(36), 600, 1.1f);
        flash(target, color);
        number(target, "-" + (-delta), color, 20f);
    }

    // ------------------------------------------------------------------ chefe derrotado / derrota

    void bossDeath(final ImageView boss, final Runnable onEnd) {
        stopIdle(boss);
        flash(boss, Color.WHITE);
        handler.postDelayed(() -> flash(boss, Color.WHITE), 240);
        shake(boss, 7);
        handler.postDelayed(() -> {
            float[] c = centerOf(boss);
            int[] colors = {Color.parseColor("#F4FBFF"), Color.parseColor("#FFE08A"), Color.parseColor("#8D93A8"), Color.parseColor("#5C4A62"), Color.parseColor("#FF7A2E")};
            for (int i = 0; i < 30; i++) {
                float x = c[0] + (random.nextFloat() - 0.5f) * boss.getWidth() * 0.7f;
                float y = c[1] + (random.nextFloat() - 0.5f) * boss.getHeight() * 0.7f;
                debris(x, y, colors[random.nextInt(colors.length)], dp(70), 700 + random.nextInt(400));
            }
            frames(EXPLOSAO, c[0], c[1], 1.3f, 70, false);
            boss.setPivotY(boss.getHeight());
            boss.animate().alpha(0f).scaleX(0.85f).scaleY(0.6f).setDuration(520).start();
        }, 480);
        handler.postDelayed(() -> {
            if (onEnd != null) onEnd.run();
        }, 1250);
    }

    void heroDefeat(final ImageView hero, final Runnable onEnd) {
        stopIdle(hero);
        setShield(hero, false);
        flash(hero, COR_DANO_HEROI);
        screenFlash(COR_DANO_HEROI, 0.45f, 1000);
        hero.setPivotX(hero.getWidth() / 2f);
        hero.setPivotY(hero.getHeight());
        hero.animate().rotation(-80f).alpha(0.35f).setStartDelay(200).setDuration(600).start();

        final TextView t = new TextView(activity);
        android.text.SpannableString msg = new android.text.SpannableString("Derrota\nVocê volta ao início");
        msg.setSpan(new android.text.style.RelativeSizeSpan(0.38f), 8, msg.length(), 0);
        msg.setSpan(new android.text.style.ForegroundColorSpan(Color.parseColor("#FFE08A")), 8, msg.length(), 0);
        t.setText(msg);
        t.setTextColor(COR_DANO_HEROI);
        t.setTextSize(64f);
        t.setTypeface(titleFont);
        t.setShadowLayer(1f, dp(4), dp(4), COR_CONTORNO);
        t.setGravity(Gravity.CENTER);
        layer.addView(t, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        t.setAlpha(0f);
        t.setScaleX(1.4f);
        t.setScaleY(1.4f);
        t.animate().alpha(1f).scaleX(1f).scaleY(1f).setStartDelay(350).setDuration(400).start();
        handler.postDelayed(() -> {
            remove(t);
            if (onEnd != null) onEnd.run();
        }, 2000);
    }

    void victory() {
        float w = layer.getWidth();
        float h = layer.getHeight();
        for (int i = 0; i < 26; i++) {
            final float x = w * (0.15f + random.nextFloat() * 0.7f);
            final float y = h * (0.05f + random.nextFloat() * 0.35f);
            final int res = random.nextInt(3) == 0 ? R.drawable.fx_estrela : R.drawable.fx_faisca;
            handler.postDelayed(() -> particle(res, x, y, (random.nextFloat() - 0.5f) * dp(40), dp(90 + random.nextInt(80)), 1300, 1.2f), i * 45L);
        }
    }

    /** Volta o sprite ao normal (depois de morrer/cair ou ao trocar de chefe). */
    void resetSprite(ImageView v) {
        if (v == null) {
            return;
        }
        v.animate().cancel();
        v.clearColorFilter();
        v.setAlpha(1f);
        v.setScaleX(1f);
        v.setScaleY(1f);
        v.setRotation(0f);
        v.setTranslationX(0f);
        v.setTranslationY(0f);
    }

    void bossEntrance(ImageView boss) {
        resetSprite(boss);
        boss.setTranslationX(dp(70));
        boss.setAlpha(0f);
        boss.animate().translationX(0f).alpha(1f).setDuration(450).setInterpolator(new DecelerateInterpolator()).start();
    }

    // ------------------------------------------------------------------ respiração (parado)

    void startIdle(View v, long phaseMs) {
        if (v == null || idle.containsKey(v)) {
            return;
        }
        ObjectAnimator a = ObjectAnimator.ofFloat(v, "translationY", 0f, -dp(PIXEL_DP));
        a.setDuration(900);
        a.setRepeatMode(ValueAnimator.REVERSE);
        a.setRepeatCount(ValueAnimator.INFINITE);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.setStartDelay(phaseMs);
        a.start();
        idle.put(v, a);
    }

    void stopIdle(View v) {
        ObjectAnimator a = idle.remove(v);
        if (a != null) {
            a.cancel();
            v.setTranslationY(0f);
        }
    }

    void stopAllIdle() {
        for (Map.Entry<View, ObjectAnimator> e : idle.entrySet()) {
            e.getValue().cancel();
            e.getKey().setTranslationY(0f);
        }
        idle.clear();
    }

    // ------------------------------------------------------------------ partículas de status

    void setAuras(View boss, Set<String> bossEffects, View hero, Set<String> heroEffects, boolean heroStunned) {
        auraBoss = boss;
        auraHero = hero;
        bossAura.clear();
        heroAura.clear();
        if (bossEffects != null) for (String s : bossEffects) bossAura.add(s.toLowerCase());
        if (heroEffects != null) for (String s : heroEffects) heroAura.add(s.toLowerCase());
        if (heroStunned) heroAura.add("atordoou");
        if (!auraRunning && (!bossAura.isEmpty() || !heroAura.isEmpty())) {
            auraRunning = true;
            handler.postDelayed(auraTick, 200);
        }
    }

    void stopAuras() {
        bossAura.clear();
        heroAura.clear();
        auraRunning = false;
        handler.removeCallbacks(auraTick);
    }

    private final Runnable auraTick = new Runnable() {
        @Override
        public void run() {
            if (!auraRunning) {
                return;
            }
            if (bossAura.isEmpty() && heroAura.isEmpty()) {
                auraRunning = false;
                return;
            }
            emitAura(auraBoss, bossAura);
            emitAura(auraHero, heroAura);
            handler.postDelayed(this, 360);
        }
    };

    private void emitAura(View target, Set<String> kinds) {
        if (target == null || kinds.isEmpty() || !target.isShown() || target.getAlpha() < 0.5f) {
            return;
        }
        float[] c = centerOf(target);
        float w = target.getWidth();
        float h = target.getHeight();
        for (String k : kinds) {
            float x = c[0] + (random.nextFloat() - 0.5f) * w * 0.7f;
            if (k.contains("queim") || k.contains("fogo")) {
                particle(R.drawable.fx_brasa, x, c[1] + h * 0.3f, (random.nextFloat() - 0.5f) * dp(10), -dp(40 + random.nextInt(25)), 900, 1f);
            } else if (k.contains("ven")) {
                particle(R.drawable.fx_bolha_veneno, x, c[1] + h * 0.2f, 0f, -dp(30 + random.nextInt(20)), 1000, 1f);
            } else if (k.contains("void")) {
                particle(R.drawable.fx_void, x, c[1] + (random.nextFloat() - 0.5f) * h * 0.6f, (random.nextFloat() - 0.5f) * dp(30), (random.nextFloat() - 0.5f) * dp(30), 700, 1f);
            } else if (k.contains("atordo") || k.contains("falha") || k.contains("paralis")) {
                float y = c[1] - h / 2f - dp(4);
                particle(R.drawable.fx_estrela, c[0] + (random.nextFloat() - 0.5f) * w * 0.5f, y, (random.nextFloat() - 0.5f) * dp(24), -dp(8), 600, 0.9f);
            }
        }
    }

    // ------------------------------------------------------------------ tela inicial

    void startMenu(View logo, View panel) {
        if (logo != null && logoFloat == null) {
            logoFloat = ObjectAnimator.ofFloat(logo, "translationY", 0f, -dp(7));
            logoFloat.setDuration(1600);
            logoFloat.setRepeatMode(ValueAnimator.REVERSE);
            logoFloat.setRepeatCount(ValueAnimator.INFINITE);
            logoFloat.setInterpolator(new AccelerateDecelerateInterpolator());
            logoFloat.start();
        }
        if (panel != null) {
            panel.setTranslationX(dp(50));
            panel.setAlpha(0f);
            panel.animate().translationX(0f).alpha(1f).setDuration(380).setInterpolator(new DecelerateInterpolator()).start();
        }
        if (!menuRunning) {
            menuRunning = true;
            nextShootingStar = System.currentTimeMillis() + 2500;
            handler.post(menuTick);
        }
    }

    void stopMenu() {
        menuRunning = false;
        handler.removeCallbacks(menuTick);
        if (logoFloat != null) {
            logoFloat.cancel();
            logoFloat = null;
        }
    }

    private final Runnable menuTick = new Runnable() {
        @Override
        public void run() {
            if (!menuRunning) {
                return;
            }
            float w = layer.getWidth();
            float h = layer.getHeight();
            if (w > 0 && h > 0) {
                // Brasas subindo devagar
                if (random.nextFloat() < 0.65f) {
                    float x = random.nextFloat() * w;
                    particle(R.drawable.fx_brasa, x, h + dp(6), (random.nextFloat() - 0.5f) * dp(60), -h * (0.45f + random.nextFloat() * 0.4f), 4200 + random.nextInt(2500), 0.8f);
                }
                // Estrelas piscando no céu
                if (random.nextFloat() < 0.35f) {
                    twinkle(random.nextFloat() * w, random.nextFloat() * h * 0.5f);
                }
                // Estrela cadente de vez em quando
                long now = System.currentTimeMillis();
                if (now >= nextShootingStar) {
                    nextShootingStar = now + 5000 + random.nextInt(5000);
                    float x = w * (0.05f + random.nextFloat() * 0.5f);
                    float y = h * (0.04f + random.nextFloat() * 0.2f);
                    particle(R.drawable.fx_estrela_cadente, x, y, dp(260), dp(130), 950, 1f);
                }
            }
            handler.postDelayed(this, 260);
        }
    };

    private void twinkle(float x, float y) {
        if (full()) {
            return;
        }
        final ImageView iv = sprite(R.drawable.fx_brilho, 1f);
        placeCenter(iv, x, y);
        iv.setScaleX(0f);
        iv.setScaleY(0f);
        iv.animate().scaleX(1f).scaleY(1f).setDuration(450).withEndAction(() ->
                iv.animate().scaleX(0f).scaleY(0f).setDuration(450).withEndAction(() -> remove(iv)).start()
        ).start();
    }

    // ------------------------------------------------------------------ limpeza

    /** Para tudo e limpa a camada (ao trocar de tela). */
    void clearAll() {
        handler.removeCallbacksAndMessages(null);
        menuRunning = false;
        auraRunning = false;
        bossAura.clear();
        heroAura.clear();
        if (logoFloat != null) {
            logoFloat.cancel();
            logoFloat = null;
        }
        if (shieldPulse != null) {
            shieldPulse.cancel();
            shieldPulse = null;
        }
        shieldView = null;
        for (int i = layer.getChildCount() - 1; i >= 0; i--) {
            View v = layer.getChildAt(i);
            v.animate().cancel();
        }
        layer.removeAllViews();
    }
}
