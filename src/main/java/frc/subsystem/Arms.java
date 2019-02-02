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

    final double wantedDegree = 0.0; 
    final double wantedSpeed = 10; //gav
    double armOffset;
    double actualDegree = 0;
    double leftSetMtrSpeed;
    double rightSetMtrSpeed;
    private MotorGroup rightMtrs;
    private MotorGroup leftMtrs;
    private Encoder leftArmEnc;
    private Encoder rightArmEnc;


    public Arms(MotorGroup rightMtrs, MotorGroup leftMtrs){
        this.rightMtrs = rightMtrs;
        this.leftMtrs = leftMtrs;
        rightArmEnc = new Encoder(IO.rightDrivesEncoderChannel1, IO.rightDrivesEncoderChannel2);
        leftArmEnc = new Encoder(IO.leftDrivesEncoderChannel1, IO.leftDrivesEncoderChannel2);
        rightArmEnc.setDistancePerPulse(0.033860431);
        leftArmEnc.setDistancePerPulse(0.033860431);
    }

    public void armsDown(){
        while(wantedDegree < actualDegree){
            leftSetMtrSpeed = leftArmEnc.getRate();
            rightSetMtrSpeed = rightArmEnc.getRate();
            armOffset = Math.abs(rightArmEnc.getDistance() - leftArmEnc.getDistance());

            if(leftSetMtrSpeed < wantedSpeed){
                leftSetMtrSpeed += 0.05; //gav
            }else if(leftSetMtrSpeed > wantedSpeed){
                leftSetMtrSpeed -= 0.05; //gav
            }

            if(rightSetMtrSpeed < wantedSpeed){
                rightSetMtrSpeed += 0.05; //gav
            }else if(rightSetMtrSpeed > wantedSpeed){
                rightSetMtrSpeed -= 0.05; //gav
            }

            if(armOffset > 2.0){ //gav
                if(rightSetMtrSpeed > leftSetMtrSpeed){
                    rightSetMtrSpeed = 0.0;
                }else{ //if leftSpeed is faster than rightSpeed
                    leftSetMtrSpeed = 0.0;
                }
            }
        }
        rightMtrs.set(rightSetMtrSpeed);
        leftMtrs.set(leftSetMtrSpeed);
    }
}
