package entity;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import math.Vector2;

public abstract class Entity {
	
	protected enum EntityType {CHICKEN, COW, COLLECTABLE, PARTICLE}
	protected Vector2 position;
	protected Vector2 vel;
	protected int xOffset;
	protected int yOffset;
    protected int width;
    protected int height;
	protected Rectangle boundingBox;
	protected EntityType type;
	
	public Entity(Vector2 position, int xOffset, int yOffset, Dimension dimension) {
		this.position = position;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.width = (int)dimension.getWidth();
		this.height = (int)dimension.getHeight();
		boundingBox = new Rectangle((int)position.x+xOffset,(int)position.y+yOffset, width, height);
		vel = new Vector2(0, 0);
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
		return position.x;
	}
	
	public double getY() {
		return position.y;
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
		return vel.x;
	}
	
	public double getVelY() {
		return vel.y;
	}
	
	public EntityType getEntityType() {
		return type;
	}
	
	public Rectangle getBoundingBox() {
		return boundingBox;
	}
	
	public void setX(double x) {
		position.x = x;
	}
	
	public void setY(double y) {
		position.y = y;
	}
	
	public void setVelX(double velX) {
		vel.x = velX;
	}
	
	public void setVelY(double velY) {
		vel.y = velY;
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
	
	public int getRenderY() {
		return (int)position.y+height;
	}
}
