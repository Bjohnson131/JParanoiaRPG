package jparanoia.server.menus;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import jparanoia.server.JPServer;
import jparanoia.server.ServerOptions;

public class FreezeMenu extends JPanel {

	//private JPanel freezePanel = new JPanel();
	private JButton freezeButton = new JButton("Freeze");
	private JButton combatButton = new JButton("Combat!");

	public FreezeMenu(ServerOptions opts) {
		super();
		this.setLayout(new GridLayout(1, 2));
		freezeButton.addActionListener(paramAnonymousActionEvent -> {
			if (!opts.isFrozen()) {
				freezePlayers();
			} else {
				unfreezePlayers();
			}
		});
		combatButton.addActionListener(paramAnonymousActionEvent -> JPServer.startCombat());
		combatButton.setEnabled(false);
		this.add(combatButton);
		this.add(freezeButton);
	}
}
