package util;

import java.util.concurrent.atomic.AtomicBoolean;

public class Holder {
    Object exchange;
    AtomicBoolean waited;
    AtomicBoolean notified;

    public Holder() {
        exchange = new Object();
        waited = new AtomicBoolean(false);
        notified = new AtomicBoolean(false);
    }

    /**
     * hold 住当前线程。
     * 
     * 相当于 wait。如果 release 发生在 hold 前，则 hold 不锁；如果 hold 发生在 release 前，则等待
    */
    public void hold() {
        waited.set(true);
        if(!notified.get()) {
            // System.out.println("[HOLD] Wait for release.");
            try {
                synchronized(exchange) {
                    exchange.wait();
                }
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        } /* else {
            System.out.println("[HOLD] Already released. No hold.");
        } */
    }

    /**
     * 唤醒被 hold 或将被 hold 的线程。
     * 
     * 相当于 notify。如果 release 发生在 hold 前，则 hold 不锁；如果 hold 发生在 release 前，则等待 */
    public void release() {
        if(!waited.get()) {
            // System.out.println("[RELEASE] No one holding. Will release.");
            notified.set(true);
        } else {
            // System.out.println("[RELEASE] Release it.");
            synchronized(exchange) {
                exchange.notify();
            }
        }
    }

    // public static void main(String[] args) {
    //     Notify notify = new Notify();
    //     Thread thread1 = new Thread(() -> {
    //         /* try {
    //             Thread.sleep(1000);
    //         } catch(InterruptedException e) {
    //             e.printStackTrace();
    //         } */

    //         System.out.println("[DEBUG] Thread 1 prepares to hold.");
    //         notify.hold();
    //         System.out.println("[DEBUG] Thread 1 is released.");
    //     }); 
    //     Thread thread2 = new Thread(() -> {
    //         /* try {
    //             Thread.sleep(1000);
    //         } catch(InterruptedException e) {
    //             e.printStackTrace();
    //         } */

    //         System.out.println("[DEBUG] Thread 2 prepares to release.");
    //         notify.release();
    //         System.out.println("[DEBUG] Thread 2 released the holder.");
    //     });
    //     thread1.start();
    //     thread2.start();
    //     try {
    //         thread1.join();
    //         thread2.join();
    //     } catch(InterruptedException e) {
    //         e.printStackTrace();
    //     }
    // }
}
