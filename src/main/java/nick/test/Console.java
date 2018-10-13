package nick.test;

/**
 * Created by Nick on 2018/10/13.
 */
public class Console {

    public static void main(String[] args) {
        String test = (String) PropertyHolder.properties().get("test");
        System.out.println(test);
    }
}
