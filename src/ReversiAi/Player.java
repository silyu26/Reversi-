package ReversiAi;
/**
    * This class represents the players
*/
public class Player {
    private int playerNumber;
    private int bombs;
    private int overrideStones;
    private int nextPlayerNumber;

    /**
		* This function initializes the class player
	 	* @param playernumber
	 	* @param number of bombs
		* @param number of override-stones
		* @param int next
	*/
    public Player(int pPlayerNumber, int pBombs, int pOverrideStones, int pNext){
        this.setPlayerNumber(pPlayerNumber);
        this.setBombs(pBombs);
        this.setOverrideStones(pOverrideStones);
        this.setNextPlayerNumber(pNext);
    }

    public int getPlayerNumber(){
        return playerNumber;
    }

    public void setPlayerNumber(int pNumber){
        this.playerNumber = pNumber;
    }

    public int getBombs(){
        return this.bombs;
    }

    public void setBombs(int pBombs){
        this.bombs = pBombs;
    }

    public int getOverrideStones(){
        return this.overrideStones;
    }

    public void setOverrideStones(int pOverrideStones){
        this.overrideStones = pOverrideStones;
    }

    public int getNextPlayerNumber(){
        return this.nextPlayerNumber;
    }

    public void setNextPlayerNumber(int pNumber){
        this.nextPlayerNumber = pNumber;
    }

    public void incBombs(){
        bombs++;
    }

    public void decBombs(){
        bombs--;
    }

    public void incOverride(){
        overrideStones++;
    }

    public void decOverride(){
        overrideStones--;
    }

    public String toString(){
        return "Player " + Integer.toString(playerNumber) + " has " + Integer.toString(bombs) + " bombs and " + Integer.toString(overrideStones) + " override stones.";
    }
}
