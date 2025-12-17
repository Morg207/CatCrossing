package math;

import java.awt.Color;

public class MathUtils {
	
	public static double distance(double x1, double y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public static double clamp01(double value) {
		return Math.max(0, Math.min(1, value));
	}
	
	public static double lerp(double start, double end, double t) {
		return (start + t * (end - start));
	}
	
	public static Color lerp(Color start, Color end, double t) {
		int red = (int)(start.getRed() + t * (end.getRed() - start.getRed()));
		int green = (int)((start.getGreen() + t * (end.getGreen() - start.getGreen())));
		int blue = (int)(start.getBlue() + t * (end.getBlue() - start.getBlue()));
		Color color = new Color(red, green, blue);
		return color;
	}

} 
