package utils;

public class Timer {
	
	private double timer;
	
	public void update(double deltaTime) {
		timer += deltaTime;
	}
	
	public boolean hasPassed(double seconds) {
		if(timer > seconds) {
			return true;
		}else {
			return false;
		}
	}
	
	public double getTimer() {
		return timer;
	}
	
	public void setTimer(double timer) {
		this.timer = timer;
	}

}
