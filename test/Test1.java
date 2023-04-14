import java.util.*;

/**
 * 应用模块名称
 *
 * @author zhousy
 * @date 2020-12-23  11:40
 */
public class Test1 {

    public static void main(String[] args) {
        /*List<String> list1 =new ArrayList<String>();
        list1.add("A");
        list1.add("B");
        List<String> list2 =new ArrayList<String>();
        list2.add("B");
        list2.add("C");

        list1.removeAll(list2);
        System.out.println(list1);*/
        System.out.println(new Date());
        List<Integer> srcIdList = new ArrayList<>();
        for (int i = 50000; i < 1000000; i++) {
            srcIdList.add(i);
        }
        System.out.println(new Date());
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < 100000; i++) {
            set.add(i);
        }
        System.out.println(new Date());
        set.removeAll(srcIdList);
        System.out.println(set.size());
        System.out.println(new Date());


    }

}
