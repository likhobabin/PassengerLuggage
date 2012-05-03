dist="../dist" 

if [ ! -d $dist ]; then
	echo "dist does not. Create dist" ;
	mkdir $dist ;
fi 
######################
#!/bin/sh
cd ../lib/airport/flights/ ;
rm -f *.class ;
#to lib dir
cd ../../../dist ;
rm -f *.jar ;
cd ../lib ;
javac -cp "$CLASSPATH:derbyclient.jar:." airport/flights/*.java ;
jar -cvf ../dist/PassengerLuggage.jar airport/flights/* ;
#########################################################
cd ../test ;
rm -f *.class ;
#to root dir
cd ../ ;
javac -cp "$CLASSPATH:derbyclient.jar:./dist/PassengerLuggage.jar:." ./test/Test.java ;
java -cp "$CLASSPATH:derbyclient.jar:./dist/PassengerLuggage.jar:." test.Test ;



