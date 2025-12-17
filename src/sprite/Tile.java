package sprite;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Tile {
	
	public enum Type{
		GRASS, DIRT, PLANT, 
		WATER, HILL, FENCE
	}
	
	public static final int TILE_TILLED = 10;
	public static final int GRASS1 = 16;
	public static final int GRASS2 = 0;
	public static final int GRASS3 = 1;
	private BufferedImage image;
	private int id;
	private boolean isSolid;
	private Type type;
	
	public Tile(int id, boolean isSolid, Type type, BufferedImage image) {
		this.id = id;
		this.image = image;
		this.isSolid = isSolid;
		this.type = type;
	}
	
	public void draw(int x, int y, Graphics2D g2d) {
		g2d.drawImage(image, null, x, y);
	}
	
	public BufferedImage getImage() {
		return image; 
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isSolid() {
		return isSolid;
	}
	
	public Type getType() {
		return type;
	}
}
