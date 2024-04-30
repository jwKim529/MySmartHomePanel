package com.example.project.tcpip;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class AppMain extends Application {

    private final double TEMP_INITIAL = 22.0;
    private final double HUMID_INITIAL = 40.0;
    private final String ROOM_INITIAL = "00000000";


    private double desired_temperature;
    private double desired_humidity;
    private String room_light_state;
    private Socket socket;

    private static AppMain instance;

    public AppMain() {
    }

    public static void main(String[] args) { launch();}

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        this.socket = new Socket();
        this.desired_temperature = TEMP_INITIAL;
        this.desired_humidity = HUMID_INITIAL;
        this.room_light_state = ROOM_INITIAL;

        try {
            this.socket.connect(new InetSocketAddress("192.168.0.5", Integer.parseInt("9999")));
        } catch (IOException e) {
            //throw new RuntimeException(e);
            System.out.printf("%s\r\n",e.getMessage());
            System.exit(-1);
        }

        try {
            final FXMLLoader loader = new FXMLLoader();
            final URL resource = this.getClass().getResource("app-start-controller.fxml");
            loader.setLocation(resource);
            final Parent parent_root = loader.load();
            final Scene scene = new Scene(parent_root);
            primaryStage.setTitle("MY HOME");
            primaryStage.setMaxWidth(800.0);
            primaryStage.setMaxHeight(600.0);
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeSerialData() {
        byte[] bytes_data = (
                room_light_state+","
                        +desired_temperature+","
                        +desired_humidity+"\n"
        ).getBytes(StandardCharsets.UTF_8);
        try {
            final var output_stream = this.socket.getOutputStream();
            output_stream.write(bytes_data);
            output_stream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double getDesired_humidity() {
        return desired_humidity;
    }

    public void setDesired_humidity(double desired_humidity) {
        this.desired_humidity = desired_humidity;
    }

    public double getDesired_temperature() {
        return desired_temperature;
    }

    public void setDesired_temperature(double desired_temperature) {
        this.desired_temperature = desired_temperature;
    }

    public String getRoom_light_state() {
        return room_light_state;
    }

    public void setRoom_light_state(String room_light_state) {
        this.room_light_state = room_light_state;
    }

    public Socket getSocket() {
        return socket;
    }

    public static AppMain getInstance() {
        return instance;
    }
}