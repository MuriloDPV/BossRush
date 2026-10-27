from px import Sprite, PAL
import math

ICONS = {
 'shield': [
  ' yggGGGGgy ',
  'ynNvGgnnnny',
  'yNvvGgNnnny',
  'yGGGGGgggyy',
  'ygggGgggyyy',
  'ynNNGgnnnny',
  ' ynNGgnnny ',
  ' ynNGgnnny ',
  '  ynGgnny  ',
  '   yGgny   ',
  '    ygy    ',
  '     y     ',
 ],
 'sword': [
  '          ow',
  '         owl',
  '        owlo',
  '       owlo ',
  '      owlo  ',
  '     owlo   ',
  ' y  owlo    ',
  ' yGowlo     ',
  '  yGlo      ',
  '  bygG      ',
  ' bBb yG     ',
  'bBb   y     ',
  'gb          ',
 ],
 'special': [
  '      QQq  ',
  '     QQq   ',
  '    QQq    ',
  '   QQq     ',
  '  QQQQQQq  ',
  '     QQq   ',
  '    QQq    ',
  '   QQq     ',
  '  QQq      ',
  ' Qq        ',
 ],
 'heal': [
  '    BCB    ',
  '    bBb    ',
  '    wlw    ',
  '   wl lw   ',
  '  wRRRRrw  ',
  ' wRwRRRrrw ',
  ' wRRRRrrrw ',
  ' wRRRrrrdw ',
  ' wrrrrrddw ',
  '  wdddddw  ',
  '   wwwww   ',
 ],
 'gear': [
  '    ll    ',
  ' l lwwl l ',
  ' lwwllmmm ',
  '  wlm  mS ',
  'lwlm    mS',
  'lwm     mS',
  ' lm    mS ',
  ' lmmSSmmS ',
  ' S SmmS S ',
  '    SS    ',
 ],
}

def medallion(icon, size=26, pressed=False, active=False):
    s = Sprite(size, size)
    c = (size - 1) / 2
    R = c - 1
    for y in range(size):
        for x in range(size):
            d = math.hypot(x - c, y - c)
            if d > R: continue
            lit = (x - c) + (y - c) < 0  # cima/esquerda
            if d > R - 2.2:
                col = ('G' if lit else 'y') if not pressed else ('g' if lit else 'y')
                if R - 2.2 < d <= R - 1.4 and lit and not pressed: col = 'g'
                if active: col = 'Y' if lit else 'v'
            elif d > R - 3.2:
                col = 'b'
            else:
                if pressed:
                    col = 't'
                else:
                    col = 's' if (x - c) + (y - c) < -R * 0.9 else ('T' if (x - c) + (y - c) < R * 0.6 else 't')
            s.g[y][x] = col
    s.outline()
    lines = ICONS[icon]
    iw = max(len(l) for l in lines); ih = len(lines)
    ox = (size - iw) // 2 + (1 if pressed else 0)
    oy = (size - ih) // 2 + (1 if pressed else 0)
    tmp = Sprite(size, size)
    tmp.rows(oy, ox, lines)
    # contorno do icone
    for y in range(size):
        for x in range(size):
            if tmp.g[y][x] != '.':
                continue
            if any(0 <= x+dx < size and 0 <= y+dy < size and tmp.g[y+dy][x+dx] not in '.o' for dx,dy in ((1,0),(-1,0),(0,1),(0,-1))):
                tmp.g[y][x] = 'o'
    for y in range(size):
        for x in range(size):
            if tmp.g[y][x] != '.':
                s.g[y][x] = tmp.g[y][x]
    return s

if __name__ == '__main__':
    from PIL import Image
    names = list(ICONS)
    sheet = Image.new('RGBA', (len(names)*30*8, 2*30*8), (0x90,0x83,0x61,255))
    for i,n in enumerate(names):
        for j,p in enumerate((False, True)):
            sheet.alpha_composite(medallion(n, pressed=p).image(8), (i*30*8+16, j*30*8+16))
    sheet.save('buttons_bg.png')
