import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.Objects;

import static java.lang.Math.*;

public class TimesTables extends Application {
    private double multiple;
    private int speed;
    private int numberOfPoints;
    private Path path;
    private int colorNumber;
    private Label currentMultiple;
    DecimalFormat df = new DecimalFormat("###.#");

    /*
    Method that creates two int arrays that determine the points around the circle, then
    draws the pattern using the coordinates
     */
    private void determinePoints (int points){
        int[] xPoints = new int[points];
        int[] yPoints = new int[points];

        for (int i = 0; i < points; i++){
            xPoints[i] = (int) (300 + 200 * -cos(Math.toRadians(i * 360 / points)));
            yPoints[i] = (int) (300 - 200 * sin(Math.toRadians(i * 360 / points)));
        }

        path.getElements().clear();

        for (int i = 0; i < points; i++){
            int j = (int) ((i * multiple)% points);
            path.getElements().add(new MoveTo(xPoints[i], yPoints[i]));
            path.getElements().add(new LineTo(xPoints[j], yPoints[j]));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Times Tables");

        //gridPane to store all controls
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        //canvas to store all animations
        Canvas canvas = new Canvas(700,600);

        //pane to store the canvas
        Pane root = new Pane(canvas);

        //vbox to store grid and canvas
        VBox firstPage = new VBox();

        //declare timer
        MyTimer timer = new MyTimer();

        //create circle for background for animations
        Circle circle = new Circle(300, 300, 200, Color.LIGHTCYAN);
        circle.setStroke(Color.BLACK);
        root.getChildren().add(circle);

        //create path for drawing lines, add it to the canvas
        path = new Path();
        colorNumber = 0;
        path.setStroke(Color.FORESTGREEN);
        root.getChildren().add(path);

        //labels for displaying the current speed, multiple, and number of points
        Label currentSpeed = new Label("Current Speed:");
        GridPane.setConstraints(currentSpeed, 0, 4);
        currentMultiple = new Label("Current Multiple:");
        GridPane.setConstraints(currentMultiple, 1, 4);
        Label currentPoints = new Label("Number of Points:");
        GridPane.setConstraints(currentPoints, 2, 4);

        //speed controls
        Label speedLabel = new Label("Speed (FPS) (From 1-20):");
        GridPane.setConstraints(speedLabel, 0, 0);
        TextField speedInput = new TextField();
        speedInput.setPromptText("speed");
        GridPane.setConstraints(speedInput, 1, 0);
        Button speedBtn = new Button("Set Speed");
        GridPane.setConstraints(speedBtn, 2, 0);
        speedBtn.setOnAction(e -> {
            speed = Integer.parseInt(speedInput.getText());
            currentSpeed.setText("Current Speed:" + " " + speed);
        });

        //multiple controls
        Label multipleLabel = new Label("Multiple (up to 360):");
        GridPane.setConstraints(multipleLabel, 0, 1);
        TextField multipleInput = new TextField();
        multipleInput.setPromptText("multiple");
        GridPane.setConstraints(multipleInput, 1, 1);
        Button multipleBtn = new Button("Set Multiple");
        GridPane.setConstraints(multipleBtn, 2, 1);
        multipleBtn.setOnAction(e -> {
            multiple = Double.parseDouble(multipleInput.getText());
            currentMultiple.setText("Current Multiple:" + " " + df.format(multiple));
        });

        //points controls
        Label pointsLabel = new Label("Number of points (up to 360):");
        GridPane.setConstraints(pointsLabel, 0, 2);
        TextField pointsInput = new TextField();
        GridPane.setConstraints(pointsInput, 1, 2);
        pointsInput.setPromptText("points");
        Button pointsBtn = new Button("Set Points");
        GridPane.setConstraints(pointsBtn, 2, 2);
        pointsBtn.setOnAction(e -> {
            numberOfPoints = Integer.parseInt(pointsInput.getText());
            currentPoints.setText("Number of Points:" + " " + numberOfPoints);
        });

        //favorites controls
        Label favoritesLabel = new Label("Favorite slides:");
        GridPane.setConstraints(favoritesLabel, 3, 0);
        ChoiceBox<String> favorites = new ChoiceBox<>();
        favorites.getItems().addAll("First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh",
                "Eighth", "Ninth", "Tenth");
        favorites.setValue("First");
        GridPane.setConstraints(favorites, 4, 0);
        Button favoritesBtn = new Button("Show");
        GridPane.setConstraints(favoritesBtn, 5, 0);
        favoritesBtn.setOnAction(e -> {
            getChoice(favorites);
            determinePoints(numberOfPoints);
        });

        //display button, play button, pause button, and clear button
        Button displayBtn = new Button("Display");
        GridPane.setConstraints(displayBtn, 0, 3);
        displayBtn.setOnAction(event -> {
            determinePoints(numberOfPoints);
            currentSpeed.setText("Current Speed:" + " " + speed);
            currentMultiple.setText("Current Multiple:" + " " + df.format(multiple));
            currentPoints.setText("Number of Points:" + " " + numberOfPoints);
        });
        Button playBtn = new Button("Play");
        GridPane.setConstraints(playBtn, 1, 3);
        playBtn.setOnAction(event -> timer.start());
        Button pauseBtn = new Button("Pause");
        GridPane.setConstraints(pauseBtn, 2, 3);
        pauseBtn.setOnAction(event -> timer.stop());
        Button clearBtn = new Button("Clear");
        GridPane.setConstraints(clearBtn, 3, 3);
        clearBtn.setOnAction(e -> path.getElements().clear());

        //populating the grid with all control elements
        grid.getChildren().addAll(speedLabel, speedInput, speedBtn, multipleLabel, multipleInput, multipleBtn,
                pointsLabel, pointsInput, pointsBtn, favoritesLabel, favorites, favoritesBtn,
                displayBtn, playBtn, pauseBtn, clearBtn, currentSpeed, currentMultiple, currentPoints);

        //generating stage and scene
        firstPage.getChildren().addAll(grid, root);
        Scene scene = new Scene(firstPage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //function for the animation timer
    private class MyTimer extends AnimationTimer {
        private long prevTime = 0;

        @Override
        public void handle(long now) {
            long dt = now - prevTime;
            if (dt > (1e9)/speed) {
                prevTime = now;
                determinePoints(numberOfPoints);
                changeColor(colorNumber);
                currentMultiple.setText("Current Multiple:" + " " + df.format(multiple));
                multiple = multiple + 0.1;
                colorNumber++;
            }
        }
    }

    //method to retrieve the choice from the dropdown menu for the favorites
    private void getChoice(ChoiceBox<String> favorites){
        String choice = favorites.getValue();
        if (Objects.equals(choice, "First")){
            multiple = 21;
            numberOfPoints = 200;
            path.setStroke(Color.FORESTGREEN);
        }
        if (Objects.equals(choice, "Second")){
            multiple = 2;
            numberOfPoints = 360;
            path.setStroke(Color.CRIMSON);
        }
        if (Objects.equals(choice, "Third")){
            multiple = 180;
            numberOfPoints = 360;
            path.setStroke(Color.DARKBLUE);
        }
        if (Objects.equals(choice, "Fourth")){
            multiple = 90;
            numberOfPoints = 360;
            path.setStroke(Color.DARKCYAN);
        }
        if (Objects.equals(choice, "Fifth")){
            multiple = 359;
            numberOfPoints = 360;
            path.setStroke(Color.DARKGOLDENROD);
        }
        if (Objects.equals(choice, "Sixth")){
            multiple = 45;
            numberOfPoints = 87;
            path.setStroke(Color.DARKGRAY);
        }
        if (Objects.equals(choice, "Seventh")){
            multiple = 50;
            numberOfPoints = 150;
            path.setStroke(Color.DARKGREEN);
        }
        if (Objects.equals(choice, "Eighth")){
            multiple = 51;
            numberOfPoints = 150;
            path.setStroke(Color.DARKGREY);
        }
        if (Objects.equals(choice, "Ninth")){
            multiple = 51;
            numberOfPoints = 300;
            path.setStroke(Color.DARKKHAKI);
        }
        if (Objects.equals(choice, "Tenth")){
            multiple = 7;
            numberOfPoints = 313;
            path.setStroke(Color.DARKTURQUOISE);
        }
    }

    //method for color cycling on the line patterns
    private void changeColor(int x){
        if (x%10 == 0){
            path.setStroke(Color.FORESTGREEN);
        }
        if (x%10 == 1){
            path.setStroke(Color.CRIMSON);
        }
        if (x%10 == 2){
            path.setStroke(Color.DARKBLUE);
        }
        if (x%10 == 3){
            path.setStroke(Color.DARKCYAN);
        }
        if (x%10 == 4){
            path.setStroke(Color.DARKGOLDENROD);
        }
        if (x%10 == 5){
            path.setStroke(Color.DARKGRAY);
        }
        if (x%10 == 6){
            path.setStroke(Color.DARKGREEN);
        }
        if (x%10 == 7){
            path.setStroke(Color.DARKGREY);
        }
        if (x%10 == 8){
            path.setStroke(Color.DARKKHAKI);
        }
        if (x%10 == 9){
            path.setStroke(Color.DARKTURQUOISE);
        }
    }

}
