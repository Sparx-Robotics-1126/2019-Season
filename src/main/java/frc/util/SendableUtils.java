package frc.util;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class SendableUtils {
	
	public interface SendableItem {
		
		public abstract void getName();
		
	}

	public static class SendableBoolean extends SendableBase {
		
		private static final BooleanSupplier falseBool = () -> false;
		private static final BooleanSupplier trueBool = () -> true;
		
		private BooleanSupplier bool;
		private boolean unique;
		private String name;
		
		public SendableBoolean(String name, BooleanSupplier bool) {
			this.bool = bool;
			this.name = name;
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
	
	public static class SendableDouble extends SendableBase {
		
		private DoubleSupplier ds;
		private boolean unique;
		private String name;
		
		public SendableDouble(String name) {
			this(name, 0);
		}
		
		public SendableDouble(String name, double dbl) {
			this.ds = new BetterDoubleSupplier(dbl);
			unique = false;
			this.name = name;
		}
		
		public SendableDouble(String name, DoubleSupplier ds) {
			this.ds = ds;
			unique = true;
			this.name = name;
		}
		
		public double get() {
			return ds.getAsDouble();
		}
		
		public void set(double dbl) {
			if(!unique) { //could use instanceof instead of having a bool but thats a big yikers from me for performance
				((BetterDoubleSupplier)ds).setDouble(dbl);
			}
		}

		@Override
		public void initSendable(SendableBuilder builder) {
			builder.setSmartDashboardType("Digital Input");
			builder.addDoubleProperty("Value", this::get, unique ? null : this::set);
		}
		
		private class BetterDoubleSupplier implements DoubleSupplier {

			private double dbl;
			
			private BetterDoubleSupplier() {
				this(0);
			}
			
			private BetterDoubleSupplier(double startingDbl) {
				dbl = startingDbl;
			}
			
			@Override
			public double getAsDouble() {
				return dbl;
			}
			
			public void setDouble(double dbl) {
				this.dbl = dbl;
			}
			
		}
		
	}
	
}
