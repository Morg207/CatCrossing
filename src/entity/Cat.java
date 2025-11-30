package entity;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import animation.Animation;
import animation.Animator;
import camera.Camera;
import catcrossing.CatCrossing;
import inventory.Inventory;
import sound.Sound;
import sprite.SpriteManager;
import sprite.Tile;
import sprite.TileMap;

public class Cat extends Entity implements KeyListener {
	
	public enum Direction{
		LEFT, RIGHT, UP, DOWN;
	}
	
	public enum Item{
		AXE, HOE, NOITEM
	}
	
	private enum ItemEquipt{
		AXE, HOE, NOITEM
	}
	
	private static final BufferedImage[][] ACTION_IMAGES = SpriteManager.splitSprites(SpriteManager.loadImage
    ("/Characters/Basic Charakter Actions.png"), 48, 48, 150, 150);
	private Animator moveAnimator;
	private Animator actionAnimator;
	private Camera camera;
	private TileMap tileMap;
	private Inventory inventory;
	private boolean doingAction;
	private boolean interacting;
	private Direction direction;
	private ItemEquipt equiptedItem;
	private Item usedItem;
	private Sound hoeSound;
	
	public Cat(BufferedImage[][] moveImages, double x, double y, Camera camera, TileMap tileMap) {
		super(x, y, 55, 50, new Dimension(40, 50));
		this.tileMap = tileMap;
		this.camera = camera;
		initAnimators(moveImages);
		direction = Direction.DOWN;
		usedItem = Item.NOITEM;
		equiptedItem = ItemEquipt.NOITEM;
		boundingBox = new Rectangle((int)this.x+xOffset,(int)this.y+yOffset,width,height);
		inventory = new Inventory();
		hoeSound = new Sound("/Sounds/hoe.wav");
		hoeSound.setVolume(0.6f);
	}
	
	private void initAnimators(BufferedImage[][] moveImages) {
		moveAnimator = new Animator();
		moveAnimator.add("walk_down", new Animation(moveImages[0], 0.1, Animation.Type.LOOP));
		moveAnimator.add("walk_up",   new Animation(moveImages[1], 0.1, Animation.Type.LOOP));
		moveAnimator.add("walk_left", new Animation(moveImages[2], 0.1, Animation.Type.LOOP));
		moveAnimator.add("walk_right",new Animation(moveImages[3], 0.1, Animation.Type.LOOP));
		moveAnimator.add("idle_down", new Animation(new BufferedImage[]{moveImages[0][0]}, 0.1, Animation.Type.LOOP));
		moveAnimator.add("idle_up",   new Animation(new BufferedImage[]{moveImages[1][0]}, 0.1, Animation.Type.LOOP));
		moveAnimator.add("idle_left", new Animation(new BufferedImage[]{moveImages[2][0]}, 0.1, Animation.Type.LOOP));
		moveAnimator.add("idle_right",new Animation(new BufferedImage[]{moveImages[3][0]}, 0.1, Animation.Type.LOOP));
		actionAnimator = new Animator();
		actionAnimator.add("axe_left",  new Animation(ACTION_IMAGES[6], 0.2, Animation.Type.ONCE));
		actionAnimator.add("axe_right", new Animation(ACTION_IMAGES[7], 0.2, Animation.Type.ONCE));
		actionAnimator.add("axe_up", new Animation(ACTION_IMAGES[5], 0.2, Animation.Type.ONCE));
		actionAnimator.add("axe_down", new Animation(ACTION_IMAGES[4], 0.2, Animation.Type.ONCE));
		actionAnimator.add("hoe_left",  new Animation(ACTION_IMAGES[2], 0.2, Animation.Type.ONCE));
		actionAnimator.add("hoe_right", new Animation(ACTION_IMAGES[3], 0.2, Animation.Type.ONCE));
		actionAnimator.add("hoe_up", new Animation(ACTION_IMAGES[1], 0.2, Animation.Type.ONCE));
		actionAnimator.add("hoe_down", new Animation(ACTION_IMAGES[0], 0.2, Animation.Type.ONCE));
	}
	
	@Override
	public void update(double deltaTime) {
		   
		   if(doingAction) {
			   if (usedItem == Item.AXE) {
		            actionAnimator.play("axe_" + direction.name().toLowerCase());
			   }
		       else if (usedItem == Item.HOE) {
		            actionAnimator.play("hoe_" + direction.name().toLowerCase());
			        tillGround();
		       }

		        actionAnimator.update(deltaTime);

		        if (actionAnimator.getCurrent().isFinished()) {
		            doingAction = false;
		            usedItem = Item.NOITEM;
		            actionAnimator.getCurrent().reset();
		        }
		        return;
		   } 
		   handleHorizontalCollisions(deltaTime);
		   handleVerticalCollisions(deltaTime);   
		   move(deltaTime);
		   moveAnimator.update(deltaTime);
	}
	
	private void tillGround() {
		int tileX = (int)(this.x + xOffset + boundingBox.width / 2);
		int tileY = (int) (this.y + yOffset + boundingBox.height / 2);
		switch(direction) {
		case Direction.RIGHT -> tileX += boundingBox.width / 2;
		case Direction.LEFT -> tileX -= boundingBox.width / 2;
		case Direction.UP -> tileY -= boundingBox.height / 2;
		case Direction.DOWN -> tileY += boundingBox.height / 2;
		}
		int col = tileX / CatCrossing.TILE_SIZE;
		int row = tileY / CatCrossing.TILE_SIZE;
		if (row < 0 || row >= tileMap.getRows() || col < 0 || col >= tileMap.getCols()) {
		        return;
		}
		if(tileMap.getTile(row, col).getType() == Tile.Type.GRASS) {
	    	tileMap.setTile(row, col, Tile.TILE_TILLED);
	    	hoeSound.play();
	    } 
	}
	
	private void move(double deltaTime) {
		if (velX > 0) {
	        direction = Direction.RIGHT;
	        moveAnimator.play("walk_right");
	    } else if (velX < 0) {
	        direction = Direction.LEFT;
	        moveAnimator.play("walk_left");
	    } else if (velY > 0) {
	        direction = Direction.DOWN;
	        moveAnimator.play("walk_down");
	    } else if (velY < 0) {
	        direction = Direction.UP;
	        moveAnimator.play("walk_up");
	    }else {
	    	moveAnimator.play("idle_" + direction.name().toLowerCase());
	    }
	    boundingBox.setBounds((int)(this.x + xOffset), (int)(this.y + yOffset), width, height);
	}
	 
	private void handleHorizontalCollisions(double deltaTime) {
	    double newX = this.x + velX * deltaTime;
	    Rectangle bbox = new Rectangle((int)(newX + xOffset), (int)(y + yOffset), width, height);

	    int topTile = (int)(bbox.y) / CatCrossing.TILE_SIZE;
	    int bottomTile = (int)(bbox.y + bbox.height - 1) / CatCrossing.TILE_SIZE;

	    if (velX > 0) { // moving right
	        int rightEdge = (int)(newX + bbox.width + xOffset);
	        int rightTile = rightEdge / CatCrossing.TILE_SIZE;

	        for (int ty = topTile; ty <= bottomTile; ty++) {
	            if (tileMap.isSolid(rightTile, ty)) {
	                newX = rightTile * CatCrossing.TILE_SIZE - bbox.width - xOffset - 0.1; 
	                break;
	            }
	        }

	    } else if (velX < 0) { // moving left
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
      this.x = newX;
     }
	
	  private void handleVerticalCollisions(double deltaTime) {
		    double newY = this.y + velY * deltaTime;
		    Rectangle bbox = new Rectangle((int)(x + xOffset), (int)(newY + yOffset), width, height);

		    int leftTile = (int)(bbox.x) / CatCrossing.TILE_SIZE;
		    int rightTile = (int)(bbox.x + bbox.width - 1) / CatCrossing.TILE_SIZE;

		    if (velY > 0) { // moving down
		        int bottomEdge = (int)(newY + bbox.height + yOffset);
		        int bottomTile = bottomEdge / CatCrossing.TILE_SIZE;

		        for (int tx = leftTile; tx <= rightTile; tx++) {
		            if (tileMap.isSolid(tx, bottomTile)) {
		                newY = bottomTile * CatCrossing.TILE_SIZE - bbox.height - yOffset - 0.1; 
		                break;
		            }
		        }

		    } else if (velY < 0) { // moving up
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
		this.y = newY;
	  }

	@Override
	public void draw(Graphics2D g2d) {
		BufferedImage frame = doingAction ? actionAnimator.getFrame() : moveAnimator.getFrame();
		g2d.drawImage(frame, null, (int)Math.round((this.x-camera.getXOffset())), (int)Math.round((this.y-camera.getYOffset())));
	}
	
	public void showInventory(Graphics2D g2d) {
		if(inventory.getIsOpen()) {
			inventory.draw(g2d);
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
			velX = 250;
			velY = 0;
		}else if(e.getKeyCode() == KeyEvent.VK_A) {
			velX = -250;
			velY = 0;
		}else if(e.getKeyCode() == KeyEvent.VK_W) {
			velY = -250;
			velX = 0;
		}else if(e.getKeyCode() == KeyEvent.VK_S) {
			velY = 250;
			velX = 0;
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
		          usedItem = Item.AXE;
		          doingAction = true;
			      break;
			  }
			  case ItemEquipt.HOE: {
				  usedItem = Item.HOE;
				  doingAction = true;
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
			velX = 0;
		}else if(e.getKeyCode() == KeyEvent.VK_A) {
			velX = 0;
		}else if(e.getKeyCode() == KeyEvent.VK_W) {
			velY = 0;
		}else if(e.getKeyCode() == KeyEvent.VK_S) {
			velY = 0;
		}
	}
	
	private void equiptSelectedTool() {
		String selectedItem = inventory.selectSlot();
		if(selectedItem.equals("Axe")) {
			equiptedItem = ItemEquipt.AXE;
		}else if(selectedItem.equals("Hoe")) {
			equiptedItem = ItemEquipt.HOE;
		}else if(selectedItem.equals("Egg")) {
			equiptedItem = ItemEquipt.NOITEM;
		}else if(selectedItem.equals("Milk")) {
			equiptedItem = ItemEquipt.NOITEM;
		}else if(selectedItem.equals("Wood")) {
			equiptedItem = ItemEquipt.NOITEM;
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
}

