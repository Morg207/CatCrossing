package dialogue;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import font.FontLoader;
import sound.Sound; 

public class DialogueSystem {
	
	private final String[] dialogue;
	private int textIndex;
	private boolean showDialogue;
	private boolean showNextLine;
	private final Font font;
	private final BufferedImage imageIcon;
	private final Sound sound;
	private int promptPresses;
	
	public DialogueSystem(String[] dialogue, BufferedImage imageIcon, Sound sound) {
		this.dialogue = dialogue;
		textIndex = -1;
		font = FontLoader.loadFont("/Fonts/pixel.ttf", Font.PLAIN, 25);
		this.imageIcon = imageIcon;
		this.sound = sound;
	}
	
	public void update() {
		if(showNextLine) {
			promptPresses++;
			if(promptPresses == 1 && dialogue.length == 1) {
			   sound.play();
			}else if(dialogue.length > 1) {
			   sound.play();
			}
			textIndex++;
			textIndex = textIndex % dialogue.length;
			showNextLine = false;
		}
	}
	
	public void draw(Graphics2D g2d) {
		if(showDialogue) {
		   drawDialogue(g2d);
		} 
	}    
	 
	private void drawDialogue(Graphics2D g2d) {
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
		g2d.setColor(Color.BLACK);
		g2d.fillRoundRect(46, 445, 508, 95, 20, 20); 
		g2d.setColor(Color.GRAY);
		g2d.fillRoundRect(50, 449, 500, 87, 20, 20);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		g2d.setColor(Color.WHITE);
		g2d.setFont(font); 
		g2d.drawString(dialogue[textIndex], 130, 489);
		g2d.drawImage(imageIcon, 55, 450, null);
	} 
	
	public void reset() {
		showNextLine = false;
		showDialogue = false;
		promptPresses = 0;
		textIndex = -1;
	}
	
	public void setShowDialogue(boolean showDialogue) {
		this.showDialogue = showDialogue;
	}
	
	public void setShowNextLine(boolean showNextLine) {
		this.showNextLine = showNextLine;
	}
}
