package com.example.oop_wsiiz;

import com.example.oop_wsiiz.models.Movie;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.*;

public class CinemaManagement extends Application {
    private final ObservableList<Movie> movies = FXCollections.observableArrayList();
    private Connection dbConnection;

    public static void main() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:database.db");
            refreshMovies();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        primaryStage.setTitle("Cinema Management");

        // Create a ListView to display the movies
        ListView<Movie> movieListView = new ListView<>(movies);
        movieListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Create a TextArea to show movie details
        TextArea movieDetailsTextArea = new TextArea();
        movieDetailsTextArea.setEditable(false);

        // Show movie details when a movie is selected in the ListView
        movieListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String sb = "Title: " + newValue.title() + "\n" +
                        "Director: " + newValue.director() + "\n" +
                        "Release Year: " + newValue.releaseYear() + "\n" +
                        "Start Time: " + newValue.startTime() + "\n" +
                        "Duration: " + newValue.duration() + "\n" +
                        "Ticket Price: " + newValue.ticketPrice() + "\n" +
                        "Room ID: " + newValue.roomId() + "\n";
                movieDetailsTextArea.setText(sb);
            } else {
                movieDetailsTextArea.setText("");
            }
        });

        // Add the ListView and TextArea to the BorderPane
        BorderPane root = new BorderPane();
        root.setLeft(movieListView);
        root.setCenter(movieDetailsTextArea);
        root.setPadding(new Insets(10));

        // Create an HBox for the add and remove buttons
        HBox buttons = new HBox(10);
        buttons.setPadding(new Insets(10));
        root.setBottom(buttons);

        // Create an "Add" button and add it to the HBox
        Button addButton = new Button("Add");
        addButton.setOnAction(event -> addMovie());

        buttons.getChildren().add(addButton);

        // Create a "Remove" button and add it to the HBox
        Button removeButton = new Button("Remove");
        removeButton.setOnAction(event -> {
            Movie selectedMovie = movieListView.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                movies.remove(selectedMovie);
                removeMovie(selectedMovie.id());
            }
        });
        buttons.getChildren().add(removeButton);

        // Set the scene and show the stage
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    void refreshMovies() {
        try {
            Statement statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM cinema_schedule");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String movieName = resultSet.getString("movie_name");
                String director = resultSet.getString("director");
                int releaseYear = resultSet.getInt("release_year");
                String startTime = resultSet.getString("start_time");
                int duration = resultSet.getInt("duration");
                double ticketPrice = resultSet.getDouble("ticket_price");
                int roomId = resultSet.getInt("room_id");
                // Add the movie to the observable list
                movies.add(new Movie(id, movieName, director, releaseYear, startTime, duration, ticketPrice, roomId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void addMovie() {
        Stage newStage = new Stage();
        newStage.setTitle("Add Schedule");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        //movie name input field
        Label nameLabel = new Label("Movie Name:");
        TextField nameInput = new TextField();
        grid.add(nameLabel, 0, 0);
        grid.add(nameInput, 1, 0);

        // start time input field
        Label startTimeLabel = new Label("Start Time:");
        TextField startTimeInput = new TextField();
        grid.add(startTimeLabel, 0, 1);
        grid.add(startTimeInput, 1, 1);

        // duration input field
        Label durationLabel = new Label("Duration:");
        TextField durationInput = new TextField();
        grid.add(durationLabel, 0, 2);
        grid.add(durationInput, 1, 2);

        // ticket price input field
        Label ticketPriceLabel = new Label("Ticket Price:");
        TextField ticketPriceInput = new TextField();
        grid.add(ticketPriceLabel, 0, 3);
        grid.add(ticketPriceInput, 1, 3);

        //create a toggle group for the radio buttons
        Label roomLabel = new Label("Room:");
        ToggleGroup roomGroup = new ToggleGroup();
        HBox roomHbox = new HBox();
        roomHbox.setSpacing(10);

        try {
            //query to get the room data from the database
            ResultSet rooms = dbConnection.createStatement().executeQuery("SELECT id FROM cinema_rooms");

            while (rooms.next()) {
                //create a radio button for each room
                RadioButton roomButton = new RadioButton(rooms.getString("id"));
                roomButton.setUserData(rooms.getInt("id"));
                roomButton.setToggleGroup(roomGroup);
                roomHbox.getChildren().add(roomButton);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        grid.add(roomLabel, 0, 4);
        grid.add(roomHbox, 1, 4);

        //add director input
        Label directorLabel = new Label("Director:");
        TextField directorInput = new TextField();
        grid.add(directorLabel, 0, 5);
        grid.add(directorInput, 1, 5);

        //add release year input
        Label releaseYearLabel = new Label("Release year:");
        TextField releaseYearInput = new TextField();
        grid.add(releaseYearLabel, 0, 6);
        grid.add(releaseYearInput, 1, 6);

        //add button
        Button insertButton = new Button("Insert");
        grid.add(insertButton, 1, 7);

        insertButton.setOnAction(e -> {
            try {
                //Insert the data into the schedule table
                PreparedStatement insertSchedule = dbConnection.prepareStatement(
                        "INSERT INTO cinema_schedule " +
                                "(movie_name, start_time, duration, ticket_price, room_id, director, release_year) " +
                                "VALUES (?,?,?,?,?, ?, ?)"
                );
                insertSchedule.setString(1, nameInput.getText());
                insertSchedule.setString(2, startTimeInput.getText());
                insertSchedule.setString(3, durationInput.getText());
                insertSchedule.setDouble(4, Double.parseDouble(ticketPriceInput.getText()));
                insertSchedule.setInt(5, ((int) roomGroup.getSelectedToggle().getUserData()));
                insertSchedule.setString(6, directorInput.getText());
                insertSchedule.setInt(7, Integer.parseInt(releaseYearInput.getText()));
                insertSchedule.executeUpdate();

                //Refresh the observable list of movies
                movies.clear();
                refreshMovies();

                // Close the new window
                newStage.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        newStage.setScene(new Scene(grid, 300, 300));
        newStage.show();
    }

    private void removeMovie(int id) {
        try  {
            PreparedStatement statement = dbConnection.prepareStatement("DELETE FROM cinema_schedule WHERE id=?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        refreshMovies();
    }
}

