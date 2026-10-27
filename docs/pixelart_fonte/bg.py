"""Cenarios 320x180 em pixel art. Chao a partir de y~128."""
import random, math
from PIL import Image, ImageDraw

W, H = 320, 180
FLOOR = 128


def hx(c):
    return tuple(int(c[i:i + 2], 16) for i in (1, 3, 5))


def bands(im, y0, y1, cols):
    """Gradiente em faixas com dithering 2x2 na transicao."""
    n = len(cols)
    for y in range(y0, y1):
        t = (y - y0) / max(1, (y1 - y0)) * (n - 1)
        i = min(int(t), n - 2)
        f = t - i
        for x in range(W):
            th = [0.125, 0.625, 0.875, 0.375][(x % 2) + 2 * (y % 2)]
            c = cols[i + 1] if f > th else cols[i]
            im.putpixel((x, y), hx(c))


def rect(d, x0, y0, x1, y1, c):
    d.rectangle([x0, y0, x1, y1], fill=hx(c))


def ridge(im, base, amp, freq, col, seed, top=None, phase=0.0):
    rnd = random.Random(seed)
    ph = [rnd.random() * 6 for _ in range(3)]
    for x in range(W):
        h = base - amp * (0.6 * math.sin(x * freq + ph[0] + phase) + 0.3 * math.sin(x * freq * 2.3 + ph[1]) + 0.1 * math.sin(x * freq * 5.1 + ph[2]))
        h = int(h)
        for y in range(max(0, h), FLOOR + 1):
            im.putpixel((x, y), hx(col))
        if top and 0 <= h < H:
            im.putpixel((x, h), hx(top))


def reino():
    im = Image.new('RGB', (W, H)); d = ImageDraw.Draw(im)
    bands(im, 0, FLOOR, ['#4f8fd0', '#77b3e6', '#a9d6f2', '#d8eef7'])
    rnd = random.Random(1)
    # nuvens
    for cx, cy, r in [(40, 22, 10), (58, 20, 13), (75, 24, 9), (210, 34, 9), (226, 30, 12), (242, 35, 8)]:
        d.ellipse([cx - r, cy - r // 2, cx + r, cy + r // 2], fill=hx('#f4fbff'))
        d.line([cx - r + 2, cy + r // 2, cx + r - 2, cy + r // 2], fill=hx('#cfe4f2'))
    # morros distantes
    ridge(im, 112, 6, 0.03, '#7aa86a', 3, '#9cc47e')
    # castelo ao fundo
    stone, stoneD, stoneL = '#8d93a8', '#666b82', '#b4b9c9'
    def tower(x, w, top, roof=True):
        rect(d, x, top, x + w, FLOOR, stone)
        rect(d, x + w - 2, top, x + w, FLOOR, stoneD)
        rect(d, x, top, x + 1, FLOOR, stoneL)
        for k in range(x, x + w + 1, 4):
            rect(d, k, top - 3, k + 1, top - 1, stone)
        if roof:
            for j in range(12):
                d.line([x - 1 + j // 2, top - 3 - j, x + w + 1 - j // 2, top - 3 - j], fill=hx('#b53a4a' if j < 10 else '#8a2a3a'))
            d.line([x + w // 2, top - 22, x + w // 2, top - 15], fill=hx('#4a3a2e'))
            rect(d, x + w // 2 + 1, top - 22, x + w // 2 + 6, top - 19, '#f2c14e')
        for wy in range(top + 6, FLOOR - 10, 12):
            rect(d, x + w // 2 - 1, wy, x + w // 2 + 1, wy + 4, '#2b2f45')
    rect(d, 120, 78, 230, FLOOR, stone)
    rect(d, 120, 78, 230, 79, stoneL)
    for k in range(120, 231, 5):
        rect(d, k, 74, k + 2, 77, stone)
    for by in range(82, FLOOR, 6):
        for bx in range(120 + (by // 6 % 2) * 4, 230, 8):
            d.point((bx, by), fill=hx(stoneD))
    tower(104, 18, 56); tower(228, 18, 56); tower(160, 26, 40)
    # portao
    d.ellipse([162, 96, 184, 118], fill=hx('#3a2a2e')); rect(d, 162, 107, 184, FLOOR, '#3a2a2e')
    for gx in range(164, 184, 4):
        d.line([gx, 100, gx, FLOOR], fill=hx('#5a4a3e'))
    # bandeirolas
    for x in range(20, 320, 70):
        rect(d, x, 92, x + 1, FLOOR, '#5a4632')
        d.polygon([(x + 2, 93), (x + 12, 96), (x + 2, 100)], fill=hx('#c0394a'))
    # chao: pedras da rua
    rect(d, 0, FLOOR, W, H, '#7d6a58')
    for y in range(FLOOR, H, 6):
        off = (y // 6 % 2) * 6
        for x in range(-off, W, 12):
            rect(d, x + 1, y + 1, x + 10, y + 4, '#9a8670')
            d.line([x + 1, y + 1, x + 9, y + 1], fill=hx('#b29d84'))
    d.line([0, FLOOR, W, FLOOR], fill=hx('#5b4a3c'))
    return im


def planicie():
    im = Image.new('RGB', (W, H)); d = ImageDraw.Draw(im)
    bands(im, 0, FLOOR, ['#f6b26b', '#f7cf8a', '#cfe3b0', '#9fd0d8'])
    d.ellipse([230, 30, 262, 62], fill=hx('#fff1b8'))
    ridge(im, 96, 10, 0.02, '#8fb3a0', 11, '#a9c9b4')
    ridge(im, 108, 9, 0.035, '#6f9e5c', 12, '#8dbb6c')
    ridge(im, 120, 6, 0.05, '#4f8a45', 13, '#6aa856')
    # arvores solitarias
    for x, y in [(52, 104), (268, 110), (300, 112)]:
        rect(d, x, y, x + 2, y + 12, '#5a3e2c')
        d.ellipse([x - 8, y - 12, x + 10, y + 2], fill=hx('#3f7a3a'))
        d.ellipse([x - 6, y - 12, x + 4, y - 4], fill=hx('#5c9c48'))
    # chao: grama
    rect(d, 0, FLOOR, W, H, '#5f9e45')
    rnd = random.Random(5)
    for _ in range(500):
        x, y = rnd.randrange(W), rnd.randrange(FLOOR + 2, H)
        d.line([x, y, x, y - 2], fill=hx(rnd.choice(['#77b552', '#4d8a3a', '#8cc760'])))
    # trilha de terra
    for y in range(FLOOR + 12, H):
        w = 30 + (y - FLOOR) * 2
        for x in range(W // 2 - w, W // 2 + w):
            if 0 <= x < W:
                im.putpixel((x, y), hx(rnd.choice(['#b08a5c', '#b08a5c', '#b08a5c', '#9c7a50', '#c49e6c'])))
    for _ in range(40):
        x, y = rnd.randrange(W), rnd.randrange(FLOOR + 2, H)
        d.point((x, y), fill=hx(rnd.choice(['#ffe08a', '#ff8fa8', '#ffffff'])))
    d.line([0, FLOOR, W, FLOOR], fill=hx('#86c25e'))
    return im


def floresta():
    im = Image.new('RGB', (W, H)); d = ImageDraw.Draw(im)
    bands(im, 0, FLOOR, ['#0f2a24', '#1d4034', '#2c5a42', '#3c6e4a'])
    rnd = random.Random(7)
    # camadas de troncos
    for layer, (col, dark, wmin, wmax, n) in enumerate([
            ('#244a3a', '#1c3c30', 6, 10, 14), ('#33402e', '#252f22', 9, 14, 9), ('#4a3a2a', '#352819', 12, 18, 6)]):
        xs = sorted(rnd.randrange(-10, W) for _ in range(n))
        for x in xs:
            w = rnd.randint(wmin, wmax)
            rect(d, x, 0, x + w, FLOOR + 4, col)
            rect(d, x + w - 2, 0, x + w, FLOOR + 4, dark)
            for k in range(0, FLOOR, 9):
                d.point((x + 2 + (k // 9) % 3, k + rnd.randrange(5)), fill=hx(dark))
            # raizes
            d.polygon([(x - 4, FLOOR + 4), (x, FLOOR - 6), (x + 3, FLOOR + 4)], fill=hx(col))
            d.polygon([(x + w - 3, FLOOR + 4), (x + w, FLOOR - 6), (x + w + 4, FLOOR + 4)], fill=hx(dark))
    # copa
    for _ in range(70):
        x, y, r = rnd.randrange(-10, W + 10), rnd.randrange(-10, 30), rnd.randint(8, 18)
        d.ellipse([x - r, y - r, x + r, y + r], fill=hx(rnd.choice(['#1f4a2c', '#2a5e34', '#163b24'])))
    # raios de luz
    for x0 in (70, 180, 250):
        for y in range(20, FLOOR):
            for k in range(6):
                x = x0 + k + (y - 20) // 3
                if 0 <= x < W and (x + y) % 2 == 0:
                    r, g, b = im.getpixel((x, y))
                    im.putpixel((x, y), (min(255, r + 40), min(255, g + 45), min(255, b + 25)))
    # chao: musgo e folhas
    rect(d, 0, FLOOR, W, H, '#2f4a2a')
    for _ in range(600):
        x, y = rnd.randrange(W), rnd.randrange(FLOOR + 1, H)
        d.point((x, y), fill=hx(rnd.choice(['#3d5e33', '#26401f', '#4f6e3a', '#5a4a2a'])))
    # cogumelos
    for x in (30, 140, 296):
        rect(d, x + 2, FLOOR + 4, x + 3, FLOOR + 8, '#e8dcc0')
        d.ellipse([x - 1, FLOOR, x + 6, FLOOR + 5], fill=hx('#c0394a'))
        d.point((x + 1, FLOOR + 1), fill=hx('#ffffff')); d.point((x + 4, FLOOR + 2), fill=hx('#ffffff'))
    d.line([0, FLOOR, W, FLOOR], fill=hx('#4a6e3a'))
    return im


def caverna():
    im = Image.new('RGB', (W, H)); d = ImageDraw.Draw(im)
    bands(im, 0, FLOOR, ['#120d1c', '#1f1630', '#2b1d3a', '#3a2440'])
    rnd = random.Random(9)
    # paredes de rocha
    ridge(im, 70, 18, 0.045, '#2a2238', 21, '#3a3050')
    ridge(im, 100, 12, 0.06, '#33283f', 22, '#4a3a58')
    # estalactites
    for x in range(0, W, 9):
        L = rnd.randint(6, 28)
        for j in range(L):
            w = max(0, 3 - j * 3 // L)
            d.line([x + 4 - w, j, x + 4 + w, j], fill=hx('#2a2238' if j < L - 3 else '#3a3050'))
    # rios de lava ao fundo
    for y in range(116, FLOOR):
        for x in range(W):
            if math.sin(x * 0.05 + y * 0.3) > 0.55:
                im.putpixel((x, y), hx('#ff7a2e' if (x + y) % 3 else '#ffcf5a'))
    # pilha de tesouro do dragao
    for cx, base, r in [(250, FLOOR, 34), (300, FLOOR, 22)]:
        for y in range(base - r // 2, base + 1):
            half = int(r * math.sqrt(max(0, 1 - ((base - y) / (r / 2)) ** 2)))
            for x in range(cx - half, cx + half):
                if 0 <= x < W:
                    im.putpixel((x, y), hx(rnd.choice(['#d0a36d', '#ffe08a', '#b8843c', '#ffe08a'])))
    # cristais/brilho
    for x, y in [(30, 90), (60, 70), (300, 84), (140, 60)]:
        d.polygon([(x, y), (x + 3, y - 8), (x + 6, y)], fill=hx('#9b6cff'))
        d.line([x + 3, y - 7, x + 3, y - 1], fill=hx('#d6c2ff'))
    # chao de pedra com rachaduras de lava
    rect(d, 0, FLOOR, W, H, '#3a2d40')
    for _ in range(90):
        x, y = rnd.randrange(-10, W), rnd.randrange(FLOOR + 2, H)
        w, h = rnd.randint(6, 18), rnd.randint(3, 6)
        d.ellipse([x, y, x + w, y + h], fill=hx(rnd.choice(['#46364e', '#4f3e57', '#332839'])))
        d.line([x + 2, y, x + w - 2, y], fill=hx('#5c4a62'))
    for _ in range(14):
        x, y = rnd.randrange(W), rnd.randrange(FLOOR + 4, H)
        for k in range(rnd.randint(4, 9)):
            d.point((x + k, y + (k % 3) - 1), fill=hx('#ff7a2e'))
    d.line([0, FLOOR, W, FLOOR], fill=hx('#5c4a62'))
    return im


AREAS = [('area_reino', reino), ('area_planicie', planicie), ('area_floresta', floresta), ('area_caverna', caverna)]

if __name__ == '__main__':
    sheet = Image.new('RGB', (W * 2 * 2 + 10, H * 2 * 2 + 10), (20, 20, 20))
    for i, (n, f) in enumerate(AREAS):
        im = f()
        im.save(n + '_raw.png')
        sheet.paste(im.resize((W * 2, H * 2), Image.NEAREST), ((i % 2) * (W * 2 + 10), (i // 2) * (H * 2 + 10)))
    sheet.save('areas_sheet.png')
