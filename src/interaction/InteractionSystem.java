package interaction;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import plant.Plant;

public class InteractionSystem implements KeyListener {
	
	private final List<Interactable> interactables;
	
	public InteractionSystem(List<Interactable> interactables) {
		this.interactables = interactables;
	}
	
	public void update(double deltaTime) {
		for(Interactable interactable : interactables) {
			interactable.updateInteraction(deltaTime);
		}
		interactables.removeIf(interactable -> (interactable instanceof Plant plant) && plant.getDead());
	}
	
	public void draw(Graphics2D g2d) {
		for(Interactable interactable : interactables) {
			interactable.drawInteraction(g2d);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	} 

	@Override
	public void keyPressed(KeyEvent e) {
		for(Interactable interactable : interactables) {
			if (interactable instanceof InteractableWithInput interactableWithInput) {
				interactableWithInput.processKeyPressed(e);
		    }
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		for(Interactable interactable : interactables) {
			if (interactable instanceof InteractableWithInput interactableWithInput) {
				interactableWithInput.processKeyReleased(e);
		    };
		}
	}

}
