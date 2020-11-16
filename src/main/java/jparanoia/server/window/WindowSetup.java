package jparanoia.server.window;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import jparanoia.names.NameManager;
import jparanoia.server.JPServer;
import jparanoia.server.ServerOptions;
import jparanoia.server.ServerPlayer;
import jparanoia.server.constants.ServerConstants;
import jparanoia.shared.JParanoia;

public class WindowSetup {

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

	public static final void setupInputTextPane(JTextArea area, ServerOptions opts, NameManager nc) {
		area = new JTextArea(3, 44);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEnabled(false);
		area.setFont(ServerConstants.FONT_NORMAL);
		ServerTextAreaListener listener = new ServerTextAreaListener(area, nc, opts);
		area.addKeyListener(listener);
		
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
