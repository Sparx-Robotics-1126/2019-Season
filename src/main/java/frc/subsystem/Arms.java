/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Encoder;
import frc.util.MotorGroup;
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
    private boolean isDone;

    public Arms(MotorGroup rightMtrs, MotorGroup leftMtrs, Encoder rightEnc, Encoder leftEnc){
        this.rightMtrs = rightMtrs;
        this.leftMtrs = leftMtrs;
        rightArmEnc = rightEnc;
        leftArmEnc = leftEnc;
    }

    public void armsDown(){
        if(wantedDegree > actualDegree){
        	isDone = false;
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
            rightMtrs.set(wantedRightMtrPwr);
            leftMtrs.set(wantedLeftMtrPwr);
        } else {
        	 rightMtrs.set(0);
             leftMtrs.set(0);
             isDone = true;
        }
    }
    
    public boolean isDone() {
    	return isDone;
    }

    public void armMtrs(){
        leftArmMtr.set(0.5);
        rightArmMtr.set(0.5);
    }

}
