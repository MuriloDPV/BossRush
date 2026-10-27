from px import Sprite


def hero():
    s = Sprite(34, 42)
    s.oy = 1
    # ---- pernas (atras de tudo)
    s.rows(30, 11, [
        'Smllm  Smllm',
        'Smllm  Smllm',
        'SmlmS  SmlmS',
        'SSmmS  SSmmS',
        'mllwm  mllwm',
        'SmmlS  SmmlS',
        'bBCBb  bBCBb',
        'bBBBbb bBBBbb',
        'bbbbbb bbbbbb',
    ])
    # ---- tronco
    s.rows(17, 11, [
        '  SmmmmmmmS ',
        'mlwlnNNNnmmS',
        'mllmSmllmmmS',
        'SmlmSmlwlmSS',
        'SmmmSmllmmSS',
        'SSmmSmmmmSSS',
        'bbBCyGgyCBbb',
        'rRRRrrrrrrrd',
        'rRRrrgGgrrrd',
        'rRRrgGGGgrrd',
        'rRrrrgGgrrdd',
        'drrrrrgrrrdd',
        ' drrrrrrrdd ',
    ])
    # ---- braco da espada (esquerda)
    s.rows(19, 6, [
        '  mlw',
        ' Smlm',
        'Smlm ',
        'SmmS ',
    ])
    # mao + espada
    s.rows(17, 1, [
        ' yggGgy ',
        '  bkkb  ',
        '  KkkK  ',
        '  bkkb  ',
        '   gG   ',
    ])
    for y in range(1, 17):
        s.seg(y, 3, 'lwm' if y > 1 else ' w ')
    s.seg(0, 4, 'w')
    s.seg(15, 3, 'mlm')
    s.seg(16, 3, 'mlm')
    # ---- cabeca (sem capacete: cabelo castanho + faixa vermelha)
    s.rows(0, 7, [
        '     C    C       ',
        '    CB   CBb  C   ',
        '   CBBb CBBbbCb   ',
        '  CCBBBbCBBBBBbb  ',
        ' CBBCBBBBCBBBBBbb ',
        ' BBBBCBBBBCBBBBbbb',
        'bBBBBBCBBBBCBBBbb ',
        ' BBBBBBBBBBBBBBbb ',
        ' RRRRRRRRRRRRrrdb ',
        ' BBkBBBkkBBkBBkbb ',
        ' Bbkkkkkkkkkkkkbb ',
        ' bbkbbkkkkkbbkkb  ',
        ' bbkkokkkkkokkkb  ',
        ' bkkkokkkkkokkKb  ',
        '  bkkkkkkkkkkkKb  ',
        '   KkkkkKKkkkkK   ',
        '    KKkkkkkkKK    ',
    ])
    s.rows(17, 13, ['SmmmmmmmS'])
    # ---- escudo (frente, direita)
    s.rows(17, 21, [
        'yggGGGGgggy',
        'ynNvGgnnnny',
        'yNvvGgNnnny',
        'yNvNGgnnnny',
        'yGGGGGgggyy',
        'ygggGgggyyy',
        'ynNNGgnnnny',
        ' ynNGgnnny ',
        ' ynNGgnnny ',
        ' ynNGgnnny ',
        '  ynGgnny  ',
        '  ynGgnny  ',
        '   yGgny   ',
        '    ygy    ',
        '     y     ',
    ])
    s.outline()
    return s


if __name__ == '__main__':
    hero().image(10).save('hero_preview.png')
