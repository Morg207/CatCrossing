package entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import animation.Animation;
import animation.Animator;
import camera.Camera;
import font.FontLoader;
import interaction.InteractableWithInput;
import math.MathUtils;
import math.Vector2;
import sprite.SpriteManager;   

public class Cow extends Entity implements InteractableWithInput {
	
	private static final BufferedImage[][] COW_IMAGES = SpriteManager.splitSprites(SpriteManager.loadImage
    ("/Characters/Free Cow Sprites.png"), 32, 32, 103, 103);
	private static final BufferedImage[][] FLIPPED_COW_IMAGES = SpriteManager.flipHorizontal(COW_IMAGES, 2, 3);
	private final Camera camera;
	private final Animator animator;
	private final AIPatrol aIPatrol;
	private Random random;
	private Cat cat;
	private Font milkFont;
	private double milkPercentage; 
	private double milkTextPercentage;
	private double milkTimer; 
	private double milkTextTimer;
	private double milkTextTime;
	private double milkTime;
	private boolean keyHeld;
	private boolean milkMeter;
	private boolean milkText;
	private boolean restart;
	private double milkTextX;
	private double milkTextY;
	private double nextMilkTime;
	private double restartTimer;
	 
	public Cow(Vector2 position, double patrolLength, Camera camera, Cat cat) {
		super(position, 10, 40, new Dimension(85, 52));
		this.camera = camera;
		this.cat = cat;
		animator = new Animator();   
		List<BufferedImage> walkImagesRight = new ArrayList<>(Arrays.asList(FLIPPED_COW_IMAGES[1]));
		walkImagesRight.remove(walkImagesRight.size()-1);
		List<BufferedImage> walkImagesLeft = new ArrayList<>(Arrays.asList(COW_IMAGES[1]));
		walkImagesLeft.remove(walkImagesLeft.size()-1);
		animator.add("idle_left", new Animation(FLIPPED_COW_IMAGES[0], 0.5, Animation.Type.LOOP));
		animator.add("idle_right", new Animation(COW_IMAGES[0], 0.5, Animation.Type.LOOP));
		animator.add("walking_left", new Animation(walkImagesRight.toArray(new BufferedImage[0]), 0.2, Animation.Type.LOOP));
		animator.add("walking_right", new Animation(walkImagesLeft.toArray(new BufferedImage[0]), 0.2, Animation.Type.LOOP));
		aIPatrol = new AIPatrol(animator, cat, this, patrolLength, 5);
		milkTime = 5;
		milkMeter = true;
		milkTextTime = 1.2; 
		milkFont = FontLoader.loadFont("/Fonts/pixel.ttf", Font.BOLD, 18);
		random = new Random();
		this.type = EntityType.COW;
	}   

	@Override
	public void update(double deltaTime) {
		aIPatrol.update(deltaTime);
		this.boundingBox.setBounds((int)position.x+xOffset, (int)position.y+yOffset, width, height);
	}
	  
	@Override
	public void draw(Graphics2D g2d) {
		BufferedImage frame = animator.getFrame();
		g2d.drawImage(frame, null, (int)Math.round((position.x - camera.getXOffset())), (int)Math.round((position.y-camera.getYOffset())));
	}
	
	@Override
	public void updateInteraction(double deltaTime) {
	    if (cat.getInventory().getIsOpen()) {
	        resetOnInventoryOpen();
	        return;
	    }
	    updateRestartCooldown(deltaTime);
	    updateMilking(deltaTime);
	    updateMilkText(deltaTime);
	    handleMilkCompletion();
	}
  
	@Override
	public void drawInteraction(Graphics2D g2d) {

	    if (milkMeter && vel.x == 0) {
	        drawMilkProgressBar(g2d);
	    }if (milkText) {
	        drawMilkText(g2d);
	    }
	}

	private void updateRestartCooldown(double deltaTime) {
	    if (!restart) return;
	    restartTimer += deltaTime;
	    if (restartTimer > nextMilkTime) {
	        restart = false;
	        restartTimer = 0;
	        milkMeter = true;
	        double distanceToPlayer = MathUtils.distance(position.x + xOffset + width/2, position.y + yOffset+height/2,
	        		cat.getX() + cat.getXOffset() + cat.getWidth()/2, cat.getY() + cat.getYOffset() + cat.getHeight()/2);
	        if(distanceToPlayer <= 100) {
	           cat.setInteracting(true);
	        }
	    }
	}

	private void updateMilking(double deltaTime) {
	    if (!milkMeter || vel.x != 0) {
	        resetMilkProgress();
	        return;
	    }
	    milkTextX = position.x + 5;
	    milkTextY = position.y + 28;
	    if (keyHeld) {
	        milkTimer += deltaTime;
	        milkPercentage = Math.min(milkTimer / milkTime, 1.0);
	    } else {
	        resetMilkProgress();
	    }
	} 

	private void resetMilkProgress() {
	    milkTimer = 0;
	    milkPercentage = 0;
	}

	private void updateMilkText(double deltaTime) {
	    if (!milkText) return;

	    milkTextTimer += deltaTime;
	    milkTextPercentage = Math.min(milkTextTimer / milkTextTime, 1.0);

	    if (milkTextPercentage >= 1) {
	        endMilkText();
	    }
	}

	private void endMilkText() {
	    milkText = false;
	    milkTextTimer = 0;
	    milkTextPercentage = 0;
	    restart = true;
	}

	private void handleMilkCompletion() {
	    if (milkPercentage < 1 || !milkMeter) return;
	    milkMeter = false;
	    milkText = true;
	    cat.getInventory().addItem("Milk");
	    nextMilkTime = random.nextDouble() * 100;
	    resetMilkProgress();
	} 

	private void resetOnInventoryOpen() {
	    keyHeld = false;
	    resetMilkProgress();
	}

	private void drawMilkProgressBar(Graphics2D g2d) {
	    final int barHeight = 8;
	    final int totalWidth = 52;
	    int drawX = (int)Math.round((position.x + 27 - camera.getXOffset()));
	    int drawY = (int)Math.round((position.y + 20 - camera.getYOffset()));
	    int barWidth = (int) (milkPercentage * totalWidth);
	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
	    g2d.setColor(Color.BLACK);
	    g2d.fillRoundRect(drawX - 2, drawY - 2, totalWidth + 4, barHeight + 4, 15, 15);
	    g2d.setColor(Color.GRAY);
	    g2d.fillRoundRect(drawX, drawY, totalWidth, barHeight, 15, 15);
	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	    g2d.setColor(Color.PINK);
	    g2d.fillRoundRect(drawX, drawY, barWidth, barHeight, 15, 15);
	}

	private void drawMilkText(Graphics2D g2d) {
	    float alpha = (float) (1.0 - milkTextPercentage);
	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	    g2d.setFont(milkFont);
	    g2d.setColor(Color.WHITE);
	    g2d.drawString("+1 milk added", (int)Math.round((milkTextX - camera.getXOffset())),(int)Math.round((milkTextY - camera.getYOffset())));
	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}

	@Override
	public void processKeyPressed(KeyEvent key) {
		if(key.getKeyCode() == KeyEvent.VK_E && vel.x == 0) {
			keyHeld = true;
		}
	}

	@Override
	public void processKeyReleased(KeyEvent key) {
		if(key.getKeyCode() == KeyEvent.VK_E && vel.x == 0) {
			keyHeld = false;
		}
	}
}
