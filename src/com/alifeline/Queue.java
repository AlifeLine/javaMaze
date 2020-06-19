package com.alifeline;

import java.util.ArrayList;

/**
 *
 * @Description: 
 * 
 * @auther: AlifeLine
 * @date: 10:00 2020/6/17 
 * @param: 
 * @return: 
 *
 */
public class Queue {
    private ArrayList<Maze.Grid> list = new ArrayList<>();

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int getSize() {
        return list.size();
    }

    public Maze.Grid peek() {
        return list.get(0);
    }

    public Maze.Grid pop() {
        Maze.Grid o = list.get(0);
        list.remove(0);
        return o;
    }

    public Maze.Grid popnew() {
        Maze.Grid o = list.get(getSize() - 1);
        list.remove(getSize() - 1);
        return o;
    }

    public void push(Maze.Grid o) {
        list.add(o);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
