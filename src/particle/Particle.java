package particle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;
import camera.Camera;
import entity.Cat;
import math.MathUtils;
import math.Vector2;
import utils.Timer;

public class Particle {
    
    private Vector2 position;
    private Vector2 vel;
    private double lifeTime;
    private double gravity; 
    private double size;
    private double endSize;
    private double startSize;
    private double floorY;
    private double bounceFactor;
    private float alpha;
    private Timer timer;
    private Camera camera;
    private boolean canKill;
    private static Random random = new Random();
    private static Color[] colours = {new Color(102/255f, 207/255f, 255/255f), 
    new Color(51/255f, 153/255f, 255/255f),new Color(0/255f, 180/255f, 220/255f)};
    private Color colour;

    private Particle(Builder builder) {
        this.position = builder.position;
        this.vel = builder.vel;
        this.lifeTime = builder.lifeTime;
        this.endSize = builder.endSize;
        this.camera = builder.camera;
        this.gravity = builder.gravity;
        this.startSize = builder.startSize;
        this.size = builder.startSize; 
        this.alpha = 1f;
        this.timer = new Timer();
        this.bounceFactor = builder.bounceFactor;
        this.floorY = builder.cat.getY() + builder.cat.getYOffset() + builder.cat.getHeight();
        this.colour = colours[random.nextInt(colours.length)];
    }

    public static class Builder {
     
        private Vector2 position;
        private Vector2 vel;
        private double lifeTime;
        private double endSize;
        private Cat cat;
        private Camera camera;
        private double gravity = 45;
        private double startSize = 0;
        private double bounceFactor = 1.0;
        public Builder x(double x) { position.x = x; return this; }
        public Builder y(double y) { position.y = y; return this; }
        public Builder velX(double velX) { vel.x = velX; return this; }
        public Builder velY(double velY) { vel.y = velY; return this; }
        public Builder lifeTime(double lifeTime) { this.lifeTime = lifeTime; return this; }
        public Builder endSize(double endSize) { this.endSize = endSize; return this; }
        public Builder cat(Cat cat) { this.cat = cat; return this; }
        public Builder camera(Camera camera) { this.camera = camera; return this; }
        public Builder gravity(double gravity) { this.gravity = gravity; return this; }
        public Builder startSize(double size) { this.startSize = size; return this; }
        public Builder position(Vector2 position) {this.position = position; return this;}
        public Builder vel(Vector2 vel) {this.vel = vel; return this;}

        public Particle build() {
            return new Particle(this);
        }
    }
    
    public void update(double deltaTime) {
        position.x += vel.x * deltaTime;
        position.y += vel.y * deltaTime;

        vel.x *= 0.98;
        vel.y += gravity * deltaTime;

        timer.update(deltaTime);

        double percentage = Math.min(timer.getTimer() / lifeTime, 1.0);
        size = MathUtils.lerp(startSize, endSize, percentage);
        alpha = (float)Math.pow(1 - percentage, 1.5);

        if (position.y >= floorY) {
            bounceFactor *= 0.8;

            if (bounceFactor <= 0.1) {
                canKill = true;
            } 

            vel.y = -vel.y * 0.6 * bounceFactor;
            vel.x = random.nextDouble(-50, 50);
        }
    }

    public void draw(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(colour.getRed()/255f, colour.getGreen()/255f, colour.getBlue()/255f, alpha));

        g2d.fillOval( (int)(position.x - camera.getXOffset()), (int)(position.y - camera.getYOffset()), (int)size, (int)size);
    }

    public boolean getCanKill() {
        return canKill;
    }
}
