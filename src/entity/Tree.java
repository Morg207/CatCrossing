package entity;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import camera.Camera;
import catcrossing.CatCrossing;
import entity.Cat.Direction;
import entity.Cat.Item;
import sound.Sound;
import sprite.SpriteManager;
import utils.Collision;

public class Tree extends Entity {    
	
	private static final BufferedImage[][] TREES = SpriteManager.splitSprites(SpriteManager.loadImage
			("/Objects/Basic Grass Biom things 1.png"), 16, 16, 50, 50);
	private static final BufferedImage TREE_TOP = TREES[0][0];
	private static final BufferedImage TREE_TRUNK = TREES[1][0];
	private static final BufferedImage STUMP = TREES[2][4];
	private final Camera camera;
	private final Cat cat;
	private int health;
	private boolean chopped;
	private boolean tookDamage;
	private final List<Collectable> collectables;
	private final Random random;
	private final Sound damageSound; 

	public Tree(double x, double y, Camera camera, Cat cat, List<Collectable> collectables) {
		super(x, y, 0, 50, new Dimension(50,
				30));
		this.camera = camera;
		this.collectables = collectables;
		this.cat = cat;
		health = 100;
		random = new Random();
		damageSound = new Sound("/Sounds/tree_hit.wav");
	}

	@Override
	public void update(double deltaTime) {
        Collision.resolveEntityCollision(cat, this, deltaTime);
        int[] inFrontOfPlayer = tileInFrontOfPlayer();
        int row = inFrontOfPlayer[0];
        int col = inFrontOfPlayer[1];
		takeDamage(row, col);
		if(chopped) {
			updateBoundingBoxInfo(10, 60, 30, 20);
			boundingBox.setBounds((int)this.x+xOffset, (int)this.y+yOffset, width, height);
		}
	}
	
	private int[] tileInFrontOfPlayer() {
		int col = (int)(cat.getX() + cat.getXOffset() + cat.getWidth() / 2) / CatCrossing.TILE_SIZE;
        int row = (int)(cat.getY() + cat.getYOffset() + cat.getHeight() / 2) / CatCrossing.TILE_SIZE;
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
	 
	private void takeDamage(int tileRow, int tileColumn) {
		int worldX = tileColumn * CatCrossing.TILE_SIZE;
        int worldY = tileRow * CatCrossing.TILE_SIZE;
		if(cat.itemInUse() == Item.AXE) {
			if(!tookDamage && worldX == this.x+xOffset && worldY == this.y+yOffset) {
				health -= 25;
				damageSound.play();
				if(health <= 0) {
					double woodX = worldX - random.nextDouble() * 50 + random.nextDouble() * 50;
					double woodY = worldY-50;
					Collectable wood = Collectable.createWood(woodX, woodY,camera);
					collectables.add(wood);
					chopped = true;
				}
				tookDamage = true;
		     } 
		}else {
			tookDamage = false;
		}
	} 
	 
	public void drawBase(Graphics2D g2d) {
        int drawX = (int)(x - camera.getXOffset());
        if(!chopped) {
            g2d.drawImage(TREE_TRUNK, drawX, (int)(y + 50 - camera.getYOffset()), null);
        } else {
            g2d.drawImage(STUMP, drawX, (int)(this.y + 50 - camera.getYOffset()), null);
        }
    }

	public void drawTop(Graphics2D g2d) {
	    if(!chopped) {
	    	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	        int drawX = (int)(x - camera.getXOffset());
	        int drawY = (int)(y - camera.getYOffset());
	        g2d.drawImage(TREE_TOP, drawX, drawY, null);
	    }
	}
	
	@Override
	public void draw(Graphics2D g2d) {
		
	}
}
