package entity;

import animation.Animator;
import math.MathUtils;
import utils.Collision;
import utils.Timer;
 
public class AIPatrol {
	
	private enum AnimationState{
		IDLE_RIGHT, WALKING_RIGHT,
		IDLE_LEFT, WALKING_LEFT;
	}   
	
	private final Animator animator;
	private final double patrolStart;
	private final double patrolEnd;
	private AnimationState animationState;
	private final Entity entity;
	private final Timer timer;
	private final double pauseTime;
	private double lastWalkDirection;
	private boolean atEnd;
	private boolean startLeft;
	private final Cat cat;

	public AIPatrol(Animator animator, Cat cat,  Entity entity, double patrolLength, double pauseTime) {
		this.animator = animator;
		this.patrolStart = entity.position.x;
		this.patrolEnd = patrolStart + patrolLength;
		this.pauseTime = pauseTime;
		this.entity = entity;
		this.cat = cat;
		timer = new Timer();
		setupPatrol(patrolLength);
	}
	
	private void setupPatrol(double patrolLength) {
		if(patrolLength > 0) {
			   animationState = AnimationState.WALKING_RIGHT;
			   lastWalkDirection = 1;
			   startLeft = false;
			   entity.setVelX(20);
			}else if(patrolLength < 0) {
			   animationState = AnimationState.WALKING_LEFT;
			   lastWalkDirection = -1;
			   startLeft = true;
			   entity.setVelX(-20);
			}
	} 
	  
	public void update(double deltaTime) {
		
		patrol(deltaTime);
		
		double distanceFromPlayer = MathUtils.distance(entity.position.x + entity.getXOffset() + entity.getWidth()/2,
				entity.position.y + entity.getYOffset() + entity.getHeight()/2,
				cat.position.x + cat.getXOffset() + cat.getWidth()/2, cat.position.y + cat.getYOffset() + cat.getHeight()/2);
		if(distanceFromPlayer > 100) {
			if(entity.vel.x == 0) {
				cat.setInteracting(false);
			}
		    entity.setVelX(20 * lastWalkDirection);
		    if(!atEnd) {
		    animationState = lastWalkDirection == 1
		            ? AnimationState.WALKING_RIGHT
		            : AnimationState.WALKING_LEFT;
		    }
		}

		if(stopOnCollision(deltaTime)) {
			return;
		}
		
		if(startLeft) {
		   patrolLeft(deltaTime);
		}else {
		   patrolRight(deltaTime);
		}
	} 
	
	private void patrol(double deltaTime) {
		entity.position.x += entity.vel.x * deltaTime;
		if(animationState == AnimationState.WALKING_RIGHT) {
			animator.play("walking_right");
			lastWalkDirection = 1; 
		}else if(animationState == AnimationState.WALKING_LEFT) {
			animator.play("walking_left");
			lastWalkDirection = -1;
		}else if(animationState == AnimationState.IDLE_RIGHT) {
			animator.play("idle_right");
		}else if(animationState == AnimationState.IDLE_LEFT) {
			animator.play("idle_left");
		}
		animator.update(deltaTime);
	}
	 
	
	private void patrolLeft(double deltaTime) {
		
		if(entity.position.x < patrolEnd) {
			entity.position.x = patrolEnd;
			atEnd = true;
			pause(deltaTime, AnimationState.IDLE_LEFT, AnimationState.WALKING_RIGHT);
		}
		else if(entity.position.x > patrolStart) {
			entity.position.x = patrolStart;
			atEnd = true;
			pause(deltaTime, AnimationState.IDLE_RIGHT, AnimationState.WALKING_LEFT);
		}
	}
	
	private void patrolRight(double deltaTime) {
		if(entity.position.x > patrolEnd) {
			entity.position.x = patrolEnd;
			atEnd = true;
			pause(deltaTime, AnimationState.IDLE_RIGHT, AnimationState.WALKING_LEFT);
		}
		else if(entity.position.x < patrolStart) {
			entity.position.x = patrolStart;
			atEnd = true;
			pause(deltaTime, AnimationState.IDLE_LEFT, AnimationState.WALKING_RIGHT);
		}
	}
	
	private void pause(double deltaTime, AnimationState idle, AnimationState walking) {
		timer.update(deltaTime);
		animationState = idle;
		if (timer.hasPassed(pauseTime)) {
		entity.vel.x *= -1;
		atEnd = false;
		animationState = walking;
		timer.setTimer(0);
	  }
	} 
	
	private boolean stopOnCollision(double deltaTime) {
		boolean collided = Collision.resolveEntityCollision(cat, entity, deltaTime);
		if(!collided) {
			return false;
		}
	    entity.setVelX(0);
	    
	    cat.setInteracting(true);

	    if (lastWalkDirection == 1) {
	        animationState = AnimationState.IDLE_RIGHT;
	    } else {
	        animationState = AnimationState.IDLE_LEFT;
	    }
	    return true;
	}
	
	public double getLastWalkDirection() {
		return lastWalkDirection;
	}
}
