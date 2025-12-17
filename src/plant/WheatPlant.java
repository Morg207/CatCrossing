package plant;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import camera.Camera;
import catcrossing.CatCrossing;
import entity.Cat;
import math.Vector2;
import sprite.SpriteManager;
import sprite.Tile;
import sprite.TileMap;
import utils.Helpers;  

public class WheatPlant extends Plant {       

	private static final BufferedImage[] WHEAT_IMAGES = SpriteManager.splitSprites(SpriteManager.loadImage(
			"/Objects/Basic Plants.png"), 16, 16, 50, 50)[0];
	private double waterPercentage;
	private double waterTimer;
	private double waterTime;
	private double soilDryness;
	private double soilTimer;
	private double soilDryTime;
	private boolean watered;
	private Cat cat;
	private TileMap tileMap;    
	
	public WheatPlant(Vector2 position, Cat cat, TileMap tileMap, Camera camera) {
		super(WHEAT_IMAGES, position, WHEAT_IMAGES.length-3, camera);
		this.cat = cat;
		this.tileMap = tileMap;
		soilDryTime = 10;
		waterTime = 5;
	}     
	
	@Override
	public void updateInteraction(double deltaTime) {
		int[] inFront = Helpers.getTileInFront(cat, 60, 70, 10, 25);
		int row = inFront[0];
		int col = inFront[1];
		if (row < 0 || row >= tileMap.getRows() || col < 0 || col >= tileMap.getCols()) {
		        return;
		} 
		if(!watered && cat.getWatering() && row == (int)position.y / CatCrossing.TILE_SIZE
				&& col == (int)position.x / CatCrossing.TILE_SIZE) {
		   water(row, col, deltaTime);
	    }
	    if(watered) {
		   drySoil(deltaTime);
	    }
	} 
	 
	private void water(int row, int col, double deltaTime) {
		waterTimer += deltaTime;
		waterPercentage = Math.min(waterTimer / waterTime, 1.0);
		if(waterPercentage >= 1) {
		   growthStage++;
		   if(growthStage > this.maxGrowthStage) {
			 growthStage = this.maxGrowthStage;
			 this.tileMap.setPlant(row, col, null);
			 tileMap.setTile(row, col, Tile.GRASS1);
			 dead = true;
			 return;
		   }
		   soilTimer = 0;
		   watered = true;
		}
	} 
	
	private void drySoil(double deltaTime) {
	     soilTimer += deltaTime;
	     soilDryness = 1 - Math.min(soilTimer / soilDryTime, 1.0);
	     if(soilDryness <= 0) {
			watered = false;
			waterTimer = 0;
			waterPercentage = 0;
			soilDryTime = soilDryTime * 1.3;
			waterTime = waterTime * 1.2;
	     }
	} 

	@Override
	public void drawInteraction(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		g2d.setColor(new Color(62, 90, 135));
		g2d.fillRoundRect((int)Math.round(position.x + 50 - camera.getXOffset()), (int)Math.round(position.y - camera.getYOffset()), 12, 44, 10, 10);
	    g2d.setColor(new Color(152, 187, 245));
	    g2d.fillRoundRect((int)Math.round(position.x + 52 - camera.getXOffset()), (int)Math.round(position.y + 2 - camera.getYOffset()), 8, 40, 10, 10);
	    g2d.setColor(new Color(207, 223, 250));
	    drawSoilWaterMeter(g2d);
	}
	
	private void drawSoilWaterMeter(Graphics2D g2d) {
		int totalHeight = 40;
	    int barHeight = 0;
	    if(!watered) {
	        barHeight = (int)(waterPercentage * totalHeight);
	    }else {
	    	barHeight = (int)(soilDryness * totalHeight);
	    }
	    int barYTop = (int)Math.round(position.y + 2 - camera.getYOffset());
	    int barY = barYTop + (totalHeight - barHeight);
	    g2d.fillRoundRect((int)Math.round(position.x + 53 - camera.getXOffset()), barY, 6, barHeight, 10, 10);
	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}
	
	public boolean getDead() {
		return dead;
	}
}
