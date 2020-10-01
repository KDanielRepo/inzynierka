import com.google.common.collect.Iterables;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GameView extends Application {
    private GameInstance[] instances;
    private BrainExporter brainExporter;
    private Genetics genetics;
    private Mutex mutex;

    private Integer delay;
    private Integer index;
    private Integer generationIndex;
    private boolean paused;

    private BrainController brainController;
    private Integer[][] gameMatrix;
    private VBox toolbox;
    private ComboBox instanceSelector;
    private TextField instanceNumber;

    private Canvas gameArea;
    private GraphicsContext gameAreaGC;
    private int squareSize;

    //wizualizacja
    private GraphicsContext gc;
    private Stage neuralNetworkStage;
    private Canvas canvas;

    //debug menu
    private Stage debugStage;
    private GraphicsContext debugGc;
    private Canvas debugCanvas;
    private Canvas generalInfo;
    private GraphicsContext generalInfoGC;
    private boolean allFinished;
    private int finishedInstances;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //OBIEKTY
        genetics = new Genetics();
        brainExporter = new BrainExporter();
        mutex = new Mutex();
        genetics.setMutex(mutex);
        index = 0;
        generationIndex = 0;
        delay = 100;
        //

        //WIDOK
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(900);
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);

        gameArea = new Canvas(800,800);
        gameAreaGC = gameArea.getGraphicsContext2D();
        squareSize = (int) gameArea.getWidth() / 4;


        Label instanceNumberLabel = new Label("Podaj ilość instancji");
        instanceNumber = new TextField();
        Button createInstancesButton = new Button("Create instances");
        createInstancesButton.setOnAction(e -> {
            createInstances();
            addInstanceSelection();
        });
        Button startInstancesButton = new Button("Start instances");
        startInstancesButton.setOnAction(e->{
            Arrays.stream(instances).forEach(i->{
                i.start();
            });
        });
        Button pauseButton = new Button("Pause all instances");
        pauseButton.setOnAction(e->{
            setPaused(!paused);
        });
        Label delayLabel = new Label("Set delay for all instances");
        TextField delayField = new TextField();
        Button delayButton = new Button("Set delay");
        delayButton.setOnAction(e->{
            try{
                setDelay(Integer.parseInt(delayField.getText()));
            }catch (Exception ex){
                System.out.println("Podano niepoprawna wartość opóźnienia");
            }
        });
        Button noInstanceButton = new Button("No instance visuals");
        noInstanceButton.setOnAction(e->{
            for (int i = 0; i < instances.length; i++) {
                instances[i].setSelectedAsView(false);
            }
        });
        Button exportBrains = new Button("Export current brains");
        exportBrains.setOnAction(e->{
            for (int i = 0; i < genetics.getGenePool().size(); i++) {
                try {
                    brainExporter.exportBrainToCsf(new File("brainGenes"+i),genetics.getGenePool().get(i));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        Button importBrains = new Button("Import into current brains");
        importBrains.setOnAction(e->{
            for (int i = 0; i < genetics.getGenePool().size(); i++) {
                try {
                    brainExporter.importBrainFromCsf(new File("brainGenes"+i),genetics.getGenePool().get(i));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        toolbox = new VBox();
        toolbox.getChildren().addAll(instanceNumberLabel, instanceNumber, createInstancesButton,startInstancesButton,noInstanceButton,delayLabel,delayField,delayButton,pauseButton,exportBrains,importBrains);
        borderPane.setCenter(gameArea);
        borderPane.setRight(toolbox);

        primaryStage.setScene(scene);
        primaryStage.show();
        //

    }

    public void showSelectedInstance(int instance){
        brainController = instances[instance].getBrainController();
        gameMatrix = instances[instance].getGameMatrix();
        updateGameArea();
    }

    public void createInstances() {
        instances = new GameInstance[Integer.parseInt(instanceNumber.getText())];
        for (int i = 0; i < instances.length; i++) {
            GameInstance instance = new GameInstance();
            instance.setTries(0);
            instance.setGenetics(genetics);
            instance.setGameView(this);
            instances[i] = instance;
        }
    }

    public void addInstanceSelection() {
        if (!toolbox.getChildren().contains(instanceSelector)) {
            List<String> names = new ArrayList<>();
            for (int i = 0; i < instances.length; i++) {
                names.add(instances[i].getName());
            }
            ObservableList<GameInstance> list = FXCollections.observableArrayList(instances);
            instanceSelector = new ComboBox(list);
            Button setSelectedButton = new Button("select instance");
            setSelectedButton.setOnAction(e -> {
                for (int i = 0; i < instances.length; i++) {
                    instances[i].setSelectedAsView(false);
                }
                instances[instanceSelector.getSelectionModel().getSelectedIndex()].setSelectedAsView(true);
                showSelectedInstance(instanceSelector.getSelectionModel().getSelectedIndex());
            });
            toolbox.getChildren().addAll(instanceSelector, setSelectedButton);
        }
    }

    public void visualizeNeuralNetwork() {
        neuralNetworkStage = new Stage();
        neuralNetworkStage.setTitle("Visualization");

        BorderPane borderPane = new BorderPane();
        Scene neuralNetworkScene = new Scene(borderPane);
        neuralNetworkStage.setScene(neuralNetworkScene);
        neuralNetworkStage.setX(300);
        neuralNetworkStage.setY(300);
        neuralNetworkStage.show();

        canvas = new Canvas(1600, 800);
        gc = canvas.getGraphicsContext2D();
        borderPane.setCenter(canvas);
    }

    public void updateNeuralNetworkVisualization() {
        if (neuralNetworkStage != null) {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2f);

            int divisionSize = brainController.getBrain().getPerceptronMap().get(0).stream().findFirst().get().size();
            int layer = brainController.getBrain().getPerceptronMap().size();
            int neuronWidth = (int) canvas.getHeight() / divisionSize;
            int neuronHeight = (int) canvas.getHeight() / divisionSize;

            for (int i = 0; i < divisionSize; i++) {
                for (int k = 0; k < divisionSize; k++) {
                    //zaczyna sie przy kazdym wejsciu albo neuronie
                    //leci do kazdego nastepnego neuronu
                    // i to warstwa w sieci, j to numer danego neuronu w danej sieci
                    gc.setStroke(Color.RED);
                    gc.strokeLine(canvas.getWidth() / layer * 0 + neuronWidth / 2, canvas.getHeight() / divisionSize * i + neuronHeight / 2, canvas.getWidth() / layer * 1 - neuronWidth, canvas.getHeight() / divisionSize * k + neuronHeight / 2);
                }
            }

            for (int i = 1; i < layer + 1; i++) {
                int layerSize = brainController.getBrain().getPerceptronMap().get(i - 1).stream().findFirst().get().size();
                for (int j = 0; j < layerSize; j++) {
                    if (i == 1) {
                        gc.strokeText(gameMatrix[j / 4][j % 4].toString(), canvas.getWidth() / layer * (i - 1), canvas.getHeight() / divisionSize * j + neuronHeight / 2);
                    }
                    for (int k = 0; k < layerSize; k++) {
                        int layerSizeNext = layerSize;
                        if (i < layer) {
                            layerSizeNext = brainController.getBrain().getPerceptronMap().get(i).stream().findFirst().get().size();
                        }
                        //zaczyna sie przy kazdym wejsciu albo neuronie
                        //leci do kazdego nastepnego neuronu
                        // i to warstwa w sieci, j to numer danego neuronu w danej sieci
                        gc.setStroke(Color.RED);
                        if (k < layerSizeNext)
                            gc.strokeLine(canvas.getWidth() / layer * (i) + neuronWidth, canvas.getHeight() / divisionSize * j + neuronHeight / 2, canvas.getWidth() / layer * (i + 1) - neuronWidth, canvas.getHeight() / divisionSize * k + neuronHeight / 2);
                    }
                    gc.setStroke(Color.BLACK);
                    gc.strokeOval(canvas.getWidth() / layer * i - neuronWidth, canvas.getHeight() / divisionSize * j, neuronWidth, neuronHeight);
                    gc.strokeText(brainController.getBrain().getPerceptronMap().get(i - 1).stream().findFirst().get().get(j).stream().findFirst().get().getOutput().toString().substring(0, 4), canvas.getWidth() / layer * i - neuronWidth, canvas.getHeight() / divisionSize * j + neuronHeight / 2);
                    //System.out.println("input: "+brainController.getBrain().getPerceptronMap().get(i-1).stream().findFirst().get().get(j).stream().findFirst().get().getInput(0).toString());
                    //System.out.println(brainController.getBrain().getPerceptronMap().get(i-1).stream().findFirst().get().get(j).stream().findFirst().get().getOutput().toString());
                }
                //System.out.println("---------------------");
            }
        }
    }

    public void debugMenu() {
        debugStage = new Stage();
        debugCanvas = new Canvas(300, 600);
        debugGc = debugCanvas.getGraphicsContext2D();
        debugGc.setStroke(Color.BLACK);
        debugGc.setLineWidth(1f);

        generalInfo = new Canvas(200, 600);
        generalInfoGC = generalInfo.getGraphicsContext2D();

        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);

        HBox hBox = new HBox();
        RadioButton[] radioButtons = new RadioButton[brainController.getBrain().getPerceptronMap().size()];
        ToggleGroup group = new ToggleGroup();
        for (int i = 0; i < radioButtons.length; i++) {
            RadioButton radioButton = new RadioButton("layer " + i);
            radioButton.setToggleGroup(group);
            hBox.getChildren().add(radioButton);
            radioButtons[i] = radioButton;
        }
        Button showSelectedWeights = new Button("show selected weights");
        showSelectedWeights.setOnAction(e -> {
            clearDebugMenuCanvas();
            for (int i = 0; i < radioButtons.length; i++) {
                if (radioButtons[i].isSelected()) {
                    int finalI = i;
                    AtomicInteger index = new AtomicInteger();
                    brainController.getBrain().getPerceptronMap().get(i).stream().findFirst().get().values().forEach(o -> {
                        debugGc.strokeText("Waga nr. " + index + " to: " + o.getWeights().get(index.get()).toString().substring(0, 4), debugCanvas.getWidth() / 2, debugCanvas.getHeight() / brainController.getBrain().getPerceptronMap().get(finalI).stream().findFirst().get().values().size() * index.get() + debugGc.getFont().getSize());
                        index.getAndIncrement();
                    });
                }
            }
        });

        VBox toolbox = new VBox();
        toolbox.getChildren().addAll(hBox, showSelectedWeights);
        borderPane.setRight(toolbox);
        borderPane.setCenter(debugCanvas);
        borderPane.setLeft(generalInfo);
        debugStage.setScene(scene);
        debugStage.setTitle("Debug menu");
        debugStage.show();
    }

    public void clearDebugMenuCanvas() {
        debugGc.clearRect(0, 0, debugCanvas.getWidth(), debugCanvas.getHeight());
        debugGc.setStroke(Color.BLACK);
        debugGc.setLineWidth(1f);
    }

    public void updateGeneralInfo() {
        generalInfoGC.clearRect(0, 0, generalInfo.getWidth(), generalInfo.getHeight());
        generalInfoGC.setStroke(Color.BLACK);
        generalInfoGC.setLineWidth(1f);
        double height = generalInfoGC.getFont().getSize();
        generalInfoGC.strokeText("Lista bloków: ", 0, height);
        for (int i = 0; i < brainController.getBlocks().size(); i++) {
            generalInfoGC.strokeText(brainController.getBlocks().get(i).toString(), 0, height * (2 + i));
        }
        generalInfoGC.strokeText("Wartości na wyjściach: ", 0, height * 7);
        for (int i = 0; i < brainController.getBrain().getOutputLayer().size(); i++) {
            String pre = "";
            switch (i) {
                case 0:
                    pre = "góra: ";
                    break;
                case 1:
                    pre = "prawo: ";
                    break;
                case 2:
                    pre = "dół: ";
                    break;
                case 3:
                    pre = "lewo: ";
                    break;
            }
            String middle = Iterables.get(brainController.getBrain().getOutputLayer().values(), i).toString();
            generalInfoGC.strokeText(pre + middle, 0, height * (8 + i));
        }
    }

    public void updateGameArea() {
        gameAreaGC.clearRect(0,0,gameArea.getWidth(),gameArea.getHeight());
        for (int i = 0; i < 5; i++) {
            gameAreaGC.setLineWidth(5f);
            gameAreaGC.setFill(Color.BLACK);
            gameAreaGC.strokeLine(i*squareSize,0,i*squareSize,gameArea.getHeight());
            gameAreaGC.strokeLine(0,i*squareSize,gameArea.getWidth(),i*squareSize);
            for (int j = 0; j < 4; j++) {
                if(i<4){
                    if (gameMatrix[i][j] != 0) {
                        gameAreaGC.setLineWidth(2f);
                        gameAreaGC.setFill(colorFromScore(gameMatrix[i][j]));
                        gameAreaGC.fillRect((i * squareSize)+2, (j * squareSize)+2, squareSize-4, squareSize-4);
                        gameAreaGC.setFill(Color.BLACK);
                        gameAreaGC.strokeText(gameMatrix[i][j].toString(), (i * squareSize)+squareSize/2, (j * squareSize)+squareSize/2);
                    }
                }
            }
        }
    }

    public Color colorFromScore(int score) {
        if (score != 0) {
            switch (score) {
                case 2:
                    return Color.LIGHTCYAN;
                case 4:
                    return Color.CYAN;
                case 8:
                    return Color.LIGHTBLUE;
                case 16:
                    return Color.LIGHTGREEN;
                case 32:
                    return Color.LIGHTPINK;
                case 64:
                    return Color.LIGHTSALMON;
                case 128:
                    return Color.LIGHTYELLOW;
                case 256:
                    return Color.RED;
                case 512:
                    return Color.DARKRED;
                case 1024:
                    return Color.ORANGE;
                case 2048:
                    return Color.DARKORANGE;
                case 4096:
                    return Color.LIGHTGREY;
                default:
                    return Color.GRAY;
            }
        }
        return Color.WHITE;
    }

    public void startGenetics() {
        if (getIndex() - instances.length == genetics.getPopulation() && isAllFinished()) {
            System.out.println("----------------------------");
            System.out.println("Koniec generacji :" + getGenerationIndex());
            genetics.getAverageFitness();
            genetics.calculateGlobalFitness();
            genetics.calculateRFitness();
            System.out.println(genetics.getGenePool().size());
            genetics.createOffspringCorrect();
            genetics.resetPcPool();
            System.out.println(ZonedDateTime.now());
            setIndex(0);
            setGenerationIndex(getGenerationIndex() + 1);
            genetics.setCurrentGeneration(getGenerationIndex());
            setFinishedInstances(0);
            System.out.println("---------------------------");
        }
    }


    public static void main(String[] args) {
        launch(GameView.class, args);
    }

    public Integer getGenerationIndex() {
        try {
            mutex.lock();
            return generationIndex;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
        return 0;
    }

    public void setGenerationIndex(Integer generationIndex) {
        try {
            mutex.lock();
            this.generationIndex = generationIndex;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
    }

    public Integer getIndex() {
        try {
            if (index > genetics.getPopulation() / 2) {
                genetics.setGenerated(false);
            }
            mutex.lock();
            return index;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
        return 0;
    }

    public void setIndex(Integer index) {
        try {
            mutex.lock();
            this.index = index;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public boolean isPaused() {
        try {
            mutex.lock();
            return paused;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
        return false;
    }

    public void setPaused(boolean paused) {
        try {
            mutex.lock();
            this.paused = paused;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
    }

    public boolean isAllFinished() {
        try {
            if (getFinishedInstances() == instances.length) {
                mutex.lock();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
        return false;
    }

    public int getFinishedInstances() {
        try {
            mutex.lock();
            return finishedInstances;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
        return 0;
    }

    public void setFinishedInstances(int finishedInstances) {
        try {
            mutex.lock();
            this.finishedInstances = finishedInstances;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mutex.unlock();
        }
    }
}
