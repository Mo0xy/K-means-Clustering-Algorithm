module com.example.graficdemo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.graficdemo to javafx.fxml;
    exports com.example.graficdemo;
}