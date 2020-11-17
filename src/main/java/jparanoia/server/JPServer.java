package jparanoia.server;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import jparanoia.server.constants.ServerConstants;
import jparanoia.server.menus.FreezeSpoofMenu;
import jparanoia.server.menus.MenuBar;
import jparanoia.server.menus.SpoofMenu;
import jparanoia.server.window.WindowSetup;
import jparanoia.shared.BrightColorArray;
import jparanoia.shared.ErrorLogger;
import jparanoia.shared.GameLogger;
import jparanoia.shared.GameRegistrar;
import jparanoia.shared.JPSounds;
import jparanoia.shared.JParanoia;

public class JPServer extends JParanoia {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public static final ServerOptions serverOptions = new ServerOptions();
	public static Random rand = new Random();

	// ServerChatThread
	private static PrintWriter someWriter;
	private static ServerSocketThread servSocketThread;
	private static ServerChatThread thisThread;
	// combatFrame
	public static ServerPlayer[] players;
	public static ServerPlayer myPlayer;
	public static int numberOfPCs = 0;
	// ServerChatThread
	public static int numberOfConnectedClients = 0;
	public static int numberOfConnectedObservers = 0;
	public static ThreadGroup chatThreadGroup = new ThreadGroup("my group of chat threads");
	public static JCheckBoxMenuItem hearObserversMenuItem;
	public static int numberOfPlayers = 0;
	public static final ArrayList<ServerChatThread> chatThreads = new ArrayList<>();
	// FontMenu
	public static Integer mainFontSize = 99;
	// SERVER_PLAYER
	public static Vector<ServerPlayer> spareNpcs = new Vector<ServerPlayer>(10);
	public static SimpleAttributeSet charsheetAttributes;
	// Server Socked Thread
	public static JMenuItem startServerMenuItem;
	public static JMenuItem stopServerMenuItem;
	// Private Message Pane
	public static ServerOptionsMenu optionsMenu;
	// charsheet panel
	public static ServerPlayer[] troubleshooters;
	// CombatFrame
	public static JButton freezeButton;
	public static JButton combatButton;
	// PrivateMessagePane
	public static String currentColorScheme = "";
	// ServerImageMenu
	public static ImageDataParser idp;
	// PrivateMessagePane
	public static CharsheetPanel charsheetPanel;

	// The below variables are not mentioned in ANY outside classes.
	// They should be divided somehow.
	private static int randInt = rand.nextInt(1000);
	private static String defaultGameDescription = "JParanoia Community " + ServerConstants.JPARANOIA_VERSION + " ("
			+ randInt + ")";
	private static FreezeSpoofMenu fsm;
	private static ServerPlayer pmTargetPlayer;
	private static JScrollPane inputScrollPane;
	private static String announcement = "";
	private static String styleBegin;
	private static String styleEnd = "";
	private static JPanel inputPanel;
	private static JPanel PMContainer;
	private static JMenuBar menuBar;

	private static JMenu fontMenu;
	private static JMenu playerMenu;
	private static JMenu npcMenu;
	private static JMenu globalPMMenu;
	private static JMenu sendImageMenu;
	private static JMenu observersMenu;
	private static JMenuItem sendGlobalPMMenuItem;
	private static JMenuItem showObserversListMenuItem;
	private static JMenuItem announceObserversMenuItem;

	private static JLabel ipLabel;
	private static JScrollPane scrollPane;
	private static JSplitPane splitPane;
	public static JTextArea inputLine;
	public static CombatFrame combatFrame;

	private static SimpleAttributeSet systemTextAttributes = new SimpleAttributeSet();
	private static Color[] brightColors;
	private static Date timeStamp;
	private static String gameDescription = defaultGameDescription;
	private static String currentPlayerID = "00";

	private static final String newColorScheme = JParanoia.WHITE_ON_BLACK;
	private static InetAddress localIP = null;
	private static PrivateMessagePane[] PMPane;
	private static PMAndStatusPanel[] pmstatus;

	public JPServer() {
		chatDocument = new DefaultStyledDocument();
		brightColors = new BrightColorArray().getColors();
		// Code above this point seems to have no effect whether commented out or not.
		// Code below is for initialization exclusively.
		scrollPane = new JScrollPane(displayArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		charsheetPanel = new CharsheetPanel();
		menuBar = new JMenuBar();
		inputPanel = new JPanel();
		sendImageMenu = new ServerImageMenu();
		GridBagLayout localGridBagLayout1 = new GridBagLayout();
		DataParser localDataParser = new DataParser();

		Profiler profiler = new Profiler("JPServer");
		setOptions();

		try {
			localIP = InetAddress.getLocalHost();
		} catch (UnknownHostException localUnknownHostException) {
			logger.warn(ServerConstants.WARN_NO_IP);
		}

		profiler.start("player list init");

		profiler.start("image data init");
		logger.info("Processing imageData.txt:");
		idp = new ImageDataParser();
		idp.parseImageURLs("imageData.txt");
		players = localDataParser.parsePlayerList("playerList.txt");
		numberOfPlayers = players.length;
		for (final ServerPlayer player : players) {
			if (player.IS_PLAYER) {
				numberOfPCs++;
			}
		}

		int pclen = numberOfPCs > 1 ? numberOfPCs - 1 : 1;
		troubleshooters = new ServerPlayer[pclen];
		System.arraycopy(players, 1, troubleshooters, 0, pclen);

		for (final ServerPlayer troubleshooter : troubleshooters) {
			JParanoia.nMgr.addPlayer(troubleshooter);
			if (JParanoia.nMgr.playerCount() > 1) {
				int j = JParanoia.nMgr.playerCount() - 1;
				int k = j - 1;
				String nameJ = JParanoia.nMgr.getPlayerAt(j).getName(), nameK = JParanoia.nMgr.getPlayerAt(k).getName();
				while (k >= 0 && nameJ.compareToIgnoreCase(nameK) < 0) {
					ServerPlayer toReplace = JParanoia.nMgr.getPlayerAt(k);
					JParanoia.nMgr.removeAtIndex(k);
					JParanoia.nMgr.addPlayerAtIndex(toReplace, j);
					j = k;
					k--;
					nameJ = JParanoia.nMgr.getPlayerAt(j).getName();
					nameK = JParanoia.nMgr.getPlayerAt(k).getName();
				}
			}
		}
		players[0].loggedIn = true;
		myPlayer = players[0];
		ServerPlayer[] arrayOfServerPlayer = new ServerPlayer[players.length - 1];
		System.arraycopy(players, 1, arrayOfServerPlayer, 0, players.length - 1);

		profiler.start("further frame init");
		WindowSetup.setupServerTextFrame(displayArea);
		inputLine = WindowSetup.setupInputTextPane(serverOptions, JParanoia.nMgr);
		charsheetAttributes = WindowSetup.getCharsheetArrs();
		textAttributes = WindowSetup.getTextPaneArrs(prefs);
		MenuBar serverMenu = new MenuBar(prefs, serverOptions, gameDescription, defaultGameDescription);
		fsm = new FreezeSpoofMenu(serverOptions, inputLine, arrayOfServerPlayer);
		setColorScheme();
		// WHY?
		new PlainDocument();

		inputScrollPane = new JScrollPane(inputLine, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// Copy of what is in the action listener.... interesting.
		// playerToSpoof = (ServerPlayer) spoofComboBox.getSelectedItem();

		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		inputPanel.add(fsm);
		inputPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		inputPanel.add(inputScrollPane);

		stopServerMenuItem.setEnabled(false);
		menuBar.add(serverMenu);
		fontMenu = new FontMenu("Font");
		menuBar.add(fontMenu);
		optionsMenu = new ServerOptionsMenu("Options");
		menuBar.add(optionsMenu);
		playerMenu = new JMenu("Player");
		for (final ServerPlayer troubleshooter : troubleshooters) {
			playerMenu.add(troubleshooter.playerMenu);
		}
		menuBar.add(playerMenu);
		npcMenu = new JMenu("Spare NPCs");
		for (ServerPlayer spareNpc : spareNpcs) {
			npcMenu.add(spareNpc.npcMenu);
		}
		menuBar.add(npcMenu);
		globalPMMenu = new JMenu("Global PM");
		sendGlobalPMMenuItem = new JMenuItem("Send Global PM...");
		sendGlobalPMMenuItem.addActionListener(paramAnonymousActionEvent -> {
			new GlobalPMDialog().setVisible(true);
		});
		globalPMMenu.add(sendGlobalPMMenuItem);
		menuBar.add(globalPMMenu);
		menuBar.add(sendImageMenu);
		observersMenu = new JMenu("Observers");
		hearObserversMenuItem = new JCheckBoxMenuItem("Hear Observers");
		hearObserversMenuItem.setSelected((Boolean) prefs.getPref(29));
		hearObserversMenuItem.addActionListener(paramAnonymousActionEvent -> JPServer.toggleHearObservers());
		announceObserversMenuItem = new JCheckBoxMenuItem("Announce Observers");
		announceObserversMenuItem.setSelected((Boolean) prefs.getPref(30));
		announceObserversMenuItem.addActionListener(paramAnonymousActionEvent -> JPServer.toggleAnnounceObservers());
		showObserversListMenuItem = new JMenuItem("Show Observers List");
		showObserversListMenuItem.addActionListener(paramAnonymousActionEvent -> obsFrame.setVisible(true));
		observersMenu.add(hearObserversMenuItem);
		observersMenu.add(announceObserversMenuItem);
		observersMenu.add(showObserversListMenuItem);
		menuBar.add(observersMenu);
		Container localContainer = frame.getContentPane();
		// WHY?
		// new JPanel();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, charsheetPanel, scrollPane);
		splitPane.setDividerLocation(122);
		splitPane.setOneTouchExpandable(true);
		GridBagConstraints localGridBagConstraints1 = new GridBagConstraints();
		localGridBagConstraints1.gridx = 0;
		localGridBagConstraints1.gridy = 0;
		localGridBagConstraints1.weighty = 1.0D;
		localGridBagConstraints1.weightx = 1.0D;
		localGridBagConstraints1.fill = 1;
		localGridBagConstraints1.anchor = 15;
		localGridBagConstraints1.insets = new Insets(2, 2, 2, 2);
		localGridBagLayout1.setConstraints(splitPane, localGridBagConstraints1);
		PMPane = new PrivateMessagePane[numberOfPCs];
		for (int m = 1; m < numberOfPCs; m++) {
			PMPane[m] = new PrivateMessagePane(players[m]);
		}

		profiler.start("status init");
		logger.info("\nServer's local IP Address: " + localIP.getHostAddress() + "\n");
		pmstatus = new PMAndStatusPanel[numberOfPCs];
		logger.info("PM & status array initialized.");
		for (int m = 1; m < numberOfPCs; m++) {
			pmstatus[m] = new PMAndStatusPanel(PMPane[m], PMPane[m].statusPanel);
		}
		logger.info("PM & status panels created.");
		PMContainer = new JPanel();
		PMContainer.setLayout(new BoxLayout(PMContainer, BoxLayout.Y_AXIS));
		for (int m = 1; m < numberOfPCs; m++) {
			PMContainer.add(Box.createRigidArea(new Dimension(0, 2)));
			PMContainer.add(pmstatus[m]);
		}
		logger.info("PM & status pane populated.");
		GridBagLayout localGridBagLayout2 = new GridBagLayout();
		GridBagConstraints localGridBagConstraints2 = new GridBagConstraints();
		localContainer.setLayout(localGridBagLayout2);
		localGridBagConstraints2.gridx = 0;
		localGridBagConstraints2.gridy = 0;
		localGridBagConstraints2.weighty = 1.0D;
		localGridBagConstraints2.weightx = 1.0D;
		localGridBagConstraints2.fill = 1;
		localGridBagConstraints2.anchor = 15;
		localGridBagConstraints2.insets = new Insets(2, 2, 2, 2);
		localGridBagLayout2.setConstraints(splitPane, localGridBagConstraints2);
		localContainer.add(splitPane);
		localGridBagConstraints2.weighty = 0.0D;
		localGridBagConstraints2.gridy = 5;
		localGridBagLayout2.setConstraints(inputPanel, localGridBagConstraints2);
		localContainer.add(inputPanel);
		localGridBagConstraints2.gridx = 4;
		localGridBagConstraints2.gridy = 0;
		localGridBagConstraints2.gridheight = 6;
		localGridBagConstraints2.gridwidth = 1;
		localGridBagConstraints2.weightx = 0.0D;
		localGridBagLayout2.setConstraints(PMContainer, localGridBagConstraints2);
		localContainer.add(PMContainer);
		// WHY?
		new JOptionPane();
		new JOptionPane();
		myPlayer = players[0];

		// Display the welcome message and the most recent patch notes!
		ServerConstants.writeIntroduction();

		profiler.start("log init");
		serverOptions.setKeepLog((Boolean) prefs.getPref(20));
		serverOptions.setHtmlLog((Boolean) prefs.getPref(21));
		if (serverOptions.isKeepLog()) {
			if (serverOptions.isHtmlLog()) {
				log = new GameLogger(players);
			} else {
				log = new GameLogger();
			}
		}
		logger.info("JPServer.frame constructed.\n");

		profiler.start("combat init");
		System.out.print("Attempting to load CombatFrame class... ");
		try {
			combatFrame = new CombatFrame();
			combatFrame = null;
			logger.info("loaded.");
		} catch (NoClassDefFoundError localNoClassDefFoundError) {
			logger.info("FAILED");
			errorMessage("CombatFrame - class definition not found",
					"The CombatFrame class failed to load. The combat manager\nis not available. Please notify the author. You will need\nto exit and relaunch the server to correct this problem.");
		}
//        net.roydesign.mac.MRJAdapter.addQuitApplicationListener(paramAnonymousActionEvent -> {
//        });
//        net.roydesign.mac.MRJAdapter.addAboutListener(paramAnonymousActionEvent -> {
//
//            JParanoia.aboutBoxMenuItem.doClick();
//        });
		mainFontSize = (Integer) textAttributes.getAttribute(StyleConstants.Size);

		WindowSetup.setupServerJframe(JParanoia.frame, menuBar, serverOptions);
		frame.setVisible(true);
		profiler.stop().print();
	}

	public void setOptions() {
		// TODO: re-locate app-info.
		serverOptions.setClobberAqua((Boolean) prefs.getPref(18));
		appInfo = "JParanoia Community Server " + ServerConstants.JPARANOIA_VERSION;
		serverOptions.setAllowObservers((Boolean) prefs.getPref(28));
		serverOptions.setRegisterGame((Boolean) prefs.getPref(31));
		serverOptions.setBehindRouter((Boolean) prefs.getPref(32));
		serverOptions.setPXPGame((Boolean) prefs.getPref(34));
		serverOptions.setGmNameNag((Boolean) prefs.getPref(35));
		serverOptions.setComputerFontIncrease((Integer) prefs.getPref(26));

		if (serverOptions.isPXPGame()) {
			serverOptions.setMaxNumClones(999);
		} else {
			serverOptions.setMaxNumClones((Integer) prefs.getPref(23));
		}

		if (serverOptions.isClobberAqua()) {
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception localException) {
				logger.info("Exception while setting L&F.");
			}
		}

	}

	public static void main(String[] paramArrayOfString) {
		logger.info("\nThis is the JParanoia Community Server console.\n");
		logger.info("Running under Java Runtime Environment version " + System.getProperty("java.version"));
		logger.info("\n");
		new JPServer();
		splitPane.repaint();
	}

	public static void exit() {
		if (numberOfConnectedClients - numberOfConnectedObservers > 0) {
			if (JOptionPane.showConfirmDialog(frame,
					"WARNING: Players are still connected!\nAre you SURE you want to quit?", "Quit confirmation...",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == 0) {
				sendCommand(ServerConstants.COMMANDS.SERVER_TERMINATION + "SERVER TERMINATED");
			} else {
				return;
			}
		}
		if (serverOptions.isKeepLog()) {
			log.closeLog();
			if (serverOptions.isHtmlLog()) {
				log.sanitize();
			}
		}
		if (ServerPlayer.numUnsavedCharsheets > 0) {
			if (JOptionPane.showConfirmDialog(frame,
					"Some character sheets contain unsaved changes.\nSave before exiting?", "Unsaved Changes",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
				soundMenu.charSheetAlertMenuItem.setSelected(false);
				for (final ServerPlayer troubleshooter : troubleshooters) {
					if (troubleshooter.hasUnsavedCharsheet()) {
						troubleshooter.saveCharsheet(false);
					}
				}
			}
		}
		if (serverOptions.isGameRegistered()) {
			GameRegistrar.removeGame();
		}
		frame.dispose();
		System.exit(0);
	}

	public static synchronized void sendCommand(String paramString) {
		for (int i = 0; i < chatThreads.size(); i++) {
			thisThread = chatThreads.get(i);
			someWriter = thisThread.out;
			someWriter.println(paramString);
		}
	}

	// TODO: used once, replace with in-line code
	private static void setColorScheme() {
		if (!currentColorScheme.equals(newColorScheme)) {
			switch (newColorScheme) {
			case WHITE_ON_BLACK:
				textColor = Color.white;
				displayArea.setBackground(Color.black);
				break;
			case BLACK_ON_WHITE:
				textColor = Color.black;
				displayArea.setBackground(Color.white);
				break;
			default:
				logger.error("Error: invalid color logic.");
				break;
			}
			currentColorScheme = newColorScheme;

			// Start to assign colors to characters.
			int i;
			switch (currentColorScheme) {
			case WHITE_ON_BLACK:
				for (i = 0; i < numberOfPlayers; i++) {
					players[i].chatColor = brightColors[i];
				}
				break;
			case BLACK_ON_WHITE:
				for (i = 0; i < numberOfPlayers; i++) {
					players[i].chatColor = ServerConstants.COLORS_DARK[i];
				}
				break;
			default:
				logger.error("Error: invalid color logic");
				break;
			}

		}
	}

	public static void playerHasJoined(int paramInt) {
		ServerPlayer localServerPlayer = players[paramInt];
		localServerPlayer.loggedIn = true;
		localServerPlayer.pmPane.enableInput();
		absoluteChat("--- " + localServerPlayer.toString() + " (" + localServerPlayer.realName + ") has joined ---");
		if (serverOptions.isShowTimeStamps()) {
			displayTimeStamp();
		}
		localServerPlayer.statusPanel.statusLoggedIn(true);
		if (soundIsOn && soundMenu.joinLeaveMenuItem.isSelected()) {
			soundPlayer.play(JPSounds.PLAYER_JOIN);
		}
	}

	public static synchronized void absoluteChat(String paramString) {
		try {
			chatDocument.insertString(chatDocument.getLength(), paramString + "\n", systemTextAttributes);
			displayArea.setDocument(chatDocument);
			if (autoScroll) {
				displayArea.setCaretPosition(chatDocument.getLength());
			}
			if (serverOptions.isKeepLog()) {
				String str;
				if (serverOptions.isHtmlLog()) {
					str = "<span class=\"gray\">" + paramString + "</span><br/>";
				} else {
					str = paramString;
				}
				log.logEntry(str);
			}
		} catch (BadLocationException localBadLocationException) {
			System.err.println("Unhandled exception. (Bad Location)");
		}
	}

	public static void displayTimeStamp() {
		timeStamp = new Date();
		absoluteChat("(" + timeStamp.toString() + ")");
	}

	public static void playerHasLeft(int paramInt) {
		ServerPlayer localServerPlayer = players[paramInt];
		localServerPlayer.loggedIn = false;
		localServerPlayer.pmPane.disableInput();
		absoluteChat("--- " + localServerPlayer.toString() + " (" + localServerPlayer.realName + ") has left ---");
		if (serverOptions.isShowTimeStamps()) {
			displayTimeStamp();
		}
		localServerPlayer.statusPanel.statusLoggedIn(false);
		localServerPlayer.realName = "???";
		if (soundIsOn && soundMenu.joinLeaveMenuItem.isSelected()) {
			soundPlayer.play(JPSounds.PLAYER_LEAVE);
		}
	}

	public static void toggleHearObservers() {
		if (hearObserversMenuItem.isSelected()) {
			sendCommand("097");
			absoluteChat("Observers are currently heard.");
		} else {
			sendCommand("098");
			absoluteChat("Observers are currently NOT heard.");
		}
	}

	public static void toggleAnnounceObservers() {
		if (announceObserversMenuItem.isSelected()) {
			JParanoia.announceObservers = true;
			absoluteSpam("Observers are currently announced.");
		} else {
			JParanoia.announceObservers = false;
			absoluteSpam("Observers are currently NOT announced.");
		}
	}

	public static void absoluteSpam(String paramString) {
		sendCommand(ServerConstants.COMMANDS.GLOBAL_MESSAGE + paramString);
		absoluteChat(paramString);
	}

	public static void setFontBold(boolean paramBoolean) {
		Boolean localBoolean = paramBoolean;
		textAttributes.addAttribute(StyleConstants.FontConstants.Bold, localBoolean);
	}

	public static void useComputerFont() {
		styleBegin = "<span class=\"computer\">";
		styleEnd = "</span>";
		mainFontSize = (Integer) textAttributes.getAttribute(StyleConstants.FontConstants.Size);
		int i = mainFontSize + serverOptions.getComputerFontIncrease();
		textAttributes.addAttribute(StyleConstants.FontConstants.Size, i);
		if (!serverOptions.isFontIsBold()) {
			setFontBold(true);
		}
	}

	public static void useGmFont() {
		styleBegin = "<span class=\"gmText\">";
		styleEnd = "</span>";
		if (!serverOptions.isFontIsBold()) {
			setFontBold(true);
		}
	}

	public static synchronized void generalChat(String paramString) {
		int i = Integer.parseInt(paramString.substring(0, 2));
		paramString = paramString.substring(2);
		styleBegin = styleEnd = "";
		selectFont(i);
		try {
			textAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, players[i].getChatColor());
			chatDocument.insertString(chatDocument.getLength(), "   " + players[i].toString() + ": ", textAttributes);
			textAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, textColor);
			chatDocument.insertString(chatDocument.getLength(), paramString + "\n", textAttributes);
			displayArea.setDocument(chatDocument);
			if (autoScroll) {
				displayArea.setCaretPosition(chatDocument.getLength());
			}
			if (serverOptions.isKeepLog()) {
				String str;
				if (serverOptions.isHtmlLog()) {
					str = styleBegin + "<span class=\"player" + i + "\">" + players[i].toString() + ":</span> "
							+ paramString + styleEnd + "<br/>";
				} else {
					str = players[i].toString() + ": " + paramString;
				}
				log.logEntry(str);
			}
		} catch (BadLocationException localBadLocationException) {
			System.err.println("Unhandled exception. (Bad Location)");
		}
		restoreFont(i);
		if (soundIsOn && soundMenu.newTextMenuItem.isSelected()) {
			soundPlayer.play(JPSounds.NEW_TEXT);
		}
	}

	public static synchronized void actionChat(String paramString) {
		int i = Integer.parseInt(paramString.substring(0, 2));
		paramString = paramString.substring(2);
		styleBegin = styleEnd = "";
		selectFont(i);
		try {
			textAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, players[i].getChatColor());
			chatDocument.insertString(chatDocument.getLength(),
					"* " + players[i].toString() + " " + paramString + " *\n", textAttributes);
			displayArea.setDocument(chatDocument);
			if (autoScroll) {
				displayArea.setCaretPosition(chatDocument.getLength());
			}
			if (serverOptions.isKeepLog()) {
				String str;
				if (serverOptions.isHtmlLog()) {
					str = styleBegin + "<span class=\"player" + i + "\">* " + players[i].toString() + " " + paramString
							+ " *</span>" + styleEnd + "<br/>";
				} else {
					str = "* " + players[i].toString() + " " + paramString + " *";
				}
				log.logEntry(str);
			}
			textAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, textColor);
		} catch (BadLocationException localBadLocationException) {
			System.err.println("Unhandled exception. (Bad Location)");
		}
		restoreFont(i);
		if (soundIsOn && soundMenu.newTextMenuItem.isSelected()) {
			soundPlayer.play(JPSounds.NEW_TEXT);
		}
	}

	public static synchronized void speechChat(String paramString) {
		int i = Integer.parseInt(paramString.substring(0, 2));
		paramString = paramString.substring(2);
		styleBegin = styleEnd = "";
		selectFont(i);
		String str1;
		if (paramString.endsWith("!!")) {
			str1 = "screams, ";
		} else if (paramString.endsWith("!")) {
			str1 = "shouts, ";
		} else {
			str1 = "says, ";
		}
		try {
			textAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, players[i].getChatColor());
			chatDocument.insertString(chatDocument.getLength(), players[i].toString() + " ", textAttributes);
			textAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, textColor);
			chatDocument.insertString(chatDocument.getLength(), str1 + "\"" + paramString + "\"\n", textAttributes);
			displayArea.setDocument(chatDocument);
			if (autoScroll) {
				displayArea.setCaretPosition(chatDocument.getLength());
			}
			if (serverOptions.isKeepLog()) {
				String str2;
				if (serverOptions.isHtmlLog()) {
					str2 = styleBegin + "<span class=\"player" + i + "\">" + players[i].toString() + "</span> " + str1
							+ "\"" + paramString + "\"" + styleEnd + "<br/>";
				} else {
					str2 = players[i].toString() + " " + str1 + "\"" + paramString + "\"";
				}
				log.logEntry(str2);
			}
			textAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, textColor);
		} catch (BadLocationException localBadLocationException) {
			System.err.println("Unhandled exception. (Bad Location)");
		}
		restoreFont(i);
		if (soundIsOn && soundMenu.newTextMenuItem.isSelected()) {
			soundPlayer.play(JPSounds.NEW_TEXT);
		}
	}

	public static synchronized void thoughtChat(String paramString) {
		int i = Integer.parseInt(paramString.substring(0, 2));
		paramString = paramString.substring(2);
		styleBegin = styleEnd = "";
		selectFont(i);
		try {
			textAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, players[i].getChatColor());
			chatDocument.insertString(chatDocument.getLength(),
					players[i].toString() + " . o O ( " + paramString + " )\n", textAttributes);
			displayArea.setDocument(chatDocument);
			if (autoScroll) {
				displayArea.setCaretPosition(chatDocument.getLength());
			}
			if (serverOptions.isKeepLog()) {
				String str;
				if (serverOptions.isHtmlLog()) {
					str = styleBegin + "<span class=\"player" + i + "\">" + players[i].toString() + " . o O ( "
							+ paramString + " )</span>" + styleEnd + "<br/>";
				} else {
					str = players[i].toString() + " . o O ( " + paramString + " )";
				}
				log.logEntry(str);
			}
			textAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, textColor);
		} catch (BadLocationException localBadLocationException) {
			System.err.println("Unhandled exception. (Bad Location)");
		}
		restoreFont(i);
		if (soundIsOn && soundMenu.newTextMenuItem.isSelected()) {
			soundPlayer.play(JPSounds.NEW_TEXT);
		}
	}

	public static synchronized void observerChat(String paramString) {
		String str1 = paramString.substring(0, paramString.indexOf("@~!~~"));
		paramString = paramString.substring(paramString.indexOf("@~!~~") + 5);
		try {
			textAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.gray);
			chatDocument.insertString(chatDocument.getLength(), "   " + str1 + ": ", textAttributes);
			textAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, textColor.darker());
			chatDocument.insertString(chatDocument.getLength(), paramString + "\n", textAttributes);
			displayArea.setDocument(chatDocument);
			if (autoScroll) {
				displayArea.setCaretPosition(chatDocument.getLength());
			}
			if (serverOptions.isKeepLog()) {
				String str2;
				if (serverOptions.isHtmlLog()) {
					str2 = "<span class=\"observer\">" + str1 + ": </span><span class=\"gray\">" + paramString
							+ "</span><br/>";
				} else {
					str2 = str1 + ": " + paramString;
				}
				log.logEntry(str2);
			}
		} catch (BadLocationException localBadLocationException) {
			System.err.println("Unhandled exception. (Bad Location)");
		}
		if (soundIsOn && soundMenu.newObserverTextMenuItem.isSelected()) {
			soundPlayer.play(JPSounds.NEW_TEXT);
		}
	}

	public static void privateMessageHandler(String paramString, boolean paramBoolean) {
		int i = Integer.parseInt(paramString.substring(0, 2));
		pmTargetPlayer = players[i];
		if (pmTargetPlayer == myPlayer) {
			if (paramBoolean && soundIsOn && soundMenu.newPMAlertMenuItem.isSelected()) {
				soundPlayer.play(JPSounds.NEW_PM_ALERT);
			}
			int j = Integer.parseInt(paramString.substring(2, 4));
			paramString = paramString.substring(4);
			players[j].statusPanel.statusNewMessage(true);
			String str1 = players[j].getName().substring(0, players[j].getName().indexOf("-"));
			try {
				PrivateMessagePane.pmAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground,
						players[j].getChatColor());
				PMPane[j].privateMessageDocument.insertString(PMPane[j].privateMessageDocument.getLength(),
						"  " + str1 + ": ", PrivateMessagePane.pmAttributes);
				PrivateMessagePane.pmAttributes.addAttribute(StyleConstants.CharacterConstants.Foreground, textColor);
				PMPane[j].privateMessageDocument.insertString(PMPane[j].privateMessageDocument.getLength(),
						paramString + "\n", PrivateMessagePane.pmAttributes);
				PMPane[j].displayArea.setDocument(PMPane[j].privateMessageDocument);
				if (autoScroll) {
					PMPane[j].displayArea.setCaretPosition(PMPane[j].privateMessageDocument.getLength());
				}
				if (serverOptions.isKeepLog()) {
					String str2;
					if (serverOptions.isHtmlLog()) {
						str2 = "<span class=\"pm\"><span class=\"player" + j + "\">(" + players[j].toString()
								+ "</span> -> <span class=\"player0\">" + myPlayer.toString() + ")</span>: "
								+ paramString + "</span><br/>";
					} else {
						str2 = "(" + players[j].toString() + " -> " + myPlayer.toString() + "): " + paramString;
					}
					log.logEntry(str2);
				}
			} catch (BadLocationException localBadLocationException) {
				System.err.println("Unhandled exception. (Bad Location)");
			}
		} else {
			pmTargetPlayer.specificSend("200" + paramString);
		}
	}

	public static void sendChat(String paramString) {
		String str1 = paramString;
		str1 = str1.replace('\n', ' ');
		if (!"".equals(str1) && !" ".equals(str1) && !str1.endsWith("\n")) {
			if ((str1.startsWith("/") || str1.startsWith("'")) && !fsm.spoofMenu.isSpoofMode()
					&& !serverOptions.isAllowGMEmotes()) {
				JParanoia.warningMessage("GM Emotes not allowed",
						"You have attempted to use the speech or action\nkey while not spoofing a character.\n\nTo permit this, go to the Options menu and\nenable \"Allow GM Emotes\".");
				fsm.spoofMenu.getFocus();
				inputLine.setText(inputLine.getText().substring(0, inputLine.getText().length() - 1));
			} else {
				String str2;
				if (str1.startsWith("/")) {
					str2 = "110";
					sendCommand(str2 + currentPlayerID + str1.substring(1));
					actionChat(currentPlayerID + str1.substring(1));
				} else if (str1.startsWith("'")) {
					str2 = "120";
					if (Integer.parseInt(currentPlayerID) == numberOfPCs && serverOptions.isComputerAllCaps()) {
						str1 = str1.toUpperCase(Locale.ENGLISH);
					}
					sendCommand(str2 + currentPlayerID + str1.substring(1));
					speechChat(currentPlayerID + str1.substring(1));
				} else if (str1.startsWith("\\")) {
					str2 = "130";
					sendCommand(str2 + currentPlayerID + str1.substring(1));
					thoughtChat(currentPlayerID + str1.substring(1));
				} else {
					str2 = "100";
					sendCommand(str2 + currentPlayerID + str1);
					generalChat(currentPlayerID + str1);
				}
				if (fsm.spoofMenu.isSpoofMode() && optionsMenu.singleUseSpoofMenuItem.isSelected()) {
					fsm.spoofMenu.doClickAction();
				}
				inputLine.setText("");
			}
		}
		if (str1.endsWith("\n") || "".equals(str1)) {
			inputLine.setText("");
		}
	}

	// TODO: eliminate this.
	/**/
	public static void setSendMainText(boolean paramBoolean) {
		serverOptions.setSendMainText(paramBoolean);
	}

	private static void startCombat() {
		if (chatThreads.isEmpty()) {
			absoluteChat("Kind of hard to have combat without combatants.");
		} else {
			try {
				combatFrame = new CombatFrame();
				combatFrame.setVisible(true);
				freezeButton.setEnabled(false);
				combatButton.setEnabled(false);
				// TODO: move the soundIsOn to the soundplayer.
				if (soundIsOn && soundMenu.combatAlertMenuItem.isSelected()) {
					soundPlayer.play(JPSounds.COMBAT_ALERT);
				}
				if (soundIsOn && soundMenu.combatMusicMenuItem.isSelected()) {
					soundPlayer.startCombatMusic();
					combatMusicIsPlaying = true;
				}
				sendCommand(ServerConstants.COMMANDS.GLOBAL_MESSAGE + "*** COMBAT ROUND BEGINS ***");
				absoluteChat("*** COMBAT ROUND BEGINS ***");
				sendCommand(ServerConstants.COMMANDS.START_COMBAT_ROUND);
				JPServer.freezePlayers();
			} catch (NoClassDefFoundError localNoClassDefFoundError) {
				errLog = new ErrorLogger("cmbt", localNoClassDefFoundError.toString() + " in JPServer.startCombat()");
				localNoClassDefFoundError.printStackTrace(errLog.out);
				errLog.closeLog();
				errLog = null;
				errorMessage("CombatFrame - class definition not found",
						"The CombatFrame class failed to load. The combat manager\nis not available. An error log has been created in your logs\ndirectory. Please notify the author. You will need to exit\nand relaunch the server to correct this problem.");
			}
		}
	}

	public static void notifyPlayersOfDeath(ServerPlayer paramServerPlayer) {
		sendCommand(ServerConstants.COMMANDS.NOTIFY_DEATH + paramServerPlayer.getID());
		if (soundIsOn && soundMenu.deathAlertMenuItem.isSelected()) {
			soundPlayer.play(JPSounds.DEATH_ALERT);
		}
	}

	public static void notifyPlayersOfUndeath(ServerPlayer paramServerPlayer) {
		sendCommand(ServerConstants.COMMANDS.NOTIFY_UNDEATH + paramServerPlayer.getID());
	}

	// Strange present-tense name for a method..
	public static void sendingPrivateMessage(ServerPlayer paramServerPlayer) {
		sendCommand(ServerConstants.COMMANDS.PRIVATE_MESSAGE + paramServerPlayer.getID());
	}

	public static void setAnnouncement() {
		new JOptionPane();
		announcement = (String) JOptionPane.showInputDialog(null, "Enter announcement:", "Set Announcement...",
				JOptionPane.PLAIN_MESSAGE, null, null, announcement);
	}

	public static String getAnnouncement() {
		StringBuilder str1 = new StringBuilder();
		try (BufferedReader localBufferedReader = new BufferedReader(new FileReader("conf/announcement.txt"))) {
			String str2 = null;
			while ((str2 = localBufferedReader.readLine()) != null) {
				str1.append(ServerConstants.COMMANDS.GLOBAL_MESSAGE).append(str2).append("\n");
			}
		} catch (FileNotFoundException localFileNotFoundException) {
			logger.info("FileNotFoundException: Can not locate announcement.txt");
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return str1.toString();
	}

	public static void clearAnnouncement() {
		announcement = "";
	}

	/*
	 * Mute and unmute. Pretty explanitory, mutes or unmutes players, allowing them
	 * to talk.. or not
	 */
	public static void mute(String paramString) {
		sendCommand(ServerConstants.COMMANDS.MUTE_PLAYER + paramString);
		// TODO: MOVE mute functionality to the soundPlayer.
		if (soundIsOn && soundMenu.mutedUnmutedMenuItem.isSelected()) {
			soundPlayer.play(JPSounds.MUTED);
		}
	}

	public static void unmute(String paramString) {
		sendCommand(ServerConstants.COMMANDS.UNMUTE_PLAYER + paramString);
		// TODO: MOVE mute functionality to the soundPlayer.
		if (soundIsOn && soundMenu.mutedUnmutedMenuItem.isSelected()) {
			soundPlayer.play(JPSounds.UNMUTED);
		}
	}

	/*
	 * Freeze and Unfreeze players. Prevents certain (?) actions from being
	 * performed by players.
	 */
	public static void freezePlayers() {
		serverOptions.setFrozen(true);
		freezeButton.setText("Unfreeze");
		sendCommand(ServerConstants.COMMANDS.FREEZE_PLAYERS);
		for (final ServerPlayer troubleshooter : troubleshooters) {
			troubleshooter.statusPanel.freeze();
		}
		if (soundIsOn && soundMenu.freezeUnfreezeMenuItem.isSelected()) {
			soundPlayer.play(JPSounds.FREEZE);
		}
	}

	public static void unfreezePlayers() {
		serverOptions.setFrozen(false);
		freezeButton.setText("Freeze");
		sendCommand(ServerConstants.COMMANDS.UNFREEZE_PLAYERS);
		for (ServerPlayer troubleshooter : troubleshooters) {
			troubleshooter.statusPanel.unfreeze();
		}
		if (soundIsOn && soundMenu.freezeUnfreezeMenuItem.isSelected()) {
			soundPlayer.play(JPSounds.UNFREEZE);
		}
	}

	// TODO: Find out what this does
	public static String stripComments(String paramString) {
		StringBuilder localStringBuffer = new StringBuilder(paramString);
		for (int j = 0; j < localStringBuffer.length(); j++) {
			int i;
			if (localStringBuffer.charAt(j) == '/' && localStringBuffer.charAt(j + 1) == '*') {
				i = j + 2;
				while (i < localStringBuffer.length()
						&& (localStringBuffer.charAt(i) != '*' || localStringBuffer.charAt(i + 1) != '/')) {
					i++;
				}
				localStringBuffer.delete(j, i + 2);
			} else if (localStringBuffer.charAt(j) == '/' && localStringBuffer.charAt(j + 1) == '/') {
				i = j + 2;
				while (i < localStringBuffer.length() && localStringBuffer.charAt(i) != '\n') {
					i++;
				}
				localStringBuffer.delete(j, i);
			}
		}
		return localStringBuffer.toString();
	}

	public static void observerHasJoined(String paramString) {
		absoluteSpam("Observer " + paramString + " has joined.");
	}

	public static void observerHasLeft(String paramString) {
		absoluteSpam("Observer " + paramString + " has disconnected.");
	}

	public static void repaintMenus() {
		fsm.repaint();
		charsheetPanel.playerComboBox.repaint();
	}

	public static void reassignThreadNumbers() {
		for (int i = 0; i < chatThreads.size(); i++) {
			thisThread = chatThreads.get(i);
			thisThread.threadNumber = i;
		}
	}

	public static synchronized void startServer() {

		if (serverOptions.isRegisterGame() && gameDescription.equals(defaultGameDescription)) {
			setGameDescriptionMenuItem.doClick();
		}
		inputLine.setEnabled(true);
		inputLine.requestFocus();
		servSocketThread = new ServerSocketThread();
		servSocketThread.start();
		if (serverOptions.isRegisterGame()) {
			GameRegistrar.addGame(gameDescription);
			String str = GameRegistrar.getIP();
			if (!"fail".equals(str)) {
				ipLabel.setText("  IP: " + str);
				if (!serverOptions.isBehindRouter() && !str.equals(localIP.getHostAddress())) {
					serverOptions.setBehindRouter(true);
					JParanoia.warningMessage(ServerConstants.WARN_BEHIND_ROUTER,
							ServerConstants.getRouterWarning(localIP));
				}
			}
		}
		if (soundIsOn && soundMenu.connectedDisconnectedMenuItem.isSelected()) {
			soundPlayer.play(JPSounds.CONNECTED);
		}
	}

	public static void stopServer() {
		try {
			serverOptions.setServerRunning(false);
			servSocketThread.listening = false;
			servSocketThread.serverSocket.close();
			if (serverOptions.isGameRegistered()) {
				GameRegistrar.removeGame();
			}
			if (soundIsOn && soundMenu.connectedDisconnectedMenuItem.isSelected()) {
				soundPlayer.play(JPSounds.DISCONNECTED);
			}
		} catch (SocketException localSocketException) {
			logger.info("Socket Exception while closing serversocket: ");
			logger.warn(localSocketException.getMessage());
		} catch (IOException localIOException) {
			logger.info("I/O Exception while closing serversocket: ");
			logger.warn(localIOException.getMessage());
		}
	}

	public static void restoreFont(int i) {
		if (i == 0 || (i == numberOfPCs && serverOptions.isBigComputerFont())) {
			styleBegin = styleEnd = "";
			textAttributes.addAttribute(StyleConstants.FontConstants.Size, mainFontSize);
			if (!serverOptions.isFontIsBold()) {
				setFontBold(false);
			}
		}
	}

	public static void selectFont(int i) {
		if (i == numberOfPCs && serverOptions.isBigComputerFont()) {
			useComputerFont();
		}
		if (i == 0) {
			useGmFont();
		}
	}

}

/*
 * Location:
 * C:\Users\noahc\Desktop\JParanoia(1.31.1)\JParanoia(1.31.1).jar!\jparanoia\
 * server\JPServer.class Java compiler version: 3 (47.0) JD-Core Version: 0.7.1
 */

//Methods below this line are in the process of being phased out.
//-----------------------

/*
 * public static void setTitleMessage(String paramString) {
 * myTitle.setExtra(paramString);
 * frame.setTitle(ServerConstants.WELCOME_MESSAGE); spamString("013" +
 * paramString); }
 */

/*
 * public static void clearTitleMessage() { //TODO: Patch
 * //myTitle.clearExtra(); frame.setTitle(ServerConstants.WELCOME_MESSAGE);
 * sendCommand("013"); }
 */

// private static TitleClass myTitle = new TitleClass("JParanoia Community
// Server", ServerConstants.JPARANOIA_VERSION,false);

/*
 * //private static Socket someSock = null; //private static OutputStream
 * someOuputStream; //private static ThreadGroup chatThreadGroup = new
 * ThreadGroup( "my group of chat threads" ); //private static boolean jvm140 =
 * System.getProperty( "java.version" ).startsWith( "1.4.0" ); //private static
 * boolean quickNamesToggle = false; //private static boolean singleUseSpoof =
 * false; //private static String titleMessage = ""; //private static Container
 * contentPane; //private static JMenu fontFamilyMenu; //private static JMenu
 * fontSizeMenu; //private static JMenu killMenu; //private static JMenu
 * unkillMenu; //private static JMenu renameMenu; //private static JMenu
 * kickMenu; //private static JMenu combatMenu; //private static JMenuItem
 * combatMenuItem; //private static JMenuItem localIPMenuItem; //private static
 * JRadioButtonMenuItem whiteOnBlackButton; //private static
 * JRadioButtonMenuItem blackOnWhiteButton; //private static
 * JRadioButtonMenuItem serifButton; //private static JRadioButtonMenuItem
 * sansSerifButton; //private static JRadioButtonMenuItem monospacedButton;
 * //private static JRadioButtonMenuItem size10Button; //private static
 * JRadioButtonMenuItem size12Button; //private static JRadioButtonMenuItem
 * size14Button; //private static JRadioButtonMenuItem size16Button; //private
 * static JRadioButtonMenuItem size18Button; //private static
 * JRadioButtonMenuItem size24Button; //private static JCheckBoxMenuItem
 * autoScrollMenuItem; //private static JCheckBoxMenuItem
 * showTimeStampsMenuItem; //private static JCheckBoxMenuItem fontBoldMenuItem;
 * //private static JButton unfreezeButton; //private static JTable lipTable;
 * //private static Dimension lipTablePreferredSize = new Dimension( 130, 160 );
 * //private static SimpleAttributeSet spaceAttributes; //private static
 * String[] nonConnectedPlayers; //private static String addressToTry = null;
 * //private static KillMenuItem[] killMenuItemArray; //private static
 * UnkillMenuItem[] unkillMenuItemArray; //private static RenamePlayerMenuItem[]
 * renameMenuItemArray; //private static KickMenuItem[] kickMenuItemArray;
 */

/*
 * private static void clearInputLine() { inputLine.selectAll();
 * inputLine.replaceSelection(""); }
 */

/*
 * private static void globalPM() {
 * sendCommand(ServerConstants.COMMANDS.PRIVATE_MESSAGE+"999"); String str =
 * (String) JOptionPane.showInputDialog(null,
 * "Enter your global private message:", "Global PM", JOptionPane.PLAIN_MESSAGE,
 * null, null, null); if (str != null && !"".equals(str)) { for (int i = 1; i <
 * numberOfPCs; i++) { if (players[i].loggedIn) { players[i].specificSend("200"
 * + players[i].getID() + myPlayer.getID() + str); PMPane[i].addMyMessage(str);
 * } } } sendCommand(ServerConstants.COMMANDS.END_PRIVATE_MESSAGE); }
 */
