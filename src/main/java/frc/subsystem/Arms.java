/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import edu.wpi.first.wpilibj.Encoder;
import frc.robot.IO;
import frc.util.MotorGroup;

/**
 * Add your docs here.
 */
public class Arms {

    final double wantedDegree = 1000; 
    final double wantedSpeed = 5; //gav
    double armOffset;
    double actualDegree = 0;
    double leftSetMtrSpeed;
    double rightSetMtrSpeed;
    double wantedRightMtrPwr;
    double wantedLeftMtrPwr;
    private MotorGroup rightMtrs;
    private MotorGroup leftMtrs;
    private Encoder leftArmEnc;
    private Encoder rightArmEnc;


    public Arms(MotorGroup rightMtrs, MotorGroup leftMtrs){
        this.rightMtrs = rightMtrs;
        this.leftMtrs = leftMtrs;
        rightArmEnc = new Encoder(IO.rightDrivesEncoderChannel1, IO.rightDrivesEncoderChannel2);
        leftArmEnc = new Encoder(IO.leftDrivesEncoderChannel2, IO.leftDrivesEncoderChannel1);
        rightArmEnc.setDistancePerPulse(0.033860431);
        leftArmEnc.setDistancePerPulse(0.033860431);
    }

    public void armsDown(){
        if(wantedDegree > actualDegree){
            leftSetMtrSpeed = leftArmEnc.getRate();
            rightSetMtrSpeed = rightArmEnc.getRate();
            armOffset = Math.abs(rightArmEnc.getDistance() - leftArmEnc.getDistance());

            if(leftSetMtrSpeed < wantedSpeed){
                wantedLeftMtrPwr += 0.05; //gav
            }else if(leftSetMtrSpeed > wantedSpeed){
                wantedLeftMtrPwr -= 0.05; //gav
            }

            if(rightSetMtrSpeed < wantedSpeed){
                wantedRightMtrPwr += 0.05; //gav
            }else if(rightSetMtrSpeed > wantedSpeed){
                wantedRightMtrPwr -= 0.05; //gav
            }

            // if(armOffset > 2.0){ //gav
            //     if(rightArmEnc.getDistance() > leftArmEnc.getDistance()){
            //         wantedRightMtrPwr = 0.0;
            //     }else{ //if leftSpeed is faster than rightSpeed
            //         wantedLeftMtrPwr = 0.0;
            //     }
            // }
        }
        rightMtrs.set(wantedRightMtrPwr);
        leftMtrs.set(wantedLeftMtrPwr);
        System.out.println("right power: " + wantedRightMtrPwr  + " right distance: " + rightArmEnc.getDistance() + " right speed" + rightArmEnc.getRate());
        System.out.println("left power: " + wantedLeftMtrPwr  + " left distance: " + leftArmEnc.getDistance() + " left speed" + leftArmEnc.getRate());
    }
}
