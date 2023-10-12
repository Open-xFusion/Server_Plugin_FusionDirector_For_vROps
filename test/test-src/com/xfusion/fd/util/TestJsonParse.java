package com.xfusion.fd.util;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xfusion.fd.api.exception.FusionDirectorException;

/**
 * JsonParse测试类
 *
 * @since 2019-02-18
 */
public class TestJsonParse {
    Logger log = Logger.getLogger(TestJsonParse.class);
    
    @Test
    public void testParseJson2Class() throws FusionDirectorException{
        String jsonString = "{\"attr1\":null, \"attr2\":null}";
        JsonClass cls = HttpRequestUtil.json2Object(jsonString, JsonClass.class);
        log.info(cls.getAttr2());
    }
    
    @Test
    public void testIsNumeric() {
        log.info(ConvertUtil.isNumeric("abc"));
        log.info(ConvertUtil.isNumeric("123"));
        log.info(ConvertUtil.isNumeric(""));
    }
}

class JsonClass{
    private int attr1;

    private Integer attr2;

    public int getAttr1() {
        return attr1;
    }
    public void setAttr1(int attr1) {
        this.attr1 = attr1;
    }
    public Integer getAttr2() {
        return attr2;
    }
    public void setAttr2(Integer attr2) {
        this.attr2 = attr2;
    }
}
