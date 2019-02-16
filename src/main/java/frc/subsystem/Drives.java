    /*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.IO;
import frc.sensors.EncoderData;
import frc.subsystem.Vision.MoveState;
import frc.util.MotorGroup;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Servo;

/**
 * Add your docs here.
 */
public class Drives extends GenericSubsystem{

    //----------------------------------------Motors/Sensors----------------------------------------

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

    private Servo rightServo;

    private Servo leftServo;

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

    private double shiftingTime;

    private boolean shifted;

    private boolean servoEnabled;

    private double wantedSpeedRight;

    private double wantedSpeedLeft;

    //----------------------------------------Constants----------------------------------------

    private final double ANGLE_OFF_BY = 2;

    private final double SPEED_PERCENTAGE = .8;

    //create a drives object
    public Drives(){
        super("Drives");
    }
    public void resetVision()
    {
        vision.reset();
    }

    //initialized all the variable in drives
    public void init(){
        rightMtr1 = new WPI_TalonSRX(IO.rightDriveCIM1);
        rightMtr2 = new WPI_TalonSRX(IO.rightDriveCIM2);
        rightMtr3 = new WPI_TalonSRX(IO.rightDriveCIM3);
        leftMtr1 = new WPI_TalonSRX(IO.leftDriveCIM1);
        leftMtr2 = new WPI_TalonSRX(IO.leftDriveCIM2);
        leftMtr3 = new WPI_TalonSRX(IO.leftDriveCIM3);
        rightMtrs = new MotorGroup(rightMtr1, rightMtr2, rightMtr3);
        leftMtrs = new MotorGroup(leftMtr1, leftMtr2, leftMtr3);
        rightEnc = new Encoder(IO.rightDrivesEncoderChannel1, IO.rightDrivesEncoderChannel2);
        leftEnc = new Encoder(IO.leftDrivesEncoderChannel1, IO.leftDrivesEncoderChannel2);
        rightEnc.setDistancePerPulse(-0.04837204);
        leftEnc.setDistancePerPulse(0.04837204);
        rightMtrs.setInverted(true);
        gyro = new AHRS(SerialPort.Port.kUSB);
        gyro.reset();
        rightServo = new Servo(0);
        leftServo = new Servo(1);
        lastAngle = 0;
        speedLeft = 0;
        speedRight = 0;
        resetGyroAngle();
        moveDist = 0;
        moveSpeed = 0; 
        turnAngle = 0;
        turnSpeed = 0;
      //  hatchPTO = new Solenoid(0)
        vision = new Vision();
        state = state.STANDBY;
        shiftingTime = 0;
        shifted = false;
        servoEnabled = false;
        wantedSpeedRight = 0;
        wantedSpeedLeft = 0;
    }

    public enum DriveState{
        STANDBY,
        TELEOP,
        MOVE_FORWARD,
        MOVE_BACKWARD,
        TURN_RIGHT,
        TURN_LEFT,
        SHIFT_LOW,
        SHIFT_HIGH,
        SHIFT_NEUTRAL,
        ARMS,
        FINDING_LINE,
        LINE_FOLLOWER;
    }

    //does all the code for drives
    public void execute(){
        //move(0.8, 150);
        
        //changeState(DriveState.LINE_FOLLOWER);
        switch(state){
            case STANDBY:
                //System.out.println("You are a bold one");
                break;
            case TELEOP:
                //System.out.println("Drives SpeedRight: " + speedRight + " speedLeft: " + speedLeft);
                rightMtrs.set(speedRight);
                leftMtrs.set(speedLeft);
                break;
            case MOVE_FORWARD:
                System.out.println("Hello");
                if(getDistance() > moveDist){
                    System.out.println("There");
                    rightMtrs.stopMotors();
                    leftMtrs.stopMotors();
                    changeState(DriveState.STANDBY);
                }else{
                    wantedSpeedRight = moveSpeed;
                    wantedSpeedLeft = moveSpeed;
                    straightenForward();
                    rightMtrs.set(wantedSpeedRight);
                    leftMtrs.set(wantedSpeedLeft);
                }
                break;
            case MOVE_BACKWARD:
                System.out.println("General");
                if(getDistance() > moveDist){
                    System.out.println("Kenobi");
                    rightMtrs.stopMotors();
                    leftMtrs.stopMotors();
                    changeState(DriveState.STANDBY);
                }else{
                    wantedSpeedRight = moveSpeed;
                    wantedSpeedLeft = moveSpeed;
                    straightenForward();
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
                    rightMtrs.set(-turnSpeed);
                    leftMtrs.set(turnSpeed);
                }
                // System.out.println("turn right");
                break;
            case TURN_LEFT:
                if(getAngle() < turnAngle){
                    rightMtrs.stopMotors();
                    leftMtrs.stopMotors();
                    changeState(DriveState.STANDBY);
                }else{
                    rightMtrs.set(turnSpeed);
                    leftMtrs.set(-turnSpeed);
                }
               // System.out.println("turn left");
                break;
            case LINE_FOLLOWER: 
            
                MoveState st = vision.getDirection();
                // System.out.println("Left motor power = " + leftMtr1.getBusVoltage());
                //System.out.println("right motor power = " + rightMtr1.getBusVoltage());
                if(st == MoveState.LEFT){
                    leftMtrs.set(-0.5);
                    rightMtrs.set(0.5);
                }else if(st == MoveState.RIGHT){
                    leftMtrs.set(0.5);
                    rightMtrs.set(-0.5);
                }else if(st == MoveState.FORWARD){
                    leftMtrs.set(0.3);
                    rightMtrs.set(0.3);
                }else if(st == MoveState.SLOWLEFT){
                    leftMtrs.set(-0.2);
                    rightMtrs.set(0.2);
                }else if(st == MoveState.SLOWRIGHT){
                    leftMtrs.set(0.2);
                    rightMtrs.set(-0.2);
                }
                else if(st == MoveState.STANDBY)
                {
                    leftMtrs.set(0);
                    rightMtrs.set(0);
                }
                else if(st == MoveState.FORWARD)
                {
                    leftMtrs.set(.3);
                    rightMtrs.set(.3);
                }
                else if(st == MoveState.BACKWARD)
                {
                    leftMtrs.set(-.3);
                    rightMtrs.set(-.3);
                }
                
                break;
            case SHIFT_LOW:
                if(shiftingTime + 400 < System.currentTimeMillis()){
                    leftMtrs.set(speedLeft);
                    rightMtrs.set(speedLeft);
                    changeState(DriveState.TELEOP);
                }
                break;
            case SHIFT_NEUTRAL:
                leftMtrs.set(0.2);
                rightMtrs.set(0.2);
                //SOOLEEEBOI
                if(shiftingTime + 200 < System.currentTimeMillis()){
                    if(!servoEnabled){
                        enableServo();
                    }
                    if(!shifted){
                        changeState(DriveState.SHIFT_LOW);
                    }
                    if(shifted){
                        changeState(DriveState.SHIFT_HIGH);
                    }
                }
                break;
            case SHIFT_HIGH:
                if(shiftingTime + 400 < System.currentTimeMillis()){
                    leftMtrs.set(speedLeft);
                    rightMtrs.set(speedRight);
                    changeState(DriveState.TELEOP);
                }
                break;
            case ARMS:
                break;
            case FINDING_LINE: 
            /*
                vision.getDirection();
                if(vision.triggered()){
                    changeState(DriveState.LINE_FOLLOWER);
                }else{
                    rightMtrs.set(0.35);
                    leftMtrs.set(0.35);
                }
                break;
                */
                
                
                
        }
      //  System.out.println("State: " + )
        // System.out.println("Right Encoder: " + rightEnc.getdista;
        // System.out.println("Left Encoder: " + leftEnc.getRaw());
         //System.out.println("Gyro: " + getAngle());
        System.out.println("GetDistance: " + getDistance());
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
        if(moveDist > 0){
            changeState(DriveState.MOVE_FORWARD);
        }else{
            changeState(DriveState.MOVE_BACKWARD);
        }
    }

    public void joystickLeft(double speed) {
        speedLeft = speed;
     // leftMtrs.set(speed);
    }

    public void joystickRight(double speed) {
        speedRight = speed;
     //   rightMtrs.set(speed);
    }

    private void lowShift(){
        shifted = true;
        shiftingTime = System.currentTimeMillis();
        changeState(DriveState.SHIFT_NEUTRAL);
    }

    private void highShift(){
        shifted = false;
        shiftingTime = System.currentTimeMillis();
        changeState(DriveState.SHIFT_NEUTRAL);
    }

    private void enableServo(){
        rightServo.set(1);
        leftServo.set(1);
        servoEnabled = true;
    }

    //straightens the robot
    private void straightenForward(){
        if(getAngle() > ANGLE_OFF_BY){
            wantedSpeedLeft *= SPEED_PERCENTAGE; 
        }else if(getAngle() < ANGLE_OFF_BY){
            wantedSpeedRight *= -SPEED_PERCENTAGE;
        }
    }

    //gets the distance the robot has travelled since the last time the encoders were reset
    private double getDistance() {
		return (rightEnc.getDistance() + leftEnc.getDistance())/2;
    }

    //gets the angle the robot has turned since the last time the gyro was reset 
    private double getAngle(){
        return gyro.getAngle() - lastAngle;
    }

    //resets the gyro's angle so the robot turns to the angle from where the robot is currently facing
    private void resetGyroAngle(){
        lastAngle = gyro.getAngle();
        System.out.println("Reset: " + lastAngle);
    }

    //turns the robot a specified angle 
    public void turn(double speed, double angle){
        turnAngle = angle;
        turnSpeed = speed;
        resetGyroAngle();
        if(angle > 0){
            changeState(DriveState.TURN_RIGHT);
        }else{
            changeState(DriveState.TURN_LEFT);
        }

    }

    //changes the state of the robot to what is given as a parameter
    private void changeState(DriveState st){
        state = st;
    }

    //used by RobotSystem to put the robot in the teleop state
    public void toTeleop(){
       // changeState(DriveState.TELEOP);
       //turn(0.5, 90);
       move(0.5, 120);
    }

    public void findLine(){
        vision.reset();
        changeState(DriveState.FINDING_LINE);
    }

}
