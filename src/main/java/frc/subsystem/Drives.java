/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.IO;
import frc.subsystem.Vision.directions;
import frc.util.Limelight;
import frc.util.Logger.LogHolder;
import frc.util.Logger.Loggable;
import frc.util.MotorGroup;

/**
 * Add your docs here.
 */
public class Drives extends GenericSubsystem implements Loggable {

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
	
	private Limelight limelightSensor;
	
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

	private boolean isMoving;

	private double slowPercent;

	private double slowSpeed;

	private double currentRate;
	
	private boolean logReady;
	
	private double voltage;
	
	private double current;

	private boolean limelightInCloseRange;
	
	// ----------------------------------------Constants----------------------------------------

	private static final double ANGLE_OFF_BY = 2;
	
	private final double SLOW_TURNING_DEADBAND = 0.15;

	private final double SLOW_TURNING_RATE = 0.8;

	private static final double TURN_SLOW_DEFAULT_PERCENT = 0.5;

	private static final double STRAIGHTEN_MIN_SPEED_MULTIPLIER = 0.7;

	// ------------------------------------------Code-------------------------------------------

	/** Creates a drives object */
	public Drives() {
		super("Drives");
	}

	/** Initializes all the variable in drives */
	public void init() {
		rightMtr1 = new WPI_TalonSRX(IO.DRIVES_RIGHTMOTOR_1.port);
		rightMtr2 = new WPI_TalonSRX(IO.DRIVES_RIGHTMOTOR_2.port);
//		rightMtr3 = new WPI_TalonSRX(IO.DRIVES_RIGHTMOTOR_3);
		leftMtr1 = new WPI_TalonSRX(IO.DRIVES_LEFTMOTOR_1.port);
		leftMtr2 = new WPI_TalonSRX(IO.DRIVES_LEFTMOTOR_2.port);
//		leftMtr3 = new WPI_TalonSRX(IO.DRIVES_LEFTMOTOR_3);
		rightEnc = new Encoder(IO.DRIVES_RIGHTENCODER_CH1.port, IO.DRIVES_RIGHTENCODER_CH2.port);
		leftEnc = new Encoder(IO.DRIVES_LEFTENCODER_CH1.port, IO.DRIVES_LEFTENCODER_CH2.port);
		limelightSensor = new Limelight();
		rightEnc.setDistancePerPulse(-0.02110013);// 0.07897476
		leftEnc.setDistancePerPulse(0.02110013);
		gyro = new AHRS(SerialPort.Port.kUSB);
		gyro.reset();
		resetGyroAngle();
		rightMtrs = new MotorGroup(rightMtr1, rightMtr2); //rightMtr3
		rightMtrs.setInverted(true);
		leftMtrs = new MotorGroup(leftMtr1, leftMtr2); //
		shifter = new Solenoid(IO.DRIVES_SHIFTINGSOLENOID.port);
		drivesPTOArms = new Solenoid(IO.DRIVES_PTOSOLENOID.port);
		arms = new Arms(rightMtrs, leftMtrs, rightEnc, leftEnc);
		unsnappy = new Solenoid(IO.DRIVES_UNSNAPPY.port);
		vision = new Vision();
		lastAngle = 0;
		speedRight = 0;
		speedLeft = 0;
		turnAngle = 0;
		turnSpeed = 0;
		moveDist = 0;
		moveSpeed = 0;
		state = DriveState.STANDBY;
		timer = 0;
		wantedSpeedRight = 0;
		wantedSpeedLeft = 0;
		isMoving = false;
		slowPercent = 1;
		slowSpeed = 0;
		logReady = true;
		limelightInCloseRange = false;
	}

	/** All the states drives can be in */
	public enum DriveState {
		STANDBY, TELEOP, MOVE_FORWARD, MOVE_BACKWARD, TURN_RIGHT, TURN_LEFT, SHIFT_LOW, SHIFT_HIGH, ARMS, FINDING_LINE,
		LINE_FOLLOWER, AMAZING_STRAIGHTNESS, LOOK_FOR_TARGET_LIME;
	}

	/* Runs all the code for drives */
	public void execute() {
		if(shifter.get()) {
			SmartDashboard.putString("SHIFTER", "High gear");
		} else {
			SmartDashboard.putString("SHIFTER", "Low gear");
		}
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
					wantedSpeedRight = slowSpeed;
					wantedSpeedLeft = slowSpeed;
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
			System.out.println("0: " + rightMtrs.get() + ", " + leftMtrs.get());
			if (getAngle() > turnAngle) {
				rightMtrs.stopMotors();
				leftMtrs.stopMotors();
				resetGyroAngle(turnAngle);
				isMoving = false;
				changeState(DriveState.STANDBY);
				System.out.println("1: " + rightMtrs.get() + ", " + leftMtrs.get());
			} else {
				if (turnAngle < getAngle() + 45) {
					System.out.println("2: " + rightMtrs.get() + ", " + leftMtrs.get());
					currentRate = gyro.getRate();
					System.out.println("currentRate: " + currentRate);
					
					if(currentRate > SLOW_TURNING_RATE + SLOW_TURNING_DEADBAND) {
						if(currentRate > 2) {
							turnSpeed = 0;
						} else {
							turnSpeed = turnSpeed - 0.05 > 0.3 ? turnSpeed - 0.05 : 0.3;
						}
						System.out.println("greater than, right: " + rightMtrs.get() + ", left: " + leftMtrs.get());
					} else if(currentRate < SLOW_TURNING_RATE - SLOW_TURNING_DEADBAND) {
						turnSpeed = turnSpeed + 0.05 <= 1 ? turnSpeed + 0.05 : 1;
						System.out.println("less than, right: " + rightMtrs.get() + ", left: " + leftMtrs.get());
					}
				} 
				rightMtrs.set(-turnSpeed);
				leftMtrs.set(turnSpeed);
				System.out.println("3: " + rightMtrs.get() + ", " + leftMtrs.get());
			}
			break;
		case TURN_LEFT:
			if (getAngle() < turnAngle) {
				rightMtrs.stopMotors();
				leftMtrs.stopMotors();
				resetGyroAngle(turnAngle);
				isMoving = false;
				changeState(DriveState.STANDBY);
			} else {
				if (turnAngle > getAngle() - 45) {
					currentRate = gyro.getRate();
					System.out.println("currentRate: " + currentRate);
					if(currentRate < -SLOW_TURNING_RATE - SLOW_TURNING_DEADBAND) {
						if(currentRate < -2) {
							turnSpeed = 0;
						} else {
							turnSpeed = turnSpeed - 0.05 > 0.3 ? turnSpeed - 0.05 : 0.3;
						}
						//						turnSpeed = turnSpeed - 0.05 > 0.3 ? turnSpeed - 0.05 : 0.3;
						System.out.println("greater than, left: " + rightMtrs.get() + ", left: " + leftMtrs.get());
					} else if(currentRate > -SLOW_TURNING_RATE + SLOW_TURNING_DEADBAND) {
						turnSpeed = turnSpeed + 0.05 <= 1 ? turnSpeed + 0.05 : 1;
						System.out.println("less than, left: " + rightMtrs.get() + ", left: " + leftMtrs.get());
					}
				}
				rightMtrs.set(turnSpeed);
				leftMtrs.set(-turnSpeed);
			}
			System.out.println("Gyro angle (turn): " + getAngle());
			break;
		case LINE_FOLLOWER:
			directions st = vision.getDirection();
			if (st == directions.LEFT) {
				leftMtrs.set(-0.4);
				rightMtrs.set(0.5);
			} else if (st == directions.RIGHT) {
				leftMtrs.set(0.4);
				rightMtrs.set(-0.5);
			} else if (st == directions.FORWARD) {
				leftMtrs.set(0.2);
				rightMtrs.set(0.2);
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
			if (timer + 0.1 < Timer.getFPGATimestamp()) {
				leftMtrs.set(speedLeft);
				rightMtrs.set(speedRight);
				changeState(prevState);
			}
			break;
		case ARMS:
			if (timer + 2.5 < Timer.getFPGATimestamp()) {
				drivesPTOArms.set(true);
				arms.armsDown();
				if (arms.isDone()) {
					toStandby();
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
			if(getDistance() > 48) {
				leftMtrs.stopMotors();
				rightMtrs.stopMotors();
				changeState(DriveState.TELEOP);
			}
			System.out.println("finding line");
			vision.getDirection();
			if(moveDist != -1 && moveDist < getDistance()) {
				toStandby();
			}
			if (vision.triggered()) {
				changeState(DriveState.LINE_FOLLOWER);
			} else {
				wantedSpeedLeft = 0.3;
				wantedSpeedRight = 0.3;
				straightenForward();
				rightMtrs.set(wantedSpeedRight);
				leftMtrs.set(wantedSpeedLeft);
			}
			break;
		case LOOK_FOR_TARGET_LIME:
			double angleOff = limelightSensor.getAngle();
			double area = limelightSensor.getAreaOfImage();
			if(area > 5 && !limelightInCloseRange) {
//				limelightSensor.blink(); Disables vision on alternating frames, shouldn't use
				limelightInCloseRange = true; //Locked like this so that when we lose vision by getting too close the slowdown remains enabled
			}

			if(limelightInCloseRange){
				wantedSpeedLeft = .4;
				wantedSpeedRight = .4;
			} else {
				wantedSpeedLeft = 1;
				wantedSpeedRight = 1;	
			}
			amazingLimeness(angleOff);
			rightMtrs.set(wantedSpeedRight);
			leftMtrs.set(wantedSpeedLeft);
			break;
		default:
			break;

		}
//		if(gyro != null) {
//			System.out.println(gyro.getAngle());
//		}
		//		System.out.println("Gyro angle: " + getAngle());
	}

	public void resetGyro() {
		gyro.zeroYaw();
		resetGyroAngle();
	}
	
	@Override
	public void delayedPrints() {
		arms.print();
	}

	public void flipUnsnappy() {
		unsnappy.set(!unsnappy.get());
	}

	/** Checks if drives is not moving */
	public boolean isDone() {
		return !isMoving;
	}

	/** move the robot at a given speed and distance */
	public void move(double speed, double dist) {
		move(speed, dist, 1, speed);
	}

	public void move(double speed, double dist, double slowPercent, double slowSpeed) {
		resetEncoders();
		moveSpeed = speed;
		moveDist = dist;
		this.slowPercent = slowPercent;
		this.slowSpeed = slowSpeed;
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

	public void toStandby() {
		stopMotors();
		changeState(DriveState.STANDBY);
	}

	/** Stops all the motors in drives */
	public void stopMotors() {
		isMoving = false;
		leftMtrs.stopMotors();
		rightMtrs.stopMotors();
	}

	public void stopAll() {
		isMoving = false;
		drivesPTOArms.set(false);
		leftMtrs.stopMotors();
		rightMtrs.stopMotors();
	}

	/** Finds the value that left joystick is reading */
	public void joystickLeft(double speed) {
		speedLeft = speed;
	}

	/** Finds the value that the right joystick is reading */
	public void joystickRight(double speed) {
		speedRight = speed;
	}

	/** Releases the arms from the robot */
	public void toArms() {
		isMoving = true;
		arms.reset();
		unsnappy.set(true);
		timer = Timer.getFPGATimestamp();
		changeState(DriveState.ARMS);
	}

	/** Shifts the robot into low gear */
	public void lowShift() {
		speedLeft = 0;
		speedRight = 0;
		isMoving = true;
		prevState = state;
		timer = Timer.getFPGATimestamp();
		changeState(DriveState.SHIFT_LOW);
	}

	/** shifts the robot into high gear */
	public void highShift() {
		speedRight = 0;
		speedLeft = 0;
		isMoving = true;
		prevState = state;
		timer = Timer.getFPGATimestamp();
		changeState(DriveState.SHIFT_HIGH);
	}

	/** straightens the robot */
	private void straightenForward() {
		double reducedPower = (Math.abs(getAngle())/ANGLE_OFF_BY) > 1 ? wantedSpeedLeft*STRAIGHTEN_MIN_SPEED_MULTIPLIER : ((ANGLE_OFF_BY - Math.abs(getAngle()))/ANGLE_OFF_BY)*wantedSpeedLeft*(1 - STRAIGHTEN_MIN_SPEED_MULTIPLIER) + wantedSpeedLeft*STRAIGHTEN_MIN_SPEED_MULTIPLIER;
//		double reducedPower = (Math.abs(getAngle())/ANGLE_OFF_BY) > 1 ? 0 : (ANGLE_OFF_BY - Math.abs(getAngle())/ANGLE_OFF_BY)*wantedSpeedLeft*(1 - 0.4) + wantedSpeedLeft*(1 - STRAIGHTEN_MIN_SPEED_MULTIPLIER);
		if (getAngle() > 0) {
			wantedSpeedLeft = reducedPower;
			//			System.out.println("correcting rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
		} else if (getAngle() < 0) {
			//			System.out.println("correcting oooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");
			wantedSpeedRight = reducedPower;
		}
	}
	
	/** straightens the robot */
	private void amazingLimeness(double angle) {
		double reducedPower = (Math.abs(angle)/ANGLE_OFF_BY) > 1 ? wantedSpeedLeft*STRAIGHTEN_MIN_SPEED_MULTIPLIER : ((ANGLE_OFF_BY - Math.abs(angle))/ANGLE_OFF_BY)*wantedSpeedLeft*(1 - STRAIGHTEN_MIN_SPEED_MULTIPLIER) + wantedSpeedLeft*STRAIGHTEN_MIN_SPEED_MULTIPLIER;
		if (angle > 0) {
			wantedSpeedRight = reducedPower;
		} else if (angle < 0) {
			wantedSpeedLeft = reducedPower;
		}
	}

	/** gets the distance the robot has travelled since the last time the encoders */
	private double getDistance() {
		return Math.abs(rightEnc.getDistance()) > Math.abs(leftEnc.getDistance()) ? rightEnc.getDistance() : leftEnc.getDistance();
	}

	/** gets the average rate of the robot */
	private double getAverageRate() {
		return (rightEnc.getRate() + leftEnc.getRate()) / 2;
	}

	/** gets the angle the robot has turned since the last time the gyro was reset */
	private double getAngle() {
		return gyro.getAngle() - lastAngle;
	}

	/** resets the gyro's angle so the robot turns to the angle from where the robot */
	public void resetGyroAngle() {
		lastAngle = gyro.getAngle();
		System.out.println("Reset: " + lastAngle);
	}

	/** resets the gyro's angle so the robot turns to the angle from where the robot */
	private void resetGyroAngle(double lastAngle) {
		this.lastAngle += lastAngle;
		System.out.println("Reset: " + lastAngle);
	}

	/** turns the robot a specified angle */
	public void turn(double speed, double angle) {
		turn(speed, angle, TURN_SLOW_DEFAULT_PERCENT);
	}

	public void turn(double speed, double angle, double slowPercent) {
		turnAngle = angle;
		turnSpeed = speed;
		this.slowPercent = slowPercent;
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
	
	public void setShifting(boolean shiftingValue) {
		shifter.set(shiftingValue);
	}
	
	public void toggleShifting() {
		shifter.set(!shifter.get());
	}

	public void togglePTO() {
		drivesPTOArms.set(!drivesPTOArms.get());
	}

	/** changes the state of the robot to what is given as a parameter */
	public void changeState(DriveState st) {
		if (state == DriveState.ARMS && st != DriveState.ARMS) {
			drivesPTOArms.set(false);
		}
		state = st;
	}

	/** used by RobotSystem to put the robot in the teleop state */
	public void toTeleop() {
		limelightSensor.setEnable(false);
		isMoving = false;
		rightMtrs.setNeutralMode(NeutralMode.Brake);
		leftMtrs.setNeutralMode(NeutralMode.Brake);
		changeState(DriveState.TELEOP);
		// turn(0.5, 90);
		// move(0.5, 240);
	}

	public void toAuto() {
		isMoving = false;
		resetGyroAngle();
		rightMtrs.setNeutralMode(NeutralMode.Brake);
		leftMtrs.setNeutralMode(NeutralMode.Brake);
	}

	/** moves the robot forward, used to call move once for drives */
	public void moveForward() {
		move(1, 120);
	}

	// hi
	public void findLine() {
		findLine(-1);
	}

	public void startLimelightFollow() {
		limelightSensor.setEnable(true);
		limelightInCloseRange = false;
		isMoving = true;
		changeState(DriveState.LOOK_FOR_TARGET_LIME);
	}

	public void findLine(double maxDistance) {
		moveDist = maxDistance;
		resetEncoders();
		resetGyroAngle();
		isMoving = true;
		vision.reset();
		changeState(DriveState.FINDING_LINE);
	}

	/** retruns the Arms object */
	public Arms getArms() {
		return arms;
	}

	/** resets vision */
	public void resetVision() {
		if(vision != null) {
			vision.reset();
		}
	}

	@Override
	public long sleepTime() {
		return 5;
	}

	@Override
	public void smartDashboardInit() {
		// rightMtr1.setName("Drives", "Right motor 1");
		// rightMtr2.setName("Drives", "Right motor 2");
		// rightMtr3.setName("Drives", "Right motor 3");
		// lightMtr1.setName("Drives", "Left motor 1");
		// leftMtr2.setName("Drives", "Left motor 2");
		// leftMtr3.setName("Drives", "Left motor 3");
		//		LiveWindow.remove(rightMtr1);
		//		LiveWindow.remove(rightMtr2);
		//		LiveWindow.remove(rightMtr3);
		//		LiveWindow.remove(leftMtr1);
		//		LiveWindow.remove(leftMtr2);
		//		LiveWindow.remove(leftMtr3);
		addToTables(rightMtrs, "Right drives");
		addToTables(leftMtrs, "Left drives");
		addToTables(rightEnc, "Right drives encoder");
		addToTables(leftEnc, "Left drives encoder");
		addToTables(shifter, "Shifter");
		addToTables(drivesPTOArms, "Arms", "Drives PTO (Arms)");
		addToTables(unsnappy, "Arms", "Unsnappy");
		addToTables(gyro, "Gyro");
		addToTables(vision.centerLeftIR, "Vision", "CLIR");
		addToTables(vision.leftIR, "Vision",  "LIR");
		addToTables(vision.centerRightIR, "Vision",  "CRIR");
		addToTables(vision.rightIR, "Vision", "R IR");
	}
	
//	@Override
//	public void logPeriodic(LogHolder lh) {
//		lh.updateLogClass("DRIVES_PERIODIC");
//		lh.logLine("Left drives motors: " + leftMtrs.get());
//		lh.logLine("Right drives motors: " + rightMtrs.get());
//		lh.logLine("Left drives encoder: " + leftEnc.getDistance());
//		lh.logLine("Right drives encoder: " + rightEnc.getDistance());
//		lh.logLine("Shifter: " + (shifter.get() ? "High Gear" : "Low Gear"));
//		if(gyro != null) {
//			lh.logLine("Gyro (virtual angle): " + getAngle());
//			lh.logLine("Gyro (real angle): " + gyro.getAngle());
//		}
//		if(vision != null) {
//			lh.updateLogClass("VISION_PERIODIC");
//			lh.logLine("Vision Left IR: " + vision.getLeftIR());
//			lh.logLine("Vision CenterLeft IR: " + vision.getCenterLeftIR());
//			lh.logLine("Vision CenterRight IR: " + vision.getCenterRightIR());
//			lh.logLine("Vision Right IR: " + vision.getRightIR());
//		}
//		lh.updateLogClass("ARMS_PERIODIC");
//		lh.logLine("Arms PTO: " + drivesPTOArms.get());
//		lh.logLine("Unsnappy: " + unsnappy.get());
//		
//	}
	
	@Override
	public void logPeriodic(LogHolder lh) {
		lh.updateLogClass("DRIVES_PERIODIC");
		lh.logLine("Left drives motors: " + leftMtrs.get());
		lh.logLine("Right drives motors: " + rightMtrs.get());
		lh.logLine("Left drives encoder: " + leftEnc.getDistance());
		lh.logLine("Right drives encoder: " + rightEnc.getDistance());
		lh.logLine("Left drives motors (voltage): " + leftMtrs.getVoltage());
		lh.logLine("Right drives motors (voltage): " + rightMtrs.getVoltage());
		lh.logLine("Left drives motors (current): " + leftMtrs.getCurrent());
		lh.logLine("Right drives motors (current): " + rightMtrs.getCurrent());
//		lh.logLine("Shifter: " + (shifter.get() ? "High Gear" : "Low Gear"));
//		if(gyro != null) {
//			lh.logLine("Gyro (virtual angle): " + getAngle());
//			lh.logLine("Gyro (real angle): " + gyro.getAngle());
//		}
//		if(vision != null) {
//			lh.updateLogClass("VISION_PERIODIC");
//			lh.logLine("Vision Left IR: " + vision.getLeftIR());
//			lh.logLine("Vision CenterLeft IR: " + vision.getCenterLeftIR());
//			lh.logLine("Vision CenterRight IR: " + vision.getCenterRightIR());
//			lh.logLine("Vision Right IR: " + vision.getRightIR());
//		}
//		lh.updateLogClass("ARMS_PERIODIC");
//		lh.logLine("Arms PTO: " + drivesPTOArms.get());
//		lh.logLine("Unsnappy: " + unsnappy.get());
//		
	}
	
	@Override
	public boolean logReady() {
		return logReady;
	}


}
