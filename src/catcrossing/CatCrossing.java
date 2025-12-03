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
import entity.Cat;
import entity.Chicken;
import entity.Collectable;
import entity.Cow;
import entity.Entity;
import entity.Tree;
import interaction.Interactable;
import interaction.InteractionSystem;
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
	
	public CatCrossing() {
		 setPreferredSize(new Dimension(WIDTH, HEIGHT));
		 this.setFocusable(true);
		 this.requestFocus();
		 JFrame frame = new JFrame("CatCrossing");
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
		tileMap = new TileMap("/Maps/map.txt", TILE_SIZE, spriteManager);
		camera = new Camera(tileMap);
		cat = new Cat(spriteManager.grabSheet("bunny move"), 200, 200, camera, tileMap);
		entities = new ArrayList<>();
		interactables = new ArrayList<>();
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
		collectables.add(Collectable.createAxe(200, 200, camera));
		collectables.add(Collectable.createAxe(300, 300, camera));
		collectables.add(Collectable.createHoe(400,  400, camera));
	}
	
	private void loadEntities() {
		List<Cow> cows = List.of(
			    new Cow(400, 300, 200, camera, cat),
			    new Cow(400, 400, -100, camera, cat),
			    new Cow(400, 150, 300, camera, cat)
			);
		for(Cow cow : cows) {
			interactables.add(cow);
			entities.add(cow);
		}
		List<Tree> trees = List.of(new Tree(400, 200, camera, cat, collectables),
				new Tree(300, 200, camera, cat, collectables));
		for(Tree tree : trees) {
			entities.add(tree);
		}
		List<Chicken> chickens = List.of(new Chicken(100,100,200,camera, cat, collectables));
		for(Chicken chicken : chickens) {
			interactables.add(chicken);
			entities.add(chicken);
		}
	}
	
	private void loadSpriteSheets() {
		spriteManager = new SpriteManager();
		spriteManager.loadSheet("bunny move", SpriteManager.loadImage("/Characters/Basic Charakter Spritesheet.png"), 48, 48, 150,150);
		spriteManager.loadSheet("grass", SpriteManager.loadImage("/Tilesets/Grass.png"), 16, 16, 50, 50);
		spriteManager.loadSheet("water", SpriteManager.loadImage("/Tilesets/Water.png"), 16, 16, 50, 50);
		spriteManager.loadSheet("plants", SpriteManager.loadImage("/Objects/Basic Grass Biom things 1.png"), 16, 16, 50, 50);
		spriteManager.loadSheet("dirt", SpriteManager.loadImage("/Tilesets/Tilled Dirt.png"), 16, 16, 50, 50);
	}
	
	private void registerTiles() {
		spriteManager.registerTile(0, "grass", 5, 0, false, Tile.Type.GRASS);
		spriteManager.registerTile(1, "grass", 6, 0, false, Tile.Type.GRASS);
		spriteManager.registerTile(2, "water", 0, 0, true, Tile.Type.WATER);
		spriteManager.registerTile(3, "water", 0, 2, true, Tile.Type.WATER);
		spriteManager.registerTile(4, "plants", 2, 7, false, Tile.Type.PLANT);
		spriteManager.registerTile(5, "plants", 3, 7, false, Tile.Type.PLANT);
		spriteManager.registerTile(6, "plants", 3, 0, true, Tile.Type.PLANT);
		spriteManager.registerTile(7, "plants", 3, 1, true, Tile.Type.PLANT);
		spriteManager.registerTile(8, "plants", 4, 7, false, Tile.Type.PLANT);
		spriteManager.registerTile(9, "plants", 4, 8, false, Tile.Type.PLANT);	
		spriteManager.registerTile(10, "dirt", 0, 1, false, Tile.Type.DIRT);
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
			entity.update(deltaTime);
		}
	    interactionSystem.update(deltaTime);
		camera.centerOnEntity(cat);
	}
	
	private void drawEntities(Graphics2D g2d) {
		for(Entity entity : entities) {
			  if(entity instanceof Tree tree) {
				  tree.drawBase(g2d);
			  }else {
			      entity.draw(g2d);
			  }
		}
	}
	
	private void drawTreeTops(Graphics2D g2d) {
		for(Entity entity : entities) {
			  if(entity instanceof Tree tree) {
				  tree.drawTop(g2d);
				  tree.draw(g2d);
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
	  drawEntities(g2d);
	  cat.draw(g2d);
	  interactionSystem.draw(g2d);
	  drawTreeTops(g2d);
	  cat.showInventory(g2d);
	  g2d.dispose();
	  bs.show();
	}

}
