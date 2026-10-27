from px import Sprite, shade
Z=lambda L,hd=2,ld=2: shade(L,'Z','z','a',hd,ld)

def dragon():
    s = Sprite(62, 50)
    s.oy = 1; s.ox = 1
    # asa (atras)
    s.rows(0, 30, [
        '              ZZz         ',
        '           zZZZzAa        ',
        '         zZZzAAAAAa       ',
        '       zZZzAAAAAAAAa      ',
        '      zZAAAzAAAAAAAAa     ',
        '     zZAAAAzAAAAzAAAAa    ',
        '    zZAAAAAzAAAAzAAAAAa   ',
        '   zZAAAAAAzAAAAAzAAAAa   ',
        '  zZAAAAAAAzAAAAAzAAAAAa  ',
        ' zZAAAAAAAAzAAAAAAzAAAAa  ',
        ' zAAAAAAAAAzAAAAAAzAAAAAa ',
        'zAAAAAAAAAAzAAAAAAAzAAAAa ',
        'zAAAAAAAAAza AAAAAAzaAAAa ',
        'zAAAAAAAAa    aAAAAa  aAa ',
        'zAAAAAAa       aAa        ',
        'zAAAAa                    ',
        'zaAa                      ',
    ])
    # cauda
    s.rows(26, 36, Z([
        '                  X   ',
        '                 XxX  ',
        '                XxxX  ',
        '               XxxX   ',
        '               aXz    ',
        '      X   X   zZza    ',
        '     XxX XxX zZza     ',
        '    zzzzzzzzzZza      ',
        'zzzzZZZZZZZZZza       ',
        'zZZZZzzzzzzzaa        ',
        'zzzzzzzaaaaa          ',
    ]))
    # perna de tras
    s.rows(28, 30, Z([
        '   zzzzza  ',
        ' zZZZZzzza ',
        'zZZZZZzzzza',
        'zZZZZzzzzza',
        'zZZZzzzzzza',
        ' zZZzzzzza ',
        '  zZzzzza  ',
        '  zZzzza   ',
        '  zZzzza   ',
        'XaXaZzza   ',
        'XxXxXzza   ',
    ]))
    # corpo
    s.rows(15, 14, Z([
        '           X   X   X        ',
        '          XxX XxX XxX       ',
        '        zzzzzzzzzzzzzz      ',
        '      zzZZZZzzzzzzzzzzz     ',
        '    zzZZZZZZZZzzzzzzzzzzz   ',
        '   zZZZZZZZZZZZzzzzzzzzzza  ',
        '  qqZZZZZZZZZZZzzzzzzzzzza  ',
        '  zzZZZZZZZZZZzzzzzzzzzzzza ',
        ' qqqqZZZZZZZZZzzzzzzzzzzzza ',
        ' zzzzZZZZZZZZzzzzzzzzzzzzza ',
        ' qqqqqZZZZZZZzzzzzzzzzzzzza ',
        ' zzzzzZZZZZZzzzzzzzzzzzzzza ',
        ' qqqqqZZZZZzzzzzzzzzzzzzzza ',
        ' zzzzzZZZZzzzzzzzzzzzzzzzza ',
        '  qqqqZZZzzzzzzzzzzzzzzzzaa ',
        '  zzzzZZzzzzzzzzzzzzzzzzza  ',
        '   qqqqzzzzzzzzzzzzzzzzaa   ',
        '    zzzzzzzzzzzzzzzzzaa     ',
        '     aaazzzzzzzzzzzaa       ',
    ]))
    # perna da frente
    s.rows(32, 15, Z([
        '  zZZZzza ',
        ' zZZZZzzza',
        ' zZZZzzzza',
        ' zZZzzzzza',
        '  zZzzzza ',
        '  zZzzza  ',
        '  zZzzza  ',
        '  zZzzza  ',
        'XaXaXzza  ',
        'XxXxXxza  ',
    ]))
    # braco
    s.rows(22, 8, Z([
        '     zZZz',
        '    zZZza',
        '   zZZza ',
        '  zZZza  ',
        'XzZza    ',
        'XxXa     ',
        ' X X     ',
    ]))
    # pescoco
    s.rows(12, 13, Z([
        '      zzzzz  ',
        '    zZZZzzzz ',
        '   qZZZZzzzza',
        '   zZZZZzzzza',
        '  qqZZZzzzzza',
        '  zzZZZzzzza ',
    ]))
    # cabeca
    s.rows(0, 2, Z([
        '                        XX ',
        '                      XXx  ',
        '               X    XXxa   ',
        '              Xx  XXxa     ',
        '         zzzzXxaXXxa       ',
        '      zzZZZZzzXXxa         ',
        '    zzZZZZZZZZZzza         ',
        '  zzZZZZZZZaaQQozzz        ',
        ' zZZZZZZZZZZaQoozzzz       ',
        'zZZZZZZZZZZZZzzzzzzza      ',
        'azZZZZZZZZZZZZzzzzzzza     ',
        'aaazzzzzzzzzZZzzzzzzza     ',
        ' XaXaXaXqqqzZZzzzzzzza     ',
        '  QQQQQqqqqzZzzzzzzaa      ',
        ' XaXaXaXqqzzZzzzzzaa       ',
        '  azzzzzzzzzzzzzaa         ',
        '   aaaaaazzzzzaa           ',
    ]))
    for x,y in [(28,24),(29,25),(29,26),(30,27),(33,21),(34,22),(25,30),(26,31),(36,28),(37,29),(36,30),(20,35),(21,36),(33,33),(34,34)]:
        s.px(x,y,'q')
    for x,y in [(29,26),(34,22),(37,29)]:
        s.px(x,y,'Q')
    # fogo saindo da boca
    s.rows(12, 0, [
        ' q ',
        'qQQ',
        ' q ',
    ])
    s.outline()
    return s

if __name__=='__main__':
    dragon().image(8).save('dragon_preview.png')
