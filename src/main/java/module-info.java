module com.example.oop_wsiiz {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.example.oop_wsiiz to javafx.fxml;
    exports com.example.oop_wsiiz;
    exports com.example.oop_wsiiz.models;
    opens com.example.oop_wsiiz.models to javafx.fxml;
}