package it.gb.gui;

import it.gb.gui.listeners.ColorSelectionListener;
import it.gb.gui.listeners.NoteThreadMouseListener;
import it.gb.gui.listeners.TitleChangeListener;
import it.gb.gui.listeners.WindowListener;
import it.gb.gui.themes.ColorComponent;
import it.gb.gui.themes.NoteColors;
import it.gb.lib.ComponentMover;
import it.gb.lib.ComponentResizer;
import it.gb.main.Main;
import it.gb.main.NoteData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class OneNoteThread extends Thread {

	private static final int WIDTH = 220;
	private static final int HEIGHT = 220;
	private static final int TOOLBAR_HEIGHT = 40;
	private static final int BUTTON_WIDTH = 25;
	private static final int BUTTON_HEIGHT = 25;
	private static final Dimension BUTTON_SIZE = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);

	private String title;
	private final String body;

	private ColorComponent theme;

	private final JDialog frame;

	NoteThreadMouseListener mouseListener = new NoteThreadMouseListener(this);

	private final JPanel titlePanel = new JPanel();
	private final JPanel colorsPanel = new JPanel(new FlowLayout());
	private final JPanel northPanel = new JPanel(new BorderLayout());
	private final JPanel buttonsMenu = new JPanel(new FlowLayout());
	private final JPanel centerPanel = new JPanel(new FlowLayout());

	private final JButton closeBtn = iconButton("x", "t_close", ActionCommand.CLOSE, mouseListener);
	private final JLabel titleLabel = new JLabel();

	private final JTextField titleField = new JTextField();
	private final JButton customizeBtn = iconButton("customize", "t_customize", ActionCommand.CUSTOMIZE, mouseListener);
	private final JButton titleBtn = iconButton("ok", ActionCommand.TITLE_OK, mouseListener);
	private final JButton addBtn = iconButton("plus", "t_add", ActionCommand.NEW_NOTE, mouseListener);
	private final JButton titleChangeBtn = iconButton("change", "t_title", ActionCommand.TITLE_CHANGE, mouseListener);
	private final JButton removeBtn = iconButton("minus", "t_delete", ActionCommand.REMOVE_NOTE, mouseListener);
	private final JTextPane noteArea = new JTextPane();
	private final JScrollPane noteAreaContainer = new JScrollPane(noteArea);

	// empty Note
	public OneNoteThread(JFrame parent) {
		title = Main.rsBundle.getString("s_default_title");
		body = "";

		frame = new JDialog(parent);
		frame.setLocation(new Point(10, 10));

		setNewTheme(NoteColors.getDefaultTheme());

		showTitlePanel(true);
	}

	// already-filled Note
	public OneNoteThread(JFrame parent, NoteData data) {
		title = data.getTitle();
		body = data.getText();

		frame = new JDialog(parent);
		frame.setLocation(data.getLocation());
		frame.setSize(data.getSize());

		setNewTheme(data.getTheme());

		// TODO: set focus on the note text
		noteArea.setFocusable(true);

		showTitlePanel(false);
	}

	@Override
	public void run() {
		SwingUtilities.invokeLater(this::buildGUI);
	}

	public void buildGUI() {
		frame.setTitle(title);
		frame.setUndecorated(true);
		frame.addWindowListener(new WindowListener());
		frame.getRootPane().setBorder(new LineBorder(Color.WHITE));

		titleLabel.setText(title);
		titleLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
		titleLabel.setPreferredSize(new Dimension(WIDTH - 5*BUTTON_WIDTH, TOOLBAR_HEIGHT - 10));
		noteArea.setText(body);
		titleField.setText(title);
		noteArea.setText(body);

		// START OF TOP PANEL
		titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));

		// TOP BUTTON PANEL
		buttonsMenu.add(customizeBtn);
		buttonsMenu.add(titleChangeBtn);
		buttonsMenu.add(removeBtn);
		buttonsMenu.add(addBtn);
		buttonsMenu.add(closeBtn);

		// GUI FUNCTIONS
		// dragging
		ComponentMover cm = new ComponentMover(JDialog.class, northPanel);
		cm.setChangeCursor(false);
		// resizing
		ComponentResizer cr = new ComponentResizer();
		cr.registerComponent(frame);
		cr.setSnapSize(new Dimension(10, 10));
		cr.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		cr.setMaximumSize(new Dimension(WIDTH, 1000));

		// add them to the top panel
		northPanel.add(titleLabel, BorderLayout.WEST);
		northPanel.add(buttonsMenu, BorderLayout.EAST);
		// END OF TOP PANEL

		// START OF CENTRAL PANEL
		// Color panel
		colorsPanel.setPreferredSize(new Dimension(WIDTH, TOOLBAR_HEIGHT));
		colorsPanel.setVisible(false);

		// get themes
		NoteColors colorLib = NoteColors.getInstance();

		ColorSelectionListener themeListener = new ColorSelectionListener(this);

		// loop over themes
		for (ColorComponent item : colorLib.getColors()) {
			JButton tempBtn = new JButton();
			tempBtn.setPreferredSize(BUTTON_SIZE);
			tempBtn.setBorder(new LineBorder(Color.BLACK));
			tempBtn.setBackground(item.getColors()[0]);
			tempBtn.setContentAreaFilled(false);
			tempBtn.setOpaque(true);

			tempBtn.addActionListener(themeListener);
			tempBtn.setActionCommand(item.getCommand());

			colorsPanel.add(tempBtn);
		}

		// confirm button
		JButton colorsOkBtn = iconButton("ok", ActionCommand.CUSTOMIZE_OK, mouseListener);
		colorsPanel.add(colorsOkBtn);

		// Change Title panel
		titlePanel.setPreferredSize(new Dimension(WIDTH, TOOLBAR_HEIGHT));

		// text field
		titleField.setPreferredSize(new Dimension(WIDTH - 80, 25));
		titleField.addKeyListener(new TitleChangeListener(this));

		// add them to the Change Title panel
		titlePanel.add(titleField);
		titlePanel.add(titleBtn);

		// text field for a note
		noteArea.setFont(new Font("Segoe Print", Font.PLAIN, 20));
		// TODO: this misses the bottom of the scrollbar if you open menu's on the minimal size
		//       it should really just use "whatever space is left" instead of being coded this way
		noteAreaContainer.setPreferredSize(new Dimension(WIDTH - 10, HEIGHT - TOOLBAR_HEIGHT));
		noteAreaContainer.setBorder(new EmptyBorder(0, 0, 0, 0));

		// add panels to the central panel
		centerPanel.add(titlePanel);
		centerPanel.add(colorsPanel);
		centerPanel.add(noteAreaContainer);
		// END OF CENTRAL PANEL

		// GUI things
		frame.setResizable(true);
		frame.getContentPane().add(northPanel, BorderLayout.NORTH);
		frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
		frame.setSize(WIDTH, HEIGHT);
		frame.setVisible(true);
	}

	public Component getFrame() {
		return frame;
	}

	public NoteData getData() {
		return new NoteData(noteArea.getText(), title, getLocation(), theme, frame.getSize());
	}

	public Point getLocation() {
		return frame.getLocationOnScreen();
	}

	public void setLocation(Point point) {
		if (point == null)
			frame.setLocationRelativeTo(null);
		else
			frame.setLocation(point);
	}

	public void setNewTheme(ColorComponent colorComp) {
		Color[] colors = colorComp.getColors();
		theme = colorComp;
		Color mainColor = colors[0];
		Color backColor = colors[1];
		Color secColor = colors[2];
		frame.setBackground(mainColor);
		northPanel.setBackground(backColor);
		buttonsMenu.setBackground(backColor);
		centerPanel.setBackground(mainColor);
		colorsPanel.setBackground(secColor);
		titlePanel.setBackground(secColor);
		noteArea.setBackground(mainColor);
		noteAreaContainer.setBackground(mainColor);
	}

	public void setNewTitle() {
		title = titleField.getText();
		frame.setTitle(title);
		titleLabel.setText(title);
	}

	public boolean isWithText() {
		return !noteArea.getText().equals("");
	}

	public void dispose() {
		frame.dispose();
	}

	public void showTitlePanel(boolean b) {
		if (b) {
			titleChangeBtn.setVisible(false);
			titlePanel.setVisible(true);
		} else {
			titlePanel.setVisible(false);
			titleChangeBtn.setVisible(true);
		}
	}

	public void showColorsPanel(boolean b) {
		if (b) {
			customizeBtn.setVisible(false);
			colorsPanel.setVisible(true);
		} else {
			colorsPanel.setVisible(false);
			customizeBtn.setVisible(true);
		}
	}

	public void confirmTitle() {
		titleBtn.doClick();
	}


	private JButton iconButton(String name, ActionCommand actionCommand, ActionListener actionListener) {
		JButton button = new JButton(new ImageIcon(getClass().getResource("/images/" + name + ".png")));
		button.setPreferredSize(BUTTON_SIZE);
		button.setBackground(Color.WHITE);
		button.setContentAreaFilled(false);
		button.setOpaque(true);
		button.setFocusable(false);
		// TODO: use setAction instead
		button.setActionCommand(actionCommand.name());
		button.addActionListener(actionListener);
		return button;
	}

	private JButton iconButton(String name, String tooltip, ActionCommand actionCommand, ActionListener actionListener) {
		JButton button = iconButton(name, actionCommand, actionListener);
		button.setToolTipText(Main.rsBundle.getString(tooltip));
		return button;
	}
}
