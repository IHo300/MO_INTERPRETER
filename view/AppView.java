package view;

import java.util.*;
import java.io.*;

import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.event.ActionEvent; 
import javafx.stage.FileChooser;
import javafx.scene.input.Mnemonic;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.text.TextAlignment;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CharStream;
import org.fxmisc.richtext.*;

import controller.AppController;

public class AppView {
    private static final String dev_file = "sort.psc";

    private AppController controller;
    private TextArea textArea;
    private Stage stage;
    private CodeArea codeArea;
    private TextFlow status;
    private Text textStatus;    
    private ImageView statusImageView;


    public AppView (Stage stage) {
        this.stage = stage;

        // MENU GUI
        Menu fileMenu = new Menu("File");

        // New
        MenuItem fileMenuItem1 = new MenuItem("New");
        Image plusIcon = new Image(getClass().getResourceAsStream("/assets/plus.png"));
        ImageView plusIconView = new ImageView(plusIcon);
        plusIconView.setFitWidth(17);
        plusIconView.setFitHeight(17);
        fileMenuItem1.setGraphic(plusIconView);

        // Open
        Image folderIcon = new Image(getClass().getResourceAsStream("/assets/folder.png"));
        ImageView folderIconView = new ImageView(folderIcon);
        folderIconView.setFitWidth(17);
        folderIconView.setFitHeight(17);
        MenuItem fileMenuItem2 = new MenuItem("Open");
        fileMenuItem2.setGraphic(folderIconView);
        fileMenuItem2.addEventHandler(ActionEvent.ACTION,event ->{ 
            chooseFile();
        });

        // Exit 
        Image exitIcon = new Image(getClass().getResourceAsStream("/assets/exit.png"));
        ImageView exitIconView = new ImageView(exitIcon);
        exitIconView.setFitWidth(17);
        exitIconView.setFitHeight(17);
        MenuItem fileMenuItem3 = new MenuItem("Exit");
        fileMenuItem3.setGraphic(exitIconView);
        fileMenuItem3.addEventHandler(ActionEvent.ACTION,event ->{ 
            stage.close();
        });

        fileMenu.getItems().add(fileMenuItem1);
        fileMenu.getItems().add(fileMenuItem2);
        fileMenu.getItems().add(fileMenuItem3);

        Menu runMenu = new Menu("Run");
    
        // Run
        MenuItem runMenuItem1 = new MenuItem("Run");
        Image playIcon = new Image(getClass().getResourceAsStream("/assets/play.png"));
        ImageView playIconView = new ImageView(playIcon);
        playIconView.setFitWidth(17);
        playIconView.setFitHeight(17);
        runMenuItem1.setGraphic(playIconView);

        // Show Parse Tree
        MenuItem runMenuItem2 = new MenuItem("Show Parse Tree");
        Image treeIcon = new Image(getClass().getResourceAsStream("/assets/tree.png"));
        ImageView treeIconView = new ImageView(treeIcon);
        treeIconView.setFitWidth(17);
        treeIconView.setFitHeight(17);
        runMenuItem2.setGraphic(treeIconView);

        // Terminate (useful for infinite loops)
        MenuItem runMenuItem3 = new MenuItem("Terminate");
        Image terminateIcon = new Image(getClass().getResourceAsStream("/assets/cancel.png"));
        ImageView terminateIconView = new ImageView(terminateIcon);
        terminateIconView.setFitWidth(17);
        terminateIconView.setFitHeight(17);
        runMenuItem3.setGraphic(terminateIconView);

        runMenu.getItems().add(runMenuItem1);
        runMenu.getItems().add(runMenuItem2);
        runMenu.getItems().add(runMenuItem3);

        MenuBar menuBar = new MenuBar(fileMenu, runMenu);

        // TEXT EDITOR
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setMinHeight(370);

        runMenuItem1.addEventHandler(ActionEvent.ACTION,event -> { 
            controller.parse(codeArea.getText());
            controller.execute();
        });

        // Keyboard Shortcut for Parse
        KeyCombination kc = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
        Runnable rn = ()-> {
            controller.parse(codeArea.getText());
            controller.execute();
        };

        runMenuItem2.addEventHandler(ActionEvent.ACTION,event -> { 
            controller.showTree(codeArea.getText());
        });

        runMenuItem3.addEventHandler(ActionEvent.ACTION,event -> { 
            controller.terminate();
        });

        fileMenuItem1.addEventHandler(ActionEvent.ACTION,event ->{ 
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("New project");
            alert.setHeaderText("Changes in the text editor will not be saved.");
            alert.setContentText("Are you sure?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    codeArea.clear();
                }
            });
        });


        status = new TextFlow();
        status.setMinHeight(27);
        status.setStyle("margin-bottom: 0.5px; -fx-background-color: linear-gradient(to bottom, derive(-fx-text-box-border, -10%), -fx-text-box-border), linear-gradient(from 0px 0px to 0px 0px, derive(-fx-control-inner-background, -9%), -fx-control-inner-background)");

        statusImageView = new ImageView();
        statusImageView.setFitWidth(19);
        statusImageView.setFitHeight(19);

    
        textStatus = new Text();
        textStatus.setTextAlignment(TextAlignment.CENTER);

        HBox statusContainer = new HBox(textStatus, statusImageView);
        statusContainer.setPadding(new Insets(5,0,0,10));

        status.getChildren().addAll(statusContainer);
        setStatus("Ready to compile / execute", "ready");

        // CONSOLE
        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setMinHeight(178);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-background-color: linear-gradient(to bottom, derive(-fx-text-box-border, -10%), -fx-text-box-border), linear-gradient(from 0px 0px to 0px 0px, derive(-fx-control-inner-background, -9%), -fx-control-inner-background)");

        textArea.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                    Object newValue) {
                textArea.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
                //use Double.MIN_VALUE to scroll to the top
            }
        });

        VBox vBox = new VBox(menuBar,codeArea, status, textArea);
        Scene scene = new Scene(vBox, 800, 600);
        scene.getAccelerators().put(kc, rn);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image("/assets/code.png"));
        stage.setTitle("Pseudocode IDE by Tan & Ty");
        stage.show();


        if (dev_file != "") {
            //for dev purposes
            File file = new File("resources/" + dev_file); 
            codeArea.appendText(readFile(file));
        }
    }


    // returns the value that the user enters
    public String getInput(String prompt) {
         // create a text input dialog 
        
        TextInputDialog td = new TextInputDialog(); 
        td.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);
        td.getDialogPane().lookupButton(ButtonType.CANCEL).setManaged(false);
        td.setTitle("Scan Input");
        td.setHeaderText(prompt);
        td.showAndWait(); 
    
        return td.getEditor().getText(); 
    }

    public void updateLogs(List<String> output) {

        if (output == null) {
            textArea.setText("");
            textArea.appendText(""); 
            return;
        }

        StringBuilder logs = new StringBuilder(""); 

        output.forEach((li) -> {
            logs.append(li + "\n");
        });

        textArea.setText(logs.toString());
        textArea.appendText(""); 
    }

    public void printToConsole(String msg) {
        String all = textArea.getText();
        textArea.setText(all + msg + "\n");
        textArea.appendText(""); 
    }

    public void setStatus(String msg, String iconType) {

        switch (iconType) {
            case "ready": textStatus.setText("STATUS: " + msg + "  "); statusImageView.setImage(new Image(getClass().getResourceAsStream("/assets/ready.png"))); break;
            case "executing": textStatus.setText("STATUS: " + msg + "  "); statusImageView.setImage(new Image(getClass().getResourceAsStream("/assets/executing.png"))); break;
            case "fail": textStatus.setText("STATUS: " + msg + "  ");  statusImageView.setImage(new Image(getClass().getResourceAsStream("/assets/fail.png")));break;
            case "success": textStatus.setText("STATUS: " + msg + "  "); statusImageView.setImage(new Image(getClass().getResourceAsStream("/assets/success.png"))); break;
            default: textStatus.setText("STATUS: Compiler is idle."); statusImageView.setImage(null); break;
        }

    }

    private String readFile(File file){
        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;
        
        try {

            bufferedReader = new BufferedReader(new FileReader(file));
            
            String text = bufferedReader.readLine();
            while (text != null) {
                stringBuffer.append(text);
                text = bufferedReader.readLine();
                if (text != null) {
                    stringBuffer.append("\n");
                }
            }

        }  catch (IOException e) {
           
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ex) { }
        } 
        
        return stringBuffer.toString();
    }

    private void chooseFile() {

        FileChooser fileChooser = new FileChooser();
                
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PSC files", "*.psc");
        fileChooser.getExtensionFilters().add(extFilter);
        
        //Show save file dialog
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
            codeArea.clear();
            codeArea.appendText(readFile(file));
        }
    }

    public void setController(AppController controller) {
        this.controller = controller;
    }

}