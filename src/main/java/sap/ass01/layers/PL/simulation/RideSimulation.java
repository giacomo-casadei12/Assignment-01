package sap.ass01.layers.PL.simulation;

import sap.ass01.layers.PL.EBikeApp;

import javax.swing.*;

public class RideSimulation extends Thread {
	
	private final int bikeId;


    private P2d loc;
	private V2d direction = new V2d(1,0);

    private final EBikeApp app;
	private volatile boolean stopped;
	
	public RideSimulation(int bikeId, int bikeX, int bikeY, EBikeApp app) {
		this.bikeId = bikeId;
		this.loc = new P2d(bikeX, bikeY);
        this.app = app;
		stopped = false;
	}

	@Override
	public void run() {
        double speed = 3;

		var lastTimeChangedDir = System.currentTimeMillis();
		
		while (!stopped) {
			/* update pos */
			this.loc = this.loc.sum(this.direction.mul(speed));
			updatePosition();

			/* change dir randomly */

			changeDirection(lastTimeChangedDir);

			this.app.requestUpdateRide(this.bikeId, (int) Math.round(this.loc.x()), (int) Math.round(this.loc.y())).onComplete(x -> {
				if (x.result() != null) {
					if (x.result().first() <= 0 || x.result().second() <= 0) {
						this.stopped = true;
						JOptionPane.showMessageDialog(app, "Credit emptied or dead battery", "End ride", JOptionPane.WARNING_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(app, "Failed to update ride", "Fail", JOptionPane.ERROR_MESSAGE);
				}
			});

			try {
				Thread.sleep(2000);
			} catch (Exception ignored) {}
			
		}
		this.app.requestEndRide(this.bikeId).onComplete(x -> {
			if (!x.result()) {
				JOptionPane.showMessageDialog(app, "Failed to stop ride", "Fail", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	private void updatePosition() {
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
	}

	private void changeDirection(long lastTimeChangedDir) {
		var elapsedTimeSinceLastChangeDir = System.currentTimeMillis() - lastTimeChangedDir;
		if (elapsedTimeSinceLastChangeDir > 500) {
			double angle = Math.random()*60 - 30;
			this.direction = this.direction.rotate(angle);
}
	}

	public void stopSimulation() {
		stopped = true;
		interrupt();
	}

}
