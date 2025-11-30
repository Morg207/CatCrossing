package entity;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class Entity {
	
	protected enum EntityType {CHICKEN, COW, COLLECTABLE}
	protected double x;
	protected double y;
	protected int xOffset;
	protected int yOffset;
    protected int width;
    protected int height;
    protected double velX;
    protected double velY;
	protected Rectangle boundingBox;
	protected EntityType type;
	
	public Entity(double x, double y, int xOffset, int yOffset, Dimension dimension) {
		this.x = x;
		this.y = y;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.width = (int)dimension.getWidth();
		this.height = (int)dimension.getHeight();
		boundingBox = new Rectangle((int)x+xOffset,(int)y+yOffset, width, height);
	}
	
	public abstract void update(double deltaTime);
	
	public abstract void draw(Graphics2D g2d);
	
	protected void updateBoundingBoxInfo(int xOffset, int yOffset, int width, int height) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.width = width;
		this.height = height;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public int getXOffset() {
		return xOffset;
	}
	
	public int getYOffset() {
		return yOffset;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public double getVelX() {
		return velX;
	}
	
	public double getVelY() {
		return velY;
	}
	
	public EntityType getEntityType() {
		return type;
	}
	
	public Rectangle getBoundingBox() {
		return boundingBox;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setVelX(double velX) {
		this.velX = velX;
	}
	
	public void setVelY(double velY) {
		this.velY = velY;
	}
	
	public void setXOffset(int xOffset) {
		this.xOffset = xOffset;
	}
	
	public void setYOffset(int yOffset) {
		this.yOffset = yOffset;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setBoundingBox(Rectangle boundingBox) {
		this.boundingBox = boundingBox;
	}
}
