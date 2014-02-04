#include <math.h>
#include <SerialLCD.h>
#include <Servo.h>
#include <SoftwareSerial.h>

// digital pins
#define MODE_PIN 5

#define ON_BOARD_LED_PIN 13
#define PIR_SENSOR_PIN 7   // http://seeedstudio.com/wiki/GROVE_-_Starter_Kit_v1.1b#Grove_-_Serial_LCD

// more digital pins.  this is a must, assign soft serial pins
#define LCD_PIN_1 11
#define LCD_PIN_2 12

#define SERVO_PIN 9
#define SERVO_DELAY 300

volatile bool daytimeMode = false;

// initialize the library
SerialLCD slcd(LCD_PIN_1, LCD_PIN_2);

Servo vineServo;

int pirState = LOW;             // we start, assuming no motion detected

const int tempPin = 0; 

void loop() 
{
  modeButton();
  serialLcd();
  if(daytimeMode)
  {
    pirSensor();    
  }

}

void modeButton()
{
  if ( !digitalRead(MODE_PIN) )
  {  // if the button is pressed
      daytimeMode = !daytimeMode;

  }
  Serial.print("daytime mode: ");
  Serial.println(daytimeMode); 
}  

/**
 * This function has to attach and detach the servo each time to avoid the jitter issue seen while also using SerialSoftware.h
*/
void moveServo()
{
  vineServo.attach(SERVO_PIN);  

  vineServo.write(40); 
  delay(SERVO_DELAY);
  
  vineServo.write(0);    
  delay(SERVO_DELAY);

  vineServo.detach();  
}

void pirSensor()
{
  int val = digitalRead(PIR_SENSOR_PIN);  // read input value
  
  if (val == HIGH)             // check if the input is HIGH
  {
    digitalWrite(ON_BOARD_LED_PIN, HIGH);  // turn LED ON
    if (pirState == LOW) 
    {
      // we have just turned on
      Serial.println("motion detected!");
      
      moveServo();
      moveServo();
      
      // We only want to print on the output change, not state
      pirState = HIGH;
    }
  } 
  else 
  {
    digitalWrite(ON_BOARD_LED_PIN, LOW); // turn LED OFF
    if (pirState == HIGH)
    {
      // we have just turned of
      Serial.println("end of motion");
      // We only want to print on the output change, not state
      pirState = LOW;
    }
  }  
}

float readTemp()
{
  const int B=3975; 
  double TEMP;
  int sensorValue = analogRead( tempPin );
  float Rsensor;
  Rsensor=(float)(1023-sensorValue)*10000/sensorValue;
  TEMP=1/(log(Rsensor/10000)/B+1/298.15)-273.15;
  return TEMP;
}

void serialLcd()
{
  // set the cursor to column 0, line 1
  // (note: line 1 is the second row, since counting begins with 0):
  slcd.setCursor(0, 1);

  SLCDprintFloat( readTemp() ,1);
  slcd.print(" Celsius");
  
  delay(500);  // give the servo some time to move back to the origin
}

void setup() 
{
  Serial.begin(9600);
    
  slcd.begin();
  slcd.print("Temperature is:");
  
  pinMode(MODE_PIN, INPUT);
  digitalWrite(MODE_PIN, HIGH);
   
  pinMode(PIR_SENSOR_PIN, INPUT);
  digitalWrite(PIR_SENSOR_PIN, HIGH);  // with pullup
   
  pinMode(ON_BOARD_LED_PIN, OUTPUT);
 
  vineServo.attach(SERVO_PIN);  // attaches the servo on pin 9 to the servo object 
  vineServo.write(0);  
  delay(500);  // give the servo some time to move back to the origin  
  vineServo.detach();
}

void SLCDprintFloat(double number, uint8_t digits) 
{ 
  // Handle negative numbers
  if (number < 0.0)
  {
     slcd.print('-');
     number = -number;
  }

  // Round correctly so that slcd.print(1.999, 2) prints as "2.00"
  double rounding = 0.5;
  for (uint8_t i=0; i<digits; ++i)
    rounding /= 10.0;
  
  number += rounding;

  // Extract the integer part of the number and print it
  unsigned long int_part = (unsigned long)number;
  float remainder = number - (float)int_part;
  slcd.print(int_part , DEC); // base DEC

  // Print the decimal point, but only if there are digits beyond
  if (digits > 0)
    slcd.print("."); 

  // Extract digits from the remainder one at a time
  while (digits-- > 0)
  {
    remainder *= 10.0;
    float toPrint = float(remainder);
    slcd.print(toPrint , DEC);//base DEC
    remainder -= toPrint; 
  } 
}

