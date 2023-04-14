package test;

import java.io.File;

/**
 * 应用模块名称
 *
 * @author zhousy
 * @date 2021-01-05  11:47
 */
public class TestFile {

    public String getPackageName(){
        Package pack =getClass().getPackage();
        return pack.getName();
    }

    public static void main(String[] args) {
        /*File dir = new File("."+File.separator  + "log" +File.separator
                +"2021"+File.separator  +"01"+File.separator  +"05"+File.separator  +"send");
        File[] children = dir.listFiles();
        assert children != null;
        System.out.println(children[0].getPath());*/

        System.out.println(new TestFile().getPackageName());
        System.out.println(System.getProperty("user.dir"));
        System.out.println(File.separator);
    }

}
