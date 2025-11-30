package entity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import animation.Animation;  
import animation.Animator;
import camera.Camera;
import sound.Sound;
import sprite.SpriteManager;
import utils.Timer;

public class Chicken extends Entity {  
	
	private static final BufferedImage[][] CHICKEN_IMAGES = SpriteManager.splitSprites(SpriteManager.loadImage
	("/Characters/Free Chicken Sprites.png"), 16, 16, 52, 52);
	private static final BufferedImage[][] FLIPPED_CHICKEN_IMAGES = SpriteManager.flipHorizontal(CHICKEN_IMAGES, 2, 4);
	private final Camera camera;
	private final Animator animator;
	private final AIPatrol aIPatrol;
	private final Timer eggTimer;
	private double eggSpawnTime;
	private double eggPercentage;
	private final Random random;
	private final List<Collectable> collectables;
	private final Sound layEggSound;

	public Chicken(double x, double y, double patrolLength, Camera camera, Cat cat, List<Collectable> collectables) {
		super(x, y, 0, 0, new Dimension(0, 0));
		this.camera = camera;
		this.collectables = collectables;
		animator = new Animator();
		List<BufferedImage> idleImagesRight = new ArrayList<>(Arrays.asList(CHICKEN_IMAGES[0]));
		List<BufferedImage> idleImagesLeft = new ArrayList<>(Arrays.asList(FLIPPED_CHICKEN_IMAGES[0]));
		idleImagesRight.remove(3);
		idleImagesRight.remove(2);
		idleImagesLeft.remove(3);
		idleImagesLeft.remove(2);
		animator.add("idle_left", new Animation(idleImagesLeft.toArray(new BufferedImage[0]), 0.5, Animation.Type.LOOP));
		animator.add("idle_right", new Animation(idleImagesRight.toArray(new BufferedImage[0]), 0.5, Animation.Type.LOOP));
		animator.add("walking_right", new Animation(CHICKEN_IMAGES[1], 0.2, Animation.Type.LOOP));
		animator.add("walking_left", new Animation(FLIPPED_CHICKEN_IMAGES[1], 0.2, Animation.Type.LOOP));
		aIPatrol = new AIPatrol(animator, cat, this, patrolLength, 5);
		random = new Random();
		eggTimer = new Timer();
		eggSpawnTime = random.nextDouble() * 100;
		this.type = EntityType.CHICKEN;
		layEggSound = new Sound("/Sounds/lay_egg.wav");
		layEggSound.setVolume(0.5f);
	}

	@Override
	public void update(double deltaTime) {
		aIPatrol.update(deltaTime);
		if(aIPatrol.getLastWalkDirection() == 1) {
			updateBoundingBoxInfo(3, 5, 37, 42);
		}else if(aIPatrol.getLastWalkDirection() == -1){
			updateBoundingBoxInfo(12, 5, 38, 42);
		}
		this.boundingBox.setBounds((int)this.x+xOffset, (int)this.y+yOffset, width, height);
		layEgg(deltaTime);
	}
	
	private void layEgg(double deltaTime) {
		eggTimer.update(deltaTime);
		eggPercentage = eggTimer.getTimer() / eggSpawnTime;
		if(eggPercentage >= 1) {
			Collectable egg = Collectable.createEgg(boundingBox.x, boundingBox.y + height / 2, camera);
			collectables.add(egg);
			layEggSound.play();
			eggPercentage = 0;
			eggTimer.setTimer(0);
			eggSpawnTime = random.nextDouble() * 100;
		}
	}
	
	private void drawEggMeter(Graphics2D g2d, double x, double y, int startBarWidth, int barWidth, int barHeight) {
		g2d.setColor(Color.BLACK);
		g2d.fillRoundRect((int)Math.round(x), (int)Math.round(y), startBarWidth+4, barHeight+4, 10, 10);
	    g2d.setColor(Color.GRAY);
	    g2d.fillRoundRect((int)Math.round(x+2), (int)Math.round(y+2), startBarWidth, barHeight, 10, 10);
	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	    g2d.setColor(Color.ORANGE);
	    g2d.fillRoundRect((int)Math.round(x+2), (int)Math.round(y+2), barWidth, barHeight, 10, 10);
	}

	@Override
	public void draw(Graphics2D g2d) {
		
		BufferedImage frame = animator.getFrame();
		g2d.drawImage(frame, null, (int)(Math.round(this.x - camera.getXOffset())), (int)Math.round((this.y - camera.getYOffset())));
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		int barHeight = 6;
		int startBarWidth = 45;
		int barWidth = (int)(eggPercentage * startBarWidth);
		if(aIPatrol.getLastWalkDirection() == 1) {
			drawEggMeter(g2d, (this.x-2-camera.getXOffset()), (this.y-10-camera.getYOffset()), startBarWidth, barWidth, barHeight);
		}else {
			drawEggMeter(g2d, (this.x+5-camera.getXOffset()), (this.y-10-camera.getYOffset()), startBarWidth, barWidth, barHeight);
		}
	}
}
