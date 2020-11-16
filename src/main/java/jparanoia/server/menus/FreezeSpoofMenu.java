package jparanoia.server.menus;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import jparanoia.server.ServerOptions;
import jparanoia.server.ServerPlayer;

public class FreezeSpoofMenu extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9207346160220387427L;
	public FreezeMenu fm;
	public SpoofMenu sm;
	
	public FreezeSpoofMenu(ServerOptions opts, JTextArea inputArea, ServerPlayer[] players) {
		super();
		fm = new FreezeMenu(opts);
		sm = new SpoofMenu(inputArea, players);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(sm);
		this.add(Box.createRigidArea(new Dimension(0, 5)));
		this.add(fm);
	}
}
