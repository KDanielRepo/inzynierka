import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.concurrent.ThreadLocalRandom;

public class Test extends Application {
    Scene scene;
    @Override
    public void start(Stage primaryStage) throws Exception {
        scene = new Scene(new BorderPane());
        scene.setOnKeyPressed(e->{
            switch (e.getCode()){
                case D:
                    System.out.println("nacislem R");
                    break;
                case A:
                    System.out.println("nacislem L");
                    break;
                case S:
                    System.out.println("nacislem D");
                    break;
                case W:
                    System.out.println("nacislem U");
                    break;
            }
        });
        scene.setOnKeyReleased(e->{
            switch (e.getCode()){
                case D:
                    System.out.println("puscilem R");
                    break;
                case A:
                    System.out.println("puscilem L");
                    break;
                case S:
                    System.out.println("puscilem D");
                    break;
                case W:
                    System.out.println("puscilem U");
                    break;
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
        test();
        test();
        test();
    }
    public void test(){
        Integer a = ThreadLocalRandom.current().nextInt(0,4);
        boolean up,left,down,right;
        up = false;
        left = false;
        down = false;
        right = false;
        switch (a){
            case 0:
                up = true;
                break;
            case 1:
                right = true;
                break;
            case 2:
                down = true;
                break;
            case 3:
                left = true;
                break;
        }
        if(up){
            KeyEvent ke = new KeyEvent(KeyEvent.KEY_PRESSED,
                    "w", "",
                    KeyCode.W, false, false, false, false);
            KeyEvent.fireEvent(scene,ke);
        }
        if(left){
            KeyEvent ke = new KeyEvent(KeyEvent.KEY_PRESSED,
                    "a", "",
                    KeyCode.A, false, false, false, false);
            KeyEvent.fireEvent(scene,ke);
        }
        if(right){
            KeyEvent ke = new KeyEvent(KeyEvent.KEY_PRESSED,
                    "d", "",
                    KeyCode.D, false, false, false, false);
            KeyEvent.fireEvent(scene,ke);
        }
        if(down){
            KeyEvent ke = new KeyEvent(KeyEvent.KEY_PRESSED,
                    "s", "",
                    KeyCode.S, false, false, false, false);
            KeyEvent.fireEvent(scene,ke);
        }
        up = false;
        left = false;
        down = false;
        right = false;
    }

    public static void main(String[] args) {
        launch(Test.class,args);
    }
}
