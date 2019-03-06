/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.controls;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import frc.controls.Automation.AutoMethod;
import frc.robot.IO;
import frc.subsystem.Drives;
import frc.subsystem.Drives.DriveState;
import frc.subsystem.HAB;
import frc.subsystem.Hatch;

/**
 * Add your docs here.
 */
public class TeleOP implements Controls {

	private Joystick[] joysticks;

	private Drives drives;
	private HAB hab;
	private Hatch hatch;
	private Automation auto;

	private TeleState state;

	private Solenoid bzzzzzz;

	private boolean[][] buttonStates = { { false, false }, // XBOX_A
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
			{ false, false }, // XBOX_A //start second xbox
			{ false, false }, // XBOX_B
			{ false, false }, // XBOX_X
			{ false, false }, // XBOX_Y
			{ false, false }, // XBOX_L1
			{ false, false }, // XBOX_R1
			{ false, false }, // XBOX_BACK
			{ false, false }, // XBOX_START
			{ false, false }, // XBOX_L2
			{ false, false }, // XBOX_L2
			{ false, false }, // XBOX_L3
			{ false, false } }; // XBOX_R3

	private boolean[][] povStates = { { false, false }, // XBOX_UP
			{ false, false }, // XBOX_RIGHT
			{ false, false }, // XBOX_DOWN
			{ false, false },  // XBOX_LEFT
			{ false, false }, // XBOX_UP //start second xbox
			{ false, false }, // XBOX_RIGHT
			{ false, false }, // XBOX_DOWN
			{ false, false } }; // XBOX_LEFT

	public TeleOP(Drives drives, HAB hab, Hatch hatch) {
		this.drives = drives;
		this.hab = hab;
		this.hatch = hatch;
		this.auto = new Automation(drives, hatch, hab);
		joysticks = new Joystick[] {new Joystick(CtrlMap.XBOXCONTROLLER_MAIN), new Joystick(CtrlMap.XBOXCONTROLLER_CLIMBING)};
		state = TeleState.TELEOP;
		bzzzzzz = new Solenoid(IO.NOISEEEE_SOLENOID);
	}

	public enum TeleState {
		TELEOP,
		AUTOMATION;
	}

	private void setAutomationClimbing() {
		auto.reset();
		auto.addStep(AutoMethod.HAB_PREARMS); //-
		auto.addStep(AutoMethod.HAB_WAIT);
		auto.addStep(AutoMethod.DRIVES_ARMS_DOWN);
		auto.addStep(AutoMethod.DRIVES_WAIT);
		auto.addStep(AutoMethod.HAB_DOWN);
		auto.addStep(AutoMethod.HAB_WHEELS_FORWARD, 0.4);
		//		auto.addStep(AutoMethod.DRIVES_FORWARD, 0.2, 60);
		auto.addStep(AutoMethod.HAB_WAIT);	
		//		auto.addStep(AutoMethod.DRIVES_STOP);
		auto.addStep(AutoMethod.HAB_WHEELS_FORWARD, 1);
		auto.addStep(AutoMethod.AUTO_DELAY, 2); 
		auto.addStep(AutoMethod.HAB_UP);
		auto.addStep(AutoMethod.HAB_WHEELS_FORWARD, 0);
		auto.addStep(AutoMethod.HAB_WAIT);
		auto.addStep(AutoMethod.DRIVES_FORWARD, 0.2, 5);
		auto.addStep(AutoMethod.HAB_WHEELS_FORWARD, 0.4);
		auto.addStep(AutoMethod.DRIVES_WAIT);
		auto.addStep(AutoMethod.HAB_WHEELS_FORWARD, 0);
		auto.addStep(AutoMethod.AUTO_STOP);
		state = TeleState.AUTOMATION;
	}

	@Override
	public void execute() {
		setJoystickStates();
		switch(state) {
		case TELEOP:
			if(bzzzzzz.get()) {
				bzzzzzz.set(false);
			}
			if (isOffZeroAxis(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_LEFT_Y)) {
				drives.joystickLeft(getAxis(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_LEFT_Y));
			} else {
				drives.joystickLeft(0);
			}
			if (isOffZeroAxis(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_RIGHT_Y)) {
				drives.joystickRight(getAxis(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_RIGHT_Y));
			} else {
				drives.joystickRight(0);
			}
			if (isRisingEdgeButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_R1)) {
				hatch.flipperButton();
			} else if (isRisingEdgeButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_L1)) {
				hatch.shooterButton();
			} else if (isFallingEdgeButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_R1) || isFallingEdgeButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_L1)) {
				hatch.homeButton();
			}
			if(isRisingEdgeButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_B)) {
//				drives.togglePTO();
				drives.toAmazingStraightness();
			} else if(isFallingEdgeButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_B)) {
				drives.changeState(DriveState.TELEOP);
			}
			if(isRisingEdgeButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_A)) {
				drives.findLine();
			} else if(isFallingEdgeButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_A)) {
				drives.toTeleop();
			}
			//CLIMBING
			if (isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_A) && isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_L1)) {
				setAutomationClimbing();
			}
			if (isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_Y)) {
				drives.toArms();
			}
			if (isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_START)) {
				hab.ctrlDown();
			}
			if (isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_BACK)) {
				hab.ctrlPreArms();
			}
			if (isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_X)) {
				System.out.println("Button pressed");
				hab.ctrlUP();
			}
			if(isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_B)) {
				drives.togglePTO();
			}

			break;
		case AUTOMATION:
			if(!bzzzzzz.get()) {
				bzzzzzz.set(true);
			}
			auto.execute();
			if(isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_B)) {
				auto.setDone(true);
				auto.stopAll();
			}
			if(auto.isDone()) {
				state = TeleState.TELEOP; //needed?
			}
			break;
		}

	}

	/**
	 * Return if the specified button was previously not pressed and is now pressed.
	 * 
	 * @param pos - the button to check in buttonStates.
	 * @return if the button was previously not pressed and is now pressed.
	 */
	public boolean isRisingEdgeButton(int joy, int pos) {
		if(joy == CtrlMap.XBOXCONTROLLER_MAIN) {
			pos += CtrlMap.XBOXMAINBUTTONOFFSET;
		} else if(joy == CtrlMap.XBOXCONTROLLER_CLIMBING) {
			pos += CtrlMap.XBOXCLIMBINGBUTTONOFFSET;
		}
		return buttonStates[pos][0] && !buttonStates[pos][1];
	}

	/**
	 * Return if the specified button was previously pressed and is now no longer
	 * pressed.
	 * 
	 * @param pos - the button to check in buttonStates.
	 * @return if the button was previously pressed and is no longer pressed.
	 */
	public boolean isFallingEdgeButton(int joy, int pos) {
		if(joy == CtrlMap.XBOXCONTROLLER_MAIN) {
			pos += CtrlMap.XBOXMAINBUTTONOFFSET;
		} else if(joy == CtrlMap.XBOXCONTROLLER_CLIMBING) {
			pos += CtrlMap.XBOXCLIMBINGBUTTONOFFSET;
		}
		return !buttonStates[pos][0] && buttonStates[pos][1];
	}

	/**
	 * Returns if the POV was previously not pressed and is now pressed.
	 * 
	 * @param pos - the POV to check in posStates.
	 * @return if the POV was previously not pressed and is now pressed.
	 */
	public boolean isRisingEdgePOV(int joy, int pos) {
		if(joy == CtrlMap.XBOXCONTROLLER_MAIN) {
			pos += CtrlMap.XBOXMAINPOVOFFSET;
		} else if(joy == CtrlMap.XBOXCONTROLLER_CLIMBING) {
			pos += CtrlMap.XBOXCLIMBINGPOVOFFSET;
		}
		return povStates[pos][0] && !povStates[pos][1];
	}

	/**
	 * Returns if the POV was previously pressed and is now not pressed.
	 * 
	 * @param pos - the POV to check in posStates.
	 * @return if the POV was previously pressed and is now not pressed.
	 */
	public boolean isFallingEdgePOV(int joy, int pos) {
		if(joy == CtrlMap.XBOXCONTROLLER_MAIN) {
			pos += CtrlMap.XBOXMAINPOVOFFSET;
		} else if(joy == CtrlMap.XBOXCONTROLLER_CLIMBING) {
			pos += CtrlMap.XBOXCLIMBINGPOVOFFSET;
		}
		return !povStates[pos][0] && povStates[pos][1];
	}

	/**
	 * Returns whether or not the button on the specified joystick is pressed.
	 * 
	 * @param joy    - the joystick to check.
	 * @param button - the button to check.
	 * @return if the button is pressed.
	 */
	public boolean isPressedButton(int joy, int button) {
		return joysticks[joy].getRawButton(button);
	}

	/**
	 * Returns whether or not the trigger on the specific joystick is pressed
	 * (deadband of 0.5).
	 * 
	 * @param joy     - the joystick to check.
	 * @param trigger - the trigger to check.
	 * @return if the trigger is pressed.
	 */
	public boolean isPressedTrigger(int joy, int trigger) {
		return joysticks[joy].getRawAxis(trigger) > CtrlMap.TRIGGER_DEADBAND;
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
	public boolean isPressedPOV(int joy, int pov) {
		return joysticks[joy].getPOV() == pov * 90;
	}

	/**
	 * Returns the value of the axis.
	 * 
	 * @param joy  - the joystick.
	 * @param axis - the axis of the joystick.
	 * @return the specified axis's current position (between -1 and 1; inverted).
	 */
	public double getAxis(int joy, int axis) {
		return -joysticks[joy].getRawAxis(axis);
	}

	/**
	 * Returns if the axis of the specified joystick is greater than the DEADBAND as
	 * stated in CtrlMap.java.
	 * 
	 * @param joy  - the joystick.
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
		for (boolean buttons[] : buttonStates) {
			buttons[1] = buttons[0];
		}
		for(boolean povs[]: povStates) {
			povs[1] = povs[0];
		}

		buttonStates[0][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_A);
		buttonStates[1][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_B);
		buttonStates[2][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_X);
		buttonStates[3][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_Y);

		buttonStates[4][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_L1);
		buttonStates[5][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_R1);
		buttonStates[6][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_BACK);
		buttonStates[7][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.XBOX_START);
		buttonStates[8][0] = isPressedTrigger(CtrlMap.XBOXCONTROLLER_MAIN,
				CtrlMap.XBOX_L2);
		buttonStates[9][0] = isPressedTrigger(CtrlMap.XBOXCONTROLLER_MAIN,
				CtrlMap.XBOX_R2);
		buttonStates[10][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_MAIN,
				CtrlMap.XBOX_L3);
		buttonStates[11][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_MAIN,
				CtrlMap.XBOX_R3);

		buttonStates[12][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_A);
		buttonStates[13][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_B);
		buttonStates[14][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_X);
		buttonStates[15][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_Y);

		buttonStates[16][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_L1);
		buttonStates[17][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_R1);
		buttonStates[18][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_BACK);
		buttonStates[19][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING, CtrlMap.XBOX_START);
		buttonStates[20][0] = isPressedTrigger(CtrlMap.XBOXCONTROLLER_CLIMBING,
				CtrlMap.XBOX_L2);
		buttonStates[21][0] = isPressedTrigger(CtrlMap.XBOXCONTROLLER_CLIMBING,
				CtrlMap.XBOX_R2);
		buttonStates[22][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING,
				CtrlMap.XBOX_L3);
		buttonStates[23][0] = isPressedButton(CtrlMap.XBOXCONTROLLER_CLIMBING,
				CtrlMap.XBOX_R3);
		//
		// povStates[0][0] = isPressedPOV(CtrlMap.RIGHTJOYSTICK, CtrlMap.POV_UP);
		// povStates[1][0] = isPressedPOV(CtrlMap.RIGHTJOYSTICK, CtrlMap.POV_RIGHT);
		// povStates[2][0] = isPressedPOV(CtrlMap.RIGHTJOYSTICK, CtrlMap.POV_DOWN);
		// povStates[3][0] = isPressedPOV(CtrlMap.RIGHTJOYSTICK, CtrlMap.POV_LEFT);
		// povStates[4][0] = isPressedPOV(CtrlMap.LEFTJOYSTICK, CtrlMap.POV_UP);
		// povStates[5][0] = isPressedPOV(CtrlMap.LEFTJOYSTICK, CtrlMap.POV_RIGHT);
		// povStates[6][0] = isPressedPOV(CtrlMap.LEFTJOYSTICK, CtrlMap.POV_DOWN);
		// povStates[7][0] = isPressedPOV(CtrlMap.LEFTJOYSTICK, CtrlMap.POV_LEFT);
		povStates[0][0] = isPressedPOV(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.POV_UP);
		povStates[1][0] = isPressedPOV(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.POV_RIGHT);
		povStates[2][0] = isPressedPOV(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.POV_DOWN);
		povStates[3][0] = isPressedPOV(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.POV_LEFT);
		povStates[4][0] = isPressedPOV(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.POV_UP);
		povStates[5][0] = isPressedPOV(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.POV_RIGHT);
		povStates[6][0] = isPressedPOV(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.POV_DOWN);
		povStates[7][0] = isPressedPOV(CtrlMap.XBOXCONTROLLER_MAIN, CtrlMap.POV_LEFT);
	}
}
