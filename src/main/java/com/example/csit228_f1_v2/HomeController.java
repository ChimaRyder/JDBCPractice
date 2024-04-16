package com.example.csit228_f1_v2;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;

public class HomeController {

    public ToggleButton tbNight;
    public ProgressIndicator piProgress;
    public Slider slSlider;
    public ProgressBar pbProgress;

    public void onSliderChange() {
        double val = slSlider.getValue();
        System.out.println(val);
        piProgress.setProgress(val/100);
        pbProgress.setProgress(val/100);
        if (val == 100) {
            System.exit(0);
        }
    }

    public void onNightModeClick() {
        if (tbNight.isSelected()) {
            tbNight.getParent().setStyle("-fx-background-color: BLACK");
            tbNight.setText("DAY");
        } else {
            tbNight.getParent().setStyle("-fx-background-color: WHITE");
            tbNight.setText("NIGHT");
        }
    }
}
