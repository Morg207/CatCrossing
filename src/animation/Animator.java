package animation;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Animator {

	    private Map<String, Animation> anims = new HashMap<>();
	    private Animation current;

	    public void add(String name, Animation anim) {
	        anims.put(name, anim);
	        if (current == null) {
	          current = anim;
	        }
	    }

	    public void play(String name) {
	        Animation next = anims.get(name);
	        if (next != current) {
	            next.reset();
	            current = next;
	        }
	    }

	    public void update(double deltaTime) {
	        current.update(deltaTime);
	    }

	    public Animation getCurrent() { return current; }

	    public BufferedImage getFrame() {
	        return current.getFrame();
	    }
	}
