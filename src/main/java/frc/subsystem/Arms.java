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
import frc.robot.IO;
import frc.util.MotorGroup;

/**
 * Add your docs here.
 */
public class Arms {

	final double wantedDegree = 17;
	final double wantedSpeed = 1; // gav
	double armOffset;
	double actualDegreeLeft = 0;
	double actualDegreeRight = 0;
	double leftMtrSpeed;
	double rightMtrSpeed;
	double wantedRightMtrPwr;
	double wantedLeftMtrPwr;
	private DigitalInput leftInput;
	private DigitalInput rightInput;
	private MotorGroup rightMtrs;
	private MotorGroup leftMtrs;
	private WPI_TalonSRX leftArmMtr;
	private WPI_TalonSRX rightArmMtr;
	private Encoder leftArmEnc;
	private Encoder rightArmEnc;
	private boolean isDone;
	private boolean stopLeft;
	private boolean stopRight;
	private double stopLeftTimer;
	private double stopRightTimer;

	public Arms(MotorGroup rightMtrs, MotorGroup leftMtrs, Encoder rightEnc, Encoder leftEnc) {
		this.rightMtrs = rightMtrs;
		this.leftMtrs = leftMtrs;
		rightArmEnc = rightEnc;
		leftArmEnc = leftEnc;
		leftInput = new DigitalInput(IO.ARMS_LIMITSWITCH_LEFT);
		rightInput = new DigitalInput(IO.ARMS_LIMITSWITCH_RIGHT);
	}

	public void reset() {
		stopLeft = false;
		stopRight = false;
		actualDegreeLeft = 0;
		actualDegreeRight = 0;
		stopLeftTimer = -1;
		stopRightTimer = -1;
	}

	public void armsDown() {
		isDone = true;
		if (wantedDegree > actualDegreeLeft && (!stopLeft || (stopLeft && !leftInput.get()))) {
			isDone = false;
			if (!leftInput.get()) {
				stopLeft = true;
			}
			leftMtrSpeed = -leftArmEnc.getRate();

			if (leftMtrSpeed < wantedSpeed) {
				wantedLeftMtrPwr -= 0.01; // gav
			} else if (leftMtrSpeed > wantedSpeed) {
				wantedLeftMtrPwr += 0.01; // gav
			}
			wantedLeftMtrPwr = wantedLeftMtrPwr > 1 ? 1 : wantedLeftMtrPwr;
			actualDegreeLeft = -leftArmEnc.getDistance();
			leftMtrs.set(wantedLeftMtrPwr);
		} else {
			leftMtrs.set(0);
		}
		if (wantedDegree > actualDegreeRight && (!stopRight || (stopRight && !rightInput.get()))) {
			isDone = false;
			if (!rightInput.get()) {
				stopRight = true;
			}
			rightMtrSpeed = -rightArmEnc.getRate();
			if (rightMtrSpeed < wantedSpeed) {
				wantedRightMtrPwr += 0.01; // gav
			} else if (rightMtrSpeed > wantedSpeed) {
				wantedRightMtrPwr -= 0.01; // gav
			}
			wantedRightMtrPwr = wantedRightMtrPwr > 1 ? 1 : wantedRightMtrPwr;
			actualDegreeRight = rightArmEnc.getDistance();
			rightMtrs.set(wantedRightMtrPwr);
		} else {
			rightMtrs.set(0);
		}
		if(isDone) {
			rightMtrs.set(0);
			leftMtrs.set(0);
		}
		System.out.println("Left arm: " + leftInput.get());
		System.out.println("Right arm: " + rightInput.get());
		System.out.println("Right motors: " + wantedRightMtrPwr);
		System.out.println("Left motors: " + wantedLeftMtrPwr);
		// System.out.println("Running arms");
	}

	public boolean isDone() {
		return isDone;
	}

	public void armMtrs() {
		leftArmMtr.set(0.5);
		rightArmMtr.set(0.5);
	}

}
