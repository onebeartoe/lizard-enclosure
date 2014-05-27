
/**
*  Show the status of the sensors, switching between them after the LCD_INTERVAL
*/
void lcdOutput()
{
  unsigned long currentMillis = millis();
 
  if(currentMillis - previousLcdUdateMillis > LCD_INTERVAL)
  {
    // save the last time the LCD was updated
    previousLcdUdateMillis = currentMillis;
    
    // switch what is being displayed
    lcdOutputMode++;
    if(lcdOutputMode == LCD_OUTPUT_MODES_COUNT)
    {
      lcdOutputMode = 0;
    }

    slcd.clear();    
    
    switch(lcdOutputMode)
    {
      case LCD_OUTPUT_MODES_MODE_DISPLAY:
      {
        break;
      }
      case LCD_OUTPUT_MODES_EXTERNAL_TEMPERATURE:
      {
        lcdOutputExternalTemperature();
        
        break;
      }
      case LCD_OUTPUT_MODES_HUMIDITY_TEMPERATURE:
      {
        humidityTemperatureSensorLcdUpdate();
        
        break;
      }
      default:
      {
        
      }
    }
  }
}

void lcdOutputExternalTemperature()
{
    slcd.setCursor(0, 0);    
    slcd.print("Extern Temper:");
    
    // set the cursor to column 0, line 1
    // (note: line 1 is the second row, since counting begins with 0):
    slcd.setCursor(0, 1);
    serialLcdPrintFloat(externalCelsius, 1);
    slcd.print(" C - ");
    
    serialLcdPrintFloat(externalFahrenheit, 1);
    slcd.print(" F");  
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

void output()
{  
  lcdOutput();
  serialOutput();  // to Raspberry Pi    
}

void serialLcdPrintFloat(double number, uint8_t digits) 
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

/**
 * This function is called to write data to the Raspberry Pi over the serial connection.
 */
void serialOutput()
{
  serialOutputHumidityTemperature();
}

void serialOutputHumidityTemperature()
{
  // check if the internal humidity and temperature are valid, if they are NaN (not a number) then something went wrong!
  if (isnan(internalCelsiusTemperature) || isnan(humidity)) 
  {
    Serial.println("Failed to read from DHT");
  } 
  else 
  {
//    if(debugInternalTempAndHumidity)
    {
      Serial.print("SENSOR_READING:INTERNAL_HUMIDITY:"); 
      Serial.println(humidity);    

      delay(1000);
  
      Serial.print("SENSOR_READING:INTERNAL_TEMPERATURE:"); 
      Serial.print(internalFahrenheitTemperature);
      Serial.println("F");
      
      delay(1000);
      
      Serial.print("SENSOR_READING:EXTERNAL_TEMPERATURE:");
      Serial.print(externalFahrenheit);
      Serial.println("F");
    }
  }  
}

