package com.sum.framework.utils

import android.util.Base64
import com.github.gzuliyujiang.rsautils.RSAUtils
import com.sum.framework.log.LogUtil
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher


/**
 *
 * @author why
 * @date 2023/6/1 16:12
 */
object RSAUtil {

    fun base64ToDecode(str: ByteArray?): ByteArray {
        return android.util.Base64.decode(str, android.util.Base64.NO_WRAP)
    }

    fun base64ToDecode2(str: String): ByteArray {
        return android.util.Base64.decode(str, android.util.Base64.NO_WRAP)
    }

    fun encode(key: ByteArray?): String? {
        return Base64.encodeToString(key, android.util.Base64.NO_WRAP)
    }

    fun onRSAAndRC4Decrypt(encryptedData: String):String {
        LogUtil.d("RC4密文：$encryptedData")
        val inputByte: ByteArray = base64ToDecode(encryptedData.toByteArray(charset("UTF-8")))
        val decryptedPassword =
            RSAUtils.decrypt(inputByte, RSAUtils.generatePrivateKey(PRIVATE_KEY))
        val password = String(decryptedPassword)
        LogUtil.d("RC4密文：$password")
        return password
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    @Throws(Exception::class)
    fun publicKeyEncrypt(key:String, str: String): String {
//        log.info("{}|RSA公钥加密前的数据|str:{}|publicKey:{}",point,str,publicKey);
        //base64编码的公钥
        val decoded: ByteArray = base64ToDecode2(key)

        val pubKey: RSAPublicKey = KeyFactory.getInstance("RSA")
            .generatePublic(X509EncodedKeySpec(decoded)) as RSAPublicKey
        //RSA加密
        val cipher: Cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, pubKey)
        //        log.info("{}|公钥加密后的数据|outStr:{}",point,outStr);
        val src = cipher.doFinal(str.toByteArray(charset("UTF-8")))

        return Base64.encodeToString(src, Base64.NO_WRAP)
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    @Throws(Exception::class)
    fun privateKeyDecrypt(str: String): String {
//        log.info("{}|RSA私钥解密前的数据|str:{}|privateKey:{}",point,str,privateKey);
        //64位解码加密后的字符串
        val inputByte: ByteArray = base64ToDecode(str.toByteArray(charset("UTF-8")))
        //base64编码的私钥
        val decoded: ByteArray = base64ToDecode(PRIVATE_KEY.toByteArray())
        val priKey: RSAPrivateKey = KeyFactory.getInstance("RSA")
            .generatePrivate(PKCS8EncodedKeySpec(decoded)) as RSAPrivateKey
        //RSA解密
        val cipher: Cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, priKey)
        //        log.info("{}|RSA私钥解密后的数据|outStr:{}",point,outStr);
        return String(cipher.doFinal(inputByte))
    }


    private const val PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEpAIBAAKCAQEA5BdGxfMo/heHaZIHcyOIA026Rx0jCKolWBvptAps40od0AbT\n" +
            "+kxPc3FT8bqJ/VAtTLWHN5LwUr/9U4Y84FARaN4PFrGFEOb59MgEHru5kyWjqnLL\n" +
            "s7EeNY4REzbx0Vhb86LEeNALKbhhG3NryM6vkohllMqGN2dozdEcZ/9zwUL4qTjl\n" +
            "0lL4W25JAmHCCrw5vGKPtHFmBRJFO57xsCHGE3dJzNjTJb1mRRK8WEBckhyLt06z\n" +
            "ACYAg7C+88u90Sk9Q3091vL3Ym69j14atbAsZ0eu2F9/oQEU4O1ALr4VAHI0/HTi\n" +
            "NN/sqjKp0zIyAkdqThkhNPhCrQdSYSW1GLlC4wIDAQABAoIBAAMCr6NzNypLYzUo\n" +
            "gUKqyGPKxdekcc7ud/m2vJnsvU0usEALpwaZtT6/0yzQmnumcEKf7r30CYn+ELjm\n" +
            "f60yQViR/b797tSvR8NMVImXTra7e3KHe1rOzqhETMNlKUHrGaOYlWFG1a7iSR3U\n" +
            "oyqOV9DmTfjtx2G50q9MwEQ7Ier+Eeh3VjJ8oA3tInReQdLEmDb+dyIqKfrYruQL\n" +
            "BTWTawLCNRe4ok8eo7j0XsXV4CE88dcqSNGUI8G+Kja9LCI46WLqyq5mZz2aQO9V\n" +
            "i9nJ6zlRu1Ah6kXkm5nb3R9aZz34Y2zQY+OITOgzNtD6A5OYAQCeCwzDxZvHg5NV\n" +
            "R7nmR6ECgYEA5CrygY0+M7+VfHNf/dD0yMWcf6PgX17GUjzqcXwP2JcGRc9p88VV\n" +
            "417FqKQC7ksO729wn24fz5pUKhmK6pguKrurA1KUZ7mU5+q2GZght0UI659JT655\n" +
            "PNE+3VYoibc6hJZEiN8V1lXvwm7FbqAuf+vpw8ID+TEb2z6X3v2McFECgYEA/+nu\n" +
            "AEoqNmNDhpCSbmRF0ZEPa+t7+HDUKmtYrjIb0QWzDqpZ2dOLqhXsFbcB9E+dH/KY\n" +
            "iDd5g4R27yWbbLStDnAXmcHnGioJUvQgG1su3FnlyhH0CFirghi33Lb8XB5p4lr/\n" +
            "Tv/oJPqNLvTmiE78CBdLlEHg8IjnzxQYUz3MxvMCgYEAzxAWofFfOLEt5RIDVCFU\n" +
            "c/u56a/7yDEHQ8yaakDPVJzCCYqQubQlHMF+GWw2aLDfLfVxPI5A+jMxHD+v7PQM\n" +
            "OaW0LcC8g2FMvCcp+RIxztMspEAa1OAekE3Igi9VfJ020upX6eqiM7ArdMT4EUv2\n" +
            "xwp+S7P/zR5/DDnCs5Bf6fECgYAKFxcjO8leS5ul+p1LbPizwpxxDN/ec9Rpt8HP\n" +
            "XHpsS2U79suEIysmkaa1VHnnQBxZ3h7VgBpybQ6jb0ApkRYhs5m6nkKWbYHgm2+I\n" +
            "pJe8aG7/AKY2jgh1ILRfJ3fbyfcyzZOzcbOdgaN/bniId1TInOKF/fL4iO1a5Nfw\n" +
            "sEcasQKBgQDRUt2bPxkFxky7FJPNR+PL/g4iIw4EzxDi5gh9Udui3ScTa6Lk4EQ4\n" +
            "gIlXXq/bfpp5J8wVfhbaEzc387iul7uDbVb68kID0JIbEoDxty+o3LiTUAi8/CLL\n" +
            "aQfBR3pbVAutSkeKa1v2btvdt7uVzejRvQUtYtS/HV4+uRYeHh0bLg==\n" +
            "-----END RSA PRIVATE KEY-----"
    private const val PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5BdGxfMo/heHaZIHcyOI\n" +
            "A026Rx0jCKolWBvptAps40od0AbT+kxPc3FT8bqJ/VAtTLWHN5LwUr/9U4Y84FAR\n" +
            "aN4PFrGFEOb59MgEHru5kyWjqnLLs7EeNY4REzbx0Vhb86LEeNALKbhhG3NryM6v\n" +
            "kohllMqGN2dozdEcZ/9zwUL4qTjl0lL4W25JAmHCCrw5vGKPtHFmBRJFO57xsCHG\n" +
            "E3dJzNjTJb1mRRK8WEBckhyLt06zACYAg7C+88u90Sk9Q3091vL3Ym69j14atbAs\n" +
            "Z0eu2F9/oQEU4O1ALr4VAHI0/HTiNN/sqjKp0zIyAkdqThkhNPhCrQdSYSW1GLlC\n" +
            "4wIDAQAB\n" +
            "-----END PUBLIC KEY-----"

}