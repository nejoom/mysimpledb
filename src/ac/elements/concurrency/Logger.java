package ac.elements.concurrency;

/**
 * http://www.devx.com/Java/Article/41377/1954: Logger.
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
