package jparanoia.server.manager;

import java.net.InetAddress;

import jparanoia.server.ServerOptions;
import jparanoia.server.ServerSocketThread;
import jparanoia.server.constants.ServerConstants;
import jparanoia.shared.GameRegistrar;
import jparanoia.shared.JPSounds;
import jparanoia.shared.JParanoia;

public class StateManager {
	
	public static final class ServerStarter implements Runnable{
		ServerOptions opts;
		InetAddress localIP;
		public ServerStarter(ServerOptions opts, InetAddress thisIp) {
			this.opts = opts;
			this.localIP = thisIp;
		}
		
		@Override
		public void run() {
			if (opts.isRegisterGame() && gameDescription.equals(defaultGameDescription)) {
				serverMenu.setGameDescriptionMenuItem.doClick();
			}
			inputLine.setEnabled(true);
			inputLine.requestFocus();
			servSocketThread = new ServerSocketThread();
			servSocketThread.start();
			if (opts.isRegisterGame()) {
				GameRegistrar.addGame(gameDescription);
				String str = GameRegistrar.getIP();
				if (!"fail".equals(str)) {
					ipLabel.setText("  IP: " + str);
					if (!opts.isBehindRouter() && !str.equals(localIP.getHostAddress())) {
						opts.setBehindRouter(true);
						
						JParanoia.warningMessage(ServerConstants.WARN_BEHIND_ROUTER,
								ServerConstants.getRouterWarning(localIP));
					}
				}
			}
			//TODO: Move mute functionality to soundPlayer
			if (soundIsOn && soundMenu.connectedDisconnectedMenuItem.isSelected()) {
				soundPlayer.play(JPSounds.CONNECTED);
			}
		}
	}

}
