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
 * 类的反射信息
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
     * 获取每个的字段属性值
     * @param field 字段
     * @throws NoSuchMethodException 异常
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

            // 如果是泛型参数的类型
            if(genericType instanceof ParameterizedType){
                myList = new MyList();
                ParameterizedType pt = (ParameterizedType) genericType;
                //得到泛型里的class类型对象
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
     * 将Document 转换成 对象
     * @param document document
     * @return 对象
     * @throws XmlException xml异常
     */
    <T> T getObj(Document document) throws XmlException {
        Element root = document.getRootElement();
        if (!rootNode.equals(root.getName())) {
            return null;
        }
        return foreachNode(root);
    }

    /**
     * 遍历节点数据
     * @param root 根节点
     * @return 对象
     * @throws XmlException xml异常
     */
    @SuppressWarnings("unchecked")
    private <T> T foreachNode(Element root) throws XmlException {
        T obj;
        try {
            obj = (T) clazz.newInstance();
            // 遍历根结点的所有孩子节点
            Element element;
            for (Iterator iter = root.elementIterator(); iter.hasNext(); ) {
                element = (Element) iter.next();
                if (element == null) {
                    continue;
                }
                getData(element, obj);
            }
        } catch (InstantiationException e) {
            throw new XmlException("初始化实例错误", e);
        } catch (IllegalAccessException e) {
            throw new XmlException("初始化实例错误", e);
        }
        return obj;
    }

    /**
     * 获取节点的数据
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
                            throw new XmlException("找不到子节点实例");
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
            logger.error("获取数据错误", e);
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
            logger.error("找不到子节点实例");
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
            logger.error("生成xml异常", e);
        } catch (InvocationTargetException e) {
            logger.error("生成xml异常", e);
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
     * 字段的属性值
     */
    static class MyMethod {
        /**
         * 类型
         */
        Class<?> type;
        /**
         * 是否是子节点
         */
        boolean isChildNodes;
        /**
         * Set方法
         */
        Method setMethod;
        /**
         * Get方法
         */
        Method getMethod;

        /**
         * 集合信息
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
         * 集合新增
         */
        Method add;

        /**
         * 泛型类型
         */
        Class<?> genericClazz;
    }
}
