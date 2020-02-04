// Autor:
// Date:
// TO-DO
//      Ver: bar PSI

//Include the SoftwareSerial library
#include "SoftwareSerial.h"

//Create a new software  serial
SoftwareSerial bluetooth(10, 11 ); //TX, RX (Bluetooth)
 
const int ledPin = 13; // the pin that the LED is attached to
int incomingByte;      // a variable to read incoming serial data into
int Sensor_1 = A0;  // Sensor conectado ao pino analógico 0 (YX52S00013P1 Excavator Low Pressure Sensor)
int Sensor_2 = A1;  
int Sensor_3 = A2;  
int Sensor_4 = A3;  

float pr_1 = 0.00;
float pr_2 = 0.00;
float pr_3 = 0.00;
float pr_4 = 0.00;

float Pres_Bar = 0.00;
float Pres_Psi = 0.00;

void setup() {
  //Initialize the software serial
  bluetooth.begin(9600);
 
  // initialize the LED pin as an output:
  pinMode(ledPin, OUTPUT);
}

void loop() {
  // see if there's incoming serial data:
  if (bluetooth.available() > 0) {
    // read the oldest byte in the serial buffer:
    incomingByte = bluetooth.read();
    // if it's a capital H (ASCII 72), turn on the LED:
    if (incomingByte == 'H') {
      digitalWrite(ledPin, HIGH);
      bluetooth.println("LED: ON");
    }
    // if it's an L (ASCII 76) turn off the LED:
    if (incomingByte == 'L') {
      digitalWrite(ledPin, LOW);
      bluetooth.println("LED: OFF");

    }
  }

  // Sensor conectado ao pino analógico 0 (YX52S00013P1 Excavator Low Pressure Sensor)
     
      //Pressao = map(analogRead(Sensor_Pressao), 0, 1023, 0.00, 5.00);  // lê o pino de entrada
      pr_1 = (analogRead(Sensor_1) * (5.00/1023));
      pr_2 = (analogRead(Sensor_2) * (5.00/1023));
      pr_3 = (analogRead(Sensor_3) * (5.00/1023));
      pr_4 = (analogRead(Sensor_4) * (5.00/1023));
      
      
     
      //se nao quiser que saia em volts, só comentar essas 3 linhas abaixo
     // bluetooth.print("Volts: ");
  //    bluetooth.print(Pressao);
   //   bluetooth.println("V");

       bluetooth.print(pr_1); 
       bluetooth.print(";"); 
       bluetooth.print(pr_2); 
       bluetooth.print(";"); 
       bluetooth.print(pr_3); 
       bluetooth.print(";"); 
       bluetooth.print(pr_4); 
       bluetooth.print("#"); 
       
     
    /*  //Pressure range: 0~50 MPa (500 Bar ou 7251.89 Psi) (Vs=5V DC 0.5V~4.5V DC)
      if ((Pressao >= 0.5) && (Pressao >= 4.5)) {
        Pressao = Pressao - 0.5;
        Pres_Bar = Pressao * (500/4.5);
        Pres_Psi = Pressao * (7251.89/4.5);
       
     //   bluetooth.print("Pressão: ");
    //    bluetooth.print(Pres_Bar);
    //    bluetooth.print(" Bar");
    //    bluetooth.print(" ou ");
    //    bluetooth.print(Pres_Psi);
    //    bluetooth.println(" Psi");
      }
      */
     
      delay(300);
}
