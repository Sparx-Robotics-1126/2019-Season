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
	
	public static class SendableDouble extends SendableBase {

		private double num;
		
		public SendableDouble(String name) {
			this(name, 0);
		}
		
		public SendableDouble(String name, double num) {
			this.num = num;
		}
		
		public double get() {
			return num;
		}
		
		public void set(double num) {
			this.num = num;
		}
		
		@Override
		public void initSendable(SendableBuilder builder) {
//			build
			
			
		}
		
		
		
	}
	
	
}
