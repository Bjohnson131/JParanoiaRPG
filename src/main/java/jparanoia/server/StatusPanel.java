package jparanoia.server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import static java.lang.invoke.MethodHandles.lookup;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class StatusPanel extends JPanel implements Serializable {
	private static final long serialVersionUID = 9110451851744998618L;
	private int timerInterval = 500;
	private TimerListener myTimerListener;
	private Timer newMessageAnimationTimer = new Timer(this.timerInterval, this.myTimerListener);
	private boolean freshTimer;
	private ServerPlayer player;
	private JButton statusButton;
	private JButton newMessageLabel;
	private ImageIcon notConnectedIcon = new ImageIcon(
			Thread.currentThread().getContextClassLoader().getResource("graphics/notConnectedIcon.jpg"));
	private ImageIcon connectedIcon = new ImageIcon(
			Thread.currentThread().getContextClassLoader().getResource("graphics/connectedIcon.jpg"));
	private ImageIcon mutedIcon = new ImageIcon(
			Thread.currentThread().getContextClassLoader().getResource("graphics/mutedIcon.jpg"));
	private ImageIcon frozenIcon = new ImageIcon(
			Thread.currentThread().getContextClassLoader().getResource("graphics/frozenIcon.jpg"));
	private ImageIcon combatIcon = new ImageIcon(
			Thread.currentThread().getContextClassLoader().getResource("graphics/combatIcon.jpg"));
	private ImageIcon nullMessageIcon = new ImageIcon(
			Thread.currentThread().getContextClassLoader().getResource("graphics/nullMessageIcon.jpg"));
	private ImageIcon newMessageIcon = new ImageIcon(
			Thread.currentThread().getContextClassLoader().getResource("graphics/newMessageIcon.jpg"));

	public StatusPanel(ServerPlayer paramServerPlayer) {
		this.myTimerListener = new TimerListener(this);
		this.player = paramServerPlayer;
		this.player.statusPanel = this;
		this.newMessageAnimationTimer.setInitialDelay(this.timerInterval);
		this.statusButton = new JButton(this.notConnectedIcon);
		this.statusButton.setEnabled(false);
		this.statusButton.setDisabledIcon(this.notConnectedIcon);
		this.statusButton.setDisabledSelectedIcon(this.notConnectedIcon);
		this.statusButton.setIcon(this.connectedIcon);
		this.statusButton.setPreferredSize(new Dimension(31, 30));
		this.statusButton.setMinimumSize(new Dimension(31, 30));
		this.statusButton.setSelected(false);
		this.statusButton.addActionListener(paramAnonymousActionEvent -> {
			if (StatusPanel.this.player.muted) {
				JPServer.unmute(StatusPanel.this.player.getID());
				StatusPanel.this.player.muted = false;
				if (!JPServer.serverOptions.isFrozen()) {
					StatusPanel.this.statusButton.setIcon(StatusPanel.this.connectedIcon);
				} else {
					StatusPanel.this.statusButton.setIcon(StatusPanel.this.frozenIcon);
				}
			} else {
				JPServer.mute(StatusPanel.this.player.getID());
				StatusPanel.this.player.muted = false;
				if (!JPServer.serverOptions.isFrozen()) {
					StatusPanel.this.statusButton.setIcon(StatusPanel.this.mutedIcon);
				}
			}
			JPServer.inputLine.requestFocus();
		});
		this.newMessageLabel = new JButton(this.nullMessageIcon);
		this.newMessageLabel.setEnabled(true);
		this.newMessageLabel.setBorderPainted(false);
		this.newMessageLabel.setPreferredSize(new Dimension(31, 30));
		this.newMessageLabel.setMinimumSize(new Dimension(31, 30));
		this.newMessageLabel.addActionListener(paramAnonymousActionEvent -> {
			StatusPanel.this.newMessageAnimationTimer.stop();
			StatusPanel.this.newMessageLabel.setIcon(StatusPanel.this.nullMessageIcon);
			JPServer.inputLine.requestFocus();
		});
		super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		super.add(this.statusButton);
		super.add(Box.createRigidArea(new Dimension(0, 2)));
		super.add(this.newMessageLabel);
	}

	public void freeze() {
		this.statusButton.setIcon(this.frozenIcon);
	}

	public void unfreeze() {
		if (this.player.muted) {
			this.statusButton.setIcon(this.mutedIcon);
		} else {
			this.statusButton.setIcon(this.connectedIcon);
		}
	}

	public void combat() {
		this.statusButton.setIcon(this.combatIcon);
	}

	public void statusLoggedIn(boolean paramBoolean) {
		this.statusButton.setEnabled(paramBoolean);
	}

	public boolean isNewMessageWaiting() {
		return this.newMessageAnimationTimer.isRunning();
	}

	public void statusNewMessage(boolean paramBoolean) {
		if (paramBoolean && !this.newMessageAnimationTimer.isRunning()) {
			this.freshTimer = true;
			this.newMessageAnimationTimer.start();
		} 
		if (!paramBoolean && this.newMessageAnimationTimer.isRunning()) {
			this.newMessageAnimationTimer.stop();
			this.newMessageLabel.setIcon(this.nullMessageIcon);
		}
	}

	static class TimerListener implements ActionListener, Serializable {
		private static final long serialVersionUID = 8507773936827487272L;
		private int frameNumber;
		public final StatusPanel panel;

		public TimerListener(StatusPanel panel) {
			this.panel = panel;
		}

		public void actionPerformed(ActionEvent paramActionEvent) {
			if (this.panel.freshTimer) {
				this.frameNumber = 0;
				this.panel.freshTimer = false;
			}
			switch (this.frameNumber) {
			case 0:
				this.frameNumber += 1;
				this.panel.newMessageLabel.setIcon(this.panel.newMessageIcon);
				break;
			case 4:
				this.frameNumber = 0;
				this.panel.newMessageLabel.setIcon(this.panel.nullMessageIcon);
				break;
			default:
				this.frameNumber += 1;
			}
		}
	}
}

/*
 * Location:
 * C:\Users\noahc\Desktop\JParanoia(1.31.1)\JParanoia(1.31.1).jar!\jparanoia\
 * server\StatusPanel.class Java compiler version: 2 (46.0) JD-Core Version:
 * 0.7.1
 */
