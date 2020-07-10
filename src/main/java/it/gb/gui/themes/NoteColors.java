package it.gb.gui.themes;

import java.awt.*;
import java.util.List;

public class NoteColors {
	// TODO: remove this suppression once the theme names are either internationalized, or we don't need to care about it anymore
	@SuppressWarnings("SpellCheckingInspection")
	private static final List<ColorComponent> COLORS = List.of(
			// VIOLA
			new ColorComponent(Color.decode("#CC9CDF"), Color.decode("#A46BBD"), Color.decode("#BF89D1"), "purple theme"),
			// VERDE
			new ColorComponent(Color.decode("#5cd65c"), Color.decode("#009933"), Color.decode("#88cc00"), "green theme"),
			// BLU (DEFAULT)
			new ColorComponent(Color.decode("#9BDBF5"), Color.decode("#6BABDA"), Color.decode("#8CC7E7"), "blue theme"),
			// ARANCIO
			new ColorComponent(Color.decode("#FABA62"), Color.decode("#EF6F2F"), Color.decode("#EF9C54"), "orange theme"),
			// BIANCO
			new ColorComponent(Color.decode("#EAEEF0"), Color.decode("#BDC1C2"), Color.decode("#DFE4E3"), "white theme")
	);

	private static NoteColors instance = null;


	public static NoteColors getInstance() {
		if (instance == null)
			instance = new NoteColors();
		return instance;
	}

	public static void initialize() {
		if (instance == null)
			instance = new NoteColors();
	}

	public static ColorComponent searchThemeFromCommand(String command) {
		return COLORS.stream()
				.filter(c -> c.getCommand().equals(command))
				.findFirst().orElse(null);
	}

	public List<ColorComponent> getColors() {
		return COLORS;
	}

	public static ColorComponent getDefaultTheme() {
		return COLORS.get(2);
	}

}
