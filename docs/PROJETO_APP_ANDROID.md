# PROJETO APP ANDROID --- BOSS RUSH

## 1. Visão geral

### Nome do aplicativo

**Boss Rush**

### Descrição

Boss Rush é um jogo mobile 2D de ação no estilo Boss Rush, desenvolvido
para Android e posteriormente preparado para iOS. O jogador controla um
robô futurista equipado com espada, escudo e ataque especial e enfrenta
uma sequência de 10 chefes robôs.

### Objetivo

Criar uma experiência de combate rápida, simples de entender e adequada
para dispositivos móveis, com controles por toque, progressão por cartas
e dificuldade crescente.

### Problema que resolve

Como projeto acadêmico, o aplicativo demonstra na prática conceitos de
programação, interface Android, lógica de jogo, gerenciamento de
estados, eventos de toque, armazenamento local e organização de código.

### Público-alvo

Jogadores que gostam de jogos de ação 2D, batalhas contra chefes e
partidas curtas em dispositivos móveis.

### Diferenciais

-   Pixel art futurista.
-   Combate focado exclusivamente em chefes.
-   Espada, escudo e ataque especial com características distintas.
-   Sistema de cartas para modificar o estilo de combate.
-   Funcionamento totalmente offline.
-   Tutorial inicial.
-   Dez chefes com padrões de ataque diferentes.
-   Interface horizontal pensada para toque.

## 2. Conceito do aplicativo

Ao abrir o jogo, o usuário acessará a tela inicial e poderá iniciar uma
nova partida. Na primeira execução, será apresentado um tutorial
interativo que ensina movimentação, ataque com espada, uso do escudo,
ataque especial com recarga, pulo, esquiva e cura.

Depois do tutorial, o jogador enfrentará os dez chefes em sequência.
Cada batalha acontecerá em uma arena futurista limpa e visualmente
organizada.

O personagem possuirá uma barra de vida e uma habilidade de cura que
recupera 20% da vida. A cura deverá ter uma limitação de uso, como
quantidade de cargas ou recarga, para evitar utilização ilimitada.

Ao derrotar um chefe, o jogador receberá cartas de melhoria. As cartas
poderão modificar individualmente a espada, o escudo ou o ataque
especial, aumentar dano ou adicionar efeitos como queimadura, veneno,
void e roubo de vida.

A dificuldade aumentará progressivamente. Os chefes terão mais vida,
ataques mais difíceis de evitar e padrões mais variados. O décimo chefe
terá uma segunda fase.

Para manter o projeto viável em quatro aulas, o jogo deve utilizar uma
estrutura simples, sem servidor, contas de usuário ou sistemas online.

## 3. Funcionalidades

### Funcionalidades principais

1.  Tela inicial.
2.  Tutorial interativo.
3.  Seleção ou início da sequência de chefes.
4.  Arena de batalha 2D lateral.
5.  Movimento horizontal.
6.  Pulo.
7.  Esquiva.
8.  Ataque com espada.
9.  Uso de escudo defensivo.
10. Ataque especial com recarga em turnos.
11. Sistema de vida.
12. Habilidade de cura de 20%.
13. Dez chefes.
14. Padrões de ataque diferentes para os chefes.
15. Progressão de dificuldade.
16. Sistema de cartas.
17. Aplicação de melhorias às habilidades.
18. Efeitos de status.
19. Vitória e derrota.
20. Pausa durante a partida.
21. Funcionamento offline.

### Funcionalidades complementares

-   Indicador visual de recarga da cura.
-   Barra de vida do chefe.
-   Barra de vida do jogador.
-   Indicador do chefe atual.
-   Pequenas animações de impacto.
-   Efeitos de partículas simples.
-   Tela de seleção de cartas após determinadas vitórias.
-   Confirmação antes de reiniciar uma partida.
-   Salvamento local do progresso.
-   Configuração de volume dos efeitos sonoros.
-   Botão para reiniciar o progresso, protegido por confirmação.
-   Tela de informações explicando os efeitos das cartas.

### Possíveis funcionalidades futuras

-   Mais chefes.
-   Novas armas.
-   Novos personagens.
-   Sistema de conquistas.
-   Ranking local.
-   Modos de dificuldade.
-   Novas arenas.
-   Cartas raras.
-   Combinações entre efeitos.
-   Controle externo por gamepad.
-   Port para iOS completo.
-   Mais efeitos sonoros e animações.

## 4. Telas

### Tela inicial

**Objetivo:** apresentar o jogo e permitir iniciar ou continuar uma
partida.

**Componentes:** ImageView para o logotipo, Button para jogar, Button
para configurações e TextView para informações de versão.

**Informações:** nome Boss Rush e estado básico do progresso.

**Botões:** Jogar, Configurações e, se necessário, Reiniciar progresso.

**Ações:** Jogar inicia a sequência; Configurações abre opções;
Reiniciar solicita confirmação.

**Navegação:** Tela inicial → Tutorial ou partida.

### Tutorial

**Objetivo:** ensinar os controles antes da primeira batalha.

**Componentes:** ImageView, TextView, controles virtuais e botões de
ação.

**Informações:** instruções curtas sobre movimentação, espada, escudo,
ataque especial, pulo, esquiva e cura.

**Ações:** o jogador precisa realizar ações simples para avançar.

**Comportamento:** cada etapa destaca o controle necessário.

**Navegação:** Tela inicial → Tutorial → Primeira batalha.

### Arena de batalha

**Objetivo:** realizar o combate.

**Componentes:** SurfaceView ou uma área de jogo customizada,
indicadores de vida, textos, imagens e controles virtuais.

**Informações:** vida do jogador, vida do chefe, chefe atual, habilidade
de cura e estado da batalha.

**Controles:** direcional à esquerda; ataque, escudo, cura e ataque
especial à direita.

**Ações:** tocar nos controles executa as ações correspondentes.

**Navegação:** Arena → Vitória, Derrota ou Pausa.

### Tela de cartas

**Objetivo:** permitir a escolha de uma melhoria.

**Componentes:** Cards ou layouts clicáveis contendo nome, ícone,
descrição e efeito.

**Informações:** efeito da carta e arma afetada.

**Ações:** selecionar uma carta e confirmar a escolha.

**Navegação:** Vitória → Cartas → Próxima batalha.

### Tela de vitória

**Objetivo:** informar que o chefe foi derrotado.

**Componentes:** TextView, ImageView e Button.

**Ações:** continuar para cartas ou próxima batalha.

### Tela de derrota

**Objetivo:** informar o fim da tentativa.

**Componentes:** mensagem, botão tentar novamente e botão voltar ao
menu.

### Tela de pausa

**Objetivo:** interromper temporariamente a partida.

**Botões:** Continuar, Reiniciar batalha e Sair.

### Configurações

**Objetivo:** controlar opções simples do jogo.

**Componentes:** Switch ou CheckBox para efeitos sonoros, SeekBar para
volume e Button para voltar.

Como o jogo não terá música, a configuração deverá ser direcionada
principalmente aos efeitos sonoros.

## 5. Experiência do usuário

Fluxo principal:

1.  O usuário abre Boss Rush.
2.  A tela inicial é exibida.
3.  Na primeira execução, o tutorial é iniciado.
4.  O tutorial ensina cada controle.
5.  O jogador entra na primeira arena.
6.  O chefe aparece e inicia o combate.
7.  O jogador utiliza espada, escudo, ataque especial, pulo e esquiva.
8.  Quando necessário, utiliza a cura.
9.  Ao derrotar o chefe, recebe opções de cartas.
10. Escolhe uma melhoria.
11. Entra na próxima batalha.
12. O processo continua até o décimo chefe.
13. O décimo chefe possui uma segunda fase.
14. Após a vitória final, o jogo apresenta a conclusão da partida.

A experiência deve ser direta, com poucos menus e pouca interrupção
entre as batalhas.

## 6. Fluxo de navegação

``` text
Tela Inicial
    │
    ├── Tutorial
    │      └── Primeira Batalha
    │
    ├── Continuar
    │      └── Batalha Atual
    │
    └── Configurações
           └── Tela Inicial

Batalha
    ├── Pausa
    │    ├── Continuar
    │    ├── Reiniciar
    │    └── Sair
    │
    ├── Derrota
    │    ├── Tentar Novamente
    │    └── Menu
    │
    └── Vitória
         └── Cartas
              └── Próximo Chefe
```

## 7. Interface

### Padrão visual

A interface deve utilizar pixel art futurista, com alto contraste entre
personagem, chefe, ataques e cenário. A arena não deve ficar poluída.

### Componentes

-   `TextView`: textos, nomes e indicadores.
-   `ImageView`: ícones, sprites e imagens estáticas.
-   `Button`: menus e ações.
-   `FrameLayout`: composição de elementos sobrepostos.
-   `LinearLayout`: organização de menus simples.
-   `CardView`: cartas de melhoria.
-   `ScrollView`: telas de informações ou configurações quando
    necessário.
-   `Dialog`: confirmações.
-   `SeekBar`: volume dos efeitos.
-   `Switch`: ativação ou desativação de efeitos sonoros.
-   `SurfaceView`: opção recomendada para a área de jogo caso a
    implementação seja feita diretamente com Canvas.
-   `Canvas`: desenho de sprites, arenas, barras e efeitos simples.

### Interface da batalha

No topo: - Barra de vida do jogador. - Barra de vida do chefe. - Nome ou
identificação do chefe. - Número da batalha.

No canto inferior esquerdo: - Direcional virtual.

No canto inferior direito: - Ataque. - Esquiva. - Pulo. - Disparo. -
Cura.

Os botões devem possuir tamanho suficiente para toque confortável e não
cobrir elementos importantes da arena.

## 8. Modelo de dados

### Jogador

  Campo              Tipo   Obrigatório   Descrição
  ------------------ ------ ------------- --------------------------------
  id                 int    Sim           Identificador
  vidaAtual          int    Sim           Vida atual
  vidaMaxima         int    Sim           Vida máxima
  danoEspada         int    Sim           Dano base da espada
  danoEspecial       int    Sim           Dano base do ataque especial
  curasDisponiveis   int    Sim           Quantidade de usos disponíveis
  chefeAtual         int    Sim           Chefe atual

### Chefe

  Campo        Tipo      Obrigatório   Descrição
  ------------ --------- ------------- -------------------------------
  id           int       Sim           Identificador
  nome         String    Sim           Nome do chefe
  vidaMaxima   int       Sim           Vida máxima
  danoBase     int       Sim           Dano dos ataques
  velocidade   float     Sim           Velocidade
  faseExtra    boolean   Sim           Indica se possui segunda fase
  cor          String    Sim           Identificação visual

### Carta

  Campo        Tipo     Obrigatório   Descrição
  ------------ -------- ------------- -----------------------
  id           int      Sim           Identificador
  nome         String   Sim           Nome
  descricao    String   Sim           Explicação
  habilidade   String   Sim           Espada, escudo ou ataque especial
  tipoEfeito   String   Sim           Tipo da melhoria
  valor        float    Sim           Intensidade do efeito

### Progresso

  Campo               Tipo      Obrigatório   Descrição
  ------------------- --------- ------------- -----------------------
  chefeAtual          int       Sim           Último chefe liberado
  tutorialConcluido   boolean   Sim           Estado do tutorial
  efeitosSonoros      boolean   Sim           Estado do áudio
  volume              int       Sim           Volume dos efeitos

## 9. Armazenamento

A primeira versão deve utilizar armazenamento local.

Para configurações simples, recomenda-se `SharedPreferences`, pois o
projeto possui poucos dados persistentes.

O progresso pode armazenar: - tutorial concluído; - chefe liberado; -
configurações de áudio; - progresso básico da partida.

Não é necessário servidor, banco de dados remoto, login ou API.

Caso o sistema de cartas e progresso cresça muito, Room poderá ser
considerado em uma versão futura. Para quatro aulas, `SharedPreferences`
e estruturas Java em memória são suficientes.

## 10. Regras de negócio

1.  O jogador começa com atributos básicos definidos pelo jogo.
2.  A espada realiza ataques próximos.
3.  O escudo reduz o dano recebido e pode refletir parte dele ao atacante.
4.  O ataque especial causa dano elevado e pode aplicar efeitos negativos.
5.  A cura recupera 20% da vida máxima.
6.  A cura não pode ultrapassar a vida máxima.
7.  A quantidade de usos da cura deve ser limitada ou possuir recarga.
8.  Um chefe é derrotado quando sua vida chega a zero.
9.  O jogador perde quando sua vida chega a zero.
10. Após uma vitória, uma recompensa de carta pode ser apresentada.
11. A carta escolhida altera os atributos correspondentes.
12. O dano da espada, do escudo e do ataque especial deve ser calculado
    separadamente.
13. Efeitos de status devem possuir duração ou quantidade de aplicações
    definida.
14. Os chefes devem ficar progressivamente mais difíceis.
15. O décimo chefe terá uma segunda fase.
16. Não existem chefes secretos na versão inicial.
17. O jogo não depende de internet.
18. O progresso salvo deve permanecer disponível após fechar o
    aplicativo.
19. Durante pausa, o combate não deve continuar.

## 11. Validações

  -----------------------------------------------------------------------
  Campo/Ação              Regra                   Mensagem
  ----------------------- ----------------------- -----------------------
  Iniciar jogo            Verificar estado mínimo Não foi possível
                          do jogo                 iniciar a partida.

  Cura                    Verificar se há cura    Nenhuma cura
                          disponível              disponível.

  Cura                    Não exceder vida máxima Vida já está cheia.

  Carta                   Exigir uma carta        Selecione uma carta.
                          selecionada             

  Reiniciar               Solicitar confirmação   Deseja reiniciar a
                                                  batalha?

  Reiniciar progresso     Solicitar confirmação   Todo o progresso será
                                                  apagado. Continuar?

  Configuração de volume  Limitar entre 0 e 100   Valor de volume
                                                  inválido.

  Navegação durante       Bloquear ações          Finalize ou pause a
  batalha                 incompatíveis           batalha antes de sair.
  -----------------------------------------------------------------------

## 12. Tratamento de erros

O aplicativo deve evitar encerramentos inesperados causados por valores
inválidos ou estados incorretos.

Casos importantes: - arquivo de progresso inexistente; - progresso
corrompido; - tentativa de acessar um chefe inválido; - carta
inexistente; - valor de vida abaixo de zero; - valor de vida acima do
máximo; - toque simultâneo em vários controles; - mudança de
orientação; - fechamento inesperado do aplicativo.

O jogo deve possuir valores padrão caso um dado salvo não possa ser
recuperado.

A orientação deve permanecer horizontal durante o jogo.

## 13. Recursos visuais

Recursos necessários: - Sprite do robô jogador. - Animação da espada. -
Animação do ataque especial. - Sprites dos 10 chefes. - Animações
básicas dos chefes. - Cenários futuristas. - Ícones dos controles. -
Ícones das cartas. - Efeitos de impacto. - Efeito de cura. - Efeitos de
queimadura, veneno e void. - Elementos da interface.

Para manter o projeto viável, recomenda-se criar ou utilizar recursos
gratuitos, simples e consistentes em pixel art. Recursos externos devem
possuir licença compatível com o projeto acadêmico.

Os efeitos podem ser feitos com sprites simples, animações quadro a
quadro e partículas básicas.

## 14. Estrutura do projeto Android

Estrutura sugerida:

``` text
app/
├── src/main/
│   ├── java/com/example/bosshrush/
│   │   ├── MainActivity.java
│   │   ├── GameActivity.java
│   │   ├── TutorialActivity.java
│   │   ├── SettingsActivity.java
│   │   ├── game/
│   │   │   ├── GameView.java
│   │   │   ├── GameState.java
│   │   │   └── GameLoop.java
│   │   ├── model/
│   │   │   ├── Player.java
│   │   │   ├── Boss.java
│   │   │   ├── Card.java
│   │   │   └── GameProgress.java
│   │   ├── combat/
│   │   │   ├── Weapon.java
│   │   │   ├── Sword.java
│   │   │   ├── SpecialAttack.java
│   │   │   └── StatusEffect.java
│   │   └── storage/
│   │       └── SaveManager.java
│   ├── res/
│   │   ├── layout/
│   │   ├── drawable/
│   │   ├── mipmap/
│   │   ├── raw/
│   │   └── values/
│   └── AndroidManifest.xml
```

Layouts possíveis: - `activity_main.xml` - `activity_game.xml` -
`activity_tutorial.xml` - `activity_settings.xml` - `dialog_card.xml` -
`dialog_confirm.xml`

## 15. Arquitetura

Para quatro aulas, recomenda-se uma arquitetura simples inspirada em
separação de responsabilidades.

`Activity` controla telas e ciclo de vida.

`GameView` controla a área visual do jogo e recebe os eventos de toque.

`GameState` mantém o estado atual da partida.

`Player` representa o robô e seus atributos.

`Boss` representa cada chefe.

`Card` representa as melhorias.

`Weapon`, `Sword` e `SpecialAttack` organizam o sistema de habilidades.

`StatusEffect` representa efeitos como queimadura, veneno, void e roubo
de vida.

`SaveManager` controla o armazenamento local.

O projeto não deve utilizar uma arquitetura complexa apenas para seguir
padrões de mercado. O objetivo é manter o código compreensível para um
estudante.

## 16. Plano de desenvolvimento em 4 aulas

### Aula 1 --- Estrutura e interface

Objetivos: - Criar o projeto no Android Studio. - Configurar Java e
XML. - Configurar orientação horizontal. - Criar tela inicial. - Criar
tela de configurações. - Criar tela de tutorial. - Criar estrutura
inicial da arena. - Criar controles virtuais. - Criar sprites
provisórios.

Resultado esperado: aplicativo abre, navega entre telas e apresenta uma
arena funcional visualmente.

### Aula 2 --- Funcionalidades

Objetivos: - Implementar movimentação. - Implementar pulo. - Implementar
ataque de espada. - Implementar escudo e ataque especial. - Implementar
esquiva. - Implementar vida do jogador. - Implementar vida do chefe. -
Implementar colisões básicas. - Implementar derrota e vitória. - Criar
pelo menos um chefe funcional.

Resultado esperado: uma batalha completa pode ser jogada do início ao
fim.

### Aula 3 --- Dados e recursos

Objetivos: - Implementar os 10 chefes ou estruturar os dados para os 10
chefes. - Diferenciar ataques e atributos. - Implementar cura. -
Implementar sistema de cartas. - Implementar efeitos básicos. -
Implementar progressão de dificuldade. - Implementar armazenamento
local. - Adicionar sprites e efeitos sonoros. - Criar a segunda fase do
chefe final.

Resultado esperado: o ciclo principal do Boss Rush está funcional.

### Aula 4 --- Finalização

Objetivos: - Corrigir bugs. - Testar controles em diferentes tamanhos de
tela. - Melhorar interface. - Ajustar dificuldade. - Ajustar colisões. -
Ajustar textos. - Adicionar efeitos visuais finais. - Testar
salvamento. - Testar tutorial. - Testar todas as telas. - Gerar APK de
apresentação.

Resultado esperado: versão funcional, organizada e apresentável.

## 17. Prompts para o GitHub Copilot

### Prompt 1 --- Estrutura inicial

**Objetivo:** criar a base do projeto. **Arquivos:** Activities, layouts
e Manifest. **Implementação:** criar a estrutura Java/XML, orientação
horizontal e navegação básica. **Regra:** não implementar ainda o
combate completo. **Resultado:** projeto abre e navega entre as telas.

### Prompt 2 --- Arena

**Objetivo:** criar a área de jogo. **Arquivos:** `GameActivity`,
`GameView` e layout da arena. **Implementação:** criar Canvas ou
SurfaceView, fundo, jogador provisório e controles. **Regra:** manter a
área de jogo separada da interface de menus. **Resultado:** personagem
aparece na arena.

### Prompt 3 --- Jogador

**Objetivo:** implementar movimentação e ações. **Arquivos:** `Player`,
`GameView` e classes relacionadas. **Implementação:** movimento, pulo,
esquiva, espada, escudo, ataque especial e vida. **Regra:** ações devem responder aos
controles de toque. **Resultado:** personagem controlável.

### Prompt 4 --- Combate

**Objetivo:** implementar dano e colisões. **Arquivos:** `Player`,
`Boss`, `Weapon` e `GameView`. **Implementação:** ataques, dano, vida,
derrota e vitória. **Resultado:** batalha funcional.

### Prompt 5 --- Chefes

**Objetivo:** criar o sistema dos 10 chefes. **Arquivos:** `Boss` e
dados dos chefes. **Implementação:** vida, velocidade, ataques, cor e
dificuldade progressiva. **Regra:** o chefe final possui segunda fase.
**Resultado:** sequência de chefes configurável.

### Prompt 6 --- Cura

**Objetivo:** adicionar a habilidade de cura. **Arquivos:** `Player`,
`GameView` e interface. **Implementação:** recuperar 20% da vida máxima
e controlar usos ou recarga. **Resultado:** cura funcional.

### Prompt 7 --- Cartas

**Objetivo:** criar progressão. **Arquivos:** `Card`, `Player`, telas de
recompensa e armazenamento. **Implementação:** cartas de dano e efeitos.
**Resultado:** escolhas modificam a próxima batalha.

### Prompt 8 --- Tutorial

**Objetivo:** criar tutorial interativo. **Arquivos:**
`TutorialActivity` e layout. **Implementação:** ensinar cada controle
por etapas. **Resultado:** tutorial concluível.

### Prompt 9 --- Salvamento

**Objetivo:** salvar progresso. **Arquivos:** `SaveManager` e
`GameProgress`. **Implementação:** utilizar SharedPreferences.
**Resultado:** configurações e progresso básico persistem.

### Prompt 10 --- Finalização

**Objetivo:** preparar apresentação. **Arquivos:** projeto completo.
**Implementação:** corrigir bugs, melhorar UI, testar estados e
organizar código. **Regra:** não adicionar sistemas complexos que
prejudiquem a estabilidade. **Resultado:** APK funcional e apresentável.

## 18. Testes

### Telas

-   Abrir tela inicial.
-   Abrir tutorial.
-   Abrir configurações.
-   Voltar corretamente entre telas.
-   Verificar orientação horizontal.

### Controles

-   Direcional.
-   Ataque.
-   Escudo.
-   Ataque especial.
-   Pulo.
-   Esquiva.
-   Cura.
-   Pausa.

### Combate

-   Jogador recebe dano.
-   Chefe recebe dano.
-   Vida não ultrapassa limites.
-   Chefe é derrotado corretamente.
-   Jogador é derrotado corretamente.
-   Segundo estágio do chefe final inicia corretamente.

### Cartas

-   Cartas aparecem após vitória.
-   Seleção funciona.
-   Efeito escolhido é aplicado.
-   A carta não modifica atributos incorretos.

### Dados

-   Progresso é salvo.
-   Progresso é carregado.
-   Configurações permanecem salvas.
-   Valores padrão são utilizados quando não há dados.

### Situações inválidas

-   Cura sem carga.
-   Cura com vida cheia.
-   Reinício de batalha.
-   Saída durante pausa.
-   Dados de progresso inexistentes.

### Navegação

-   Menu → Tutorial.
-   Tutorial → Jogo.
-   Jogo → Vitória.
-   Vitória → Cartas.
-   Cartas → Próximo chefe.
-   Jogo → Derrota.
-   Derrota → Reinício.
-   Jogo → Pausa.

## 19. Checklist final

-   [ ] Projeto abre no Android Studio.
-   [ ] Aplicativo funciona em orientação horizontal.
-   [ ] Tela inicial funcionando.
-   [ ] Tutorial funcionando.
-   [ ] Personagem controlável.
-   [ ] Espada funcionando.
-   [ ] Arco funcionando.
-   [ ] Pulo funcionando.
-   [ ] Esquiva funcionando.
-   [ ] Cura funcionando.
-   [ ] Chefes funcionando.
-   [ ] Dez chefes configurados.
-   [ ] Segunda fase do chefe final funcionando.
-   [ ] Cartas funcionando.
-   [ ] Efeitos das cartas funcionando.
-   [ ] Progressão de dificuldade funcionando.
-   [ ] Salvamento funcionando.
-   [ ] Efeitos sonoros funcionando.
-   [ ] Interface revisada.
-   [ ] Bugs principais corrigidos.
-   [ ] Testes realizados.
-   [ ] APK de apresentação gerado.

## 20. Possíveis melhorias

Após a versão acadêmica, o projeto poderá evoluir com novos personagens,
armas, chefes, arenas, cartas, efeitos de status, modos de dificuldade e
conquistas.

Também poderá receber um sistema de desafios, ranking local, chefes
opcionais, mais fases para determinados chefes, combinações entre cartas
e maior variedade de animações.

Uma versão futura poderá utilizar uma engine especializada em jogos,
como Godot ou Unity, caso o objetivo seja expandir significativamente o
projeto. Para a disciplina atual, entretanto, a implementação com
Android Studio, Java, XML e Android SDK mantém o projeto alinhado ao
objetivo de aprendizagem.

## Conclusão

Boss Rush apresenta um escopo adequado para demonstrar programação
Android aplicada a um jogo 2D. O projeto combina interface, eventos de
toque, lógica de combate, orientação de tela, armazenamento local,
organização de classes e recursos visuais.

A prioridade deve ser entregar primeiro um ciclo de jogo funcional:
tutorial → chefe → vitória/derrota → carta → próximo chefe. Depois que
esse ciclo estiver estável, os recursos visuais, efeitos e conteúdos
adicionais podem ser incrementados.

O GitHub Copilot deve ser utilizado como ferramenta de apoio à
implementação, enquanto o estudante deve revisar, compreender, testar e
adaptar o código gerado. Isso mantém o projeto adequado ao contexto
acadêmico e facilita a apresentação das decisões tomadas durante o
desenvolvimento.
