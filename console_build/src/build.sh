#!/bin/sh
javac -cp "$CLASSPATH" airport/flights/*.java
jar -cvf ../dist/PassengerLuggage.jar airport/flights/*



