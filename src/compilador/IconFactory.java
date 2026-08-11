package compilador;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;

/**
 * Gera, em tempo de execução, os ícones usados nos botões da barra de
 * ferramentas (item 9 do enunciado). Evita depender de arquivos de imagem
 * externos: cada ícone é desenhado com formas geométricas simples.
 */
final class IconFactory {

	private static final int SIZE = 20;
	private static final Color STROKE_COLOR = new Color(60, 60, 60);

	private IconFactory() {
	}

	private interface Painter {
		void paint(Graphics2D g2, int size);
	}

	private static Icon build(Painter painter) {
		return new Icon() {
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.translate(x, y);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(STROKE_COLOR);
				g2.setStroke(new BasicStroke(1.4f));
				painter.paint(g2, SIZE);
				g2.dispose();
			}

			@Override
			public int getIconWidth() {
				return SIZE;
			}

			@Override
			public int getIconHeight() {
				return SIZE;
			}
		};
	}

	/** Ícone de "novo": folha de papel em branco com canto dobrado. */
	static Icon novo() {
		return build((g2, s) -> {
			int w = 12, h = 16, fold = 4;
			int ox = 3, oy = 2;
			g2.drawLine(ox, oy, ox + w - fold, oy);
			g2.drawLine(ox + w - fold, oy, ox + w, oy + fold);
			g2.drawLine(ox + w, oy + fold, ox + w, oy + h);
			g2.drawLine(ox + w, oy + h, ox, oy + h);
			g2.drawLine(ox, oy + h, ox, oy);
			g2.drawLine(ox + w - fold, oy, ox + w - fold, oy + fold);
			g2.drawLine(ox + w - fold, oy + fold, ox + w, oy + fold);
		});
	}

	/** Ícone de "abrir": pasta. */
	static Icon abrir() {
		return build((g2, s) -> {
			g2.drawRect(2, 7, 16, 10);
			g2.drawLine(2, 7, 5, 4);
			g2.drawLine(5, 4, 11, 4);
			g2.drawLine(11, 4, 12, 7);
		});
	}

	/** Ícone de "salvar": disquete. */
	static Icon salvar() {
		return build((g2, s) -> {
			g2.drawRect(3, 2, 14, 16);
			g2.drawRect(6, 2, 8, 5);
			g2.fillRect(12, 2, 2, 5);
			g2.drawRect(6, 11, 8, 6);
		});
	}

	/** Ícone de "copiar": dois retângulos sobrepostos. */
	static Icon copiar() {
		return build((g2, s) -> {
			g2.drawRect(3, 3, 11, 13);
			g2.drawRect(6, 6, 11, 13);
		});
	}

	/** Ícone de "colar": prancheta. */
	static Icon colar() {
		return build((g2, s) -> {
			g2.drawRect(4, 4, 12, 15);
			g2.drawRect(7, 2, 6, 3);
			g2.drawLine(7, 9, 15, 9);
			g2.drawLine(7, 12, 15, 12);
			g2.drawLine(7, 15, 12, 15);
		});
	}

	/** Ícone de "recortar": tesoura estilizada. */
	static Icon recortar() {
		return build((g2, s) -> {
			g2.drawOval(2, 3, 5, 5);
			g2.drawOval(2, 12, 5, 5);
			g2.drawLine(7, 6, 17, 17);
			g2.drawLine(7, 15, 17, 4);
		});
	}

	/** Ícone de "compilar": engrenagem simplificada. */
	static Icon compilar() {
		return build((g2, s) -> {
			g2.drawOval(5, 5, 10, 10);
			g2.drawOval(8, 8, 4, 4);
			for (int i = 0; i < 8; i++) {
				double ang = Math.toRadians(i * 45);
				int x1 = (int) (10 + 5 * Math.cos(ang));
				int y1 = (int) (10 + 5 * Math.sin(ang));
				int x2 = (int) (10 + 8 * Math.cos(ang));
				int y2 = (int) (10 + 8 * Math.sin(ang));
				g2.drawLine(x1, y1, x2, y2);
			}
		});
	}

	/** Ícone de "equipe": três pessoas estilizadas. */
	static Icon equipe() {
		return build((g2, s) -> {
			g2.drawOval(8, 2, 4, 4);
			g2.drawArc(5, 7, 10, 8, 0, 180);
			g2.drawOval(2, 5, 3, 3);
			g2.drawArc(0, 9, 7, 6, 0, 180);
			g2.drawOval(15, 5, 3, 3);
			g2.drawArc(13, 9, 7, 6, 0, 180);
		});
	}
}
