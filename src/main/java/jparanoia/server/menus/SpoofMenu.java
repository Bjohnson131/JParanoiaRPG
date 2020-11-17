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
	private ServerPlayer playerToSpoof;
	private String currentPlayerID = "00";

	/**
	 * 
	 */
	private static final long serialVersionUID = 7152601545840492953L;

	private JCheckBox spoofCheckBox;
	private JComboBox<? extends ServerPlayer> spoofComboBox;

	public SpoofMenu(JTextArea inputArea, ServerPlayer[] players) {
		super();
		this.spoofCheckBox = getSpoofCheckBox(inputArea);
		this.spoofComboBox = getPlayerSpoofComboBox(players, inputArea, this.spoofCheckBox);

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.add(Box.createRigidArea(new Dimension(2, 0)));
		this.add(spoofComboBox);
		this.add(Box.createRigidArea(new Dimension(5, 0)));
		this.add(spoofCheckBox);
		this.add(Box.createRigidArea(new Dimension(2, 0)));
		this.setBorder(BorderFactory.createTitledBorder("Spoof"));

	}

	private JCheckBox getSpoofCheckBox(JTextArea inputArea) {
		JCheckBox spoofCheckBox = new JCheckBox();
		spoofCheckBox.addActionListener(paramAnonymousActionEvent -> {
			if (spoofCheckBox.isSelected()) {
				this.currentPlayerID = this.playerToSpoof.getID();
				inputArea.setFont(ServerConstants.FONT_SPOOF);
			} else {
				this.currentPlayerID = "00";
				inputArea.setFont(ServerConstants.FONT_NORMAL);
			}
			inputArea.requestFocus();
		});
		return spoofCheckBox;
	}

	private  JComboBox<? extends ServerPlayer> getPlayerSpoofComboBox(ServerPlayer[] aosp, JTextArea inputArea,
			JCheckBox spoofBox) {
		JComboBox<? extends ServerPlayer> spoofComboBox = new JComboBox<>(aosp);
		spoofComboBox.addActionListener(paramAnonymousActionEvent -> {
			playerToSpoof = (ServerPlayer) spoofComboBox.getSelectedItem();
			if (spoofBox.isSelected()) {
				this.currentPlayerID = this.playerToSpoof.getID();
			}
			inputArea.requestFocus();
		});
		spoofComboBox.setMaximumSize(new Dimension(130, 20));
		spoofComboBox.setPreferredSize(new Dimension(130, 20));
		spoofComboBox.setMinimumSize(new Dimension(130, 20));
		return spoofComboBox;
	}
	
	public void doClickAction() {
		spoofCheckBox.doClick();
	}
	
	public void getFocus() {
		spoofCheckBox.requestFocus();
	}
	
	public boolean isSpoofMode() {
		return spoofCheckBox.isSelected();
	}
	
	public String getCurrentPlayerID() {
		return this.currentPlayerID;
	}
	
	public ServerPlayer getPlayerToSpoof() {
		return this.playerToSpoof;
	}

}
