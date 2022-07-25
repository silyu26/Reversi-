package ReversiAi;
import java.io.*;
import java.util.*;

/**
 * Main Class off the ReversiAI
 */
public class MainClass {
    //private static Game game;
    private static Client client;

    /**
		* This function is the main function
	 	* @param array of strings
        * @throws IOException
	*/
    public static void main(String args[])throws IOException{
        //Game game = createGame(1);
        //bench(game, 1);
        //test(game, 12);
        //System.exit(0);
        String ip = "";
        int port = 0;
        boolean prune = true;
        boolean sort = true;
        if(args.length > 0){
            if(args[0].equals("-h") || args[0].equals("--help")){
                System.out.println("Usage: (-s ip adress ) (-p port number) [ --noprune ]");
                System.out.println("Arguments:");
                System.out.println("-s, --server: Use this IP-Adress");
                System.out.println("-p, --port: Use this Port");
                System.out.println("--noprune: Disables the use of Alpha-Beta pruning");
                System.out.println("--nosort: Disables sorting for Alpha-Beta pruning");
                System.out.println("-h, --help: Shows this help text");
                System.exit(0);
            }
            if((args[0].equals("-s") || args[0].equals("--server")) && (args[2].equals("-p") || args[2].equals("--port"))){
                ip = args[1];
                port = Integer.parseInt(args[3]);
            } else {
                System.out.println("-s/--server or -p/--port missing");
            }
            for(String s : args){
                if(s.equals("--noprune")){
                    prune = false;
                    System.out.println("Alpha-Beta disabled");
                }
                if(s.equals("--nosort")){
                    sort = false;
                    System.out.println("Sort for Alpha-Beta disabled");
                }
            }   
        } else {
            ip = "127.0.0.1";
            port = 5555;
        }
        client = new Client(ip, port, prune, sort);
        boolean connected = client.connect();
        if(connected){
            while(true){
                client.process();
            }
        }
    }
    
    /**
		* This function creates the game
	 	* @param groupNumber
        * @throws IOException
	*/
    public static Game createGame(int groupNumber) throws IOException{
        File doc = new File("maps/testMaps/25_25_2_25_rnd_1.map");
        BufferedReader reader = new BufferedReader(new FileReader(doc));
        String currLine;
        //saves number of players
        currLine = reader.readLine();
        int numberOfPlayers = Integer.valueOf(currLine);
        //saves number of ovveride-stones
        currLine = reader.readLine();
        int override = Integer.valueOf(currLine);
        //saves number and strength of bombs
        currLine = reader.readLine();
        int bombs = Integer.valueOf(currLine.split(" ")[0]);
        int bombStrength = Integer.valueOf(currLine.split(" ")[1]);
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
        currLine = reader.readLine();
        int mapHeight = Integer.valueOf(currLine.split(" ")[0]);
        int mapWidth = Integer.valueOf(currLine.split(" ")[1]);
        Map newMap = new Map(mapHeight, mapWidth, numberOfPlayers, players, players[1]);
        //tiles
        String[] tiles;
        for(int h = 0; h < mapHeight; h++){
            currLine = reader.readLine();
            tiles = currLine.split(" ");
            Tile tile;
            for (int w = 0; w < mapWidth; w++){
                switch(tiles[w]){
                    case "0": 
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
                        break;
                    case "c": 
                        newMap.raiseChoice();
                        tile = new Tile(w, h, TileTypes.CHOICE); 
                        break;
                    case "i": 
                        tile = new Tile(w, h, TileTypes.INVERSION);
                        newMap.raiseInversions(); 
                        break;
                    case "b": 
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
        //transitions
        currLine = reader.readLine();
        while((currLine  != null && !currLine.equals(""))){
            String[] valArgs = currLine.split(" ");
            String[] strKey = Arrays.copyOfRange(valArgs, 0, 3);
            String[] strVal = Arrays.copyOfRange(valArgs, 4, 7);
            newMap.readField(Integer.valueOf(strKey[0]), Integer.valueOf(strKey[1])).setNeighbour(Integer.valueOf(strKey[2]), Integer.valueOf(strVal[0]), Integer.valueOf(strVal[1]), Integer.valueOf(strVal[2]));
            newMap.readField(Integer.valueOf(strVal[0]), Integer.valueOf(strVal[1])).setNeighbour(Integer.valueOf(strVal[2]), Integer.valueOf(strKey[0]), Integer.valueOf(strKey[1]), Integer.valueOf(strKey[2]));
            currLine = reader.readLine();
        }
        //fill the maps emptyWithNeighbour ArrayList
        for(int y = 0; y < mapHeight; y++){
            for(int x = 0; x < mapWidth; x++){
                if(newMap.readField(x, y).getType()!=TileTypes.INVALID && newMap.readField(x, y).getType()!=TileTypes.PLAYER){
                    for(int d = 0; d<8;d++){ //search every direction for neighbour
                        if(newMap.getNeighbour(x, y, d).getType()==TileTypes.PLAYER){
                            newMap.insert(newMap.getEmptyWithNeighbour(), newMap.readField(x, y));
                            //newMap.emptyWithNeighbour.add();
                            break;
                        }
                    }
                }
            }
        }
        //System.out.println(newMap.emptyWithNeighbour.toString());
        reader.close();
        System.out.println(newMap.toString());
        return new Game(newMap, bombStrength, groupNumber, numberOfPlayers);
    }

    //different test cases
    public static void test(Game game, int t){
        switch(t){
            case 0:
                System.out.println(game.rateBoard(game.currentMap, game.currentMap.players[1] ));
                break;
            case 1:
                for(Move m1 : game.moveEnumeration(game.currentMap)){
                    System.out.println("Move: " + m1);
                    for(Move m2 : game.moveEnumeration(game.succMap(m1, game.currentMap))){
                    System.out.println(m1);
                    System.out.println(m2);
                    System.out.println(game.succMap(m2, game.succMap(m1, game.currentMap)));
                    System.out.println(Double.toString(game.rateBoard(game.succMap(m2, game.succMap(m1, game.currentMap)), game.currentMap.getPlayer(1))));
                    System.out.println("\n");
                    }
                }
                break;
            case 2:
                for (Move m1 : game.moveEnumeration(game.currentMap)){
                    System.out.println(m1);
                }
                System.out.println("\n");
                System.out.println(game.miniMaxDecision(2, game.currentMap));
                break;
            case 3:
                game.phase = 2;
                System.out.println(game.succMap(new Move(0, 3, game.currentMap.getPlayer(1)), game.currentMap));
                break;
            case 4: 
            Move move = game.miniMaxDecision(2, game.currentMap);
                System.out.println(move);
                break;
            case 5:
                long start = System.currentTimeMillis();
                System.out.println(game.alphaBetaDecision(3, game.currentMap, false));
                long finish = System.currentTimeMillis();
                System.out.println(start - finish);
                break;
            case 7:
                ArrayList<Move> moves = game.moveEnumeration(game.currentMap);
                System.out.println(moves.size());
                for(int y = 0; y < game.currentMap.getHeight();y++){
                    System.out.println("\n");
                    for(int x = 0; x < game.currentMap.getWidth();x++){
                        for (Move m : moves){
                            if(m.getX() == x && m.getY() == y){
                                System.out.print("(" + x +","+y+") ");
                                System.out.print("y ");
                                break;
                            }
                        }
                    }
                }
                break;
            case 8:
                System.out.println(Arrays.toString(game.currentMap.getRanking()));
                game.currentMap = game.succMap(new Move(7, 4, game.currentMap.getPlayer(1)), game.currentMap);
                System.out.println(game.currentMap);
                System.out.println(game.rateBoard(game.currentMap, game.currentMap.getPlayer(1)));
                break;
            case 9:
                //Tile sorting tests
                Collections.sort(game.currentMap.getOccupiedTiles(), new Tile.TileCoordinateComparator());
                System.out.println(game.currentMap.getOccupiedTiles());
                game.currentMap.insert(game.currentMap.getOccupiedTiles(), new Tile(2,1,TileTypes.EMPTY));
                System.out.println(game.currentMap.getOccupiedTiles());
                break;
            case 10:
                move = game.miniMaxDecision(3, game.currentMap);
                move = game.alphaBetaDecision(3, game.currentMap, false);
                game.compareMinimaxAB();
                break;
            case 11:
                game.setGoalTime(System.currentTimeMillis() + 2000);
                move = game.iterativeDeepeningMinimax(game.currentMap, 20);
                System.out.println(game.getGoalTime() - System.currentTimeMillis());
                break;
            case 12:
                System.out.println(game.currentMap.getOccupiedTiles());
            default: 
                break;
        }
    }
}
