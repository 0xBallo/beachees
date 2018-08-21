#include "DHT.h"

// define digital input pins
#define DHTPIN 2

// define digital output pins
#define TEMPLED 13
#define HUMLED 12
#define UVLED 11
#define WTEMPH 10
#define WTEMPL 9
#define WTURB 8
#define WROUGH 7
#define ALARM 6

#define DHTTYPE DHT22

// define type of data
#define TYPE_DHT 0
#define TYPE_GYRO 1
#define TYPE_UVA 2
#define TYPE_WATER_T 3
#define TYPE_TURBIDITY 4

// Initialize DHT sensor
DHT dht(DHTPIN, DHTTYPE);

void setup()
{
  Serial.begin(9600);

  // Setup Output port
  pinMode(TEMPLED, OUTPUT);
  pinMode(HUMLED, OUTPUT);
  pinMode(UVLED, OUTPUT);
  pinMode(WTEMPH, OUTPUT);
  pinMode(WTEMPL, OUTPUT);
  pinMode(WTURB, OUTPUT);
  pinMode(WROUGH, OUTPUT);
  pinMode(ALARM, OUTPUT);

  dht.begin();
}

void loop()
{
  delay(5000);

  // counter for alarm
  unsigned char i;
  int freq = 0;

  // Reading temperature and humidity. Temperature as Celsius (the default)
  float h = dht.readHumidity();
  float t = dht.readTemperature();

  // Check if any reads failed and exit early (to try again).
  if (isnan(h) || isnan(t))
  {
    Serial.println("Failed to read from DHT sensor!");
    return;
  }

  // Check if wear temperature is greater than 31°C
  if (t > 31.0)
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

  // Alarm Ring if Critical levels are reached
  for (i = 0; i < 240; i++)
  {
    digitalWrite(ALARM, HIGH);
    delay(freq);
    digitalWrite(ALARM, LOW);
    delay(freq);
  }

  //TODO: Check UV
  //TODO: Check water temperature
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

  delay(100);

}
