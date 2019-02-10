
/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.subsystem;

import java.util.OptionalInt;

import javax.lang.model.util.ElementScanner6;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.IO;

/**
 * Add your docs here.
 */
public class Vision{

    private directions direction;

    private DigitalInput leftIR;
    
    private DigitalInput centerLeftIR;
    
    private DigitalInput centerRightIR;

    private DigitalInput rightIR;

    int firstHit;

    private Drives drives;

   private boolean lir, rir, cir, hitLine, cirHit;
    public Vision()
    {
        leftIR = new DigitalInput(IO.leftFollowingSensor);
        centerLeftIR = new DigitalInput(IO.centerLeftFollowingSensor);
        centerRightIR = new DigitalInput(IO.centerRightFollowingSensor);
        rightIR = new DigitalInput(IO.rightFollowingSensor);
        direction  = directions.STANDBY;
    }

    public void reset()
    {
        hitLine = false;
        System.out.println("Vision reset");
        cirHit = false;
    }

    public enum directions
    {
        LEFT,
        SLIGHTLEFT,
        STANDBY,
        FORWARD,
        RIGHT,
        SLIGHTRIGHT
    }

    public directions getDirection() 
    {
        lir = !leftIR.get();
        cir = !centerLeftIR.get();
        rir = !rightIR.get();

        if(!hitLine)
            direction = directions.FORWARD;
        if(rir)
        {
            direction = directions.RIGHT;
            hitLine = true;
        }
        else if(cir)
        {
            direction = directions.SLIGHTLEFT;
            cirHit = true;
        }
        else if(!cir && cirHit)
            direction = directions.SLIGHTRIGHT;
        System.out.println("lir: " + lir + " cir: " + cir + " rir: " + rir);
        return direction;
    }

    public boolean triggered(){
        return lir || rir;
    }

}
