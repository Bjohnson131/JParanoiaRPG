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

/* This class holds many of the strings that are in the game.
 * Eventually all of these strings should be dynamically loaded
 * into memory at runtime via a "language pack".
 * */
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

	//General messages for various purposes
	public static final String KICKED_BY_SERVER_MESSAGE = "( * Kicked by server * )";
	public static final String DUMMY_NPC_NAME = "spareNPC";
	public static final String GM_FAIL_PLAYER_KILL_ATTEMPT = "The GM has attempted to kill ";
	public static final String PLAYER_TURN_SKIPPED = " waived their right to a combat turn. (Not too bright)";
	public static final String PLAYER_DEAD_PREFIX = "(dead)";
	public static final String PLAYER_CONNECTION_ATTEMPT_MESSAGE = " has attempted to login!";
	public static final String PLAYER_DEATH_NO_CLONES = " has died and has no clones left! Oh, the humanity!!";
	
	//Constants related to the warning that the server is not on the same local IP as the clients
	public static final String WARN_BEHIND_ROUTER = "Behind a router";
	public static final String WARN_NO_IP = "Error: Unable to get local host address";
	private static final Pattern IP_REPLACE_REGEX = Pattern.compile("8IP8");
	private static final String ROUTER_WARNING_RAW = 
	        "The JParanoia Game Registry has determined\nthat your computer is behind a router."
	        + "\n\nPlayers should be able to connect without\nany problems if you have forwarded port 11777\n"
	        + "to your local IP. (8IP8)\n" + "\n"
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
	

	/* Invalid clone error message.
	 * Used by the ServerPlayer class to warn that the current player has an incorrect clearance.
	 * */
	public static final String INVALID_CLEARANCE_WARNING_TITLE = "Invalid clearance";
	public static final String INVALID_CLEARANCE_WARNING = "The character sheet " +"\n" +
            "attempts to grant a player an\n" +
            "invalid security clearance ";
	public static final String INVALID_CLEARANCE_WARNING_DESCRIPTION = 
			"\n\nAllowed clearance codes are:\n" +
            "(blank) = infrared\n" +
            "R = red\n" +
            "O = orange\n" +
            "Y = yellow\n" +
            "G = green\n" +
            "B = blue\n" +
            "I = indigo\n" +
            "V = violet\n" +
            "U = ultraviolet\n" +
            "\n";
	public static final String INVALID_CLEARANCE_REMEDY_SERVER = "Correct the error and relaunch the server.\n";
	public static final String INVALID_CLEARANCE_SEC_CLRNCE = "Security clearance " ;
	public static final String INVALID_CLEARANCE_INVALID = " is invalid. \n" ;
	
	
	/*Errors related to the player entering 'GM' as a name.
	 * */
	public static final String PLAYER_NAME_IS_GM_WARNING_TITLE = "Boring GM Name...";
	public static final String PLAYER_NAME_IS_GM_WARNING = "Your name, as defined in your own charsheet file, ";
	public static final String PLAYER_NAME_IS_GM_WARNING_DESCRIPTION = "\n" +
            "is \"GM\". This does not make you very unique. You can choose a name here\n" +
            "but it will be forgotten when you exit JParanoia. To choose a lasting name,\n" +
            "you must change the first line of your charsheet file.\n" +
            "Just be sure to leave the -0 on the end.\n" +
            "\n" +
            "(You can click Cancel to keep \"GM\" if you so choose.\n" +
            "To permanently surpress this notice, set bGmNameNag=false\n" +
            "in your jpConfig.ini file.)";
	
	/*Errors related to an incorrect name format.
	 * */
	public static final String PLAYER_NAME_ERROR_DESCRIPTION = 
            ".\n\n" +
            "Player names must consist of a first name,\n" +
            "a clearance initial, and a sector. (Clearance\n" +
            "initial may be omitted for infrared players.)\n" +
            "\n" +
            "Correct the error and relaunch the server.";
	
	/*A class to organize command codes
	 * At some point, the codes should be re-organized to B64
	 * */
	public static class COMMANDS {
		
		//JFrame trickery
		public static final String CLEAR_TITLE = "013";
		
		//Player muting perms
		public static final String UNMUTE_PLAYER = "050";
		public static final String MUTE_PLAYER = "051";
		public static final String FREEZE_PLAYERS = "052";
		public static final String UNFREEZE_PLAYERS = "053";
		
		//Player Death
		public static final String NOTIFY_DEATH = "060";
		public static final String NOTIFY_UNDEATH = "061";
		
		//Server info.
		public static final String SERVER_TERMINATION = "086";

		//Messaging
		public static final String GLOBAL_MESSAGE = "199";
		public static final String PRIVATE_MESSAGE = "210";
		public static final String END_PRIVATE_MESSAGE = "211";
		
		//Combat
		public static final String END_COMBAT  = "597";
		public static final String START_COMBAT_ROUND = "599"; //Unknown if correct.
		public static final String ABORT_COMBAT = "609";
	}
	
	
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
