package view;

import controller.AppController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import javax.swing.*;

public class Gui {


    private static final String dev_file = "sort.psc";
    private AppController controller;
    private TextArea out = new TextArea("",0,60,TextArea.SCROLLBARS_VERTICAL_ONLY);

    public void Gui() throws Exception {
        Frame f = new Frame();
        f.setSize(500,820);

        Panel panel1 = new Panel();
        panel1.setSize(500,600);

        Panel panel2 = new Panel();
        panel2.setSize(500,200);



        MenuBar m = new MenuBar();
        f.setMenuBar( m );

        Menu m_menu = new Menu("Menu");
        m.add(m_menu);

        MenuItem i_load = new MenuItem("Load");
        m_menu.add(i_load);

        MenuItem i_run = new MenuItem("Run");
        m_menu.add(i_run);



        TextArea t = new TextArea("",35,60,TextArea.SCROLLBARS_VERTICAL_ONLY);
        t.setSize(500,600);

        panel1.add(t);


        out.setEditable(false);
        out.setSize(500,200);

        panel2.add(out);

        f.add(panel1,"North");
        f.add(panel2,"South");


        f.setVisible(true);

        i_run.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.out.println("run button");

                controller.parse(t.getText());
                controller.execute();
            }
        });


        i_load.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String temps = new String();
                String temps2 = new String();

                FileDialog dialog = new FileDialog(f, "Load", FileDialog.LOAD);
                dialog.setFile("*.txt;");
                dialog.setVisible(true);

                String path = dialog.getDirectory() + dialog.getFile();

                if( dialog.getFile() == null ) return;



                try{
                    FileReader filereader = new FileReader(path);
                    int singleCh = 0;
                    while((singleCh = filereader.read()) != -1){
                        temps = temps.concat((char)singleCh+"");
                    }
                    filereader.close();
                }catch (FileNotFoundException e2) {
                    // TODO: handle exception
                }catch(IOException e2){
                    System.out.println(e2);
                }


                t.setText(temps);
            }
        });
    }

    public void updateLogs(List<String> output) {

        if (output == null) {
            out.setText("");
            //out.appendText("");
            return;
        }

        StringBuilder logs = new StringBuilder("");

        output.forEach((li) -> {
            logs.append(li + "\n");
        });

        out.setText(logs.toString());
        //out.appendText("");
    }

    public void setController(AppController controller) {
        this.controller = controller;
    }

    public void printToConsole(String msg) {
        out.setText(out.getText() + msg + "\n");
    }

    public String getInput(String prompt) {
        // create a text input dialog

        String input = JOptionPane.showInputDialog("Input");;

        return input;
    }
}