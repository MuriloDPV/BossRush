"""Arte da interface: logo, fundo da tela inicial, 9-patches (botao, painel, carta, campo, item) e icones das cartas."""
import os, math, random
from PIL import Image, ImageDraw, ImageFont, ImageEnhance
from px import Sprite, PAL
from buttons import ICONS
import bg

OUT = 'out/app/src/main/res/drawable-nodpi'
DRW = 'out/app/src/main/res/drawable'
os.makedirs(OUT, exist_ok=True); os.makedirs(DRW, exist_ok=True)

def rgb(c):
    h = PAL.get(c, c)
    return tuple(int(h[i:i + 2], 16) for i in (1, 3, 5)) + (255,)

# ---------------------------------------------------------------- logo
def logo():
    font = ImageFont.truetype('fonts/jacquard24.ttf', 24)
    m = Image.new('L', (140, 40), 0); d = ImageDraw.Draw(m); d.fontmode = '1'
    d.text((4, 2), 'Boss Rush', font=font, fill=255)
    bb = m.getbbox(); m = m.crop((bb[0] - 3, bb[1] - 3, bb[2] + 4, bb[3] + 8))
    W, H = m.size
    s = Sprite(W, H)
    top, bot = 3, H - 8
    for y in range(H):
        for x in range(W):
            if m.getpixel((x, y)):
                t = (y - top) / max(1, bot - top)
                s.g[y][x] = 'Q' if t < 0.25 else ('G' if t < 0.55 else ('g' if t < 0.8 else 'y'))
    # sombra vermelha escura deslocada
    sh = [(x, y) for y in range(H) for x in range(W) if m.getpixel((x, y))]
    for x, y in sh:
        for dx, dy in ((1, 1), (2, 2)):
            if x + dx < W and y + dy < H and s.g[y + dy][x + dx] == '.':
                s.g[y + dy][x + dx] = 'd'
    s.outline()
    # espada embaixo do titulo
    y0 = H - 4
    s2 = Sprite(W, H + 2)
    s2.g = [r[:] for r in s.g] + [['.'] * W for _ in range(2)]
    for x in range(8, W - 10):
        s2.g[y0][x] = 'l' if x % 2 else 'w'
        s2.g[y0 + 1][x] = 'm'
    s2.g[y0][W - 10] = 'w'
    for yy in range(y0 - 2, y0 + 4):
        s2.g[yy][10] = 'G'; s2.g[yy][11] = 'g'
    for x in range(3, 10):
        s2.g[y0][x] = 'B'; s2.g[y0 + 1][x] = 'b'
    s2.g[y0][2] = 'G'; s2.g[y0 + 1][2] = 'g'
    s2.outline()
    return s2

# ---------------------------------------------------------------- fundo da tela inicial (castelo a noite)
def fundo_menu():
    W, H, FLOOR = bg.W, bg.H, bg.FLOOR
    im = Image.new('RGB', (W, H)); d = ImageDraw.Draw(im)
    bg.bands(im, 0, H, ['#0b1026', '#141b3d', '#24255a', '#3b2a5e'])
    rnd = random.Random(4)
    for _ in range(90):
        x, y = rnd.randrange(W), rnd.randrange(0, 100)
        im.putpixel((x, y), (255, 255, 230) if rnd.random() < 0.3 else (170, 180, 230))
    d.ellipse([250, 18, 276, 44], fill=(255, 241, 196)); d.ellipse([258, 18, 284, 42], fill=(20, 27, 61))
    bg.ridge(im, 120, 10, 0.025, '#1d1f45', 31, '#2b2d5c')
    # castelo em silhueta com janelas acesas
    sil = (22, 20, 46)
    def tower(x, w, top):
        d.rectangle([x, top, x + w, 150], fill=sil)
        for k in range(x, x + w + 1, 4):
            d.rectangle([k, top - 3, k + 1, top - 1], fill=sil)
        for j in range(10):
            d.line([x - 1 + j // 2, top - 3 - j, x + w + 1 - j // 2, top - 3 - j], fill=sil)
        d.line([x + w // 2, top - 20, x + w // 2, top - 13], fill=sil)
    d.rectangle([110, 86, 214, 150], fill=sil)
    for k in range(110, 215, 5):
        d.rectangle([k, 82, k + 2, 85], fill=sil)
    tower(96, 16, 62); tower(206, 16, 62); tower(150, 24, 44)
    for wx, wy in [(102, 80), (102, 96), (160, 60), (160, 76), (212, 80), (130, 100), (190, 100), (166, 96)]:
        d.rectangle([wx, wy, wx + 2, wy + 4], fill=(255, 200, 90))
    # chao com grama escura
    bg.ridge(im, 146, 5, 0.04, '#141532', 33, '#22234a')
    for y in range(150, H):
        for x in range(W):
            if im.getpixel((x, y)) == (0, 0, 0):
                pass
    d.rectangle([0, 152, W, H], fill=(16, 17, 40))
    # silhueta do dragao voando perto da lua
    from dragon import dragon
    dm = dragon().image().resize((31, 25), Image.NEAREST)
    for y in range(dm.size[1]):
        for x in range(dm.size[0]):
            if dm.getpixel((x, y))[3]:
                im.putpixel((214 + x, 22 + y), (12, 10, 28))
    return im

# ---------------------------------------------------------------- 9-patch
def ninepatch(sprite, stretch_x, stretch_y, pad_x, pad_y, scale, name):
    """stretch_x/y e pad_x/y em pixels de arte (inicio, fim inclusivos)."""
    img = sprite.image(scale)
    W, H = img.size
    out = Image.new('RGBA', (W + 2, H + 2), (0, 0, 0, 0))
    out.paste(img, (1, 1))
    k = (0, 0, 0, 255)
    for x in range(stretch_x[0] * scale, (stretch_x[1] + 1) * scale):
        out.putpixel((x + 1, 0), k)
    for y in range(stretch_y[0] * scale, (stretch_y[1] + 1) * scale):
        out.putpixel((0, y + 1), k)
    for x in range(pad_x[0] * scale, (pad_x[1] + 1) * scale):
        out.putpixel((x + 1, H + 1), k)
    for y in range(pad_y[0] * scale, (pad_y[1] + 1) * scale):
        out.putpixel((W + 1, y + 1), k)
    out.save(f'{OUT}/{name}.9.png', optimize=True)
    return out


def box(w, h, rim_hi, rim_lo, fill_hi, fill, fill_lo, rivets=None, band=None):
    s = Sprite(w, h)
    for y in range(h):
        for x in range(w):
            corner = (x in (0, w - 1)) and (y in (0, h - 1))
            if corner:
                continue
            edge = x in (0, w - 1) or y in (0, h - 1)
            edge2 = x in (1, w - 2) or y in (1, h - 2)
            if edge:
                s.g[y][x] = rim_hi if (y == 0 or x == 0) else rim_lo
            elif edge2:
                s.g[y][x] = 'o'
            else:
                if band and 2 <= y < 2 + band[1]:
                    s.g[y][x] = band[0]
                elif y == 2 or x == 2:
                    s.g[y][x] = fill_hi
                elif y == h - 3 or x == w - 3:
                    s.g[y][x] = fill_lo
                else:
                    s.g[y][x] = fill
    if rivets:
        for (x, y) in ((1, 1), (w - 2, 1), (1, h - 2), (w - 2, h - 2)):
            s.g[y][x] = rivets
    return s


def ornaments(s, top=3):
    w, h = s.w, s.h
    for (cx, cy, dx, dy) in ((3, top, 1, 1), (w - 4, top, -1, 1), (3, h - 4, 1, -1), (w - 4, h - 4, -1, -1)):
        s.g[cy][cx] = 'G'; s.g[cy][cx + dx] = 'g'; s.g[cy + dy][cx] = 'g'
    return s


def gray(img):
    g = img.convert('LA').convert('RGBA')
    g = ImageEnhance.Brightness(g).enhance(0.6)
    g.putalpha(img.getchannel('A'))
    return g


def gray9(name_src, name_dst):
    im = Image.open(f'{OUT}/{name_src}.9.png').convert('RGBA')
    W, H = im.size
    inner = im.crop((1, 1, W - 1, H - 1))
    out = im.copy(); out.paste(gray(inner), (1, 1))
    out.save(f'{OUT}/{name_dst}.9.png', optimize=True)


def selector(name, items):
    xml = ['<?xml version="1.0" encoding="utf-8"?>', '<selector xmlns:android="http://schemas.android.com/apk/res/android">']
    for st, dr in items:
        xml.append(f'    <item {st}android:drawable="@drawable/{dr}" />')
    xml.append('</selector>')
    open(f'{DRW}/{name}.xml', 'w').write('\n'.join(xml) + '\n')


S = 6
def build():
    # botao de madeira com moldura de bronze
    def plank(pressed=False):
        s = box(32, 14, 'G' if not pressed else 'g', 'y', 'C' if not pressed else 'B', 'B' if not pressed else 'b', 'b', rivets='G')
        for x in range(4, 28, 7):  # veios
            s.g[6][x] = 'b'; s.g[6][x + 1] = 'b'; s.g[9][x + 3] = 'b'
        return s
    ninepatch(plank(), (5, 26), (4, 9), (3, 28), (3, 10), S, 'botao_madeira')
    ninepatch(plank(True), (5, 26), (4, 9), (3, 28), (4, 10), S, 'botao_madeira_press')
    gray9('botao_madeira', 'botao_madeira_off')
    selector('botao_madeira_sel', [('android:state_enabled="false" ', 'botao_madeira_off'),
                                   ('android:state_pressed="true" ', 'botao_madeira_press'),
                                   ('', 'botao_madeira')])
    # painel escuro com moldura de bronze
    p = ornaments(box(24, 24, 'G', 'y', 'N', 'n', 'S', rivets='G'))
    ninepatch(p, (5, 18), (5, 18), (4, 19), (4, 19), S, 'painel')
    # item de lista (mais simples)
    it = box(16, 12, 'g', 'y', 'm', 'S', 'S')
    ninepatch(it, (4, 11), (4, 7), (3, 12), (3, 8), S, 'painel_item')
    # campo de texto
    c = box(16, 12, 'y', 'g', 'o', 'D', 'L')
    ninepatch(c, (4, 11), (4, 7), (3, 12), (3, 8), S, 'campo_texto')
    # cartas de pergaminho com faixa da categoria
    for nome, cor in (('espada', 'r'), ('escudo', 'n'), ('especial', 'u')):
        def card(pressed=False):
            s = box(30, 40, 'G', 'y', 'X', 'X' if not pressed else 'x', 'x', rivets='G', band=(cor, 5))
            for y in range(10, 33, 6):
                s.g[y][5] = 'x'; s.g[y + 2][24] = 'x'
            return ornaments(s, top=8)
        ninepatch(card(), (8, 21), (12, 30), (3, 26), (8, 36), S, f'carta_{nome}')
        ninepatch(card(True), (8, 21), (12, 30), (3, 26), (8, 36), S, f'carta_{nome}_press')
        gray9(f'carta_{nome}', f'carta_{nome}_off')
        selector(f'carta_{nome}_sel', [('android:state_enabled="false" ', f'carta_{nome}_off'),
                                       ('android:state_pressed="true" ', f'carta_{nome}_press'),
                                       ('', f'carta_{nome}')])
    # icones das cartas
    for nome, icon in (('espada', 'sword'), ('escudo', 'shield'), ('especial', 'special')):
        L = ICONS[icon]
        s = Sprite(max(len(l) for l in L) + 2, len(L) + 2)
        s.rows(1, 1, L); s.outline()
        s.image(7).save(f'{OUT}/icone_carta_{nome}.png', optimize=True)
    # logo + fundo
    logo().image(8).save(f'{OUT}/logo_boss_rush.png', optimize=True)
    fundo_menu().resize((bg.W * 6, bg.H * 6), Image.NEAREST).save(f'{OUT}/fundo_menu.png', optimize=True)


if __name__ == '__main__':
    build()
