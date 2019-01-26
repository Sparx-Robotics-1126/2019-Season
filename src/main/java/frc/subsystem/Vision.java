
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

/**
 * Add your docs here.
 */
public class Vision{

    private directions direction;

    private DigitalInput leftIR;
    
    private DigitalInput centerIR;
    
    private DigitalInput rightIR;

    int firstHit;

    private Drives drives;

   private boolean lir, rir, cir, hitLine, cirHit;
    public Vision()
    {
        leftIR = new DigitalInput(0);
        centerIR = new DigitalInput(1);
        rightIR = new DigitalInput(2);
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
        cir = !centerIR.get();
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
            /*
            System.out.println("over: " + over + "rightHit: " + rightHit + "leftHit: " + leftHit);
        if(rir)
            if(over)
                direction = directions.LEFT;
            else
                rightHit = true;
        else if(rightHit && !over)
            over = true;
        else 
            direction = directions.FORWARD;

        if(lir)
            if(over)
                direction = directions.RIGHT;
            else
                leftHit = true;
        else if(leftHit && !over)
         over = true;
        else 
         direction = directions.FORWARD;
        */


        /*
        if(lir && !cir)
            direction = directions.SLIGHTRIGHT;
        else if(rir && cir && centerBfrRight)
            direction = directions.RIGHT;
        else if(rir && cir && !centerBfrRight)
            direction = directions.FORWARD;  
        else if(rir && !cir)
            direction = directions.SLIGHTLEFT;
        else if(lir && cir && centerBfrLeft)
            direction = directions.LEFT;
        else if(lir && cir && !centerBfrLeft)
            direction = directions.FORWARD;
        //else if(!lir && cir && !rir)
         //   direction = directions.FORWARD;
        else 
            direction = directions.FORWARD;

        // if(firstHit == 0)
        //     direction = directions.LEFT;
        // else if(firstHit == 2)
        //     direction = directions.RIGHT;
        // else
        //     direction = directions.STANDBY;
        */
        System.out.println(direction);
        return direction;
    }

    public boolean triggered(){
        return lir || rir;
    }

}
