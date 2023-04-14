package com.raymond.xml.exception;

/**
 * xml异常
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-04 19:26
 */
public class XmlException extends Exception {
    public XmlException(){

    }
    public XmlException(String str, Throwable cause){
        //此处传入的是抛出异常后显示的信息提示
        super(str, cause);
    }

    public XmlException(String str){
        //此处传入的是抛出异常后显示的信息提示
        super(str);
    }

}
