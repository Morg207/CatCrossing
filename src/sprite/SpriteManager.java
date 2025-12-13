package sprite;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class SpriteManager {
	
	private HashMap<String,BufferedImage[][]> sprites;
	private HashMap<Integer,Tile> tileRegistry;
	
	public SpriteManager() {
		sprites = new HashMap<>();
		tileRegistry = new HashMap<>();
	}
	
	public void loadSheet(String key, BufferedImage spriteSheet, int width, int height, int targetWidth, int targetHeight) {
		sprites.put(key, SpriteManager.splitSprites(spriteSheet, width, height, targetWidth, targetHeight));
	}
	
	public BufferedImage[][] grabSheet(String key) {
		return sprites.get(key);
	} 
	
	public static BufferedImage[][] splitSprites(BufferedImage spriteSheet, int spriteWidth, int spriteHeight, int targetWidth, int targetHeight) {
		int cols = spriteSheet.getWidth() / spriteWidth;
		int rows = spriteSheet.getHeight() / spriteHeight;
		BufferedImage[][] sprites = new BufferedImage[rows][cols];
		for(int y=0; y<rows; y++) {
			for(int x=0; x<cols; x++) {
			  BufferedImage image = spriteSheet.getSubimage(x*spriteWidth, y*spriteHeight, spriteWidth, spriteHeight);
			  BufferedImage scaledImage = new BufferedImage(targetWidth,targetHeight,BufferedImage.TYPE_INT_ARGB);
			  Graphics2D g2d = scaledImage.createGraphics();
			  g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			  g2d.drawImage(image, 0, 0, targetWidth, targetHeight, null);
			  g2d.dispose();
			  sprites[y][x] = scaledImage;
			}   
		}
		return sprites; 
	}
	
	public static BufferedImage loadImage(String path) {

		try {
			return ImageIO.read(SpriteManager.class.getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}
	
	public static BufferedImage loadImageScaled(String path, int targetWidth, int targetHeight) {
		try {
			BufferedImage scaledImage = scaleImage(ImageIO.read
			(SpriteManager.class.getResource(path)), targetWidth, targetHeight);
			return scaledImage;
		}catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}
	
	public static BufferedImage scaleImage(BufferedImage image, int targetWidth, int targetHeight) {
		BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = scaledImage.createGraphics();
		g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2d.drawImage(image, 0, 0, targetWidth, targetHeight, null);
		g2d.dispose();
		return scaledImage;
	}
	
	public static BufferedImage[][] flipHorizontal(BufferedImage[][] sprites, int rows, int cols){
		BufferedImage[][] flippedSprites = new BufferedImage[rows][cols];
		for(int y=0; y<rows; y++) {
			for(int x=0; x<cols; x++) {
				BufferedImage flippedSprite = flipHorizontal(sprites[y][x]);
				flippedSprites[y][x] = flippedSprite;
			}
		}
		return flippedSprites;
	}
	
	public static BufferedImage flipHorizontal(BufferedImage sourceSprite) {
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
	    tx.translate(-sourceSprite.getWidth(), 0);

	    AffineTransformOp op = new AffineTransformOp(
	            tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

	    return op.filter(sourceSprite, null);
	}
	
	public void registerTile(int id, String sheetKey, int row, int col, boolean isSolid, Tile.Type type) {
		BufferedImage[][] sheetImages = sprites.get(sheetKey);
		if (sheetImages == null) {
            throw new IllegalArgumentException("No sprite sheet found for key: " + sheetKey);
        }

        if (tileRegistry.containsKey(id)) {
            throw new IllegalArgumentException("Tile ID already registered: " + id);
        }
        BufferedImage image = sheetImages[row][col];
        Tile tile = new Tile(id, isSolid, type, image);
        tileRegistry.put(id, tile);
	}
	
	public Tile getTileById(int id) {
		return tileRegistry.get(id);
	}
}
