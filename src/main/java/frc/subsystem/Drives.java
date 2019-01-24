/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.IO;
import frc.sensors.EncoderData;
import frc.subsystem.Vision.directions;
import frc.util.MotorGroup;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;

/**
 * Add your docs here.
 */
public class Drives extends GenericSubsystem{

    //----------------------------------------Motors/Sensors----------------------------------------

    private WPI_TalonSRX rightMtr1;

    private WPI_TalonSRX rightMtr2;

    //private WPI_TalonSRX rightMtr3;

    private WPI_TalonSRX leftMtr1;

    private WPI_TalonSRX leftMtr2;

    //private WPI_TalonSRX leftMtr3;

    private Encoder rawRight;

    private Encoder rawLeft;

    private EncoderData rightEncoder; 

    private EncoderData leftEncoder;

    private AHRS gyro;

    private MotorGroup rightMtrs;

    private MotorGroup leftMtrs;

    private Solenoid drivesPTO;

    //----------------------------------------Variable----------------------------------------

    private double lastAngle;

    private double speedRight;

    private double speedLeft;

    private double turnAngle;

    private double turnSpeed;

    private double moveSpeed;

    private double moveDist;

    private DriveState state;

    private Vision vision;

    //----------------------------------------Constants----------------------------------------

    private final double ANGLE_OFF_BY = .1;

    private final double SPEED_PERCENTAGE = .8;

    //create a drives object
    public Drives(){
        super("Drives");
    }

    //initialized all the variable in drives
    public void init(){
        rightMtr1 = new WPI_TalonSRX(IO.rightDriveCIM1);
        rightMtr2 = new WPI_TalonSRX(IO.rightDriveCIM2);
        leftMtr1 = new WPI_TalonSRX(IO.leftDriveCIM1);
        leftMtr2 = new WPI_TalonSRX(IO.leftDriveCIM2);
        rightMtrs = new MotorGroup(rightMtr1, rightMtr2);
        leftMtrs = new MotorGroup(leftMtr1, leftMtr2);
        rawRight = new Encoder(IO.rightDrivesEncoderChannel1, IO.rightDrivesEncoderChannel2);
        rawLeft = new Encoder(IO.leftDrivesEncoderChannel1, IO.leftDrivesEncoderChannel2);
        leftEncoder = new EncoderData(rawLeft, 0.033860431);
        rightEncoder = new EncoderData(rawRight, 0.033860431);
        rightEncoder.reset();
        leftEncoder.reset();
        leftMtrs.setInverted(true);
        gyro = new AHRS(SerialPort.Port.kUSB);
        lastAngle = 0;
        speedLeft = 0;
        speedRight = 0;
        resetGyroAngle();
        moveDist = 0;
        moveSpeed = 0;
        turnAngle = 0;
        turnSpeed = 0;
        drivesPTO = new Solenoid(0);
        vision = new Vision();
        //state = state.STANDBY;
    }

    public enum DriveState{
        STANDBY,
        TELEOP,
        MOVE_FORWARD,
        MOVE_BACKWARD,
        TURN_RIGHT,
        TURN_LEFT,
        LINE_FOLLOWER;
    }

    //does all the code for drives
    public void execute(){
        //move(0.8, 150);
        //turn(0.5, 90);
        changeState(DriveState.LINE_FOLLOWER);
        switch(state){
            case STANDBY:
                break;
            case TELEOP:
                System.out.println("Drives SpeedRight: " + speedRight + " speedLeft: " + speedLeft);
                rightMtrs.set(speedRight);
                leftMtrs.set(speedLeft);
                break;
            case MOVE_FORWARD:
                if(getDistance() > moveDist){
                    rightMtrs.stopMotors();
                    leftMtrs.stopMotors();
                    changeState(DriveState.STANDBY);
                }else{
                    rightMtrs.set(speedRight);
                    leftMtrs.set(speedLeft);
                }
                break;
            case MOVE_BACKWARD:
                if(getDistance() < moveDist){
                    rightMtrs.stopMotors();
                    leftMtrs.stopMotors();
                    changeState(DriveState.STANDBY);
                }else{
                    rightMtrs.set(-speedRight);
                    leftMtrs.set(-speedLeft);
                }
                break;
            case TURN_RIGHT:
                if(getAngle() > turnAngle){
                    rightMtrs.stopMotors();
                    leftMtrs.stopMotors();
                    changeState(DriveState.STANDBY);
                }else{
                    rightMtrs.set(speedRight);
                    leftMtrs.set(-speedLeft);
                }
                break;
            case TURN_LEFT:
                if(getAngle() < turnAngle){
                    rightMtrs.stopMotors();
                    leftMtrs.stopMotors();
                    changeState(DriveState.STANDBY);
                }else{
                    rightMtrs.set(-speedRight);
                    leftMtrs.set(speedLeft);
                }
                break;
            case LINE_FOLLOWER:
                directions st = vision.getDirection();
                if(st == directions.LEFT){
                    leftMtrs.set(-0.3);
                    rightMtrs.set(0.3);
                }else if(st == directions.RIGHT){
                    leftMtrs.set(0.3);
                    rightMtrs.set(-0.3);
                }else if(st == directions.STANDBY){
                    leftMtrs.set(0.2);
                    rightMtrs.set(0.2);
                }
        }
      //  System.out.println("State: " + )
        System.out.println("Right Encoder: " + leftEncoder.getDistance());
        System.out.println("Left Encoder: " + rightEncoder.getDistance());
    }

    //debugs all the possible problems in drives
    public void debug(){

    }

    //checks if drives is done with its autonomous code
    public boolean isDone(){
        return false;
    }

    //the time in milliseconds between each call to execute
    public long sleepTime(){
        return 20;
    }

    //move the robot at a given speed and distance
    public void move(double speed, double dist){
        moveSpeed = speed;
        moveDist = dist;
        speedRight = moveSpeed;
        speedLeft = moveSpeed;
        if(moveDist > 0){
            changeState(DriveState.MOVE_FORWARD);
        }else{
            changeState(DriveState.MOVE_BACKWARD);
        }
    }

    public void joystickLeft(double speed) {
      //  speedLeft = speed;
      leftMtrs.set(speed);
    }

    public void joystickRight(double speed) {
        //speedRight = speed;
        rightMtrs.set(speed);
    }

    public void buttonB(boolean a){
        drivesPTO.set(a);
    }

    //straightens the robot
    private void straightenForward(){
        if(getAngle() > ANGLE_OFF_BY){
            speedRight *= SPEED_PERCENTAGE; 
        }else if(getAngle() < ANGLE_OFF_BY){
            speedLeft *= SPEED_PERCENTAGE;
        }
    }

    //gets the distance the robot has travelled since the last time the encoders were reset
    private double getDistance() {
		rightEncoder.calculateSpeed();
		leftEncoder.calculateSpeed();
		return (rightEncoder.getDistance() + leftEncoder.getDistance())/2;
    }

    //gets the angle the robot has turned since the last time the gyro was reset 
    private double getAngle(){
        return gyro.getAngle() - lastAngle;
    }

    //resets the gyro's angle so the robot turns to the angle from where the robot is currently facing
    private void resetGyroAngle(){
        lastAngle = gyro.getAngle();
    }

    //turns the robot a specified angle 
    public void turn(double speed, double angle){
        turnAngle = angle;
        turnSpeed = speed;
        if(angle > 0){
          changeState(DriveState.TURN_RIGHT);
        }else{
            if(getAngle() < angle){
                rightMtrs.stopMotors();
                leftMtrs.stopMotors();
            }else{
                rightMtrs.set(-speed);
                leftMtrs.set(speed);
            }
        }

    }

    //changes the state of the robot to what is given as a parameter
    private void changeState(DriveState st){
        state = st;
    }

    //used by RobotSystem to put the robot in the teleop state
    public void toTeleop(){
        changeState(DriveState.TELEOP);
    }

}
