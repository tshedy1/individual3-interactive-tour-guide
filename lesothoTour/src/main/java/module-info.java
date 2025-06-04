module proxxy.lesothoTour {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.web;
    requires jdk.jsobject;

    opens proxxy.lesothoTour to javafx.fxml;
    exports proxxy.lesothoTour;
}