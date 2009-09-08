package ac.elements.concurrency;

/**
 * Logger.
 */
public class Logger {

  public static void log(String msg) {
    System.out.println(msg);
  }

  public static void error(String msg) {
    System.err.println(msg);
  }

  public static void error(String msg, Exception e) {
    System.err.println(msg);
    e.printStackTrace();
  }

}
