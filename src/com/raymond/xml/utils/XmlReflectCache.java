package com.raymond.xml.utils;

import com.alibaba.druid.util.StringUtils;

import com.raymond.xml.annotation.XmlNode;
import com.raymond.xml.annotation.XmlRootNode;
import com.raymond.xml.exception.XmlException;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import java.lang.reflect.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * ��ķ�����Ϣ
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-10 10:55
 */
class XmlReflectCache {
    private final static Logger logger = Logger.getLogger(XmlReflectCache.class);
    /**
     * class
     */
    private Class clazz;


    private Map<String, MyMethod> attribute;

    private String rootNode;


    boolean getAttribute() throws NoSuchMethodException {
        XmlRootNode xmlRootNode = (XmlRootNode) this.clazz.getAnnotation(XmlRootNode.class);
        if (xmlRootNode == null) {
            return false;
        }
        rootNode = xmlRootNode.name();
        Field[] declaredFields = this.clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            getField(field);
        }
        return true;
    }

    /**
     * ��ȡÿ�����ֶ�����ֵ
     * @param field �ֶ�
     * @throws NoSuchMethodException �쳣
     */
    @SuppressWarnings("unchecked")
    private void getField(Field field) throws NoSuchMethodException {
        XmlNode xmlNode = field.getAnnotation(XmlNode.class);
        if (xmlNode == null) {
            return;
        }
        String nodeName = xmlNode.name();
        Class<?> type = field.getType();
        MyList myList = null;
        if (type.isAssignableFrom(List.class)) {
            Type genericType = field.getGenericType();
            if(genericType == null) {
                return;
            }

            // ����Ƿ��Ͳ���������
            if(genericType instanceof ParameterizedType){
                myList = new MyList();
                ParameterizedType pt = (ParameterizedType) genericType;
                //�õ��������class���Ͷ���
                Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0];
                myList.add = type.getMethod("add", Object.class);
                myList.genericClazz = genericClazz;
            }
        }

        String fieldName = field.getName();
        Method setFieldName = this.clazz.getMethod("set" + Character.toUpperCase(fieldName.charAt(0))
                + fieldName.substring(1), type);
        Method getFieldName = this.clazz.getMethod("get" + Character.toUpperCase(fieldName.charAt(0))
                + fieldName.substring(1));
        attribute.put(nodeName, new MyMethod(type, xmlNode.isChildNodes(), setFieldName, getFieldName, myList));
    }

    <T> XmlReflectCache(Class<T> clazz) {
        this.clazz = clazz;
        attribute = new HashMap<String, MyMethod>(16);
    }

    /**
     * ��Document ת���� ����
     * @param document document
     * @return ����
     * @throws XmlException xml�쳣
     */
    <T> T getObj(Document document) throws XmlException {
        Element root = document.getRootElement();
        if (!rootNode.equals(root.getName())) {
            return null;
        }
        return foreachNode(root);
    }

    /**
     * �����ڵ�����
     * @param root ���ڵ�
     * @return ����
     * @throws XmlException xml�쳣
     */
    @SuppressWarnings("unchecked")
    private <T> T foreachNode(Element root) throws XmlException {
        T obj;
        try {
            obj = (T) clazz.newInstance();
            // �������������к��ӽڵ�
            Element element;
            for (Iterator iter = root.elementIterator(); iter.hasNext(); ) {
                element = (Element) iter.next();
                if (element == null) {
                    continue;
                }
                getData(element, obj);
            }
        } catch (InstantiationException e) {
            throw new XmlException("��ʼ��ʵ������", e);
        } catch (IllegalAccessException e) {
            throw new XmlException("��ʼ��ʵ������", e);
        }
        return obj;
    }

    /**
     * ��ȡ�ڵ������
     * @param element element
     * @param obj obj
     */
    private <T> void getData(Element element, T obj) {
        try {
            MyMethod mymethod;
            Method method;
            Class<?> type;
            XmlReflectCache reflectUtil;
            for (String nodeName : attribute.keySet()) {
                if (element.getName().equals(nodeName)) {
                    mymethod = attribute.get(nodeName);
                    method = mymethod.setMethod;
                    type = mymethod.type;
                    if (mymethod.myList != null) {
                        getGenericData(mymethod, element, obj);
                        continue;
                    }
                    if (mymethod.isChildNodes) {
                        reflectUtil = MyXmlUtil.REFLECT_UTIL_MAP.get(type.getCanonicalName());
                        if (reflectUtil == null) {
                            throw new XmlException("�Ҳ����ӽڵ�ʵ��");
                        }
                        T t = reflectUtil.foreachNode(element);
                        method.invoke(obj, t);
                        continue;
                    }
                    String fieldType = type.getSimpleName();
                    String textTrim = element.getTextTrim();
                    if (StringUtils.isEmpty(textTrim)) {
                        continue;
                    }
                    if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
                        method.invoke(obj, Integer.parseInt(textTrim));
                    } else if ("Float".equals(fieldType) || "float".equals(fieldType)) {
                        method.invoke(obj, Float.parseFloat(textTrim));
                    } else if ("Long".equals(fieldType) || "long".equals(fieldType)) {
                        method.invoke(obj, Long.parseLong(textTrim));
                    } else if ("Double".equals(fieldType) || "double".equals(fieldType)) {
                        method.invoke(obj, Double.parseDouble(textTrim));
                    } else if ("Timestamp".equals(fieldType)) {
                        method.invoke(obj, Timestamp.valueOf(textTrim));
                    } else if ("Boolean".equals(fieldType) || "boolean".equals(fieldType)) {
                        method.invoke(obj, Boolean.parseBoolean(textTrim));
                    } else {
                        method.invoke(obj, textTrim);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("��ȡ���ݴ���", e);
        }
    }

    private <T> void getGenericData(MyMethod mymethod, Element element, T obj) throws Exception {
        XmlReflectCache reflectUtil;
        Class<?> type = mymethod.myList.genericClazz;
        Object invoke = mymethod.getMethod.invoke(obj);
        if (invoke == null) {
            mymethod.setMethod.invoke(obj, new ArrayList());
            invoke = mymethod.getMethod.invoke(obj);
        }
        reflectUtil = MyXmlUtil.REFLECT_UTIL_MAP.get(type.getCanonicalName());
        if (reflectUtil == null) {
            logger.error("�Ҳ����ӽڵ�ʵ��");
            return;
        }
        Object o = reflectUtil.foreachNode(element);
        mymethod.myList.add.invoke(invoke, o);
    }


    String getXmlData(Object obj) {
        return "<?xml version='1.0' encoding='UTF-8'?>\n" +
                getXml(obj);
    }

    private String getXml(Object obj) {
        MyMethod mymethod;
        Method method;
        Class<?> type;
        XmlReflectCache reflectUtil;
        StringBuilder xmlStr = new StringBuilder(64);
        xmlStr.append("<").append(rootNode).append(">\n");
        try {
            for (String nodeName : attribute.keySet()) {
                mymethod = attribute.get(nodeName);
                method = mymethod.getMethod;
                type = mymethod.type;
                if (mymethod.isChildNodes) {
                    reflectUtil = MyXmlUtil.REFLECT_UTIL_MAP.get(type.getCanonicalName());
                    Object invoke = method.invoke(obj);
                    if (reflectUtil == null) {
                        getGenericXml(invoke, xmlStr);
                        continue;
                    }
                    xmlStr.append(reflectUtil.getXml(invoke)).append("\n");
                    continue;
                }
                Object invoke = method.invoke(obj);
                xmlStr.append("<").append(nodeName).append(">").append(invoke == null ? "" : invoke).append("</").append(nodeName).append(">\n");
            }
        } catch (IllegalAccessException e) {
            logger.error("����xml�쳣", e);
        } catch (InvocationTargetException e) {
            logger.error("����xml�쳣", e);
        }
        return xmlStr.append("</").append(rootNode).append(">").toString();
    }
    private void getGenericXml(Object invoke, StringBuilder xmlStr) {
        XmlReflectCache reflectUtil;
        if (invoke.getClass().isAssignableFrom(List.class) || invoke.getClass().isAssignableFrom(ArrayList.class)) {
            List cast = (List) invoke;
            for (Object o : cast) {
                reflectUtil = MyXmlUtil.REFLECT_UTIL_MAP.get(o.getClass().getCanonicalName());
                xmlStr.append(reflectUtil.getXml(o)).append("\n");
            }
        }
    }

    /**
     * �ֶε�����ֵ
     */
    static class MyMethod {
        /**
         * ����
         */
        Class<?> type;
        /**
         * �Ƿ����ӽڵ�
         */
        boolean isChildNodes;
        /**
         * Set����
         */
        Method setMethod;
        /**
         * Get����
         */
        Method getMethod;

        /**
         * ������Ϣ
         */
        MyList myList;


        MyMethod(Class<?> type, boolean isChildNodes, Method setMethod, Method getMethod, MyList myList) {
            this.type = type;
            this.isChildNodes = isChildNodes;
            this.setMethod = setMethod;
            this.getMethod = getMethod;
            this.myList = myList;
        }
    }

    static class MyList {

        /**
         * ��������
         */
        Method add;

        /**
         * ��������
         */
        Class<?> genericClazz;
    }
}
