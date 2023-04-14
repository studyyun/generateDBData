import cn.hutool.core.thread.NamedThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 应用模块名称
 *
 * @author zhousy
 * @date 2020-12-25  14:51
 */
public class Test3 {

    public static void main(String[] args) throws InterruptedException {

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2, 6, 0L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1024),
                new NamedThreadFactory("testThread-", false),new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < 1000; i++) {

            /*poolExecutor.execute(() -> {
                list.add(6);
                System.out.println(Thread.currentThread().getName()+list.get(6));
            });*/
            new Thread(()->{
                list.add(6);
                System.out.println(Thread.currentThread().getName()+list.get(6));
            }).start();

        }

        TimeUnit.SECONDS.sleep(5);
        poolExecutor.shutdown();
    }

}
