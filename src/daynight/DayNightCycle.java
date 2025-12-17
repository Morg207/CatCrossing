package daynight;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import catcrossing.CatCrossing;
import math.MathUtils;

public class DayNightCycle {

    public enum Phase {
        DAY, SUNSET, NIGHT, SUNRISE
    } 

    private Phase phase;
    private double phaseTimer;
    private double dayDuration;
    private double nightDuration;
    private double transitionDuration;
    private float alpha;
    private double time;
    private Color tintColour;
    private Color dayColour;
    private Color sunsetColour;
    private Color nightColour;
    private Color sunriseColour;
    private double sunsetDuration;
    private double sunsetTimer;
    private boolean atSunset;   
    
    public DayNightCycle() {
    	dayColour = new Color(255,255,255);
    	sunsetColour = new Color(237,214,83);
    	nightColour = new Color(56,91,105);
    	sunriseColour = new Color(245,239,132);
    	sunsetDuration = 15;
    	dayDuration = 60;
    	nightDuration = 30;
    	transitionDuration = 10;
    	phase = Phase.DAY;   
    }  

    public void update(double deltaTime) {
    	if(!atSunset) {
        phaseTimer += deltaTime;
    	}

        switch (phase) {
            case DAY -> {
            	time = 0;
            	sunsetTimer = 0;
                alpha = 0f;
                tintColour = dayColour;

                if (phaseTimer >= dayDuration) {
                    nextPhase(Phase.SUNSET);
                }
            }

            case SUNSET -> {
            	time = MathUtils.clamp01(phaseTimer / transitionDuration);

                if (time < 0.5) {
                    double lerpTime = time / 0.5;
                    tintColour = MathUtils.lerp(dayColour, sunsetColour, lerpTime);
                    if(tintColour.getRed() == 237) {
                    	atSunset = true;
                    	sunsetTimer += deltaTime;
                    	if(sunsetTimer > sunsetDuration) {
                    		atSunset = false;
                    	}
                    }
                } else {
                    double lerpTime = (time - 0.5) / 0.5;
                    tintColour = MathUtils.lerp(sunsetColour, nightColour, lerpTime);
                }

                alpha = (float) MathUtils.lerp(0, 0.7, time);

                if (time >= 1) {
                    nextPhase(Phase.NIGHT);
                }
            }

            case NIGHT -> {
            	time = 0;
            	sunsetTimer = 0;
                alpha = 0.7f;
                tintColour = nightColour;

                if (phaseTimer >= nightDuration) {
                    nextPhase(Phase.SUNRISE);
                }
            } 

            case SUNRISE -> {
            	time = MathUtils.clamp01(phaseTimer / transitionDuration);

                if (time < 0.5) {
                    double lerpTime = time / 0.5;
                    tintColour = MathUtils.lerp(nightColour, sunriseColour, lerpTime);
                    if(tintColour.getRed() == 244) {
                    	atSunset = true;
                    	sunsetTimer += deltaTime;
                    	if(sunsetTimer > sunsetDuration) {
                    		atSunset = false;
                    	}
                    }
                } else {
                    double lerpTime = (time - 0.5) / 0.5;
                    tintColour = MathUtils.lerp(sunriseColour, dayColour, lerpTime);
                }

                alpha = (float) MathUtils.lerp(0.7, 0, time);

                if (time >= 1) {
                    nextPhase(Phase.DAY);
                }
            }
        }
    }

    private void nextPhase(Phase next) {
        phase = next;
        phaseTimer = 0;
    }

    public void draw(Graphics2D g2d) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setColor(new Color(tintColour.getRed() / 255f, tintColour.getGreen() / 255f, tintColour.getBlue() / 255f));
        g2d.fillRect(0, 0, CatCrossing.WIDTH, CatCrossing.HEIGHT);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
    
    public Phase getPhase() {
    	return phase;
    }
    
    public float getInvAlpha() {
        switch (phase) {
            case DAY:
                return 0.6f;
            case SUNSET:
                if (time < 0.5) {
                    return 0.6f;
                } else {
                    double t = (time - 0.5) / 0.5;
                    return (float) MathUtils.lerp(0.6, 0.3, t);
                }
            case NIGHT:
                return 0.3f;
            case SUNRISE:
                if (time < 0.5) {
                	double t = time / 0.5;
                    return (float) MathUtils.lerp(0.3, 0.6, t);
                } else {
                    return 0.6f;
                }
        }
        return 0.6f;
    }
    
    public float getInvTextAlpha() {
        switch (phase) {
            case DAY:
                return 1.0f;
            case SUNSET:
                if (time < 0.5) {
                    return 1.0f;
                } else {
                    double t = (time - 0.5) / 0.5;
                    return (float) MathUtils.lerp(1.0, 0.7, t);
                }
            case NIGHT:
                return 0.7f;
            case SUNRISE:
                if (time < 0.5) {
                	double t = time / 0.5;
                    return (float) MathUtils.lerp(0.7, 1.0, t);
                } else {
                    return 1.0f;
                }
        }
        return 1.0f;
    }
    
    public float getStaminaAlpha() {
        switch (phase) {
            case DAY:
                return 1.0f;
            case SUNSET:
                if (time < 0.5) {
                    return 1f;
                } else {
                    double t = (time - 0.5) / 0.5;
                    return (float) MathUtils.lerp(1.0, 0.6, t);
                }
            case NIGHT:
                return 0.6f;
            case SUNRISE:
                if (time < 0.5) {
                	double t = time / 0.5;
                    return (float) MathUtils.lerp(0.6, 1.0, t);
                } else {
                    return 1.0f;
                }
        }
        return 1.0f;
    }
}
