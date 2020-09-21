package com.github.supermoonie.proxy.codec;

import java.security.MessageDigest;

/**
 * SHA 消息摘要组件
 *
 * @author supermoonie
 * @since 2020/7/31
 */
public abstract class SHACoder {

    public enum Algorithms {

        /**
         * SHA-1
         */
        SHA("SHA"),
        /**
         * SHA-256
         */
        SHA_256("SHA-256"),
        /**
         * SHA-383
         */
        SHA_384("SHA-384"),
        /**
         * SHA-512
         */
        SHA_512("SHA-512");

        private final String name;

        Algorithms(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * SHA-1 消息摘要
     *
     * @param data 待做摘要处理的数据
     * @return byte[] 消息摘要
     * @throws Exception e
     */
    public static byte[] encodeSHA(byte[] data) throws Exception {
        return encode(data, Algorithms.SHA.getName());
    }

    /**
     * SHA-256 消息摘要
     *
     * @param data 待做摘要处理的数据
     * @return byte[] 消息摘要
     * @throws Exception e
     */
    public static byte[] encodeSHA256(byte[] data) throws Exception {
        return encode(data, Algorithms.SHA_256.getName());
    }

    /**
     * SHA-384 消息摘要
     *
     * @param data 待做摘要处理的数据
     * @return byte[] 消息摘要
     * @throws Exception e
     */
    public static byte[] encodeSHA384(byte[] data) throws Exception {
        return encode(data, Algorithms.SHA_384.getName());
    }

    /**
     * SHA-512 消息摘要
     *
     * @param data 待做摘要处理的数据
     * @return byte[] 消息摘要
     * @throws Exception e
     */
    public static byte[] encodeSHA512(byte[] data) throws Exception {
        return encode(data, Algorithms.SHA_512.getName());
    }

    /**
     * 消息摘要
     *
     * @param data       待做摘要处理的数据
     * @param algorithms 摘要算法
     * @return byte[] 消息摘要
     * @throws Exception e
     */
    public static byte[] encode(byte[] data, String algorithms) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algorithms);
        return md.digest(data);
    }
}
