package entity;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import camera.Camera;
import sprite.SpriteManager; 

public class Collectable extends Entity {
    
	private static final BufferedImage[][] TOOL_IMAGES = SpriteManager.splitSprites
            (SpriteManager.loadImage("/Objects/Basic tools and meterials.png"), 16, 16, 47, 47);
	private static final BufferedImage[][] EGG_IMAGES = SpriteManager.splitSprites
			(SpriteManager.loadImage("/Characters/Egg_And_Nest.png"), 16, 16, 45, 45);
	private static final BufferedImage WOOD_IMAGE = SpriteManager.scaleImage(TOOL_IMAGES[1][2], 40, 40);
	private final double amplitude;
	private final double freq;
	private final double startY;
	private final Camera camera;
	private final CollectableType collectableType;
	
	public enum CollectableType {
		    Axe(TOOL_IMAGES[0][1], 0, 0, 47, 47),
		    Hoe(TOOL_IMAGES[0][2], 0, 0, 47, 47),
		    Egg(EGG_IMAGES[0][0], 12, 17, 20, 20),
		    Wood(WOOD_IMAGE, 0, 0, 40, 40);

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

	public Collectable(double x, double y, CollectableType type, Camera camera) {
		super(x, y, type.xOffset, type.yOffset, new Dimension(type.bboxWidth, type.bboxHeight));
		startY = y;
		amplitude = 7;
		freq = 0.007;
		collectableType = type;
		this.type = EntityType.COLLECTABLE;
		this.camera = camera;
	}
	
	public static Collectable createAxe(double x, double y, Camera camera) {
		return new Collectable(x, y, CollectableType.Axe, camera);
	}
	
	public static Collectable createHoe(double x, double y, Camera camera) {
		return new Collectable(x, y, CollectableType.Hoe, camera);
	}
	
	public static Collectable createEgg(double x, double y, Camera camera) {
		return new Collectable(x, y, CollectableType.Egg, camera);
	}
	
	public static Collectable createWood(double x, double y, Camera camera) {
		return new Collectable(x, y, CollectableType.Wood, camera);
	}
	
	@Override
	public void update(double deltaTime) {
		
		this.y = startY + Math.sin(System.currentTimeMillis() * freq) * amplitude;
		boundingBox.x = (int)(this.x + collectableType.xOffset);
	    boundingBox.y = (int)(this.y + collectableType.yOffset);
	}

	@Override
	public void draw(Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		g2d.drawImage(collectableType.getImage(), null, (int)(this.x-camera.getXOffset()), (int)(this.y-camera.getYOffset()));
	}
	
	public CollectableType getCollectableType() {
		return collectableType;
	}
}
