package font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public class FontLoader {
	
	public static Font loadFont(String path, int style,  int size) {
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, FontLoader.class.getResourceAsStream(path));
			font = font.deriveFont(style, size);
			return font;
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
		
	}

}
