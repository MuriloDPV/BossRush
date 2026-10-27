"""Mini toolkit de pixel art: camadas de segmentos + contorno automatico."""
from PIL import Image

PAL = {
    'o': '#1b223c',  # contorno
    'S': '#2c3b57', 'm': '#4d6680', 'l': '#7f9bb0', 'w': '#e9eef2',  # aco
    'n': '#1d3a64', 'N': '#30507a', 'v': '#4a74a8',  # azul tecido
    'r': '#af2645', 'R': '#e04a4a', 'd': '#672040',  # vermelho
    'k': '#ffcfae', 'K': '#d9926e',  # pele
    'h': '#3a2a35', 'H': '#6b4a4a',  # cabelo
    'y': '#8a5a2e', 'g': '#d0a36d', 'G': '#ffe08a',  # ouro
    'b': '#5a2e2e', 'B': '#8f4f3a', 'C': '#c27a4a',  # couro
    'e': '#f7f5f6',  # branco olho
    # dragao
    'a': '#35324a', 'A': '#8a2f3a', 'f': '#9a2f3f', 'F': '#d9573f', 'q': '#ff9a4a', 'Q': '#ffd27a',
    'z': '#555170', 'Z': '#8a87a3', 'x': '#c9b48a', 'X': '#efe2b8',
    # verde / roxo / extra
    'j': '#2e5a3c', 'J': '#4f8f4a', 'i': '#9fd46a', 'I': '#d6f59a',
    'u': '#3b2a6b', 'U': '#7a4fc9', 'V': '#b89cff',
    't': '#4a3a2e', 'T': '#7a6248', 's': '#a8957a',
    'c': '#1a6a6a', 'E': '#3fb3a3', 'P': '#a8f0e0',
    '0': '#1f3b2a',                                  # verde capuz escuro
    '1': '#8a7a62',                                  # osso sombra
    '2': '#3e4558', '3': '#68738a', '4': '#a9b3c2',  # lobo
    '5': '#3a5233', '6': '#5f7f45', '7': '#8faa5a',  # orc
    'D': '#2e2846', 'L': '#453a63', 'M': '#66578f',  # casaco escuro
    'O': '#5a4632', 'W': '#ffffff', 'Y': '#7fe0ff',  # misc
}


class Sprite:
    def __init__(self, w, h):
        self.w, self.h = w, h
        self.g = [['.'] * w for _ in range(h)]
        self.ox = self.oy = 0

    def seg(self, y, x, s):
        x += self.ox; y += self.oy
        for i, c in enumerate(s):
            if c != ' ' and 0 <= x + i < self.w and 0 <= y < self.h:
                self.g[y][x + i] = c

    def rows(self, y0, x0, lines):
        for j, s in enumerate(lines):
            self.seg(y0 + j, x0, s)

    def px(self, x, y, c):
        x += self.ox; y += self.oy
        if 0 <= x < self.w and 0 <= y < self.h:
            self.g[y][x] = c

    def outline(self, c='o'):
        filled = [[self.g[y][x] != '.' for x in range(self.w)] for y in range(self.h)]
        for y in range(self.h):
            for x in range(self.w):
                if filled[y][x]:
                    continue
                for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    nx, ny = x + dx, y + dy
                    if 0 <= nx < self.w and 0 <= ny < self.h and filled[ny][nx] and self.g[ny][nx] != c:
                        self.g[y][x] = c
                        break

    def image(self, scale=1, bg=None):
        im = Image.new('RGBA', (self.w, self.h), bg or (0, 0, 0, 0))
        for y in range(self.h):
            for x in range(self.w):
                c = self.g[y][x]
                if c != '.':
                    hx = PAL[c]
                    im.putpixel((x, y), tuple(int(hx[i:i + 2], 16) for i in (1, 3, 5)) + (255,))
        if scale > 1:
            im = im.resize((self.w * scale, self.h * scale), Image.NEAREST)
        return im


def shade(lines, hi, mid, lo, hd=1, ld=2, keep=''):
    """Recolore pixels hi/mid de uma parte: borda cima/esquerda = hi, borda baixo/direita = lo."""
    W = max(len(l) for l in lines)
    g = [list(l.ljust(W)) for l in lines]
    H = len(g)
    inside = lambda x, y: 0 <= x < W and 0 <= y < H and g[y][x] != ' '
    out = [row[:] for row in g]
    for y in range(H):
        for x in range(W):
            if g[y][x] not in (hi, mid):
                continue
            if any(not inside(x - d, y) or not inside(x, y - d) for d in range(1, hd + 1)):
                out[y][x] = hi
            elif any(not inside(x + d, y) or not inside(x, y + d) for d in range(1, ld + 1)):
                out[y][x] = lo
            else:
                out[y][x] = mid
    return [''.join(r) for r in out]


def shade_parts(lines, ramps, hd=1, ld=1):
    """Sombreamento por material: cada ramp (hi, mid, lo) usa a propria borda."""
    W = max(len(l) for l in lines)
    g = [list(l.ljust(W)) for l in lines]
    H = len(g)
    mat = {}
    for i, r in enumerate(ramps):
        for c in r:
            mat.setdefault(c, i)
    out = [row[:] for row in g]
    def same(x, y, m):
        return 0 <= x < W and 0 <= y < H and mat.get(g[y][x]) == m
    for y in range(H):
        for x in range(W):
            m = mat.get(g[y][x])
            if m is None:
                continue
            hi, mid, lo = ramps[m]
            if any(not same(x - d, y, m) or not same(x, y - d, m) for d in range(1, hd + 1)):
                out[y][x] = hi
            elif any(not same(x + d, y, m) or not same(x, y + d, m) for d in range(1, ld + 1)):
                out[y][x] = lo
            else:
                out[y][x] = mid
    return [''.join(r) for r in out]


def part(s, y, x, lines, ramps=(), hd=1, ld=1, edge=False):
    """Desenha uma parte sombreada; edge=True contorna a parte por cima do que ja existe."""
    L = shade_parts(lines, ramps, hd, ld) if ramps else lines
    if edge:
        W = max(len(l) for l in L)
        pts = set()
        for j, l in enumerate(L):
            for i, c in enumerate(l):
                if c != ' ':
                    pts.add((i, j))
        for (i, j) in list(pts):
            for di, dj in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                ni, nj = i + di, j + dj
                if (ni, nj) not in pts:
                    X, Y = x + ni + s.ox, y + nj + s.oy
                    if 0 <= X < s.w and 0 <= Y < s.h and s.g[Y][X] != '.':
                        s.g[Y][X] = 'o'
    s.rows(y, x, L)
