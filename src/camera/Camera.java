package camera;

import catcrossing.CatCrossing;
import entity.Entity;
import sprite.TileMap;

public class Camera {
	
	private double xOffset;
	private double yOffset;
	private TileMap tileMap;
	
	public Camera(TileMap tileMap) {
		this.tileMap = tileMap;
	}
	
	public void centerOnEntity(Entity entity) {
		xOffset = entity.getX() - CatCrossing.WIDTH / 2 + entity.getXOffset() + entity.getWidth() / 2;
		yOffset = entity.getY() - CatCrossing.HEIGHT / 2 + entity.getYOffset() + entity.getHeight() / 2;
		xOffset = Math.round(xOffset);
		yOffset = Math.round(yOffset);
		clamp();
	}
	
	private void clamp() {
		int farRightMap = tileMap.getCols() * tileMap.getTileSize() - CatCrossing.WIDTH;
		int bottomOfMap = tileMap.getRows() * tileMap.getTileSize() - CatCrossing.HEIGHT;
		if (xOffset < 0) {
			xOffset = 0;
		}
		if(yOffset < 0) {
			yOffset = 0;
		}
		if(xOffset > farRightMap) {
			xOffset = farRightMap;
		}
		if(yOffset > bottomOfMap) {
			yOffset = bottomOfMap;
		}
	}
	
	public double getXOffset() {
		return xOffset;
	}
	
	public double getYOffset() {
		return yOffset;
	}
}
