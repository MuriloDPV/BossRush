import os
from PIL import Image, ImageEnhance
from hero import hero
from dragon import dragon
from enemies import ENEMIES
from buttons import medallion
import bg

OUT = 'out/app/src/main/res'
NODPI = OUT + '/drawable-nodpi'; DRW = OUT + '/drawable'
os.makedirs(NODPI, exist_ok=True); os.makedirs(DRW, exist_ok=True)
SC = 8
hero().image(SC).save(f'{NODPI}/heroi.png', optimize=True)
dragon().image(SC).save(f'{NODPI}/chefe_dragao.png', optimize=True)
for n, f in ENEMIES:
    f().image(SC).save(f'{NODPI}/chefe_{n}.png', optimize=True)
for n, f in bg.AREAS:
    f().resize((bg.W * 6, bg.H * 6), Image.NEAREST).save(f'{NODPI}/{n}.png', optimize=True)

def disabled(im):
    g = im.convert('LA').convert('RGBA')
    g = ImageEnhance.Brightness(g).enhance(0.55)
    g.putalpha(im.getchannel('A'))
    return g

BTN = {'escudo': 'shield', 'espada': 'sword', 'especial': 'special', 'cura': 'heal', 'config': 'gear'}
for name, icon in BTN.items():
    n = medallion(icon).image(SC); p = medallion(icon, pressed=True).image(SC)
    n.save(f'{NODPI}/btn_{name}.png', optimize=True)
    p.save(f'{NODPI}/btn_{name}_press.png', optimize=True)
    disabled(n).save(f'{NODPI}/btn_{name}_off.png', optimize=True)
    items = ['    <item android:state_enabled="false" android:drawable="@drawable/btn_%s_off" />' % name,
             '    <item android:state_pressed="true" android:drawable="@drawable/btn_%s_press" />' % name]
    if name == 'escudo':
        medallion(icon, active=True).image(SC).save(f'{NODPI}/btn_escudo_ativo.png', optimize=True)
        items.append('    <item android:state_activated="true" android:drawable="@drawable/btn_escudo_ativo" />')
    items.append('    <item android:drawable="@drawable/btn_%s" />' % name)
    open(f'{DRW}/btn_{name}_sel.xml', 'w').write(
        '<?xml version="1.0" encoding="utf-8"?>\n<!-- Botao em pixel art: estados desativado / pressionado / normal -->\n'
        '<selector xmlns:android="http://schemas.android.com/apk/res/android">\n' + '\n'.join(items) + '\n</selector>\n')

open(f'{DRW}/hud_panel.xml', 'w').write('''<?xml version="1.0" encoding="utf-8"?>
<!-- Painel escuro com borda de bronze para os textos da batalha -->
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#D91B223C" />
    <stroke android:width="2dp" android:color="#D0A36D" />
    <corners android:radius="4dp" />
</shape>
''')
