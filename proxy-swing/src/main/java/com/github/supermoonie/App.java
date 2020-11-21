package com.github.supermoonie;

import de.javagl.treetable.JTreeTable;

import javax.swing.*;
import java.awt.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Lighting");
        MenuBar mb = new MenuBar();
        Menu file = new Menu("File");
        file.add(new CheckboxMenuItem("Open"));
        mb.add(file);
        frame.setMenuBar(mb);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        JButton button1 = new JButton("上");
        JButton button2 = new JButton("左");
        JButton button3 = new JButton("中");
        JButton button4 = new JButton("右");
        JButton button5 = new JButton("下");
        frame.add(button1, BorderLayout.NORTH);
        frame.add(button2, BorderLayout.WEST);
        frame.add(button3, BorderLayout.CENTER);
        frame.add(button4, BorderLayout.EAST);
        frame.add(button5, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
