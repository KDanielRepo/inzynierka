import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DateTimeStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TableViewHelper extends TableView {
    /*List<Being> list = new ArrayList<>();
    ObservableList<Being> listt;
    TableColumn lp;
    TableColumn gen;
    TableColumn score;
    TableColumn moves;
    TableColumn amount;
    Integer a = 0;
    //lp,score,ilosc ruchow,
    public TableViewHelper() {
        gen = new TableColumn("Generation");
        lp = new TableColumn("Lp");
        score = new TableColumn("Score");
        amount = new TableColumn("Amount of moves");
        moves = new TableColumn("Moves");
        this.getColumns().addAll(gen,lp,score,moves,amount);
    }
    public void addRow(Being b){
        list.add(b);
        a++;
        gen.setCellValueFactory(new PropertyValueFactory<Being,Integer>("gen"));
        lp.setCellValueFactory(new PropertyValueFactory<TableViewHelper,Integer>("lp"));
        score.setCellValueFactory(new PropertyValueFactory<Being,String>("score"));
        moves.setCellValueFactory(new PropertyValueFactory<Being,String>("moves"));
        amount.setCellValueFactory(new PropertyValueFactory<Being,String>("amount"));
        listt = FXCollections.observableList(list);
        this.setItems(listt);
    }

    public TableViewHelper(final ObservableList<?> list) throws IllegalAccessException {
        Field[] fields = list.get(0).getClass().getDeclaredFields();
        for (final Field f : fields) {
                f.setAccessible(true);
                Object o = f.get(list.get(0));
                TableColumn a = new TableColumn(f.getName());
                if(o instanceof Integer){
                    a.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
                }else if(o instanceof Date){
                    a.setCellFactory(TextFieldTableCell.forTableColumn(new DateTimeStringConverter()));
                }else if(o instanceof Float){
                    a.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
                }else{
                    a.setCellFactory(TextFieldTableCell.forTableColumn());
                }
                a.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent event) {
                        try {
                            Class<?> clazz = list.get(0).getClass();
                            Object obj = list.get(TableViewHelper.this.getSelectionModel().getSelectedIndex());
                            Method get = list.get(0).getClass().getDeclaredMethod("get"+f.getName().substring(0,1).toUpperCase()+f.getName().substring(1,f.getName().length()));
                            Class<?> t = get.getReturnType();
                            Object ins;
                            if(t.getName().equals("java.lang.Float")){
                                Constructor tCons = t.getConstructor(new Class[]{float.class});
                                Object[] arg = new Object[]{new Float(0)};
                                ins = tCons.newInstance(arg);
                            }else if(t.getName().equals("java.lang.Integer")){
                                Constructor tCons = t.getConstructor(new Class[]{int.class});
                                Object[] arg = new Object[]{new Integer(0)};
                                ins = tCons.newInstance(arg);
                            }else if(t.getName().equals("java.sql.Time")){
                                Constructor tCons = t.getConstructor(new Class[]{long.class});
                                Object[] arg = new Object[]{new Long(0)};
                                ins = tCons.newInstance(arg);
                            }else{
                                ins = t.newInstance();
                            }

                            if(ins instanceof String){
                                Method set = list.get(0).getClass().getDeclaredMethod("set"+f.getName().substring(0,1).toUpperCase()+f.getName().substring(1,f.getName().length()),String.class);
                                set.invoke(obj,event.getNewValue());
                            }else if(ins instanceof Float){
                                Method set = list.get(0).getClass().getDeclaredMethod("set"+f.getName().substring(0,1).toUpperCase()+f.getName().substring(1,f.getName().length()),Float.class);
                                set.invoke(obj,event.getNewValue());
                            }else if(ins instanceof Integer){
                                Method set = list.get(0).getClass().getDeclaredMethod("set"+f.getName().substring(0,1).toUpperCase()+f.getName().substring(1,f.getName().length()),Integer.class);
                                set.invoke(obj,event.getNewValue());
                            }else {
                                Method set = list.get(0).getClass().getDeclaredMethod("set"+f.getName().substring(0,1).toUpperCase()+f.getName().substring(1,f.getName().length()), Date.class);
                                set.invoke(obj,event.getNewValue());
                            }
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                });
                f.setAccessible(true);
                Object name = f.getName();
                a.setCellValueFactory(new PropertyValueFactory<Object, String>(name.toString()));
                a.setEditable(true);
                this.getColumns().add(a);
        }
        this.setEditable(true);
    }*/

}
