package interaction;

import java.awt.event.KeyEvent;

public interface InteractableWithInput extends Interactable {
	
    public void processKeyPressed(KeyEvent key);
	public void processKeyReleased(KeyEvent key);

}
