package ru.albemuth.util;

import junit.framework.TestCase;

import java.util.Map;

public class TestConvertor extends TestCase {

    public void testParseValues() {
        try {
            Map<String, String> values;

            values = Convertor.parseValues("aaa=bbb,ccc=ddd,,eee==ff,jjj,hhh=", Convertor.PATTERN_PAIRS_COMMA_SEPARATED);
            assertEquals(5, values.size());
            assertEquals("bbb", values.get("aaa"));
            assertEquals("ddd", values.get("ccc"));
            assertEquals("=ff", values.get("eee"));
            assertEquals("", values.get("jjj"));
            assertEquals("", values.get("hhh"));

            values = Convertor.parseValues("aaa=bbb;ccc=ddd;;eee==ff;jjj;hhh=", Convertor.PATTERN_PAIRS_SEMICOLON_SEPARATED);
            assertEquals(5, values.size());
            assertEquals("bbb", values.get("aaa"));
            assertEquals("ddd", values.get("ccc"));
            assertEquals("=ff", values.get("eee"));
            assertEquals("", values.get("jjj"));
            assertEquals("", values.get("hhh"));

            values = Convertor.parseValues("aaa=bbb&ccc=ddd&&eee==ff&jjj&hhh=", Convertor.PATTERN_PAIRS_AMPERSAND_SEPARATED);
            assertEquals(5, values.size());
            assertEquals("bbb", values.get("aaa"));
            assertEquals("ddd", values.get("ccc"));
            assertEquals("=ff", values.get("eee"));
            assertEquals("", values.get("jjj"));
            assertEquals("", values.get("hhh"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
