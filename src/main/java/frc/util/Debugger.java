package frc.util;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;
import frc.subsystem.GenericSubsystem;

public class Debugger extends GenericSubsystem{

	private static final Map<Object, Component> components = new HashMap<>();
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
		if(DEBUG_TABLE.containsSubTable(name) || components.putIfAbsent(sendable, comp) != null) {
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


}
