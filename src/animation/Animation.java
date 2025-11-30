package animation;

import java.awt.image.BufferedImage;

public class Animation {

	 public enum Type { LOOP, ONCE }

	    private BufferedImage[] frames;
	    private double speed;
	    private int index;
	    private double timer;
	    private Type type;
	    private boolean finished;

	    public Animation(BufferedImage[] frames, double speed, Type type) {
	        this.frames = frames;
	        this.speed = speed;
	        this.type = type;
	    }

	    public void update(double deltaTime) {
	        if (finished) return;

	        timer += deltaTime;
	        if (timer >= speed) {
	            timer -= speed; 
	            index++;
	            if (index >= frames.length) {
	                if (type == Type.LOOP) {
	                    index = 0;
	                } else {
	                    index = frames.length - 1;
	                    finished = true;
	                }
	            }
	        }
	    }

	    public void reset() {
	        index = 0;
	        finished = false;
	        timer = 0;
	    }

	    public boolean isFinished() {
	        return finished;
	    }

	    public BufferedImage getFrame() {
	        return frames[index];
	    }
	}
