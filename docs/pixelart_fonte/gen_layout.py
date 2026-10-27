"""Gera o activity_main.xml com todas as telas no estilo pixel art medieval."""
import re

cur = open('out/app/src/main/res/layout/activity_main.xml').read()
a = cur.index('    <LinearLayout\n        android:id="@+id/battleLayout"')
b = cur.index('    <LinearLayout\n        android:id="@+id/cardLayout"')
battle = cur[a:b]


def ind(text, n):
    pad = ' ' * n
    return '\n'.join(pad + l if l.strip() else l for l in text.strip('\n').split('\n'))


def btn(id_, text, w='match_parent', h='46dp', extra='', size='16sp'):
    return f'''<androidx.appcompat.widget.AppCompatButton
    android:id="@+id/{id_}"
    style="@style/Widget.AppCompat.Button.Borderless"
    android:layout_width="{w}"
    android:layout_height="{h}"
    android:background="@drawable/botao_madeira_sel"
    android:fontFamily="@font/pixelify"
    android:shadowColor="#000000"
    android:shadowDx="2"
    android:shadowDy="2"
    android:shadowRadius="1"
    android:stateListAnimator="@null"
    android:text="{text}"
    android:textAllCaps="false"
    android:textColor="#FFF3D6"
    android:textSize="{size}"
    android:textStyle="bold"{extra} />'''


def title(text, size='34sp', id_=None, extra=''):
    idl = f'\n    android:id="@+id/{id_}"' if id_ else ''
    return f'''<TextView{idl}
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:fontFamily="@font/jacquard"
    android:gravity="center"
    android:shadowColor="#672040"
    android:shadowDx="3"
    android:shadowDy="3"
    android:shadowRadius="1"
    android:text="{text}"
    android:textColor="#FFE08A"
    android:textSize="{size}"{extra} />'''


def label(text, size='16sp', extra='', w='match_parent'):
    return f'''<TextView
    android:layout_width="{w}"
    android:layout_height="wrap_content"
    android:fontFamily="@font/pixelify"
    android:text="{text}"
    android:textColor="#F5F0E1"
    android:textSize="{size}"{extra} />'''


SEEK = '''    android:progressBackgroundTint="#3C1E28"
    android:progressTint="#D0A36D"
    android:thumbTint="#FFE08A"'''

menu = f'''<!-- ===== Tela inicial ===== -->
<LinearLayout
    android:id="@+id/menuLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="horizontal"
    android:padding="20dp"
    android:visibility="visible">

    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:gravity="center"
        android:orientation="vertical">

        <ImageView
            android:layout_width="380dp"
            android:layout_height="wrap_content"
            android:adjustViewBounds="true"
            android:contentDescription="Boss Rush"
            android:src="@drawable/logo_boss_rush" />

        <!-- Mantido para o código; o título agora é a imagem acima -->
        <TextView
            android:id="@+id/titleText"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:visibility="gone" />

        <TextView
            android:id="@+id/progressText"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="14dp"
            android:background="@drawable/hud_panel"
            android:fontFamily="@font/pixelify"
            android:gravity="center"
            android:paddingStart="14dp"
            android:paddingTop="6dp"
            android:paddingEnd="14dp"
            android:paddingBottom="6dp"
            android:text="Progresso: 0/9 mestres"
            android:textColor="#FFE08A"
            android:textSize="16sp" />
    </LinearLayout>

    <LinearLayout
        android:layout_width="300dp"
        android:layout_height="wrap_content"
        android:background="@drawable/painel"
        android:gravity="center"
        android:orientation="vertical"
        android:padding="18dp">

{ind(btn('startButton', 'Nova partida'), 8)}

{ind(btn('continueButton', 'Continuar', extra='\n    android:layout_marginTop="8dp"'), 8)}

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:orientation="horizontal">

{ind(btn('settingsButton', 'Configurações', w='0dp', extra='\n    android:layout_marginEnd="4dp"\n    android:layout_weight="1"', size='14sp'), 12)}

{ind(btn('codesButton', 'Códigos', w='0dp', extra='\n    android:layout_marginStart="4dp"\n    android:layout_weight="1"', size='14sp'), 12)}
        </LinearLayout>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:orientation="horizontal">

{ind(btn('effectsButton', 'Efeitos', w='0dp', extra='\n    android:layout_marginEnd="4dp"\n    android:layout_weight="1"', size='14sp'), 12)}

{ind(btn('resetButton', 'Reiniciar', w='0dp', extra='\n    android:layout_marginStart="4dp"\n    android:layout_weight="1"', size='14sp'), 12)}
        </LinearLayout>
    </LinearLayout>
</LinearLayout>'''

settings = f'''<!-- ===== Configurações ===== -->
<LinearLayout
    android:id="@+id/settingsLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical"
    android:padding="16dp"
    android:visibility="gone">

    <LinearLayout
        android:layout_width="460dp"
        android:layout_height="wrap_content"
        android:background="@drawable/painel"
        android:orientation="vertical"
        android:paddingStart="24dp"
        android:paddingTop="12dp"
        android:paddingEnd="24dp"
        android:paddingBottom="16dp">

        <ScrollView
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:fillViewport="true">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical">

{ind(title('Configurações'), 16)}

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="10dp"
                    android:gravity="center_vertical"
                    android:orientation="horizontal">

{ind(label('Efeitos sonoros', w='0dp', extra=chr(10) + '    android:layout_weight="1"'), 20)}

                    <Switch
                        android:id="@+id/soundSwitch"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:thumbTint="#FFE08A"
                        android:trackTint="#8A5A2E" />
                </LinearLayout>

{ind(label('Volume dos efeitos sonoros', extra=chr(10) + '    android:layout_marginTop="12dp"'), 16)}

                <SeekBar
                    android:id="@+id/volumeSeekBar"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="4dp"
                    android:max="100"
                    android:progress="70"
{ind(SEEK, 16)} />

{ind(label('Volume da música', extra=chr(10) + '    android:layout_marginTop="12dp"'), 16)}

                <SeekBar
                    android:id="@+id/musicVolumeSeekBar"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="4dp"
                    android:max="100"
                    android:progress="70"
{ind(SEEK, 16)} />
            </LinearLayout>
        </ScrollView>

{ind(btn('settingsBackButton', 'Voltar', w='200dp', extra=chr(10) + '    android:layout_gravity="center_horizontal"' + chr(10) + '    android:layout_marginTop="10dp"'), 8)}
    </LinearLayout>
</LinearLayout>'''

codes = f'''<!-- ===== Códigos ===== -->
<LinearLayout
    android:id="@+id/codesLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical"
    android:padding="12dp"
    android:visibility="gone">

    <LinearLayout
        android:layout_width="520dp"
        android:layout_height="match_parent"
        android:background="@drawable/painel"
        android:orientation="vertical"
        android:paddingStart="22dp"
        android:paddingTop="10dp"
        android:paddingEnd="22dp"
        android:paddingBottom="14dp">

        <ScrollView
            android:id="@+id/codesScrollView"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:fillViewport="true">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:gravity="center_horizontal"
                android:orientation="vertical">

{ind(title('Códigos'), 16)}

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="8dp"
                    android:gravity="center_vertical"
                    android:orientation="horizontal">

                    <EditText
                        android:id="@+id/codeInputEditText"
                        android:layout_width="0dp"
                        android:layout_height="48dp"
                        android:layout_weight="1"
                        android:background="@drawable/campo_texto"
                        android:fontFamily="@font/pixelify"
                        android:hint="Digite o código"
                        android:imeOptions="actionDone"
                        android:importantForAutofill="no"
                        android:inputType="textNoSuggestions"
                        android:maxLines="1"
                        android:paddingStart="14dp"
                        android:paddingEnd="14dp"
                        android:textColor="#FFF3D6"
                        android:textColorHint="#9C8A9E"
                        android:textCursorDrawable="@null"
                        android:textSize="17sp" />

{ind(btn('redeemCodeButton', 'Resgatar', w='150dp', h='48dp', extra=chr(10) + '    android:layout_marginStart="10dp"'), 20)}
                </LinearLayout>

                <TextView
                    android:id="@+id/codeStatusText"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="8dp"
                    android:fontFamily="@font/pixelify"
                    android:gravity="center"
                    android:minHeight="22dp"
                    android:text=""
                    android:textColor="#FFE08A"
                    android:textSize="15sp" />

                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="6dp"
                    android:fontFamily="@font/jacquard"
                    android:text="Códigos desbloqueados"
                    android:textColor="#FFE08A"
                    android:textSize="24sp" />

                <LinearLayout
                    android:id="@+id/unlockedCodesContainer"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="6dp"
                    android:orientation="vertical" />
            </LinearLayout>
        </ScrollView>

{ind(btn('codesBackButton', 'Voltar', w='200dp', extra=chr(10) + '    android:layout_gravity="center_horizontal"' + chr(10) + '    android:layout_marginTop="10dp"'), 8)}
    </LinearLayout>
</LinearLayout>'''

effects = f'''<!-- ===== Efeitos ===== -->
<LinearLayout
    android:id="@+id/effectsLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical"
    android:padding="12dp"
    android:visibility="gone">

    <LinearLayout
        android:layout_width="560dp"
        android:layout_height="match_parent"
        android:background="@drawable/painel"
        android:orientation="vertical"
        android:paddingStart="22dp"
        android:paddingTop="10dp"
        android:paddingEnd="22dp"
        android:paddingBottom="14dp">

        <ScrollView
            android:id="@+id/effectsScrollView"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:fillViewport="true">

            <LinearLayout
                android:id="@+id/effectsContainer"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:paddingBottom="8dp" />
        </ScrollView>

{ind(btn('effectsBackButton', 'Voltar', w='200dp', extra=chr(10) + '    android:layout_gravity="center_horizontal"' + chr(10) + '    android:layout_marginTop="10dp"'), 8)}
    </LinearLayout>
</LinearLayout>'''


def card(id_, cat):
    return f'''<androidx.appcompat.widget.AppCompatButton
    android:id="@+id/{id_}"
    style="@style/Widget.AppCompat.Button.Borderless"
    android:layout_width="0dp"
    android:layout_height="230dp"
    android:layout_marginStart="10dp"
    android:layout_marginEnd="10dp"
    android:layout_weight="1"
    android:background="@drawable/carta_{cat}_sel"
    android:fontFamily="@font/pixelify"
    android:gravity="center"
    android:lineSpacingExtra="2dp"
    android:paddingStart="16dp"
    android:paddingTop="26dp"
    android:paddingEnd="16dp"
    android:paddingBottom="14dp"
    android:stateListAnimator="@null"
    android:textAllCaps="false"
    android:textColor="#3A2418"
    android:textSize="14sp" />'''


cards = f'''<!-- ===== Escolha de carta ===== -->
<LinearLayout
    android:id="@+id/cardLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#99000000"
    android:gravity="center_horizontal"
    android:orientation="vertical"
    android:padding="16dp"
    android:visibility="gone">

{ind(title('Escolha uma carta', size='38sp'), 4)}

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:gravity="center"
        android:orientation="horizontal"
        android:paddingStart="24dp"
        android:paddingEnd="24dp"
        android:paddingBottom="12dp">

{ind(card('cardButtonA', 'espada'), 8)}

{ind(card('cardButtonB', 'escudo'), 8)}

{ind(card('cardButtonC', 'especial'), 8)}
    </LinearLayout>
</LinearLayout>'''

victory = f'''<!-- ===== Vitória ===== -->
<LinearLayout
    android:id="@+id/victoryLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#66000000"
    android:gravity="center"
    android:orientation="vertical"
    android:visibility="gone">

    <LinearLayout
        android:layout_width="460dp"
        android:layout_height="wrap_content"
        android:background="@drawable/painel"
        android:gravity="center"
        android:orientation="vertical"
        android:padding="22dp">

{ind(title('Vitória!', size='44sp'), 8)}

        <TextView
            android:id="@+id/victoryText"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="4dp"
            android:fontFamily="@font/pixelify"
            android:gravity="center"
            android:text="Chefe derrotado!"
            android:textColor="#F5F0E1"
            android:textSize="18sp"
            android:textStyle="bold" />

{ind(btn('nextBossButton', 'Próximo chefe', w='240dp', extra=chr(10) + '    android:layout_marginTop="18dp"'), 8)}

{ind(btn('menuButton', 'Voltar ao menu', w='240dp', extra=chr(10) + '    android:layout_marginTop="10dp"'), 8)}
    </LinearLayout>
</LinearLayout>'''

xml = f'''<?xml version="1.0" encoding="utf-8"?>
<!-- Telas do Boss Rush em pixel art medieval. Só uma fica visível por vez (o código troca). -->
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/rootLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#0B1026">

    <!-- Fundo das telas de menu: castelo à noite -->
    <ImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:contentDescription="@null"
        android:scaleType="centerCrop"
        android:src="@drawable/fundo_menu" />

{ind(menu, 4)}

{ind(settings, 4)}

{ind(codes, 4)}

{ind(effects, 4)}

{battle.rstrip()}

{ind(cards, 4)}

{ind(victory, 4)}
</FrameLayout>
'''
open('out/app/src/main/res/layout/activity_main.xml', 'w').write(xml)

import xml.dom.minidom
xml.dom.minidom.parseString(open('out/app/src/main/res/layout/activity_main.xml').read())
old = open('orig/layout/activity_main.xml').read()
io = set(re.findall(r'@\+id/(\w+)', old)); inn = set(re.findall(r'@\+id/(\w+)', open('out/app/src/main/res/layout/activity_main.xml').read()))
print('xml ok | faltando:', io - inn, '| novos:', inn - io)
