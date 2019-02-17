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

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
/**
 * Add your docs here.
 */
public class Arms {

    final double wantedDegree = 17; 
    final double wantedSpeed = 1; //gav
    double armOffset;
    double actualDegree = 0;
    double leftMtrSpeed;
    double rightMtrSpeed;
    double wantedRightMtrPwr;
    double wantedLeftMtrPwr;
    private MotorGroup rightMtrs;
    private MotorGroup leftMtrs;
    private WPI_TalonSRX leftArmMtr;
    private WPI_TalonSRX rightArmMtr;
    private Encoder leftArmEnc;
    private Encoder rightArmEnc;
    

    public Arms(MotorGroup rightMtrs, MotorGroup leftMtrs, Encoder rightEnc, Encoder leftEnc){
        this.rightMtrs = rightMtrs;
        this.leftMtrs = leftMtrs;
        rightArmEnc = rightEnc;
        leftArmEnc = leftEnc;
    }

    public void armsDown(){
        if(wantedDegree > actualDegree){
            leftMtrSpeed = -leftArmEnc.getRate();
            rightMtrSpeed = -rightArmEnc.getRate();
          

            if(leftMtrSpeed < wantedSpeed){
                wantedLeftMtrPwr += 0.01; //gav
            }else if(leftMtrSpeed > wantedSpeed){
                wantedLeftMtrPwr -= 0.01; //gav
            }
            wantedLeftMtrPwr = wantedLeftMtrPwr > 1 ? 1 : wantedLeftMtrPwr;

            if(rightMtrSpeed < wantedSpeed){
                wantedRightMtrPwr += 0.01; //gav
            }else if(rightMtrSpeed > wantedSpeed){
                wantedRightMtrPwr -= 0.01; //gav
            }
            wantedRightMtrPwr = wantedRightMtrPwr > 1 ? 1 : wantedRightMtrPwr;
            // if(armOffset > 2.0){ //gav
            //     if(rightArmEnc.getDistance() > leftArmEnc.getDistance()){
            //         wantedRightMtrPwr = 0.0;
            //     }else{ //if leftSpeed is faster than rightSpeed
            //         wantedLeftMtrPwr = 0.0;
            //     }
            // }
            actualDegree = (-leftArmEnc.getDistance() + rightArmEnc.getDistance()) / 2;
        }
        rightMtrs.set(wantedRightMtrPwr);
        leftMtrs.set(wantedLeftMtrPwr);

        
       // System.out.println("right power: " + wantedRightMtrPwr  + " right distance: " + rightArmEnc.getDistance() + " right speed " + rightArmEnc.getRate());
       // System.out.println("left power: " + wantedLeftMtrPwr  + " left distance: " + leftArmEnc.getDistance() + " left speed " + leftArmEnc.getRate());
    }

    public void armMtrs(){
        leftArmMtr.set(0.5);
        rightArmMtr.set(0.5);
    }

}
