package frc.util;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class SendableUtils {

	public static class SendableBoolean extends SendableBase {
		
		private boolean bool;
		
		public SendableBoolean(String name) {
			this(name, false);
		}
		
		public SendableBoolean(String name, boolean bool) {
			this.bool = bool;
		}
		
		public boolean get() {
			return bool;
		}
		
		public void set(boolean bool) {
			this.bool = bool;
		}

		@Override
		public void initSendable(SendableBuilder builder) {
			builder.setSmartDashboardType("Digital Input");
			builder.addBooleanProperty("Value", this::get, this::set);
		}
		
	}
	
}
