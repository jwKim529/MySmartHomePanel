package com.example.project.tcpip;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


import java.net.URL;
import java.util.ResourceBundle;

public class AppRoomController implements Initializable {
    public Button room_button_end;
    public Circle living1_light;
    public Circle living2_light;
    public Circle kitchen_light;
    public Circle room1_light;
    public Circle room2_light;
    public Circle room3_light;
    public Circle bath1_light;
    public Circle bath2_light;
    public Button room_button_to_main;
    private String room_light_state;
    private AppMain myApp;

    public AppRoomController ()
    {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.myApp = AppMain.getInstance();
        this.room_light_state = myApp.getRoom_light_state();
        char[] initial_room_state = room_light_state.toCharArray();

        if(initial_room_state[0] == '0') {
            living1_light.setFill(Paint.valueOf("black"));
        } else {
            living1_light.setFill(Paint.valueOf("yellow"));
        }

        if(initial_room_state[1] == '0') {
            living2_light.setFill(Paint.valueOf("black"));
        } else {
            living2_light.setFill(Paint.valueOf("yellow"));
        }

        if(initial_room_state[2] == '0') {
            kitchen_light.setFill(Paint.valueOf("black"));
        } else {
            kitchen_light.setFill(Paint.valueOf("yellow"));
        }

        if(initial_room_state[3] == '0') {
            room1_light.setFill(Paint.valueOf("black"));
        } else {
            room1_light.setFill(Paint.valueOf("yellow"));
        }

        if(initial_room_state[4] == '0') {
            room2_light.setFill(Paint.valueOf("black"));
        } else {
            room2_light.setFill(Paint.valueOf("yellow"));
        }

        if(initial_room_state[5] == '0') {
            room3_light.setFill(Paint.valueOf("black"));
        } else {
            room3_light.setFill(Paint.valueOf("yellow"));
        }

        if(initial_room_state[6] == '0') {
            bath1_light.setFill(Paint.valueOf("black"));
        } else {
            bath1_light.setFill(Paint.valueOf("yellow"));
        }

        if(initial_room_state[7] == '0') {
            bath2_light.setFill(Paint.valueOf("black"));
        } else {
            bath2_light.setFill(Paint.valueOf("yellow"));
        }

        living1_light.setOnMouseClicked(event -> handleCircleClick(event, living1_light,0));
        living2_light.setOnMouseClicked(event -> handleCircleClick(event, living2_light,1));
        kitchen_light.setOnMouseClicked(event -> handleCircleClick(event, kitchen_light,2));
        room1_light.setOnMouseClicked(event -> handleCircleClick(event, room1_light,3));
        room2_light.setOnMouseClicked(event -> handleCircleClick(event, room2_light,4));
        room3_light.setOnMouseClicked(event -> handleCircleClick(event, room3_light,5));
        bath1_light.setOnMouseClicked(event -> handleCircleClick(event, bath1_light,6));
        bath2_light.setOnMouseClicked(event -> handleCircleClick(event, bath2_light,7));
    }

    private void handleCircleClick(MouseEvent event, Circle targetCircle, int targetIndex) {
        if(targetCircle.getFill() == Color.YELLOW){
            targetCircle.setFill(Paint.valueOf("black"));
            room_light_state = room_light_state.substring(0,targetIndex) + '0'
                    + room_light_state.substring(targetIndex+1);
            myApp.setRoom_light_state(room_light_state);
            myApp.writeSerialData();
        }else {
            targetCircle.setFill(Paint.valueOf("yellow"));
            room_light_state = room_light_state.substring(0,targetIndex) + '1'
                    + room_light_state.substring(targetIndex+1);
            myApp.setRoom_light_state(room_light_state);
            myApp.writeSerialData();
        }
    }
    public void buttonOnClickedRoomEnd(ActionEvent actionEvent) {
        Stage stage = (Stage)room_button_end.getScene().getWindow();
        stage.close();
        System.exit(0);
    }

    public void buttonOnClickedRoomToMain(ActionEvent actionEvent) {
        Stage stage = (Stage)room_button_to_main.getScene().getWindow();
        stage.hide();
    }
}
