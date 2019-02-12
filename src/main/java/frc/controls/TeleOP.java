/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.controls;

import edu.wpi.first.wpilibj.Joystick;
import frc.subsystem.Drives;
import frc.subsystem.HAB;
import frc.subsystem.Hatch;

/**
 * Add your docs here.
 */
public class TeleOP implements Controls{

    private Joystick[] joysticks;

	private Drives drives;
	
	private HAB hab;

	private Hatch hatch;

    private boolean[][] buttonStates =
		{{false, false},
		{false, false}};

	private boolean[][] povStates =
		{{false, false}, //LEFTJOY_UP
				{false, false},  //LEFTJOY_RIGHT
				{false, false},  //LEFTJOY_DOWN
				{false, false},  //LEFTJOY_LEFT
				{false, false},  //RIGHTJOY_UP
				{false, false},  //RIGHTJOY_RIGHT
				{false, false},  //RIGHTJOY_DOWN
				{false, false},  //RIGHTJOY_LEFT
				{false, false},  //XBOX_UP
				{false, false},  //XBOX_RIGHT
				{false, false},  //XBOX_DOWN
                {false, false}};  //XBOX_LEFT
                
    public TeleOP(Drives drives, Hatch hatch){
		this.drives = drives;
		this.hatch = hatch;
        joysticks = new Joystick[] {new Joystick(CtrlMap.RIGHTJOYSTICK), new Joystick(CtrlMap.LEFTJOYSTICK), new Joystick(CtrlMap.XBOXCONTROLLER)};
    }

    @Override
    public void execute(){
		// if(isOffZeroAxis(CtrlMap.RIGHTJOYSTICK, CtrlMap.JOY_Y_AXIS)) {
		// 	drives.joystickRight(getAxis(CtrlMap.RIGHTJOYSTICK, CtrlMap.JOY_Y_AXIS));
		// } else {
		// 	drives.joystickRight(0);
		// }
		// if(isOffZeroAxis(CtrlMap.LEFTJOYSTICK, CtrlMap.JOY_Y_AXIS)) {
		// 	drives.joystickLeft(getAxis(CtrlMap.LEFTJOYSTICK, CtrlMap.JOY_Y_AXIS));
		// } else {
		// 	drives.joystickLeft(0);
		// }
		// if(isPressedButton(CtrlMap.XBOXCONTROLLER, CtrlMap.XBOX_B)){
		// 	drives.buttonB(true);
		// }else{
		// 	drives.buttonB(false);
		// }
		// if(isPressedButton(CtrlMap.XBOXCONTROLLER, CtrlMap.XBOX_R1)){
		// 	drives.buttonB(true);
		// }else{
		// 	drives.buttonB(false);
		// }
		if(isOffZeroAxis(CtrlMap.XBOXCONTROLLER, CtrlMap.XBOX_LEFT_Y)){
			drives.joystickLeft(getAxis(CtrlMap.XBOXCONTROLLER, CtrlMap.XBOX_LEFT_Y));
		}else{
			drives.joystickLeft(0);
		}
		if(isOffZeroAxis(CtrlMap.XBOXCONTROLLER, CtrlMap.XBOX_RIGHT_Y)){
			drives.joystickRight(getAxis(CtrlMap.XBOXCONTROLLER, CtrlMap.XBOX_RIGHT_Y));
		}else{
			drives.joystickRight(0);
		}
		if(isRisingEdgeButton(0)){
			hatch.flipperButton();
		}else if(isRisingEdgeButton(1)){
			hatch.shooterButton();
		}else if(isFallingEdgeButton(0) || isFallingEdgeButton(1)){
			hatch.homeButton();
		}
		if(isPressedButton(CtrlMap.XBOXCONTROLLER, CtrlMap.XBOX_A)){
			drives.findLine();
		}
		if(isPressedButton(CtrlMap.XBOXCONTROLLER, CtrlMap.XBOX_B)){
			drives.toTeleop();
		}
		if(isPressedButton(CtrlMap.XBOXCONTROLLER, CtrlMap.XBOX_X)){
			drives.moveForward();
		}
		if(isPressedButton(CtrlMap.XBOXCONTROLLER, CtrlMap.XBOX_Y)){
			drives.highShift();
		}
		if(isPressedButton(CtrlMap.XBOXCONTROLLER, CtrlMap.XBOX_START)){
			drives.lowShift();
		}
		// if(isRisingEdgeButton(13)){
		// 	hatch.shooterButton(true);
		// }else{
		// 	hatch.shooterButton(false);
		// }
		setJoystickStates();

    }

    /**
	 * Return if the specified button was previously not pressed and is now pressed.
	 * @param pos - the button to check in buttonStates.
	 * @return if the button was previously not pressed and is now pressed.
	 */
	public boolean isRisingEdgeButton(int pos) {
		return buttonStates[pos][0] && !buttonStates[pos][1];
	}
	
	/**
	 * Return if the specified button was previously pressed and is now no longer pressed.
	 * @param pos - the button to check in buttonStates.
	 * @return if the button was previously pressed and is no longer pressed.
	 */
	public boolean isFallingEdgeButton(int pos) {
		return !buttonStates[pos][0] && buttonStates[pos][1];
	}

	/**
	 * Returns if the POV was previously not pressed and is now pressed.
	 * @param pos - the POV to check in posStates.
	 * @return if the POV was previously not pressed and is now pressed.
	 */
	public boolean isRisingEdgePOV(int pos) {
		return povStates[pos][0] && !povStates[pos][1];
	}

	/**
	 * Returns whether or not the button on the specified joystick is pressed.
	 * @param joy - the joystick to check.
	 * @param button - the button to check.
	 * @return if the button is pressed.
	 */
	public boolean isPressedButton(int joy, int button) {
		return joysticks[joy].getRawButton(button);
	}
	
	/**
	 * Returns whether or not the trigger on the specific joystick is pressed (deadband of 0.5).
	 * @param joy - the joystick to check.
	 * @param trigger - the trigger to check.
	 * @return if the trigger is pressed.
	 */
	public boolean isPressedTrigger(int joy, int trigger) {
		return joysticks[joy].getRawAxis(trigger) > CtrlMap.TRIGGER_DEADBAND;
	 }

	/**
	 * Returns whether or not the specific POV is selected on the controller; works in angles of 90 (pov 0 -> 0 degrees, pov 1 -> 90 degrees, pov 2 -> 180 degrees, pov 3 = 270 degrees)
	 * @param joy - the joystick.
	 * @param pov - the POV to check.
	 * @return whether or not the POV on the joystick is pressed.
	 */
	public boolean isPressedPOV(int joy, int pov) {
		return joysticks[joy].getPOV() == pov * 90;
	}

	/**
	 * Returns the value of the axis.
	 * @param joy - the joystick.
	 * @param axis - the axis of the joystick.
	 * @return the specified axis's current position (between -1 and 1; inverted).
	 */
	public double getAxis(int joy, int axis) {
		return -joysticks[joy].getRawAxis(axis);
	}

	/**
	 * Returns if the axis of the specified joystick is greater than the DEADBAND as stated in CtrlMap.java.
	 * @param joy - the joystick.
	 * @param axis - the axis on the joystick to check.
	 * @return if the axis of the specified joystick is greater than the DEADBAND.
	 */
	public boolean isOffZeroAxis(int joy, int axis) {
		return getAxis(joy, axis) > CtrlMap.DEADBAND || getAxis(joy, axis) < -CtrlMap.DEADBAND;
	}

	/**
	 * Updates the previous joystick states to what they currently are now.
	 */
	public void setJoystickStates() {
		for(boolean buttons[]: buttonStates) {
			buttons[1] = buttons[0];
		}
//		for(boolean povs[]: povStates) {
//			povs[1] = povs[0];
//		}
		buttonStates[0][0] = isPressedButton(CtrlMap.XBOXCONTROLLER, CtrlMap.XBOX_R1);
		buttonStates[1][0] = isPressedButton(CtrlMap.XBOXCONTROLLER, CtrlMap.XBOX_L1);
	}
}
