package com.example.project.tcpip;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.ResourceBundle;

public class AppMainController implements Initializable {
    private final int DATA_LENGTH = 3;

    public Label label_temperature;
    public ProgressBar progressbar_temperature;
    public Label label_humidity;
    public ProgressBar progressbar_humidity;
    public Button main_button_end;
    public Button main_button_to_room;
    public Label desired_label_temperature;
    public TextField desired_text_temperature;
    public Button desired_button_temperature;
    public Label desired_label_humidity;
    public TextField desired_text_humidity;
    public Button desired_button_humidity;
    public Circle cooler_led;
    public Circle heater_led;
    public Circle humidif_led;
    public Circle dehumidif_led;

    private final Socket socket;

    private final AppMain myApp;

    private double desired_temperature;
    private double desired_humidity;


    public AppMainController()
    {
        this.myApp = AppMain.getInstance();
        this.desired_temperature = myApp.getDesired_temperature();
        this.desired_humidity = myApp.getDesired_humidity();
        this.socket = myApp.getSocket();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        desired_label_temperature.setText(String.valueOf(desired_temperature));
        desired_label_humidity.setText(String.valueOf(desired_humidity));
        if(socket.isConnected()){
            this.received_data_from_server(new ActionEvent());
        }
    }

    private void received_data_from_server(ActionEvent event) {
        Thread thread_of_receiving = new Thread(()->{
            while(true)
            {
                try {
                    final InputStream inputStream = this.socket.getInputStream();
                    byte[] bytes_data = new byte[512];
                    final int read_byte_count = inputStream.read(bytes_data);
                    final String serial_input_data =
                            new String(bytes_data, 0, read_byte_count, StandardCharsets.UTF_8);
                    final String[] parsings_data = serial_input_data.split(",");
                    if(parsings_data.length != DATA_LENGTH) continue;
                    final double current_temperature = Double.parseDouble(parsings_data[0]);
                    final double current_humidity = Double.parseDouble(parsings_data[1]);
                    check_led(parsings_data[2]);

                    final double changed_temperature = change_progress_value(current_temperature, 0.0, 40.0, 0.0, 1.0);
                    final double changed_humidity = change_progress_value(current_humidity, 0.0, 100.0, 0.0, 1.0);
                    Platform.runLater(()->{
                        progressbar_temperature.setProgress(changed_temperature);
                        progressbar_humidity.setProgress(changed_humidity);
                        label_temperature.setText(parsings_data[0]);
                        label_humidity.setText(parsings_data[1]);
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread_of_receiving.start();
    }
    private void check_led(String current_airC) {
        final int current_cooler = Integer.parseInt(String.valueOf(current_airC.charAt(0)));
        final int current_heater = Integer.parseInt(String.valueOf(current_airC.charAt(1)));
        final int current_humidif = Integer.parseInt(String.valueOf(current_airC.charAt(2)));
        final int current_dehumidif = Integer.parseInt(String.valueOf(current_airC.charAt(3)));

        if(current_cooler == 1) {
            cooler_led.setFill(Paint.valueOf("blue"));
        } else {
            cooler_led.setFill(Paint.valueOf("black"));
        }
        if(current_heater == 1) {
            heater_led.setFill(Paint.valueOf("red"));

        } else {
            heater_led.setFill(Paint.valueOf("black"));
        }
        if(current_humidif == 1) {
            humidif_led.setFill(Paint.valueOf("skyblue"));
        } else {
            humidif_led.setFill(Paint.valueOf("black"));
        }
        if(current_dehumidif == 1) {
            dehumidif_led.setFill(Paint.valueOf("greenyellow"));
        } else {
            dehumidif_led.setFill(Paint.valueOf("black"));
        }

    }

    private double change_progress_value(double x,
                                         double in_min, double in_max,
                                         double out_min, double out_max){
        return (x - in_min) * (out_max - out_min) / ((in_max - in_min) * (out_max - out_min));
    }

    public void buttonOnClickedMainToRoom(ActionEvent actionEvent) {
        Stage newStage = new Stage();
        Stage stage = (Stage)main_button_to_room.getScene().getWindow();
        try {
            Parent second = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("app-room-controller.fxml")));

            // 씬에 레이아웃 추가
            Scene sc = new Scene(second);
            newStage.setTitle("MY HOME : ROOM'S LIGHT CONTROL");
            newStage.setMaxWidth(800.0);
            newStage.setMaxHeight(600.0);
            newStage.setResizable(false);

            // 씬을 스테이지에서 상영
            newStage.setScene(sc);
            newStage.show();

            // 기존 페이지 숨김
            stage.hide();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void buttonOnClickedMainEnd(ActionEvent actionEvent) {
        Stage stage = (Stage)main_button_end.getScene().getWindow();
        stage.close();
        System.exit(0);
    }

    public void buttonOnClickedChangedTemperature(ActionEvent actionEvent) {
        if(this.socket.isConnected()){
            String change_desired_temp_text = desired_text_temperature.getText();
            if(!change_desired_temp_text.isEmpty()){
                try {
                    desired_temperature = Double.parseDouble(change_desired_temp_text);
                } catch (Exception e) {
                    // 경고창 생성
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("경고");
                    alert.setHeaderText(null);
                    alert.setContentText("잘못된 입력입니다.");

                    // OK 버튼 클릭 시에만 경고창 닫기
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    });
                }
                desired_label_temperature.setText(String.valueOf(desired_temperature));
                desired_text_temperature.setText("");
                myApp.setDesired_temperature(desired_temperature);
                myApp.writeSerialData();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("경고");
                alert.setHeaderText(null);
                alert.setContentText("잘못된 입력입니다.");

                // OK 버튼 클릭 시에만 경고창 닫기
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        alert.close();
                    }
                });
            }
        }
    }

    public void buttonOnClickedChangedHumidity(ActionEvent actionEvent) {
        if(this.socket.isConnected()){
            String change_desired_humid_text = desired_text_humidity.getText();
            if(!change_desired_humid_text.isEmpty()){
                try {
                    desired_humidity = Double.parseDouble(change_desired_humid_text);
                } catch (Exception e) {
                    // 경고창 생성
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("경고");
                    alert.setHeaderText(null);
                    alert.setContentText("잘못된 입력입니다.");

                    // OK 버튼 클릭 시에만 경고창 닫기
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    });
                }
                desired_label_humidity.setText(String.valueOf(desired_humidity));
                desired_text_humidity.setText("");
                myApp.setDesired_humidity(desired_humidity);
                myApp.writeSerialData();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("경고");
                alert.setHeaderText(null);
                alert.setContentText("잘못된 입력입니다.");

                // OK 버튼 클릭 시에만 경고창 닫기
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        alert.close();
                    }
                });
            }
        }
    }


}
