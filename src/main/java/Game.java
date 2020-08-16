import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
    private TextArea textArea;
    private int moves = 0;
    private int tries = 0;
    private boolean multipleTries;
    private boolean visual;

    //vizualizacja
    private GraphicsContext gc;
    private Stage neuralNetworkStage;
    private Canvas canvas;

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
        Button visualButton = new Button("Visual");
        visualButton.setOnAction(e->{
            visual = !visual;
            if(!visual){
                visualButton.setText("Visual");
            }else{
                visualButton.setText("No visual");
            }
        });
        Button multipleTriesButton = new Button("Single Try");
        multipleTriesButton.setOnAction(e->{
            multipleTries=!multipleTries;
            if(multipleTries){
                multipleTriesButton.setText("Multiple Tries");
            }else{
                multipleTriesButton.setText("Single Try");
            }
        });

        toolBox.getChildren().addAll(pause, restart, nextMove, automatic, scoreBoard, timerLabel, timer, timerButton,visualButton,multipleTriesButton);
        borderPane.setRight(toolBox);

        primaryStage.setScene(scene);
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.show();
        visualizeNeuralNetwork();
    }

    public void visualizeNeuralNetwork(){
        neuralNetworkStage = new Stage();
        neuralNetworkStage.setMinWidth(600);
        neuralNetworkStage.setMinHeight(600);

        BorderPane borderPane = new BorderPane();
        Scene neuralNetworkScene = new Scene(borderPane);
        neuralNetworkStage.setScene(neuralNetworkScene);
        neuralNetworkStage.setX(300);
        neuralNetworkStage.setY(300);
        neuralNetworkStage.show();

        canvas = new Canvas(1600,800);
        gc = canvas.getGraphicsContext2D();
        borderPane.setCenter(canvas);
    }
    public void updateNeuralNetworkVisualization(){
        if(neuralNetworkStage!=null){
            gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2f);

            int divisionSize = brainController.getBrain().getPerceptronMap().get(0).stream().findFirst().get().size();
            int layer = brainController.getBrain().getPerceptronMap().size();
            int neuronWidth = (int) canvas.getHeight()/divisionSize;
            int neuronHeight = (int) canvas.getHeight()/divisionSize;

            for (int i = 0; i < divisionSize; i++) {
                for (int k = 0; k < divisionSize; k++) {
                    //zaczyna sie przy kazdym wejsciu albo neuronie
                    //leci do kazdego nastepnego neuronu
                    // i to warstwa w sieci, j to numer danego neuronu w danej sieci
                    gc.setStroke(Color.RED);
                    gc.strokeLine(canvas.getWidth()/layer*0+neuronWidth/2,canvas.getHeight()/divisionSize*i+neuronHeight/2,canvas.getWidth()/layer*1-neuronWidth,canvas.getHeight()/divisionSize*k+neuronHeight/2);
                }
            }

            for (int i = 1; i < layer+1; i++) {
                int layerSize = brainController.getBrain().getPerceptronMap().get(i-1).stream().findFirst().get().size();
                for (int j = 0; j < layerSize; j++) {
                    if(i==1){
                        gc.strokeText(gameMatrix[j/4][j%4].toString(),canvas.getWidth()/layer*(i-1),canvas.getHeight()/divisionSize*j+neuronHeight/2);
                    }
                    for (int k = 0; k < layerSize; k++) {
                        int layerSizeNext = layerSize;
                        if(i<layer) {
                            layerSizeNext = brainController.getBrain().getPerceptronMap().get(i).stream().findFirst().get().size();
                        }
                        //zaczyna sie przy kazdym wejsciu albo neuronie
                        //leci do kazdego nastepnego neuronu
                        // i to warstwa w sieci, j to numer danego neuronu w danej sieci
                        gc.setStroke(Color.RED);
                        if(k<layerSizeNext)
                        gc.strokeLine(canvas.getWidth()/layer*(i)+neuronWidth,canvas.getHeight()/divisionSize*j+neuronHeight/2,canvas.getWidth()/layer*(i+1)-neuronWidth,canvas.getHeight()/divisionSize*k+neuronHeight/2);
                    }
                    gc.setStroke(Color.BLACK);
                    gc.strokeOval(canvas.getWidth()/layer*i-neuronWidth,canvas.getHeight()/divisionSize*j,neuronWidth,neuronHeight);
                    gc.strokeText(brainController.getBrain().getPerceptronMap().get(i-1).stream().findFirst().get().get(j).stream().findFirst().get().getOutput().toString(),canvas.getWidth()/layer*i-neuronWidth,canvas.getHeight()/divisionSize*j+neuronHeight/2);
                    //System.out.println("input: "+brainController.getBrain().getPerceptronMap().get(i-1).stream().findFirst().get().get(j).stream().findFirst().get().getInput(0).toString());
                    //System.out.println(brainController.getBrain().getPerceptronMap().get(i-1).stream().findFirst().get().get(j).stream().findFirst().get().getOutput().toString());
                }
                //System.out.println("---------------------");
            }
        }
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
                if(!visual){
                    mutex.lock();
                }
            }
            if (left) {
                for (int k = 0; k < 4; k++) {
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 4; j++) {
                            if (gameMatrix[i][j].equals(gameMatrix[i + 1][j]) && (gameMatrix[i][j] != 0)) {
                                gameMatrix[i][j] = gameMatrix[i][j] * 2;
                                score+=gameMatrix[i][j];
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
                                score+=gameMatrix[i][j];
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
                                score+=gameMatrix[i][j];
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
                                score+=gameMatrix[i][j];
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
                moves++;
                random(1);
                if(!visual){
                    updateGameArea();
                }

                checkGameOver();
                if (automation) {
                    brainController.setCurrentInputs(gameMatrix);
                    brainController.getBlocks().clear();
                }
                brainController.setCurrentInputs(gameMatrix);
                //values();
                moved = false;
            } else if (!moved && automation) {
                brainController.addBlock(brainController.getCurrentMove());
                if(!visual){
                    mutex.unlock(delay);
                }
            }
        } else if (!game) {
            tries++;
            System.out.println(score);
            restart();
            //index++;
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
                genetics.createOffspring();
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
        updateNeuralNetworkVisualization();
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
                if(gameMatrix[i][j]!=0){
                    int test = gameMatrix[i][j];
                    int test2 = 0;
                    while(test !=1){
                        test2++;
                        test = test/2;
                    }
                    score += gameMatrix[i][j] * test2;
                }
                /*if (j + 1 < 4) {
                    if ((gameMatrix[i][j]/2 == gameMatrix[i][j+1])) {
                        score += gameMatrix[i][j] * gameMatrix[i][j + 1];
                    }
                }
                if (j - 1 > 0) {
                    if ((gameMatrix[i][j] * 2 == gameMatrix[i][j - 1])) {
                        score += gameMatrix[i][j] * gameMatrix[i][j - 1];
                    }
                }
                if (i + 1 < 4) {
                    if ((gameMatrix[i][j]/2 == gameMatrix[i+1][j])) {
                        score += gameMatrix[i + 1][j] * gameMatrix[i][j];
                    }
                }
                if (i - 1 > 0) {
                    if ((gameMatrix[i][j] * 2 == gameMatrix[i-1][j])) {
                        score += gameMatrix[i - 1][j] * gameMatrix[i][j];
                    }
                }*/

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
        if(multipleTries){
            if(tries<10 && automation){
                brainController.getBrain().setScore(brainController.getBrain().getScore()+score);
                brainController.getBrain().setLp(index);
                brainController.getBrain().setMoves(brainController.getBrain().getMoves()+moves);
            }
            if (!groupset && tries>=10) {
                brainController.getBrain().setScore((brainController.getBrain().getScore()+score)/10);
                brainController.getBrain().setLp(index);
                brainController.getBrain().setMoves((brainController.getBrain().getMoves()+moves)/10);
                index++;
                genetics.getGenePool().add(brainController.getBrain());
                Brain brain = new Brain();
                brain.createDefaultPerceptronMap();
                brainController.setBrain(brain);
                tries = 0;
            } else if(groupset && tries >=10){
                brainController.getBrain().setScore((brainController.getBrain().getScore()+score)/10);
                brainController.getBrain().setLp(index);
                brainController.getBrain().setMoves((brainController.getBrain().getMoves()+moves)/10);
                brainController.setBrain(genetics.getGenePool().get(index));
                index++;
                tries = 0;
            }
        }else{
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
            }
            index++;
        }
        System.out.println("index: "+index);
        brainController.getBlocks().clear();
        if (index == genetics.getPopulation()) {
            for (int i = 0; i < genetics.getGenePool().size(); i++) {
                System.out.println(genetics.getGenePool().get(i).getScore());
            }
        }
        moves = 0;
        score = 0;
        game = true;
        setGameMatrix();
        if(!visual){
            updateGameArea();
        }

        random(2);
        if(!visual){
            updateGameArea();
        }
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
