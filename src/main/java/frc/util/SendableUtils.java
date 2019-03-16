package frc.util;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class SendableUtils {

	public static class SendableBoolean extends SendableBase {
		
		private static final BooleanSupplier falseBool = () -> false;
		private static final BooleanSupplier trueBool = () -> true;
		
		private BooleanSupplier bool;
		private boolean unique;
		
		public SendableBoolean(String name, BooleanSupplier bool) {
			this.bool = bool;
			unique = true;
		}
		
		public SendableBoolean(String name) {
			this(name, false);
		}
		
		public SendableBoolean(String name, boolean bool) {
			if(bool) {
				this.bool = trueBool;
			} else {
				this.bool = falseBool;
			}
			unique = false;
		}
		
		public boolean get() {
			return bool.getAsBoolean();
		}
		
		public void set(boolean bool) {
			if(unique) {
				if(bool) {
					this.bool = trueBool;
				} else {
					this.bool = falseBool;
				}
			}
			
		}

		@Override
		public void initSendable(SendableBuilder builder) {
			builder.setSmartDashboardType("Digital Input");
			builder.addBooleanProperty("Value", this::get, this::set);
		}
		
	}
	
}
