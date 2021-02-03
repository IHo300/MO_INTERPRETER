javac --module-path lib\javafx-sdk-11.0.2\lib --add-modules javafx.controls -cp ".;lib/richtextfx-fat-0.10.5.jar;lib/antlr-4.9-complete.jar"  model/*.java model/objects/*.java model/semcheck/*.java model/visitors/*.java model/commands/*.java

javac --module-path lib\javafx-sdk-11.0.2\lib --add-modules javafx.controls -cp ".;lib/richtextfx-fat-0.10.5.jar;lib/antlr-4.9-complete.jar"  controller/*.java

javac --module-path lib\javafx-sdk-11.0.2\lib --add-modules javafx.controls -cp ".;lib/richtextfx-fat-0.10.5.jar;lib/antlr-4.9-complete.jar"  view/*.java

