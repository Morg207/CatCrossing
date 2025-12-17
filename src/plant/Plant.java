package plant;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import camera.Camera;
import interaction.Interactable;
import math.Vector2;

public abstract class Plant implements Interactable {
	
	protected Vector2 position;
	protected int growthStage;
	protected int maxGrowthStage;
	protected BufferedImage[] growthImages;
	protected Camera camera;
	protected boolean dead;  
	
	public Plant(BufferedImage[] growthImages, Vector2 position, int maxGrowthStage, Camera camera) {
		this.growthImages = growthImages;
		this.maxGrowthStage = maxGrowthStage;
		this.position = position;
		this.camera = camera;
		ArrayList<BufferedImage> listGrowthImages = new ArrayList<>(Arrays.asList(growthImages));
		listGrowthImages.remove(5);
		listGrowthImages.remove(0);
		this.growthImages = listGrowthImages.toArray(new BufferedImage[0]);
	}
	
	public void draw(Graphics2D g2d) {
		BufferedImage growthImage = growthImages[growthStage];
		g2d.drawImage(growthImage, (int)(position.x - camera.getXOffset()), (int)(position.y - camera.getYOffset()), null);
		
	}
	
	public int getMaxGrowthStage() {
		return maxGrowthStage;
	}
	
	public int getGrowthStage() {
		return growthStage;
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public boolean getDead() {
		return dead;
	}
}
