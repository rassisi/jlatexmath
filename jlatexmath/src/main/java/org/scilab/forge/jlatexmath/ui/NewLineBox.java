package org.scilab.forge.jlatexmath.ui;

import java.awt.Graphics2D;

import org.scilab.forge.jlatexmath.model.Atom;

/**
 * @author Ramin
 *
 */
public class NewLineBox extends Box {

	public NewLineBox(Atom atom) {
		super(atom);
	}

	@Override
	public void draw(Graphics2D g2, float x, float y) {
	}

	@Override
	public int getLastFontId() {
		return 0;
	}

}
