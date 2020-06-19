package com.alifeline;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.ArrayList;

/**
 *
 * @Description: 
 * 
 * @auther: AlifeLine
 * @date: 9:59 2020/6/17 
 * @param: 
 * @return: 
 *
 */
public class Maze {

    Grid[][] maze;
    int startX;
    int startY;
    int exitX;
    int exitY;
    int row;
    int column;

    // 调用算法产生随机迷宫
    // 无参构造方法默认5x5大小
    public Maze() {
        this(5, 5);
    }
    // 带迷宫大小的无参构造方法
    // 默认0，0入 column-1,row-1 出
    public Maze(int row, int column) {
        this(row, column, 0, 0, column - 1, row - 1);
    }
    // 带迷宫大小起点终点的构造方法
    public Maze(int row, int column, int startX, int startY, int exitX, int exitY) {

        // 初始化各项数据
        maze = new Grid[row][column];
        this.row = row;
        this.column = column;
        this.startX = startX;
        this.startY = startY;
        this.exitX = exitX;
        this.exitY = exitY;

        //初始化墙
        for (int i = 0; i < maze.length; i++)
            for (int j = 0; j < maze[i].length; j++)
                maze[i][j] = new Grid(i, j);

        // 把入口和出口的墙拆了
        maze[startY][startX].destroyWall(maze.length, maze[0].length);


        // 递归回溯生成随机迷宫 //部分代码参考stackoverflow
        Stack stack = new Stack();
        stack.push(maze[startY][startX]);
        int x, y;
        int[] arr = null;
        while (stack.getSize() != 0) {
            x = stack.peek().x;
            y = stack.peek().y;
            arr = stack.peek().getRandom();
            if (arr != null) {
                switch (arr[(int) (Math.random() * arr.length)]) {
                    case 2:
                        maze[y][x].destroyWall(2);
                        maze[--y][x].destroyWall(8);
                        stack.push(maze[y][x]);
                        break;
                    case 4:
                        maze[y][x].destroyWall(4);
                        maze[y][--x].destroyWall(6);
                        stack.push(maze[y][x]);
                        break;
                    case 6:
                        maze[y][x].destroyWall(6);
                        maze[y][++x].destroyWall(4);
                        stack.push(maze[y][x]);
                        break;
                    case 8:
                        maze[y][x].destroyWall(8);
                        maze[++y][x].destroyWall(2);
                        stack.push(maze[y][x]);
                        break;
                }
            } else {
                stack.pop();
            }
        }

        maze[exitY][exitX].destroyWall(maze.length, maze[0].length);

    }



    // 根据数组返回pane页面
    public Pane getPane() {
        Pane pane = new Pane();
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (maze[i][j].getLine(maze) != null)
                    pane.getChildren().addAll(maze[i][j].getLine(maze));
            }
        }
        return pane;
    }

    @Override
    public String toString() {
        return super.toString();
    }


    public int getRow() {
        return maze.length;
    }

    public int getColumn() {
        return maze[0].length;
    }
    //用于生成墙的类参考自github
    class Grid {
        // 1 -> 墙
        // 0 -> 无墙

        int x;
        int y;
        int wall;
        ArrayList<Integer> last = new ArrayList<>();
        final int SIZE = 700 / maze.length;//计算单个墙在界面上占用的大小

        Grid(int y, int x) {
            this.x = x;
            this.y = y;
            this.wall = 15;
        }

        // 获取随机方向 2->上, 4->左, 8->下, 6->右

        public int[] getRandom() {
            int[] array = null;
            if (x == 0 && y == 0)
                array = new int[]{6, 8};
            else if (x == 0 && y == row - 1)
                array = new int[]{2, 6};
            else if (x == column - 1 && y == 0)
                array = new int[]{4, 8};
            else if (x == column - 1 && y == row - 1)
                array = new int[]{2, 4};
            else if (x == 0)
                array = new int[]{2, 6, 8};
            else if (y == 0)
                array = new int[]{4, 6, 8};
            else if (x == column - 1)
                array = new int[]{2, 4, 8};
            else if (y == row - 1)
                array = new int[]{2, 4, 6};
            else
                array = new int[]{2, 4, 6, 8};

            int[] arr = new int[array.length];
            int num = 0;
            for (int i = 0; i < array.length; i++) {
                switch (array[i]) {
                    case 2:
                        if (maze[y - 1][x].wall == 15)
                            arr[num++] = 2;
                        break;
                    case 4:
                        if (maze[y][x - 1].wall == 15)
                            arr[num++] = 4;
                        break;
                    case 6:
                        if (maze[y][x + 1].wall == 15)
                            arr[num++] = 6;
                        break;
                    case 8:
                        if (maze[y + 1][x].wall == 15)
                            arr[num++] = 8;
                        break;
                }
            }
            if (num == 0)
                return null;
            else {
                int[] arr1 = new int[num];
                System.arraycopy(arr, 0, arr1, 0, num);
                return arr1;
            }
        }

        // 获取可用方向
        public int[] getDirection() {
            int[] array = null;
            if (x == 0 && y == 0)
                array = new int[]{6, 8};
            else if (x == 0 && y == row - 1)
                array = new int[]{2, 6};
            else if (x == column - 1 && y == 0)
                array = new int[]{4, 8};
            else if (x == column - 1 && y == row - 1)
                array = new int[]{2, 4};
            else if (x == 0)
                array = new int[]{2, 6, 8};
            else if (y == 0)
                array = new int[]{4, 6, 8};
            else if (x == column - 1)
                array = new int[]{2, 4, 8};
            else if (y == row - 1)
                array = new int[]{2, 4, 6};
            else
                array = new int[]{2, 4, 6, 8};

            int[] arr = new int[array.length];
            int num = 0;
            int dir;
            for (int i = 0; i < array.length; i++) {
                dir = array[i];
                if (!last.contains(dir))
                    if (!isWall(wall, dir))
                        arr[num++] = dir;
            }
            if (num == 0)
                return null;
            else {
                int[] arr1 = new int[num];
                System.arraycopy(arr, 0, arr1, 0, num);
                return arr1;
            }
        }

        // 判断某墙是否存在

        public boolean isWall(int source, int direction) {
            int pos;
            switch (direction) {
                case 2:
                    pos = 3;
                    break;
                case 4:
                    pos = 2;
                    break;
                case 8:
                    pos = 1;
                    break;
                case 6:
                    pos = 0;
                    break;
                default:
                    return true;
            }
            int a = ((source >> pos) & 1);
            return a == 1;
        }

        // 常规拆墙

        public void destroyWall(int direction) {
            switch (direction) {
                case 2:
                    this.wall = wall & ~(1 << 3);  //使用位运算加快运算
                    break;
                case 4:
                    this.wall = wall & ~(1 << 2);
                    break;
                case 8:
                    this.wall = wall & ~(1 << 1);
                    break;
                case 6:
                    this.wall = wall & ~(1);
                    break;
                default:
                    break;
            }
        }

        // 边界拆墙
        public void destroyWall(int row, int column) {
            if (x == 0)
                destroyWall(4);
            if (y == 0)
                destroyWall(2);
            if (y == row - 1)
                destroyWall(8);
            if (x == column - 1)
                destroyWall(6);
        }

        public Line[] getLineAll(int y, int x) {
            Line line1= new Line(x, y, x + SIZE, y);
            line1.setStrokeWidth(UI.LineSize);
            Line line2= new Line(x, y, x, y + SIZE);
            line2.setStrokeWidth(UI.LineSize);
            Line line3= new Line(x, y + SIZE, x + SIZE, y + SIZE);
            line3.setStrokeWidth(UI.LineSize);
            Line line4= new Line(x + SIZE, y, x + SIZE, y + SIZE);
            line4.setStrokeWidth(UI.LineSize);
            return new Line[]{line1,line2,line3,line4};
        }

        public Line[] getLine(Grid[][] maze) {
            Line[] lineAll = getLineAll(this.y * SIZE, this.x * SIZE);
            switch (this.wall) {
                case 0:
                    return null;
                case 1:
                    return new Line[]{lineAll[3]};
                case 2:
                    return new Line[]{lineAll[2]};
                case 3:
                    return new Line[]{lineAll[2], lineAll[3]};
                case 4:
                    return new Line[]{lineAll[1]};
                case 5:
                    return new Line[]{lineAll[1], lineAll[3]};
                case 6:
                    return new Line[]{lineAll[1], lineAll[2]};
                case 7:
                    return new Line[]{lineAll[1], lineAll[2], lineAll[3]};
                case 8:
                    return new Line[]{lineAll[0]};
                case 9:
                    return new Line[]{lineAll[0], lineAll[3]};
                case 10:
                    return new Line[]{lineAll[0], lineAll[2]};
                case 11:
                    return new Line[]{lineAll[0], lineAll[2], lineAll[3]};
                case 12:
                    return new Line[]{lineAll[0], lineAll[1]};
                case 13:
                    return new Line[]{lineAll[0], lineAll[1], lineAll[3]};
                case 14:
                    return new Line[]{lineAll[0], lineAll[1], lineAll[2]};
                case 15:
                    return lineAll;
                default:
                    return null;
            }
        }

    }
}



