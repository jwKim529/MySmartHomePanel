package com.example.project.tcpip;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.stage.Stage;

import java.util.Objects;

public class AppStartController {
    public Button button_start;
    public Button button_end;

    public AppStartController () {
    }

    public void buttonOnClickedStart(ActionEvent actionEvent) {
        Stage newStage = new Stage();
        Stage stage = (Stage)button_start.getScene().getWindow();
        try {
            Parent second = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("app-main-controller.fxml")));

            // 씬에 레이아웃 추가
            Scene sc = new Scene(second);
            newStage.setTitle("MY HOME : STATUS");
            newStage.setMaxWidth(800.0);
            newStage.setMaxHeight(600.0);
            newStage.setResizable(false);

            // 씬을 스테이지에서 상영
            newStage.setScene(sc);
            newStage.show();

            stage.hide();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void buttonOnClickedEnd(ActionEvent actionEvent) {
        Stage stage = (Stage)button_end.getScene().getWindow();
        stage.close();
        System.exit(0);
    }
}
