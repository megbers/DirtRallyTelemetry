import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import controlP5.*;
import hypermedia.net.*;
import processing.serial.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class DiRT_Telemetry_noarduino_pde extends PApplet {





//import cc.arduino.*; //removed

    Serial port;

    ControlP5 cp5;
    UDP udpRX;
    PImage img;
    String ip="127.0.0.1";
    int portRX=20777;
    //float gForceX = 0.0;
    float gForceX = 0.0f;
    float gForceY = 0.0f;
    float steeringOutput = 0.0f;
    int pos;

    float fdiff = 0.0f;
    float cdiff = 0.0f;
    float rdiff = 0.0f;
    float maxSpd = 0.0f;
    float avgSpd = 0.0f;


    public void settings() {
        size(1920, 200);
    }

    public void setup(){
        size(1920,200);
        smooth();
        background(0);

        img=loadImage("C:\\Users\\matt_\\Development\\java\\DirtRallyTelemetry\\Reference\\Zeb\\src\\main\\resources\\data\\background1080_200revb.png");
        // Arduino connection
//  port = new Serial(this, "COM3", 9600);  // Your offset may vary

        // Create some dials and gauges on screen


        cp5 = new ControlP5(this);
        // change the default font to Verdana
        PFont p = createFont("Georgia",14, true);
        PFont p1 = createFont("arial",24, true);
        PFont p2 = createFont("arial",96, true);
        //cp5.setControlFont(p);
        cp5.setFont(p);
        // change the original colors
        cp5.setColorForeground(0xffd0eff2);
        cp5.setColorBackground(0xff2e2e2e);
        //cp5.setColorLabel(0xffebf8ff);  //0a0a0a will hide it
        //cp5.setColorValue(0xffebf8ff);
        cp5.setColorValueLabel(0xffebf8ff);
        cp5.setColorActive(0xff12acdb);

        cp5.addSlider("rpm")
                .setSize(25,150)
                .setPosition(1033,14)
                .setValue(0)
                .setDecimalPrecision(0)
                .setRange(0,10000);
        cp5.getController("rpm").getCaptionLabel().setVisible(false);


        cp5.addSlider("brakes")
                .setSize(25,150)
                .setPosition(800,14)
                .setValue(0)
                .setRange(0,1);
        cp5.getController("brakes").getValueLabel().setVisible(false);
        cp5.getController("brakes").getCaptionLabel().setVisible(false);

        cp5.addSlider("clutch")
                .setSize(25,150)
                .setPosition(875,14)
                .setValue(0)
                .setDecimalPrecision(1)
                .setRange(0,1);
        cp5.getController("clutch").getValueLabel().setVisible(false);
        cp5.getController("clutch").getCaptionLabel().setVisible(false);


        cp5.addSlider("throttle")
                .setSize(25,150)
                .setPosition(949,14)
                .setValue(0)
                .setDecimalPrecision(1)
                .setRange(0,1);
        cp5.getController("throttle").getValueLabel().setVisible(false);
        cp5.getController("throttle").getCaptionLabel().setVisible(false);

        cp5.addNumberbox("gear")
                .setSize(0,0)
                .setDecimalPrecision(0)
                .setPosition(575,110)
                .setValue(0);
        cp5.getController("gear").getCaptionLabel().setVisible(false);
        cp5.getController("gear").getValueLabel().setFont(p2);

        cp5.addNumberbox("lapTime")
                .setSize(75,25)
                .setDecimalPrecision(3)
                .setPosition(78,23)
                .setValue(0);
        cp5.getController("lapTime").getCaptionLabel().setVisible(false);

        cp5.addNumberbox("distance")
                .setSize(75,25)
                .setDecimalPrecision(1)
                .setPosition(146,57)
                .setValue(0);
        cp5.getController("distance").getCaptionLabel().setVisible(false);

        cp5.addNumberbox("posx")
                .setSize(75,25)
                .setDecimalPrecision(3)
                .setDecimalPrecision(1)
                .setPosition(78,120)
                .setValue(0);

        cp5.addNumberbox("posy")
                .setSize(75,25)
                .setDecimalPrecision(3)
                .setDecimalPrecision(1)
                .setPosition(78,150)
                .setValue(0);
        cp5.getController("posy").getCaptionLabel().setVisible(false);

        cp5.addKnob("speed")
                .setRadius(60)
                .setPosition(285,40)
                .setDecimalPrecision(0)
                .setValue(0)
                .setRange(0,280);
        cp5.getController("speed").getCaptionLabel().setVisible(false);
        cp5.getController("speed").getValueLabel().setFont(p1);

//Steering wheel
//cp5.addKnob("steering")
//               .setRange(-1,1)
//               .setValue(0)
//               .setPosition(1181,30)
//               .setRadius(70)
//               .setViewStyle(Knob.ELLIPSE)
//               .setNumberOfTickMarks(10)
//               .setTickMarkLength(4)
//               .snapToTickMarks(false)
//               .setColorForeground(color(255,255,240))
//               .setColorBackground(color(0,15,255,0))
//               .setColorActive(color(255,255,240))
//               .setDragDirection(Knob.HORIZONTAL)
//               ;
//        cp5.getController("steering").getCaptionLabel().setVisible(false);
//        cp5.getController("steering").getValueLabel().setVisible(false);

        cp5.addSlider("suspFL")
                .setSize(15,50)
                .setPosition(1460,29)
                .setValue(0)
                .setRange(-3,40);
        cp5.getController("suspFL").getValueLabel().setVisible(false);
        cp5.getController("suspFL").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspFR")
                .setSize(15,50)
                .setPosition(1545,29)
                .setValue(0)
                .setRange(-3,40);
        cp5.getController("suspFR").getValueLabel().setVisible(false);
        cp5.getController("suspFR").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspAL")
                .setSize(15,50)
                .setPosition(1455,125)
                .setValue(0)
                .setRange(-3,40);
        cp5.getController("suspAL").getValueLabel().setVisible(false);
        cp5.getController("suspAL").getCaptionLabel().setVisible(false);

        cp5.addSlider("suspAR")
                .setSize(15,50)
                .setPosition(1552,125)
                .setValue(0)
                .setRange(-3,40);
        cp5.getController("suspAR").getValueLabel().setVisible(false);
        cp5.getController("suspAR").getCaptionLabel().setVisible(false);

        cp5.addNumberbox("maxSpd")
                .setSize(60,25)
                .setDecimalPrecision(3)
                .setDecimalPrecision(1)
                .setPosition(1848,87)
                .setValue(0);
        cp5.getController("maxSpd").getCaptionLabel().setVisible(false);

        cp5.addNumberbox("avgSpd")
                .setSize(60,25)
                .setDecimalPrecision(3)
                .setDecimalPrecision(1)
                .setPosition(1848,125)
                .setValue(0);
        cp5.getController("avgSpd").getCaptionLabel().setVisible(false);

        // Create new object for receiving
        udpRX=new UDP(this,portRX,ip);
        udpRX.log(false);
        udpRX.listen(true);
    }




    public void receive(byte[] data, String ip, int portRX){

        // Function to output all the game data received
        // fullOutput(data);

        // Time elapsed since game start
        //
        //Bitwise & ([pos] & 0xff), Takes byte and multiplies it by 00000000 00000000 11111111, so that the only thing remaining are the last 8 bits.
        //Float.intBitsToFloat converts binary to a float integer and makes it equal to the stated vars.
        pos = 0;
        float tTime = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));

        // Lap time
        pos = 4;
        float lapTime = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));

        // Distance
        //
        pos = 8;
        float distance = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));

        //Pos X
        pos = 16;
        float posx = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));

        //Pos Y
        pos = 20;
        float posy = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));


        // Speed, *3.6 for Km/h
        //  float speed = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24))*3.6;
        pos = 28;
        float speed = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24))*3.6f;

//Suspension travel aft left
        pos = 68;
        float suspAL = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));
//Suspension travel aft right
        pos = 72;
        float suspAR = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));
//Suspension travel fwd left
        pos = 76;
        float suspFL = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));
//Suspension travel fwd right
        pos = 80;
        float suspFR = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));

//Wheel speed aft left
        pos = 100;
        float wspAL = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));
//Wheel speed aft right
        pos = 104;
        float wspAR = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));
//Wheel speed fwd left
        pos = 108;
        float wspFL = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));
//Wheel speed fwd right
        pos = 112;
        float wspFR = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));

        //Throttle 01
        pos = 116;
        float throttle = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));

        //steering
        pos = 120;
        float steering = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));


        //Brakes 0-1
        pos = 124;
        float brakes = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));

        //Clutch 0-1
        pos = 128;
        float clutch = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));

        // Gear, neutral = 0
        pos = 132;
        float gear = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));

        // gForceX
        pos = 136;
        float gForce_X = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));

// gForceY
        pos = 140;
        float gForce_Y = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));

        // Current lap, starts at 0
        pos = 144;
        float cLap = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24));

        // RPM, requires *10 for realistic values
        pos = 148;
        float rpm = Float.intBitsToFloat((data[pos] & 0xff) | ((data[pos+1] & 0xff) << 8) | ((data[pos+2] & 0xff) << 16) | ((data[pos+3] & 0xff) << 24))*10;

        // Debug the received values
        gameDataOutput(tTime, lapTime, speed, gear, cLap, rpm, gForce_X);

        // Output the values to the dashboard
        cp5.getController("lapTime").setValue(lapTime);
        cp5.getController("distance").setValue(distance);
        cp5.getController("posx").setValue(posx);
        cp5.getController("posy").setValue(posy);
        cp5.getController("brakes").setValue(brakes);
//  cp5.getController("steering").setValue(steering);
        cp5.getController("clutch").setValue(clutch);
        cp5.getController("throttle").setValue(throttle);
        cp5.getController("rpm").setValue(rpm);
        cp5.getController("gear").setValue(gear);
        cp5.getController("speed").setValue(speed);
        cp5.getController("suspFL").setValue(suspFL);
        cp5.getController("suspFR").setValue(suspFR);
        cp5.getController("suspAL").setValue(suspAL);
        cp5.getController("suspAR").setValue(suspAR);

        //GForce Processing
        gForceX = gForce_X * 10;
        constrain(gForceX, -20, 20);
        gForceY = gForce_Y * 10;
        constrain(gForceY, -20, 20);

//Steering Processing
        steeringOutput = steering*70;

//Differential math
        fdiff = (wspFL*-1) + wspFR;
        rdiff = (wspAL*-1) + wspAR;
        cdiff = ((wspFL + wspFR)*-1) + (wspAL + wspAR);

//Stats
//Max Speed
        if(speed > maxSpd) {
            maxSpd = speed;
        }
        cp5.getController("maxSpd").setValue(maxSpd);

//  Avg Spd
        avgSpd = distance/lapTime;
        cp5.getController("avgSpd").setValue(avgSpd);
        //Send data to arduino
//String datar = int(rpm)+","+int(gear)+"\n";
//print(datar);
//port.write(datar);
        // Send the speed to the Servo
        //arduinoPos = (int)map(speed, 0, 350, 1, 180); // Note that I've set the max speed to 350, you might have to change this for other games
        //arduino.servoWrite(9, 180-arduinoPos);
    }

    public void gameDataOutput(float tTime, float lapTime, float speed, float gear, float cLap, float rpm, float wspFL){
        println("Total time: " + tTime);
        println("Lap time: " + lapTime);
        println("Speed: " + speed);
        println("Gear: " + gear);
        println("Current lap: " + cLap);
        println("RPM: " + rpm);
        println("SuspFL: " + wspFL);
    }





    // Function that outputs all the received game data
    public void fullOutput(byte[] data){

        // Loop all the received bytes
        for(int i=0; i <= data.length-1; i++){

            // Values consist of 4 bytes
            if(i % 4 == 0){

                // Combine 4 bytes to the value
                float val = Float.intBitsToFloat((data[i] & 0xff) | ((data[i+1] & 0xff) << 8) | ((data[i+2] & 0xff) << 16) | ((data[i+3] & 0xff) << 24));

                // Output the 'raw' value
                println("Value received at position " + i + " = " + val);

            }
        }
    }
    public void draw(){
        background(0);
        image(img,0,0);
//GForces
        ellipseMode(CENTER);
        stroke(0);
        fill(255);
        ellipse(gForceX + 1251,gForceY + 100,16,16);
//Steering
        ellipseMode(CENTER);
        stroke(0);
        fill(255);
        ellipse(steeringOutput + 1251,20,16,16);
//Front Differential draw
        rectMode(CENTER);
        stroke(0);
        fill(255);
        rect(1511+fdiff,52,10,30);
        rect(1511,100+cdiff,30,10);
        rect(1511+rdiff,151,10,30);
    }

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "DiRT_Telemetry_noarduino_pde" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}
