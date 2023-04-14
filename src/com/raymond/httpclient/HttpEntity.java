package com.raymond.httpclient;

import org.apache.log4j.Logger;

/**
 * http����
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-05 11:38
 */
public class HttpEntity {
    private final static Logger logger = Logger.getLogger(HttpEntity.class);
    /**
     * �����ӳ���ȡ���ӵĳ�ʱʱ��Ĭ��2��
     */
    public static int connectionRequestTimeout = 30 * 1000;

    /**
     * ���ӳ�ʱʱ��Ĭ��5��
     */
    public static int connectTimeout = 20 * 1000;

    /**
     * ��Ӧ��ʱʱ��Ĭ��5��
     */
    public static int socketTimeout = 60 * 1000;

    /**
     * ���ӳ����������Ĭ��10��
     */
    public static int maxTotal = 20;

    /**
     * ÿ��ip���������Ĭ��2��
     */
    public static int maxPerRoute = 10;
    /**
     * �Զ�����ʱ��
     */
    public static long clearTime = 15;
    /**
     * ���ô����ӳػ�ȡ���ӳ�ʱʱ��
     * @param time ��ʱ��
     * @return void
     * */
    public static void setRequestTimeout(int time) {
        if (time > 0) {
            connectionRequestTimeout = time;
        } else {
            logger.info("���ӳػ�ȡ���ӳ�ʱʱ������ʧ��Ĭ������Ϊ30��");
        }
    }
    /**
     * �������ӳ�ʱʱ��
     * @param time ��ʱ��
     * @return void
     * */
    public static void setConnectTimeout(int time) {
        if (time > 0) {
            connectTimeout = time;
        } else {
            logger.info("���ӳ�ʱʱ������ʧ��Ĭ������Ϊ20��");
        }

    }
    /**
     * ������Ӧ��ʱʱ��
     * @param time ��ʱ��
     * @return void
     * */
    public static void setSocketTimeout(int time) {
        if (time > 0) {
            socketTimeout = time;
        } else {
            logger.info("��Ӧ��ʱʱ������ʧ��Ĭ������Ϊ60��");
        }
    }
    /**
     * ���������������ÿ��ip���������
     * @param total ���������
     * @param route ÿ��ip���������
     * @return void
     * */
    public static void setMaxTotal(int total, int route) {
        if (route > 0 && total >= route) {
            maxTotal = total;
            maxPerRoute = route;
        } else {
            logger.info("���ӳ������������ip������������ò����Ϲ涨����Ĭ��ֵ����");
        }
    }
    /**
     * �����Զ������������ʱ��
     * @param time ʱ����
     * @return void
     * */
    public static void setClearTime(int time){
        if(time>0){
            clearTime = time;
        }else{
            logger.info("�Զ������������ʱ��ʧ��Ĭ������Ϊ15��");
        }
    }
}
