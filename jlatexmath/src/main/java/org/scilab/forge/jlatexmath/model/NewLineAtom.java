package org.scilab.forge.jlatexmath.model;

import org.scilab.forge.jlatexmath.Char;
import org.scilab.forge.jlatexmath.TeXEnvironment;
import org.scilab.forge.jlatexmath.TeXFont;
import org.scilab.forge.jlatexmath.ui.Box;
import org.scilab.forge.jlatexmath.ui.NewLineBox;

/**
 * @author Ramin
 *
 */
public class NewLineAtom extends Atom {

	public NewLineAtom() {
		super();
	}

	@Override
	public Box doCreateBox(TeXEnvironment env) {
		TeXFont tf = env.getTeXFont();
		int style = env.getStyle();
		String textStyle = "mathnormal";
		Char c = tf.getChar((char) 186, textStyle, style);
		Box box = new NewLineBox(this, c);
		return box;
	}

}
