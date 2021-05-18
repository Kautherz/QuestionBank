package com.agileteamproject2021.questionbank;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.JFormattedTextField;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.BorderLayout;


public class FilterQuestions extends JPanel {
    private JLabel label1 = new JLabel("Start date:");
    private DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    private JFormattedTextField field1 = new JFormattedTextField(df);

    private JLabel label2 = new JLabel("End date:");
    private JFormattedTextField field2 = new JFormattedTextField(df);

    private JLabel label3 = new JLabel("Course:");
    private JMenuBar field3= new JMenuBar();
    
    private JLabel label4 = new JLabel("Topic:");
    private JMenuBar field4= new JMenuBar();

    private JLabel label5 = new JLabel("Question-type");
    private JMenuBar field5= new JMenuBar();

    private JLabel label6 = new JLabel("Keyword:");
    private JTextField field6 = new JTextField(20);

    private JButton filter = new JButton("Filter");
    private JButton cancel = new JButton("Cancel");

    JFrame filterFrame = new JFrame("Filter Questions");

    public FilterQuestions(AppLogger appLogger, DataFile dataFile) {
        
        field1.setText("mm/dd/yyyy"); 
        field2.setText("mm/dd/yyyy");

        JMenuItem course1 = new JMenuItem("Course A"); 
        JMenuItem course2 = new JMenuItem("Course B");   
        JMenu courseMenu= new JMenu("Choose course");
        courseMenu.add(course1);
        courseMenu.add(course2);
        field3.add(courseMenu);

        JCheckBox topic1 = new JCheckBox("Topic A");
        JCheckBox topic2 = new JCheckBox("Topic B");
        JCheckBox topic3 = new JCheckBox("Topic C");
        JMenu topicMenu = new JMenu("Choose Topics");
        topicMenu.add(topic1);
        topicMenu.add(topic2);
        topicMenu.add(topic3);
        field4.add(topicMenu);

        JCheckBox qType1 = new JCheckBox("Multiple Choice");
        JCheckBox qType2 = new JCheckBox("Short Q/A");
        JCheckBox qType3 = new JCheckBox("Essays");
        JCheckBox qType4 = new JCheckBox("Numerical");

        JMenu qMenu = new JMenu("Choose Question Types");
        qMenu.add(qType1);
        qMenu.add(qType2);
        qMenu.add(qType3);
        qMenu.add(qType4);
        field5.add(qMenu);

        GroupLayout layout = new GroupLayout(this);          
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(label1)
                .addComponent(label2)
                .addComponent(label3)
                .addComponent(label4)
                .addComponent(label5)
                .addComponent(label6)
                .addComponent(filter))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(field1)
                .addComponent(field2)
                .addComponent(field3)
                .addComponent(field4)
                .addComponent(field5)
                .addComponent(field6)
                .addComponent(cancel))
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(label1)
                .addComponent(field1))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(label2)
                .addComponent(field2))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(label3)
                .addComponent(field3))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(label4)
                .addComponent(field4))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(label5)
                .addComponent(field5))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(label6)
                .addComponent(field6))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(filter)
                .addComponent(cancel))
        );
        JLabel label = new JLabel("Please enter your choices: ");
        filterFrame.add(label, BorderLayout.NORTH);

        filterFrame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        filterFrame.add(this, BorderLayout.CENTER);
        filterFrame.pack();
        filterFrame.setLocationRelativeTo(null);
    }

    public void display() {
        filterFrame.setVisible(true);
    }
}
