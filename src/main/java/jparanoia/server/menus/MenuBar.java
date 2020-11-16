package jparanoia.server.menus;

import java.net.InetAddress;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import jparanoia.server.ServerOptions;
import jparanoia.server.constants.ServerConstants;
import jparanoia.server.window.WindowSetup;
import jparanoia.shared.GameRegistrar;
import jparanoia.shared.Prefs;

public class MenuBar extends JMenu {
	public JMenuItem startServerMenuItem;
	public JMenuItem stopServerMenuItem;
	public JCheckBoxMenuItem registerGameMenuItem;
	public JMenuItem setGameDescriptionMenuItem;

	public MenuBar(Prefs p, ServerOptions opts, String description, String defaultDesc) {
		super(ServerConstants.MENU_LABEL);

		startServerMenuItem = new JMenuItem("Start server");
		startServerMenuItem.setToolTipText("Opens your computer for new connections.");
		startServerMenuItem.addActionListener(paramAnonymousActionEvent -> startServer());

		stopServerMenuItem = new JMenuItem("Stop server");
		stopServerMenuItem.setToolTipText("Stops your computer from accepting new connections.");
		stopServerMenuItem.addActionListener(paramAnonymousActionEvent -> stopServer());

		registerGameMenuItem = new JCheckBoxMenuItem("Register Game");
		registerGameMenuItem.setToolTipText(
				"<HTML>When checked, your server will be made available<BR>to players via the JParanoia Game Registry so they<BR>will not need the IP address of your server.</HTML>");
		registerGameMenuItem.setSelected(p.getPref(31).equals(true));
		registerGameMenuItem.addActionListener(paramAnonymousActionEvent -> {
			if (opts.isRegisterGame()) {
				opts.setRegisterGame(false);
				if (opts.isGameRegistered()) {
					GameRegistrar.removeGame();
				}
			} else {
				opts.setRegisterGame(true);
				if (opts.isServerRunning()) {
					if (description.equals(defaultDesc)) {
						setGameDescriptionMenuItem.doClick();
					}
					GameRegistrar.addGame(description);
				}
			}

		});

		setGameDescriptionMenuItem = new JMenuItem("Set Game Description...");
		setGameDescriptionMenuItem
				.setToolTipText("<HTML>This changes the name of your game<BR> on the JParanoia Game Registry.</HTML>");
		setGameDescriptionMenuItem.addActionListener(paramAnonymousActionEvent -> {
			String str = (String) JOptionPane.showInputDialog(null, "Enter a description for your game:",
					"Set Game Description...", JOptionPane.PLAIN_MESSAGE, null, null, description);
			if (str != null && !str.equals("") && !str.equals(description)) {
				description = str;
				if (opts.isGameRegistered()) {
					GameRegistrar.addGame(description);
				}
			}
		});

		this.add(startServerMenuItem);
		this.add(stopServerMenuItem);
		this.addSeparator();
		this.add(registerGameMenuItem);
		this.add(setGameDescriptionMenuItem);
		this.addSeparator();
		this.add(new JLabel("  Local IP :   " + InetAddress.getLocalHost().getHostAddress()));
	}

}
