
// second verse same as the first
// this now has a module to allow romote control

// thi sis the adafruit skets for the trinket softserver with a change to move it past the
// top of the servo's enclosure

/*******************************************************************
SoftServo sketch for Adafruit Trinket. Turn the potentiometer knob
to set the corresponding position on the servo
(0 = zero degrees, full = 180 degrees)
Required library is the Adafruit_SoftServo library
available at https://github.com/adafruit/Adafruit_SoftServo
The standard Arduino IDE servo library will not work with 8 bit
AVR microcontrollers like Trinket and Gemma due to differences
in available timer hardware and programming. We simply refresh
by piggy-backing on the timer0 millis() counter
Required hardware includes an Adafruit Trinket microcontroller
a servo motor, and a potentiometer (nominally 1Kohm to 100Kohm
As written, this is specifically for the Trinket although it should
be Gemma or other boards (Arduino Uno, etc.) with proper pin mappings
Trinket: USB+ Gnd Pin #0 Pin #2 A1
Connection: Servo+ - Servo1 Potentiometer wiper
*******************************************************************/

#include <Adafruit_SoftServo.h> // SoftwareServo (works on non PWM pins)

#include "duemilanove.h"
//#include "trinket_5v.h"

#define SWEEP_MIN 0

#define SWEEP_MAX 180

// create a servo object
Adafruit_SoftServo servo1; 

int pos;

void setup() 
{
  // Set up the interrupt that will refresh the servo for us automagically
  OCR0A = 0xAF; // any number is OK
  TIMSK |= _BV(OCIE0A); // Turn on the compare interrupt (below!)
  servo1.attach(SERVO1PIN); // Attach the servo to pin 0 on Trinket
//  servo1.write(0); // Tell servo to go to position per quirk
  servo1.write(90); // Tell servo to go to position per quirk
  delay(15); // Wait 15ms for the servo to reach the position
  servo1.write(180);
  delay(15); // Wait 15ms for the servo to reach the position

  // setup the open switch
  pinMode(OPEN_SWITCH, INPUT);
  // ...with a pullup
  digitalWrite(OPEN_SWITCH, HIGH);

  // setup the close switch
  pinMode(CLOSE_SWITCH, INPUT);
  // ...with a pullup
  digitalWrite(CLOSE_SWITCH, HIGH);
}

void loop()
{
  if ( digitalRead(OPEN_SWITCH) )
  {
    // the button is pressed

    pos = SWEEP_MAX;
    servo1.write(pos);
      delay(15); // Wait 15ms for the servo to reach the position

  }
  
  if( digitalRead(CLOSE_SWITCH) )
  {
    pos = SWEEP_MIN;
    servo1.write(pos);
      delay(15); // Wait 15ms for the servo to reach the position

  }
}

volatile uint8_t counter = 0;

// We'll take advantage of the built in millis() timer that goes off
// to keep track of time, and refresh the servo every 20 milliseconds
// The SIGNAL(TIMER0_COMPA_vect) function is the interrupt that will be
// Called by the microcontroller every 2 milliseconds
SIGNAL(TIMER0_COMPA_vect) 
{
  // this gets called every 2 milliseconds
  counter += 2;
  
  // every 20 milliseconds, refresh the servos!
  if (counter >= 20) 
  {
    counter = 0;
    servo1.refresh();
  }
}

