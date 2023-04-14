package com.raymond.xml.utils;



import com.raymond.utils.ClassUtils;
import com.raymond.xml.annotation.XmlScan;
import com.raymond.xml.exception.XmlException;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * xml����
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-04 14:35
 */
public class MyXmlUtil {
    static final Map<String, XmlReflectCache> REFLECT_UTIL_MAP = new HashMap<String, XmlReflectCache>();

    private final static Logger logger = Logger.getLogger(MyXmlUtil.class);

    static {
        Class<?> aClass = deduceMainApplicationClass();
        if (aClass != null) {
            XmlScan annotation = aClass.getAnnotation(XmlScan.class);
            setPackageNameList(annotation.basePackages());
        }
    }
    private static Class<?> deduceMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException e) {
            logger.error("xml���������쳣", e);
        }
        return null;
    }

    /**
     * ���ð�ɨ��·��,�������
     * @param packageNameList ����
     */
    private static void setPackageNameList(String[] packageNameList) {
        for (String packageName : packageNameList) {
            setPackageName(packageName);
        }

    }

    /**
     * ���ð�ɨ��·��,��������
     * @param packageName ����
     */
    private static void setPackageName(String packageName) {
        try {
            Set<Class<?>> classes = ClassUtils.getClasses(packageName);
            loadClass(classes);
        } catch (Exception e) {
            logger.error("����ʧ��", e);
        }
    }

    private static void loadClass(Set<Class<?>> classes) throws NoSuchMethodException {
        XmlReflectCache reflectUtil;
        boolean attribute;
        for (Class<?> aClass : classes) {
            reflectUtil = new XmlReflectCache(aClass);
            attribute = reflectUtil.getAttribute();
            if (!attribute) {
                continue;
            }
            REFLECT_UTIL_MAP.put(aClass.getCanonicalName(), reflectUtil);
        }
    }
    public static <T> T convertXmlStrToObject(Class<T> clazz, String xmlStr){
        XmlReflectCache reflectUtil = REFLECT_UTIL_MAP.get(clazz.getCanonicalName());
        try {
            if (reflectUtil == null) {
                throw new XmlException("δɨ�赽������ע��");
            }
            Document document = DocumentHelper.parseText(xmlStr);
            return reflectUtil.getObj(document);
        } catch (Exception e) {
            logger.error("����xml�쳣", e);
            return null;
        }

    }
    public static String convertToXml(Object obj){
        if (obj == null) {
            return null;
        }
        if (obj.getClass().isAssignableFrom(String.class)) {
            return (String) obj;
        }
        XmlReflectCache reflectUtil = REFLECT_UTIL_MAP.get(obj.getClass().getCanonicalName());
        try {
            if (reflectUtil == null) {
                throw new XmlException("δɨ�赽������ע��");
            }
            return reflectUtil.getXmlData(obj);
        } catch (Exception e) {
            logger.error("����xml�쳣", e);
            return null;
        }
    }


}
