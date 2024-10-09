package sap.ass01.bbom;

public class RideSimulation extends Thread {
	
	private final Ride ride;
	private final User user;
	
	private final EBikeApp app;
	private volatile boolean stopped;
	
	public RideSimulation(Ride ride, User user, EBikeApp app) {
		this.ride = ride;
		this.user = user;
		this.app = app;
		stopped = false;
	}
	
	public void run() {
		var b = ride.getEBike();
		b.updateSpeed(1);
		
		var lastTimeDecreasedCredit = System.currentTimeMillis();
		user.decreaseCredit(1);

		var lastTimeChangedDir = System.currentTimeMillis();
		
		while (!stopped) {
			
			/* update pos */
			
			var l = b.getLocation();
			var d = b.getDirection();
			var s = b.getSpeed();
			b.updateLocation(l.sum(d.mul(s)));
			l = b.getLocation();
			if (l.x() > 200 || l.x() < -200) {
				b.updateDirection(new V2d(-d.x(), d.y()));
				if (l.x() > 200) {
					b.updateLocation(new P2d(200, l.y()));
				} else {
					b.updateLocation(new P2d(-200, l.y()));
				}
			}
			if (l.y() > 200 || l.y() < -200) {
				b.updateDirection(new V2d(d.x(), -d.y()));
				if (l.y() > 200) {
					b.updateLocation(new P2d(l.x(), 200));
				} else {
					b.updateLocation(new P2d(l.x(), -200));
				}
			}
			
			/* change dir randomly */
			
			var elapsedTimeSinceLastChangeDir = System.currentTimeMillis() - lastTimeChangedDir;
			if (elapsedTimeSinceLastChangeDir > 500) {
				double angle = Math.random()*60 - 30;
				b.updateDirection(d.rotate(angle));
				elapsedTimeSinceLastChangeDir = System.currentTimeMillis();
			}
			
			
			/* update credit */
			
			var elapsedTimeSinceLastDecredit = System.currentTimeMillis() - lastTimeDecreasedCredit;
			if (elapsedTimeSinceLastDecredit > 1000) {
				user.decreaseCredit(1);
				lastTimeDecreasedCredit = System.currentTimeMillis();
			}
			
			app.refreshView();

			
			try {
				Thread.sleep(20);
			} catch (Exception ignored) {}
			
		}
	}

	public void stopSimulation() {
		stopped = true;
		interrupt();
	}
}
