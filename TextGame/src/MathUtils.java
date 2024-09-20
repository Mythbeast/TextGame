import java.util.HashMap;

public final class MathUtils {

  // private constructor
  private MathUtils() {
  }

  // static utility methods
  public static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(value, max));
  }

  public static HashMap<String, Integer> hashMapSubtract(HashMap<String, Integer> hashMap1,
      HashMap<String, Integer> hashMap2) {
    for (HashMap.Entry<String, Integer> entry : hashMap2.entrySet()) {
      String stat = entry.getKey();
      Integer value2 = entry.getValue();
      Integer value1 = hashMap1.getOrDefault(stat, 0);
      hashMap1.replace(stat, value1, value1 - value2);
    }
    return hashMap1;
  }

  public static HashMap<String, Integer> hashMapAdd(HashMap<String, Integer> hashMap1,
      HashMap<String, Integer> hashMap2) {
    for (HashMap.Entry<String, Integer> entry : hashMap2.entrySet()) {
      String stat = entry.getKey();
      Integer value2 = entry.getValue();
      Integer value1 = hashMap1.getOrDefault(stat, 0);
      hashMap1.replace(stat, value1, value1 + value2);
    }
    return hashMap1;
  }

  public static int[] intArrayAdd(int[] array1, int[] array2) {
    int[] result = new int[array1.length];
    for (int i = 0; i < array1.length - 1; i++) {
      result[i] = array1[i] + array2[i];
    }
    return result;
  }

}
