/* Atom.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
 * Copyright (C) 2009 DENIZET Calixte
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * Linking this library statically or dynamically with other modules
 * is making a combined work based on this library. Thus, the terms
 * and conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce
 * an executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under terms
 * of your choice, provided that you also meet, for each linked independent
 * module, the terms and conditions of the license of that module.
 * An independent module is a module which is not derived from or based
 * on this library. If you modify this library, you may extend this exception
 * to your version of the library, but you are not obliged to do so.
 * If you do not wish to do so, delete this exception statement from your
 * version.
 *
 */

/* Modified by Calixte Denizet */

package org.scilab.forge.jlatexmath.model;

import java.util.ArrayList;
import java.util.List;

import org.scilab.forge.jlatexmath.LatexPane;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXEnvironment;
import org.scilab.forge.jlatexmath.ui.Box;
import org.scilab.forge.jlatexmath.ui.CharBox;

import javafx.geometry.Rectangle2D;

/**
 * An abstract superclass for all logical mathematical constructions that can be
 * a part of a TeXFormula. All subclasses must implement the abstract
 * {@link #createBox(TeXEnvironment)} method that transforms this logical unit
 * into a concrete box (that can be painted). They also must define their type,
 * used for determining what glue to use between adjacent atoms in a "row
 * construction". That can be one single type by asigning one of the type
 * constants to the {@link #type} field. But they can also be defined as having
 * two types: a "left type" and a "right type". This can be done by implementing
 * the methods {@link #getLeftType()} and {@link #getRightType()}. The left type
 * will then be used for determining the glue between this atom and the previous
 * one (in a row, if any) and the right type for the glue between this atom and
 * the following one (in a row, if any).
 *
 * @author Kurt Vermeulen
 */
public abstract class Atom implements Cloneable {

	/**
	 * The type of the atom (default value: ordinary atom)
	 */
	public int type = TeXConstants.TYPE_ORDINARY;

	public int type_limits = TeXConstants.SCRIPT_NOLIMITS;

	public int alignment = -1;

	public String parsString;

	// Assisi

	private int caretPosition = -1;

	private Box box;

	private LatexPane latexPane;

	public Atom() {
		latexPane = LatexPane.INSTANCE;
	}

	/**
	 * Convert this atom into a {@link Box}, using properties set by "parent" atoms,
	 * like the TeX style, the last used font, color settings, ...
	 *
	 * @param env the current environment settings
	 * @return the resulting box.
	 */
	public Box createBox(TeXEnvironment env) {
		box = doCreateBox(env);
		box.setAtom(this);
		return box;
	}

	public static Box findCaretBox(LatexPane latexPane, double x, double y) {
		List<Box> hitBoxes = new ArrayList<Box>();
		for (Box b : latexPane.getBoxes()) {
			if (b instanceof CharBox) {
				if (b.getScreenBox() != null) {
					if (b.getScreenBox().contains(x, y)) {
						String c = getBoxContentDisplay(b.getAtom());
						hitBoxes.add(b);
						Rectangle2D r = b.getScreenBox();
						System.out.println("Hit: " + b.getClass().getSimpleName() + " (" + c + ")  at:  "
								+ ((int) r.getMinX()) + "," + ((int) r.getMinY()) + "," + ((int) r.getWidth()) + ","
								+ ((int) r.getHeight()) + "    caret: " + b.getCaretPosition());
					}
				}
			}
		}

		if (!hitBoxes.isEmpty()) {
			return hitBoxes.get(hitBoxes.size() - 1);
		}

		return null;
	}

	private static String getBoxContentDisplay(Atom a) {
		String c = " ";
		if (a instanceof CharAtom) {
			char cc = ((CharAtom) a).getC();
			c = String.valueOf(cc);
		} else if (a instanceof SymbolAtom) {
			SymbolAtom s = (SymbolAtom) a;
			c = "Symbol: " + s.getName() + ", " + s.getUnicode();
		} else if (a instanceof TextStyleAtom) {
			TextStyleAtom s = (TextStyleAtom) a;
			c = "TextStyle: " + getBoxContentDisplay(s.getAtom());
		} else if (a instanceof ScriptsAtom) {
			ScriptsAtom s = (ScriptsAtom) a;
			Atom base = s.getBase();
			Atom subScript = s.getSubscript();
			Atom superScript = s.getSuperscript();
			c = "Script: " + getBoxContentDisplay(base) + getBoxContentDisplay(subScript)
					+ getBoxContentDisplay(superScript);
		} else {
			c = "" + a;
		}
		return c;
	}

	public static void clear(TeXEnvironment texEnvironment) {
//		texEnvironment.boxes.clear();
	}

	protected abstract Box doCreateBox(TeXEnvironment env);

	/**
	 * Get the type of the leftermost child atom. Most atoms have no child atoms, so
	 * the "left type" and the "right type" are the same: the atom's type. This also
	 * is the default implementation. But Some atoms are composed of child atoms put
	 * one after another in a horizontal row. These atoms must override this method.
	 *
	 * @return the type of the leftermost child atom
	 */
	public int getLeftType() {
		return type;
	}

	/**
	 * Get the type of the rightermost child atom. Most atoms have no child atoms,
	 * so the "left type" and the "right type" are the same: the atom's type. This
	 * also is the default implementation. But Some atoms are composed of child
	 * atoms put one after another in a horizontal row. These atoms must override
	 * this method.
	 *
	 * @return the type of the rightermost child atom
	 */
	public int getRightType() {
		return type;
	}

	public Atom clone() {
		try {
			return (Atom) super.clone();
		} catch (Exception e) {
			return null;
		}
	}

	public int getCaretPosition() {
		return caretPosition;
	}

	public void setCaretPosition(int caretPosition) {
		if (caretPosition > latexPane.getCaretPosition()) {
			latexPane.setCaretPosition(caretPosition);
			this.caretPosition = caretPosition;
		} else {
			this.caretPosition = latexPane.getCaretPosition();
		}
	}

	@Override
	public String toString() {
		return parsString;
	}

	public Box getBox() {
		return box;
	}

}
