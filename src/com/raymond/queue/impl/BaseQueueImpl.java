package com.raymond.queue.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.raymond.entity.Test;
import com.raymond.queue.BaseQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 队列
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2020-12-14 14:47
 */
public class BaseQueueImpl<E> implements BaseQueue<E> {

    private AtomicInteger atomicInteger = new AtomicInteger();

    private AtomicInteger maxIndex = new AtomicInteger();

    private RandomAccessFile accessFileIndex;

    private FileChannel fileChannelIndex;

    private RandomAccessFile accessFileMaxIndex;

    private FileChannel fileChannelMaxIndex;

    private RandomAccessFile accessFileLog;

    private final Class<E> eClass;

    private FileChannel fileChannelLog;

    private MappedByteBuffer bufLog;
    private MappedByteBuffer bufIndex;
    private MappedByteBuffer bufMaxIndex;

    private int startIndex = 0;

    private final BlockingQueue<E> queue;

    public BaseQueueImpl(Class<E> eClass, BlockingQueue<E> queue) throws IOException, InterruptedException {
        this.eClass = eClass;
        this.queue = queue;
        createFileIndex();


        createFileMaxIndex();
        createFileLog();
        bufLog = fileChannelLog.map(FileChannel.MapMode.READ_WRITE, 0, 300 * 1024 * 1024);
        bufIndex = fileChannelIndex.map(FileChannel.MapMode.READ_WRITE, 0, 8);

        bufMaxIndex = fileChannelMaxIndex.map(FileChannel.MapMode.READ_WRITE, 0, 8);
        atomicInteger.set(getIndex());
        maxIndex.set(getMaxIndex());
        initQueue();

    }



    @Override
    public boolean add(E e) {
        return queue.add(e);
    }



    @Override
    public E poll() {
        atomicInteger.incrementAndGet();
        index();
        return queue.poll();
    }

    @Override
    public void put(E e) throws InterruptedException {
        queue.put(e);
        maxIndex.incrementAndGet();
        indexMax();
        log(JSONObject.toJSONString(e));
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public List<E> list(int count) {
        List<E> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            E e = queue.poll();
            if (e == null) {
                break;
            }
            result.add(e);
        }
        return result;
    }

    private void index() {
        try {
            bufIndex.putInt(atomicInteger.get());
            bufIndex.flip();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indexMax() {
        try {
            bufMaxIndex.putInt(maxIndex.get());
            bufMaxIndex.flip();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFileIndex() {
        try {
            accessFileIndex = new RandomAccessFile("index.txt", "rw");
            fileChannelIndex = accessFileIndex.getChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFileMaxIndex() {
        try {
            accessFileMaxIndex = new RandomAccessFile("indexMax.txt", "rw");
            fileChannelMaxIndex = accessFileMaxIndex.getChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void log(String log) {
        try {
            log += System.lineSeparator();
            bufLog.put(log.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFileLog() {
        try {
            accessFileLog = new RandomAccessFile("log.txt", "rw");
            fileChannelLog = accessFileLog.getChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initQueue() throws IOException, InterruptedException {
        try {
            BufferedReader reader = FileUtil.getReader(System.getProperty("user.dir") + "/log.txt", "utf-8");
            String line;
            int i = 0;
            int index = atomicInteger.get();
            int maxIndex = this.maxIndex.get();
            while (!StrUtil.isEmpty(line = reader.readLine())) {
                if (index <= i && i < maxIndex) {
                    queue.put(JSONObject.parseObject(line, eClass));
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getIndex() throws IOException {
        if (bufIndex.hasRemaining()) {
            return bufIndex.getInt();
        }
        return 0;
    }

    public int getMaxIndex() throws IOException {
        if (bufMaxIndex.hasRemaining()) {
            return bufMaxIndex.getInt();
        }
        return 0;
    }

    public String getLog() throws IOException {
        System.out.println(fileChannelLog.size());
        MappedByteBuffer buf = fileChannelLog.map(FileChannel.MapMode.READ_WRITE, 0, fileChannelLog.size());
        StringBuilder sb = new StringBuilder();
        while (buf.hasRemaining()) {
            sb.append((char)buf.get());
        }
        return sb.toString();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        BaseQueueImpl<Test> baseQueue = new BaseQueueImpl<>(Test.class, new LinkedBlockingQueue<>());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            baseQueue.put(new Test("name" + i));
        }
        System.out.println(System.currentTimeMillis() - start);
//        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            System.out.println(JSONObject.toJSONString(baseQueue.poll()));
        }
        System.out.println(System.currentTimeMillis() - start);
//        System.out.println(baseQueue.getIndex());
//        System.out.println(baseQueue.getLog());
    }
}
