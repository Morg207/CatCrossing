package interaction;

import java.awt.Graphics2D;

public interface Interactable {
	
	public void updateInteraction(double deltaTime);
	
	public void drawInteraction(Graphics2D g2d);
	
}
 