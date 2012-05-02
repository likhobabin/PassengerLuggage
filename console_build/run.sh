#!/bin/sh
javac -cp "$CLASSPATH:./dist/PassengerLuggage.jar:." ./test/Test.java ;
java -cp "$CLASSPATH:./dist/PassengerLuggage.jar:." test.Test ;



