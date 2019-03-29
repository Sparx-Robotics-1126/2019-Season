package frc.controls;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Joystick;
import frc.subsystem.Drives;
import frc.subsystem.GenericSubsystem;
import frc.subsystem.HAB;
import frc.subsystem.Hatch;
import frc.util.SendableUtils.SendableBoolean;

public class Debug implements Controls{
	
	private Automation auto;
	private Joystick debugJoystick;
	private boolean debugPassive;
	private ArrayList<SendableBoolean> passiveDebug;
	
	private DebugState state;
	
	private boolean[][] buttonStates = { 
			{ false, false }, // XBOX_A
			{ false, false }, // XBOX_B
			{ false, false }, // XBOX_X
			{ false, false }, // XBOX_Y
			{ false, false }, // XBOX_L1
			{ false, false }, // XBOX_R1
			{ false, false }, // XBOX_BACK
			{ false, false }, // XBOX_START
			{ false, false }, // XBOX_L2
			{ false, false }, // XBOX_R2
			{ false, false }, // XBOX_L3
			{ false, false }, // XBOX_R3
	};
	
	private boolean[][] povStates = { 
			{ false, false }, // XBOX_UP
			{ false, false }, // XBOX_RIGHT
			{ false, false }, // XBOX_DOWN
			{ false, false },  // XBOX_LEFT
	};
	
	private void setDebugActive() {
		
	}
	
	private void setDebugPassive(boolean passive) {
		debugPassive = passive;
	}
	
	public enum DebugState {
		STANDBY,
		DEBUG_ACTIVE;
	}
	
	public Debug(Drives drives, Hatch hatch, HAB hab, TeleOP teleop) {
		passiveDebug = new ArrayList<SendableBoolean>();
		auto = new Automation(drives, hatch, hab);
		debugJoystick = new Joystick(CtrlMap.XBOXCONTROLLER_DEBUG);
	}
	
	public void initDebugPassive() {
		if(passiveDebug.size() == 0) {
			passiveDebug.add(new SendableBoolean(" "));
			for(SendableBoolean bool: passiveDebug) {
				GenericSubsystem.addToTables(bool, "DebugControl", bool.getName());
			}
		}
//		GenericSubsystem.addToTables(sendable, subsystem, name);
	}

	@Override
	public void execute() {
		switch(state) {
		case STANDBY:
			return;
		case DEBUG_ACTIVE:
			break;
		}
	}
	
	private interface DebugObject {
		
		public abstract void resetDebugStatus();
		
		public abstract boolean debugStatus();
		
		public abstract void update();
		
	}
	
	public class DebugBoolean implements DebugObject {

		@Override
		public void resetDebugStatus() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean debugStatus() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void update() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class DebugDouble implements DebugObject {

		@Override
		public void resetDebugStatus() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean debugStatus() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void update() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	/**
	 * Return if the specified button was previously not pressed and is now pressed.
	 * 
	 * @param pos - the button to check in buttonStates.
	 * @return if the button was previously not pressed and is now pressed.
	 */
	public boolean isRisingEdgeButton(int pos) {
		return buttonStates[pos][0] && !buttonStates[pos][1];
	}
	
	/**
	 * Return if the specified button was previously pressed and is now no longer
	 * pressed.
	 * 
	 * @param pos - the button to check in buttonStates.
	 * @return if the button was previously pressed and is no longer pressed.
	 */
	public boolean isFallingEdgeButton(int pos) {
		return !buttonStates[pos][0] && buttonStates[pos][1];
	}

	/**
	 * Returns if the POV was previously not pressed and is now pressed.
	 * 
	 * @param pos - the POV to check in posStates.
	 * @return if the POV was previously not pressed and is now pressed.
	 */
	public boolean isRisingEdgePOV(int pos) {
		return povStates[pos][0] && !povStates[pos][1];
	}

	/**
	 * Returns if the POV was previously pressed and is now not pressed.
	 * 
	 * @param pos - the POV to check in posStates.
	 * @return if the POV was previously pressed and is now not pressed.
	 */
	public boolean isFallingEdgePOV(int pos) {
		return !povStates[pos][0] && povStates[pos][1];
	}

	/**
	 * Returns whether or not the button on the specified joystick is pressed.
	 * 
	 * @param joy    - the joystick to check.
	 * @param button - the button to check.
	 * @return if the button is pressed.
	 */
	public boolean isPressedButton(int button) {
		return debugJoystick.getRawButton(button);
	}

	/**
	 * Returns whether or not the trigger on the specific joystick is pressed
	 * (deadband of 0.5).
	 * 
	 * @param joy     - the joystick to check.
	 * @param trigger - the trigger to check.
	 * @return if the trigger is pressed.
	 */
	public boolean isPressedTrigger(int trigger) {
		return debugJoystick.getRawAxis(trigger) > CtrlMap.TRIGGER_DEADBAND;
	}

	/**
	 * Returns whether or not the specific POV is selected on the controller; works
	 * in angles of 90 (pov 0 -> 0 degrees, pov 1 -> 90 degrees, pov 2 -> 180
	 * degrees, pov 3 = 270 degrees)
	 * 
	 * @param joy - the joystick.
	 * @param pov - the POV to check.
	 * @return whether or not the POV on the joystick is pressed.
	 */
	public boolean isPressedPOV(int pov) {
		return debugJoystick.getPOV() == pov * 90;
	}

	/**
	 * Returns the value of the axis.
	 * 
	 * @param axis - the axis of the joystick.
	 * @return the specified axis's current position (between -1 and 1; inverted).
	 */
	public double getAxis(int axis) {
		return -debugJoystick.getRawAxis(axis);
	}

	/**
	 * Returns if the axis of the specified joystick is greater than the DEADBAND as
	 * stated in CtrlMap.java.
	 * 
	 * @param joy  - the joystick.
	 * @param axis - the axis on the joystick to check.
	 * @return if the axis of the specified joystick is greater than the DEADBAND.
	 */
	public boolean isOffZeroAxis(int axis) {
		return getAxis(axis) > CtrlMap.DEADBAND || getAxis(axis) < -CtrlMap.DEADBAND;
	}
	
	public void setJoystickStates() {
		for (boolean buttons[] : buttonStates) {
			buttons[1] = buttons[0];
		}
		for(boolean povs[]: povStates) {
			povs[1] = povs[0];
		}

		buttonStates[0][0] = isPressedButton(CtrlMap.XBOX_A);
		buttonStates[1][0] = isPressedButton(CtrlMap.XBOX_B);
//		buttonStates[2][0] = isPressedButton(CtrlMap.XBOX_X);
		buttonStates[3][0] = isPressedButton(CtrlMap.XBOX_Y);

		buttonStates[4][0] = isPressedButton(CtrlMap.XBOX_L1);
		buttonStates[5][0] = isPressedButton(CtrlMap.XBOX_R1);
//		buttonStates[6][0] = isPressedButton(CtrlMap.XBOX_BACK);
//		buttonStates[7][0] = isPressedButton(CtrlMap.XBOX_START);
//		buttonStates[8][0] = isPressedButton(CtrlMap.XBOX_L3);
//		buttonStates[9][0] = isPressedButton(CtrlMap.XBOX_R3);
		
		buttonStates[10][0] = isPressedTrigger(CtrlMap.XBOX_L2_AXIS);
		buttonStates[11][0] = isPressedTrigger(CtrlMap.XBOX_R2_AXIS);
	
//		povStates[0][0] = isPressedPOV(CtrlMap.POV_UP);
//		povStates[1][0] = isPressedPOV(CtrlMap.POV_RIGHT);
//		povStates[2][0] = isPressedPOV(CtrlMap.POV_DOWN);
//		povStates[3][0] = isPressedPOV(CtrlMap.POV_LEFT);
	}

}
