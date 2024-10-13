package sap.ass01.layers.PL.simulation;

import sap.ass01.bbom.EBike;
import sap.ass01.layers.PL.EBikeApp;

public class RideSimulation extends Thread {
	
	private final int bikeId;
	private final int user;


	private P2d loc;
	private V2d direction = new V2d(1,0);;
	private double speed;
	
	private final EBikeApp app;
	private volatile boolean stopped;
	
	public RideSimulation(int bikeId, int bikeX, int bikeY, int user, EBikeApp app) {
		this.bikeId = bikeId;
		this.loc = new P2d(bikeX, bikeY);
		this.user = user;
		this.app = app;
		stopped = false;
	}
	
	public void run() {
		speed = 1;

		var lastTimeChangedDir = System.currentTimeMillis();
		
		while (!stopped) {
			/* update pos */
			this.loc = this.loc.sum(this.direction.mul(this.speed));
			if (this.loc.x() > 200 || this.loc.x() < -200) {
				this.direction = new V2d(-this.direction.x(), this.direction.y());
				if (this.loc.x() > 200) {
					this.loc = new P2d(200, this.loc.y());
				} else {
					this.loc = new P2d(-200, this.loc.y());
				}
			}
			if (this.loc.y() > 200 || this.loc.y() < -200) {
				this.direction = new V2d(this.direction.x(), -this.direction.y());
				if (this.loc.y() > 200) {
					this.loc = new P2d(this.loc.x(), 200);
				} else {
					this.loc = new P2d(this.loc.x(), -200);
				}
			}
			
			/* change dir randomly */
			
			var elapsedTimeSinceLastChangeDir = System.currentTimeMillis() - lastTimeChangedDir;
			if (elapsedTimeSinceLastChangeDir > 500) {
				double angle = Math.random()*60 - 30;
				this.direction = this.direction.rotate(angle);
            }

			app.refreshView();

			try {
				Thread.sleep(500);
			} catch (Exception ignored) {}
			
		}
	}

	public void stopSimulation() {
		stopped = true;
		interrupt();
	}
}
