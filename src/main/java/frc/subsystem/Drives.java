/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Encoder;
import frc.robot.IO;
import frc.sensors.EncoderData;
import frc.util.MotorGroup;

import com.kauailabs.navx.frc.AHRS;

/**
 * Add your docs here.
 */
public class Drives extends GenericSubsystem{

    private WPI_TalonSRX rightMtr1;

    private WPI_TalonSRX rightMtr2;

    private WPI_TalonSRX leftMtr1;

    private WPI_TalonSRX leftMtr2;

    private Encoder rawRight;

    private Encoder rawLeft;

    private EncoderData rightEncoder; 

    private EncoderData leftEncoder;

   //private AHRS gyro;

    private MotorGroup rightMtrs;

    private MotorGroup leftMtrs;

    public Drives(){
        super("Drives");
    }

    public void init(){
        rightMtr1 = new WPI_TalonSRX(IO.rightDriveCIM1);
        rightMtr2 = new WPI_TalonSRX(IO.rightDriveCIM2);
        leftMtr1 = new WPI_TalonSRX(IO.leftDriveCIM1);
        leftMtr2 = new WPI_TalonSRX(IO.leftDriveCIM2);
        rightMtrs = new MotorGroup(rightMtr1, rightMtr2);
        leftMtrs = new MotorGroup(leftMtr1, leftMtr2);
        rawRight = new Encoder(IO.rightDrivesEncoderChannel1, IO.rightDrivesEncoderChannel2);
        rawLeft = new Encoder(IO.leftDrivesEncoderChannel1, IO.leftDrivesEncoderChannel2);
        rightEncoder = new EncoderData(rawRight, 0.033860431);
        leftEncoder = new EncoderData(rawLeft, -0.033860431);
        rightMtrs.setInverted(true);
    }

    public void execute(){
        leftMtrs.set(0.2);
        rightMtrs.set(0.2);
        System.out.println("Right Encoder: " + rightEncoder.getDistance());
        System.out.println("Left Encoder: " + leftEncoder.getDistance());
    }

    public void debug(){

    }

    public boolean isDone(){
        return false;
    }

    public long sleepTime(){
        return 20;
    }





}
