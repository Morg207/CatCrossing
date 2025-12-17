package entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import camera.Camera;
import catcrossing.CatCrossing;
import entity.Cat.Item;
import font.FontLoader;
import math.Vector2;
import sound.Sound;
import sprite.SpriteManager;
import utils.Collision;
import utils.Helpers;

public class Tree extends Entity {      
	
	private static final BufferedImage[][] TREES = SpriteManager.splitSprites(SpriteManager.loadImage
			("/Objects/Basic Grass Biom things 1.png"), 16, 16, 50, 50);
	private static final BufferedImage TREE_TOP = TREES[0][0];
	private static final BufferedImage TREE_TRUNK = TREES[1][0];
	private static final BufferedImage STUMP = TREES[2][4];
	private final Camera camera;
	private final Cat cat;
	private int health;
	private int damage;
	private boolean chopped;
	private boolean tookDamage;
	private final List<Collectable> collectables;
	private final Random random;
	private final Sound damageSound;
	private final Font damageFont;
	private double damageTextVelX;
	private double damageTextVelY;
	private double damageTextX;
	private double damageTextY;
	private boolean drawDamage;
	private double bounceFactor;   

	public Tree(Vector2 position, Camera camera, Cat cat, List<Collectable> collectables) {
		super(position, 0, 50, new Dimension(50,
				30));
		this.camera = camera;
		this.collectables = collectables;
		this.cat = cat;
		health = 100;
		random = new Random();
		damageSound = new Sound("/Sounds/tree_hit.wav");
		damageFont = FontLoader.loadFont("/Fonts/pixel.ttf", Font.BOLD, 21);
		damage = random.nextInt(20, 50);
		bounceFactor = 1.0;
	}

	@Override
	public void update(double deltaTime) {
        Collision.resolveEntityCollision(cat, this, deltaTime);
        int[] inFrontOfPlayer = Helpers.getTileInFront(cat);
        int row = inFrontOfPlayer[0];
        int col = inFrontOfPlayer[1];
		takeDamage(row, col);
		if(chopped) {
			updateBoundingBoxInfo(10, 60, 30, 20);
			boundingBox.setBounds((int)position.x+xOffset, (int)position.y+yOffset, width, height);
		}
		if(drawDamage) {
		  bounceDamageText(deltaTime);
		}
	}
	
	private void bounceDamageText(double deltaTime) {
		damageTextX += damageTextVelX * deltaTime;
		damageTextY += damageTextVelY * deltaTime;
		damageTextVelY += 45;
		if(damageTextY > position.y+100) {
			bounceFactor *= 0.8;
			if(bounceFactor <= 0.1) {
				damageTextVelX = 0;
				drawDamage = false;
			}
			damageTextVelY = -damageTextVelY * bounceFactor;
		}
	} 
	
	private void setDamageInfo(double worldX, double worldY) {
		drawDamage = true;
		damage = random.nextInt(20, 50);
		bounceFactor = 1.0;
		damageTextVelX = random.nextDouble(-100, 100);
		damageTextVelY = random.nextDouble(-300, -100);
	    damageTextX = random.nextDouble(worldX, worldX+50);
	    damageTextY = position.y+50;
	} 

	private void takeDamage(int tileRow, int tileColumn) {
		int worldX = tileColumn * CatCrossing.TILE_SIZE;
        int worldY = tileRow * CatCrossing.TILE_SIZE;
		if(cat.itemInUse() == Item.AXE) {
			if(!tookDamage && worldX == position.x+xOffset && worldY == position.y+yOffset) {
				setDamageInfo(worldX, worldY);
				health -= damage;
				damageSound.play();
				if(health <= 0) {
					double woodX = worldX - random.nextDouble() * 50 + random.nextDouble() * 50;
					double woodY = worldY-50;
					Collectable wood = Collectable.createWood(new Vector2(woodX, woodY),camera);
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
        int drawX = (int)(position.x - camera.getXOffset());
        if(!chopped) {
            g2d.drawImage(TREE_TRUNK, drawX, (int)(position.y + 50 - camera.getYOffset()), null);
        } else {
            g2d.drawImage(STUMP, drawX, (int)(position.y + 50 - camera.getYOffset()), null);
        }
    } 
  
	public void drawTop(Graphics2D g2d) {
	    if(!chopped) {
	    	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	        int drawX = (int)(position.x - camera.getXOffset());
	        int drawY = (int)(position.y - camera.getYOffset());
	        g2d.drawImage(TREE_TOP, drawX, drawY, null);
	    }
	}
	
	@Override
	public void draw(Graphics2D g2d) {
		if(drawDamage) {
			g2d.setFont(damageFont);
			g2d.setColor(new Color(245, 120, 120));
			g2d.drawString(Integer.toString(damage), (int)(damageTextX-camera.getXOffset()), (int)(damageTextY-camera.getYOffset()));
		}
	}
	
	public int getRenderY() {
		return (int)position.y+50;
	}
}
