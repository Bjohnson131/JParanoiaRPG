package jparanoia.names;

import java.util.ArrayList;
import java.util.StringTokenizer;

import jparanoia.server.ServerPlayer;

public class NameManager {    

    private ArrayList<ServerPlayer> sortedNames = new ArrayList<ServerPlayer>( 8 );
    private String lastNameCompleted = "";
    private int previousKey = 0;
    private int thisKey = 100;
    private int lastCompletionPlayer = 99;
    
    public NameManager() {
    	
    }
    
    public int playerCount() {
    	return sortedNames.size();
    }
    
    public ServerPlayer getPlayerAt( int i ) {
    	return sortedNames.get(i);
    }
    
    public void addPlayer(ServerPlayer name) {
    	sortedNames.add(name);
    }
    
    public void removeAtIndex(int index) {
    	sortedNames.remove(index);
    }
    
    public boolean removePlayer(String playerName) {
    	for(ServerPlayer player : sortedNames) {
    		if( player.getName() == playerName) {
    			sortedNames.remove(player);
    			return true;
    		}
    	}
    	return false;
    }
    
    
    public String completeName(String paramString, boolean paramBoolean) {
		String str1 = "";
		StringBuilder str2 = new StringBuilder();
		if (paramBoolean) {
			paramString = paramString.substring(0, paramString.length() - lastNameCompleted.length() + 1);
		}
		StringTokenizer st = new StringTokenizer(paramString);
		str1 = st.nextToken();
		while (st.hasMoreTokens()) {
			str2.append(str1).append(" ");
			str1 = st.nextToken();
		}
		if (paramBoolean) {
			if (lastCompletionPlayer == sortedNames.size() - 1) {
				lastCompletionPlayer = 0;
				lastNameCompleted = ((ServerPlayer) sortedNames.get(lastCompletionPlayer)).getName();
				return str2 + lastNameCompleted;
			}
			lastNameCompleted = ((ServerPlayer) sortedNames.get(++lastCompletionPlayer)).getName();
			return str2 + lastNameCompleted;
		}
		for (int i = 0; i < sortedNames.size()
				&& str1.compareToIgnoreCase(((ServerPlayer) sortedNames.get(i)).getName()) > 0; i++) {
			if (i < sortedNames.size()) {
				lastCompletionPlayer = i;
			} else {
				lastCompletionPlayer = sortedNames.size() - 1;
			}
			lastNameCompleted = ((ServerPlayer) sortedNames.get(lastCompletionPlayer)).getName();
		}
		return str2 + lastNameCompleted;
	}

	public void addPlayerAtIndex(ServerPlayer toReplace, int index) {
    	sortedNames.add(index,toReplace);
	}
}
