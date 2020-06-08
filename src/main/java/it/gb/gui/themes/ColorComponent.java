package it.gb.gui.themes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.Color;
import java.io.Serializable;

@RequiredArgsConstructor
public class ColorComponent implements Serializable {

	private static final long serialVersionUID = -5239977092938421570L;
	private final Color mainColor;
	private final Color backColor;
	private final Color secColor;
	@Getter
	private final String command;

	public Color[] getColors() {
		return new Color[] { mainColor, backColor, secColor };
	}
}
