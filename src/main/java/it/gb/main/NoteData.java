package it.gb.main;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;

import it.gb.gui.themes.ColorComponent;
import lombok.Value;

@Value
public class NoteData implements Serializable {

	private static final long serialVersionUID = -41657801163597106L;
	String text;
	String title;
	Point location;
	ColorComponent theme;
	Dimension size;

}
