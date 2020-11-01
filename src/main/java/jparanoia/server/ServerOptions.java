package jparanoia.server;

public class ServerOptions {

	private boolean allowGMEmotes = true;
	private boolean behindRouter;
	private boolean bigComputerFont = true;
	private boolean computerAllCaps;
	private boolean fontIsBold;
	private boolean frozen;
	private boolean gameRegistered;
	private boolean htmlLog = true;
	private boolean isPXPGame;
	private boolean keepLog;
	private boolean registerGame;
	private boolean sendMainText = true;
	private boolean serverRunning;
	private boolean allowObservers;
	private boolean clobberAqua;
	private boolean showTimeStamps = true;
	private boolean gmNameNag;
	private int computerFontIncrease;
	private int maxNumClones = 5;

	// An instance of a set of server options.
	public ServerOptions() {
		// empty for now.
	}

	public boolean isAllowGMEmotes() {
		return allowGMEmotes;
	}

	public void setAllowGMEmotes(boolean allowGMEmotes) {
		this.allowGMEmotes = allowGMEmotes;
	}

	public boolean isBehindRouter() {
		return behindRouter;
	}

	public void setBehindRouter(boolean behindRouter) {
		this.behindRouter = behindRouter;
	}

	public boolean isBigComputerFont() {
		return bigComputerFont;
	}

	public void setBigComputerFont(boolean bigComputerFont) {
		this.bigComputerFont = bigComputerFont;
	}

	public boolean isComputerAllCaps() {
		return computerAllCaps;
	}

	public void setComputerAllCaps(boolean computerAllCaps) {
		this.computerAllCaps = computerAllCaps;
	}

	public boolean isFontIsBold() {
		return fontIsBold;
	}

	public void setFontIsBold(boolean fontIsBold) {
		this.fontIsBold = fontIsBold;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	public boolean isGameRegistered() {
		return gameRegistered;
	}

	public void setGameRegistered(boolean gameRegistered) {
		this.gameRegistered = gameRegistered;
	}

	public boolean isHtmlLog() {
		return htmlLog;
	}

	public void setHtmlLog(boolean htmlLog) {
		this.htmlLog = htmlLog;
	}

	public boolean isPXPGame() {
		return isPXPGame;
	}

	public void setPXPGame(boolean isPXPGame) {
		this.isPXPGame = isPXPGame;
	}

	public boolean isKeepLog() {
		return keepLog;
	}

	public void setKeepLog(boolean keepLog) {
		this.keepLog = keepLog;
	}

	public boolean isRegisterGame() {
		return registerGame;
	}

	public void setRegisterGame(boolean registerGame) {
		this.registerGame = registerGame;
	}

	public boolean isSendMainText() {
		return sendMainText;
	}

	public void setSendMainText(boolean sendMainText) {
		this.sendMainText = sendMainText;
	}

	public boolean isServerRunning() {
		return serverRunning;
	}

	public void setServerRunning(boolean serverRunning) {
		this.serverRunning = serverRunning;
	}

	public boolean isShowTimeStamps() {
		return showTimeStamps;
	}

	public void setShowTimeStamps(boolean showTimeStamps) {
		this.showTimeStamps = showTimeStamps;
	}

	public int getMaxNumClones() {
		return maxNumClones;
	}

	public void setMaxNumClones(int maxNumClones) {
		this.maxNumClones = maxNumClones;
	}

	public boolean isAllowObservers() {
		return allowObservers;
	}

	public void setAllowObservers(boolean allowObservers) {
		this.allowObservers = allowObservers;
	}

	public boolean isClobberAqua() {
		return clobberAqua;
	}

	public void setClobberAqua(boolean clobberAqua) {
		this.clobberAqua = clobberAqua;
	}

	public boolean isGmNameNag() {
		return gmNameNag;
	}

	public void setGmNameNag(boolean gmNameNag) {
		this.gmNameNag = gmNameNag;
	}

	public int getComputerFontIncrease() {
		return computerFontIncrease;
	}

	public void setComputerFontIncrease(int computerFontIncrease) {
		this.computerFontIncrease = computerFontIncrease;
	}

}
