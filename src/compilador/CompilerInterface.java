package compilador;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Interface gráfica do compilador (Trabalho Final - parte 1).
 *
 * Implementa todos os itens descritos em "p1-3aN.md":
 *  1. Janela com tamanho fixo 1500x800 (minimizável e fechável, não redimensionável).
 *  2. Barra de ferramentas (150 x n), editor, área de mensagens e barra de status.
 *  3. Divisor de tamanho ajustável entre editor e área de mensagens.
 *  4-5. Editor com numeração de linhas e barras de rolagem sempre visíveis.
 *  6-7. Área de mensagens não editável, com barras de rolagem sempre visíveis.
 *  8. Barra de status mostrando pasta/arquivo aberto.
 *  9-15. Barra de ferramentas com botões (novo, abrir, salvar, copiar, colar,
 *        recortar, compilar, equipe), ícone + nome + atalho, e as respectivas ações.
 */
public class CompilerInterface extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final String[] EQUIPE = {
			"Felipe Behling",
			"Gustavo Henrique Probst"
	};

	private static final Dimension BUTTON_SIZE = new Dimension(140, 56);
	private static final int TOOLBAR_WIDTH = 150;
	private static final int STATUSBAR_HEIGHT = 25;

	private JTextArea editorArea;
	private JTextArea messageArea;
	private JLabel statusLabel;

	/** Arquivo atualmente aberto/salvo. null enquanto o arquivo for "novo" (nunca salvo). */
	private File currentFile = null;

	public CompilerInterface() {
		super("Compilador");

		initComponents();
		initShortcuts();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1500, 800);
		setResizable(false); // item 1: não redimensionável, mas minimizável/fechável
		setLocationRelativeTo(null);
	}

	// ---------------------------------------------------------------
	// Montagem da interface
	// ---------------------------------------------------------------

	private void initComponents() {
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(buildToolBar(), BorderLayout.WEST);
		getContentPane().add(buildCenterSplit(), BorderLayout.CENTER);
		getContentPane().add(buildStatusBar(), BorderLayout.SOUTH);
	}

	/** Item 2/9: barra de ferramentas vertical, largura fixa 150, altura = espaço disponível. */
	private JToolBar buildToolBar() {
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		toolBar.setPreferredSize(new Dimension(TOOLBAR_WIDTH, 0));
		toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.Y_AXIS));
		toolBar.setBorder(new EmptyBorder(8, 5, 8, 5));

		// manipulação de arquivos
		toolBar.add(criarBotao("Novo", "Ctrl+N", IconFactory.novo(), e -> onNovo()));
		toolBar.add(Box.createVerticalStrut(4));
		toolBar.add(criarBotao("Abrir", "Ctrl+O", IconFactory.abrir(), e -> onAbrir()));
		toolBar.add(Box.createVerticalStrut(4));
		toolBar.add(criarBotao("Salvar", "Ctrl+S", IconFactory.salvar(), e -> onSalvar()));

		toolBar.add(Box.createVerticalStrut(12));
		toolBar.addSeparator();
		toolBar.add(Box.createVerticalStrut(12));

		// edição de texto
		toolBar.add(criarBotao("Copiar", "Ctrl+C", IconFactory.copiar(), e -> onCopiar()));
		toolBar.add(Box.createVerticalStrut(4));
		toolBar.add(criarBotao("Colar", "Ctrl+V", IconFactory.colar(), e -> onColar()));
		toolBar.add(Box.createVerticalStrut(4));
		toolBar.add(criarBotao("Recortar", "Ctrl+X", IconFactory.recortar(), e -> onRecortar()));

		toolBar.add(Box.createVerticalStrut(12));
		toolBar.addSeparator();
		toolBar.add(Box.createVerticalStrut(12));

		// compilação
		toolBar.add(criarBotao("Compilar", "F7", IconFactory.compilar(), e -> onCompilar()));

		toolBar.add(Box.createVerticalStrut(12));
		toolBar.addSeparator();
		toolBar.add(Box.createVerticalStrut(12));

		// informações sobre o compilador
		toolBar.add(criarBotao("Equipe", "F1", IconFactory.equipe(), e -> onEquipe()));

		toolBar.add(Box.createVerticalGlue());
		return toolBar;
	}

	/** Cria um botão padronizado: mesmo tamanho, ícone, nome completo e atalho (item 9). */
	private JButton criarBotao(String nome, String atalho, Icon icone, Consumer<ActionEvent> acao) {
		JButton botao = new JButton("<html><div style='text-align:center;'>" + nome
				+ "<br><small>[" + atalho + "]</small></div></html>", icone);
		botao.setHorizontalTextPosition(JButton.CENTER);
		botao.setVerticalTextPosition(JButton.BOTTOM);
		botao.setAlignmentX(JButton.CENTER_ALIGNMENT);
		botao.setPreferredSize(BUTTON_SIZE);
		botao.setMinimumSize(BUTTON_SIZE);
		botao.setMaximumSize(BUTTON_SIZE);
		botao.setFocusPainted(false);
		botao.setToolTipText(nome + " (" + atalho + ")");
		botao.addActionListener(acao::accept);
		return botao;
	}

	/** Itens 3, 4, 5, 6, 7: editor e área de mensagens dentro de um JSplitPane vertical. */
	private JSplitPane buildCenterSplit() {
		editorArea = new JTextArea();
		editorArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		editorArea.setLineWrap(false);
		editorArea.setBorder(new NumberedBorder()); // item 4: numeração de linhas

		JScrollPane editorScroll = new JScrollPane(editorArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); // item 5

		messageArea = new JTextArea();
		messageArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
		messageArea.setEditable(false); // item 6
		messageArea.setLineWrap(false);

		JScrollPane messageScroll = new JScrollPane(messageArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); // item 7

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorScroll, messageScroll);
		splitPane.setResizeWeight(0.7);
		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true); // item 3: divisor ajustável
		return splitPane;
	}

	/** Item 8: barra de status (m x 25) mostrando pasta e nome do arquivo aberto. */
	private JPanel buildStatusBar() {
		JPanel painel = new JPanel(new BorderLayout());
		painel.setPreferredSize(new Dimension(10, STATUSBAR_HEIGHT));
		painel.setBorder(BorderFactory.createLoweredBevelBorder());

		statusLabel = new JLabel(" ");
		statusLabel.setBorder(new EmptyBorder(2, 8, 2, 8));
		painel.add(statusLabel, BorderLayout.WEST);
		return painel;
	}

	/** Atalhos de teclado (Ctrl+N, Ctrl+O, Ctrl+S, F7, F1) válidos em toda a janela. */
	private void initShortcuts() {
		JRootPane root = getRootPane();
		bindShortcut(root, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "novo", e -> onNovo());
		bindShortcut(root, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "abrir", e -> onAbrir());
		bindShortcut(root, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "salvar", e -> onSalvar());
		bindShortcut(root, KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "compilar", e -> onCompilar());
		bindShortcut(root, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "equipe", e -> onEquipe());
		// Ctrl+C / Ctrl+V / Ctrl+X seguem o comportamento padrão do JTextArea quando o
		// editor está com foco (item 13: mesmo comportamento de editores convencionais).
	}

	private void bindShortcut(JRootPane root, KeyStroke tecla, String nome, Consumer<ActionEvent> acao) {
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(tecla, nome);
		root.getActionMap().put(nome, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				acao.accept(e);
			}
		});
	}

	// ---------------------------------------------------------------
	// Ações dos botões (itens 10 a 15)
	// ---------------------------------------------------------------

	/** Item 10: novo -> limpa editor, mensagens e barra de status. */
	private void onNovo() {
		editorArea.setText("");
		messageArea.setText("");
		currentFile = null;
		statusLabel.setText(" ");
		editorArea.requestFocusInWindow();
	}

	/** Item 11: abrir -> seleciona e carrega arquivo .txt; se cancelado, mantém o estado atual. */
	private void onAbrir() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Abrir arquivo");
		chooser.setFileFilter(new FileNameExtensionFilter("Arquivos de texto (*.txt)", "txt"));

		int resultado = chooser.showOpenDialog(this);
		if (resultado != JFileChooser.APPROVE_OPTION) {
			return; // cancelado: estado da interface é mantido
		}

		File arquivo = chooser.getSelectedFile();
		try {
			String conteudo = new String(Files.readAllBytes(arquivo.toPath()), StandardCharsets.UTF_8);
			editorArea.setText(conteudo);
			editorArea.setCaretPosition(0);
			messageArea.setText("");
			currentFile = arquivo;
			atualizarStatusBar();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this,
					"Não foi possível abrir o arquivo:\n" + ex.getMessage(),
					"Erro ao abrir", JOptionPane.ERROR_MESSAGE);
		}
	}

	/** Item 12: salvar -> "salvar como" se novo, ou sobrescreve o arquivo atual. */
	private void onSalvar() {
		if (currentFile == null) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Salvar arquivo");
			chooser.setFileFilter(new FileNameExtensionFilter("Arquivos de texto (*.txt)", "txt"));

			int resultado = chooser.showSaveDialog(this);
			if (resultado != JFileChooser.APPROVE_OPTION) {
				return; // cancelado: nada muda
			}

			File arquivo = chooser.getSelectedFile();
			if (!arquivo.getName().toLowerCase().endsWith(".txt")) {
				arquivo = new File(arquivo.getParentFile(), arquivo.getName() + ".txt");
			}

			if (salvarConteudoNoArquivo(arquivo)) {
				currentFile = arquivo;
				messageArea.setText("");
				atualizarStatusBar();
			}
		} else {
			if (salvarConteudoNoArquivo(currentFile)) {
				messageArea.setText("");
				// barra de status é mantida (já reflete a pasta/arquivo corretos)
			}
		}
	}

	private boolean salvarConteudoNoArquivo(File arquivo) {
		try {
			Files.write(arquivo.toPath(), editorArea.getText().getBytes(StandardCharsets.UTF_8));
			return true;
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this,
					"Não foi possível salvar o arquivo:\n" + ex.getMessage(),
					"Erro ao salvar", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private void atualizarStatusBar() {
		if (currentFile == null) {
			statusLabel.setText(" ");
			return;
		}
		String pasta = currentFile.getParent() != null ? currentFile.getParent() : "";
		statusLabel.setText(pasta + File.separator + currentFile.getName());
	}

	/** Item 13: copiar/colar/recortar com o mesmo comportamento de editores convencionais. */
	private void onCopiar() {
		editorArea.copy();
	}

	private void onColar() {
		editorArea.paste();
	}

	private void onRecortar() {
		editorArea.cut();
	}

	/** Item 14: compilar -> mensagem fixa (substitui qualquer mensagem anterior). */
	private void onCompilar() {
		messageArea.setText("compilação de programas ainda não foi implementada");
	}

	/** Item 15: equipe -> nomes da equipe (substitui qualquer mensagem anterior). */
	private void onEquipe() {
		StringBuilder sb = new StringBuilder("Equipe de desenvolvimento do compilador:\n");
		for (String nome : EQUIPE) {
			sb.append("- ").append(nome).append('\n');
		}
		messageArea.setText(sb.toString());
	}

	// ---------------------------------------------------------------
	// main
	// ---------------------------------------------------------------

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ignored) {
				// segue com o look and feel padrão caso o do sistema não esteja disponível
			}
			new CompilerInterface().setVisible(true);
		});
	}
}
