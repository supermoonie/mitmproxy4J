package com.github.supermoonie.util;

import cn.hutool.core.util.HexUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;

import java.util.Map;

/**
 * @author supermoonie
 * @since 2020/6/26
 */
public class JSONTest {

    @Test
    public void testToJsonString() {
        String hex = "7b0a20202261726773223a207b7d2c200a20202264617461223a2022222c200a20202266696c6573223a207b7d2c200a202022666f726d223a207b0a2020202022666f6f223a2022626172220a20207d2c200a20202268656164657273223a207b0a20202020224163636570742d456e636f64696e67223a2022677a69702c6465666c617465222c200a2020202022436f6e74656e742d4c656e677468223a202237222c200a2020202022436f6e74656e742d54797065223a20226170706c69636174696f6e2f782d7777772d666f726d2d75726c656e636f6465643b20636861727365743d5554462d38222c200a2020202022486f7374223a20226874747062696e2e6f7267222c200a2020202022557365722d4167656e74223a20224170616368652d48747470436c69656e742f342e352e3520284a6176612f31312e302e3729222c200a2020202022582d416d7a6e2d54726163652d4964223a2022526f6f743d312d35656634373662362d373832653437343036333263616636313931323232306538220a20207d2c200a2020226a736f6e223a206e756c6c2c200a2020226f726967696e223a20223232332e3136362e33322e323137222c200a20202275726c223a202268747470733a2f2f6874747062696e2e6f72672f706f7374220a7d0a";
        Map<String, Object> map = JSON.parse(HexUtil.decodeHexStr(hex), new TypeReference<Map<String, Object>>() {
        });
        System.out.println(JSON.toJsonString(map, true));
    }
}