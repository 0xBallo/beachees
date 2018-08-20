#include "DHT.h"

// define digital pin
#define DHTPIN 2
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

void setup() {
  Serial.begin(9600);
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

void loop() {
  unsigned char i;
  delay(5000);

  // Reading temperature and humidity. Temperature as Celsius (the default)
  float h = dht.readHumidity();
  float t = dht.readTemperature();


  // Check if any reads failed and exit early (to try again).
  if (isnan(h) || isnan(t)) {
    Serial.println("Failed to read from DHT sensor!");
    return;
  }

  // Check if temperature is greater than 31°C
  if(t > 31.0){
    digitalWrite(TEMPLED, HIGH);
    //digitalWrite(ALARM, HIGH);
    for(i=0;i<240;i++){
      digitalWrite(ALARM,HIGH);
      delay(2);//wait for 1ms
      digitalWrite(ALARM,LOW);
      delay(2);//wait for 1ms
    }
  }else{
    digitalWrite(TEMPLED, LOW);
  }

  //Check if humidity is greater than 74%
  if(h > 74.0){
      digitalWrite(HUMLED, HIGH);
  }else{
    digitalWrite(HUMLED, LOW);
  }


  //Check UV
  //Check water temperature
  //Check water turbidity
  //Check water rough

  //Debug
  Serial.print(TYPE_DHT);
  Serial.print("_");
  Serial.print(h);
  Serial.print("_");
  Serial.print(t);

  Serial.println();
}
