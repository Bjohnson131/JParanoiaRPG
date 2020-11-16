package jparanoia.server.menus;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import jparanoia.server.ServerPlayer;
import jparanoia.server.constants.ServerConstants;
import jparanoia.server.window.WindowSetup;

public class SpoofMenu extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7152601545840492953L;

	private JCheckBox spoofCheckBox;
	private JComboBox<? extends ServerPlayer> spoofComboBox;

	public SpoofMenu(JTextArea inputArea, ServerPlayer[] players) {
		super();
		this.spoofCheckBox = SpoofMenu.getSpoofCheckBox(inputArea);
		this.spoofComboBox = SpoofMenu.getPlayerSpoofComboBox(players, inputArea, this.spoofCheckBox);

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.add(Box.createRigidArea(new Dimension(2, 0)));
		this.add(spoofComboBox);
		this.add(Box.createRigidArea(new Dimension(5, 0)));
		this.add(spoofCheckBox);
		this.add(Box.createRigidArea(new Dimension(2, 0)));
		this.setBorder(BorderFactory.createTitledBorder("Spoof"));

	}

	private static JCheckBox getSpoofCheckBox(JTextArea inputArea) {
		JCheckBox spoofCheckBox = new JCheckBox();
		spoofCheckBox.addActionListener(paramAnonymousActionEvent -> {
			if (spoofCheckBox.isSelected()) {
				currentPlayerID = playerToSpoof.getID();
				inputArea.setFont(ServerConstants.FONT_SPOOF);
			} else {
				currentPlayerID = "00";
				inputArea.setFont(ServerConstants.FONT_NORMAL);
			}
			inputArea.requestFocus();
		});
		return spoofCheckBox;
	}

	private static JComboBox<? extends ServerPlayer> getPlayerSpoofComboBox(ServerPlayer[] aosp, JTextArea inputArea,
			JCheckBox spoofBox) {
		JComboBox spoofComboBox = new JComboBox<>(aosp);
		spoofComboBox.addActionListener(paramAnonymousActionEvent -> {
			playerToSpoof = (ServerPlayer) spoofComboBox.getSelectedItem();
			if (spoofBox.isSelected()) {
				currentPlayerID = playerToSpoof.getID();
			}
			inputArea.requestFocus();
		});
		spoofComboBox.setMaximumSize(new Dimension(130, 20));
		spoofComboBox.setPreferredSize(new Dimension(130, 20));
		spoofComboBox.setMinimumSize(new Dimension(130, 20));
	}

}
