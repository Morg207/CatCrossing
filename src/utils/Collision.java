package utils;

import java.awt.Rectangle;

import entity.Cat;
import entity.Entity;

public class Collision {
	
	public static boolean resolveEntityCollision(Cat cat, Entity other, double deltaTime) {
		boolean collidedX = false;
		boolean collidedY = false;
		collidedX = resolveX(cat, other, deltaTime);
		collidedY = resolveY(cat, other, deltaTime);
		return collidedX || collidedY;
	}  
	
	private static boolean resolveX(Cat cat, Entity other, double deltaTime) {
		boolean collided = false;
		double newX = cat.getX() + cat.getVelX() * deltaTime;
		Rectangle bbox = new Rectangle((int)(newX + cat.getXOffset()), (int)(cat.getY() + cat.getYOffset()), cat.getWidth(), cat.getHeight());
		Rectangle otherBBox = other.getBoundingBox();
		if(cat.getVelX() > 0) {
			if(bbox.intersects(otherBBox)) {
				newX = otherBBox.x - bbox.width - cat.getXOffset();
				other.setVelX(0);
				collided = true; 
			}
		}else if(cat.getVelX() < 0) {
			if(bbox.intersects(otherBBox)) {
				newX = otherBBox.x + otherBBox.width - cat.getXOffset();
				collided = true;
				other.setVelX(0);
			} 
		}else if(cat.getVelX() == 0 && cat.getVelY() == 0) {
			if(bbox.intersects(otherBBox)) {
				collided = true;
				double overlap = 0;
				if(other.getVelX() > 0) {
					overlap = other.getX() + other.getXOffset() + other.getWidth() - (cat.getX() + cat.getXOffset());
					other.setX(other.getX()-overlap);
				}else if(other.getVelX() < 0) {
					overlap = cat.getX() + cat.getXOffset() + cat.getWidth() - (other.getX() + other.getXOffset());
					other.setX(other.getX()+overlap);
				}
				other.setVelX(0);
			} 
		}  
		if(collided) {
		   cat.setX(newX);
		}
		return collided;
	}
	
	private static boolean resolveY(Cat cat, Entity other, double deltaTime) {
		boolean collided = false;
		double newY = cat.getY() + cat.getVelY() * deltaTime;
		Rectangle bbox = new Rectangle((int)(cat.getX() + cat.getXOffset()), (int)(newY + cat.getYOffset()), cat.getWidth(), cat.getHeight());
		Rectangle otherBBox = other.getBoundingBox();
		if(cat.getVelY() > 0) {
			if(bbox.intersects(otherBBox)) {
				if(cat.getY() + cat.getYOffset() + cat.getHeight() / 2 < other.getY() + other.getYOffset()) {
				newY = otherBBox.y - bbox.height - cat.getYOffset();
				collided = true;
				other.setVelX(0);
				}
			}
		}else if(cat.getVelY() < 0) {
			if(bbox.intersects(otherBBox)) {
				if(cat.getY() + cat.getYOffset() + cat.getHeight() / 2 > other.getY() + other.getYOffset() + other.getHeight()) {
				newY = otherBBox.y + otherBBox.height - cat.getYOffset();
				collided = true;
				other.setVelX(0);
				}
			} 
		}
		if(collided) {
		  cat.setY(newY);
		}
		return collided;
	}
	
}