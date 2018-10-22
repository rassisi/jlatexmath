package org.scilab.forge.jlatexmath;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.model.Atom;
import org.scilab.forge.jlatexmath.ui.Box;
import org.scilab.forge.jlatexmath.ui.CharBox;
import org.scilab.forge.jlatexmath.ui.NewLineBox;

import javafx.geometry.Rectangle2D;

/**
 * @author Ramin
 *
 *         The whole system does not have a unique root object. But this is
 *         needed. To circumvent this problem this class will be instantiated
 *         bevor the whole tree is constructed. All texparser, texEnvironment
 *         and all atoms will hold a reference.
 * 
 */
public class LatexPane {

	/**
	 */
	static public LatexPane INSTANCE = null;

	private JLabel mathLabel;

	private TeXFormula formula;

	private TeXIcon icon;

	private final List<Box> boxes;;

	private final List<Atom> atoms;

	private final String originalParseString;

	private final double width;

	private final Color bgColor;

	private final Color fgColor;

	private final double size;

	private List<TeXParser> parserStack = new ArrayList<TeXParser>();

	public int[] caretTranslation = new int[1000000];

	public LatexPane(String originalParseString, double width, double size, Color bgColor, Color fgColor) {
		this.originalParseString = originalParseString;
		for (int i = 0; i < originalParseString.length(); i++) {
			caretTranslation[i] = i;
		}
		this.width = width;
		this.bgColor = bgColor;
		this.fgColor = fgColor;
		this.size = size;
		this.boxes = new ArrayList<Box>();
		this.atoms = new ArrayList<Atom>();
	}

	public void build() {
		formula = new TeXFormula(originalParseString);
		icon = formula.new TeXIconBuilder().setStyle(TeXConstants.STYLE_DISPLAY).setSize((float) size)
				.setFGColor(fgColor).setBGColor(bgColor)
				.setWidth(TeXConstants.UNIT_PIXEL, (float) width, TeXConstants.ALIGN_LEFT).setIsMaxWidth(false)
				.setInterLineSpacing(TeXConstants.UNIT_PIXEL, 20f).build();
		mathLabel = new JLabel(icon);
		mathLabel.setBackground(Color.white);
	}

	public static LatexPane createInstance(String text, double width, double size, Color bgColor, Color fgColor) {
		LatexPane data = new LatexPane(text, width, size, bgColor, fgColor);
		INSTANCE = data;
		INSTANCE.build();
		return data;
	}

	public static void clearInstance() {
		INSTANCE = null;
	}

	public List<Box> getBoxes() {
		return boxes;
	}

	public int getCaretPosition() {
		int caret = 0;
		for (TeXParser parser : parserStack) {
			caret += parser.getStartPos();
		}

//		caret += caretTranslation[caret];

		return caret;
	}

	public String getOriginalParseString() {
		return originalParseString;
	}

	public JLabel getMathLabel() {
		return mathLabel;
	}

	public TeXFormula getFormula() {
		return formula;
	}

	public TeXIcon getIcon() {
		return icon;
	}

	public double getSize() {
		return size;
	}

	public void addParser(TeXParser parser) {
		if (!parser.isFirstPass()) {
			if (!parserStack.contains(parser)) {
				parserStack.add(parser);
			}
		}
	}

	public void removeParser() {
		if (!parserStack.isEmpty()) {
			parserStack.remove(parserStack.size() - 1);
		}
	}

	public List<Atom> getAtoms() {
		return atoms;
	}

	public List<TeXParser> getParserStack() {
		return parserStack;
	}

	public String getMacroExpandedString() {
		return formula.getParser().getParseString().toString();
	}

	public Box findCaretBox(double x, double y) {
		List<Box> hitBoxes = new ArrayList<Box>();
		for (Box b : getBoxes()) {
			if (b instanceof CharBox || b instanceof NewLineBox) {
				if (b.getScreenBox() != null) {
					if (b.getAtom() != null && b.getScreenBox().contains(x, y)) {
						String c = b.getAtom().getBoxContentDisplay();
						hitBoxes.add(b);
						Rectangle2D r = b.getScreenBox();
						System.out.println("Hit: " + b.getClass().getSimpleName() + " (" + c + ")  at:  "
								+ ((int) r.getMinX()) + "," + ((int) r.getMinY()) + "," + ((int) r.getWidth()) + ","
								+ ((int) r.getHeight()) + "    caret: " + b.getCaretPosition() + "  parse: "
								+ b.getAtom().getParseString());
					}
				}
			}
		}

		if (!hitBoxes.isEmpty()) {
			return hitBoxes.get(hitBoxes.size() - 1);
		}

		return null;
	}
}
