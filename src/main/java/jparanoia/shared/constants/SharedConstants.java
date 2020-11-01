package jparanoia.shared.constants;

import jparanoia.client.JPClient;
import jparanoia.server.constants.ServerConstants;

public final class SharedConstants {

	public static final String SELECTOR_TITLE_BAR          = "Launch JParanoia Community Edition";
	public static final String SERVER_VERSION_ANNOUNCEMENT = "JPSERVER VERSION: "+ ServerConstants.JPARANOIA_VERSION;
	public static final String CLIENT_VERSION_ANNOUNCEMENT = "JPCLIENT VERSION: " + JPClient.getVersionName();
	
	public static final int SELECTOR_WINDOW_WIDTH   = 610;
	public static final int SELECTOR_WINDOW_HEIGHT  = 360;
	
	
	private SharedConstants(){
		
	}
}
