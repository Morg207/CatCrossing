package sprite;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import camera.Camera;
import catcrossing.CatCrossing;
import plant.Plant;

public class TileMap {
	
	private int[][] map;
	private Plant[][] plants;
	private int rows; 
	private int cols;
	private int tileSize;
	private SpriteManager spriteManager;
	
	public TileMap(String path, int tileSize, SpriteManager spriteManager) {
		this.tileSize = tileSize;
		this.spriteManager = spriteManager;
		loadMap(path);
	}
	 
	public Tile getTile(int row, int col) {
		if(row < 0 || row >= rows || col < 0 || col >= cols) { 
			return null;
		}
		int id = map[row][col];
		return spriteManager.getTileById(id);
	}
	
	public int getTileMapId(int row, int col) {
		if(row < 0 || row >= rows || col < 0 || col >= cols) { 
			return -1;
		}
		int id = map[row][col];
		return id;  
	}
	
	public Tile getTileAt(int x, int y) { 
		int row = y / tileSize;
		int col = x / tileSize;
		if(row < 0 || row >= rows || col < 0 || col >= cols) { 
			return null;
		}
		int id = map[row][col];
		return spriteManager.getTileById(id);
	}
	
	public void setTile(int row, int col, int id) {
		if(row < 0 || row >= rows || col < 0 || col >= cols) { 
			return;
		}
		map[row][col] = id;
	}
	
	public void setTileAt(int x, int y, int id) {
		int row = y / tileSize;
		int col  = x / tileSize;
		if(row < 0 || row >= rows || col < 0 || col >= cols) { 
			return;
		}
		map[row][col] = id;
	}
	
	public boolean isSolid(int tileX, int tileY) {
	    if (tileX < 0 || tileX >= cols || tileY < 0 || tileY >= rows) return true;
	    int id = map[tileY][tileX];
	    Tile tile = spriteManager.getTileById(id); 
	    return tile != null && tile.isSolid();
	}
	
	private void loadMap(String path) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(TileMap.class.getResourceAsStream(path)));
			String line;
			int index = 0;
			while((line=br.readLine()) != null) {
				if(index == 0) {
					String[] dimensions = line.split(" ");
					rows = Integer.parseInt(dimensions[0]);
					cols = Integer.parseInt(dimensions[1]);
					map = new int[rows][cols];
					plants = new Plant[rows][cols];
				}else {
					String[] rowData = line.split(" ");
					for(int x=0; x<rowData.length; x++) {
						int id = Integer.parseInt(rowData[x]);
						map[index-1][x] = id;
					}
				}
				index++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}  
	
	public void draw(Graphics2D g2d, SpriteManager spriteManager, Camera camera) {
		int xStart = Math.max(0, (int)(camera.getXOffset() / tileSize));
		int xEnd = Math.min(cols, (int)(xStart + CatCrossing.WIDTH / tileSize + 1));
		int yStart = Math.max(0, (int)(camera.getYOffset() / tileSize));
		int yEnd = Math.min(rows, (int)(yStart + CatCrossing.HEIGHT / tileSize + 1));
		for(int y=yStart; y<yEnd; y++) {
			for(int x=xStart; x<xEnd; x++) {
				int id = map[y][x];
				Tile tile = spriteManager.getTileById(id);
				tile.draw((int)(x*tileSize - camera.getXOffset()),(int)(y*tileSize - camera.getYOffset()), g2d);
				Plant plant = plants[y][x];
				if(plant != null) {
					plant.draw(g2d);
				}
			}
		}
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getCols() {
		return cols;
	}
	
	public int getTileSize() {
		return tileSize;
	}
	
	public Plant getPlant(int row, int col) {
		return plants[row][col];
	}
	
	public void setPlant(int row, int col, Plant plant){
		plants[row][col] = plant;
	}
}
