package org.scilab.forge.jlatexmath;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.ui.Box;

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
			caret = caretTranslation[parser.getStartPos()]; // Spos()];
		}
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
		parserStack.add(parser);
	}

	public void removeParser() {
		if (!parserStack.isEmpty()) {
			parserStack.remove(parserStack.size() - 1);
		}
	}
}
