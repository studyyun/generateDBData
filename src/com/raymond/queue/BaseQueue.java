package com.raymond.queue;

import java.util.List;

/**
 * 队列
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2020-12-14 14:46
 */
public interface BaseQueue<E> {
    /**
     * 队列中新增数据
     * @param e 需要新增的数据
     * @return 成功失败
     */
    boolean add(E e);

    /**
     * 获取数据
     * 没有数据放回空
     * @return 数据对象
     */
    E poll();

    /**
     * 队列中新增数据
     * @param e 需要新增的数据
     * @throws InterruptedException 线程中断异常
     */
    void put(E e) throws InterruptedException;

    /**
     * 数据clear
     */
    void clear();

    /**
     * 获取数据条数
     * @return 当前条数
     */
    int size();

    /**
     * 是否有数据
     * @return true 没有数据
     */
    boolean isEmpty();

    /**
     * 获取多条数据
     * @param count 获取最大条数，不足获取当前所有
     * @return 数据对象集合
     */
    List<E> list(int count);
}
