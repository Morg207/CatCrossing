package entity;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.List;
import animation.Animation;
import animation.Animator;
import camera.Camera; 
import catcrossing.CatCrossing;
import daynight.DayNightCycle;
import interaction.Interactable;
import inventory.Inventory;
import math.Vector2;
import particle.ParticleSystem;
import plant.Plant;
import plant.WheatPlant;
import sound.Sound;
import sprite.SpriteManager;
import sprite.Tile;
import sprite.TileMap;
import utils.Helpers;
 
public class Cat extends Entity implements KeyListener {
	
	public enum Direction{
		LEFT, RIGHT, UP, DOWN; 
	}
	 
	public enum Item{  
		AXE, HOE, CAN, WHEAT_SEEDS, NOITEM
	} 
	  
	private enum ItemEquipt{
		AXE, HOE, CAN, WHEAT_SEEDS, NOITEM
	}
	 
	private static final BufferedImage[][] ACTION_IMAGES = SpriteManager.splitSprites(SpriteManager.loadImage
    ("/Characters/Basic Charakter Actions.png"), 48, 48, 150, 150);
	private static final BufferedImage STAMINA_FULL_IMAGE = SpriteManager.loadImageScaled("/Hud/stamina full.png", 29, 29);
	private static final BufferedImage STAMINA_EMPTY_IMAGE = SpriteManager.loadImageScaled("/Hud/stamina empty.png", 29, 29);
	private Animator moveAnimator;
	private Animator actionAnimator;
	private Camera camera;
	private TileMap tileMap;
	private Inventory inventory;
	private List<Interactable> interactables;
	private boolean doingAction;
	private boolean interacting;
	private Direction direction;
	private ItemEquipt equiptedItem;
	private Item usedItem;
	private Sound hoeSound;
	private int stamina;
	private final int maxStamina;
	private double staminaRegenTime;
	private double staminaRegenCoolDown;
	private ParticleSystem wateringCanSystem;
	private boolean watering;
	private DayNightCycle dayNightCycle;
	
	public Cat(BufferedImage[][] moveImages, Vector2 position, Camera camera, TileMap tileMap, DayNightCycle dayNightCycle, List<Interactable> interactables) {
		super(position, 55, 50, new Dimension(40, 50));
		this.tileMap = tileMap;
		this.camera = camera;
		this.interactables = interactables;
		this.dayNightCycle = dayNightCycle;
		initAnimators(moveImages);
		direction = Direction.DOWN;
		usedItem = Item.NOITEM;
		equiptedItem = ItemEquipt.NOITEM;
		boundingBox = new Rectangle((int)position.x+xOffset,(int)position.y+yOffset,width,height);
		inventory = new Inventory(dayNightCycle);
		hoeSound = new Sound("/Sounds/hoe.wav");
		hoeSound.setVolume(0.6f);
		maxStamina = 6;
		stamina = 6;
		staminaRegenTime = 0.8; 
		staminaRegenCoolDown = staminaRegenTime;
		wateringCanSystem = new ParticleSystem(25, this, camera);
	}
	
	private void initMoveAnimator(BufferedImage[][] moveImages) {
		moveAnimator = new Animator();
		moveAnimator.add("walk_down", new Animation(moveImages[0], 0.1, Animation.Type.LOOP));
		moveAnimator.add("walk_up",   new Animation(moveImages[1], 0.1, Animation.Type.LOOP));
		moveAnimator.add("walk_left", new Animation(moveImages[2], 0.1, Animation.Type.LOOP));
		moveAnimator.add("walk_right",new Animation(moveImages[3], 0.1, Animation.Type.LOOP));
		moveAnimator.add("idle_down", new Animation(new BufferedImage[]{moveImages[0][0]}, 0.1, Animation.Type.LOOP));
		moveAnimator.add("idle_up",   new Animation(new BufferedImage[]{moveImages[1][0]}, 0.1, Animation.Type.LOOP));
		moveAnimator.add("idle_left", new Animation(new BufferedImage[]{moveImages[2][0]}, 0.1, Animation.Type.LOOP));
		moveAnimator.add("idle_right",new Animation(new BufferedImage[]{moveImages[3][0]}, 0.1, Animation.Type.LOOP));
	}
	
	private void initActionAnimator() {
		actionAnimator = new Animator();
		actionAnimator.add("axe_left",  new Animation(ACTION_IMAGES[6], 0.2, Animation.Type.ONCE));
		actionAnimator.add("axe_right", new Animation(ACTION_IMAGES[7], 0.2, Animation.Type.ONCE));
		actionAnimator.add("axe_up", new Animation(ACTION_IMAGES[5], 0.2, Animation.Type.ONCE));
		actionAnimator.add("axe_down", new Animation(ACTION_IMAGES[4], 0.2, Animation.Type.ONCE));
		actionAnimator.add("hoe_left",  new Animation(ACTION_IMAGES[2], 0.2, Animation.Type.ONCE));
		actionAnimator.add("hoe_right", new Animation(ACTION_IMAGES[3], 0.2, Animation.Type.ONCE));
		actionAnimator.add("hoe_up", new Animation(ACTION_IMAGES[1], 0.2, Animation.Type.ONCE));
		actionAnimator.add("hoe_down", new Animation(ACTION_IMAGES[0], 0.2, Animation.Type.ONCE));
	    actionAnimator.add("watering_left", new Animation(ACTION_IMAGES[10], 0.2, Animation.Type.ONCE));
	    actionAnimator.add("watering_right", new Animation(ACTION_IMAGES[11], 0.2, Animation.Type.ONCE));
	    actionAnimator.add("watering_up", new Animation(ACTION_IMAGES[9], 0.2, Animation.Type.ONCE));
	    actionAnimator.add("watering_down", new Animation(ACTION_IMAGES[8], 0.2, Animation.Type.ONCE));
	}
	 
	private void initAnimators(BufferedImage[][] moveImages) {
		initMoveAnimator(moveImages);
		initActionAnimator();
	}  
	 
	@Override 
	public void update(double deltaTime) {
		   
		   if(watering) {
			   wateringCanSystem.update(deltaTime);
		   }else {
			   wateringCanSystem.removeParticles(deltaTime);
		   }
		   if(doingAction) {
			    useTool(deltaTime);
		        return;
		   }
		   plantSeeds();
		   handleHorizontalCollisions(deltaTime);
		   handleVerticalCollisions(deltaTime);   
		   move(deltaTime);
		   moveAnimator.update(deltaTime);
		   regenStamina(deltaTime);
		   inventory.update();
	}
	
	private void playToolAnimations(double deltaTime){
		if (usedItem == Item.AXE) {
            actionAnimator.play("axe_" + direction.name().toLowerCase());
	   }
       else if (usedItem == Item.HOE) {
            actionAnimator.play("hoe_" + direction.name().toLowerCase());
	        tillGround();
       }
       else if(usedItem == Item.CAN) {
    	   actionAnimator.play("watering_" + direction.name().toLowerCase());
       }
        actionAnimator.update(deltaTime);
	}
	
	private void useTool(double deltaTime) {
		playToolAnimations(deltaTime);
        if (actionAnimator.getCurrent().isFinished() && usedItem != Item.CAN) {
        	loseStamina();
            doingAction = false;
            usedItem = Item.NOITEM;
            actionAnimator.getCurrent().reset();
        }else if(actionAnimator.getCurrent().isFinished() && usedItem == Item.CAN) {
        	if(watering) {
        	    actionAnimator.getCurrent().reset();
        	}else {
        		doingAction = false;
	        	usedItem = Item.NOITEM;
	        	actionAnimator.getCurrent().reset();
        	}
        }
	}
	
	private void plantSeeds() {
		int[] inFront = Helpers.getTileInFront(this, boundingBox.width / 2, boundingBox.width / 2,
				boundingBox.height / 2, boundingBox.height / 2);
		int row = inFront[0];
		int col = inFront[1];
		if (row < 0 || row >= tileMap.getRows() || col < 0 || col >= tileMap.getCols()) {
		        return;
		}
		spawnPlant(row, col);
	}
	
	private void spawnPlant(int row, int col) {
		int tileId = tileMap.getTileMapId(row, col);
		if(tileId == -1) {
			return;
		}
		Plant plant = tileMap.getPlant(row, col);
		if(plant == null && tileId == Tile.TILE_TILLED && usedItem == Item.WHEAT_SEEDS) {
			WheatPlant wheatPlant = new WheatPlant(new Vector2(col*CatCrossing.TILE_SIZE, row*CatCrossing.TILE_SIZE),
			this, tileMap, camera);
	    	tileMap.setPlant(row, col, wheatPlant);
	    	interactables.add(wheatPlant);
	    	usedItem = Item.NOITEM;
	    }
		usedItem = Item.NOITEM;
	}
	
	private void tillGround() {
		int[] inFront = Helpers.getTileInFront(this, boundingBox.width / 2, boundingBox.width / 2,
				boundingBox.height / 2, boundingBox.height / 2);
		int row = inFront[0];
		int col = inFront[1];
		if (row < 0 || row >= tileMap.getRows() || col < 0 || col >= tileMap.getCols()) {
		        return;
		}
		if(tileMap.getTile(row, col).getType() == Tile.Type.GRASS) {
	    	tileMap.setTile(row, col, Tile.TILE_TILLED);
	    	hoeSound.play();
	    } 
	} 
	
	private void move(double deltaTime) {
		if (vel.x > 0) {
	        direction = Direction.RIGHT;
	        moveAnimator.play("walk_right");
	    } else if (vel.x < 0) {
	        direction = Direction.LEFT;
	        moveAnimator.play("walk_left");
	    } else if (vel.y > 0) {
	        direction = Direction.DOWN;
	        moveAnimator.play("walk_down");
	    } else if (vel.y < 0) {
	        direction = Direction.UP;
	        moveAnimator.play("walk_up");
	    }else {
	    	moveAnimator.play("idle_" + direction.name().toLowerCase());
	    }
	    boundingBox.setBounds((int)(position.x + xOffset), (int)(position.y + yOffset), width, height);
	}
	 
	private void handleHorizontalCollisions(double deltaTime) {
	    double newX = position.x + vel.x * deltaTime;
	    Rectangle bbox = new Rectangle((int)(newX + xOffset), (int)(position.y + yOffset), width, height);

	    int topTile = (int)(bbox.y) / CatCrossing.TILE_SIZE;
	    int bottomTile = (int)(bbox.y + bbox.height - 1) / CatCrossing.TILE_SIZE;

	    if (vel.x > 0) { // moving right
	        int rightEdge = (int)(newX + bbox.width + xOffset);
	        int rightTile = rightEdge / CatCrossing.TILE_SIZE;

	        for (int ty = topTile; ty <= bottomTile; ty++) {
	            if (tileMap.isSolid(rightTile, ty)) {
	                newX = rightTile * CatCrossing.TILE_SIZE - bbox.width - xOffset - 0.1; 
	                break;
	            }
	        }

	    } else if (vel.x < 0) { // moving left
	        int leftEdge = (int)(newX + xOffset);
	        int leftTile = leftEdge / CatCrossing.TILE_SIZE;
	        if(leftEdge < 0) {
	        	newX = -xOffset;
	        }else {
	        for (int ty = topTile; ty <= bottomTile; ty++) {
	        	
	            if (tileMap.isSolid(leftTile, ty)) {
	                newX = (leftTile + 1) * CatCrossing.TILE_SIZE - xOffset + 0.1;
	                break;
	            }
	         }
	       }
	    }
      position.x = newX;
     }
	
	  private void handleVerticalCollisions(double deltaTime) {
		    double newY = position.y + vel.y * deltaTime;
		    Rectangle bbox = new Rectangle((int)(position.x + xOffset), (int)(newY + yOffset), width, height);

		    int leftTile = (int)(bbox.x) / CatCrossing.TILE_SIZE;
		    int rightTile = (int)(bbox.x + bbox.width - 1) / CatCrossing.TILE_SIZE;

		    if (vel.y > 0) { // moving down
		        int bottomEdge = (int)(newY + bbox.height + yOffset);
		        int bottomTile = bottomEdge / CatCrossing.TILE_SIZE;

		        for (int tx = leftTile; tx <= rightTile; tx++) {
		            if (tileMap.isSolid(tx, bottomTile)) {
		                newY = bottomTile * CatCrossing.TILE_SIZE - bbox.height - yOffset - 0.1; 
		                break;
		            }
		        }

		    } else if (vel.y < 0) { // moving up
		        int topEdge = (int)(newY + yOffset);
		        int topTile = topEdge / CatCrossing.TILE_SIZE;
                if(topEdge < 0) {
                	newY = -yOffset;
                }else {
		        for (int tx = leftTile; tx <= rightTile; tx++) {
		            if (tileMap.isSolid(tx, topTile)) {
		                newY = (topTile + 1) * CatCrossing.TILE_SIZE - yOffset + 0.1;
		                break;
		            } 
		        }
               }
           
		    }
		position.y = newY;
	  }
	  
	public void drawStamina(Graphics2D g2d) {
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, dayNightCycle.getStaminaAlpha()));
		for(int i=0; i<maxStamina; i++) {
			if(i < stamina) {
			    g2d.drawImage(STAMINA_FULL_IMAGE, 10 + i * 35, 560, null);
			}else {
				g2d.drawImage(STAMINA_EMPTY_IMAGE, 10 + i * 35,  560, null);
			}
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		
	}

	@Override
	public void draw(Graphics2D g2d) {
		BufferedImage frame = doingAction ? actionAnimator.getFrame() : moveAnimator.getFrame();
		if(wateringCanSystem.particleAmount() > 0) {
		wateringCanSystem.draw(g2d);
		}
		g2d.drawImage(frame, null, (int)Math.round((position.x-camera.getXOffset())), (int)Math.round((position.y-camera.getYOffset())));
	}
	
	public void showInventory(Graphics2D g2d) {
		if(inventory.getIsOpen()) {
			inventory.draw(g2d);
		}
	}  
	
	private void alignWateringCanParticles() {
		if(direction == Direction.RIGHT) {
			  wateringCanSystem.setPos(new Vector2(position.x + xOffset + width * 2, position.y + 50));
		  }else if(direction == Direction.LEFT) {
			  wateringCanSystem.setPos(new Vector2(position.x + xOffset - 50, position.y + 50));
		  }else if(direction == Direction.UP) {
			  wateringCanSystem.setPos(new Vector2(position.x + xOffset + 35, position.y + yOffset));
		  }else if(direction == Direction.DOWN) {
			  wateringCanSystem.setPos(new Vector2(position.x + xOffset, position.y + yOffset));
		  }
	}
 
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_Q) {
			inventory.toggleInventory();
		}
		if(!inventory.getIsOpen()) {
		  handleMoveEvents(e);
		  handleToolEvents(e);
		}else {
	      handleInventoryEvents(e);
		}
	}
	 
	private void handleMoveEvents(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_D) {
			vel.x = 250;
			vel.y = 0;
		}else if(e.getKeyCode() == KeyEvent.VK_A) {
			vel.x = -250;
			vel.y = 0;
		}else if(e.getKeyCode() == KeyEvent.VK_W) {
			vel.y = -250;
			vel.x = 0;
		}else if(e.getKeyCode() == KeyEvent.VK_S) {
			vel.y = 250;
			vel.x = 0;
		}
	}
	 
	private void handleInventoryEvents(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_E) {
		    equiptSelectedTool();
		}
		if(e.getKeyCode() == KeyEvent.VK_W) {
			inventory.decrementSlot();
		}
		if(e.getKeyCode() == KeyEvent.VK_S) {
			inventory.incrementSlot();
		}
	}  
	
	private void handleToolEvents(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_E && !interacting) {
		   switch(equiptedItem) {
			  case ItemEquipt.AXE:{
				  if(stamina > 0) {
		          usedItem = Item.AXE;
		          doingAction = true;
				  }
			      break;
			  }
			  case ItemEquipt.HOE: {
				  if(stamina > 0) {
				  usedItem = Item.HOE;
				  doingAction = true;
				  }
				  break;
			  }
			  case ItemEquipt.CAN:{
				  usedItem = Item.CAN;
				  doingAction = true;
				  watering = true;
				  alignWateringCanParticles();
				  break;
			  }
			  case ItemEquipt.WHEAT_SEEDS:{
				  usedItem = Item.WHEAT_SEEDS;
				  break;
			  }
			  case ItemEquipt.NOITEM: { 
				  usedItem = Item.NOITEM;
				  break;
			  }
		   }
		}
	} 

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_D) {
			vel.x = 0;
		}else if(e.getKeyCode() == KeyEvent.VK_A) {
			vel.x = 0;
		}else if(e.getKeyCode() == KeyEvent.VK_W) {
			vel.y = 0;
		}else if(e.getKeyCode() == KeyEvent.VK_S) {
			vel.y = 0;
		}else if(e.getKeyCode() == KeyEvent.VK_E) {
			watering = false;
		}
	}
	 
	private void equiptSelectedTool() {
		String selectedItem = inventory.selectSlot();
		if(selectedItem.equals("Axe")) {
			equiptedItem = ItemEquipt.AXE;
		}else if(selectedItem.equals("Hoe")) {
			equiptedItem = ItemEquipt.HOE;
		}else if(selectedItem.equals("Can")) {
			equiptedItem = ItemEquipt.CAN;
		}else if(selectedItem.equals("Milk")) {
			equiptedItem = ItemEquipt.NOITEM;
		}else if(selectedItem.equals("Wood")) {
			equiptedItem = ItemEquipt.NOITEM;
		}else if(selectedItem.equals("Egg")) {
			equiptedItem = ItemEquipt.NOITEM;
		}else if(selectedItem.equals("Wheat")) {
			equiptedItem = ItemEquipt.WHEAT_SEEDS;
		}
	}   
	 
	private void loseStamina() {
		stamina--;
		stamina = Math.max(stamina, 0);
		if(stamina == 0) {
			staminaRegenCoolDown = 2; //Regen time is 2 seconds for the first lightning bolt.
		}else {                       //After the first, regen time is 0.8 seconds per bolt.
			staminaRegenCoolDown = staminaRegenTime;
		}
	} 
	
	private void regenStamina(double deltaTime) {
		if(stamina < maxStamina) {
			staminaRegenCoolDown -= deltaTime;
			if(staminaRegenCoolDown <= 0) {
				stamina++;
				if(stamina == 1) { 
					staminaRegenTime = 0.8;
				}
				staminaRegenCoolDown += staminaRegenTime; 
			}
		}else {
			staminaRegenCoolDown = staminaRegenTime;
		}
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public boolean getInteracting() {
		return interacting;
	}
	
	public void setInteracting(boolean interacting) {
		this.interacting = interacting;
	}
	
	public Item itemInUse() {
		return usedItem;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public boolean getWatering() {
		return watering;
	}
}

