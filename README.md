## Netty Socks Proxy Detail

SocksPortUnificationServerHandler：判断Socks版本号，加入Socks5 编码器



证书格式：https://www.cnblogs.com/MomentsLee/p/10460832.html

SOCKS 协议：https://tools.ietf.org/html/rfc1928

Netty Socks代理服务器源码分析：https://alwayswithme.github.io/jekyll/update/2015/07/25/netty-socksproxy-detail.html

生成根证书：https://knowledge.broadcom.com/external/article/166370/how-to-create-a-selfsigned-ssl-certifica.html

```shell
 openssl genrsa -out ca.key 2048
 openssl pkcs8 -topk8 -nocrypt -inform PEM -outform DER -in ca.key -out ca_private.pem
 openssl req -sha256 -new -x509 -days 365 -key ca.key -out ca.crt \                                                                                            09:21:48
     -subj "/C=CN/ST=Shanghai/L=Shanghai/O=github/OU=supermoonie/CN=mitmproxy4J"
```