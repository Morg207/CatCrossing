package utils;

import catcrossing.CatCrossing;
import entity.Cat;
import entity.Cat.Direction;

public class Helpers {
	
	public static int[] getTileInFront(Cat cat, int right, int left, int up, int down) {
		int tileX = (int)(cat.getX() + cat.getXOffset() + cat.getWidth() / 2);
		int tileY = (int) (cat.getY() + cat.getYOffset() + cat.getHeight() / 2);
		Direction direction = cat.getDirection();
		switch(direction) {
		case Direction.RIGHT -> tileX += right;
		case Direction.LEFT -> tileX -= left;
		case Direction.UP -> tileY -= up;
		case Direction.DOWN -> tileY += down; 
		}
		int col = tileX / CatCrossing.TILE_SIZE;
		int row = tileY / CatCrossing.TILE_SIZE;
		return new int[] {row, col};
	}  
	  
	public static int[] getTileInFront(Cat cat) {
		int col = (int)(cat.getX() + cat.getXOffset() + cat.getWidth() / 2) / CatCrossing.TILE_SIZE;
        int row = (int)(cat.getY() + cat.getYOffset() + cat.getHeight()) / CatCrossing.TILE_SIZE;
        Direction playerDirection = cat.getDirection();
        if(playerDirection == Direction.UP) {
        	row--;
        }else if(playerDirection == Direction.DOWN) {
        	row++;
        }else if(playerDirection == Direction.LEFT) {
        	col--;
        }else if(playerDirection == Direction.RIGHT) {
        	col++;
        }
	    return new int[] {row, col};
	}

}
