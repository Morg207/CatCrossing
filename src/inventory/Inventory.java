package inventory;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import daynight.DayNightCycle;
import font.FontLoader;
import sound.Sound;
import sprite.SpriteManager;

public class Inventory {
	
	private final HashMap<String, Integer> inventory;
	private final HashMap<String, BufferedImage> itemImages;
	private final Font font;
	private static final BufferedImage[][] TOOL_IMAGES = SpriteManager.splitSprites(SpriteManager.loadImage("/Objects/Basic tools and meterials.png"), 16, 16, 32, 32);
	private static final BufferedImage[][] MISC_IMAGES = SpriteManager.splitSprites(SpriteManager.loadImage("/Objects/Simple Milk and grass item.png"), 16, 16, 32, 32);
	private static final BufferedImage WHEAT_SEEDS_IMAGE = SpriteManager.splitSprites(SpriteManager.loadImage
			("/Objects/Basic Plants.png"), 16, 16, 32, 32)[0][0];
	private static final BufferedImage EGG_IMAGE = SpriteManager.loadImageScaled("/Objects/Egg item.png", 32, 32);
	private static final BufferedImage AXE_IMAGE = TOOL_IMAGES[0][1];
	private static final BufferedImage HOE_IMAGE = TOOL_IMAGES[0][2];
	private static final BufferedImage MILK_IMAGE = MISC_IMAGES[0][0];
	private static final BufferedImage WOOD_IMAGE = TOOL_IMAGES[1][2];
	private static final BufferedImage WATERING_CAN_IMAGE = TOOL_IMAGES[0][0];
	private final int slotCount;
	private final float spacing;
	private float alpha;
	private float invTextAlpha;
	private int slot;
	private final Color slotColour;
	private Color equiptColour;
	private boolean isOpen;
	private String selectedItem;
	private final Sound selectSound;
	private final Sound scrollSound;
	private DayNightCycle dayNightCycle;
	
	public Inventory(DayNightCycle dayNightCycle) {
		this.dayNightCycle = dayNightCycle;
		inventory = new HashMap<>();
		itemImages = new HashMap<>();
		loadItemImages();
		font = FontLoader.loadFont("/Fonts/pixel.ttf", Font.BOLD, 25);
		slotCount = 7;
		spacing = 320 / (float)slotCount;
		slotColour = new Color(50,50,50);
		equiptColour = Color.WHITE;
		selectedItem = "";
		selectSound = new Sound("/Sounds/inventory_select.wav");
		scrollSound = new Sound("/Sounds/inventory_scroll.wav");
		selectSound.setVolume(0.6f);
		scrollSound.setVolume(0.45f);
		alpha = 0.6f;
	}
	
	private void loadItemImages() {
		itemImages.put("Axe", AXE_IMAGE);
		itemImages.put("Hoe", HOE_IMAGE);
		itemImages.put("Milk", MILK_IMAGE);
		itemImages.put("Egg", EGG_IMAGE);
		itemImages.put("Wood", WOOD_IMAGE);
		itemImages.put("Can", WATERING_CAN_IMAGE);
		itemImages.put("Wheat", WHEAT_SEEDS_IMAGE);
	}
	 
	public void addItem(String newItem) {
		if (inventory.containsKey(newItem)){
		     inventory.replace(newItem, inventory.get(newItem)+1);
		}else{
		     inventory.put(newItem, 1);
		}
	}
	
	public void removeItem(String item) {
		int count = inventory.get(item);
		if(count > 0) {
		inventory.replace(item, count-1);
		}
	}
	
	public void update() {
		 alpha = dayNightCycle.getInvAlpha();
		 invTextAlpha = dayNightCycle.getInvTextAlpha();
	}
	
	public void draw(Graphics2D g2d) {
      drawBackground(g2d);
      if(inventory.size() > 0) {
    	 selectSlot(g2d);  
      }
      drawLines(g2d);
      drawInventoryItems(g2d);
   }
	
	private void selectSlot(Graphics2D g2d) {
	   g2d.setColor(slotColour);
	   int y = Math.round(135 + slot * spacing);
	   int width = 320;
	   int height = (int)spacing;
	   int arc = 15;
  	   if (slot == 0) {
  	    Path2D.Float path = new Path2D.Float();
  	    path.moveTo(140, y + arc);
  	    path.quadTo(140, y, 140 + arc, y);                 // top-left round
  	    path.lineTo(140 + width - arc, y);
  	    path.quadTo(140 + width, y, 140 + width, y + arc); // top-right round
  	    path.lineTo(140 + width, y + height);              // straight bottom edge
  	    path.lineTo(140, y + height);                      // bottom edge
  	    path.closePath();
  	    g2d.fill(path);
  	  }else {
  		g2d.fillRect(140, y, 320, (int)spacing);
  	  }
	}
	
	private void drawBackground(Graphics2D g2d) {
		 g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
	     g2d.setColor(Color.WHITE);
	     g2d.fillRoundRect(135, 130, 330, 330, 20, 20);
	     g2d.setColor(new Color(156, 114, 59));
	     g2d.fillRoundRect(140, 135, 320, 320, 20, 20);
	     
	}
	
    private void drawLines(Graphics2D g2d) {
    	g2d.setColor(Color.YELLOW);
        g2d.setStroke(new BasicStroke(3.0f));
        for(int i=1; i <= slotCount-1; i++) {
      	 int y = Math.round(135 + i * spacing);
           g2d.drawLine(140, y, 460, y);
        }
    }

    private void drawInventoryItems(Graphics2D g2d) {
    	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, invTextAlpha));
        g2d.setFont(font);
        List<Integer> valueList = new ArrayList<>(inventory.values());
        Iterator<String> items = inventory.keySet().iterator();
        FontMetrics fontMetrics = g2d.getFontMetrics(font);
        for (int slot = 0; slot < inventory.size(); slot++) {
          String item = items.next();
          int y = (int) (135 + slot * spacing + (spacing - (fontMetrics.getAscent() + fontMetrics.getDescent())) / 2 + fontMetrics.getAscent());
          g2d.setColor(Color.WHITE);
          g2d.drawString(item + " x" + valueList.get(slot), 180, y);
          if(item.equals(selectedItem)) {
        	 equiptColour = Color.RED;
          }else {
        	 equiptColour = Color.WHITE;
          }
          g2d.setColor(equiptColour);
          g2d.drawString("Equipt", 350, y);
          BufferedImage image = itemImages.get(item);
          if(image != null) {
            g2d.drawImage(image, null, 145, y-fontMetrics.getAscent());
          }
       }
    }
    
    public void highlightSelectedSlot() {
    	Iterator<String> items = inventory.keySet().iterator();
		for(int slot=0; slot<inventory.size(); slot++) {
			String item = items.next();
			if(item.equals(selectedItem)) {
				this.slot = slot;
				break;
			}
		}
    }
    
    public void decrementSlot() {
    	if(inventory.size() > 1) {
    	    slot--;
    	    scrollSound.play();
    	if (slot < 0) {
    		slot = inventory.size()-1;
    	}
      }
    }
    
    public void incrementSlot() {
    	if (inventory.size() > 1) {
    	    slot++;
    	    scrollSound.play();
    	if(slot > inventory.size()-1) {
    		slot = 0;
    	}
      }
    } 
    
    public String selectSlot() {
    	if(inventory.size() > 0) {
          List<String> listItems = new ArrayList<>(inventory.keySet());
          String selection = listItems.get(slot);
          if(!selectedItem.equals(selection)) {
             selectedItem = selection;
             selectSound.play();
          }
    	}
        return selectedItem;
    }
    
    public boolean getIsOpen() {
    	return isOpen;
    }
    
    public void toggleInventory() {
    	isOpen = !isOpen;
    	if(isOpen) {
    	highlightSelectedSlot();
    	}
    }
}
