#!/bin/sh

dist="../dist" 

if [ ! -d $dist ]; then
	echo "dist does not. Create dist" ;
	mkdir $dist ;
fi 
######################
cd ./airport/flights/ ;
rm -f *.class ;
cd ../../../dist ;
rm -f *.jar ;
cd ../lib ;
javac -cp "$CLASSPATH:derbyclient.jar:" airport/flights/*.java ;
jar -cvf ../dist/PassengerLuggage.jar airport/flights/* ;



