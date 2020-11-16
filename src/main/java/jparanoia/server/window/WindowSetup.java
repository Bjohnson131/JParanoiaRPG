package jparanoia.server.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import jparanoia.names.NameManager;
import jparanoia.server.JPServer;
import jparanoia.server.ServerOptions;
import jparanoia.server.ServerPlayer;
import jparanoia.server.constants.ServerConstants;
import jparanoia.shared.GameRegistrar;
import jparanoia.shared.JParanoia;
import jparanoia.shared.Prefs;

public class WindowSetup {
	/* The main problem of this class is becoming obvious;
	 * The methods which cannot be resolved need to be abstracted from the server to be able to be passed in as a parameter.
	 * */

	/*
	 * Sets the following: title, the icon, close operation, window trap catch,
	 * window frame size, for the server's mainwin
	 */
	public static final void setupServerJframe(JFrame j, JMenuBar menuBar, ServerOptions opts) {
		j.setTitle(ServerConstants.WELCOME_MESSAGE);
		j.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(Thread.currentThread().getContextClassLoader().getResource(ServerConstants.SERVER_ICON_LOC)));
		j.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		j.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent paramAnonymousWindowEvent) {
				// TODO: Abstract this call from this class.
				JPServer.exit();
			}
		});
		j.setSize(770, 540);
		j.setJMenuBar(menuBar);
		if (opts.isPXPGame()) {
			j.setSize(855, j.getHeight());
		}
	}

	public static final void setupServerTextFrame(JTextPane pane) {
		pane.setEditable(false);
		pane.setEnabled(true);
		pane.setBackground(Color.black);
	}

	public static final JTextArea setupInputTextPane(ServerOptions opts, NameManager nc) {
		JTextArea area = new JTextArea(3, 44);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEnabled(false);
		area.setFont(ServerConstants.FONT_NORMAL);
		ServerTextAreaListener listener = new ServerTextAreaListener(area, nc, opts);
		area.addKeyListener(listener);	
		return area;
				
	}	
	
	public static SimpleAttributeSet getCharsheetArrs() {
		SimpleAttributeSet toReturn = new SimpleAttributeSet();
		toReturn.addAttribute(StyleConstants.Bold, true);
		toReturn.addAttribute(StyleConstants.Family, "SansSerif");
		toReturn.addAttribute(StyleConstants.Size, 12);
		return toReturn;
	}
	
	public static SimpleAttributeSet getTextPaneArrs(Prefs p) {
		SimpleAttributeSet toReturn = new SimpleAttributeSet();
		toReturn.addAttribute(StyleConstants.Foreground, Color.white);
		toReturn.addAttribute(StyleConstants.Size, p.getPref(15));
		toReturn.addAttribute(StyleConstants.Family, p.getPref(16));
		return toReturn;		
	}
	
	public static JMenuItem getDescriptionSetMenuItem(String gameDescription, ServerOptions opts) {
		JMenuItem setGameDescriptionMenuItem = new JMenuItem("Set Game Description...");
		setGameDescriptionMenuItem
				.setToolTipText("<HTML>This changes the name of your game<BR> on the JParanoia Game Registry.</HTML>");
		setGameDescriptionMenuItem.addActionListener(paramAnonymousActionEvent -> {
			String str = (String) JOptionPane.showInputDialog(null, "Enter a description for your game:",
					"Set Game Description...", JOptionPane.PLAIN_MESSAGE, null, null, gameDescription);
			if (str != null && !str.equals("") && !str.equals(gameDescription)) {
				gameDescription = str;
				if (opts.isGameRegistered()) {
					GameRegistrar.addGame(gameDescription);
				}
			}
		});
		return setGameDescriptionMenuItem;
	}
	
	public static JCheckBoxMenuItem getRegisterGameMenuItem(Prefs p, ServerOptions so, String gameDesc, String defGameDesc) {
		JCheckBoxMenuItem registerGameMenuItem = new JCheckBoxMenuItem("Register Game");
		registerGameMenuItem.setToolTipText(
				"<HTML>When checked, your server will be made available<BR>to players via the JParanoia Game Registry so they<BR>will not need the IP address of your server.</HTML>");
		registerGameMenuItem.setSelected(p.getPref(31).equals(true));
		registerGameMenuItem.addActionListener(paramAnonymousActionEvent -> {
			if (so.isRegisterGame()) {
				so.setRegisterGame(false);
				if (so.isGameRegistered()) {
					GameRegistrar.removeGame();
				}
			} else {
				so.setRegisterGame(true);
				if (so.isServerRunning()) {
					if (gameDesc.equals(defGameDesc)) {
						setGameDescriptionMenuItem.doClick();
					}
					GameRegistrar.addGame(gameDesc);
				}
			}
		});
		return registerGameMenuItem;
	}
	
	public static JMenu getServerMenu(JMenuItem startServer,JMenuItem stopServer,JMenuItem registerGame, JMenuItem gamedesc, InetAddress localIP) {
		JMenu toReturn = new JMenu(ServerConstants.MENU_LABEL);
		toReturn.add(startServer);
		toReturn.add(stopServer);
		toReturn.addSeparator();
		toReturn.add(registerGame);
		toReturn.add(gamedesc);
		toReturn.addSeparator();
		toReturn.add(new JLabel("  Local IP :   " + localIP.getHostAddress()));
		return toReturn;
	}
	
	/* This is a class which processes the text in the input bar
	 * based on what is inside, and the current options.
	 * */
	public static class ServerTextAreaListener implements KeyListener{
		// Line below may result in a bug. Originally added in to make the listener
		// class happy.
		private int thisKey, previousKey;
		private final NameManager nc;
		private final ServerOptions opts;
		private final JTextArea area;
		
		public ServerTextAreaListener(JTextArea area, NameManager nm, ServerOptions opts) {
			this.area = area;
			this.nc = nm;
			this.opts = opts;
		}
		
		@Override
		public void keyPressed(KeyEvent paramAnonymousKeyEvent) {
			previousKey = thisKey;
			thisKey = paramAnonymousKeyEvent.getKeyCode();
			String str;
			if (thisKey == 10 && this.opts.isSendMainText()) {
				str = area.getText();
				if (str.length() > 0) {
					area.setCaretPosition(str.length());
				}
			}
			if (thisKey == 9) {
				paramAnonymousKeyEvent.consume();
				if (area.getText().length() > 0) {
					str = area.getText();
					if (str.startsWith("'") && str.length() > 1) {
						area.setText("'" + this.nc.completeName(str.substring(1), thisKey == previousKey));
					} else if (!str.startsWith("'")) {
						area.setText(this.nc.completeName(str, thisKey == previousKey));
					}
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent paramAnonymousKeyEvent) {
			int keyCode = paramAnonymousKeyEvent.getKeyCode();
			if (keyCode == KeyEvent.VK_ENTER) {
				if (opts.isSendMainText()) {
					String str = area.getText();
					if (str.length() > 0) {
						sendChat(str.substring(0, str.length() - 1));
					}
				}else {
					opts.setSendMainText(true);
				}
			} else if (keyCode == KeyEvent.VK_ENTER && !opts.isSendMainText()) {
				
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {			
		}
		
	}
	
	
}
