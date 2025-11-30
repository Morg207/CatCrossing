package interaction;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public interface Interactable {
	
	public void updateInteraction(double deltaTime);
	
	public void drawInteraction(Graphics2D g2d);
	
	public void processKeyPressed(KeyEvent key);
	
	public void processKeyReleased(KeyEvent key);

}
