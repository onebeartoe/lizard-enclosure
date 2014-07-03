
// this now has a module to allow romote control to move a servo past the top of the servo's enclosure

#include <Servo.h>

#define CLOSE_SWITCH 8

#define OPEN_SWITCH 12

#define SERVO1PIN 10 // Servo control line (orange)

#define SWEEP_MIN 180

#define SWEEP_MAX 20

// create a servo object
Servo servo1; 

int pos;

void setup() 
{
  Serial.begin(9600);  
  Serial.println("Preparing to feed...");
  
  servo1.attach(SERVO1PIN); // Attach the servo to pin 0 on Trinket
  servo1.write(180);

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

    Serial.println("open requested");

    pos = SWEEP_MAX;
    servo1.write(pos);
//      delay(15); // Wait 15ms for the servo to reach the position
  }
  
  if( digitalRead(CLOSE_SWITCH) )
  {
    Serial.println("closed requested");
    
    pos = SWEEP_MIN;
    servo1.write(pos);
//      delay(15); // Wait 15ms for the servo to reach the position
  }
}
