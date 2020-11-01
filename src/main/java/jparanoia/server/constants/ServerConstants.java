package jparanoia.server.constants;

import static java.awt.Color.cyan;
import static java.awt.Color.green;
import static java.awt.Color.orange;
import static java.awt.Color.red;
import static java.awt.Color.white;
import static java.awt.Color.yellow;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jparanoia.shared.JPVersionNumber;
import jparanoia.shared.JParanoia;

public final class ServerConstants {
	
	public static final String MY_PLAYER_ID = "00";
	public static final int MY_PLAYER_NUMBER = 0;
	
	//Version and patch notes contents
	public static final JPVersionNumber VERSION_NUMBER = new JPVersionNumber( 1, 31, 4 );
	public static final JPVersionNumber MIN_COMPATIBLE_VERSION_NUMBER = new JPVersionNumber( 1, 31, 4 );
	public static final String JPARANOIA_VERSION = VERSION_NUMBER.toString();
	public static final String WELCOME_MESSAGE = "JParanoia Community Server " + ServerConstants.JPARANOIA_VERSION;
	public static final String JPARANOIA_WEBSITE_LINK = "https://github.com/ndo360/JParanoiaRPG";
	public static final String DISCLAIMER = 
			"This is an official community edition for JParanoia.\n"
			+ "We take no credit for the original creation of this program.\n"
			+ "Our only goal is to allow for people to play on this classic program once again.\n\n";
	public static final String PATCH_NOTES_INTRODUCTORY = "New in this community release:\n\n";
	public static final String PATCH_NOTES = 
			"- SOUND IS NOW FUNCTIONAL!! MAKE SURE TO UNMUTE PROGRAM.\n"
			+ "- Replaced default PreGens, with 'classic' RED CLEARANCE\n"
			+ "- PreGens From PARANOIA XP's 'Crash Priority' supplement.\n\n";
	
	//Window and display constants
	public static final int IDEAL_WIDTH = 770;
	public static final int IDEAL_HEIGHT = 540;
	
	//Constants related to the warning that the server is not on the same local IP as the clients
	public static final String WARN_BEHIND_ROUTER = "Behind a router";
	public static final String WARN_NO_IP = "Error: Unable to get local host address";
	private static final Pattern IP_REPLACE_REGEX = Pattern.compile("{IP}");
	private static final String ROUTER_WARNING_RAW = 
	        "The JParanoia Game Registry has determined\nthat your computer is behind a router."
	        + "\n\nPlayers should be able to connect without\nany problems if you have forwarded port 11777\n"
	        + "to your local IP. ({IP})\n" + "\n"
			+ "Consult the included README for details on\n"
			+ "running a server from behind a router.\n" + "\n"
			+ "This notice will only appear once each time\n"
			+ "you run this program. To disable this reminder\n"
			+ "completely, edit the jpConfig.ini file such that\n" + "BehindRouter=true";
	private static final Matcher ROUTER_WARNING_MATCHER = IP_REPLACE_REGEX.matcher(ROUTER_WARNING_RAW);
	
	
	//Death messages
	public static final String[] DEATH_MESSAGES = {
			"has gone to Great Alpha Complex in the Sky.",
			"has kicked the synthe-bucket.",
			"has been visited by the reaper-bot.",
			"has shuffled off this mortal sector.",
			"has got bored of his present clone.",
			"has bought the highly-treasonous farm.",
			"has drunk his last bottle of Bouncy Bubble Beverage.",
			"has eaten his last Hot Fun.",
			"is pushing up traitorous daises.",
			"has passed onto a better sector.",
			"has exited, vid-stage left.",
			"is, alas, no more.",
			"has passed away.",
			"has ceased to show any signs of life.",
			"has cashed in his credits."
	};
	
	public static final String INVALID_CLONE_WARNING_DESCRIPTION = 
			"\".\n\nAllowed clearance codes are:\n" +
            "(blank) = infrared\n" +
            "R = red\n" +
            "O = orange\n" +
            "Y = yellow\n" +
            "G = green\n" +
            "B = blue\n" +
            "I = indigo\n" +
            "V = violet\n" +
            "U = ultraviolet\n" +
            "\n" +
            "Correct the error and relaunch the server.";
	public static final String INVALID_CLONE_WARNING = "";
	
	
	
	
	
	private ServerConstants() {
	}
	
	public static String getRouterWarning(InetAddress localIP) {
		return ROUTER_WARNING_MATCHER.replaceAll(localIP.getHostAddress());
	}
	
	public static void writeIntroduction() {
		JParanoia.displayWrite(green, WELCOME_MESSAGE);
		JParanoia.displayWrite(orange, JPARANOIA_WEBSITE_LINK);
		JParanoia.displayWrite(red, DISCLAIMER);
		JParanoia.displayWrite(cyan,  PATCH_NOTES_INTRODUCTORY);
		JParanoia.displayWrite(white, PATCH_NOTES);
		JParanoia.displayWrite(yellow,"READ THE README.\n");
	}
}
