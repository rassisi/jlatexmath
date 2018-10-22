package org.scilab.forge.jlatexmath.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.scilab.forge.jlatexmath.Char;
import org.scilab.forge.jlatexmath.CharFont;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.model.Atom;

/**
 * @author Ramin
 *
 */
public class NewLineBox extends Box {

	private double size;

	private CharFont c;

	private final char[] arr = new char[1];

	public NewLineBox(Atom atom, Char cc) {
		super(atom);
		size = 2; // LatexPane.INSTANCE.getSize();
		setHeight((float) 1);
		setWidth(1);
		foreground = Color.black;
		background = Color.red;
		c = cc.getCharFont();
	}

	@Override
	public void draw(Graphics2D g2, float x, float y) {
		drawDebug(g2, x, y);
		AffineTransform at = g2.getTransform();
		g2.translate(x, y);
		Font font = new Font("Arial", Font.PLAIN, 48); // FontInfo.getFont(c.fontId);

		if (Math.abs(size - TeXFormula.FONT_SCALE_FACTOR) > TeXFormula.PREC) {
			g2.scale(size / TeXFormula.FONT_SCALE_FACTOR, size / TeXFormula.FONT_SCALE_FACTOR);
		}

		if (g2.getFont() != font) {
			g2.setFont(font);
		}

		String s = "¶";
//		s.getChars(0, 1, arr, 0);
//		g2.drawChars(arr, 0, 1, 5, 0);

		g2.drawString(s, 10, -8);

		g2.setTransform(at);
	}

	@Override
	public int getLastFontId() {
		return 0;
	}

	@Override
	protected void drawDebug(Graphics2D g2, float x, float y) {
		DEBUG = false;
		markForDEBUG = null;
		super.drawDebug(g2, x, y);
		DEBUG = false;
	}
}
