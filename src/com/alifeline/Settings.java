package com.alifeline;

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
class Settings {
    static int SIZE = 5;
    static int TIME = 8;
    public static String[] getSIZE() {
        String[] str = new String[90];
        for (int i = 0; i < 90; i++) {
            str[i] = (i + 5) + "x" + (i + 5);
        }
        return str;
    }

    public static void setSIZE(int SIZE) {
        Settings.SIZE = SIZE + 5;
    }

}
