from px import Sprite, part

STEEL = ('l', 'm', 'S'); RED = ('R', 'r', 'd'); SKIN = ('k', 'k', 'K'); LEATH = ('C', 'B', 'b')
GOLD = ('G', 'g', 'y'); GOB = ('I', 'i', 'J'); ORC = ('7', '6', '5'); WOLF = ('4', '3', '2')
BONE = ('X', 'x', '1'); HOOD = ('J', 'j', '0'); PURP = ('V', 'U', 'u'); COAT = ('M', 'L', 'D')
CLOTH = ('s', 'T', 't'); WOOD = ('C', 'B', 'b')


def bandido():
    s = Sprite(34, 44); s.oy = 1; s.ox = 1
    # braco de tras
    part(s, 18, 22, [
        ' jjj', 'jjjj', 'jjjj', 'jjj ', 'jjj ', 'kkk ', 'kk  ',
    ], [HOOD, SKIN])
    # pernas
    part(s, 27, 10, [
        ' TTTT  TTTT ',
        ' TTTT  TTTT ',
        ' TTTT  TTTT ',
        ' TTTT  TTTT ',
        ' TTT   TTTT ',
        ' BBBB  BBBB ',
        'BBBBB BBBBB ',
        'BBBBB BBBBB ',
    ], [CLOTH, LEATH])
    # corpo
    part(s, 16, 9, [
        '   jjjjjjjjj   ',
        ' jjjjBBBBBjjjj ',
        'jjjjBBBBBBBjjjj',
        'jjjBBBBBBBBBjjj',
        'jjjBBBBBBBBBjjj',
        'jjjBBBBBBBBBjjj',
        ' jjBBBBBBBBBjj ',
        ' bbbbbbgbbbbbb ',
        ' jjjjjjjjjjjjj ',
        ' jjjjjjjjjjjjj ',
        '  jjjjjjjjjjj  ',
    ], [HOOD, LEATH], ld=1)
    s.px(16, 23, 'G')
    # capuz + rosto
    part(s, 1, 7, [
        '       jjjj       ',
        '     jjjjjjjj     ',
        '    jjjjjjjjjj    ',
        '   jjjjjjjjjjjj   ',
        '  jjjjjjjjjjjjjj  ',
        '  jjjjjjjjjjjjjjj ',
        ' jjooooooooojjjjj ',
        ' jokkkkkkkkkojjjjj',
        ' jokoookkooookjjjj',
        ' jokkekkkkekkojjjj',
        ' jokkokkkkokkojjjj',
        ' jrrrrrrrrrrrrjjjj',
        ' jrrrrrrrrrrrrrjjj',
        '  rrrrrrrrrrrrrjj ',
        '   rrrrrrrrrrrjj  ',
        '     rrr  jjjj    ',
    ], [HOOD, RED, SKIN])
    # braco da frente + adaga
    part(s, 18, 3, [
        '     jjjj',
        '    jjjjj',
        '   jjjjj ',
        ' kkkjjj  ',
        'kkkk     ',
        ' kk      ',
    ], [HOOD, SKIN], edge=True)
    for i, (x, y) in enumerate([(4, 20), (3, 19), (2, 18), (1, 17), (0, 16)]):
        s.px(x, y, 'w' if i % 2 else 'l')
        s.px(x + 1, y, 'm')
    s.px(5, 21, 'g'); s.px(3, 21, 'g'); s.px(4, 22, 'y')
    s.outline()
    return s


def lobo():
    s = Sprite(54, 36); s.oy = 1; s.ox = 1
    # pernas do lado de longe (mais escuras)
    part(s, 20, 18, [' 22', ' 22', '22 ', '22 ', '22 ', '22 ', '222', '2222'])
    part(s, 19, 41, ['2222', ' 222', '  22', '  22', ' 22 ', ' 22 ', ' 22 ', ' 222', ' 2222'])
    # cauda
    part(s, 3, 41, [
        '         333',
        '        3333',
        '       33333',
        '      333333',
        '     33333x ',
        '   333333x  ',
        ' 3333333    ',
        '333333      ',
    ], [WOLF])
    # corpo
    part(s, 8, 12, [
        '    3333333333333333333      ',
        '  33333333333333333333333    ',
        ' 3333333333333333333333333   ',
        '333333333333333333333333333  ',
        '3333333333333333333333333333 ',
        'xx3333333333333333333333333333',
        'xxx33333333333333333333333333 ',
        'xxx3333333333333333333333333  ',
        'xxxx333333333333333333333333  ',
        ' xxx33333333333333333333333   ',
        '  xxx333333     333333333     ',
        '   xx3333         33333       ',
    ], [WOLF], ld=2)
    # faixa escura de pelo nas costas
    for x, y in [(18, 8), (19, 8), (22, 8), (23, 8), (26, 8), (27, 9), (30, 8), (31, 9), (34, 9), (35, 10)]:
        s.px(x, y, '2')
    # perna da frente (perto)
    part(s, 18, 14, [' 3333', ' 3333', '  333', '  333', '  33 ', '  33 ', '  33 ', '  33 ', ' 333 ', '3333 '], [WOLF], edge=True)
    # perna de tras (perto) com joelho dobrado
    part(s, 17, 34, ['33333 ', '333333', ' 33333', '  3333', '   333', '   33 ', '  33  ', '  33  ', '  33  ', '  333 ', '  3333'], [WOLF], edge=True)
    # cabeca
    part(s, 0, 0, [
        '         3    3      ',
        '        33   33      ',
        '        3o3 3o3      ',
        '       333333333     ',
        '      33333333333    ',
        '     ooR3333333333   ',
        '   33333333333333333 ',
        ' 3333333333333333333 ',
        'o333333333333333333  ',
        'oo33333333333333333  ',
        ' WoWoWoWo3333333333  ',
        '  dddddddd33333333   ',
        ' WoWoWoW333333333    ',
        '  xxxxxxxx33333      ',
        '    xxxxxxxx         ',
    ], [WOLF])
    s.px(6, 5, 'Q')
    s.outline()
    return s


def goblin():
    s = Sprite(36, 38); s.oy = 1; s.ox = 1
    # pernas
    part(s, 26, 11, [
        ' iii  iii ', ' iii  iii ', ' iii  iii ', 'BBBB BBBB ', 'BBBB BBBB ',
    ], [GOB, LEATH])
    # corpo (tunica rasgada)
    part(s, 16, 9, [
        '   TTTTTTT    ',
        '  TTTTTTTTT   ',
        ' TTTTTTTTTTT  ',
        ' TTTTTTTTTTT  ',
        ' TTTTTTTTTTT  ',
        ' bbbbbbbbbbb  ',
        ' TTTTTTTTTTT  ',
        ' TTTTTTTTTTT  ',
        '  T TT TT T   ',
        '  T  T  T     ',
    ], [CLOTH, LEATH])
    # braco de tras
    part(s, 17, 20, [' ii', 'iii', 'iii', 'ii ', 'ii '], [GOB])
    # cabeca com orelhas grandes
    part(s, 2, 0, [
        '            iiii         ',
        '         iiiiiiiiii      ',
        '        iiiiiiiiiiii     ',
        'i      iiiiiiiiiiiiii   i',
        'iii   iiiiiiiiiiiiiii iii',
        ' iiiiiiooiiiiiooiiiiiii  ',
        '  iiiiioQQiiiiQQoiiiiii  ',
        '   iiiiiQoiiiiQoiiiiii   ',
        '      iiiiiiiiiiiiii     ',
        '      iiiiiiiiiiiiii     ',
        '      iioXoXoXoXoii      ',
        '       iiiiiiiiiiii      ',
        '        iiiiiiiiii       ',
        '          iiiiii         ',
    ], [GOB])
    # porrete na mao da frente
    part(s, 4, 3, [
        ' BBB ',
        'BlBBB',
        'BBBlB',
        'BBBBB',
        ' BBB ',
        '  B  ',
        '  B  ',
        '  B  ',
        '  B  ',
        '  B  ',
        '  B  ',
        '  B  ',
        ' iii ',
        'iiiii',
        ' iii ',
    ], [WOOD, GOB], edge=True)
    s.px(1, 6, 'l'); s.px(7, 6, 'l'); s.px(4, 3, 'l')
    part(s, 17, 6, [' iiii', 'iiii ', 'iii  '], [GOB], edge=True)
    s.outline()
    return s


def orc():
    s = Sprite(50, 52); s.oy = 1; s.ox = 1
    # machado (atras do braco)
    part(s, 2, 2, [
        '      mmm     ',
        '    mmmmm     ',
        '  lmmmmmmB    ',
        ' lmmmmmmmB    ',
        'lmmmmmmmmB    ',
        'lmmmmmmmmB    ',
        'lmmmmmmmmB    ',
        ' lmmmmmmmB    ',
        '  lmmmmmmB    ',
        '    mmmmmB    ',
        '      mmmB    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         B    ',
        '         b    ',
    ])
    for y in range(4, 13):
        s.px(1 if y in (6, 7, 8) else 2, y, 'w')
    # pernas
    part(s, 38, 14, [
        ' TTTTT   TTTTT ',
        ' TTTTT   TTTTT ',
        ' TTTTT   TTTTT ',
        ' 66666   66666 ',
        ' 6666    66666 ',
        'bbbbbb  bbbbbb ',
        'bbbbbb  bbbbbb ',
        'bbbbbb  bbbbbb ',
    ], [CLOTH, ORC, LEATH])
    # corpo
    part(s, 18, 11, [
        '      66666666666      ',
        '   6666666666666666    ',
        '  666666666666666666   ',
        ' 6666B66666666666B666  ',
        ' 66666B666666666B6666  ',
        ' 666666B6666666B66666  ',
        ' 6666666B66666B666666  ',
        ' 66666666B666B6666666  ',
        ' 666666666BgB66666666  ',
        '  66666666666666666    ',
        '  bbbbbbbbbbbbbbbbb    ',
        '  TTTTTTTTTTTTTTTTT    ',
        '  TTTTTTTTTTTTTTTTT    ',
        '  TTTTTTTTTTTTTTTTT    ',
        '   TTTTTTTTTTTTTTT     ',
        '    TTTT     TTTT      ',
        '    TTTT     TTTT      ',
        '    TTTT     TTTT      ',
        '    TTTT     TTTT      ',
        '    TTTT     TTTT      ',
    ], [ORC, LEATH, CLOTH], ld=2)
    # ombreira
    part(s, 16, 27, [
        '  mmmmm  ',
        ' mmmmmmmm',
        'mmmmmmmmm',
        'mmmmmmmmm',
        ' mmmmmmm ',
    ], [STEEL])
    s.px(30, 17, 'w'); s.px(31, 18, 'w')
    # braco de tras
    part(s, 21, 33, ['666', '6666', '6666', '6666', ' 666', ' 666', ' 666', '6666', '666 '], [ORC])
    # cabeca
    part(s, 3, 13, [
        '       bb         ',
        '      bbbb        ',
        '      bbbb        ',
        '   66666666666    ',
        '  6666666666666   ',
        ' 666666666666666  ',
        ' 66oo6666oo66666  ',
        ' 6RRo66666RRo6666 ',
        '666666666666666666',
        '666666666666666666',
        ' 6666666666666666 ',
        ' 6X6oooooooo6X666 ',
        ' 6X6o666666o6X666 ',
        '  66666666666666  ',
        '   666666666666   ',
        '     66666666     ',
    ], [ORC])
    # braco da frente segurando o cabo
    part(s, 20, 8, [
        '    6666',
        '   66666',
        '  66666 ',
        ' 6666   ',
        '66666   ',
        '66666   ',
        ' 666    ',
    ], [ORC], edge=True)
    s.outline()
    return s


def bandido_lider():
    s = Sprite(40, 48); s.oy = 1; s.ox = 1
    # pernas
    part(s, 33, 12, [
        ' TTTT  TTTT ',
        ' TTTT  TTTT ',
        ' TTTT  TTTT ',
        ' bbbb  bbbb ',
        'bbbbb  bbbbb',
        'bbbbb  bbbbb',
        'bbbbbb bbbbb',
        'bbbbbb bbbbbb',
    ], [CLOTH, ('b', 'b', 'o')])
    # casaco
    part(s, 18, 9, [
        '    LLLLLLLLL     ',
        '  LLLLLLwLLLLLL   ',
        ' LLLLLLwwwLLLLLL  ',
        ' LLLLLLwwwLLLLLL  ',
        'LLLLLgLwwwLgLLLLL ',
        'LLLLLLLwwwLLLLLLL ',
        'LLLLLgLLwLLgLLLLL ',
        'LLLLLrrrrrrrLLLLL ',
        'LLLLLrrrrrrrLLLLL ',
        'LLLLLLLLLLLLLLLLL ',
        'LLLLLgLLLLLgLLLLL ',
        'LLLLLLLLLLLLLLLLLL',
        'LLLLLL     LLLLLLL',
        'LLLLL       LLLLLL',
        ' LLL         LLLL ',
    ], [COAT, RED], ld=1)
    # braco de tras
    part(s, 19, 25, [' LLL', 'LLLL', 'LLLL', 'LLL ', 'LLL ', 'kkk ', 'kk  '], [COAT, SKIN])
    # cabeca
    part(s, 8, 10, [
        '   kkkkkkkk   ',
        '  kkkkkkkkkk  ',
        ' oookkkkoookk ',
        ' ooo kkkkokkk ',
        ' oookkkkkokkk ',
        ' kkkkkkkkkkkK ',
        'hhhhhhkkkkkkK ',
        ' hhhhhhkkkkK  ',
        '  kkkkkkkkK   ',
        '   KKKKKKK    ',
    ], [SKIN])
    s.px(13, 11, 'h')
    # chapeu de aba larga + pena
    part(s, 0, 4, [
        '                  RR    ',
        '                 RRr    ',
        '        LLLLLL  RRr     ',
        '       LLLLLLLLRRr      ',
        '      LLLLLLLLLLrd      ',
        '      LLgggggggLL       ',
        '  LLLLLLLLLLLLLLLLLLL   ',
        'LLLLLLLLLLLLLLLLLLLLLLL ',
        ' LLLL           LLLLL   ',
    ], [COAT, RED, GOLD])
    # braco da frente + sabre
    part(s, 19, 4, [
        '      LLLL',
        '     LLLLL',
        '    LLLLL ',
        '  LLLLL   ',
        ' kkLL     ',
        'kkkk      ',
        ' kk       ',
    ], [COAT, SKIN], edge=True)
    blade = [(2, 22), (1, 21), (1, 20), (0, 19), (0, 18), (0, 17), (0, 16), (1, 15), (1, 14), (2, 13)]
    for x, y in blade:
        s.px(x, y, 'l')
        s.px(x + 1, y, 'w')
    s.px(1, 23, 'g'); s.px(2, 23, 'G'); s.px(3, 23, 'g'); s.px(4, 24, 'y')
    s.outline()
    return s


def esqueleto_forte():
    s = Sprite(46, 54); s.oy = 1; s.ox = 1
    # espadao (atras)
    part(s, 0, 4, [
        ' l ', 'lwm', 'lwm', 'lwm', 'lwm', 'lwm', 'lwm', 'lwm', 'lwm', 'lwm', 'lwm', 'lwm', 'lwm', 'lwm',
        'lwm', 'lwm', 'lwm', 'lwm', 'lwm', 'lwm', 'lwm', 'lwm',
    ])
    part(s, 22, 0, ['ygggGGGgy', '   BBB   ', '   BBB   ', '   BBB   ', '   BBB   ', '   BBB   ', '    g    '], [WOOD])
    # pernas de osso
    part(s, 36, 14, [
        '  mmmmmmmmmmmm  ',
        '  mmmm    mmmm  ',
        '   xx      xx   ',
        '   xx      xx   ',
        '  xxxx    xxxx  ',
        '   xx      xx   ',
        '   xx      xx   ',
        '  mmmm    mmmm  ',
        ' mmmmm   mmmmm  ',
        'mmmmmm  mmmmmm  ',
    ], [BONE, STEEL])
    # torso: peitoral + costelas
    part(s, 18, 13, [
        '    mmmmmmmmmm     ',
        '  mmmmmmmmmmmmmm   ',
        ' mmmmmmmmmmmmmmmm  ',
        ' mmmmmmmmmmmmmmmm  ',
        ' mmmmmmmmmmmmmmmm  ',
        ' mmmmmmmmmmmmmmmm  ',
        '  mmmmmmmmmmmmmm   ',
        '   xxoxxxxxxoxx    ',
        '   xoxxxxxxxxox    ',
        '    xxoxxxxoxx     ',
        '      xxxxxx       ',
        '      xxxxxx       ',
        '     bbbbbbbb      ',
        '    bbbbgbbbbb     ',
        '    BBBBBBBBBB     ',
        '   BBBB  BBBBB     ',
        '   BBB    BBBB     ',
        '                   ',
    ], [STEEL, BONE, LEATH])
    s.px(19, 21, 'w'); s.px(18, 22, 'w'); s.px(20, 20, 'w')
    for y in range(25, 30):
        s.px(22, y, 'x')
    # braco de tras
    part(s, 19, 30, ['mmmm', 'mmmm', ' xx ', ' xx ', ' xx ', ' xx ', ' xx ', 'xxxx', ' xx '], [STEEL, BONE])
    # cranio + elmo enferrujado
    part(s, 2, 13, [
        '      mmmmmm       ',
        '    mmmmmmmmmm     ',
        '   mmmmmmmmmmmm    ',
        '  mmmmmmmmmmmmmm   ',
        '  mmmmmmmmmmmmmmm  ',
        '  ggggggggggggggg  ',
        '  xxxxxxxxxxxxxxm  ',
        ' xxooooxxooooxxxm  ',
        ' xxoRRoxxoRRoxxxm  ',
        ' xxooooxxooooxxm   ',
        ' xxxxxxxoxxxxxxm   ',
        '  xxxxxooxxxxxx    ',
        '  xoxoxoxoxoxox    ',
        '   xxxxxxxxxxx     ',
        '    xoxoxoxox      ',
        '     xxxxxxx       ',
    ], [STEEL, BONE, GOLD])
    s.px(16, 4, 'w'); s.px(17, 3, 'w'); s.px(18, 3, 'w')
    # braco da frente segurando o espadao
    part(s, 19, 5, [
        '        mmmm',
        '       mmmmm',
        '     xxxmmm ',
        '   xxxx     ',
        ' xxxx       ',
        'xxxx        ',
        'xxx         ',
    ], [STEEL, BONE], edge=True)
    s.outline()
    return s


def goblin_rei():
    s = Sprite(46, 50); s.oy = 1; s.ox = 1
    # capa vermelha (atras)
    part(s, 14, 10, [
        '        rrrrrrrrrrrr      ',
        '     rrrrrrrrrrrrrrrrrr   ',
        '   rrrrrrrrrrrrrrrrrrrrr  ',
        '  rrrrrrrrrrrrrrrrrrrrrr  ',
        '  rrrrrrrrrrrrrrrrrrrrrrr ',
        ' rrrrrrrrrrrrrrrrrrrrrrrr ',
        ' rrrrrrrrrrrrrrrrrrrrrrrr ',
        ' rrrrrrrrrrrrrrrrrrrrrrrrr',
        ' rrrrrrrrrrrrrrrrrrrrrrrrr',
        'rrrrrrrrrrrrrrrrrrrrrrrrrr',
        'rrrrrrrrrrrrrrrrrrrrrrrrrr',
        'rrrrrrrrrrrrrrrrrrrrrrrrrr',
        'rrrrrrrrrrrrrrrrrrrrrrrrrr',
        'rrrrrrrrrrrrrrrrrrrrrrrrrr',
        ' rrrrrrrrrrrrrrrrrrrrrrrr ',
        ' WWWW4WWWW4WWWW4WWWW4WWWW ',
        '  WW4WWW4WWWW4WWW4WWWW    ',
    ], [RED], ld=2)
    # pernas
    part(s, 35, 14, [
        ' iiii    iiii ',
        ' iiii    iiii ',
        ' iiii    iiii ',
        'BBBBBB  BBBBBB',
        'BBBBBB  BBBBBB',
        'bbbbbb  bbbbbb',
    ], [GOB, LEATH])
    # barrigao
    part(s, 19, 11, [
        '     iiiiiiiiii      ',
        '   iiiiiiiiiiiiii    ',
        '  iiiiiiiiiiiiiiii   ',
        ' iiiiiiiiiiiiiiiiii  ',
        ' iiiiiiiiiiiiiiiiii  ',
        'iiiiiiiiiiiiiiiiiiii ',
        'iiiiiiiiiiiiiiiiiiii ',
        'iiiiiiiiiiiiiiiiiiii ',
        'bbbbbbbbbGGbbbbbbbbb ',
        'bbbbbbbbbGGbbbbbbbbb ',
        'iiiiiiiiiiiiiiiiiiii ',
        ' iiiiiiiiiiiiiiiiii  ',
        '  TTTTTTTTTTTTTTTT   ',
        '  TTTTTTTTTTTTTTTT   ',
        '   TTTTTT  TTTTTT    ',
        '   TTTTT    TTTTT    ',
        '                     ',
        '                     ',
        '                     ',
        '                     ',
    ], [GOB, LEATH, CLOTH], ld=2)
    for x, y in [(15, 23), (16, 24), (17, 23)]:
        s.px(x, y, 'J')
    # cabeca + orelhas + coroa
    part(s, 0, 4, [
        '          G  G  G  G        ',
        '          GgGGgGGgGGg       ',
        '          gRgggQgggRg       ',
        '          ggggggggggg       ',
        '        iiiiiiiiiiiiiii     ',
        '       iiiiiiiiiiiiiiiii    ',
        'i     iiiiiiiiiiiiiiiiiii  i',
        'iii  iiiooiiiiiioooiiiii iii',
        ' iiiiiioQQiiiiiiQQoiiiiiii  ',
        '  iiiiiiQoiiiiiiQoiiiiiii   ',
        '     iiiiiiiiiiiiiiiiiii    ',
        '     iiiiiiiiiiiiiiiiiii    ',
        '     iioXoXoXoXoXoXoiii     ',
        '      iioooooooooooiii      ',
        '       iiiiiiiiiiiiii       ',
        '         iiiiiiiiii         ',
        '           iiiiii           ',
    ], [GOB, GOLD])
    s.px(14, 2, 'R'); s.px(22, 2, 'R'); s.px(18, 2, 'Y')
    # cetro
    part(s, 8, 2, [
        ' GGG ',
        'GgRgG',
        'GRRRG',
        'GgRgG',
        ' GgG ',
        '  g  ', '  g  ', '  g  ', '  g  ', '  g  ', '  g  ', '  g  ', '  g  ', '  g  ',
        '  g  ', '  g  ', '  g  ', '  g  ', '  g  ', '  g  ', '  g  ', '  y  ',
    ], [GOLD, RED])
    part(s, 22, 3, [
        '      iiii',
        '     iiiii',
        '   iiiii  ',
        ' iiiii    ',
        'iiii      ',
        'iii       ',
    ], [GOB], edge=True)
    s.outline()
    return s


def esqueleto_rei():
    s = Sprite(48, 58); s.oy = 1; s.ox = 1
    # cajado com orbe do vazio
    part(s, 0, 1, [
        '   UUU   ',
        '  UVVUU  ',
        ' UVWVUUu ',
        ' UVVUUUu ',
        ' UUUUUuu ',
        '  uUUuu  ',
        '   ggg   ',
        '  g g g  ',
    ], [PURP])
    for y in range(8, 48):
        s.px(5, y, 'B' if y % 6 else 'b')
        s.px(4, y, 'C' if y % 6 else 'B')
    # manto roxo
    part(s, 17, 9, [
        '      UUUUUUUUUU        ',
        '    UUUUUUUUUUUUUU      ',
        '   UUUUUUUUUUUUUUUU     ',
        '  UUUUUUUxxxxxUUUUUU    ',
        '  UUUUUUxoxxxoxUUUUU    ',
        '  UUUUUUxxoxoxxUUUUUU   ',
        '  UUUUUUUxxxxxUUUUUUU   ',
        '  UUUUUUUgGGgUUUUUUUU   ',
        '  UUUUUUUUgGUUUUUUUUU   ',
        '  UUUUUUUUgGUUUUUUUUU   ',
        '  UUUUUUUUgGUUUUUUUUUU  ',
        ' UUUUUUUUUgGUUUUUUUUUU  ',
        ' UUUUUUUUUgGUUUUUUUUUU  ',
        ' UUUUUUUUUgGUUUUUUUUUUU ',
        ' UUUUUUUUUgGUUUUUUUUUUU ',
        ' UUUUUUUUUgGUUUUUUUUUUU ',
        'UUUUUUUUUUgGUUUUUUUUUUUU',
        'UUUUUUUUUUgGUUUUUUUUUUUU',
        'UUUUUUUUUUgGUUUUUUUUUUUU',
        'UUUUUUUUUUgGUUUUUUUUUUUU',
        'UUUUUUUUUUgGUUUUUUUUUUUU',
        'UUUUUUUUUUgGUUUUUUUUUUUU',
        'UUUUUUUUUUgGUUUUUUUUUUUU',
        'UUUUUUUUUUgGUUUUUUUUUUUU',
        'UUUUUUUUUUgGUUUUUUUUUUUU',
        'UUUUUUUUUUgGUUUUUUUUUUUU',
        'gGgGgGgGgGgGgGgGgGgGgGgG',
        '  xx  xx         xx  xx ',
        ' xxxx xxx       xxxx xxx',
    ], [PURP, BONE, GOLD], ld=2)
    # gola de pele
    part(s, 15, 11, [
        '   WWWWWWWWWWWWWW   ',
        ' WW4WWW4WWWW4WWW4W  ',
        '  W  WW  WW  WW  W  ',
    ])
    # cranio + coroa alta
    part(s, 0, 12, [
        '    G   G   G   G    ',
        '    GG  GG  GG  GG   ',
        '    GgG GgG GgG GgG  ',
        '    GgggggggggggggG  ',
        '    gRggggYggggRggg  ',
        '    ggggggggggggggg  ',
        '    yyyyyyyyyyyyyyy  ',
        '    xxxxxxxxxxxxxxx  ',
        '   xxxxxxxxxxxxxxxx  ',
        '   xxooooxxxooooxxx  ',
        '   xxoVVoxxxoVVoxxx  ',
        '   xxooooxxxooooxx   ',
        '   xxxxxxxoxxxxxxx   ',
        '    xxxxxoooxxxxx    ',
        '    xoxoxoxoxoxox    ',
        '     xxxxxxxxxxx     ',
        '      xoxoxoxox      ',
    ], [GOLD, BONE])
    # braco segurando o cajado
    part(s, 20, 5, [
        '      UUUUU',
        '    UUUUUUU',
        '  UUUUUUUU ',
        'xxxUUU     ',
        'xxxx       ',
        'xxx        ',
    ], [PURP, BONE], edge=True)
    s.outline()
    return s


ENEMIES = [
    ('bandido', bandido), ('lobo', lobo), ('goblin', goblin), ('orc', orc),
    ('bandido_lider', bandido_lider), ('esqueleto_forte', esqueleto_forte),
    ('goblin_rei', goblin_rei), ('esqueleto_rei', esqueleto_rei),
]

if __name__ == '__main__':
    from PIL import Image
    sc = 6
    ims = [(n, f().image(sc)) for n, f in ENEMIES]
    W = sum(i.size[0] for _, i in ims[:4]) + 5 * 30
    H = 2 * max(i.size[1] for _, i in ims) + 90
    sheet = Image.new('RGBA', (W, H), (0x90, 0x83, 0x61, 255))
    rowh = max(i.size[1] for _, i in ims) + 30
    x = 30
    for k, (n, im) in enumerate(ims):
        if k == 4:
            x = 30
        y = (k // 4) * rowh + rowh - im.size[1]
        sheet.alpha_composite(im, (x, y))
        x += im.size[0] + 30
    sheet.save('enemies_sheet.png')
