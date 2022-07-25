package ReversiAi;

/** 
    * object of type Move
    * A move can be validated or done
*/
public class Move {
    private int x;
    private int y;
    private Player player;
    private int choice; //for inversion (player number to switch) or bonus fields (20=bomb, 21=override stone) 
    private Double rated = -Double.MAX_VALUE; //atribute to store how promising a move is 

    
    /**
		* This function initializes the class move
	 	* @param x 
        * @param y 
        * @param player
	*/
    public Move(int pX, int pY, Player pPlayer){
        this.setX(pX);
        this.setY(pY);
        this.setPlayer(pPlayer);
    }

     /**
		* This function initializes the class move
	 	* @param x 
        * @param y 
        * @param player
        * @param choice
	*/
    public Move(int pX, int pY, Player pPlayer, int pChoice){
        x = pX;
        y = pY;
        player = pPlayer;
        choice = pChoice;
    }
    
    public int getX(){
        return this.x;
    }

    public void setX(int pX){
        this.x = pX;
    }

    public int getY(){
        return this.y;
    }

    public void setY(int pY){
        this.y = pY;
    }

    public Player getPlayer(){

        return this.player;
    }

    public void setPlayer(Player pPlayer){
        this.player = pPlayer;
    }

    public int getChoice(){
        return this.choice;
    }

    public void setChoice(int pChoice){
        this.choice = pChoice;
    }
    
    public Double getRated(){
        return this.rated;
    }
    public void setRated(double pRate){
       this.rated = pRate;
    }

    public String toString(){
        return "Player " + player.getPlayerNumber() + " sets a stone on tile " + x + ", " + y; 
    }
}
