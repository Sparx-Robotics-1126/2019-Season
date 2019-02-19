/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import frc.robot.IO;
import frc.subsystem.Vision.directions;
import frc.util.MotorGroup;

/**
 * Add your docs here.
 */
public class Drives extends GenericSubsystem {

	// --------------------------------------Motors/Sensors-------------------------------------

	private WPI_TalonSRX rightMtr1;

	private WPI_TalonSRX rightMtr2;

	private WPI_TalonSRX rightMtr3;

	private WPI_TalonSRX leftMtr1;

	private WPI_TalonSRX leftMtr2;

	private WPI_TalonSRX leftMtr3;

	private Encoder rightEnc;

	private Encoder leftEnc;

	private AHRS gyro;

	private MotorGroup rightMtrs;

	private MotorGroup leftMtrs;

	private Solenoid shifter;

	private Solenoid drivesPTOArms;

	private Arms arms;

	private Solenoid unsnappy;

	private Vision vision;

	// ----------------------------------------Variable-----------------------------------------

	private double lastAngle;

	private double speedRight;

	private double speedLeft;

	private double turnAngle;

	private double turnSpeed;

	private double moveDist;

	private double moveSpeed;

	private DriveState state;

	private DriveState prevState;

	private double timer;

	private double wantedSpeedRight;

	private double wantedSpeedLeft;

	private boolean shiftingPosition;

	private boolean isMoving;

	private double slowPercent;

	private double slowSpeed;
	
	private double highestLeft;
	
	private double highestRight;

	// ----------------------------------------Constants----------------------------------------

	private final double ANGLE_OFF_BY = 1;

	private final double SPEED_PERCENTAGE = .7;

	// ------------------------------------------Code-------------------------------------------

	// create a drives object
	public Drives() {
		super("Drives");
	}

	// initialized all the variable in drives
	public void init() {
		rightMtr1 = new WPI_TalonSRX(IO.DRIVES_RIGHTMOTOR_1);
		rightMtr2 = new WPI_TalonSRX(IO.DRIVES_RIGHTMOTOR_2);
		rightMtr3 = new WPI_TalonSRX(IO.DRIVES_RIGHTMOTOR_3);
		leftMtr1 = new WPI_TalonSRX(IO.DRIVES_LEFTMOTOR_1);
		leftMtr2 = new WPI_TalonSRX(IO.DRIVES_LEFTMOTOR_2);
		leftMtr3 = new WPI_TalonSRX(IO.DRIVES_LEFTMOTOR_3);
		rightEnc = new Encoder(IO.DRIVES_RIGHTENCODER_CH1, IO.DRIVES_RIGHTENCODER_CH2);
		leftEnc = new Encoder(IO.DRIVES_LEFTENCODER_CH1, IO.DRIVES_LEFTENCODER_CH2);
		rightEnc.setDistancePerPulse(-0.02110013);// 0.07897476
		leftEnc.setDistancePerPulse(0.02110013);
		gyro = new AHRS(SerialPort.Port.kUSB);
		gyro.reset();
		resetGyroAngle();
		rightMtrs = new MotorGroup(rightMtr1, rightMtr2, rightMtr3);
		rightMtrs.setInverted(true);
		leftMtrs = new MotorGroup(leftMtr1, leftMtr2, leftMtr3);
		shifter = new Solenoid(IO.DRIVES_SHIFTINGSOLENOID);
		drivesPTOArms = new Solenoid(IO.DRIVES_PTOSOLENOID);
		arms = new Arms(rightMtrs, leftMtrs, rightEnc, leftEnc);
		unsnappy = new Solenoid(IO.DRIVES_UNSNAPPY);
		vision = new Vision();
		lastAngle = 0;
		speedRight = 0;
		speedLeft = 0;
		turnAngle = 0;
		turnSpeed = 0;
		moveDist = 0;
		moveSpeed = 0;
		highestLeft = 0;
		highestRight = 0;
		state = DriveState.STANDBY;
		timer = 0;
		wantedSpeedRight = 0;
		wantedSpeedLeft = 0;
		shiftingPosition = false;
		isMoving = false;
		slowPercent = 1;
		slowSpeed = 0;
	}

	// all the states drives can be in
	public enum DriveState {
		STANDBY, TELEOP, MOVE_FORWARD, MOVE_BACKWARD, TURN_RIGHT, TURN_LEFT, SHIFT_LOW, SHIFT_HIGH, ARMS, FINDING_LINE,
		LINE_FOLLOWER, AMAZING_STRAIGHTNESS;
	}

	// does all the code for drives
	public void execute() {
		if (state != DriveState.ARMS && drivesPTOArms.get()) {
			drivesPTOArms.set(false);
		}
		switch (state) {
		case STANDBY:
			break;
		case TELEOP:
			if(shifter.get()) {
				lowShift();
			}
			rightMtrs.set(speedRight);
			leftMtrs.set(speedLeft);
			break;
		case MOVE_FORWARD:
			if (getDistance() > moveDist) {
				rightMtrs.stopMotors();
				leftMtrs.stopMotors();
				isMoving = false;
				changeState(DriveState.STANDBY);
			} else {
				if (getDistance() * slowPercent > moveDist) {
					wantedSpeedRight = slowSpeed;
					wantedSpeedLeft = slowSpeed;
				} else {
					wantedSpeedRight = moveSpeed;
					wantedSpeedLeft = moveSpeed;
				}
				straightenForward();
				rightMtrs.set(wantedSpeedRight);
				leftMtrs.set(wantedSpeedLeft);
			}
			break;
		case MOVE_BACKWARD:
			if (getDistance() < moveDist) {
				rightMtrs.stopMotors();
				leftMtrs.stopMotors();
				isMoving = false;
				changeState(DriveState.STANDBY);
			} else {
				if (getDistance() * slowPercent < moveDist) {
					wantedSpeedRight = moveSpeed;
					wantedSpeedLeft = moveSpeed;
				} else {
					wantedSpeedRight = moveSpeed;
					wantedSpeedLeft = moveSpeed;
				}
				straightenForward();
				rightMtrs.set(-wantedSpeedLeft);
				leftMtrs.set(-wantedSpeedRight);
			}
			break;
		case TURN_RIGHT:
			if (getAngle() > turnAngle) {
				rightMtrs.stopMotors();
				leftMtrs.stopMotors();
				isMoving = false;
				changeState(DriveState.STANDBY);
			} else {
				if (slowPercent * getAngle() > turnAngle) {
					rightMtrs.set(-slowSpeed);
					leftMtrs.set(slowSpeed);
				} else {
					rightMtrs.set(-turnSpeed);
					leftMtrs.set(turnSpeed);
				}
			}
			break;
		case TURN_LEFT:
			if (getAngle() < turnAngle) {
				rightMtrs.stopMotors();
				leftMtrs.stopMotors();
				isMoving = false;
				changeState(DriveState.STANDBY);
			} else {
				if (slowPercent * getAngle() < turnAngle) {
					rightMtrs.set(slowSpeed);
					leftMtrs.set(-slowSpeed);
				} else {
					rightMtrs.set(turnSpeed);
					leftMtrs.set(-turnSpeed);
				}
			}
			break;
		case LINE_FOLLOWER:
			directions st = vision.getDirection();
			// System.out.println("Left motor power = " + leftMtr1.getBusVoltage());
			// System.out.println("right motor power = " + rightMtr1.getBusVoltage());
			if (st == directions.LEFT) {
				leftMtrs.set(-0.5);
				rightMtrs.set(0.5);
			} else if (st == directions.RIGHT) {
				leftMtrs.set(0.5);
				rightMtrs.set(-0.5);
			} else if (st == directions.FORWARD) {
				leftMtrs.set(0.3);
				rightMtrs.set(0.3);
			} else if (st == directions.SLIGHTLEFT) {
				isMoving = false;
				leftMtrs.set(0.00);
				rightMtrs.set(0.40);
			} else if (st == directions.SLIGHTRIGHT) {
				isMoving = false;
				leftMtrs.set(0.40);
				rightMtrs.set(0.00);
			} else if (st == directions.STANDBY) {
				leftMtrs.set(0);
				rightMtrs.set(0);
			}
			break;
		case SHIFT_LOW:
			leftMtrs.set(0.2);
			rightMtrs.set(0.2);
			shifter.set(false);
			shiftingPosition = false;
			if (timer + 0.1 < Timer.getFPGATimestamp()) {
				leftMtrs.set(speedLeft);
				rightMtrs.set(speedRight);
				isMoving = false;
				changeState(prevState);
			}
			break;
		case SHIFT_HIGH:
			leftMtrs.set(0.2);
			rightMtrs.set(0.2);
			shifter.set(true);
			shiftingPosition = true;
			if (timer + 0.1 < Timer.getFPGATimestamp()) {
				leftMtrs.set(speedLeft);
				rightMtrs.set(speedRight);
				changeState(prevState);
			}
			break;
		case ARMS:
			if (timer + 1.5 < Timer.getFPGATimestamp()) {
				drivesPTOArms.set(true);
				arms.armsDown();
				if (arms.isDone()) {
					changeState(DriveState.STANDBY);
					isMoving = false;
				}
			}
			break;
		case AMAZING_STRAIGHTNESS:
			if (!shifter.get() && (getAverageRate() > 65)) {
				highShift();
			}
			wantedSpeedLeft = 1;
			wantedSpeedRight = 1;
			straightenForward();
			leftMtrs.set(wantedSpeedLeft);
			rightMtrs.set(wantedSpeedRight);
			break;
		case FINDING_LINE:
			vision.getDirection();
			if (vision.triggered()) {
				changeState(DriveState.LINE_FOLLOWER);
			} else {
				rightMtrs.set(0.35);
				leftMtrs.set(0.35);
			}
			break;

		}
		// System.out.println("State: " + )
		// System.out.println("Right Encoder: " + rightEnc.getDistance());
		// System.out.println("Left Encoder: " + leftEnc.getDistance());
		// System.out.println("Gyro: " + getAngle());
		
		 System.out.println("left rate: " + leftEnc.getRate());
		 System.out.println("right rate: " + rightEnc.getRate());
		 if(Math.abs(leftEnc.getRate()) > highestLeft) {
			 highestLeft = leftEnc.getRate();
		 }
		 if(Math.abs(rightEnc.getRate()) > highestRight) {
			 highestRight = rightEnc.getRate();
		 }
		 System.out.println("highest left rate: " + highestLeft);
		 System.out.println("highest right rate: " + highestRight);
		// System.out.println("GetDistance: " + getDistance());
		// System.out.println("RightMtr" + wantedSpeedRight + " LeftMtr: " +
		// wantedSpeedLeft);
	}

	@Override
	public void delayedPrints() {

	}

	public void flipUnsnappy() {
		unsnappy.set(!unsnappy.get());
	}

	// checks if drives is done with its autonomous code
	public boolean isDone() {
		return !isMoving;
	}

	// move the robot at a given speed and distance
	public void move(double speed, double dist) {
		move(speed, dist, 1, speed);
	}

	public void move(double speed, double dist, double slowPercent, double slowSpeed) {
		resetEncoders();
		moveSpeed = speed;
		moveDist = dist;
		this.slowPercent = slowPercent;
		this.slowSpeed = slowSpeed;
		resetGyroAngle();
		isMoving = true;
		if (moveDist > 0) {
			changeState(DriveState.MOVE_FORWARD);
		} else {
			changeState(DriveState.MOVE_BACKWARD);

		}
	}

	public void resetEncoders() {
		leftEnc.reset();
		rightEnc.reset();
	}

	// stops all the motors in drives
	public void stopMotors() {
		isMoving = false;
		leftMtrs.stopMotors();
		rightMtrs.stopMotors();
	}

	// finds the value that left joystick is reading
	public void joystickLeft(double speed) {
		speedLeft = speed;
	}

	// finds the value that the right joystick is reading
	public void joystickRight(double speed) {
		speedRight = speed;
	}

	// releases the arms from the robot
	public void toArms() {
		isMoving = true;
		arms.reset();
		unsnappy.set(true);
		timer = Timer.getFPGATimestamp();
		changeState(DriveState.ARMS);
	}

	// shifts the robot into low gear
	public void lowShift() {
		speedLeft = 0;
		speedRight = 0;
		isMoving = true;
		prevState = state;
		timer = Timer.getFPGATimestamp();
		changeState(DriveState.SHIFT_LOW);
	}

	// shifts the robot into high gear
	public void highShift() {
		speedRight = 0;
		speedLeft = 0;
		isMoving = true;
		prevState = state;
		timer = Timer.getFPGATimestamp();
		changeState(DriveState.SHIFT_HIGH);
	}

	// straightens the robot
	private void straightenForward() {
		if (getAngle() > ANGLE_OFF_BY) {
			wantedSpeedLeft *= SPEED_PERCENTAGE;
			System.out.println("correcting rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
		} else if (getAngle() < -ANGLE_OFF_BY) {
			System.out.println("correcting oooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");
			wantedSpeedRight *= SPEED_PERCENTAGE;
		}
	}

	// gets the distance the robot has travelled since the last time the encoders
	private double getDistance() {
		return (rightEnc.getDistance() + leftEnc.getDistance()) / 2;
	}

	// gets the average rate of the robot
	private double getAverageRate() {
		return (rightEnc.getRate() + leftEnc.getRate()) / 2;
	}

	// gets the angle the robot has turned since the last time the gyro was reset
	private double getAngle() {
		return gyro.getAngle() - lastAngle;
	}

	// resets the gyro's angle so the robot turns to the angle from where the robot
	private void resetGyroAngle() {
		lastAngle = gyro.getAngle();
		System.out.println("Reset: " + lastAngle);
	}

	// turns the robot a specified angle
	public void turn(double speed, double angle) {
		turn(speed, angle, 1, speed);
	}

	public void turn(double speed, double angle, double slowPercent, double slowSpeed) {
		turnAngle = angle;
		turnSpeed = speed;
		this.slowPercent = slowPercent;
		this.slowSpeed = slowSpeed;
		resetGyroAngle();
		isMoving = true;
		if (angle > 0) {
			changeState(DriveState.TURN_RIGHT);
		} else {
			changeState(DriveState.TURN_LEFT);
		}

	}
	
	public void toAmazingStraightness() {
		resetGyroAngle();
		changeState(DriveState.AMAZING_STRAIGHTNESS);
	}

	// changes the state of the robot to what is given as a parameter
	public void changeState(DriveState st) {
		if (state == DriveState.ARMS && st != DriveState.ARMS) {
			drivesPTOArms.set(false);
		}
		state = st;
	}

	// used by RobotSystem to put the robot in the teleop state
	public void toTeleop() {
		changeState(DriveState.TELEOP);
		// turn(0.5, 90);
		// move(0.5, 240);
	}

	// moves the robot forward, used to call move once for drives
	public void moveForward() {
		move(1, 120);
	}

	public void findLine() {
		isMoving = true;
		vision.reset();
		changeState(DriveState.FINDING_LINE);
	}

	// retruns the Arms object
	public Arms getArms() {
		return arms;
	}

	// resets vision
	public void resetVision() {
		vision.reset();
	}

	@Override
	public long sleepTime() {
		return 20;
	}

	@Override
	public void smartDashboardInit() {
		// rightMtr1.setName("Drives", "Right motor 1");
		// rightMtr2.setName("Drives", "Right motor 2");
		// rightMtr3.setName("Drives", "Right motor 3");
		// lightMtr1.setName("Drives", "Left motor 1");
		// leftMtr2.setName("Drives", "Left motor 2");
		// leftMtr3.setName("Drives", "Left motor 3");
		LiveWindow.remove(rightMtr1);
		LiveWindow.remove(rightMtr2);
		LiveWindow.remove(rightMtr3);
		LiveWindow.remove(leftMtr1);
		LiveWindow.remove(leftMtr2);
		LiveWindow.remove(leftMtr3);
		addToTables(rightMtrs, "Right drives");
		addToTables(leftMtrs, "Left drives");
		addToTables(rightEnc, "Right drives encoder");
		addToTables(leftEnc, "Left drives encoder");
		addToTables(shifter, "Shifter");
		addToTables(drivesPTOArms, "Arms", "Drives PTO (Arms)");
		addToTables(unsnappy, "Arms", "Unsnappy");
		addToTables(gyro, "Gyro");
	}

}
