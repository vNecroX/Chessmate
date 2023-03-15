const int stepPin = 4; 
const int dirPin = 3; 
const int enPin = 2;

void setup() 
{
  pinMode(stepPin,OUTPUT); 
  pinMode(dirPin,OUTPUT);

  pinMode(enPin,OUTPUT);
  digitalWrite(enPin,LOW);
}

void loop() 
{
  delay(1000); 

  digitalWrite(dirPin, HIGH); 
  
  for(int x = 0; x < 800; x++) 
  {
    digitalWrite(stepPin,HIGH);
    delay(1);
    digitalWrite(stepPin,LOW);
    delay(1);
  }
  
  delay(1000);
}
