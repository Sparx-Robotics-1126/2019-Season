package frc.util;

import java.util.HashMap;
import java.util.Map;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;
import frc.subsystem.GenericSubsystem;

public class Debugger extends GenericSubsystem{

	private static final Map<String, Component> components = new HashMap<>();
	private static final NetworkTable DEBUG_TABLE = NetworkTableInstance.getDefault().getTable("DebugTable");
	
	//TODO: implement sensor checking
	public Debugger() {
		super("Debugger", Thread.MIN_PRIORITY);
	}

	@Override
	public void init() {

	}

	@Override
	public void execute() {
		updateValues();
	}
	
	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long sleepTime() {
		return 20;
	}
	
	public void startDebug(String debugMode) {
		
	}
	
	public static synchronized void addToTable(Sendable sendable, String name, String subsystem) {
		if(sendable == null || name == null || name.isEmpty()) {
			return;
		}
		if(subsystem == null || subsystem.isEmpty()) {
			subsystem = "Undefined";
		}
		Component comp = new Component(sendable, name, subsystem);
		NetworkTable table = DEBUG_TABLE.getSubTable(subsystem);
		if(DEBUG_TABLE.containsSubTable(name) || components.putIfAbsent(name, comp) != null) {
			return;
		}
		NetworkTable subTable = table.getSubTable(name);
		subTable.getEntry(".name").setString(sendable.getName());
		comp.sendableBuilder.setTable(subTable);
		comp.sendable.initSendable(comp.sendableBuilder);
		comp.sendableBuilder.startListeners();
	}

	private static synchronized void updateValues() {
		for(Component component: components.values()) {
			if(component.sendable != null) {
				component.sendableBuilder.updateTable();
			}
		}
	}
	
	
	public class DebugBoolean extends Debug{
		
		private boolean valueToReach;
		private boolean startingValue;
		
		public DebugBoolean(Sendable sendable, int comparison, int valueToCompare) {
			super(sendable, comparison, valueToCompare);
		}
		
		@Override
		public int debug() {
			return 0;
		}
		
	}
	
	public class DebugDouble extends Debug{
		
		private double valueToTeach;
		private double comparison;
		private double startingValue;

		public DebugDouble(Sendable sendable, int comparison, int valueToCompare) {
			super(sendable, comparison, valueToCompare);
		}

		@Override
		public int debug() {
			return 0;
		}
		
		public void reset() {
			super.reset();
			
		}
		
	}
	
	public abstract class Debug {
		private int debugStatus; //-1 = false, 0 = untested, 1 = true, -2 = sendable not found
		private Sendable sendable;
		
		public Debug(Sendable sendable, int comparison, int valueToCompare) {
			this.sendable = sendable;
		}
		
		public void setDebugStatus(int debugStatus) {
			this.debugStatus = debugStatus;
		}
		
		public int getDebugStatus() {
			return debugStatus;
		}
		
		public abstract int debug();
		
		public double get() {
			if(sendable instanceof AnalogInput) {
				return ((AnalogInput)sendable).getVoltage();
			} else if(sendable instanceof Encoder) {
				return ((Encoder)sendable).get();
			} else if(sendable instanceof DigitalInput) {
				return ((DigitalInput)sendable).get() ? 1 : 0;
			} else if(sendable instanceof DigitalOutput) {
				return ((DigitalOutput)sendable).get() ? 1 : 0;
			} else if(sendable instanceof Servo) {
				return ((Servo)sendable).get();
			} else if(sendable instanceof Solenoid) {
				return ((Solenoid)sendable).get() ? 1 : 0;
			} else if(sendable instanceof WPI_TalonSRX) {
				return ((WPI_TalonSRX)sendable).get();
			}
			return 0;
		}
		
		public void reset() {
			debugStatus = 0;
		}
		
	}
	
	private static class Component {

		private final Sendable sendable;
		private final SendableBuilderImpl sendableBuilder;
		@SuppressWarnings("unused") //to be used for debug
		private final String name;
		@SuppressWarnings("unused")
		private final String subsystem;

		private Component(Sendable sendable, String name, String subsystem) {
			this.sendable = sendable;
			sendableBuilder = new SendableBuilderImpl();
			this.name = name;
			this.subsystem = subsystem;
		}

	}
	
	public interface Debuggable {
		
		public int debug(Sendable sendable);
		
	}



}

