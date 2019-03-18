
/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.IO;

/**
 * Add your docs here.
 */
public class Vision {

	private directions direction;

	public DigitalInput leftIR;

	public DigitalInput centerLeftIR;

	public DigitalInput centerRightIR;

	public DigitalInput rightIR;

	private boolean hitLine, centerHit, rightHitFirst;

	public Vision() {
		leftIR = new DigitalInput(IO.VISION_LEFTFOLLOWINGSENSOR.getPort());
		centerLeftIR = new DigitalInput(IO.VISION_CENTERLEFTFOLLOWINGSENSOR.getPort());
		centerRightIR = new DigitalInput(IO.VISION_CENTERRIGHTFOLLOWINGSENSOR.getPort());
		rightIR = new DigitalInput(IO.VISION_RIGHTFOLLOWINGSENSOR.getPort());
		direction = directions.STANDBY;
	}

	public void reset() {
		hitLine = false;
		rightHitFirst = false;
		centerHit = false;
		System.out.println("Vision reset");
	}

	public enum directions {
		LEFT, SLIGHTLEFT, STANDBY, FORWARD, RIGHT, SLIGHTRIGHT
	}
	
	public boolean getleftIR()
	{
		return !leftIR.get();
	}
	public boolean getCenterleftIR()
	{
		return !centerLeftIR.get();
	}
	public boolean getrightIR()
	{
		return !rightIR.get();
	}
	public boolean getcenterRightIR()
	{
		return !centerRightIR.get();
	}
	
	public directions getDirection() {
		boolean left = !leftIR.get();
		boolean right = !rightIR.get();
		boolean centerLeft = !centerLeftIR.get();
		boolean centerRight = !centerRightIR.get();
		// If we've hit right turn right
		if (right) {
			direction = directions.RIGHT;
			hitLine = true;
			rightHitFirst = true;

			// If we've hit left turn left
		} 
		else if (left) {
			direction = directions.LEFT;
			hitLine = true;
			rightHitFirst = false;
		}

		// We'ver hit line on side now we turn until center line hit.
		if (hitLine && !centerHit) {
			// Waiting for the middle to be found
			if ((rightHitFirst && centerLeft) || (!rightHitFirst && centerRight)) {
				centerHit = true;
			}
		} else {
			// Still Searching for line
			direction = directions.FORWARD;
		}

		// We've found line now we must stay on it
		if (hitLine && centerHit) {
			if (centerLeft) {
				direction = directions.SLIGHTLEFT;
			} else {
				direction = directions.SLIGHTRIGHT;
			}
		}
		return direction;
	}

	public boolean triggered() {
		return hitLine;
	}
}
