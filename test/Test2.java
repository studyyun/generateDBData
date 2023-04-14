import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;

/**
 * 应用模块名称
 *
 * @author zhousy
 * @date 2020-12-23  15:45
 */
public class Test2 {

    public int i = 2;
    public final List<Integer> list = new ArrayList<>();

    private void testFinal(){
    }

    public static void main(String[] args) {
        List<Integer> list = new Test2().list;
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.forEach(System.out::println);

        list = new ArrayList<>();
        System.out.println(list);
    }

}

class Son extends Test2{

    public void testFinal(){
        super.i = 3;
    }

    public static void main(String[] args) {

    }
}
