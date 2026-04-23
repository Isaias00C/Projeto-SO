# 🏀 Brincadeira de Criança

Integrantes da equipe: 

ISAIAS CASTRO DA SILVEIRA GONÇALVES

JOÃO PEDRO QUEIROZ RODRIGUES

UDSON MARÇAL DE LIMA MAIA

Simulação visual do problema **Brincadeira de Criança**, usando **Threads** e **Semáforos** em Java, desenvolvido para a disciplina de **Sistemas Operacionais**, lecionada pelo professor **Fernando Parente Garcia**.

Crianças (threads) compartilham um cesto de bolas (recurso limitado), alternando entre pegar, brincar e devolver bolas — com sincronização garantida por semáforos.

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-blue?style=flat-square)
![SO](https://img.shields.io/badge/Disciplina-Sistemas%20Operacionais-green?style=flat-square)

---

## 🎮 Demonstração

> Interface gráfica com quadra de basquete animada, personagens se movendo e estados visíveis em tempo real.

| Estado | Animação |
|---|---|
| 🏀 Brincando | Bola quicando ao lado do personagem |
| 🚶 Caminhando | Pernas alternando (animação de passos) |
| 💤 Descansando | Camisa pulsando (apagado ↔ aceso) |
| ⏳ Aguardando bola | Personagem parado no cesto |

---

## 🧠 Conceitos de SO Aplicados

- **Threads** — cada criança é uma thread independente (`extends Thread`)
- **Semáforo `mutex`** — garante exclusão mútua ao acessar o cesto
- **Semáforo `full`** — controla quantas bolas estão disponíveis no cesto
- **Semáforo `empty`** — controla quantos espaços vazios existem no cesto
- **Problema Produtor/Consumidor** — crianças produzem (devolvem) e consomem (pegam) bolas de forma sincronizada

---

## 🗂️ Estrutura do Projeto

```
projeto/
├── MySemaphores/
│   └── SemProducerConsumer.java   # Semáforos compartilhados (mutex, full, empty)
├── MainGUI.java                   # Ponto de entrada da aplicação Swing
├── ControlPanel.java              # Painel de controle (inputs e botões)
├── CourtPanel.java                # Quadra de basquete (renderização gráfica)
└── ChildThreadGUI.java            # Thread com lógica visual (movimento + estados)
```

---

## ▶️ Como Executar

### Pré-requisitos
- Java 11+ instalado
- Terminal na pasta raiz do projeto

### Opção 1 — Compilar e rodar pelo terminal

```bash
# 1. Compilar
javac -d out MySemaphores/SemProducerConsumer.java MyThreads/ChildThread.java ChildThreadGUI.java CourtPanel.java ControlPanel.java MainGUI.java

# 2. Executar
java -cp out MainGUI
```

### Opção 2 — Gerar e rodar o JAR

```bash
# 1. Compilar
javac -d out MySemaphores/SemProducerConsumer.java MyThreads/ChildThread.java ChildThreadGUI.java CourtPanel.java ControlPanel.java MainGUI.java

# 2. Empacotar
jar cfe SimuladorThreads.jar MainGUI -C out .

# 3. Executar
java -jar SimuladorThreads.jar
```

---

## 🖥️ Como Usar a Interface

1. **Digite o tamanho do cesto** no campo superior direito
2. Clique em **Instanciar** para inicializar os semáforos com essa capacidade
3. Preencha os dados de uma criança:
   - **Nome** — identificador da thread
   - **Bola** — se começa com ou sem bola
   - **Tempo de brincadeira** — segundos que a criança brinca
   - **Tempo de descanso** — segundos que descansa após devolver a bola
4. Clique em **Criar** para lançar a thread na quadra
5. Repita o passo 3-4 para adicionar mais crianças simultâneas
6. Clique em **Destruir** para encerrar todas as threads

> ⚠️ O cesto deve ser **instanciado antes** de criar qualquer criança.

---

## 🔄 Fluxo da Thread (Criança)

```
┌─ COM BOLA ──────────────────────────────────────────────────┐
│  brinca() → vai ao cesto → [empty.acquire + mutex] →        │
│  devolve bola → [mutex.release + full.release] →            │
│  volta ao canto → descansa()                                │
└─────────────────────────────────────────────────────────────┘

┌─ SEM BOLA ──────────────────────────────────────────────────┐
│  vai ao cesto → [full.acquire + mutex] → pega bola →        │
│  [mutex.release + empty.release] → volta ao canto →         │
│  brinca() → vai ao cesto → [empty.acquire + mutex] →        │
│  devolve bola → [mutex.release + full.release] →            │
│  volta ao canto → descansa()                                │
└─────────────────────────────────────────────────────────────┘
```

---

## 👨‍💻 Tecnologias

- **Java** — lógica de threads e semáforos
- **Java Swing** — interface gráfica
- **Java2D (Graphics2D)** — renderização da quadra e personagens animados

---

## 📚 Disciplina

Trabalho desenvolvido para a disciplina de **Sistemas Operacionais**  
Universidade: UFC — Universidade Federal do Ceará  
