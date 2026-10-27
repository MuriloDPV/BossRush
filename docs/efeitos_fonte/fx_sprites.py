# Gera os sprites de efeitos do Boss Rush em pixel art.
# Cada sprite é desenhado em grade pequena e exportado ampliado 8x (mesmo padrão das outras artes).
import math, os, random, sys
from PIL import Image

OUT = sys.argv[1] if len(sys.argv) > 1 else "out"
SCALE = 8
os.makedirs(OUT, exist_ok=True)

def hexc(h, a=255):
    h = h.lstrip('#')
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), a)

# Paleta tirada das artes que já estão no jogo
C = {
    'o': hexc('#16142E'),   # contorno escuro
    'W': hexc('#F4FBFF'),
    'w': hexc('#D8EEF7'),
    'L': hexc('#A9D6F2'),
    'B': hexc('#77B3E6'),
    'D': hexc('#4F8FD0'),
    'Y': hexc('#FFE08A'),
    'y': hexc('#FFF1B8'),
    'R': hexc('#FF7A2E'),   # laranja fogo
    'r': hexc('#AF2645'),   # vermelho escuro
    'g': hexc('#CFE3B0'),
    'G': hexc('#5F9E45'),
    'h': hexc('#2A5E34'),
    'p': hexc('#3B2A6B'),
    'P': hexc('#7A4FC9'),
    'v': hexc('#C7B2F2'),
    's': hexc('#5C4A62'),   # fumaça
    'S': hexc('#3A2D40'),
    'k': hexc('#B53A4A'),
}

def save(name, img):
    big = img.resize((img.width * SCALE, img.height * SCALE), Image.NEAREST)
    big.save(os.path.join(OUT, name + '.png'))

def from_rows(rows, pal=C):
    h = len(rows); w = max(len(r) for r in rows)
    img = Image.new('RGBA', (w, h), (0, 0, 0, 0))
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch in pal:
                img.putpixel((x, y), pal[ch])
    return img

# ---------- partículas pequenas (desenhadas à mão) ----------
save('fx_cura', from_rows([
    "...ooo...",
    "...ogo...",
    "...ogo...",
    "ooooGoooo",
    "oggGWGggo",
    "ooooGoooo",
    "...ogo...",
    "...ogo...",
    "...ooo...",
], {**C, 'o': C['h']}))

save('fx_faisca', from_rows([
    "..R..",
    "..Y..",
    "RYWYR",
    "..Y..",
    "..R..",
]))

save('fx_faisca_azul', from_rows([
    "..D..",
    "..L..",
    "DLWLD",
    "..L..",
    "..D..",
]))

save('fx_brasa', from_rows([
    ".R.",
    "RYR",
    ".R.",
]))

save('fx_bolha_veneno', from_rows([
    ".GG.",
    "Gg.G",
    "G..G",
    ".GG.",
]))

save('fx_void', from_rows([
    "..p..",
    ".pPp.",
    "pPvPp",
    ".pPp.",
    "..p..",
]))

save('fx_estrela', from_rows([
    "..Y..",
    ".YYY.",
    "YYyYY",
    ".YYY.",
    ".Y.Y.",
]))

save('fx_brilho', from_rows([
    "..L..",
    "..w..",
    "LwWwL",
    "..w..",
    "..L..",
]))

# Estrela cadente na diagonal: cauda no canto de cima à esquerda, cabeça embaixo à direita
def shooting_star():
    w, h = 18, 9
    img = Image.new('RGBA', (w, h), (0, 0, 0, 0))
    for i in range(16):
        x, y = i, i // 2
        col = 'D' if i < 5 else 'L' if i < 10 else 'w' if i < 14 else 'W'
        img.putpixel((x, y), C[col])
    for (x, y) in [(16, 8), (17, 8), (16, 7)]:
        img.putpixel((x, y), C['W'])
    img.putpixel((15, 8), C['L'])
    return img
save('fx_estrela_cadente', shooting_star())

# ---------- corte da espada (4 quadros) ----------
def slash_frame(t):
    n = 34
    img = Image.new('RGBA', (n, n), (0, 0, 0, 0))
    cx, cy = 6, n - 6
    r_out = 26
    # arco de -95° a 5° (de cima para a direita), varrendo com o tempo
    a0, a1 = -100, 10
    sweep_end = a0 + (a1 - a0) * min(1.0, 0.45 + t * 0.55)
    thick = [4.0, 8.0, 6.5, 3.0][int(t * 3.999)]
    for y in range(n):
        for x in range(n):
            dx, dy = x + 0.5 - cx, y + 0.5 - cy
            r = math.hypot(dx, dy)
            ang = math.degrees(math.atan2(dy, dx))
            if not (a0 <= ang <= sweep_end):
                continue
            # espessura cresce no meio do arco
            frac = (ang - a0) / (a1 - a0)
            k = math.sin(frac * math.pi)
            th = max(1.0, thick * k)
            d = r_out - r
            if 0 <= d < th:
                if t >= 0.99 and (x + y) % 3 == 0:
                    continue   # último quadro some em fragmentos
                if d < 1.5:
                    col = 'W'
                elif d < th * 0.45:
                    col = 'w'
                elif d < th * 0.75:
                    col = 'L'
                else:
                    col = 'B'
                img.putpixel((x, y), C[col])
    return img
for i, t in enumerate([0.0, 0.34, 0.67, 1.0]):
    save('fx_corte_%d' % (i + 1), slash_frame(t))

# ---------- explosão do ataque especial (5 quadros) ----------
def explosion_frame(i):
    n = 34
    img = Image.new('RGBA', (n, n), (0, 0, 0, 0))
    c = n / 2
    rnd = random.Random(42)
    noise = [[rnd.random() for _ in range(n)] for _ in range(n)]
    radii = [5, 9, 13, 15, 16]
    R = radii[i]
    for y in range(n):
        for x in range(n):
            dx, dy = x + 0.5 - c, y + 0.5 - c
            r = math.hypot(dx, dy) + (noise[y][x] - 0.5) * 2.4
            if r > R:
                continue
            q = r / R
            if i == 0:
                col = 'W' if q < 0.5 else 'y' if q < 0.8 else 'Y'
            elif i == 1:
                col = 'W' if q < 0.3 else 'y' if q < 0.55 else 'Y' if q < 0.75 else 'R' if q < 0.92 else 'r'
            elif i == 2:
                col = 'y' if q < 0.3 else 'Y' if q < 0.5 else 'R' if q < 0.78 else 'r'
            elif i == 3:
                if q < 0.45:
                    continue   # centro abre (vira anel)
                col = 'R' if q < 0.6 else 'r' if q < 0.78 else 's'
                if noise[y][x] > 0.75:
                    continue
            else:
                if q < 0.6 or noise[y][x] > 0.45:
                    continue
                col = 's' if noise[y][x] > 0.2 else 'S'
            img.putpixel((x, y), C[col])
    return img
for i in range(5):
    save('fx_explosao_%d' % (i + 1), explosion_frame(i))

# ---------- impacto no herói (3 quadros) ----------
def impact_frame(i):
    n = 24
    img = Image.new('RGBA', (n, n), (0, 0, 0, 0))
    c = n // 2
    lengths = [[5, 3, 5, 3, 5, 3, 5, 3], [10, 6, 9, 6, 10, 6, 9, 6], [11, 8, 11, 8, 11, 8, 11, 8]][i]
    dirs = [(1, 0), (1, 1), (0, 1), (-1, 1), (-1, 0), (-1, -1), (0, -1), (1, -1)]
    for (dx, dy), L in zip(dirs, lengths):
        start = 0 if i < 2 else 5
        for s in range(start, L):
            x, y = c + dx * s, c + dy * s
            if 0 <= x < n and 0 <= y < n:
                col = 'W' if s < L * 0.4 else 'y' if s < L * 0.7 else 'k'
                if i == 2:
                    col = 'k' if s < L - 2 else 'r'
                img.putpixel((x, y), C[col])
                # raios principais mais grossos
                if dx == 0 or dy == 0:
                    px, py = x + (1 if dy else 0), y + (1 if dx else 0)
                    if 0 <= px < n and 0 <= py < n and s < L - 1:
                        img.putpixel((px, py), C[col])
    if i < 2:
        for y in range(c - 2, c + 3):
            for x in range(c - 2, c + 3):
                if abs(x - c) + abs(y - c) <= 2:
                    img.putpixel((x, y), C['W'])
    return img
for i in range(3):
    save('fx_impacto_%d' % (i + 1), impact_frame(i))

# ---------- bolha do escudo ----------
def shield_bubble():
    w, h = 46, 54
    img = Image.new('RGBA', (w, h), (0, 0, 0, 0))
    cx, cy = w / 2, h / 2
    ax, ay = w / 2 - 1, h / 2 - 1
    for y in range(h):
        for x in range(w):
            e = ((x + 0.5 - cx) / ax) ** 2 + ((y + 0.5 - cy) / ay) ** 2
            if e > 1:
                continue
            if e > 0.88:
                img.putpixel((x, y), C['B'])
            elif e > 0.76:
                img.putpixel((x, y), hexc('#A9D6F2', 150))
            elif (x + y) % 4 == 0 and e > 0.45:
                img.putpixel((x, y), hexc('#A9D6F2', 70))   # pontilhado leve por dentro
    # brilho no canto de cima
    for (x, y) in [(12, 8), (13, 7), (14, 7), (15, 6), (11, 9), (10, 11), (16, 6)]:
        img.putpixel((x, y), C['W'])
    return img
save('fx_escudo', shield_bubble())

# ---------- configurações: botão do controle deslizante e do interruptor ----------
save('ui_puxador', from_rows([
    "..oooooo..",
    ".oYYYYYYo.",
    "oYyyYYYYRo",
    "oYyYYYYYRo",
    "oYYYYYYYRo",
    "oYYYYYYRRo",
    "oYYYYYYRRo",
    "oRYYYYRRRo",
    ".oRRRRRRo.",
    "..oooooo..",
]))

print('ok')
