
#include "DHT.h"   //  https://github.com/adafruit/DHT-sensor-library
#include <math.h>
#include <SerialLCD.h>
#include <Servo.h>
#include <SoftwareSerial.h>

// analog pins
const int externalTemperaturePin = 0;   /// use a define!

// digital pins
#define DHTPIN 2     // temperature and humidity sensor pin 
#define MODE_BUTTON_PIN 5
#define PIR_SENSOR_PIN 7   // http://seeedstudio.com/wiki/GROVE_-_Starter_Kit_v1.1b#Grove_-_Serial_LCD
#define SERVO_PIN 9
#define ON_BOARD_LED_PIN 13

// more digital pins
#define LCD_PIN_1 11   // assign soft serial pin
#define LCD_PIN_2 12   // assign soft serial pin

#define LCD_OUTPUT_MODES_MODE_DISPLAY 0
#define LCD_OUTPUT_MODES_EXTERNAL_TEMPERATURE 1
#define LCD_OUTPUT_MODES_HUMIDITY_TEMPERATURE 2

#define LCD_OUTPUT_MODES_COUNT 3

int lcdOutputMode = LCD_OUTPUT_MODES_MODE_DISPLAY;

#define DHTTYPE DHT11    // sensor type: http://www.seeedstudio.com/depot/Grove-TempHumi-Sensor-p-745.html
DHT htSensor(DHTPIN, DHTTYPE);

float humidity;
float internalCelsiusTemperature;
float internalFahrenheitTemperature;
#define SERVO_DELAY 300

#define MODES_DAY_TIME 10
#define MODES_NIGHT_TIME 11

long SERIAL_LCD_SENSOR_INTERVAL = 1000 * 10;
long previousLcdUdateMillis = 0;        // will store last time LCD was updated

int currentMode = MODES_DAY_TIME;

volatile bool daytimeMode = false;

// initialize the library
SerialLCD slcd(LCD_PIN_1, LCD_PIN_2);

Servo vineServo;

int pirState = LOW;             // on start, set to no motion detected 

double externalCelsius;
double externalFahrenheit;

// debug settings
boolean static debugInternalTempAndHumidity = false;
boolean static debugMotionSensor = false;

void loop() 
{
  serialInput();  // from Raspberry Pi
  
  modeButton();  
    
  externalTemperatureSensor();  
  pirSensor();
  humidityTemperatureSensor();  
  
  lcdOutput();
  serialOutput();  // to Raspberry Pi
}

void setup() 
{
  Serial.begin(9600);
    
  slcd.begin();
  slcd.print("Greetings");
  
  pinMode(MODE_BUTTON_PIN, INPUT);
  digitalWrite(MODE_BUTTON_PIN, HIGH);
   
  pinMode(PIR_SENSOR_PIN, INPUT);
  digitalWrite(PIR_SENSOR_PIN, HIGH);  // with pullup
   
  pinMode(ON_BOARD_LED_PIN, OUTPUT);
 
  vineServo.attach(SERVO_PIN);  // attaches the servo on pin 9 to the servo object 
  vineServo.write(0);  
  delay(1000);  // give the servo some time to move back to the origin  
  vineServo.detach();
  
  htSensor.begin();
}

