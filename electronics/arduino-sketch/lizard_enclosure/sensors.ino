
void externalTemperatureSensor()
{
  externalCelsius = readExternalTemp();
  externalFahrenheit = externalCelsius * 9 / 5 + 32;
}

void humidityTemperatureSensor()
{
 // Reading temperature or humidity takes about 250 milliseconds!
  // Sensor readings may also be up to 2 seconds 'old' (its a very slow sensor)
  humidity = htSensor.readHumidity();
  internalTemperature = htSensor.readTemperature();
}

void humidityTemperatureSensorLcdUpdate()
{
  
}

void modeButton()
{
  if( !digitalRead(MODE_BUTTON_PIN) )
  {  
    // the button was pressed
    daytimeMode = !daytimeMode;
    Serial.print("daytime mode: ");
    Serial.println(daytimeMode); 
  }
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
      
        if(daytimeMode)
        {
          moveServo();
          moveServo();
          moveServo();
        }

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

float readExternalTemp()
{
  const int B=3975; 
  double TEMP;
  int sensorValue = analogRead(externalTemperaturePin);
  float Rsensor;
  Rsensor=(float)(1023-sensorValue)*10000/sensorValue;
  TEMP=1/(log(Rsensor/10000)/B+1/298.15)-273.15;
  
  return TEMP;
}

/**
*  Serial Input: http://stackoverflow.com/questions/11421905/java-integer-to-byte-and-byte-to-integer
*/
void serialInput()
{
  if(Serial.available())
  {
    currentMode = Serial.read() - '0';
    
    switch(currentMode)
    {
      case MODES_NIGHT_TIME:
      {
        
        
        break;
      }
      default:
      {
        // do something, sheesh!
      }
    }
  }  
}

