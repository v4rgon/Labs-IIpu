package main;

import main.controller.WebcamController;
import main.utils.Utils;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Objects;
import javax.swing.*;
import javax.swing.JOptionPane;

public class Main {
    private static final String APP_NAME = "Webcam app";
    private static final int FPS = 50;
    private static WebcamController webcamController;

    public static void main(String[] args) {
        webcamController = new WebcamController();
        /* Use an appropriate Look and Feel */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        UIManager.put("swing.boldMetal", Boolean.FALSE);
        //Schedule a job for the event-dispatching thread:
        //adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Test frame");
        frame.setSize(200, 200);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton infoBtn = new JButton("Information");
        JButton photoBtn = new JButton("Take a photo");
        JButton videoBtn = new JButton("Record a video");
        JButton trayBtn =new JButton("Tray");

        infoBtn.setPreferredSize(new Dimension(200,50));
        photoBtn.setPreferredSize(new Dimension(100,50));
        videoBtn.setPreferredSize(new Dimension(100,50));
        trayBtn.setPreferredSize(new Dimension(200,50));

        frame.getContentPane().add(BorderLayout.NORTH,infoBtn);
        frame.getContentPane().add(BorderLayout.WEST,photoBtn);
        frame.getContentPane().add(BorderLayout.EAST,videoBtn);
        frame.getContentPane().add(BorderLayout.SOUTH,trayBtn);
        frame.setVisible(true);

        infoBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Webcam: " + webcamController.getName());
            }
        });

        photoBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Photo had been taken",
                        "Photo",
                        JOptionPane.INFORMATION_MESSAGE);
                try {
                    webcamController.takePhoto();
                } catch (Exception exception) {
                    Utils.showExceptionDialog(exception);
                }
            }
        });

        videoBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                int duration = showDurationInputDialogWindow();
                try {
                    webcamController.recordVideo(duration, FPS);
                } catch (Exception exception) {
                    Utils.showExceptionDialog(exception);
                }
                JOptionPane.showMessageDialog(null,
                        "Video was recorded",
                        "Video",
                        JOptionPane.INFORMATION_MESSAGE);

            }
        });

        trayBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        createAndShowTray();
                    }
                });
            }
        });

    }
    private static void createAndShowTray() {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon =
                new TrayIcon(Objects.requireNonNull(createImage("resources/icon.gif", "tray icon")));
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a popup menu components
        MenuItem webcamItem = new MenuItem("Information");
        MenuItem photoItem = new MenuItem("Take a photo");
        MenuItem videoItem = new MenuItem("Record a video");
        MenuItem exitItem = new MenuItem("Exit");

        //Add components to popup menu
        popup.add(webcamItem);
        popup.addSeparator();
        popup.add(photoItem);
        popup.add(videoItem);
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "This dialog box is run from System Tray");
            }
        });
        webcamItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Webcam: " + webcamController.getName());
            }
        });


        photoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MenuItem item = (MenuItem)e.getSource();

                System.out.println(item.getLabel());

                try {
                    webcamController.takePhoto();
                } catch (Exception exception) {
                    Utils.showExceptionDialog(exception);
                }

                trayIcon.displayMessage(APP_NAME,
                        "Photo had been taken", TrayIcon.MessageType.INFO);
            }
        });

        videoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MenuItem item = (MenuItem)e.getSource();

                System.out.println(item.getLabel());

                int duration = showDurationInputDialogTray(trayIcon);

                try {
                    webcamController.recordVideo(duration, FPS);
                } catch (Exception exception) {
                    Utils.showExceptionDialog(exception);
                }

                //type = TrayIcon.MessageType.INFO;
                trayIcon.displayMessage(APP_NAME,
                        "Video was recorded", TrayIcon.MessageType.INFO);
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                trayIcon.displayMessage(APP_NAME,
                        "Webcam app was shut down", TrayIcon.MessageType.INFO);

                tray.remove(trayIcon);
                System.exit(0);
            }
        });

        //signal that the app has started working
        trayIcon.displayMessage(APP_NAME,
                "Webcam app is working", TrayIcon.MessageType.INFO);
    }

    private static int showDurationInputDialogTray(TrayIcon trayIcon)
    {
        int result;

        String inputValue = JOptionPane.showInputDialog("Please input required video duration");

        try {
            result = Integer.parseUnsignedInt(inputValue);
        } catch (Exception exception){
            trayIcon.displayMessage(APP_NAME,
                    "The value of param duration is invalid!", TrayIcon.MessageType.ERROR);
            result = showDurationInputDialogTray(trayIcon);
        }

        return result;
    }
    private static int showDurationInputDialogWindow()
    {
        int resultWindow;

        String inputValue = JOptionPane.showInputDialog("Please input required video duration");
        try {
            resultWindow = Integer.parseUnsignedInt(inputValue);
        }catch (Exception exception){
            JOptionPane.showMessageDialog(null,
                    "The value of param duration is invalid!","Record", JOptionPane.ERROR_MESSAGE);
            resultWindow = showDurationInputDialogWindow();
        }

        return resultWindow;
    }


    //Obtain the image URL
    private static Image createImage(String path, String description) {
        URL imageURL = Main.class.getClassLoader().getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}
