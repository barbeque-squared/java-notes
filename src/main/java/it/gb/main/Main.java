package it.gb.main;

import java.io.*;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import it.gb.gui.listeners.WindowListener;
import it.gb.gui.themes.NoteColors;
import lombok.extern.java.Log;

@Log
public class Main {

	public static ResourceBundle rsBundle;

	private static ServerSocket socketOffline;
	private static JFrame mainInvisibleFrame;
	public static File noteFile;

	public static void main(String[] args) {

		// TODO: make this a command line parameter instead, but just use the current directory for now
//		notePath = System.getenv("APPDATA") + "\\JNotes\\notes.jnotes";
		String notePath = "notes.jnotes";
		noteFile = new File(notePath);

		Locale locale = Locale.getDefault();

		try {
			rsBundle = ResourceBundle.getBundle("it.gb.lang.Res", locale);
		} catch (MissingResourceException e) {
			log.warning("MissingResourceException: setting English language as default");
			locale = new Locale("en", "US");
			rsBundle = ResourceBundle.getBundle("it.gb.lang.Res", locale);
		} catch (Exception e) {
			log.severe(e.toString());
		}

		// TODO: this can probably be removed if you can configure the notes file from the command line
		try {
			socketOffline = new ServerSocket(8765);
		} catch (IOException e) {
			log.info("Another instance is probably running...");
			System.exit(0);
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.severe(e.toString());
		}

		SwingUtilities.invokeLater(() -> {
			NoteColors.initialize();
			buildGUI();
			findNotes();
			mainInvisibleFrame.setVisible(true);
		});

		new SaverThread().start();
	}

	private static void buildGUI() {
		mainInvisibleFrame = new JFrame("JNotes");
		mainInvisibleFrame.addWindowListener(new WindowListener());
		mainInvisibleFrame.setIconImage(new ImageIcon(Main.class.getResource("/images/icon.png")).getImage());
		mainInvisibleFrame.setSize(0, 0);
		mainInvisibleFrame.setUndecorated(true);
	}

	public static JFrame getFrame() {
		return mainInvisibleFrame;
	}

	private static void findNotes() {
		HashSet<NoteData> notes = new HashSet<>();

		if (noteFile.exists()) {
			try (InputStream inputStream = new FileInputStream(noteFile)) {
				ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
				notes = (HashSet<NoteData>) objectInputStream.readObject();
				objectInputStream.close();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Error while initializing data from file", "Critical error",
						JOptionPane.ERROR_MESSAGE);
				log.severe(e.toString());
				System.exit(-1);
			}
		} else {
			notes = new HashSet<>();
		}

		for (NoteData item : notes) {
			Controller.newNote(item);
		}

		if (notes.isEmpty()) {
			Controller.newNote();
		}
	}

	public static void saveAndClose(int code) {
		SaverThread.saveAll();
		try {
			socketOffline.close();
		} catch (Exception e) {
			log.severe(e.toString());
		}
		System.exit(code);
	}
}
