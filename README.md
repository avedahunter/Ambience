Used what was learnt from the lectures, and designed and developed a Sense&Control system for smart office/home. The idea was to develop a simple system that responses to sensor’s readings and adapts certain behavior (e.g., change LED color or blinking rate). The hardware used was a Raspberry Pi, sensors (temperature, light intensity), and LEDs. 
The sensprs were are connecting to the LEDs (actuators in real systems) to the GPIO pins on the Raspberry Pi board. Using Pi4j (written in Java), the Java program reads sensing values from and send commands to these devices.
Following the basic setup, following tasks were achieved in stages:
Developed a JSON-RPC program to answer queries about current sensor readings.
Developed a Fuzzy Logic controller for a smart blind system. The temperature setting used were 'freezing', 'cold', 'comfort', 'warm', 'hot' and ambient setting used were 'dark', 'dim', 'bright' as our inputs. The output is to be the blind’s position 'open', 'half', 'close'; the LED was used to mimic the motor, thus the color of LED was changed accordingly Red, Blue and Green.
The following fuzzy rules were used as adaptation rules in Pervasive Systems. (The jfuzzylogic library has been used: "http://jfuzzylogic.sourceforge.net" http://jfuzzylogic.sourceforge.net (examples are available from the website) to develop a fuzzy logic controller, which adapts to these four rules.)

IF temperature IS hot AND ambient IS dim THEN blind IS close;

IF temperature IS cold AND ambient IS bright THEN blind IS half;

IF temperature IS warm AND ambient IS dim THEN blind IS half;

IF temperature IS warm AND ambient IS bright THEN blind IS close;

IF ambient IS dark THEN blind IS open;

The application was developed in android.

Developed by:

Amey Edwankar, Utkarsh Bhatia and Siddharth Kalluru

pi4j website "http://pi4j.com" http://pi4j.com

pi4j example projects "https://code.google.com/p/raspberry-pi4j-samples/" https://code.google.com/p/raspberry-pi4j-samples/
