package com.ls.download.m3u8;

/**
 * EXT-X-KEY：媒体片段可以进行加密，而该标签可以指定解密方法。
 */
public class M3u8Key {
    /**
     * 该值是一个可枚举的字符串，指定了加密方法。
     * 该键是必须参数。其值可为NONE，AES-128，SAMPLE-AES当中的一个。
     * 其中：
     * NONE：表示切片未进行加密（此时其他属性不能出现）；
     * AES-128：表示表示使用 AES-128 进行加密。
     * SAMPLE-AES：意味着媒体片段当中包含样本媒体，比如音频或视频，它们使用 AES-128 进行加密。这种情况下 IV 属性可以出现也可以不出现。
     *
     */
    private String method;

    /**
     * 指定密钥路径。
     * 该密钥是一个 16 字节的数据。
     * 该键是必须参数，除非 METHOD 为NONE
     */
    private String uri;

    /**
     * 该值是一个 128 位的十六进制数值。
     * AES-128 要求使用相同的 16字节 IV 值进行加密和解密。使用不同的 IV 值可以增强密码强度。
     * 如果属性列表出现 IV，则使用该值；如果未出现，则默认使用媒体片段序列号（即 EXT-X-MEDIA-SEQUENCE）作为其 IV 值，
     * 使用大端字节序，往左填充 0 直到序列号满足 16 字节（128 位）。
     */
    private String iv;

    /**
     * 由双引号包裹的字符串，标识密钥在密钥文件中的存储方式（密钥文件中的 AES-128 密钥是以二进制方式存储的16个字节的密钥）。
     * 该属性为可选参数，其默认值为"identity"。
     * 使用该属性要求兼容版本号 EXT-X-VERSION 大于等于 5
     */
    private String keyFormat;

    private String keyFormatVersions;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getKeyFormat() {
        return keyFormat;
    }

    public void setKeyFormat(String keyFormat) {
        this.keyFormat = keyFormat;
    }

    public String getKeyFormatVersions() {
        return keyFormatVersions;
    }

    public void setKeyFormatVersions(String keyFormatVersions) {
        this.keyFormatVersions = keyFormatVersions;
    }
}
