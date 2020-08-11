import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends Application {
    private Integer[][] gameMatrix = new Integer[4][4];
    private TextArea[][] gameArea = new TextArea[4][4];
    private Integer randomA, randomB, score;
    private TextField scoreBoard;
    private boolean up, right, down, left, game, paused, automation, moved;
    private GridPane gameGrid;
    private VBox toolBox;
    private BrainController brainController;
    private Scene scene;
    private Mutex mutex;
    private Mutex keyMutex;
    private TextField timer;
    private Integer delay;
    private Robot robot;
    private Genetics genetics;
    private int index = 0;
    private int generationIndex = 0;
    private boolean groupset;

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        brainController = new BrainController();
        genetics = new Genetics();
        robot = new Robot();
        mutex = new Mutex();
        keyMutex = new Mutex();
        gameGrid = new GridPane();
        toolBox = new VBox();
        timer = new TextField();

        delay = 100;
        game = true;
        score = 0;

        setGameMatrix();
        brainController.setCurrentInputs(gameMatrix);
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
        pause.setOnAction(e -> {
            paused = !paused;
            if (paused) {
                pause.setText("Start");
            } else {
                pause.setText("Pause");
            }
        });
        Button restart = new Button("Restart");
        restart.setOnAction(e -> {
            restart();
        });
        Button automatic = new Button("Start brain");
        automatic.setOnAction(e -> {
            automation = !automation;
            if (automation) {
                automatic.setText("Stop Brain");
            } else {
                automatic.setText("Start Brain");
            }
        });
        Button nextMove = new Button("next move");
        nextMove.setOnAction(e -> {
            simulateKeyPress(brainController.generateMove());
        });
        Label timerLabel = new Label("Podaj delay pomiedzy ruchami:");
        Button timerButton = new Button("Set delay");
        timerButton.setOnAction(e -> {
            try {
                delay = Integer.parseInt(timer.getText());
            } catch (Exception ex) {
                System.out.println("Podaj poprawną wartość opóźnienia");
            }
        });

        toolBox.getChildren().addAll(pause, restart, nextMove, automatic, scoreBoard, timerLabel, timer, timerButton);
        borderPane.setRight(toolBox);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void update() {
        if (game && !paused) {
            if (!mutex.isLocked() && automation) {
                if (brainController.isNotBlocked()) {
                    brainController.setCurrentMove(brainController.generateMove());
                } else {
                    brainController.setCurrentMove(brainController.generateMoveWithoutBlocks());
                }
                simulateKeyPress(brainController.getCurrentMove());
                mutex.lock();
            }
            if (left) {
                for (int k = 0; k < 4; k++) {
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 4; j++) {
                            if (gameMatrix[i][j].equals(gameMatrix[i + 1][j]) && (gameMatrix[i][j] != 0)) {
                                gameMatrix[i][j] = gameMatrix[i][j] * 2;
                                gameMatrix[i + 1][j] = 0;
                                moved = true;
                            }
                            if (gameMatrix[i][j] == 0) {
                                if (gameMatrix[i + 1][j] != 0) {
                                    gameMatrix[i][j] = gameMatrix[i + 1][j];
                                    gameMatrix[i + 1][j] = 0;
                                    moved = true;
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
                                moved = true;
                            }
                            if (gameMatrix[i][j] == 0) {
                                if (gameMatrix[i - 1][j] != 0) {
                                    gameMatrix[i][j] = gameMatrix[i - 1][j];
                                    gameMatrix[i - 1][j] = 0;
                                    moved = true;
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
                                moved = true;
                            }
                            if (gameMatrix[i][j] == 0) {
                                if (gameMatrix[i][j + 1] != 0) {
                                    gameMatrix[i][j] = gameMatrix[i][j + 1];
                                    gameMatrix[i][j + 1] = 0;
                                    moved = true;
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
                                moved = true;
                            }
                            if (gameMatrix[i][j] == 0) {
                                if (gameMatrix[i][j - 1] != 0) {
                                    gameMatrix[i][j] = gameMatrix[i][j - 1];
                                    gameMatrix[i][j - 1] = 0;
                                    moved = true;
                                }
                            }
                        }
                    }
                }
            }
            if (moved) {
                random(1);
                updateGameArea();
                calculateScore();
                checkGameOver();
                if (automation) {
                    brainController.setCurrentInputs(gameMatrix);
                    brainController.getBlocks().clear();
                }
                //brainController.setCurrentInputs(gameMatrix);
                //values();
                moved = false;
            } else if (!moved && automation) {
                brainController.addBlock(brainController.getCurrentMove());
                mutex.unlock(delay);
            }
        } else if (!game) {
            System.out.println(score);
            restart();
            index++;
            if (index < genetics.getPopulation()) {
                update();
            } else {
                groupset = true;
                //before();
                System.out.println("generacja: " + generationIndex);
                System.out.println("Sredni fitness to: ");
                genetics.getAverageFitness();
                genetics.calculateGlobalFitness();
                genetics.calculateRFitness();
                //genetics.getFittest();
                genetics.createOffspringNew();
                genetics.resetPcPool();
                //after();
                index = 0;
                generationIndex++;
                System.out.println("---------------------------");
                if (generationIndex <= genetics.getGeneration()) {
                    update();
                } else {
                    for (int i = 0; i < genetics.getGenePool().size(); i++) {
                        System.out.println(genetics.getGenePool().get(i).getScore());
                    }

                }
            }
        }
    }

    public void before() {
        for (int i = 0; i < genetics.getGenePool().size(); i++) {
            System.out.println("Przed, nr: " + i);
            genetics.getGenePool().get(i).getPerceptronMap().values().stream().forEach(e -> {
                e.values().stream().forEach(o -> {
                    System.out.println(o.getWeights());
                });
            });
            System.out.println("-----------------------------------------");
        }
    }

    public void after() {
        for (int i = 0; i < genetics.getGenePool().size(); i++) {
            System.out.println("Po, nr: " + i);
            genetics.getGenePool().get(i).getPerceptronMap().values().stream().forEach(e -> {
                e.values().stream().forEach(o -> {
                    System.out.println(o.getWeights());
                });
            });
            System.out.println("-----------------------------------------");
        }
    }

    public void values() {
        brainController.getBrain().getPerceptronMap().values().stream().forEach(e -> {
            e.values().stream().forEach(o -> {
                System.out.println(o.getInputs());
            });
        });
    }

    public void checkGameOver() {
        int test = 0;
        int test2 = 0;
        int test3 = 0;
        int test4 = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (gameMatrix[i][j] != gameMatrix[i + 1][j] && (gameMatrix[i][j] > 0) && (gameMatrix[i + 1][j] > 0)) {
                    test++;
                }
            }
        }
        for (int i = 3; i > 0; i--) {
            for (int j = 0; j < 4; j++) {
                if (gameMatrix[i][j] != gameMatrix[i - 1][j] && (gameMatrix[i][j] > 0) && (gameMatrix[i - 1][j] > 0)) {
                    test2++;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameMatrix[i][j] != gameMatrix[i][j + 1] && (gameMatrix[i][j] > 0) && (gameMatrix[i][j + 1] > 0)) {
                    test3++;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 3; j > 0; j--) {
                if (gameMatrix[i][j] != gameMatrix[i][j - 1] && (gameMatrix[i][j] > 0) && (gameMatrix[i][j - 1] > 0)) {
                    test4++;
                }
            }
        }
        boolean noUp = test == 12;
        boolean noRight = test2 == 12;
        boolean noDown = test3 == 12;
        boolean noLeft = test4 == 12;
        if (noUp && noRight && noDown && noLeft) {
            game = false;
        }
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
        if (automation) {
            mutex.unlock(delay);
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

    public void calculateScore() {
        score = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (j + 1 < 4) {
                    if ((gameMatrix[i][j + 1] == gameMatrix[i][j] / 2)) {
                        score += gameMatrix[i][j] * gameMatrix[i][j + 1];
                    }
                }
                if (j - 1 > 0) {
                    if ((gameMatrix[i][j] * 2 == gameMatrix[i][j - 1])) {
                        score += gameMatrix[i][j] * gameMatrix[i][j - 1];
                    }
                }
                if (i + 1 < 4) {
                    if ((gameMatrix[i + 1][j] == gameMatrix[i][j] / 2)) {
                        score += gameMatrix[i + 1][j] * gameMatrix[i][j];
                    }
                }
                if (i - 1 > 0) {
                    if ((gameMatrix[i - 1][j] == gameMatrix[i][j] * 2)) {
                        score += gameMatrix[i - 1][j] * gameMatrix[i][j];
                    }
                }
                score += gameMatrix[i][j];
                /*if (i - 1 > 0 && i + 1 < 4 && j - 1 > 0 && j + 1 < 4) {//srodek
                    score += ((gameMatrix[i][j])+1) * ((gameMatrix[i-1][j])+1) * ((gameMatrix[i+1][j])+1) * ((gameMatrix[i][j-1])+1) * ((gameMatrix[i][j+1])+1);
                } else if (i - 1 < 0 && i + 1 < 4 && j - 1 < 0 && j + 1 < 4) {//lewy gorny
                    score += ((gameMatrix[i][j])+1) * ((gameMatrix[i+1][j])+1) * ((gameMatrix[i][j+1])+1);
                } else if (i - 1 < 0 && i + 1 < 4 && j - 1 > 0 && j + 1 > 4) {//prawy gorny
                    score += ((gameMatrix[i][j])+1) * ((gameMatrix[i+1][j])+1) * ((gameMatrix[i][j-1])+1);
                } else if (i - 1 > 0 && i + 1 > 4 && j - 1 < 0 && j + 1 < 4) {//lewy dolny
                    score += ((gameMatrix[i][j])+1) * ((gameMatrix[i-1][j])+1) * ((gameMatrix[i][j+1])+1);
                } else if (i - 1 > 0 && i + 1 > 4 && j - 1 > 0 && j + 1 > 4) {//prawy dolny
                    score += ((gameMatrix[i][j])+1) * ((gameMatrix[i-1][j])+1) * ((gameMatrix[i][j-1])+1);
                } else if (i - 1 < 0 && i + 1 < 4 && j - 1 > 0 && j + 1 < 4) {//brak gory
                    score += ((gameMatrix[i][j])+1) * ((gameMatrix[i+1][j])+1) * ((gameMatrix[i][j-1])+1) * ((gameMatrix[i][j+1])+1);
                } else if (i - 1 > 0 && i + 1 > 4 && j - 1 > 0 && j + 1 < 4) {//brak dolu
                    score += ((gameMatrix[i][j])+1) * ((gameMatrix[i-1][j])+1) * ((gameMatrix[i][j-1])+1) * ((gameMatrix[i][j+1])+1);
                } else if (i - 1 > 0 && i + 1 < 4 && j - 1 < 0 && j + 1 < 4) {//brak lewego
                    score += ((gameMatrix[i][j])+1) * ((gameMatrix[i-1][j])+1) * ((gameMatrix[i+1][j])+1) * ((gameMatrix[i][j+1])+1);
                } else if (i - 1 > 0 && i + 1 < 4 && j - 1 > 0 && j + 1 > 4) {//brak prawego
                    score += ((gameMatrix[i][j])+1) * ((gameMatrix[i-1][j])+1) * ((gameMatrix[i+1][j])+1) * ((gameMatrix[i][j-1])+1);
                }*/
            }
        }
        scoreBoard.setText(score.toString());
    }

    public void restart() {
        if (automation) {
            brainController.getBrain().setScore(score);
            brainController.getBrain().setLp(index);
            if (!groupset) {
                genetics.getGenePool().add(brainController.getBrain());
                Brain brain = new Brain();
                brain.createDefaultPerceptronMap();
                brainController.setBrain(brain);
            } else {
                brainController.setBrain(genetics.getGenePool().get(index));
            }
            brainController.getBlocks().clear();
            if (index == genetics.getPopulation()) {
                for (int i = 0; i < genetics.getGenePool().size(); i++) {
                    System.out.println(genetics.getGenePool().get(i).getScore());
                }
            }
        }
        score = 0;
        game = true;
        setGameMatrix();
        updateGameArea();
        calculateScore();
        random(2);
        updateGameArea();
    }

    public void simulateKeyPress(int i) {
        try {
            switch (i) {
                case 0:
                    robot.keyPress(java.awt.event.KeyEvent.VK_W);
                    robot.delay(delay);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_W);
                    break;
                case 1:
                    robot.keyPress(java.awt.event.KeyEvent.VK_D);
                    robot.delay(delay);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_D);
                    break;
                case 2:
                    robot.keyPress(java.awt.event.KeyEvent.VK_S);
                    robot.delay(delay);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_S);
                    break;
                case 3:
                    robot.keyPress(java.awt.event.KeyEvent.VK_A);
                    robot.delay(delay);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_A);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(Game.class, args);
    }
}
