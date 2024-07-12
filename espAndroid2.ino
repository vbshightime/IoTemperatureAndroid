//#include <ESP8266WebServer.h>


#include "DHT.h"
#include <ESP8266WiFi.h>
#include <WiFiClient.h> 
#define DHTTYPE DHT11
#include <ArduinoJson.h>
#include<ESP8266WebServer.h>
 
#define DHTPIN D1         // Pin which is connected to the DHT sensor.
#define DHTTYPE DHT11

unsigned long startTime =0;
unsigned long interval =3000;
unsigned long DHTtimer= 0;
unsigned long clientTimer =0;
unsigned long jsonTimer =0;
unsigned long jsonInterval = 2000;
const char pass[] = "24041990";
const char ssid[] = "sahilvibhu" ;

float temperature;
float humidity;
String json;

int counter=0;

ESP8266WebServer server(80);

DHT dht(DHTPIN,DHTTYPE);


void setup() {
// put your setup code here, to run once:
Serial.begin(115200);

Serial.setTimeout(2000);//time to waitfor before recieving any serial data

for(uint8_t t=4; t>0; t--){
          Serial.printf("[SETUP] WAIT %d...\n", t);
        Serial.flush();
        delay(1000);
  }
  Serial.println();
  dht.begin();
  reconnectWiFi();
  server.on("/", handleRoot);
  server.on("/Feed", handleFeed);
  server.onNotFound(handleNotFound);
  server.begin();  
}

void loop() {
  // put your main code here, to run repeatedly:
if (millis() > DHTtimer + 3000) {
    DHTtimer = millis();
    temperature = dht.readTemperature();
    humidity =dht.readHumidity();
    if (isnan(temperature)&&isnan(humidity)) {
      temperature=31.00;
      humidity=85.00;
      return;
    }
  }
    server.handleClient();
    
   if (millis() - clientTimer > 30000) {    // stops and restarts the WiFi server after 30 sec
    clientTimer = millis();
    WiFi.disconnect();                     // idle time
    delay(500);
    reconnectWiFi();
  }
}

String inToJson(){
  DynamicJsonBuffer jsonBuffer;
   //StaticJsonBuffer<300> jsonBuffer;
    JsonObject& root =jsonBuffer.createObject();
    root["device"] = "NodeMcu-12E";
    root["sensorType"] = "DHT11";
    JsonArray& temp = root.createNestedArray("temp");
    JsonArray& humid = root.createNestedArray("humid");
    temp.add(String(temperature,1));
    humid.add(String(humidity,1));
    String JSON;
    root.printTo(Serial);
    root.prettyPrintTo(JSON);
    return JSON;
  }

  
void reconnectWiFi(){
 WiFi.mode(WIFI_STA);
  
  WiFi.disconnect();
  WiFi.begin(ssid, pass);
  
  while (WiFi.status() != WL_CONNECTED) {
    if(counter > 20){
       Serial.println("- can't connect, going to sleep");
       hibernate(5);
    } 
    delay(500);
    Serial.print(".");
    counter++;
  }
    
    Serial.println("Connected");
    Serial.println(WiFi.localIP());
    Serial.printf("SSID: %s\n", WiFi.SSID().c_str());
    Serial.print("PASS:");
    Serial.print(WiFi.psk());
    delay(500);
}


void handleRoot(){
  
  server.send(200,"text/plain","Hello Server");
 }

void handleFeed(){
    server.send(200,"application/json",inToJson());   
 }

void handleNotFound(){
  String message = "File Not Found\n\n";
  message += "URI: ";
  message += server.uri();
  message += "\nMethod: ";
  message += (server.method() == HTTP_GET)?"GET":"POST";
  message += "\nArguments: ";
  message += server.args();
  message += "\n";
  for (uint8_t i=0; i<server.args(); i++){
    message += " " + server.argName(i) + ": " + server.arg(i) + "\n";
  }
server.send(404, "text/plain", message);
}

void hibernate(int pInterval) {
  WiFi.disconnect();
  ESP.deepSleep(10 * 600000 * pInterval, WAKE_RFCAL);
  delay(100);
}

