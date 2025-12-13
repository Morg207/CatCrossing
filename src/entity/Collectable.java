package entity;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import camera.Camera;
import math.Vector2;
import sprite.SpriteManager; 

public class Collectable extends Entity {
    
	private static final BufferedImage[][] TOOL_IMAGES = SpriteManager.splitSprites
            (SpriteManager.loadImage("/Objects/Basic tools and meterials.png"), 16, 16, 47, 47);
	private static final BufferedImage[][] EGG_IMAGES = SpriteManager.splitSprites
			(SpriteManager.loadImage("/Characters/Egg_And_Nest.png"), 16, 16, 45, 45);
	private static final BufferedImage WOOD_IMAGE = SpriteManager.scaleImage(TOOL_IMAGES[1][2], 40, 40);
	private static final BufferedImage WHEAT_SEEDS_IMAGE = SpriteManager.splitSprites(SpriteManager.loadImage
			("/Objects/Basic Plants.png"), 16, 16, 40, 40)[0][0];
	private final double amplitude;
	private final double freq;
	private final double startY;
	private final Camera camera;
	private final CollectableType collectableType;
	
	public enum CollectableType {
		    Axe(TOOL_IMAGES[0][1], 0, 0, 47, 47),
		    Hoe(TOOL_IMAGES[0][2], 0, 0, 47, 47),
		    Can(TOOL_IMAGES[0][0], 0, 0, 47, 47),
		    Egg(EGG_IMAGES[0][0], 12, 17, 20, 20),
		    Wood(WOOD_IMAGE, 0, 0, 40, 40),
		    Wheat(WHEAT_SEEDS_IMAGE, 0, 0, 40, 40);

		    private final BufferedImage image;
		    public final int bboxWidth, bboxHeight;
		    public final int xOffset, yOffset;

		    CollectableType(BufferedImage image, int xOffset, int yOffset, int bboxWidth, int bboxHeight) {
		        this.image = image;
		        this.bboxWidth = bboxWidth;
		        this.bboxHeight = bboxHeight;
		        this.xOffset = xOffset;
		        this.yOffset = yOffset;
		    }

		    public BufferedImage getImage() { return image; }
	}

	public Collectable(Vector2 position, CollectableType type, Camera camera) {
		super(position, type.xOffset, type.yOffset, new Dimension(type.bboxWidth, type.bboxHeight));
		startY = position.y;
		amplitude = 7;
		freq = 0.007;
		collectableType = type;
		this.type = EntityType.COLLECTABLE;
		this.camera = camera;
	}
	
	public static Collectable createAxe(Vector2 position, Camera camera) {
		return new Collectable(position, CollectableType.Axe, camera);
	}
	
	public static Collectable createHoe(Vector2 position, Camera camera) {
		return new Collectable(position, CollectableType.Hoe, camera);
	}
	
	public static Collectable createEgg(Vector2 position, Camera camera) {
		return new Collectable(position, CollectableType.Egg, camera);
	}
	
	public static Collectable createWood(Vector2 position, Camera camera) {
		return new Collectable(position, CollectableType.Wood, camera);
	}
	
	public static Collectable createWateringCan(Vector2 position, Camera camera) {
		return new Collectable(position, CollectableType.Can, camera);
	}
	
	public static Collectable createWheatSeeds(Vector2 position, Camera camera) {
		return new Collectable(position, CollectableType.Wheat, camera);
	}
	
	@Override
	public void update(double deltaTime) {
		
		position.y = startY + Math.sin(System.currentTimeMillis() * freq) * amplitude;
		boundingBox.x = (int)(position.x + collectableType.xOffset);
	    boundingBox.y = (int)(position.y + collectableType.yOffset);
	}

	@Override
	public void draw(Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		g2d.drawImage(collectableType.getImage(), null, (int)(this.position.x-camera.getXOffset()), (int)(this.position.y-camera.getYOffset()));
	}
	
	public CollectableType getCollectableType() {
		return collectableType;
	}
}
