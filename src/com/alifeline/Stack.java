package com.alifeline;

import java.util.ArrayList;

/**
 * @Description:
 * @auther: AlifeLine
 * @date: 9:59 2020/6/17
 * @param:
 * @return:
 */
public class Stack {
    private ArrayList<Maze.Grid> list = new ArrayList<>();

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int getSize() {
        return list.size();
    }

    public Maze.Grid peek() {
        return list.get(getSize() - 1);
    }

    public Maze.Grid pop() {
        Maze.Grid o = list.get(getSize() - 1);
        list.remove(getSize() - 1);
        return o;
    }

    public Maze.Grid get(int i) {
        return list.get(i);
    }

    public void push(Maze.Grid o) {
        list.add(o);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
