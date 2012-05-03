#!/bin/sh
cd ./airport/flights/ ;
rm -f *.class ;
cd ../../ ;
javac -cp "$CLASSPATH:derbyclient.jar:" airport/flights/*.java ;
jar -cvf ../dist/PassengerLuggage.jar airport/flights/* ;



