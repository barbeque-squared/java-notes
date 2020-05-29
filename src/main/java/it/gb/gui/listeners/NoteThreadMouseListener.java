package it.gb.gui.listeners;

import it.gb.gui.ActionCommand;
import it.gb.gui.OneNoteThread;
import it.gb.main.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NoteThreadMouseListener implements ActionListener {

	private final OneNoteThread instance;

	public NoteThreadMouseListener(OneNoteThread instance) {
		this.instance = instance;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ActionCommand command = ActionCommand.valueOf(e.getActionCommand());
		switch (command) {
			case CLOSE:
				Controller.exit(0);
				break;
			case TITLE_OK:
				this.instance.showTitlePanel(false);
				break;
			case TITLE_CHANGE:
				this.instance.showColorsPanel(false);
				this.instance.showTitlePanel(true);
				break;
			case NEW_NOTE:
				Controller.newNote();
				break;
			case REMOVE_NOTE:
				Controller.removeNote(this.instance);
				break;
			case CUSTOMIZE:
				this.instance.showTitlePanel(false);
				this.instance.showColorsPanel(true);
				break;
			case CUSTOMIZE_OK:
				this.instance.showColorsPanel(false);
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + command);
		}

	}
}
