package ReversiAi;
import java.util.Scanner;
import java.util.Arrays;
import java.util.*;
import java.net.*;
import java.io.*;
//import java.io.IOException;
//import org.w3c.dom.Text; 

/**
	* This class manages the connection to the server
*/
public class Client {
	private String IP_Addr;
	private int port;
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private Game game;
	private int moveNr = 0;
	private boolean alphabeta;
	private int mistakes = 0;
	private boolean sort;
	private int i = 0;
	double[] mm =new double[1000];
	double[] ab =new double[1000];	//benchmark related
	double[] abs =new double[1000];
	boolean useAspirationWindow = false;

	/**
		* This function initializes the class Client
	 	* @param IP_Addr
	 	* @param Port
		* @param alphabeta
		* @param sort
	*/
	public Client(String pIP, int pPort, boolean pPrune, boolean pSort){
		this.setIP_Addr(pIP);
		this.setPort(pPort);
		this.setAlphabeta(pPrune);
		this.setSort(pSort);
	}

	public String getIP_Addr() {
		return this.IP_Addr;
	}

	public void setIP_Addr(String pIP_Addr){
		this.IP_Addr = pIP_Addr;
	}
	
	public int getPort() {
		return this.port;
	}

	public void setPort(int pPort){
		this.port = pPort;
	}

	public Socket getSocket(){
		return this.socket;
	}

	public void setSocket(Socket pSocket){
		this.socket = pSocket;
	}
	
	public DataOutputStream getDataOutputStream(){
		return this.outputStream;
	}

	public void setDataOutputStream(DataOutputStream pStream){
		this.outputStream = pStream;
	}

	public Game getGame(){
		return this.game;
	}

	public void setGame(Game pGame){
		this.game = pGame;
	}

	public int getMoveNr(){
		return this.moveNr;
	}

	public void setMoveNr(int pMoveNr){
		this.moveNr = pMoveNr;
	}

	public boolean getAlphabeta(){
		return this.alphabeta;
	}

	public void setAlphabeta(boolean pAlphabeta){
		this.alphabeta = pAlphabeta;
	}

	public int getMistakes(){
		return this.mistakes;
	}

	public void setMistakes(int pMistakes){
		this.mistakes = pMistakes;
	}

	public boolean getSort(){
		return this.sort;
	}

	public void setSort(boolean pSort){
		this.sort = pSort;
	}

	public int getI(){
		return this.i;
	}

	public void setI(int pI){
		this.i = pI;
	}

	public double[] getMm(){
		return this.mm;
	}

	public void setMm(double[] pMm){
		this.mm = pMm;
	}

	public double[] getAb(){
		return this.ab;
	}

	public void setAb(double[] pAb){
		this.ab = pAb;
	}

	public double[] getAbs(){
		return this.abs;
	}

	public void setAbs(double[] pAbs){
		this.abs = pAbs;
	}

	/**
	 * This function check if the conncection is established
	 * @param ip Hostname
	 * @param port Port Nr
	 * @return True if connection established, false otherwise
	 * @throws UnknownHostException Hostname invalid
	 * @throws IOException 
	 */
	public boolean connect() throws UnknownHostException, IOException {
		try{
			for(int i=0; i<3; i++) {//reconnect 2 times for failed connection first time
				this.socket = new Socket(IP_Addr,port);
				if(this.socket.isConnected()) {
					inputStream = new DataInputStream(this.socket.getInputStream());
					outputStream = new DataOutputStream(this.socket.getOutputStream());
					sendGroupNumber();
					System.out.println("Connection established!");
					return true;
				} else {
					System.out.println("Failed to connect, reconnecting...");
				}
			}
			System.out.println("Connection failed, please check your input information and restart the programm.");
			return false;
		}
		catch(Exception e){
			System.out.println("Connection failed, please check your input information and restart the programm.");
			return false;
		}

	}
	
	/**
	 * This function processes the message from the server
	 * @param game
	 * @throws IOException 
	 */
    public void process() throws IOException{
        byte type = inputStream.readByte();
        int length = inputStream.readInt();
        if(length >= 0){
            byte[] message = new byte[length];
            inputStream.read(message);//store the message
			String text = new String(message,0,length);
			Move move;
			switch (type) {
				case 2: 
					this.game = readMap(text);
					//this.game.setSort(sort);
					System.out.println("nonHole: " + game.currentMap.getNonHole());
					System.out.println("occupied: " + game.currentMap.getOccupiedTiles().size());
					break;
				case 3:
					this.game.setOurNr(message[0]);
					break;
				case 4:
						if(this.game.getBench()&& this.game.getOurNr()==1){
							this.game.currentMap.setCurrentPlayer(this.game.currentMap.getPlayer(this.game.getOurNr()));
							double t;
							if(this.game.phase == 2){
								this.game.setSort(false);
								t = System.currentTimeMillis();
								move = this.game.alphaBetaDecision(1, this.game.currentMap, useAspirationWindow);
								this.ab[i] = System.currentTimeMillis() -t;
								this.game.setSort(true);
								t = System.currentTimeMillis();
								move = this.game.alphaBetaDecision(1, this.game.currentMap, useAspirationWindow);
								this.abs[i] = System.currentTimeMillis() -t;
								t = System.currentTimeMillis();
								move = this.game.miniMaxDecision(1, this.game.currentMap);
								this.mm[i] = System.currentTimeMillis() -t;
							} else {
								this.game.setSort(false);
								t = System.currentTimeMillis();
								move = this.game.alphaBetaDecision(3, this.game.currentMap, useAspirationWindow);
								this.ab[i] = System.currentTimeMillis() -t;
								this.game.setSort(true);
								t = System.currentTimeMillis();
								move = this.game.alphaBetaDecision(3, this.game.currentMap, useAspirationWindow);
								this.abs[i] = System.currentTimeMillis() -t;
								t = System.currentTimeMillis();
								move = this.game.miniMaxDecision(3, this.game.currentMap);
								this.mm[i] = System.currentTimeMillis() -t;
							}
							if(this.game.getOurNr() == 1){
								i++;
							}
							if(this.game.getAbRate() != this.game.getAbSRate()){// counts times here the heuristic value for alpha beta and minimax is different.
								System.out.println("Mistake in depth 1");
								mistakes++;
							}
						} else {
							Byte depthLimit = message[4];
							int timeLimit = ((message[0] & 0xff) << 24) + ((message[1] & 0xff) << 16) + ((message[2] & 0xff) << 8) + (message[3] & 0xff);
							if(timeLimit == 0){
								timeLimit = 10000; //server timeout after 10 seconds
							}
							if(depthLimit == 0){
								depthLimit = 20; //max cut-off value
							}
							this.game.setGoalTime(System.currentTimeMillis()+timeLimit-Long.max(200L, (long)(timeLimit*0.005))); //at least 200 ms buffer or 0.5% of the time limit
							this.game.currentMap.setCurrentPlayer(this.game.currentMap.getPlayer(this.game.getOurNr()));
							if(alphabeta) {
								move = this.game.iterativeDeepeningAlphaBeta(this.game.currentMap, depthLimit);
							} else {
								move = this.game.iterativeDeepeningMinimax(this.game.currentMap, depthLimit);
							} 
						}
						sendMove(move);
						
						Double temp = this.game.currentMap.getRanking()[this.game.getOurNr()];
						this.game.currentMap = this.game.succMap(move, this.game.currentMap);
						
						Queue<Double> queue = this.game.getTurned();
						if(queue.size() >= 3){
							queue.remove();
						}
						queue.add(this.game.currentMap.getRanking()[this.game.getOurNr()] - temp);
						
						moveNr++;
						System.out.println("Move "+ moveNr);
						if(game.phase == 2){
							System.out.println(game.currentMap);
						}
						//System.out.println(game.currentMap);
					break;
				case 6: 
				    if(message[5] != this.game.getOurNr()) {
						this.game.currentMap.setCurrentPlayer(this.game.currentMap.getPlayer(Byte.toUnsignedInt(message[5])));
						//player message[5] made a move on (mesasge[1], message[3])
						move = new Move(Byte.toUnsignedInt(message[1]), Byte.toUnsignedInt(message[3]), this.game.currentMap.getPlayer(Byte.toUnsignedInt(message[5])), Byte.toUnsignedInt(message[4]));
					    this.game.currentMap = this.game.succMap(move, this.game.currentMap); //update the map
						moveNr++;
				    }
					if(game.phase == 2){
						System.out.println(moveNr);
						System.out.println(game.currentMap);
					}
					break;
				case 7: //Disqualify player message[0]
					this.game.currentMap.disqualifyPlayer(message[0]);
					break;
				case 8:
					this.game.phase = 2;
					this.game.currentMap.initPhase2();
					break;
				case 9: //end of game
					this.socket.close(); //close the socket once the game ends
					System.exit(0);
				default:
					break;
			}
        }
    }

	/**
	 * This function creates or updates the map with the given information from the message
	 * @param String message
	 * @return the current game
	 * @throws IOException 
	 */
    public Game readMap(String message) throws IOException {
		Scanner scanner = new Scanner(message);
		//have received a text named text, which stores the map in String format, now to set it as the current map
		String currLine;
		//set number of players
        currLine = scanner.nextLine();
        int numberOfPlayers = Integer.valueOf(currLine);
		//set number of ovveride-stones
        currLine = scanner.nextLine();
        int override = Integer.parseUnsignedInt(currLine);
		//set number and strength of bombs
        currLine = scanner.nextLine();
        int bombs = Integer.parseUnsignedInt(currLine.split(" ")[0]);
        int bombStrength = Integer.parseUnsignedInt(currLine.split(" ")[1]);
        //create players
        Player[] players = new Player[numberOfPlayers+1];
        int next;
        for(int p = 1; p <= numberOfPlayers; p++){
            if(p == numberOfPlayers){
                next = 1;
            } else {
                next = p+1;
            }
            players[p]= new Player(p, bombs, override,next);
        }
        //map
        currLine = scanner.nextLine();
        int mapHeight = Integer.valueOf(currLine.split(" ")[0]);
        int mapWidth = Integer.valueOf(currLine.split(" ")[1]);
        Map newMap = new Map(mapHeight, mapWidth, numberOfPlayers, players, players[1]);
        //tiles
        String[] tiles;
		int nonHole = mapHeight * mapWidth;
        for(int h = 0; h < mapHeight; h++){
            currLine = scanner.nextLine();
            tiles = currLine.split(" ");
            Tile tile;
            for (int w = 0; w < mapWidth; w++){
                switch(tiles[w]){
                    case "0":
						newMap.raisePlayableTiles(); 	
						tile = new Tile(w, h, TileTypes.EMPTY); break;
                    case "1": 	
						tile = new Tile(w, h, TileTypes.PLAYER, 1);
                    	newMap.insert(newMap.getOccupiedTiles(), tile);
                        newMap.addTileToRanking(1);
                        break;
                    case "2": 
						tile = new Tile(w, h, TileTypes.PLAYER, 2);
            			newMap.insert(newMap.getOccupiedTiles(), tile);
                        newMap.addTileToRanking(2);
                        break;
                    case "3": 
						tile = new Tile(w, h, TileTypes.PLAYER, 3);
                   	 	newMap.insert(newMap.getOccupiedTiles(), tile);
                        newMap.addTileToRanking(3);
                        break;
                    case "4": 
						tile = new Tile(w, h, TileTypes.PLAYER, 4);
                    	newMap.insert(newMap.getOccupiedTiles(), tile);
                        newMap.addTileToRanking(4);
                        break;
                    case "5": 
						tile = new Tile(w, h, TileTypes.PLAYER, 5);
                    	newMap.insert(newMap.getOccupiedTiles(), tile);
                        newMap.addTileToRanking(5);
                        break;
                    case "6": 
						tile = new Tile(w, h, TileTypes.PLAYER, 6);
                    	newMap.insert(newMap.getOccupiedTiles(), tile);
                        newMap.addTileToRanking(6);
                        break;
                    case "7": 
						tile = new Tile(w, h, TileTypes.PLAYER, 7);
                    	newMap.insert(newMap.getOccupiedTiles(), tile);
                        newMap.addTileToRanking(7);
                        break;
                    case "8": 
						tile = new Tile(w, h, TileTypes.PLAYER, 8);
                    	newMap.insert(newMap.getOccupiedTiles(), tile);
                        newMap.addTileToRanking(8);
                        break;
                    case "-": 
						tile = new Tile(w, h, TileTypes.INVALID); 
						nonHole--;
						break;
                    case "c":
						newMap.raiseChoice(); 
						newMap.raisePlayableTiles();
						tile = new Tile(w, h, TileTypes.CHOICE); 
						break;
                    case "i": 
						newMap.raiseInversions();
						newMap.raisePlayableTiles();
						tile = new Tile(w, h, TileTypes.INVERSION); 
						break;
                    case "b": 
						newMap.raisePlayableTiles();
						tile = new Tile(w, h, TileTypes.BONUS); 
						break;
                    case "x": 
						tile = new Tile(w, h, TileTypes.PLAYER, 0);
                    	newMap.insert(newMap.getOccupiedTiles(), tile);
                        break;
                    default: 
						tile = new Tile(w, h, TileTypes.INVALID);
						break;
                }
                newMap.writeField(w, h, tile);
            }
        }
		newMap.setNonHole(nonHole);
        //transitions
        while(scanner.hasNextLine()){
			currLine = scanner.nextLine();
			if(currLine == ""){
				break;
			}
            String[] valArgs = currLine.split(" ");
            String[] strKey = Arrays.copyOfRange(valArgs, 0, 3);
            String[] strVal = Arrays.copyOfRange(valArgs, 4, 7);
			newMap.readField(Integer.valueOf(strKey[0]), Integer.valueOf(strKey[1])).setNeighbour(Integer.valueOf(strKey[2]), Integer.valueOf(strVal[0]), Integer.valueOf(strVal[1]), Integer.valueOf(strVal[2]));
            newMap.readField(Integer.valueOf(strVal[0]), Integer.valueOf(strVal[1])).setNeighbour(Integer.valueOf(strVal[2]), Integer.valueOf(strKey[0]), Integer.valueOf(strKey[1]), Integer.valueOf(strKey[2]));
        }
        scanner.close();
        //fill the maps emptyWithNeighbour ArrayList
        for(int y = 0; y < mapHeight; y++){
            for(int x = 0; x < mapWidth; x++){
                if(newMap.readField(x, y).getType()!= TileTypes.PLAYER && newMap.readField(x, y).getType()!= TileTypes.INVALID){
                    for(int d = 0; d<8;d++){ //search every direction for neighbour
                        if(newMap.getNeighbour(x, y, d).getType()==TileTypes.PLAYER){
                            newMap.getEmptyWithNeighbour().add(newMap.readField(x, y));
                            break;
                        }
                    }
                }
            }
        }
        return new Game(newMap, bombStrength, numberOfPlayers);
	}

	//public boolean setOurNr() throws IOException {
		//set the byte in position message[] as our player number
	//}

	/**
	 * This function sends our move to the server
	 * @param Move pMove
	 * @throws IOException 
	 */
	public void sendMove(Move pMove) throws IOException{
		outputStream.writeByte(5); 	//type (8 bit)
		outputStream.writeInt(5); 	//length is always 5 byte for moves
		outputStream.writeChar(pMove.getX());	//char is 16 bit unsigned int
		outputStream.writeChar(pMove.getY());
		Tile tile = game.currentMap.readField(pMove.getX(), pMove.getY());
		if(tile.getType() == TileTypes.BONUS || tile.getType() == TileTypes.CHOICE){
			outputStream.writeByte(pMove.getChoice());
		} else {
			outputStream.writeByte(0);
		}

		//update heuristic
		game.setHeuristic("EARLY");
		if(game.phase == 2){
			System.out.println("BOMB heuristic"); 
			game.setHeuristic("BOMB");
		} else if(game.getCurrentMap().getChoice() <= 0 || game.currentMap.getOccupiedTiles().size() >= (game.currentMap.getNonHole() * 0.25) ){
			game.setHeuristic("MID");
			System.out.println("MID heuristic");
		} /* else if(game.currentMap.getOccupiedTiles().size() >= (game.currentMap.getNonHole() * 0.95)) {
			game.setHeuristic("LATE");
			System.out.println("LATE heuristic");
		} */ else {
			System.out.println("EARLY heuristic");
		}
	}

	/**
	 * This function sends our groupnumber to the server
	 * @param Move pMove
	 * @throws IOException 
	 */
    public boolean sendGroupNumber() throws IOException{
        //Message groupOut = new Message((byte)1,"3");
        outputStream.writeByte(1);
        outputStream.writeInt(1);
        outputStream.writeByte(3);
        return true;
    }

	/**
	 * This function writes data collected in the benchmarking process into a csv-file
	 * @throws FileNotFoundException
	 */
	public static void csvWriter(String name, double[] mm, double[] ab, double[] abs, int m, int[] states) throws FileNotFoundException{
        try {
            File csvFile = new File("./Plots/"+ name +"MAP3.csv");
            System.out.println("CSVwrite, da bin ich!");
            PrintWriter out = new PrintWriter(csvFile);
            out.println("Time taken to calculate move with mm ab and abs");
            for(int i = 0; i< mm.length;i++){
                out.printf("%d, %f, %f, %f \n", i, mm[i], ab[i], abs[i]);
            }
			out.printf("%d \n", m);
			out.printf("%d, %d, %d \n", states[0], states[1], states[2]);
            out.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
    } 
	
}
