# Interface do Compilador — Trabalho Final, parte 1

Implementação em Java/Swing de todos os itens descritos em `p1-3aN.md`.

## Estrutura

```
src/compilador/
  CompilerInterface.java   -> classe principal (JFrame), contém main()
  NumberedBorder.java      -> borda com numeração de linhas (arquivo fornecido)
  IconFactory.java         -> ícones dos botões, desenhados via Java2D (sem
                               depender de arquivos de imagem externos)
compile.sh                 -> script para compilar e gerar o .jar
```

## ⚠️ Antes de entregar: edite os nomes da equipe

O botão **Equipe [F1]** mostra os nomes definidos na constante `EQUIPE`, no
topo de `CompilerInterface.java`:

```java
private static final String[] EQUIPE = {
        "Nome do Integrante 1",
        "Nome do Integrante 2",
        "Nome do Integrante 3"
};
```

Troque pelos nomes reais da equipe antes de compilar e enviar o trabalho.

## Como compilar e gerar o .jar

Este ambiente de execução não possui JDK/rede disponível para compilar e
testar o projeto automaticamente, então o `.jar` **não** está incluso — seguem
os comandos para gerá-lo na sua máquina (com JDK 8 ou superior instalado):

```bash
chmod +x compile.sh
./compile.sh
java -jar interface-equipeXX.jar
```

Ou manualmente:

```bash
javac -encoding UTF-8 -d bin src/compilador/*.java
jar cfe interface-equipeXX.jar compilador.CompilerInterface -C bin .
java -jar interface-equipeXX.jar
```

Renomeie `interface-equipeXX.jar` para `interface<numero_da_equipe>` conforme
pedido no enunciado antes de compactar e enviar no AVA (junto com a pasta
`src` com o código-fonte).

## Como os itens do enunciado foram atendidos

| Item(ns) | Onde / como |
|---|---|
| 1 – janela 1500x800 fixa, minimizável/fechável | `setSize(1500,800)`, `setResizable(false)`, `EXIT_ON_CLOSE` no construtor de `CompilerInterface` |
| 2 – toolbar 150×n, editor, mensagens, status | `BorderLayout`: toolbar em `WEST` (largura fixa 150), status em `SOUTH` (altura fixa 25, largura = janela toda), editor/mensagens em `CENTER` |
| 3 – divisor ajustável | `JSplitPane` (`buildCenterSplit`) |
| 4 – numeração de linhas | `NumberedBorder` aplicado ao `JTextArea` do editor |
| 5, 7 – scrollbars sempre visíveis | `JScrollPane` com política `..._SCROLLBAR_ALWAYS` |
| 6 – mensagens não editáveis | `messageArea.setEditable(false)` |
| 8 – status mostra pasta/arquivo | `atualizarStatusBar()` |
| 9 – botões (ícone + nome + atalho), na ordem pedida | `buildToolBar()` / `criarBotao()` |
| 10 – novo | `onNovo()` |
| 11 – abrir (mantém estado se cancelado) | `onAbrir()` |
| 12 – salvar / salvar como | `onSalvar()` |
| 13 – copiar/colar/recortar padrão | `onCopiar()/onColar()/onRecortar()` (usam `JTextArea.copy/paste/cut`, com os atalhos padrão do próprio componente) |
| 14 – compilar (mensagem fixa) | `onCompilar()` |
| 15 – equipe (mensagem fixa) | `onEquipe()` |

## Observações de implementação

- Os atalhos **Ctrl+N**, **Ctrl+O**, **Ctrl+S**, **F7** e **F1** funcionam em
  qualquer ponto da janela (vinculados ao `JRootPane`, condição
  `WHEN_IN_FOCUSED_WINDOW`). Já **Ctrl+C/V/X** usam o comportamento padrão do
  `JTextArea` (funcionam quando o editor está com foco), igual a qualquer
  editor de texto convencional.
- Os arquivos abertos/salvos são `.txt` em UTF-8 (compatível com o Notepad
  moderno do Windows). Se sua turma exigir compatibilidade estrita com
  versões antigas do Notepad (ANSI), troque `StandardCharsets.UTF_8` por
  `Charset.forName("Cp1252")` em `onAbrir`/`salvarConteudoNoArquivo`.
- Os ícones dos botões são desenhados em código (`IconFactory`) para não
  depender de arquivos de imagem externos — fique à vontade para substituí-los
  por ícones próprios (basta trocar as chamadas `IconFactory.xxx()` em
  `buildToolBar()` por `new ImageIcon(...)`).
