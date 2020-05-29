package it.gb.main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashSet;

import javax.swing.JOptionPane;

import it.gb.gui.OneNoteThread;
import lombok.extern.java.Log;

@Log
public class SaverThread extends Thread {
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			saveAll();
		}
	}

	public static void saveAll() {
		log.info("Saving...");
		HashSet<NoteData> notes = new HashSet<>();

		for (OneNoteThread item : Controller.getThreads()) {
			NoteData temp = item.getData();
			if (!temp.getText().equals(""))
				notes.add(item.getData());
		}

		if (!Main.noteFile.exists()) {
			try {
				Main.noteFile.getCanonicalFile().getParentFile().mkdirs();
				Main.noteFile.createNewFile();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error while creating output file", "Critical error",
						JOptionPane.ERROR_MESSAGE);
				log.severe(e.toString());
				System.exit(-1);
			}
		}

		try (OutputStream outputStream = new FileOutputStream(Main.noteFile)) {
			ObjectOutputStream byteStreamToSave;

			byteStreamToSave = new ObjectOutputStream(outputStream);

			byteStreamToSave.writeObject(notes);
			byteStreamToSave.close();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error while saving data on file", "Critical error",
					JOptionPane.ERROR_MESSAGE);
			log.severe(e.toString());
			System.exit(-1);
		}

	}

}
