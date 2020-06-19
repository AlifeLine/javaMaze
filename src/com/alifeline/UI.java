package com.alifeline;
/**
 * @Description:
 * @auther: AlifeLine
 * @date: 8:59 2020/6/19
 * @param:
 * @return:
 */

import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

class AliOss {
/**
 *
 * @Description:
 * 获取阿里云oss的对象
 * @auther: AlifeLine
 * @date: 17:29 2020/6/18
 * @param: [origin]
 * @return: java.lang.String
 *
 */
    @SuppressWarnings("restriction")
    private static String base64Encode(byte[] origin) {
        if (null == origin) {
            return null;
        }
        return new sun.misc.BASE64Encoder().encode(origin).replace("\n", "").replace("\r", "");
    }
    /**
     *
     * @Description:
     * 使用HmacSHA1加密方式生成签名数据
     * @auther: AlifeLine
     * @date: 17:30 2020/6/18
     * @param: [key, data]
     * @return: java.lang.String
     *
     */
    private static String getSignature(byte[] key, byte[] data) {
        SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
        try {
            Mac mac;
            mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data);
            return base64Encode(rawHmac);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getUrl(){
        String Authorization;
        String AccessKeyId = "LTAI4G3rt5CrzzRPSGJ9AtFT";
        String AccessKeySecret = "p48tiFIxZUowfZSaiZpl2Z8FgjrBXt";
        String timeMap=System.currentTimeMillis()+3600+"";
        String sign = getSignature(AccessKeySecret.getBytes(),
                ("GET\n\n\n"+timeMap+"\n/ftp123/6.jpg").getBytes());
        Authorization = "?OSSAccessKeyId="+AccessKeyId+"&Expires="+timeMap+"&Signature="+ URLEncoder.encode(sign);
        return "https://ftp123.oss-accelerate.aliyuncs.com/6.jpg"+Authorization;
    }
}


public class UI extends Application {

    private boolean hasMaze = false;

    private Button genMaze = new Button("生成迷宫");
    private Button findWays = new Button("寻找路径");
    private Button traverseMaze = new Button("遍历迷宫");
    private Button bestWay = new Button("最短路径");

    private Circle circle;
    private double SIZE;

    private PathTransition pt;
    private SequentialTransition sequentialTransition;
    private SequentialTransition sequentialTransition1;
    private SequentialTransition sequentialTransition2;

    private ArrayList<Integer> arrayList;
    private ArrayList<Integer> arrayList1;
    private ArrayList<Integer> arrayList2;
    private ArrayList<Integer> arrayList3;
    private ArrayList<Integer> arrayList5;
    private ArrayList<Integer> arrayList6;
    private Line line;
    public Canvas canvas;
    private Queue queue;
    private Stack stack;
    private Maze maze;
    private Pane pane;

    private Text text = new Text("迷宫尺寸: ");
    private ComboBox<String> cbo = new ComboBox<>();
    private Text text1 = new Text("光标移动速度: ");
    private ComboBox<String> cbo1 = new ComboBox<>();
    private String stackString = "";
    public static double LineSize=20;
    public static void main(String[] args) {
        launch(args);
    }

    public Animation createPathAnimation(Path path, Duration duration,Color color) {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (duration.lessThan(Duration.seconds(0.1))){
            duration=Duration.seconds(0.1);
        }
        // move a node along a path. we want its position
        Circle pen = new Circle(0, 0, 4);

        // create path transition
        PathTransition pathTransition = new PathTransition(duration,path ,pen);
        pathTransition.currentTimeProperty().addListener( new ChangeListener<Duration>() {
            UI.Location oldLocation = null;

            /**
             * Draw a line from the old location to the new location
             */
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {

                // skip starting at 0/0
                if( oldValue == Duration.ZERO)
                    return;

                // get current location
                double x = pen.getTranslateX();
                double y = pen.getTranslateY();

                // initialize the location
                if( oldLocation == null) {
                    oldLocation = new UI.Location();
                    oldLocation.x = x;
                    oldLocation.y = y;
                    return;
                }

                // draw line
                gc.setStroke(color);
                gc.setLineCap(StrokeLineCap.ROUND);
                gc.setLineWidth(SIZE / 4);
                gc.strokeLine(oldLocation.x, oldLocation.y, x, y);
                gc.setLineJoin( StrokeLineJoin.ROUND );

                // update old location with current one
                oldLocation.x = x;
                oldLocation.y = y;
            }
        });

        return pathTransition;
    }
    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));

        HBox hBox = new HBox(genMaze, findWays, traverseMaze, bestWay);
        findWays.setDisable(true);
        traverseMaze.setDisable(true);
        bestWay.setDisable(true);
        hBox.setPadding(new Insets(5));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        borderPane.setBottom(hBox);


        VBox vBox = new VBox();
        vBox.setPadding(new Insets(0, 30, 10, 10));


        cbo.getItems().addAll(Settings.getSIZE());

        HBox hbo1 = new HBox();
        hbo1.setAlignment(Pos.TOP_RIGHT);
        hbo1.setPadding(new Insets(10, 10, 10, 30));
        hbo1.setSpacing(4.45);
        hbo1.getChildren().addAll(text, cbo, text1, cbo1);
        TextArea textArea = new TextArea();

        textArea.setText("");
        //大小
        textArea.setFont(Font.font(16));
        //允许自动换行
        textArea.setWrapText(false);
        //初始化设置行数
        textArea.setPrefRowCount(10);
        textArea.setEditable(false);

        //设置宽高
        textArea.setPrefWidth(100);
        textArea.setPrefHeight(200);
        textArea.setStyle(
                "-fx-background-color: transparent;"
        );
        borderPane.setRight(textArea);
        borderPane.setTop(hbo1);
        try {
            //防止用户端没有网络
            borderPane.setStyle(
                    "-fx-background-image: url(" +
                            AliOss.getUrl() +
                            "); " +
                            "-fx-background-size: cover;"+
                            "-fx-background-color: transparent;"
            );
        } catch (Exception exception) {
            System.out.println("No NetWork");
        }
        Scene scene = new Scene(borderPane,1100,850);
        primaryStage.setTitle("Java课程设计");
        primaryStage.setScene(scene);
        primaryStage.show();


        genMaze.setOnAction(event -> {
            int a = 0, b = 0;
            try {
                for (int i = 0; i < Settings.getSIZE().length; i++) {
                    if (Settings.getSIZE()[i].compareTo(cbo.getValue()) == 0) {
                        a = i;
                        break;
                    }
                }
            } catch (Exception ex) {
                a = 0;
            }
            Settings.setSIZE(a);

            if (!hasMaze) {
                genMaze.setText("重置迷宫");
                findWays.setDisable(false);
                traverseMaze.setDisable(true);
                bestWay.setDisable(true);
                maze = new Maze(Settings.SIZE, Settings.SIZE);
                Settings.TIME=Settings.SIZE*2;
                LineSize=700 / (maze.maze.length*6);
                if(LineSize<1){
                    LineSize=1;
                }else if (LineSize>20){
                    LineSize=20;
                }
                System.out.println(LineSize);
                pane = maze.getPane();
                borderPane.setCenter(pane);
                hasMaze = true;
            } else {
                genMaze.setText("生成迷宫");
                findWays.setDisable(true);
                traverseMaze.setDisable(true);
                bestWay.setDisable(true);
                pane.getChildren().clear();
                hasMaze = false;
            }
        });

        findWays.setOnAction(event -> { //点击了寻找路径按钮后
            stackString = "栈内容\n";
            //获取当前时间用于计算程序运行时间
            long start = System.currentTimeMillis();
            //将按钮设置为不可用
            findWays.setDisable(true);
            //创建栈和队列
            stack = new Stack();
            queue = new Queue();

            //创建两个列表用于
            arrayList = new ArrayList<>();
            arrayList1 = new ArrayList<>();

            //将入口压入栈中
            stack.push(maze.maze[maze.startY][maze.startX]);

            int x=0, y=0;
            //定于单面墙的大小
            SIZE = 700 / maze.maze.length;
            canvas=new Canvas(700,700);
            pane.getChildren().add(canvas);
            //创建一个圆用于路径的导航显示
            circle = new Circle(SIZE / 2, SIZE / 2, SIZE / 2);
            //设置圆的颜色
            circle.setFill(Color.rgb(5, 255, 0));
            //将圆放入pane中
//            pane.getChildren().add(circle);

            // 设置导航路径线
            sequentialTransition = new SequentialTransition();
            // create path transition
            pt = new PathTransition();

            pt.setDuration(Duration.millis(Settings.TIME));
            pt.setPath(new Line(0, 0, circle.getRadius(), circle.getRadius()));
            pt.setNode(circle);
            pt.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
            sequentialTransition.getChildren().add(pt);

            int[] arr = null;
            //开始寻找路径
            while (!(stack.peek().y == Settings.SIZE-1 && stack.peek().x == Settings.SIZE-1 )) { //判断是否到达指定出口

                x = stack.peek().x;
                y = stack.peek().y;

                queue.push(maze.maze[y][x]);

                arrayList.add(y);
                arrayList1.add(x);

                System.out.println(y + " " + x);
                stackString += "(" + y + "," + x + ")" + "\n";
                textArea.setText(stackString);
                arr = stack.peek().getDirection();
                if (arr != null) {
                    switch (arr[(int) (Math.random() * arr.length)]) {
                        case 2:
                            maze.maze[y][x].last.add(2);
                            stack.push(maze.maze[y - 1][x]);
                            maze.maze[y - 1][x].last.add(8);
                            break;
                        case 4:
                            maze.maze[y][x].last.add(4);
                            stack.push(maze.maze[y][x - 1]);
                            maze.maze[y][x - 1].last.add(6);
                            break;
                        case 6:
                            maze.maze[y][x].last.add(6);
                            stack.push(maze.maze[y][x + 1]);
                            maze.maze[y][x + 1].last.add(4);
                            break;
                        case 8:
                            maze.maze[y][x].last.add(8);
                            stack.push(maze.maze[y + 1][x]);
                            maze.maze[y + 1][x].last.add(2);
                            break;
                    }
                } else {
                    queue.popnew();
                    queue.popnew();
                    stack.pop();
                }
            }
            long time = System.currentTimeMillis() - start;
            System.out.println(time + " ms");
            if (x>=maze.row-1){
                arrayList.add(y+1);
                arrayList1.add(x);
                stackString += "(" + x + "," + (y+1) + ")" + "\n";
            }else {
                arrayList.add(y);
                arrayList1.add(x+1);
                stackString += "(" + (x+1) + "," + y + ")" + "\n";
            }
            stackString += "耗时" + time + " ms";
            textArea.setText(stackString);
//            moveCircle(arrayList, arrayList1, sequentialTransition, true, Color.BLUE);
//            sequentialTransition.play();
            Path path= getPath(arrayList,arrayList1,false);
            Animation animation=createPathAnimation(path, Duration.seconds(Settings.TIME),Color.BLUE);
            animation.play();
            traverseMaze.setDisable(false);
        });

        traverseMaze.setOnAction(event -> {
            traverseMaze.setDisable(true);
            long start = System.currentTimeMillis();
            int x, y;
            int[] arr;
            arrayList2 = new ArrayList<>();
            arrayList3 = new ArrayList<>();

            sequentialTransition1 = new SequentialTransition();
            stackString = "栈内容\n";
            while (!stack.isEmpty()) {
                x = stack.peek().x;
                y = stack.peek().y;
                System.out.println(y + " " + x);
                stackString += "(" + y + "," + x + ")" + "\n";
                textArea.setText(stackString);
                arrayList2.add(y);
                arrayList3.add(x);

                arr = stack.peek().getDirection();
                if (arr != null) {
                    switch (arr[(int) (Math.random() * arr.length)]) {
                        case 2:
                            maze.maze[y][x].last.add(2);
                            stack.push(maze.maze[y - 1][x]);
                            maze.maze[y - 1][x].last.add(8);
                            break;
                        case 4:
                            maze.maze[y][x].last.add(4);
                            stack.push(maze.maze[y][x - 1]);
                            maze.maze[y][x - 1].last.add(6);
                            break;
                        case 6:
                            maze.maze[y][x].last.add(6);
                            stack.push(maze.maze[y][x + 1]);
                            maze.maze[y][x + 1].last.add(4);
                            break;
                        case 8:
                            maze.maze[y][x].last.add(8);
                            stack.push(maze.maze[y + 1][x]);
                            maze.maze[y + 1][x].last.add(2);
                            break;
                    }
                } else {
                    stack.pop();
                }
            }
//            moveCircle(arrayList2, arrayList3, sequentialTransition1, false, Color.ORANGE);
//            sequentialTransition1.play();
            Path path= getPath(arrayList2,arrayList3,true);
            Animation animation=createPathAnimation(path, Duration.seconds(Settings.TIME),Color.ORANGE);
            animation.play();
            long time = System.currentTimeMillis() - start;
            stackString += "耗时" + time + " ms";
            textArea.setText(stackString);
            bestWay.setDisable(false);

        });

        bestWay.setOnAction(event -> {
            bestWay.setDisable(true);
            long start = System.currentTimeMillis();
            sequentialTransition2 = new SequentialTransition();

            arrayList5 = new ArrayList<>();
            arrayList6 = new ArrayList<>();
            stackString = "栈内容\n";
            Integer y=0;
            Integer x=0;
            while (!queue.isEmpty()) {
                y = queue.peek().y;
                x = queue.pop().x;
                arrayList5.add(y);
                arrayList6.add(x);
                stackString += "(" + y + "," + x + ")" + "\n";
                textArea.setText(stackString);
            }
            long time = System.currentTimeMillis() - start;
            if (x>=maze.row-1){
                arrayList5.add(y+1);
                arrayList6.add(x);
                stackString += "(" + x + "," + (y+1) + ")" + "\n";
            }else {
                arrayList5.add(y);
                arrayList6.add(x+1);
                stackString += "(" + (x+1) + "," + y + ")" + "\n";
            }
            stackString += "(" + (x+1) + "," + (y+1) + ")" + "\n";
            stackString += "耗时" + time + " ms";
            textArea.setText(stackString);
            //moveCircle(arrayList5, arrayList6, sequentialTransition2, true, Color.GREEN);
            sequentialTransition2.play();
            Path path= getPath(arrayList5,arrayList6,false);
            Animation animation=createPathAnimation(path, Duration.seconds(Settings.TIME),Color.GREEN);
            animation.play();


        });
    }
    public static class Location {
        double x;
        double y;
    }
    public Path getPath(ArrayList<Integer> arrayList, ArrayList<Integer> arrayList1,boolean flag) {
        Path path=new Path();
        int[] arr = new int[arrayList.size()];
        int[] arr1 = new int[arrayList1.size()];
        double size=SIZE;
        if (flag){
            for (int i = 0,k=arr.length-1; i < arr.length; i++,k--) {
                arr[i] = arrayList.get(k);
            }
            for (int i = 0,k=arr.length-1; i < arr.length; i++,k--) {
                arr1[i] = arrayList1.get(k);
            }


        }else {
            for (int i = 0, k = arr.length - 1; i < arr.length; i++, k--) {
                arr[i] = arrayList.get(i);
            }
            for (int i = 0, k = arr.length - 1; i < arr.length; i++, k--) {
                arr1[i] = arrayList1.get(i);
            }
        }
        int x, y;

        path.getElements().add(new MoveTo(arr[0]*size+size/2,arr1[0]*size+size/2));

        for (int i = 0; i < arr.length; i++) {
            y = arr[i];
            x = arr1[i];
            path.getElements().add(new LineTo((x)*size+size/2, (y)*size+size/2));
        }
        return path;
    }

    private void moveCircle(ArrayList<Integer> arrayList, ArrayList<Integer> arrayList1, SequentialTransition sequentialTransition, boolean bool, Color color) {

        int[] arr = new int[arrayList.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arrayList.get(i);
        }
        int[] arr1 = new int[arrayList1.size()];
        for (int i = 0; i < arr.length; i++) {
            arr1[i] = arrayList1.get(i);
        }

        int x, y;
        int x1 = 0, y1 = 0;
        int temp;
        for (int i = 0; i < arr.length - 1; i++) {
            pt = new PathTransition();
            pt.setDuration(Duration.millis(Settings.TIME));
            pt.setNode(circle);

            y = arr[i];
            x = arr1[i];
            y1 = arr[i + 1];
            x1 = arr1[i + 1];
            if (x1 - x > 0)
                temp = 6;
            else if (x1 - x < 0)
                temp = 4;
            else if (y1 - y > 0)
                temp = 8;
            else
                temp = 2;

            switch (temp) {
                case 2:
                    pt.setPath(line = new Line((x * 2 + 1) * circle.getRadius(), (y * 2 + 1) * circle.getRadius(), (x * 2 + 1) * circle.getRadius(), (y * 2 - 1) * circle.getRadius()));
                    break;
                case 4:
                    pt.setPath(line = new Line((x * 2 + 1) * circle.getRadius(), (y * 2 + 1) * circle.getRadius(), (x * 2 - 1) * circle.getRadius(), (y * 2 + 1) * circle.getRadius()));
                    break;
                case 6:
                    pt.setPath(line = new Line((x * 2 + 1) * circle.getRadius(), (y * 2 + 1) * circle.getRadius(), (x * 2 + 3) * circle.getRadius(), (y * 2 + 1) * circle.getRadius()));
                    break;
                case 8:
                    pt.setPath(line = new Line((x * 2 + 1) * circle.getRadius(), (y * 2 + 1) * circle.getRadius(), (x * 2 + 1) * circle.getRadius(), (y * 2 + 3) * circle.getRadius()));
                    break;
            }
            line.setStroke(color);
            line.setStrokeWidth(LineSize);
            pane.getChildren().add(line);
            sequentialTransition.getChildren().add(pt);
        }
        if (bool) {
            pt = new PathTransition();
            pt.setDuration(Duration.millis(Settings.TIME));
            pt.setNode(circle);
            pt.setPath(new Line((x1 * 2 + 1) * circle.getRadius(), (y1 * 2 + 1) * circle.getRadius(), (Settings.SIZE * 2 - 1) * circle.getRadius(), (Settings.SIZE * 2 - 1) * circle.getRadius()));
            sequentialTransition.getChildren().add(pt);

        }
    }

}
