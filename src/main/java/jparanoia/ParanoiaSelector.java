package jparanoia;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import static java.awt.Toolkit.getDefaultToolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import static java.lang.System.exit;
import java.lang.invoke.MethodHandles;
import static java.lang.invoke.MethodHandles.lookup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jparanoia.client.JPClient;
import jparanoia.server.JPServer;
import jparanoia.server.constants.ServerConstants;
import jparanoia.shared.constants.SharedConstants;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

public class ParanoiaSelector {
	static {
		System.setProperty("swing.metalTheme", "steel");
	}

	private static final Logger LOGGER = getLogger(MethodHandles.lookup().lookupClass());
	public static final Dimension localDimension = getDefaultToolkit().getScreenSize();

	private static JFrame frame = new JFrame(SharedConstants.SELECTOR_TITLE_BAR);
	private static JButton serverButton = new JButton("Server " + ServerConstants.JPARANOIA_VERSION);
	private static JButton clientButton = new JButton("Client " + JPClient.getVersionName());
	private static Container contentPane = frame.getContentPane();

	public static void main(String[] paramArrayOfString) {
		LOGGER.info(SharedConstants.SERVER_VERSION_ANNOUNCEMENT);
		LOGGER.info(SharedConstants.CLIENT_VERSION_ANNOUNCEMENT);

		ParanoiaSelector.makeWindow();

		frame.setVisible(true);
	}

	public static void makeWindow() {
		JLabel localJLabel;
		JPanel localJPanel;
		ImageIcon localImageIcon;
		Insets localInsets;
		
		contentPane.setLayout(null);
		int i = (int) localDimension.getWidth();
		int j = (int) localDimension.getHeight();
		frame.setSize(SharedConstants.SELECTOR_WINDOW_WIDTH, SharedConstants.SELECTOR_WINDOW_HEIGHT);
		frame.setLocation(i / 2 - frame.getWidth() / 2, j / 2 - frame.getHeight() / 2);

		ClassLoader imageLoader = Thread.currentThread().getContextClassLoader();
		localImageIcon = new ImageIcon(getDefaultToolkit().getImage(imageLoader.getResource("graphics/jpsplash.jpg")));
		localJLabel = new JLabel(localImageIcon);
		localJPanel = new JPanel();
		localJPanel.setLayout(new GridLayout(1, 2, 4, 4));
		localJPanel.add(serverButton);
		localJPanel.add(clientButton);
		serverButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			frame.dispose();
			JPServer.main(null);
		}
		});
		clientButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			frame.dispose();
			JPClient.main(null);
		}
		});
		contentPane.add(localJLabel);
		contentPane.add(localJPanel);
		localInsets = contentPane.getInsets();
		localJLabel.setBounds(0 + localInsets.left, 0 + localInsets.top, 600, 295);
		localJPanel.setBounds(0 + localInsets.left, 298 + localInsets.top, 600, 35);

		frame.setIconImage(getDefaultToolkit().getImage(imageLoader.getResource("graphics/jparanoiaIcon.jpg")));
		frame.addWindowListener(new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent paramAnonymousWindowEvent) {
			exit(0);
		}
		});
		frame.setResizable(false);
	}
}

/*
 * Location:
 * C:\Users\noahc\Desktop\JParanoia(1.31.1)\JParanoia(1.31.1).jar!\jparanoia\
 * ParanoiaSelector.class Java compiler version: 2 (46.0) JD-Core Version: 0.7.1
 */
