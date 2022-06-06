package ui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public class UIUtil {
    public static void tryInvokeAndWait(Runnable runnable) {
        try {
            SwingUtilities.invokeAndWait(runnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
