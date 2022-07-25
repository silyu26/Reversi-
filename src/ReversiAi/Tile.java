package ReversiAi;
import java.util.*;

/**
 *  This class represents the tiles
 */
public class Tile{
   private int x;
   private int y;
   private TileTypes type;
   private int playerNumber; //access Player object by player number (not minus 1)
   private ArrayList<int[]> neighbours; //entry is int[4] with {direction, neighbourX, neighbourY, neighbourDirection}
   
   /**
      * this function initialises the tile 
      * @param x 
      * @param y 
      * @param tiletype
   */
   public Tile(int pX, int pY, TileTypes pType){
      this.setX(pX);
      this.setY(pY);
      this.setType(pType);
      this.setNeighbours(new ArrayList<int[]>());
   }

   /**
      * this function initialises the tile 
      * @param x 
      * @param y 
      * @param tiletype
      * @param playernumber
   */
   public Tile(int pX, int pY, TileTypes pType, int pPlayerNumber){
      this.setX(pX);
      this.setY(pY);
      this.setType(pType);
      this.setPlayerNumber(pPlayerNumber);
      this.setNeighbours(new ArrayList<int[]>());
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

   public TileTypes getType(){
      return this.type;
   }

   public void setType(TileTypes pType){
      this.type = pType;
   }

   public int getPlayerNumber(){
      return this.playerNumber;
   }

   public void setPlayerNumber(int pNumber){
      this.playerNumber = pNumber;
   }
    
   public ArrayList<int[]> getNeighbours(){
      return this.neighbours;
   }

   public void setNeighbours(ArrayList<int[]> pNeighbours){
      this.neighbours = pNeighbours;
   }

   public void setNeighbour(int inDirection, int x, int y, int fromDirection){
      int[] arr = {inDirection, x, y, fromDirection};
      neighbours.add(arr);
   }
   
   public Tile copy(){
      Tile newTile;
      if(type == TileTypes.PLAYER){
         newTile = new Tile(x, y, type, playerNumber);
      } else {
         newTile = new Tile(x, y, type);
      }
      newTile.neighbours = neighbours;
      return newTile;
   }
   /**
		* This function retruns the coordinates of the Tiles neighbour in the given direction.
	 	* @param int direction
      * @return int[]
	*/
   public int[] getNeighbourCoordinates(int direction){
      for (int[] entry : neighbours){
         if(entry[0] == direction){
            return Arrays.copyOfRange(entry, 1, 4);
         }
      }
      return null;
   }

   @Override public String toString(){
      return "x:" + Integer.valueOf(x) + ", y:" + Integer.valueOf(y) + ", type:" + type.name();
   }
   
   /**
      * This class compares coordinates of given tiles
   */
   public static class TileCoordinateComparator implements Comparator<Tile> {
      @Override public int compare(Tile tile1, Tile tile2){
         //ascending order
         int xComp = Integer.compare(tile1.getX(), tile2.getX());
         int yComp = Integer.compare(tile1.getY(), tile2.getY());
         return (xComp == 0) ? yComp : xComp;
      }
   }
}
