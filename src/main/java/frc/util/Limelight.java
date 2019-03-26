package frc.util;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight{

	NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
	NetworkTableEntry tx = table.getEntry("tx");
	NetworkTableEntry ta = table.getEntry("ta");
	
	public void setEnable(boolean ledOn) {
		NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(ledOn ? 3 : 1);
	}
	
	public double getAngle() {
		return tx.getDouble(0.0);
	}
	
	public double getAreaOfImage() {
		return ta.getDouble(0.0);
	}
	
	public void blink() {
		NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(2);
	}
	
}
