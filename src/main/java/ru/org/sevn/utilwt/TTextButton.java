package ru.org.sevn.utilwt;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JButton;

public class TTextButton extends JButton {
	public TTextButton(String ttext, Icon i) {
		this(null, i, ttext);
	}
	public TTextButton(String text, Icon i, String ttext) {
		super(i);
		setBackground(Color.WHITE);
		setMargin(new Insets(2, 3, 2, 3));
		if (ttext != null) {
			setToolTipText(ttext);
		}
		if (text != null) {
			setText(text);
		}
	}
}