# Desenha o Mago e o Tanque do Boss Rush no mesmo estilo (e paleta) do heroi.png.
# O rosto do Mago é copiado do herói original para os três parecerem do mesmo jogo.
import os, sys
from PIL import Image

OUT = sys.argv[1] if len(sys.argv) > 1 else 'out'
HERO = sys.argv[2] if len(sys.argv) > 2 else 'heroi.png'
os.makedirs(OUT, exist_ok=True)

def hx(h):
    h = h.lstrip('#'); return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), 255)

P = {
    'A': '#1B223C',  # contorno
    'B': '#E9EEF2',  # brilho metal
    'C': '#C27A4A', 'D': '#8F4F3A', 'E': '#5A2E2E',  # cabelo / couro
    'F': '#7F9BB0', 'G': '#4D6680', 'P': '#2C3B57',  # aço
    'H': '#E04A4A', 'I': '#AF2645', 'J': '#672040',  # vermelho
    'K': '#FFCFAE', 'L': '#D9926E',  # pele
    'M': '#8A5A2E', 'N': '#D0A36D', 'O': '#FFE08A',  # madeira / ouro
    'Q': '#1D3A64', 'R': '#30507A', 'S': '#4A74A8',  # azul do escudo
    'U': '#7A4FC9', 'V': '#3B2A6B', 'W': '#B99AF2',  # roxo do mago
    'X': '#77B3E6', 'Y': '#F4FBFF', 'Z': '#A9D6F2',  # brilho mágico
    'T': '#B4B9C9',  # aço claro
}
P = {k: hx(v) for k, v in P.items()}

def load_hero():
    im = Image.open(HERO).convert('RGBA')
    s = im.resize((im.width // 8, im.height // 8), Image.NEAREST)
    inv = {v: k for k, v in P.items()}
    rows = []
    for y in range(s.height):
        r = ''
        for x in range(s.width):
            c = s.getpixel((x, y))
            r += '.' if c[3] < 128 else inv.get(tuple(c), 'A')
        rows.append(r)
    return rows

class Canvas:
    def __init__(self, w, h):
        self.w, self.h = w, h
        self.g = [['.'] * w for _ in range(h)]
    def blit(self, rows, ox, oy):
        for y, r in enumerate(rows):
            for x, ch in enumerate(r):
                if ch != '.' and 0 <= x + ox < self.w and 0 <= y + oy < self.h:
                    self.g[y + oy][x + ox] = ch
    def img(self):
        im = Image.new('RGBA', (self.w, self.h), (0, 0, 0, 0))
        for y in range(self.h):
            for x in range(self.w):
                ch = self.g[y][x]
                if ch != '.':
                    im.putpixel((x, y), P[ch])
        return im

def save(name, im):
    im.resize((im.width * 8, im.height * 8), Image.NEAREST).save(os.path.join(OUT, name + '.png'))

hero = load_hero()

# ------------------------------------------------------------------ MAGO
mago = Canvas(36, 48)
# chapéu pontudo com estrela e faixa dourada
hat = [
    "...............AA.......",
    "..............AWVA......",
    ".............AWUVA......",
    "............AWUUVA......",
    "...........AWUUUVA......",
    "..........AWUUUUVA......",
    ".........AWUUUOUUVA.....",
    "........AWUUOOOUUUVA....",
    ".......AWUUUUOUUUUUVA...",
    "......AWUUUUUUUUUUUUVA..",
    "....AAWUUUUUUUUUUUUUUVAA",
    "..AAOOOOOOOOOOOOOOOOOOOA",
    ".AWUUUUUUUUUUUUUUUUUUUUVA",
    "..AAVVVVVVVVVVVVVVVVVVAA.",
]
# rosto do herói (sem a faixa vermelha e sem a espada)
face = [r[7:25] for r in hero[10:18]]
mago.blit(face, 9, 13)
mago.blit(hat, 5, 0)
# manto
robe = [
    "....AVUUUUUUUUUVA.....",
    "...AVUUUUUOUUUUUVA....",
    "..AVUUUUUUOUUUUUUVA...",
    ".AVUUUUUUUOUUUUUUUVA..",
    "AVUUAUUUUUOUUUUUAUUVA.",
    "AVUUAUUUUUOUUUUUAUUVA.",
    "AKKAAUUUUUOUUUUUUAKKA.",
    "ALLAAMMMMMOMMMMMMALLA.",
    ".AA.AUUUUUOUUUUUUUAA..",
    "....AUUUUUOUUUUUUUVA..",
    "...AWUUUUUOUUUUUUUVA..",
    "...AWUUUUUOUUUUUUUUVA.",
    "..AWUUUUUUOUUUUUUUUVA.",
    "..AWUUUUUUOUUUUUUUUUVA",
    ".AWUUUUUUUOUUUUUUUUUVA",
    ".AOOOOOOOOOOOOOOOOOOOA",
    "..AAAAAEEDAAAAEEDAAAA.",
    ".....AEEDDA..AEEDDA...",
    ".....AAAAAA..AAAAAA...",
]
mago.blit(robe, 8, 21)
# cajado com orbe brilhando
staff = [
    "..AAA..",
    ".AYZXA.",
    "AYYZXXA",
    "AZZXXXA",
    "AXXXXQA",
    ".AXXQA.",
    "..AAA..",
    "..ANA..",
    "..AMA..",
    "..ANA..",
    "..AMA..",
    "..AMA..",
    "..ANA..",
    "..AMA..",
    "..AMA..",
    "..ANA..",
    "..AMA..",
    "..AMA..",
    "..ANA..",
    "..AMA..",
    "..AMA..",
    "..ANA..",
    "..AMA..",
    "..AMA..",
    "..AMA..",
    "..AMA..",
    "..AMA..",
    "...A...",
]
mago.blit(staff, 4, 13)
# mão segurando o cajado
mago.blit(["AKKA", "ALLA", ".AA."], 5, 27)
save('heroi_mago', mago.img())

# ------------------------------------------------------------------ TANQUE
tank = Canvas(40, 44)
helm = [
    "........AAA.........",
    ".......AHIA.........",
    "......AHHIJA........",
    ".....AHHIIJA........",
    "...AAAAHIJAAAAA.....",
    "..AGTBBTFFFFFFGA....",
    ".AGTBBTFFFFFFFFGA...",
    "AGTBTFFFFFFFFFFFGA..",
    "AGTTFFFFFFFFFFFFGA..",
    "AGTFFFFFFFFFFFFFGGA.",
    "AGFFFFFFFFFFFFFFFGA.",
    "AGAAAAAAAAAAAAAAAGA.",
    "AGAOAAAAAAAAAAOAAGA.",
    "AGFFFFFFFAFFFFFFFGA.",
    "AGFFFFFFFAFFFFFFGGA.",
    "AGGFFFFFFAFFFFFFGA..",
    ".AGGFFFFFAFFFFFGGA..",
    "..AAGGGGGGGGGGGAA...",
]
tank.blit(helm, 9, 0)
left_body = ['...AAAAAAA.....', '..AGTBBTFGAAAAA', '.AGTBFFFFGPGFFF', '.AGTFFFFFGPGFTB', '.AGGFFFFGGPGFBF', '..AAGGGGAAPGFFF', '...AFFGA.APGFFF', '...AFGGA.APGHHH', '...AFFGA.APGIHH', '...AKKLA.AMMMMO', '...ALLLA.APGHHH', '....AAA..APGIHH', '..........APGIH', '..........APGFA', '..........AGTBA', '..........AGFFA', '..........AGGFA', '..........AEDDA', '.........AEDDDA', '.........AAAAAA']
body = [r + r[::-1] for r in left_body]
tank.blit(body, 3, 17)
shield = [
    "AAAAAAAAAAAAA",
    "AMNNNNNNNNNMA",
    "AMQRRSOSRRQMA",
    "AMQRRSOSRRQMA",
    "AMQRRSOSRRQMA",
    "AMQRRSOSRRQMA",
    "AMOOOOOOOOOMA",
    "AMQRRSOSRRQMA",
    "AMQRRSOSRRQMA",
    "AMQRRSOSRRQMA",
    "AMQRRSOSRRQMA",
    "AMQRRSOSRRQMA",
    "AMQQRSOSRQQMA",
    ".AMQQSOSQQMA.",
    "..AMQQOQQMA..",
    "...AMMOMMA...",
    "....AMMMA....",
    ".....AAA.....",
]
tank.blit(shield, 22, 21)
save('heroi_tanque', tank.img())
print('ok')
