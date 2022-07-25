package ReversiAi;
import java.util.*;

/**
 * Datastructure for modified Reversi map.
 * Array of arrays
 * Contains objects of class: Tile
 */
public class Map {
    private Tile[][] mapState;
    private int height;
    private int width;
    private ArrayList<Tile> emptyWithNeighbour; //saves each tile where possibly a stone can be placed
    private ArrayList<Tile> occupiedTiles; //saves each player tile where an override stone can be placed
    public Player[] players;
    private int numberOfPlayers;
    private Player currentPlayer;
    private double[] ranking ={0, 0, 0, 0, 0, 0, 0, 0, 0};
    private Tile.TileCoordinateComparator tileComparator = new Tile.TileCoordinateComparator();
    private int inversions = 0;
    private int choice = 0;
    private int playableTiles;
    private int nonHole;
    private int securedTiles = 0;
    
    /**
		* This function initializes the class map
	 	* @param height
        * @param width
        * @param number of players
        * @param array of players
        * @param current player
	*/
    public Map(int pHeight, int pWidth, int pNumPlayers, Player[] pPlayers, Player pCurrentPlayer){ 
        this.setHeight(pHeight);
        this.setWidth(pWidth);
        this.setMapState(new Tile[width][height]);
        this.setPlayers(pPlayers);
        this.setNumberOfPlayers(pNumPlayers);
        this.setCurrentPlayer(pCurrentPlayer);
        emptyWithNeighbour = new ArrayList<Tile>();
        occupiedTiles = new ArrayList<Tile>();
    }

    public Map(int pHeight, int pWidth, int pNumPlayers, Player[] pPlayers, Player pCurrentPlayer, int pEmpty, int pOccupied){ 
        this.setHeight(pHeight);
        this.setWidth(pWidth);
        this.setMapState(new Tile[width][height]);
        this.setPlayers(pPlayers);
        this.setNumberOfPlayers(pNumPlayers);
        this.setCurrentPlayer(pCurrentPlayer);
        emptyWithNeighbour = new ArrayList<Tile>(pEmpty);
        occupiedTiles = new ArrayList<Tile>(pOccupied);
    }

    public Tile[][] getMap(){
        return this.mapState;
    }

    public void setMapState(Tile[][] pMapState){
        this.mapState = pMapState;
    }

    public int getHeight(){
        return this.height;
    }

    public void setHeight(int pHeight){
        this.height = pHeight;
    }

    public int getWidth(){
        return this.width;
    }

    public void setWidth(int pWidth){
        this.width = pWidth;
    }

    public Player[] getPlayers(){
        return this.players;
    }

    public void setPlayers(Player[] pPlayer){
        this.players = pPlayer;
    }

    public Player getPlayer(int i){
        return this.players[i];
    }

    public void setPlayer(int i, Player pPlayer){
        this.players[i] = pPlayer;
    }

    public int getNumberOfPlayers(){
        return this.numberOfPlayers;
    }
    
    public void setNumberOfPlayers(int pNumberOfPlayers){
        this.numberOfPlayers = pNumberOfPlayers;
    }

    public Player getCurrentPlayer(){
        return this.currentPlayer;
    }

    public void setCurrentPlayer(Player pCurrentPlayer){
        this.currentPlayer = pCurrentPlayer;
    }

    public double[] getRanking(){
        return this.ranking;
    }

    public void setRanking(double[] pRanking){
        this.ranking = pRanking;
    }
    
    public void addTileToRanking(int playerNr){
        ranking[playerNr]++;
    }

    public void subTileFromRanking(int playerNr){
        ranking[playerNr]--;
    }

    public void raiseInversions(){
        inversions++;
    } 
    
    public void lowerInversions(){
        inversions--;
    } 

    public int getInversions(){
        return inversions;
    }

    public void raiseChoice(){
        choice++;
    } 

    public void lowerChoice(){
        choice--;
    } 

    public int getChoice(){
        return choice;
    }
    public void setPlayableTiles(int t){
        playableTiles = t;
    }

    public void raisePlayableTiles(){
        playableTiles++;
    }

    public void lowerPlayableTiles(){
        playableTiles++;
    }
    
    public int getPlayableTiles(){
        return playableTiles;
    }

    public void raiseSecuredTiles(){
        securedTiles++;
    }

    public int getSecuredTiles(){
        return securedTiles;
    }

    public int getNonHole(){
        return nonHole;
    }

    public void setNonHole(int pNew){
        nonHole = pNew;
    }
    public ArrayList<Tile> getEmptyWithNeighbour(){
        return emptyWithNeighbour;
    }
    public ArrayList<Tile> getOccupiedTiles(){
        return occupiedTiles;
    }
    public Tile.TileCoordinateComparator getTileComparator(){
        return tileComparator;
    }

    /**
		* This function returns the file at a given position. Indices out of bounds return an InvalidTile object
	 	* @param x 
        * @param y 
        * @return tile
	*/
    public Tile readField(int pX , int pY){ 
        if(pX >= width || pY >= height|| pX < 0 || pY < 0){
            return new Tile(51 , 51, TileTypes.INVALID);
        }
        return mapState[pX][pY];
    }

     /**
		* This function places the given Tile on the indicated field on the map
	 	* @param x 
        * @param y 
	*/
    public void writeField(int pX , int pY, Tile pTile){ 
        mapState[pX][pY] = pTile;
    }

    /**
		* This function returns the neighbour of the given tile in the given direction
	 	* @param x 
        * @param y 
        * @param direction
        * @return tile
	*/
    public Tile getNeighbour(int pX, int pY, int pDirection){
        int[] coord = readField(pX, pY).getNeighbourCoordinates(pDirection);
        if(coord != null){
            return readField(coord[0], coord[1]);
        }
        switch(pDirection){
            case 0: 
                return readField(pX,pY-1);
            case 1: 
                return readField(pX+1,pY-1);
            case 2: 
                return readField(pX+1,pY);
            case 3: 
                return readField(pX+1,pY+1);
            case 4: 
                return readField(pX,pY+1);
            case 5: 
                return readField(pX-1,pY+1);
            case 6: 
                return readField(pX-1,pY);
            case 7: 
                return readField(pX-1,pY-1);
            default: 
                return new Tile(pX, pY, TileTypes.INVALID);
        }
    }
    
    /**
		* This function disqualifies player by deleting his number from the nextPlayerNumbers
        * looks for the player before the disqualified one and sets his nextPlayerNumber to
        * the player after the disqualified
	 	* @param number of disqualified player 
	*/
    public void disqualifyPlayer(int dis){
        setCurrentPlayer(getPlayer(getPlayer(dis).getNextPlayerNumber()));
        for(int i = 1; i < players.length; i++){
            if(players[i].getNextPlayerNumber() == dis){
                players[i].setNextPlayerNumber(players[dis].getNextPlayerNumber());
            }
        }
    }

    /**
		* This function removes the given tile from teh emptyWithNeighbour arrayliost
	 	* @param number of disqualified player 
	*/
    public void removeFromEmptyWN(Tile tile){
        int index = Collections.binarySearch(emptyWithNeighbour, tile, tileComparator);
        if(index >= 0){
            emptyWithNeighbour.remove(index);
        }
    }

    //called at the beginning of phase 2, we don't need the arrayLists anymore so they can be deleted
    public void initPhase2(){
        emptyWithNeighbour = null;
        occupiedTiles = null;
    }

   
    /**
		* This function changes the current game in a string
	*/
    public String toString(){
        String out = "";
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                if(readField(x, y).getType()==TileTypes.INVALID || readField(x, y).getType()==TileTypes.BOMBHOLE){
                    out += "-";
                } else if(readField(x, y).getType()==TileTypes.PLAYER){
                    if(mapState[x][y].getPlayerNumber() == 0){
                        out += "x";
                    } else {
                        out += mapState[x][y].getPlayerNumber();
                    }
                } else if(readField(x, y).getType()==TileTypes.CHOICE){
                    out += "c";
                } else if(readField(x, y).getType()==TileTypes.INVERSION){
                    out += "i";
                } else if(readField(x, y).getType()==TileTypes.BONUS){
                    out += "b";
                } else if(readField(x, y).getType()==TileTypes.EMPTY){
                    out += "0";
                } else {
                    out += mapState[x][y].getType().name();
                }
            }
            out += "\n";
        }
        return out;
    }

    /**
		* This function creates a copy of the map 
	 	* @param phase
        * @return map
	*/
    public Map copyMap(int phase){
        Player[] newPlayers = new Player[players.length];
        for (int i = 1; i < players.length;i++){
            newPlayers[i] = new Player(i, players[i].getBombs(), players[i].getOverrideStones(), players[i].getNextPlayerNumber());
        }
        Map map;
        if(phase == 1){
            map = new Map(height, width,  numberOfPlayers, newPlayers, newPlayers[this.currentPlayer.getPlayerNumber()], emptyWithNeighbour.size(), occupiedTiles.size());
        } else {
            map = new Map(height, width,  numberOfPlayers, newPlayers, newPlayers[this.currentPlayer.getPlayerNumber()]);
        }
        map.ranking = new double[9];
        for(int i = 1; i < 9; i++){
            map.ranking[i] = ranking[i];
        }
        for(int h = 0; h < height; h++){
            for (int w = 0; w < width; w++){
                map.writeField(w, h, readField(w, h).copy());
            }
        }
        map.choice = this.getChoice();
        map.inversions = this.getInversions();
        map.nonHole = this.getNonHole();
        if(phase == 1){
            for (Tile tile : occupiedTiles){
                map.occupiedTiles.add(map.readField(tile.getX(), tile.getY()));
            }
            for (Tile tile : emptyWithNeighbour) {
                map.emptyWithNeighbour.add(map.readField(tile.getX(), tile.getY()));
            }
        }   
        return map;
    } 

    
    /**
		* This function inserts the given tile in the given arraylist, but only if it is not already in it
	 	* @param arraylist al
        * @param tile
	*/
    public void insert(ArrayList<Tile> al, Tile tile){
        int index = Collections.binarySearch(al, tile, tileComparator);
        if (index < 0) {
            index = -index - 1;
            al.add(index, tile);
        }
    }
    /**
		* This function decides whether we own a stone next to a special tile.
	 	* @param int pOurNr
        * @return boolean
	*/
    public boolean nextToSpecial(int pOurNr){
        for(Tile t : occupiedTiles){
            if(t.getPlayerNumber() == pOurNr){
                Tile temp;
                for(int i = 0; i < 8; i++){
                    temp = this.getNeighbour(t.getX(), t.getY(), i);
                    if(temp.getType()==TileTypes.BONUS || temp.getType()==TileTypes.CHOICE){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
		* This function decides whether we own a stone two tiles away from a special tile.
	 	* @param int pOurNr
        * @return boolean
	*/
    public boolean twoToSpecial(int pOurNr){
        for(Tile t : occupiedTiles){
            if(t.getPlayerNumber() == pOurNr){
                Tile dist1;
                Tile dist2;
                for(int j = 0; j < 8; j++){
                    dist1 = this.getNeighbour(t.getX(), t.getY(), j);
                    for(int i = 0; i < 8; i++){
                        dist2 = this.getNeighbour(dist1.getX(), dist1.getY(), i);
                        if(dist2.getType()==TileTypes.CHOICE || dist2.getType()==TileTypes.BONUS){
                            return true;
                        }
                    }
                }
                
            }
        }
        return false;
    }

    /**
		* This function decides whether we have a tile occupied next to an inversion.
	 	* @param int pOurNr
        * @return boolean
	*/
    public boolean inversionInReach(int pOurNr){
        for(Tile t : occupiedTiles){
            if(t.getPlayerNumber() == pOurNr){
                Tile temp;
                for(int i = 0; i < 8; i++){
                    temp = this.getNeighbour(t.getX(), t.getY(), i);
                    if(temp.getType()==TileTypes.INVERSION){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasEmptyNeighbour(int x, int y){
        Tile neighbour;
        for(int i = 0; i < 8 ; i++){
            neighbour = getNeighbour(x, y, i);
            if(neighbour.getType() == TileTypes.EMPTY){
                return true;
            }
        }
        return false;
    }
}
