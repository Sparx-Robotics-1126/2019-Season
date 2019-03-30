/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.IO;
import frc.util.MotorGroup;
import frc.util.SendableUtils.SendableBoolean;

/**
 * Add your docs here.
 */
public class Arms {

	// --------------------------------------Motors/Sensors-------------------------------------

	private MotorGroup rightMtrs;

	private MotorGroup leftMtrs;

	private WPI_TalonSRX leftArmMtr;

	private WPI_TalonSRX rightArmMtr;

	private Encoder leftArmEnc;

	private Encoder rightArmEnc;

	private DigitalInput leftInput;

	private DigitalInput rightInput;

	// ----------------------------------------Variable-----------------------------------------

	private double actualDegreeLeft = 0;

	private double actualDegreeRight = 0;

	private double leftMtrSpeed;

	private double rightMtrSpeed;

	private double wantedRightMtrPwr;

	private double wantedLeftMtrPwr;
	
	private boolean isDone;
	
	private boolean stopLeft;

	private boolean stopRight;

	// ----------------------------------------Constants----------------------------------------

	private final double wantedDegree = 17; //17

	private final double wantedSpeed = 7;  //10

	// ------------------------------------------Code-------------------------------------------

	public Arms(MotorGroup rightMtrs, MotorGroup leftMtrs, Encoder rightEnc, Encoder leftEnc) {
		this.rightMtrs = rightMtrs;
		this.leftMtrs = leftMtrs;
		rightArmEnc = rightEnc;
		leftArmEnc = leftEnc;
		leftInput = new DigitalInput(IO.ARMS_LIMITSWITCH_LEFT.port);
		rightInput = new DigitalInput(IO.ARMS_LIMITSWITCH_RIGHT.port);
		GenericSubsystem.addToTables(leftInput, "Arms", "Left Limit Switch");
		GenericSubsystem.addToTables(rightInput, "Arms", "Right Limit Switch");
//		leftStatus = new SendableBoolean("Left arm locked");
//		rightStatus = new SendableBoolean("Right arm locked");
//		SmartDashboard.putData("Left Limit Switch Status", leftStatus);
//		SmartDashboard.putData("Right Limit Switch Status", rightStatus);
//		SmartDashboard.putBoolean("haHAA", false);
	}

	public void reset() {
		stopLeft = false;
		stopRight = false;
		rightArmEnc.reset();
		leftArmEnc.reset();
		actualDegreeLeft = 0;
		actualDegreeRight = 0;
	}

	public void armsDown() {
		isDone = true;
		if (wantedDegree > actualDegreeLeft && !stopLeft) {
			isDone = false;
			if (leftInput.get()) {
				stopLeft = true;
				leftMtrs.set(0);
			} else {
				leftMtrSpeed = -leftArmEnc.getRate();
				if (leftMtrSpeed < wantedSpeed) {
					wantedLeftMtrPwr -= 0.02; // gav
				} else if (leftMtrSpeed > wantedSpeed) {
					wantedLeftMtrPwr += 0.01; // gav
				}
				wantedLeftMtrPwr = wantedLeftMtrPwr > 1 ? 1 : wantedLeftMtrPwr;
				actualDegreeLeft = -leftArmEnc.getDistance();
				leftMtrs.set(wantedLeftMtrPwr);
			}
		} else {
			leftMtrs.set(0);
		}
		if (wantedDegree > actualDegreeRight && !stopRight) {
			isDone = false;
			if (rightInput.get()) {
				stopRight = true;
				rightMtrs.set(0);
			} else {
				rightMtrSpeed = -rightArmEnc.getRate();
				if (rightMtrSpeed < wantedSpeed) {
					wantedRightMtrPwr -= 0.02; // gav
				} else if (rightMtrSpeed > wantedSpeed) {
					wantedRightMtrPwr += 0.01; // gav
				}
				wantedRightMtrPwr = wantedRightMtrPwr > 1 ? 1 : wantedRightMtrPwr;
				actualDegreeRight = rightArmEnc.getDistance();
				rightMtrs.set(wantedRightMtrPwr);
			}
			
		} else {
			rightMtrs.set(0);
		}
		if (isDone) {
			rightMtrs.set(0);
			leftMtrs.set(0);
		}
	}
	
	public void print() {
		System.out.println("Left switch: " + leftInput.get());
		System.out.println("Right switch: " + rightInput.get());
	}
	
	public boolean isDone() {
		return isDone;
	}

	public void armMtrs() {
		leftArmMtr.set(0.5);
		rightArmMtr.set(0.5);
	}

}
