#include <OneWire.h>
#include <Servo.h>
#include <DallasTemperature.h>
#include "DHT.h"


// define input pins
#define DHTPIN 2
#define DS18PIN 3
#define UVPIN A0
#define REF_3V3 A1
#define WTURB A5


// define digital output pins
#define TEMPLED 13
#define HUMLED 12
#define UVLED 11
#define WTEMPHLED 10
#define WTEMPLLED 9
#define WTURBLED 8
#define WROUGHLED 7
#define ALARM 6

#define DHTTYPE DHT22
#define ALARM_COUNTER 12

// define type of data
#define TYPE_DHT 0
#define TYPE_GYRO 4
#define TYPE_UVA 1
#define TYPE_WATER_T 2
#define TYPE_TURBIDITY 3

// Initialize DHT sensor
DHT dht(DHTPIN, DHTTYPE);
// Setup oneWire Bus
OneWire oneWire(DS18PIN);
// Pass our oneWire reference to Dallas Temperature.
DallasTemperature waterTemp(&oneWire);


void setup()
{
  Serial.begin(9600);

  // Setup Output port
  pinMode(TEMPLED, OUTPUT);
  pinMode(HUMLED, OUTPUT);
  pinMode(UVLED, OUTPUT);
  pinMode(WTEMPHLED, OUTPUT);
  pinMode(WTEMPLLED, OUTPUT);
  pinMode(WTURBLED, OUTPUT);
  pinMode(WROUGHLED, OUTPUT);
  pinMode(ALARM, OUTPUT);

  // Setup Input port
  pinMode(UVPIN, INPUT);
  pinMode(REF_3V3, INPUT);
  pinMode(WTURB, INPUT);

  dht.begin();
  waterTemp.begin();
}

void loop()
{
  // counter for alarm
  unsigned char i, j;
  //alarm frequency
  int freq = 0;

  // Reading water temperature in °C
  waterTemp.requestTemperatures(); // Send the command to get temperature readings
  float w_t = waterTemp.getTempCByIndex(0);

  // Reading temperature and humidity. Temperature as Celsius (the default)
  float h = dht.readHumidity();
  float t = dht.readTemperature();

  // UV Sensors Reading
  int levelUV = averageAnalogRead(UVPIN, 8);
  int refLevelUV = averageAnalogRead(REF_3V3, 8);

  // Water Turbidity reading
  int w_turb = averageAnalogRead(WTURB, 8);

  //Use the 3.3V power pin as a reference to get a very accurate output value from sensor
  float outputVoltageUV = 3.3 / refLevelUV * levelUV;
  float intensityUV = mapfloat(outputVoltageUV, 0.99, 2.9, 0.0, 15.0); //Convert the voltage to a UV intensity level


  // Check if any reads failed and exit early (to try again).
  if (isnan(h) || isnan(t))
  {
    Serial.println("Failed to read from DHT11 sensor!");
    return;
  }
  if (isnan(w_t))
  {
    Serial.println("Failed to read from DS18B20 water sensor!");
    return;
  }

  // Check if wear temperature is greater than 31°C
  if (t > 30.0)
  {
    digitalWrite(TEMPLED, HIGH);
    freq++;
  }
  else
  {
    digitalWrite(TEMPLED, LOW);
  }

  //Check if humidity is greater than 80%
  if (h > 80.0)
  {
    digitalWrite(HUMLED, HIGH);
    freq++;
  }
  else
  {
    digitalWrite(HUMLED, LOW);
  }

  //Check water temperature value
  if (w_t > 25.0)
  {
    digitalWrite(WTEMPHLED, HIGH);
    freq++;
  }
  else
  {
    digitalWrite(WTEMPHLED, LOW);
  }
  if (w_t < 15.0)
  {
    digitalWrite(WTEMPLLED, HIGH);
    freq++;
  }
  else
  {
    digitalWrite(WTEMPLLED, LOW);
  }

  //Check if UV is more than 12 mW/cm2
  /*if (intensityUV > 12.0)
    {
    digitalWrite(UVLED, HIGH);
    freq++;
    }
    else
    {
    digitalWrite(UVLED, LOW);
    }*/

  //TODO: Check water turbidity
  //TODO: Check water rough

  //INFO: DHT22 Data OUTPUT
  Serial.print(TYPE_DHT);
  Serial.print("_");
  Serial.print("PM12");
  Serial.print("_");
  Serial.print(h);
  Serial.print("_");
  Serial.print(t);
  Serial.println();
  delay(100);

  //INFO: GY-ML8511 data OUTPUT
  Serial.print(TYPE_UVA);
  Serial.print("_");
  Serial.print("PM12");
  Serial.print("_");
  Serial.print(intensityUV);
  Serial.println();
  delay(100);

  //INFO: DS18B20 water data OUTPUT
  Serial.print(TYPE_WATER_T);
  Serial.print("_");
  Serial.print("PM12");
  Serial.print("_");
  Serial.print(w_t);
  Serial.println();
  delay(100);

  //INFO: water turbidity data OUTPUT
  Serial.print(TYPE_TURBIDITY);
  Serial.print("_");
  Serial.print("PM12");
  Serial.print("_");
  Serial.print(w_turb);
  Serial.println();
  delay(100);

  for (j = 0; j < ALARM_COUNTER; j++) {
    if (freq > 0) {
      // Alarm Ring if Critical levels are reached
      for (i = 0; i < 240; i++)
      {
        digitalWrite(ALARM, HIGH);
        delay(freq);
        digitalWrite(ALARM, LOW);
        delay(freq);
      }
    }
    delay(900);
  }

}

//Takes an average of readings on a given pin
//Returns the average
int averageAnalogRead(int pinToRead, byte numberOfReadings)
{
  unsigned int runningValue = 0;

  for (int x = 0 ; x < numberOfReadings ; x++)
    runningValue += analogRead(pinToRead);
  runningValue /= numberOfReadings;

  return (runningValue);
}

//The Arduino Map function but for floats
//From: http://forum.arduino.cc/index.php?topic=3922.0
float mapfloat(float x, float in_min, float in_max, float out_min, float out_max)
{
  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}
