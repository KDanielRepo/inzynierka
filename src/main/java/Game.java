import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends Application {
    private Integer[][] gameMatrix = new Integer[4][4];
    private TextArea[][] gameArea = new TextArea[4][4];
    private Integer randomA, randomB, score;
    private TextField scoreBoard;
    private boolean up, right, down, left, game, paused, automation;
    private GridPane gameGrid;
    private VBox toolBox;
    private BrainController brainController;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        brainController = new BrainController();
        game = true;
        gameGrid = new GridPane();
        toolBox = new VBox();
        score = 0;
        setGameMatrix();
        setGameArea(gameGrid);
        random(2);
        updateGameArea();
        borderPane.setLeft(gameGrid);
        scene = new Scene(borderPane);
        scene.getRoot().requestFocus();
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                switch (event.getCode()) {
                    case W:
                        up = true;
                        break;
                    case D:
                        right = true;
                        break;
                    case S:
                        down = true;
                        break;
                    case A:
                        left = true;
                        break;
                }
                update();
            }
        });
        scene.setOnKeyReleased(new EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                switch (event.getCode()) {
                    case W:
                        up = false;
                        break;
                    case D:
                        right = false;
                        break;
                    case S:
                        down = false;
                        break;
                    case A:
                        left = false;
                        break;
                }
            }
        });

        scoreBoard = new TextField();
        scoreBoard.setEditable(false);

        Button pause = new Button("Pause");
        pause.setOnAction(e->{
            paused=!paused;
            if(paused){
                pause.setText("Start");
            }else{
                pause.setText("Pause");
            }
        });
        Button restart = new Button("Restart");
        restart.setOnAction(e->{
            restart();
        });
        Button automatic = new Button();
        automatic.setOnAction(e->{
            automation = !automation;
            if(automation){
                automatic.setText("Stop Brain");
            }else{
                automatic.setText("Start Brain");
            }
        });
        Button nextMove = new Button("next move");
        nextMove.setOnAction(e->{
            simulateKeyPress(brainController.generateMove());
        });


        toolBox.getChildren().addAll(pause,restart,nextMove,scoreBoard);

        borderPane.setRight(toolBox);

        primaryStage.setScene(scene);
        primaryStage.show();
        brainController.setCurrentInputs(gameMatrix);
    }

    public void update() {
        if(game && !paused) {
            if (left) {
                for (int k = 0; k < 4; k++) {
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 4; j++) {
                            if (gameMatrix[i][j].equals(gameMatrix[i + 1][j]) && (gameMatrix[i][j] != 0)) {
                                gameMatrix[i][j] = gameMatrix[i][j] * 2;
                                gameMatrix[i + 1][j] = 0;
                            }
                            if (gameMatrix[i][j] == 0) {
                                if (gameMatrix[i + 1][j] != 0) {
                                    gameMatrix[i][j] = gameMatrix[i + 1][j];
                                    gameMatrix[i + 1][j] = 0;
                                }
                            }
                        }
                    }
                }
            } else if (right) {
                for (int k = 0; k < 4; k++) {
                    for (int i = 3; i > 0; i--) {
                        for (int j = 0; j < 4; j++) {
                            if (gameMatrix[i][j].equals(gameMatrix[i - 1][j]) && (gameMatrix[i][j] != 0)) {
                                gameMatrix[i][j] = gameMatrix[i][j] * 2;
                                gameMatrix[i - 1][j] = 0;

                            }
                            if (gameMatrix[i][j] == 0) {
                                if (gameMatrix[i - 1][j] != 0) {
                                    gameMatrix[i][j] = gameMatrix[i - 1][j];
                                    gameMatrix[i - 1][j] = 0;

                                }
                            }
                        }
                    }
                }
            } else if (up) {
                for (int k = 0; k < 4; k++) {
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (gameMatrix[i][j].equals(gameMatrix[i][j + 1]) && (gameMatrix[i][j] != 0)) {
                                gameMatrix[i][j] = gameMatrix[i][j] * 2;
                                gameMatrix[i][j + 1] = 0;
                            }
                            if (gameMatrix[i][j] == 0) {
                                if (gameMatrix[i][j + 1] != 0) {
                                    gameMatrix[i][j] = gameMatrix[i][j + 1];
                                    gameMatrix[i][j + 1] = 0;
                                }
                            }
                        }
                    }
                }
            } else if (down) {
                for (int k = 0; k < 4; k++) {
                    for (int i = 0; i < 4; i++) {
                        for (int j = 3; j > 0; j--) {
                            if (gameMatrix[i][j].equals(gameMatrix[i][j - 1]) && (gameMatrix[i][j] != 0)) {
                                gameMatrix[i][j] = gameMatrix[i][j] * 2;
                                gameMatrix[i][j - 1] = 0;
                            }
                            if (gameMatrix[i][j] == 0) {
                                if (gameMatrix[i][j - 1] != 0) {
                                    gameMatrix[i][j] = gameMatrix[i][j - 1];
                                    gameMatrix[i][j - 1] = 0;
                                }
                            }
                        }
                    }
                }
            }
            random(1);
            updateGameArea();
            calculateScore();
            checkGameOver();
            brainController.setCurrentInputs(gameMatrix);
        }
    }

    public void checkGameOver(){

    }

    public void setGameMatrix() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                gameMatrix[i][j] = 0;
            }
        }
    }

    public void setGameArea(GridPane gridPane) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                gameArea[i][j] = new TextArea();
                gameArea[i][j].setPrefSize(150, 150);
                gameArea[i][j].setText("");
                gameArea[i][j].setEditable(false);
                gridPane.add(gameArea[i][j], i, j);
            }
        }
    }

    public void updateGameArea() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (gameMatrix[i][j] == 0) {
                    gameArea[i][j].setText("");
                } else {
                    gameArea[i][j].setText(Integer.toString(gameMatrix[i][j]));
                }
            }
        }
    }

    public void random(int times) {
        for (int k = 0; k < times; k++) {
            int iteration = 0;
            randomA = ThreadLocalRandom.current().nextInt(0, 4);
            randomB = ThreadLocalRandom.current().nextInt(0, 4);
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (gameMatrix[i][j] > 0) {
                        iteration++;
                    }
                }
            }
            if (iteration == 16) {
                return;
            }
            if (gameMatrix[randomA][randomB] != 0) {
                random(1);
            }
            int twoOrFour = ThreadLocalRandom.current().nextInt(0, 4);
            if (twoOrFour == 3) {
                gameMatrix[randomA][randomB] = 4;
            } else {
                gameMatrix[randomA][randomB] = 2;
            }
        }
    }

    public void calculateScore(){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                score+=gameMatrix[i][j];
            }
        }
        scoreBoard.setText(score.toString());
    }

    public void restart(){
        score = 0;
        setGameMatrix();
        updateGameArea();
        calculateScore();
    }

    public void simulateKeyPress(int i){
        try{
            Robot robot = new Robot();
            switch (i){
                case 0:
                    robot.keyPress(java.awt.event.KeyEvent.VK_W);
                    robot.delay(10);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_W);
                    break;
                case 1:
                    robot.keyPress(java.awt.event.KeyEvent.VK_D);
                    robot.delay(10);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_D);
                    break;
                case 2:
                    robot.keyPress(java.awt.event.KeyEvent.VK_S);
                    robot.delay(10);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_S);
                    break;
                case 3:
                    robot.keyPress(java.awt.event.KeyEvent.VK_A);
                    robot.delay(10);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_A);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void translateMove(){
        switch(brainController.generateMove()){
            case 0:
                up = true;
                break;
            case 1:
                left = true;
                break;
            case 2:
                down = true;
                break;
            case 3:
                right = true;
                break;
        }
    }

    public static void main(String[] args) {
        launch(Game.class, args);
    }
}
