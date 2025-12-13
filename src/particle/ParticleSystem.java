package particle;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import camera.Camera;
import entity.Cat;
import math.Vector2;
import utils.Timer;

public class ParticleSystem {
	
	private ArrayList<Particle> particles;
	private Vector2 position;
	private Camera camera;
	private Random random;
	private Timer timer;
	private double spawnRate;
	private Cat cat;
	
	public ParticleSystem(Vector2 position, double spawnRate, Cat cat, Camera camera) {
		particles = new ArrayList<>();
		this.cat = cat;
		this.position = position;
		this.camera = camera;
		random = new Random();
		timer = new Timer();
		this.spawnRate = spawnRate;
	}
	
	public ParticleSystem(double spawnRate, Cat cat, Camera camera) {
		this(new Vector2(0,0), spawnRate, cat, camera);
	}
	
	public void emit() {
			double baseAngle = Math.toRadians(-90);
			double spread = Math.toRadians(40);
			double angle = baseAngle + (Math.random() - 0.5) * spread;
		    double speed = 50 + Math.random() * 50;
		    double lifeTime = (Math.random() + 1);
		    int endSize = 7 + random.nextInt(15); 
		    double velX = Math.cos(angle) * speed;
		    double velY = Math.sin(angle) * speed;
            velY = -velY; 
		    Particle particle = new Particle.Builder()
		    		.position(new Vector2(position.x, position.y))
		    		.vel(new Vector2(velX, velY))
		    		.lifeTime(lifeTime)
		    		.gravity(100 + random.nextDouble() * 40)
		    		.endSize(endSize)
		    		.cat(cat).camera(camera)
		    		.build();
		    particles.add(particle);
		}
	
	public void update(double deltaTime) {
		timer.update(deltaTime);
		while (timer.getTimer() > 1.0 / spawnRate) {
		    emit();   
		    timer.setTimer(timer.getTimer() - 1.0 / spawnRate);
		}
		removeParticles(deltaTime);
	}
	
	public void removeParticles(double deltaTime) {
		Iterator<Particle> iterator = particles.iterator();
		while(iterator.hasNext()) {
			Particle particle = iterator.next();
			particle.update(deltaTime);
			if(particle.getCanKill()) {
				iterator.remove();
			}
		}
	}
	
	public void draw(Graphics2D g2d) {
		for(Particle particle : particles) {
			particle.draw(g2d);
		}
	}
	
	public void setPos(Vector2 position) {
		this.position = position;
	}
	
	public int particleAmount() {
		return particles.size();
	}
	
	public void setSpawnRate(double spawnRate) {
		this.spawnRate = spawnRate;
	}
}
