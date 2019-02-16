/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.util;

public class Arduino {
  private edu.wpi.first.wpilibj.SerialPort arduino;

  private String out;

  private double edgeDist, midDist;

  private boolean waiting;

  public Arduino(int baudRate, edu.wpi.first.wpilibj.SerialPort.Port port, int dataBits,
      edu.wpi.first.wpilibj.SerialPort.Parity parity, edu.wpi.first.wpilibj.SerialPort.StopBits stopBits) {
    out = "";
    arduino = new edu.wpi.first.wpilibj.SerialPort(baudRate, port, dataBits, parity, stopBits);
    edgeDist = Double.MAX_VALUE;
    midDist = Double.MAX_VALUE;
  }

  private void updateDistances()
  {
    if(!waiting)
    {
      byte[] buffer = {42};
      arduino.write(buffer, 1);
      waiting = true;
    }
    else
    {
      if(arduino.getBytesReceived() >= 13)
      {
        String temp;
        temp = arduino.readString();
        if(temp.substring(0, 6).equals("XX.XXX"))
          edgeDist = Double.MAX_VALUE;
        else
          edgeDist = Double.parseDouble(temp.substring(0, 6));
        if(temp.substring(7, 13).equals("XX.XXX"))
          edgeDist = Double.MAX_VALUE;
        else
          midDist = Double.parseDouble(temp.substring(7, 13));
        waiting = false;
      }
    }
  }

  public double getEdgeDist()
  {
    updateDistances();
    double toReturn = edgeDist;
    edgeDist = -1.0;
    return toReturn;
  }

  public double getmidDist()
  {
    updateDistances();
    double toReturn = midDist;
    midDist = -1.0;
    return toReturn;
  }
  

  /*
  public double getDistance() {
    double distance = -1.0;
    String temp;
    if (arduino.getBytesReceived() > 0) {
      out += arduino.readString();
    }

    int pos = -1;
    for (int i = out.length() - 5; i >= 2; i--) {
      if (out.charAt(i) == '.') {
        pos = i;
        break;
      }
    }
    if (pos != -1) {
      out = out.substring(pos - 2);
      temp = out.substring(0, 7);
      out = out.substring(7);
      if (temp.equals("NO.NONE")) {
        System.err.println("out of range");
        distance = Double.MAX_VALUE;
      } else {
        distance = Double.parseDouble(temp);
        // System.out.println("distance = " + distance);
      }
    }
    return distance;
  }
  */
}
