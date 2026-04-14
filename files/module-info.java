module producerconsumer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens ui to javafx.fxml;
    opens MyThreads to javafx.fxml;
    opens MySemaphores to javafx.fxml;
}
