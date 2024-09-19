
public final class MathUtils {

  // private constructor
  private MathUtils() {
  }

  // static utility methods
  public static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(value, max));
  }

}
