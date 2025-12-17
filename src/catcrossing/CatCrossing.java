package catcrossing;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame; 
import camera.Camera;
import daynight.DayNightCycle;
import entity.Cat;
import entity.Chicken;
import entity.Collectable;
import entity.Cow;
import entity.Entity;
import entity.Tree;
import interaction.Interactable;
import interaction.InteractionSystem;
import math.Vector2;
import sound.Sound;
import sprite.SpriteManager;
import sprite.Tile;
import sprite.TileMap;

public class CatCrossing extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L; 
	public static final int WIDTH = 600;
	public static final int HEIGHT = 600;
	public static final int TILE_SIZE = 50;
	private Thread gameThread;
	private boolean running;
	private BufferStrategy bs;
	private SpriteManager spriteManager;  
	private TileMap tileMap; 
	private Camera camera;  
	private Cat cat;
	private List<Collectable> collectables;
	private List<Entity> entities;
	private List<Interactable> interactables;
	private InteractionSystem interactionSystem;
	private Sound collectSound;
	private DayNightCycle dayNightCycle;
	
	public CatCrossing() {
		 setPreferredSize(new Dimension(WIDTH, HEIGHT));
		 this.setFocusable(true);
		 this.requestFocus();
		 JFrame frame = new JFrame("Cat Crossing");
		 frame.add(this);
		 frame.pack();
		 frame.setLocationRelativeTo(null);
		 frame.setResizable(false);
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 frame.setVisible(true);
		 start();
	} 
	
	private void start() {
		if(gameThread==null) {
			this.createBufferStrategy(2);
			running = true;
			gameThread = new Thread(this);
			gameThread.start();
		}
	} 
	 
	private void init() {
		bs = this.getBufferStrategy();
		loadSpriteSheets(); 
		registerTiles();
		loadSounds();
		dayNightCycle = new DayNightCycle();
		interactables = new ArrayList<>();
		tileMap = new TileMap("/Maps/map.txt", TILE_SIZE, spriteManager);
		camera = new Camera(tileMap);
		cat = new Cat(spriteManager.grabSheet("bunny move"), new Vector2(100, 100), camera, tileMap, dayNightCycle, interactables);
		entities = new ArrayList<>();
		loadCollectables();
		loadEntities();
		interactionSystem = new InteractionSystem(interactables);
		this.addKeyListener(cat);
		this.addKeyListener(interactionSystem);
	}
	
	private void loadSounds() {
		collectSound = new Sound("/Sounds/collectable.wav");
		collectSound.setVolume(0.7f);
	}
	
	private void loadCollectables() {
		collectables = new ArrayList<>();
		collectables.add(Collectable.createAxe(new Vector2(600, 500), camera));
		collectables.add(Collectable.createHoe(new Vector2(600,  600), camera));
		collectables.add(Collectable.createWateringCan(new Vector2(700, 500), camera));
		collectables.add(Collectable.createWheatSeeds(new Vector2(700, 600), camera));
	}
	
	private void loadEntities() {
		List<Cow> cows = List.of(
			    new Cow(new Vector2(400, 300), 200, camera, cat),
			    new Cow(new Vector2(400, 400), -100, camera, cat),
			    new Cow(new Vector2(400, 150), 300, camera, cat)
			);
		for(Cow cow : cows) {
			interactables.add(cow);
			entities.add(cow);
		}
		List<Tree> trees = List.of(new Tree(new Vector2(300, 450), camera, cat, collectables),
				new Tree(new Vector2(300, 500), camera, cat, collectables),
				new Tree(new Vector2(700, 200), camera, cat, collectables),
				new Tree(new Vector2(750, 200), camera, cat, collectables),
				new Tree(new Vector2(900, 400), camera, cat, collectables),
				new Tree(new Vector2(1300, 450), camera, cat, collectables),
				new Tree(new Vector2(1300, 500), camera, cat, collectables));
		for(Tree tree : trees) {
			entities.add(tree);     
		}
		List<Chicken> chickens = List.of(new Chicken(new Vector2(500,100),200,camera, cat, collectables),
				new Chicken(new Vector2(800,400),200,camera, cat, collectables));
		for(Chicken chicken : chickens) {
			interactables.add(chicken);
			entities.add(chicken);
		}
		entities.add(cat);
	}
	
	private void loadSpriteSheets() {
		spriteManager = new SpriteManager();
		spriteManager.loadSheet("bunny move", SpriteManager.loadImage("/Characters/Basic Charakter Spritesheet.png"), 48, 48, 150,150);
		spriteManager.loadSheet("grass", SpriteManager.loadImage("/Tilesets/Grass.png"), 16, 16, 50, 50);
		spriteManager.loadSheet("water", SpriteManager.loadImage("/Tilesets/Water.png"), 16, 16, 50, 50);
		spriteManager.loadSheet("plant", SpriteManager.loadImage("/Objects/Basic Grass Biom things 1.png"), 16, 16, 50, 50);
		spriteManager.loadSheet("dirt", SpriteManager.loadImage("/Tilesets/Tilled Dirt.png"), 16, 16, 50, 50);
		spriteManager.loadSheet("hill", SpriteManager.loadImage("/Tilesets/Hills.png"), 16, 16, 50, 50);
		spriteManager.loadSheet("fence", SpriteManager.loadImage("/Tilesets/Fences.png"), 16, 16, 50, 50);
	} 
	
	private void registerTiles() {   
		registerGrassTiles();
		registerHillTiles();
		registerPlantTiles();
		registerFenceTiles();
		spriteManager.registerTile(2, "water", 0, 0, true, Tile.Type.WATER);
		spriteManager.registerTile(3, "water", 0, 2, true, Tile.Type.WATER);
		spriteManager.registerTile(10, "dirt", 0, 1, false, Tile.Type.DIRT);
	}
	
	private void registerFenceTiles() {
		spriteManager.registerTile(37, "fence", 3, 2, true, Tile.Type.FENCE);
		spriteManager.registerTile(38, "fence", 2, 3, true, Tile.Type.FENCE);
		spriteManager.registerTile(39, "fence", 1, 0, true, Tile.Type.FENCE);
		spriteManager.registerTile(40, "fence", 0, 3, true, Tile.Type.FENCE);
	}
	
	private void registerGrassTiles() {
		spriteManager.registerTile(0, "grass", 5, 0, false, Tile.Type.GRASS);
		spriteManager.registerTile(1, "grass", 6, 0, false, Tile.Type.GRASS);
		spriteManager.registerTile(11, "grass", 0, 0, false, Tile.Type.GRASS);
		spriteManager.registerTile(12, "grass", 0,  1, true, Tile.Type.GRASS); 
		spriteManager.registerTile(13, "grass", 0,  2, true, Tile.Type.GRASS);
		spriteManager.registerTile(14, "grass", 5,  3, false, Tile.Type.GRASS);
		spriteManager.registerTile(15, "grass", 6,  4, false, Tile.Type.GRASS);
		spriteManager.registerTile(16, "grass", 1,  1, false, Tile.Type.GRASS);
		spriteManager.registerTile(17, "grass", 1,  0, true, Tile.Type.GRASS);
		spriteManager.registerTile(18, "grass", 2,  0, false, Tile.Type.GRASS);
		spriteManager.registerTile(19, "grass", 2,  1, true, Tile.Type.GRASS);
		spriteManager.registerTile(20, "grass", 2,  2, false, Tile.Type.GRASS);
		spriteManager.registerTile(21, "grass", 1,  2, true, Tile.Type.GRASS);
		spriteManager.registerTile(22, "grass", 2,  2, true, Tile.Type.GRASS);
		spriteManager.registerTile(23, "grass", 5,  2, false, Tile.Type.GRASS);
		spriteManager.registerTile(24, "grass", 6,  5, false, Tile.Type.GRASS);
		spriteManager.registerTile(25, "grass", 6,  1, false, Tile.Type.GRASS);
		spriteManager.registerTile(26, "grass", 6,  2, false, Tile.Type.GRASS);
	}
	
	private void registerHillTiles() {
		spriteManager.registerTile(27, "hill", 2,  0, true, Tile.Type.HILL);
		spriteManager.registerTile(28, "hill", 2,  1, true, Tile.Type.HILL);
		spriteManager.registerTile(29, "hill", 2,  2, true, Tile.Type.HILL);
		spriteManager.registerTile(30, "hill", 1,  2, true, Tile.Type.HILL);
		spriteManager.registerTile(31, "hill", 0,  2, true, Tile.Type.HILL);
		spriteManager.registerTile(32, "hill", 0,  1, true, Tile.Type.HILL);
		spriteManager.registerTile(33, "hill", 0,  0, true, Tile.Type.HILL);
		spriteManager.registerTile(34, "hill", 1,  0, true, Tile.Type.HILL);
	}
	
	private void registerPlantTiles() {
		spriteManager.registerTile(4, "plant", 2, 7, false, Tile.Type.PLANT);
		spriteManager.registerTile(5, "plant", 3, 7, false, Tile.Type.PLANT);
		spriteManager.registerTile(6, "plant", 3, 0, true, Tile.Type.PLANT);
		spriteManager.registerTile(7, "plant", 3, 1, true, Tile.Type.PLANT);
		spriteManager.registerTile(8, "plant", 4, 7, false, Tile.Type.PLANT);
		spriteManager.registerTile(9, "plant", 4, 8, false, Tile.Type.PLANT);
		spriteManager.registerTile(35, "plant", 2, 8, false, Tile.Type.PLANT);
		spriteManager.registerTile(36, "plant", 3, 8, false, Tile.Type.PLANT);
	}
   
	@Override
	public void run() {
		init();
		long startTime = System.nanoTime();
		double timePerTick = 1000000000.0 / 60.0;
		double deltaTime = 0;
		double delta = 0;
		long timer = 0;
		int frames = 0;
		
		while(running) {
			long timeNow = System.nanoTime();
			delta += (timeNow - startTime) / timePerTick;
			deltaTime += (timeNow - startTime) / 1000000000.0;
			timer += timeNow - startTime;
			startTime = timeNow;
			
		    if(delta >= 1) {
		    	update(deltaTime);
		    	draw();
		    	frames++;
		    	delta--; 
		    	deltaTime -= deltaTime;
		    }
		    
		    if(timer >= 1000000000) {
		    	System.out.println("Fps:" + frames);
		    	timer = 0;
		    	frames = 0;
		    }  
		}
	}
	
	private void update(double deltaTime) {
		Iterator<Collectable> iterator = collectables.iterator();
		while(iterator.hasNext()) {
			Collectable collectable = iterator.next();
			collectable.update(deltaTime);
			if(collectable.getBoundingBox().intersects(cat.getBoundingBox())) {
				cat.getInventory().addItem(collectable.getCollectableType().name());
				collectSound.play();
				iterator.remove();
			} 
		}  
		cat.update(deltaTime);
		for(Entity entity : entities) {
			if (entity.equals(cat)) {
				continue;
			}
			entity.update(deltaTime);
		}
		dayNightCycle.update(deltaTime);
	    interactionSystem.update(deltaTime);
		camera.centerOnEntity(cat);
	}
	
	private void drawEntities(Graphics2D g2d) {
		 for (Entity entity : entities) {
		        if (entity instanceof Tree tree) {
		            tree.drawBase(g2d);
		            tree.drawTop(g2d);
		            tree.draw(g2d); 
		        } else {
		            entity.draw(g2d);
		        }
		    }
	}      
	
	private void draw() {     
	 
	  if(bs==null) {  
		  return; 
	  }
	  Graphics2D g2d = (Graphics2D)bs.getDrawGraphics();
	  
	  g2d.setColor(new Color(192,212,112));
	  g2d.fillRect(0, 0, WIDTH, HEIGHT);
	  tileMap.draw(g2d, spriteManager, camera);
	  for(Collectable col : collectables) {
		  col.draw(g2d);
	  } 
	  entities.sort((a, b) -> Integer.compare(a.getRenderY(), b.getRenderY()));
	  drawEntities(g2d); 
	  interactionSystem.draw(g2d);
	  dayNightCycle.draw(g2d);
	  cat.drawStamina(g2d);
	  cat.showInventory(g2d);
	  g2d.dispose();
	  bs.show();
	}

}
