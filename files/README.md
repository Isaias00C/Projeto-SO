# Producer-Consumer Basketball — JavaFX

## Estrutura de arquivos

```
src/
├── MainFX.java                         ← Entry point (substitui Main.java)
├── module-info.java
├── styles.css
├── MySemaphores/
│   └── SemProducerConsumer.java        ← Versão atualizada com inicializar()
├── MyThreads/
│   └── ChildThread.java                ← Versão atualizada com callbacks de UI
└── ui/
    ├── GameView.java                   ← Monta a tela completa
    ├── ControlPanel.java               ← Painel superior (inputs + botões)
    ├── CourtPane.java                  ← Quadra de basquete desenhada em shapes
    ├── ChildSprite.java                ← Sprite animado da criança
    └── BasketSprite.java               ← Cesto com contador de bolas
```

## Como rodar (IntelliJ IDEA)

1. **Adicione JavaFX ao projeto:**
   - Baixe o SDK JavaFX em https://openjfx.io/
   - Em `File > Project Structure > Libraries`, adicione o `lib/` do JavaFX

2. **Configure a VM options no Run Configuration:**
   ```
   --module-path /caminho/para/javafx/lib --add-modules javafx.controls,javafx.fxml,javafx.graphics
   ```

3. **Compile e rode `MainFX.java`**

## Como rodar com Maven (pom.xml mínimo)

```xml
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>21</version>
    </dependency>
</dependencies>
<build>
    <plugins>
        <plugin>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-maven-plugin</artifactId>
            <version>0.0.8</version>
            <configuration>
                <mainClass>MainFX</mainClass>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## Mudanças no código original

### ChildThread.java
- Adicionado `setOnStateChange(Consumer<String> callback)` — notifica a UI
- `esperarCpuBound` substituído por `esperarSleep` (Thread.sleep) para não travar a UI
- Thread marcada como **daemon** (`child.setDaemon(true)`) — encerra com o app

### SemProducerConsumer.java
- Adicionado `inicializar(int capacidade)` — reseta semáforos ao criar novo cesto
- `empty` agora inicia com a capacidade (semântica correta do produtor-consumidor)

## Fluxo da interface

```
[Insira tamanho do cesto]   → configura semáforos
[Preenche campos da criança] → [Instanciar] → cria ChildThread pendente
[Criar] → lança a thread e adiciona sprite na quadra
[Destruir] → interrompe todas as threads e limpa a quadra
```

## Estados visuais dos sprites

| Estado     | Cor do boneco | Animação        |
|------------|---------------|-----------------|
| IDLE       | Laranja       | Parado          |
| WALKING    | Laranja       | Bounce leve     |
| PLAYING    | Laranja/Âmbar | Bounce alto     |
| WAITING    | Cinza         | Pulsa devagar   |
| AT_BASKET  | Verde         | Bounce médio    |
