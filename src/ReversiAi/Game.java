package ReversiAi;
//import java.io.*;
import java.util.*;

/**
	* This class represents the calculation parts of the game
*/
public class Game {
    public Map currentMap;    
    public int phase; // 1 = building phase, 2 = bomb phase
    private int ourNr; /*our player number */
    private int numberOfPlayers;
    private int bombStrength;
    private boolean sort; // the sorting algorithm for alpha/beta can be switched of by flipping this to false

    //method variables
    private boolean useOverrides = false;
    private long goalTime;
    private boolean abortSearch = false;
    private float numberOfFactors;
    private float averageBranchingFactor;
    private float numberOfStates; //for iterative deepening alpha beta
    private double saveAlpha;
    private double saveMinimax;
    private boolean bench = false;// decides if benchmark data is collected
    private int[] statesVisited = {0,0,0}; //counter for benching and iterative deepening, {minimax, alpha-beta unsorted, alpha-beta sorted}
    private double abRate;
    private double absRate;
    private double mMRate;
    private static double choiceWorth = 8;
    private static double bombWorth = 2;
    private static double overrideWorth = 50;
    private static double inversionWorth = 5;
    private static double moveWorth = 1;
    private static double secureWorth = 5;
    private String heuristic = "EARLY";
    private double aspUpperBound = Double.MAX_VALUE;
    private double aspLowerBound = -Double.MAX_VALUE;
    private boolean useAspirationWindow = true;
    private long bufferTime;
    private Queue<Double> turned = new LinkedList<Double>();
    private int lastNrOfMoves = 10;
    
    /**
		* This function initializes the class game
	 	* @param map
	 	* @param bombStrength
		* @param ourNr
		* @param numberOfPlayers
	*/
    public Game(Map pMap, int pBombStrength, int pOurNr, int pNumberOfPlayers){
        this.setCurrentMap(pMap);
        this.setPhase(1);
        this.setBombStrength(pBombStrength);
        this.setOurNr(pOurNr);
        this.setNumberOfPlayer(pNumberOfPlayers);
    }

    /**
		* This function initializes the class game
	 	* @param map
	 	* @param bombStrength
		* @param numberOfPlayers
	*/
    public Game(Map pMap, int pBombStrength, int pNumPlayers){
        this.setCurrentMap(pMap);
        this.setPhase(1);
        this.setBombStrength(pBombStrength);
        this.setNumberOfPlayer(pNumPlayers);
        if(pMap.getHeight() >= 40 || pMap.getWidth() >= 40){
            bufferTime = 300;
            sort = false;
        } else if(pMap.getHeight() >= 25 || pMap.getWidth() >= 25){
            bufferTime = 150;
            sort = true;
        } else {
            bufferTime = 100;
            sort = true;
        }
        System.out.println("Time limit: " + bufferTime);
        System.out.println("sort: " + sort);
    }

    public Map getCurrentMap(){
        return this.currentMap;
    }

    public void setCurrentMap(Map pMap){
        this.currentMap = pMap;
    }

    public int getPhase(){
        return this.phase;
    }

    public void setPhase(int pPhase){
        this.phase = pPhase;
    }

    public int getOurNr() {
        return this.ourNr;
    }

    public void setOurNr(byte pNr) {
        this.ourNr = pNr;
    }
    public void setOurNr(int pNr) {
        this.ourNr = pNr;
    }
    public int getNumberOfPlayer(){
        return this.numberOfPlayers;
    }

    public void setNumberOfPlayer(int pNumberOfPlayer){
        this.numberOfPlayers = pNumberOfPlayer;
    }

    public int getBombStrength(){
        return this.bombStrength;
    }

    public void setBombStrength(int pStrength){
        this.bombStrength = pStrength;
    }

    public boolean getSort() {
        return this.sort;
    }

    public void setSort(boolean pSort) {
        this.sort = pSort;
    }

    public boolean getUseOverrides(){
        return this.useOverrides;
    }

    public void setUseOverrisdes(boolean pUse){
        this.useOverrides = pUse;
    }

    public long getGoalTime(){
        return this.goalTime;
    }

    public void setGoalTime(long pTime){
        this.goalTime = pTime;
    }

    public boolean getAbortSearch(){
        return this.abortSearch;
    }

    public void setAbortSearch(boolean pAbortSearch){
        this.abortSearch = pAbortSearch;
    }
    
    public float getNumberOfFactors(){
        return this.numberOfFactors;
    }

    public void setNumberOfFactors(float pFactors){
        this.numberOfFactors = pFactors;
    }
    
    public float getAverageBranchingFactor(){
        return this.averageBranchingFactor;
    }

    public void setAverageBranchingFactor(float pFactor){
        this.averageBranchingFactor = pFactor;
    }

    public double getSaveAlpha(){
        return this.saveAlpha;
    }

    public void setSaveAlpha(double pAlpha){
        this.saveAlpha = pAlpha;
    }

    public double getMinimax(){
        return this.saveMinimax;
    }

    public void setMinimax(double pMinimax){
        this.saveMinimax = pMinimax;
    }

    public boolean getBench(){
        return this.bench;
    }

    public void setBench(boolean pBench) {
        this.bench = pBench;
    }

    public int[] getStatesVisited(){
        return this.statesVisited;
    }

    public void setStatesVisited(int[] pStates){
        this.statesVisited = pStates;
    }

    public double getAbRate(){
        return this.abRate;
    }

    public void setAbRate(double pRate){
        this.abRate = pRate;
    }

    public double getAbSRate(){
        return this.absRate;
    }

    public void setAbsRate(double pRate){
        this.absRate = pRate;
    }

    public double getMMRate(){
        return this.mMRate;
    }

    public void setMMRate(double pRate){
        this.mMRate = pRate;
    }
    
    public void setUseAspirationWindow(boolean use){
        useAspirationWindow = use;
    }
    public String getHeuristic(){
        return heuristic;
    }

    public void setHeuristic(String pH){
        heuristic=pH;
    }

    public Queue<Double> getTurned(){
        return turned;
    }
    public void setTurned(Queue<Double> q){
        turned = q;
    }
    
    
    /**
		* This function decides if a given move is valid on the given playing field
	 	* @param map
	 	* @param move
        * @return boolean if move is valid
	*/
    public boolean moveValidator(Move pMove, Map pMap){
        Tile tileContent = pMap.readField(pMove.getX(), pMove.getY());
        if( tileContent.getType() == TileTypes.INVALID || tileContent.getType() == TileTypes.BOMBHOLE){
            return false;
        }
        if(phase == 2){
            return (pMap.getPlayer(pMove.getPlayer().getPlayerNumber()).getBombs() > 0);
        }
        if(tileContent.getType() == TileTypes.PLAYER){
            if(tileContent.getPlayerNumber() == 0 && pMove.getPlayer().getOverrideStones()>0){ //expansion rule
                    return true;
            }
            if(pMove.getPlayer().getOverrideStones()<1){
                return false;
            }
        }
        int direction;
        int tempDir;
        for(int i = 0; i < 8; i++){
            direction = i;
            Tile curr = pMap.readField(pMove.getX(), pMove.getY());
            int path = 0;
            while(true){
                path++;
                //if we took an extra transition: change direction accordingly
                if(curr.getNeighbourCoordinates(direction) != null){
                    tempDir = (curr.getNeighbourCoordinates(direction)[2]+4)%8;
                    curr = pMap.getNeighbour(curr.getX(), curr.getY(), direction);
                    direction = tempDir;
                } else {
                    curr = pMap.getNeighbour(curr.getX(), curr.getY(), direction);
                }
                if(!(curr.getType()==TileTypes.PLAYER)){
                    break;
                }
                //checks whether we already reached this tile so we don't run into circles
                if(curr.getX() == pMove.getX() && curr.getY() == pMove.getY()){
                    break;
                }
                if(curr.getPlayerNumber() == pMove.getPlayer().getPlayerNumber()){
                    //if path == 1 it means that our stone we found is the direct neighbour and then we do not want to use this
                    if(path == 1){
                        break;
                    } else {
                        return true;
                    }  
                }
            }                  
        }
        return false;
    }
    
     /**
		* This function is a recursive help function for the bomb phase, 
        * using limited depth-first search for finding all Tiles within distance of bombStrength and bomb them
	 	* @param map
	 	* @param x
        * @param y
        * @param distance
	*/
    public void succMapBombDFS(Map pMap, int pX, int pY, int pDistance){
        Tile moveTile = pMap.readField(pX, pY);
        if(moveTile.getType() == TileTypes.INVALID){
            return;
        }
        if(pDistance == 0){
            moveTile.setType(TileTypes.BOMBHOLE);
            return;
        }
        moveTile.setType(TileTypes.BOMBHOLE);
        Stack<Tile> stack = new Stack<Tile>();
        for(int i=0; i<8; i++){
            stack.add(pMap.getNeighbour(pX, pY, i));
        }
        for(Tile t : stack){
            succMapBombDFS(pMap, t.getX(), t.getY(), pDistance-1);
        }

    }

    /**
		* This function returns the resulting map from the given move and map. It does not alter the given map.
	 	* @param move
	 	* @param map
        * @return map
	*/
    public Map succMap(Move pMove, Map pMap){
        Map editMap = pMap.copyMap(phase);   //copies map
        Tile moveTile = editMap.readField(pMove.getX(), pMove.getY());
        if(phase == 2){ //bomb phase
            editMap.getPlayer(pMove.getPlayer().getPlayerNumber()).decBombs();
            succMapBombDFS(editMap, pMove.getX(), pMove.getY(), bombStrength);
        } else { //build phase
            if(moveTile.getType() == TileTypes.EMPTY){ //player stone
                editMap.removeFromEmptyWN(moveTile);
                editMap.insert(editMap.getOccupiedTiles(), editMap.readField(moveTile.getX(), moveTile.getY()));
                editMap = turnTiles(editMap, pMove);
                editMap.lowerPlayableTiles();
                Tile tempN;
                for(int d = 0; d<8;d++){
                    tempN = editMap.getNeighbour(pMove.getX(), pMove.getY(), d);
                    if(tempN.getType()!= TileTypes.PLAYER && tempN.getType() != TileTypes.INVALID){
                        editMap.insert(editMap.getEmptyWithNeighbour(), tempN);
                    }
                }
            } else if(moveTile.getType() == TileTypes.CHOICE) { 
                editMap.lowerChoice();
                editMap.lowerPlayableTiles();
                editMap.removeFromEmptyWN(moveTile);
                editMap.insert(editMap.getOccupiedTiles(), editMap.readField(moveTile.getX(), moveTile.getY()));
                editMap = turnTiles(editMap, pMove);
                Tile tempN;
                for(int d = 0; d<8;d++){
                    tempN = editMap.getNeighbour(pMove.getX(), pMove.getY(), d);
                    if(tempN.getType()!= TileTypes.PLAYER && tempN.getType() != TileTypes.INVALID){
                        editMap.insert(editMap.getEmptyWithNeighbour(), tempN);
                    }
                }
                Tile curr;
                for(int y = 0; y < editMap.getHeight(); y++){
                    for(int x = 0; x < editMap.getWidth(); x++){
                        curr = editMap.readField(x, y);
                        if(curr.getPlayerNumber() == pMove.getPlayer().getPlayerNumber()){
                            curr.setPlayerNumber(pMove.getChoice());
                        } else if(curr.getPlayerNumber() == pMove.getChoice()){
                            curr.setPlayerNumber(pMove.getPlayer().getPlayerNumber());
                        }
                    }
                }
            } else if(moveTile.getType() == TileTypes.INVERSION) {
                editMap.lowerInversions();
                editMap.lowerPlayableTiles();
                editMap.removeFromEmptyWN(moveTile);
                editMap.insert(editMap.getOccupiedTiles(), editMap.readField(moveTile.getX(), moveTile.getY()));
                editMap = turnTiles(editMap, pMove);
                Tile tempN;
                for(int d = 0; d<8;d++){
                    tempN = editMap.getNeighbour(pMove.getX(), pMove.getY(), d);
                    if(tempN.getType()!= TileTypes.PLAYER && tempN.getType() != TileTypes.INVALID){
                        editMap.insert(editMap.getEmptyWithNeighbour(), tempN);
                    }
                }
                Tile curr;
                for(int y = 0; y < editMap.getHeight(); y++){
                    for(int x = 0; x < editMap.getWidth(); x++){
                        curr = editMap.readField(x, y);
                        if(curr.getType() == TileTypes.PLAYER && curr.getPlayerNumber() != 0){
                            curr.setPlayerNumber((curr.getPlayerNumber() % numberOfPlayers) +1);
                        }
                    }
                }
            } else if(moveTile.getType() == TileTypes.BONUS) {
                editMap.lowerPlayableTiles();
                editMap.removeFromEmptyWN(moveTile);
                editMap.insert(editMap.getOccupiedTiles(), editMap.readField(moveTile.getX(), moveTile.getY()));
                editMap = turnTiles(editMap, pMove);
                Tile tempN;
                for(int d = 0; d<8;d++){
                    tempN = editMap.getNeighbour(pMove.getX(), pMove.getY(), d);
                    if(tempN.getType()!= TileTypes.PLAYER && tempN.getType() != TileTypes.INVALID){
                        editMap.insert(editMap.getEmptyWithNeighbour(), tempN);
                    }
                }
                if(pMove.getChoice()==20){
                    editMap.getPlayer(pMove.getPlayer().getPlayerNumber()).incBombs();
                } else if(pMove.getChoice()==21){
                    editMap.getPlayer(pMove.getPlayer().getPlayerNumber()).incOverride();
                }
            } else if(moveTile.getType() == TileTypes.PLAYER) { //override stone
                editMap.getPlayer(pMove.getPlayer().getPlayerNumber()).decOverride();
                editMap = turnTiles(editMap, pMove);
            }
        }
        //set next player
        Player currentPlayer = editMap.getCurrentPlayer();
        editMap.setCurrentPlayer(editMap.players[currentPlayer.getNextPlayerNumber()]);
        return editMap;   
    }

    /**
		* This function turns the tiles on the given map and returns the resulting map
	 	* @param move
	 	* @param map
        * @return map
	*/
    public Map turnTiles(Map pMap, Move pMove){
        Map editMap = pMap;
        int path = 0;
        editMap.readField(pMove.getX(), pMove.getY()).setType(TileTypes.PLAYER);
        editMap.readField(pMove.getX(), pMove.getY()).setPlayerNumber(pMove.getPlayer().getPlayerNumber());
        editMap.addTileToRanking(pMove.getPlayer().getPlayerNumber());
        ArrayList<Tile> toTurn = new ArrayList<Tile>();
        ArrayList<Tile> temp = new ArrayList<Tile>();
        int direction;
        int tempDir;
        for(int i = 0; i < 8; i++){
            direction = i;
            temp.clear();
            Tile curr = editMap.readField(pMove.getX(), pMove.getY());
            path = 0;
            while(true){
                //if we took an extra transition: change direction accordingly
                if(curr.getNeighbourCoordinates(direction) != null){
                    tempDir = (curr.getNeighbourCoordinates(direction)[2]+4)%8;
                    curr = editMap.getNeighbour(curr.getX(), curr.getY(), direction);
                    direction = tempDir;
                } else {
                    curr = editMap.getNeighbour(curr.getX(), curr.getY(), direction);
                }
                path ++;
                temp.add(curr);
                if(!(curr.getType()==TileTypes.PLAYER)){
                    break;
                }
                if(curr.getX() == pMove.getX() && curr.getY() == pMove.getY()){
                    break;
                }
                if(curr.getPlayerNumber() == pMove.getPlayer().getPlayerNumber()){
                    //if path == 1 it means that our stone we found is the direct neighbour and then we do not want to use this
                    if(path == 1){
                        break;
                    } else {
                        toTurn.addAll(temp);
                        break;
                    }
                }
            }                  
        }
        for (Tile tile : toTurn) {
            try{
            editMap.subTileFromRanking(tile.getPlayerNumber());
            editMap.addTileToRanking(pMove.getPlayer().getPlayerNumber());
            editMap.readField(tile.getX(), tile.getY()).setType(TileTypes.PLAYER);
            editMap.readField(tile.getX(), tile.getY()).setPlayerNumber(pMove.getPlayer().getPlayerNumber());
            } catch(ArrayIndexOutOfBoundsException e){
                System.out.println("hier");
            }
            if(!pMap.hasEmptyNeighbour(tile.getX(), tile.getY())){
                if(pMove.getPlayer().getPlayerNumber() == ourNr){
                    pMap.raiseSecuredTiles();
                }    
            }
        
        }
        return editMap;
    }

     /**
		* This function creates a arraylist of all possible moves
	 	* @param map
        * @return arraylist of moves
	*/
    public ArrayList<Move> moveEnumeration(Map pMap){
        ArrayList<Move> moves = new ArrayList<Move>(lastNrOfMoves);
        if(phase == 2){
            if(pMap.getCurrentPlayer().getBombs() > 0){
                for (int y = 0; y < currentMap.getHeight(); y++){
                    for(int x = 0; x < currentMap.getWidth(); x++){
                        if(pMap.readField(x, y).getType() != TileTypes.INVALID && pMap.readField(x, y).getType() != TileTypes.BOMBHOLE) {
                            moves.add(new Move(x,y,pMap.getCurrentPlayer()));
                        }
                    }
                }
            }
        } else {
            pMap.getEmptyWithNeighbour().forEach((e) -> {
                moves.addAll(movesOnTile(e, pMap));
            });
            if(useOverrides){
                if(pMap.getCurrentPlayer().getOverrideStones()>0){
                    pMap.getOccupiedTiles().forEach((o) -> {
                        moves.addAll(movesOnTile(o, pMap));
                    });
                }    
            } else {
                if(moves.size() == 0){
                    pMap.getOccupiedTiles().forEach((o) -> {
                        moves.addAll(movesOnTile(o, pMap));
                    });
                }
            }
        }
        lastNrOfMoves = moves.size();  
        return moves;
    }

     /**
		* This function creates a arraylist of all maps resulting from all possible moves
        * @param arraylist of moves
	 	* @param map
        * @return arraylist of maps
	*/
    public ArrayList<Map> mapEnumeration(ArrayList<Move> pMoves, Map pMap){
        //Map tempMap = pMap.copyMap(phase);
        Map tempMap = pMap;
        ArrayList<Map> maps = new ArrayList<Map>();
        pMoves.forEach((m) -> {
            maps.add(succMap(m,tempMap));
        });
        return maps;
    }   

     /**
	    * This function creates a arraylist of all possible moves on given Tile and map 
        * @param tile
	 	* @param map
        * @return arraylist of moves
	*/
    public ArrayList<Move> movesOnTile(Tile t, Map map){
        ArrayList<Move> moves = new ArrayList<Move>();
        if(phase == 2){
            Move validBomb = new Move(t.getX(),t.getY(), map.getCurrentPlayer());
            if(moveValidator(validBomb, map)) {   
                moves.add(validBomb);
            }
        } else {
            if(t.getType() == TileTypes.CHOICE){
                Move validChoiceTest = new Move(t.getX(),t.getY(), map.getCurrentPlayer(), 1);
                if(moveValidator(validChoiceTest, map)){
                for(int i = 1; i< map.players.length; i++){
                    Move m = new Move(t.getX(),t.getY(), map.getCurrentPlayer(), i );
                    moves.add(m);
                }
            }
            } else if(t.getType() == TileTypes.BONUS){
                Move validTest = new Move(t.getX(),t.getY(), map.getCurrentPlayer(), 20);
                if(moveValidator(validTest, map)){
                    moves.add(validTest);
                    moves.add(new Move(t.getX(),t.getY(), map.getCurrentPlayer(), 21));
                }
            } else if(t.getType() == TileTypes.PLAYER){
                Move validPlayerTest = new Move(t.getX(),t.getY(), map.getCurrentPlayer());
                if(moveValidator(validPlayerTest, map)){
                    moves.add(validPlayerTest);
                }    
            } else {
                Move validTestRest = new Move(t.getX(),t.getY(), map.getCurrentPlayer());
                if(moveValidator(validTestRest, map)){
                    moves.add(validTestRest);
                }
            }
        }
        return moves;
    }
    /**
	    * This function rates the given map from the view of the given player 
	 	* @param map
        * @param player
        * @return double
	*/
    public double rateBoard(Map pMap, Player pPlayer){
        switch(heuristic){
            case "EARLY": 
                return rateEarlyBoard(pMap, pPlayer);
            case "MID":
                return rateLateBoard(pMap, pMap.getPlayer(((ourNr + pMap.getInversions() - 1) % numberOfPlayers) + 1));
            case "LATE":
                //return estimateRes(pMap, pMap.getPlayer(ourNr));
                return rateLateBoard(pMap, pMap.getPlayer(ourNr));
            case "BOMB":
                //return rateBombPhase(pMap);
                return rateLateBoard(pMap, pMap.getPlayer(ourNr));
            default:
                return rateLateBoard(pMap, pMap.getPlayer(ourNr));
        }
    }
    /**
     * Heuristic for the early stages of the game.
     * A map is considered good if there are many moves for us to do
     * the return grows proportionally to the number of moves
     * @param map
     * @param player
     * @return double
     */
    public double rateEarlyBoard(Map pMap, Player pPlayer){
        //Map tempMap= pMap.copyMap(phase);
        Map tempMap = pMap;
        Player currentPlayer = pMap.getCurrentPlayer();
        tempMap.setCurrentPlayer(pMap.getPlayer(ourNr));
        
        //number of moves added as a base rating
        double rate =(moveEnumeration(tempMap).size() * moveWorth);
        //number of remaining choices should be low 
        rate -= (pMap.getChoice() * choiceWorth );
        rate -= pMap.getInversions() * inversionWorth;
        //number of bombs should be high 
        rate += (bombStrength * bombStrength * pMap.getPlayer(ourNr).getBombs() * bombWorth);
        //number of overrides should be high 
        rate += (pMap.getPlayer(ourNr).getOverrideStones() * overrideWorth);
        //Its valued bad to move right next to a good field
        if(pMap.nextToSpecial(ourNr)){
            rate -= 100;
        }
        pMap.setCurrentPlayer(currentPlayer);
        return rate;
    }

    /**
     * Heuristic for the late stages of the game.
     * Rates the board from the view of the position pPlayer will end on
     * @param map
     * @param player
     * @return double
     */
    public double rateLateBoard(Map pMap, Player pPlayer){
        double[] ranking = pMap.getRanking();
        double sumDistance = 0;
        ArrayList<Double> distances = new ArrayList<Double>();
        int temp = pPlayer.getNextPlayerNumber();
        while(temp != pPlayer.getPlayerNumber()){
            distances.add(ranking[temp] - ranking[pPlayer.getPlayerNumber()]);
            sumDistance += Math.abs(ranking[temp] - ranking[pPlayer.getPlayerNumber()]);
            temp = pMap.getPlayer(temp).getNextPlayerNumber();  
        }
        double res = 0;
        for(double d : distances)   {
            if(d > 0){
                res += -d*Math.exp(2*(-d/sumDistance));
                //res += -d*Math.exp(-d/sumDistance);
            }
            if(d < 0){
                res -= d*2.5*Math.exp(2*(d/sumDistance));
                //res += -d*3*Math.exp(-d/sumDistance);
            }
        }
        if(phase == 1){
            res -= (pMap.getChoice() * choiceWorth );
            res -= pMap.getInversions() * inversionWorth;
            res += (bombStrength * bombStrength * pMap.getPlayer(ourNr).getBombs() * bombWorth);
            res += (pMap.getPlayer(ourNr).getOverrideStones() * overrideWorth);
            if(pMap.nextToSpecial(ourNr)){
                res -= 100;
            }
            res += pMap.getSecuredTiles() * secureWorth;
        } 
        return res; 
        
        
    }
    /**
     * Heuristic for the bomb phase of the game.
     * Rates the board by counting stones only. 
     * @param map
     * @return double
     */

    public double rateBombPhase(Map pMap){
        double temp = 0;
        for(int i = 0;i<pMap.getRanking().length;i++){
            if(i != ourNr){
                temp -= pMap.getRanking()[i];
            }
            else{
                temp += pMap.getRanking()[i];
            }
        }
        return temp;
    }
  
    /**
     * Heuristic that estimates how many stones we will have in the end. 
     * @param map
     * @param player
     * @return double
     */
    public double estimateRes(Map pMap, Player pPlayer){
        return ( pMap.getRanking()[pPlayer.getPlayerNumber()] + ( (pMap.getPlayableTiles()/numberOfPlayers) * turnEstimator() ) );
    }

    /**
     * Returns the average turned stones from the last three moves
     * @param map
     * @param player
     * @return double
     */
    public double turnEstimator(){
        Double sum = (double) 0;
        for(int i=0; i<turned.size();i++){
            Double temp = turned.remove();
            sum += temp;
            turned.add(temp);
        }
    return (sum/turned.size());
    
    
    
   }
    /**
     * Function that returns how many stone are turned when pPlayer makes every possible move at once(non duplicate free!) 
     * @param map
     * @param player
     * @param ArrayList move
     * @return double
     */
    public double turnableStones(ArrayList<Move> moves, Player pPlayer, Map tempMap){
        int rate = 0;
        for(Move m : moves){
            for(int i = 0; i < 8; i++){
                int direction = i;
                int tempRate = 0;
                Tile curr = tempMap.readField(m.getX(), m.getY());
                int path = 0;
                while(true){
                    //if we took an extra transition: change direction accordingly
                    if(curr.getNeighbourCoordinates(i) != null){
                        int tempDir = (curr.getNeighbourCoordinates(direction)[2]+4)%8;
                        curr = tempMap.getNeighbour(curr.getX(), curr.getY(), direction);
                        direction = tempDir;
                        tempRate++;
                    } else {
                        curr = tempMap.getNeighbour(curr.getX(), curr.getY(), i);
                        tempRate++;
                    }
                    path ++;
                    if(!(curr.getType()==TileTypes.PLAYER)){
                        break;
                    }
                    if(curr.getX() == m.getX() && curr.getY() == m.getY()){
                        break;
                    }
                    if(curr.getPlayerNumber() == m.getPlayer().getPlayerNumber()){
                        //if path == 1 it means that our stone we found is the direct neighbour and then we do not want to use this
                        if(path == 1){
                            break;
                        } else {
                            rate += tempRate;
                            break;
                        }
                    }
                }
            }
        }
        System.out.print("There are "+ rate +" stones to turn");
        return rate;
        
    }
   
    /**
	    * This function returns the Move with the highest MiniMax Value of succMap(pMap, move), search depth limited to the depth argument
        * @param depth
	 	* @param map
        * @return move
	*/
    public Move miniMaxDecision(int pDepth, Map pMap){
        double bestMiniMaxVal = -Double.MAX_VALUE;
        Move bestMove = null;
        abortSearch = false;
        ArrayList<Move> moveEnum = moveEnumeration(pMap);
        averageBranchingFactor = moveEnum.size();
        numberOfFactors = 1;
        for (Move possibleMove : moveEnum) {
            if(abortSearch){
                return new Move(0,0,currentMap.getCurrentPlayer());
            }
            Double curr = miniMaxValue(pDepth, succMap(possibleMove, pMap), pMap.getPlayer(pMap.getPlayer(ourNr).getNextPlayerNumber()), 1);
            if((bestMiniMaxVal <= curr)){
                bestMiniMaxVal = curr;
                bestMove = possibleMove;
            }
        }
        saveMinimax = bestMiniMaxVal;
        return bestMove;
    }

    /**
	    * This function returns the MiniMax Value and is a help funtion for a recursive calculation
        * @param depth
	 	* @param map
        * @param player
        * @param currentDepth
        * @return double
	*/
    public Double miniMaxValue(int depth, Map pMap, Player player, int currDepth) {
        if(bench){        
            statesVisited[0]++; //benchmark related state counter
        }
        if (currDepth == depth) {
            return rateBoard(pMap, pMap.getPlayer(ourNr));
        } else {
            if(goalTime - System.currentTimeMillis() < bufferTime){
                abortSearch = true;
                return -Double.MAX_VALUE;
            }
            double bestRating;
            double curr;
            ArrayList<Move> moveEnum = moveEnumeration(pMap);
            numberOfFactors++;
            averageBranchingFactor = ((numberOfFactors-1)/numberOfFactors)*averageBranchingFactor + (1/numberOfFactors)*moveEnum.size();
            if(!moveEnum.isEmpty()){ //player can make a turn
                if (player.getPlayerNumber() == ourNr) { //our turn (MAX)
                    bestRating = -Double.MAX_VALUE;
                        for (Move possibleMove : moveEnum) { //looks for the highest Minimax Value of successors
                            if(abortSearch){
                                return -Double.MAX_VALUE;
                            }
                            curr = miniMaxValue(depth, succMap(possibleMove, pMap), pMap.getPlayer(player.getNextPlayerNumber()), currDepth + 1);
                            if (bestRating < curr) {
                                bestRating = curr;
                            }
                        }
                } else { //someone elses turn (MIN)
                    bestRating = Double.MAX_VALUE;
                    for (Move possibleMove : moveEnum) { //looks for the lowest Minimax Value of successors
                        if(abortSearch){
                            return -Double.MAX_VALUE;
                        }
                        curr = miniMaxValue(depth, succMap(possibleMove, pMap), pMap.getPlayer(player.getNextPlayerNumber()), currDepth + 1);
                        if (bestRating > curr) {
                            bestRating = curr;
                        }
                    }
                }
                return bestRating;
                } else { //we cannot make a turn -> stop. another player cannot make a turn -> next player
                if(player.getPlayerNumber() == ourNr){
                    return rateBoard(pMap, player);
                } else {
                    return miniMaxValue(depth, pMap, pMap.getPlayer(player.getNextPlayerNumber()), currDepth);
                }
        } 
    }
}

     /**
	    * This function returns the Move with the highest alphabeta Value
        * @param depth
	 	* @param map
        * @return move
	*/
    public Move alphaBetaDecision(int depth, Map pMap, boolean useWindow){
        //here a modified version of alphaValue is used for the first layer of the search tree
        //in order to remember the Move that belongs to the current v and return this
        double alpha;
        double beta;
        double v;
        if(useWindow){
            alpha = aspLowerBound;
            beta = aspUpperBound;
        } else {
            alpha = -Double.MAX_VALUE;
            beta = Double.MAX_VALUE;
        }     
        abortSearch = false;
        ArrayList<Move> moves;
        //set sort to be true to activate moveSort
        if(sort) {
        moves = sortedMoveEnum(pMap);
        } else {
            moves = moveEnumeration(pMap);
        }
        if(moves.size()==0){
            System.out.println("Our turn but move validator did not find any moves");
            System.out.println(pMap);
            for(int y = 0; y < pMap.getHeight(); y++){
                for(int x = 0; x < pMap.getWidth(); x++){
                    if(pMap.readField(x, y).getType() != TileTypes.INVALID && pMap.readField(x, y).getType() != TileTypes.BOMBHOLE){
                        return new Move(x, y, pMap.getPlayer(ourNr));
                    }
                }
            }
        }
        Move alphaMove = moves.get(0);
        numberOfStates = 1;
        Move m;
        while(moves.size()>0) {
            m = moves.get(0);
            if(abortSearch || goalTime - System.currentTimeMillis() < bufferTime){
                abortSearch = true;
                return alphaMove;
            }
            v = betaValue(depth, succMap(m, pMap), 1, alpha, beta);
            if(v >= alpha){
                alphaMove = m;
            }
            alpha = Double.max(alpha, v);
            moves.remove(0);
        }
        saveAlpha = alpha;
        return alphaMove;
    }

    /**
	    * This function returns the alpha Value and is a help funtion for a recursive calculation
        * @param depth
	 	* @param map
        * @param currentDepth
        * @param alpha
        * @param beta
        * @return double
	*/
    public Double alphaValue(int depth, Map pMap, int currDepth, double pAlpha, double pBeta){
        if(bench){    
            if(sort) {
                statesVisited[2]++; //benchmark related state counter
            } else {
                statesVisited[1]++;
            }
        }
        numberOfStates++;
        if(depth == currDepth){
            return rateBoard(pMap, pMap.getPlayer(ourNr));
        }
        double v = -Double.MAX_VALUE;
        ArrayList<Move> moveEnum;
        if(sort) {
            moveEnum = sortedMoveEnum(pMap);
        } else {
            moveEnum = moveEnumeration(pMap);
        }
        if(!moveEnum.isEmpty()){
            Move m;
            if(pMap.getCurrentPlayer().getNextPlayerNumber() != ourNr){
                while(moveEnum.size() > 0) {
                    m = moveEnum.get(0);
                    if(abortSearch || goalTime - System.currentTimeMillis() < bufferTime){
                        abortSearch = true;
                        return -Double.MAX_VALUE;
                    }
                    v = Double.max(v, betaValue(depth, succMap(m, pMap), currDepth + 1, pAlpha, pBeta));
                    if(v >= pBeta){
                        return v;
                    }
                    pAlpha = Double.max(pAlpha, v);
                    moveEnum.remove(0);
                }
                return v;
            } else {
                while(moveEnum.size() > 0) {
                    m = moveEnum.get(0);
                    if(abortSearch || goalTime - System.currentTimeMillis() < bufferTime){
                        abortSearch = true;
                        return -Double.MAX_VALUE;
                    }
                    v = Double.max(v, alphaValue(depth, succMap(m, pMap), currDepth + 1, pAlpha, pBeta));
                    if(v >= pBeta){
                        return v;
                    }
                    pAlpha = Double.max(pAlpha, v);
                    moveEnum.remove(0);
                }
                return v;
            }
        } else {
            if(pMap.getCurrentPlayer().getPlayerNumber() == ourNr){
                //Worst case if we cannot make any more moves
                return -Double.MAX_VALUE;
            } else {
                Map newMap = pMap.copyMap(phase);
                newMap.setCurrentPlayer(newMap.getPlayer(newMap.getCurrentPlayer().getNextPlayerNumber()));
                return betaValue(depth, newMap, currDepth, pAlpha, pBeta);
            }
        }  
    }

    /**
	    * This function returns the beta Value and is a help funtion for a recursive calculation
        * @param depth
	 	* @param map
        * @param currentDepth
        * @param alpha
        * @param beta
        * @return double
	*/
    public Double betaValue(int depth, Map pMap, int currDepth, double pAlpha, double pBeta){
        if(bench){    
            if(sort) {
                statesVisited[2]++; //benchmark related state counter
            } else {
                statesVisited[1]++;
            }
        }
        numberOfStates++;
        if(depth == currDepth){
            return rateBoard(pMap, pMap.getPlayer(ourNr));
        }
        double v = Double.MAX_VALUE;
        ArrayList<Move> moveEnum;
        if(sort) {
            moveEnum = sortedMoveEnum(pMap);
        } else {
            moveEnum = moveEnumeration(pMap);
        }
        if(!moveEnum.isEmpty()){
            Move m;
            if(pMap.getCurrentPlayer().getNextPlayerNumber() != ourNr){
                while(moveEnum.size() > 0){
                    m = moveEnum.get(0);
                    if(abortSearch || goalTime - System.currentTimeMillis() < bufferTime){
                        abortSearch = true;
                        return -Double.MAX_VALUE;
                    }
                    v = Double.min(v, betaValue(depth, succMap(m, pMap), currDepth + 1, pAlpha, pBeta));
                    if(v <= pAlpha){
                        return v;
                    }
                    pBeta = Double.min(pBeta, v);
                    moveEnum.remove(0);
                }
                return v;
            } else {
                while(moveEnum.size() > 0){
                    m = moveEnum.get(0);
                    if(abortSearch || goalTime - System.currentTimeMillis() < bufferTime){
                        abortSearch = true;
                        return -Double.MAX_VALUE;
                    }
                    v = Double.min(v, alphaValue(depth, succMap(m, pMap), currDepth + 1, pAlpha, pBeta));
                    if(v <= pAlpha){
                        return v;
                    }
                    pBeta = Double.min(pBeta, v);
                    moveEnum.remove(0);
                }
                return v;
            }
        } else {
            if(pMap.getCurrentPlayer().getPlayerNumber() == ourNr){
                //Worst case if we cannot make any more moves
                return -Double.MAX_VALUE;
            } else {
                Map newMap = pMap.copyMap(phase);
                newMap.setCurrentPlayer(newMap.getPlayer(newMap.getCurrentPlayer().getNextPlayerNumber()));
                return betaValue(depth, newMap, currDepth, pAlpha, pBeta);
            }
        }
    }

    /**
	    * This function returns the best move using minimax with given depth
	 	* @param map
        * @param depth
        * @return move
	*/
    public Move iterativeDeepeningMinimax(Map pMap, int depth){
        Move returnMove = null;
        long lastDepthTime;
        long sumTime = 0;
        long tempStart;
        Move lastMove = null;
        for(int i = 1; i <= depth; i++){
            averageBranchingFactor = 1;
            tempStart = System.currentTimeMillis();
            returnMove = miniMaxDecision(i, pMap);
            if(i == 1){
                lastMove = returnMove;
            }
            //time for depth i is total time for calculating level i minus the time it took to calculate level i-1
            lastDepthTime = System.currentTimeMillis() - tempStart - sumTime; 
            //sumTime is the time it took to calculate level i
            sumTime = System.currentTimeMillis() - tempStart;
            if(abortSearch){
                System.out.println("Iterative Deepening: Aborted depth" + i);
                System.out.println("Remaining time: " + (goalTime - System.currentTimeMillis()));
                //return the move of the last complete calculation (depth i-1) if search needed to be aborted because of running out of time
                return lastMove;
            }
            if((System.currentTimeMillis() + sumTime + (long)(lastDepthTime*averageBranchingFactor*1.05f)) >= goalTime){ //5% buffer for branching factor
                System.out.println("Iterative Deepening: Stop at depth " + i);
                System.out.println("Last calculation took " + sumTime);
                System.out.println("Last depth took " + lastDepthTime);
                System.out.println("Remaining time: " + (goalTime - System.currentTimeMillis()));
                System.out.println("Estimated time: " + (sumTime + (long)(lastDepthTime*averageBranchingFactor)) + " with average branching factor " + averageBranchingFactor);
                return returnMove;
            }
            lastMove = returnMove;
        }
        System.out.println("Iterative Deepening: Max stop");
        return returnMove;
    }

    /**
	    * This function returns the best move using alphabeta with given depth
	 	* @param map
        * @param depth
        * @return move
	*/
    public Move iterativeDeepeningAlphaBeta(Map pMap, int depthLimit){
        Move returnMove = null;
        long lastDepthTime;
        long sumTime = 0;
        long tempStart;
        Move lastMove = null;
        numberOfStates = 1;
        float lastStates;
        useAspirationWindow = false;
        aspLowerBound = -Double.MAX_VALUE;
        aspUpperBound = Double.MAX_VALUE;
        int depth;
        if(heuristic.equals("EARLY")){
            depth = Integer.max(6, numberOfPlayers);
        } else if(phase ==2) {
            depth = 1;
        } else {    
            depth = depthLimit;
        }
        for(int i = 1; i <= depth; i++){
            lastStates = numberOfStates;
            numberOfStates = 1;
            tempStart = System.currentTimeMillis();
            returnMove = alphaBetaDecision(i, pMap, useAspirationWindow);
            //time for depth i is total time for calculating level i minus the time it took to calculate level i-1
            lastDepthTime = System.currentTimeMillis() - tempStart - sumTime;
            //sumTime is the time it took to calculate level i
            sumTime = System.currentTimeMillis() - tempStart;
            averageBranchingFactor = numberOfStates/lastStates;
            if(i == 1){
                lastMove = returnMove;
            }
            if(abortSearch){
                System.out.println("Iterative Deepening: Aborted depth" + i);
                System.out.println("Remaining time: " + (goalTime - System.currentTimeMillis()) + " with average branching factor " + averageBranchingFactor);
                //return the move of the last complete calculation (depth i-1) if search needed to be aborted because of running out of time
                return lastMove;
            }
            //this code was for Aspiration Window but we do not use Aspiration Window anymore
            /*if((saveAlpha < aspLowerBound) || returnMove == null){
                System.out.println("Window too small: " + saveAlpha + " < " + aspLowerBound);
                //re-run alphaBeta without any window
                numberOfStates = 1;
                tempStart = System.currentTimeMillis();
                returnMove = alphaBetaDecision(i, pMap, false);
                lastDepthTime = System.currentTimeMillis() - tempStart - sumTime;
                averageBranchingFactor = numberOfStates/lastStates;
                aspUpperBound = saveAlpha + 5;
                aspLowerBound = saveAlpha - 5;
                if(abortSearch){
                    return lastMove;
                }
                if((System.currentTimeMillis() + sumTime + (long)(lastDepthTime*averageBranchingFactor)) >= goalTime){
                    return returnMove;
                }
            } else {
                System.out.println(saveAlpha + " in [" + aspLowerBound + "," +aspUpperBound + "]");
                aspUpperBound = saveAlpha + Math.abs(saveAlpha);
                aspLowerBound = saveAlpha - Math.abs(saveAlpha);
            }*/
            if((System.currentTimeMillis() + sumTime + (long)(lastDepthTime*averageBranchingFactor*1.05)) >= goalTime){
                System.out.println("Iterative Deepening: Stop at depth " + i);
                System.out.println("Last calculation took " + sumTime);
                System.out.println("Last depth took " + lastDepthTime);
                System.out.println("Remaining time: " + (goalTime - System.currentTimeMillis()));
                System.out.println("Estimated time: " + (sumTime + (long)(lastDepthTime*averageBranchingFactor)) + " with average branching factor " + averageBranchingFactor);
                return returnMove;
            }
            lastMove = returnMove;
        }
        System.out.println("Iterative Deepening: Max stop");
        return returnMove;
    }

    /**
	    * This function compares the results of minimax and alphabeta
	*/
    public void compareMinimaxAB(){
        if(saveMinimax != saveAlpha){
            System.err.println("Different Minimax and Alpha-Beta Values!");
            System.out.println("Minimax: " + saveMinimax);
            System.out.println("Alpha-Beta: " + saveAlpha);
            System.out.println(currentMap);
        }
    }
    /** 
     * Sorts the Arraylist given by moveEnumeration(), by the rating of their subsequent map
     * @param map
     * @return Arraylist move
    */
        
    public ArrayList<Move> sortedMoveEnum(Map pMap){
        Map tempMap = pMap;
        ArrayList<Move> moves = moveEnumeration(tempMap);
    	for(Move ms : moves) {
            ms.setRated(rateBoard(succMap(ms, tempMap),tempMap.getCurrentPlayer()));
            tempMap = pMap;
    	}
        if(ourNr != currentMap.getCurrentPlayer().getNextPlayerNumber()){
            Collections.sort(moves, new MoveRateComparatorDEC());
        }
        else{
            Collections.sort(moves, new MoveRateComparatorASC());
        }
    	return moves;
    }

    public static class MoveRateComparatorDEC implements Comparator<Move> {
        @Override public int compare(Move m1 , Move m2){
            return m2.getRated().compareTo(m1.getRated());
        }
    }
    public static class MoveRateComparatorASC implements Comparator<Move> {
        @Override public int compare(Move m1 , Move m2){
            return m1.getRated().compareTo(m2.getRated());
        }
    }
}
