package com.tutorial;

import java.util.*;

/**
 * Created by Chanhui on 2014/10/11.
 */
public class Main {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(new ThreadDemo()).start();
        }

    }
}

class ThreadDemo implements Runnable {

    private List<Integer> list = new ArrayList<Integer>();

    private Map<Integer, Object> map = new HashMap<Integer, Object>();

    private Random random = new Random(100);

    @Override
    public void run() {
        while (true) {

            map.put(random.nextInt(100), new byte[Math.abs(random.nextInt(100))*65536]);

            byte[] cache = new byte[102400];
            list.add(new Random().nextInt());
            int sum = 0;
            for (int i : list) {
                sum += i;
            }
            System.out.println(sum);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
