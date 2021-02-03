java -jar lib/antlr-4.9-complete.jar -package parser -o parser grammar/PSC.g4

javac --module-path lib\javafx-sdk-11.0.2\lib --add-modules javafx.controls -cp ".;lib/richtextfx-fat-0.10.5.jar;lib/antlr-4.9-complete.jar" parser/*.java

