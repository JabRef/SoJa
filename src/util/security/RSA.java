package util.security;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import util.Base64;

/**
 *
 * @author Thien Rong
 */
public class RSA {

    public static final String SIGNATURE = "SHA1withRSA";
    public static final int KEY_LENGTH = 1024; /// 2048 signing is very slow
    public static final float BYTES_BASE64_SIZE = 1.359375f;
    static final String ALGO = "RSA";
    public static final String testJoePublicKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANEc2gvyzxRQfC+eNFFW/cTwFvrGdqV/uy+dYqLO2XCcv84LPeJOKcLkz+W8Xg3Lm5NrtNk/a49oTmXeK3/a7r5h/g13/4tUa5doZQEXCJXrNGSor3TJ5G+1X5/v0395VO6k5kApHgxu6mX8hT5GMVFU9hXcTFlOeOWOeeLHG14DAgMBAAECgYBFxOSTW/3vfzMQCC7YfLfKv3hLT3BCo61mo5JHU9CEnCf6rRGGsiGI3yLmOcT/wVE1DJiYa2qNvHV703acAAXrcQ2w+2jtYmdNymQiazImT009Ngp8dA30rD8zD4mM2aC9woOwlIEo8xr97dpS3Om7jsUwsUrl5dyZV+nUsln7kQJBAOeAEv6eg1rZH8271kW75iK/DeSp7psy1vUJBIDI/ks9FLIKP4gLUsg/gelUFBTBEj7iXnw8b9cYe34PNdKnMk8CQQDnPj3NxN1p4zN4E8SSEktoafP8GtmI4oa4GPxvVpmWpcFrCIyOmfqIKHSPqkK3yev0s45GkVCOIWm0900RITANAkAlmEv9Y5qrX0Vl8NI8YuZd0C8eamZgd+Imyv8bNwjtCQ9aVoW4vzYDZSTg0pGsSSqAYdy8SkhTYxty/l9cRWDLAkEAvaZhX8JGaRJqYt+rhrU0XTUQYkHPCSHnpVLwrYuTuWbGI7dGN6n3O/YZ6r2TrFIRj4Y3eI6zR24F2OI9tX/HfQJAb328186d96CNsnsR0iajHUOv6oD0ft5LMrcO/CH3yb5sJxs4bBf6WJNdZme/7IqxR1Ajn6Ha9XveDMXpveQnkw==";
    public static final String testJoePrivateKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDRHNoL8s8UUHwvnjRRVv3E8Bb6xnalf7svnWKiztlwnL/OCz3iTinC5M/lvF4Ny5uTa7TZP2uPaE5l3it/2u6+Yf4Nd/+LVGuXaGUBFwiV6zRkqK90yeRvtV+f79N/eVTupOZAKR4Mbupl/IU+RjFRVPYV3ExZTnjljnnixxteAwIDAQAB";
    public static final String testSmithPublicKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAN5yjBDV9SHpX38CdLdiabxhaKP6oOpIPL/x1nL165V6Ah+jMgQ7wmG2URKnpXoEvLowl5ox0kGKL+vqrqesyXJE9oeAGU6fclGPr5evj+y8Mr8c+sHAAR7kierk6WgTfdjA0B4yET8JO2Sjxv9Ydl0xBtno6//bfhFSitqkQDO5AgMBAAECgYAKmaTJDJkQ06EkWUackoj9XBrzbXcLkwOqFeklM0jVp6nAPjOc9ggORfTZEboV9XHU4Ynn5CXjz6T29gj6noY+3/trWA0waUHomRf1w79ibuushxUUNC0wsZWReC1s7z2iqoMOiF6Ylcvl8o7gmZ+rSZNtTfeo7tdGAdjDfv+LgQJBAPpZBdRYkptdWxg09buBLpUbnrU4//Hy6G9sq38svRWJONOd/IT3IymLbAY60+TSQxTWJHUOTvvC7pqQOr9qBakCQQDjeEOpP//vPBNSIsUPwIa23HrCSWXkkM40/YUYSBgK8UV/tA3iyIzMtk3UUcrY2t3sGIlmq9wdRoBSRRwFDGeRAkB/5zmmOrZ0cCbZYD1n1eFBxwG33u5M4+jC/MdFy0qzlQ8WdvIL0OQDFrtTF30ovBAPtjScJXpPAFG9YBDTuMw5AkA5Sj7wT7gsDttm4m7zarADOkCI0mehS39EBV4SIYY8q0uvwC+HIK+ll3y+ruMB/w3JutxQSZSkou8KyXEnLFqhAkEA8WxG7DSnPxJpTn7rwoUlK42nxIf66R7D5LQ7oixEBPr553mc16MydOMfpHDVQN8DG3vLiAHSFG3V+mNy2vvPDg==";
    public static final String testSmithPrivateKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDecowQ1fUh6V9/AnS3Ymm8YWij+qDqSDy/8dZy9euVegIfozIEO8JhtlESp6V6BLy6MJeaMdJBii/r6q6nrMlyRPaHgBlOn3JRj6+Xr4/svDK/HPrBwAEe5Inq5OloE33YwNAeMhE/CTtko8b/WHZdMQbZ6Ov/234RUorapEAzuQIDAQAB";

    public static void main(String args[])
            throws Exception {
        byte a[] = "abc111111111111111111gerwgkmpppriperjgpoerwgjpoergjeporgjpoerjgpoerjwgojwpogjwporejgpowjrogjrpojgojgoprjgporjgpjgporjwgpojrogj11111111111111111111okewpokfpoekwfpewofkwpofkewpo".getBytes();
        KeyPair kp = generateKeyPair();
        String keys[] = toBase64(kp);
        KeyPair kp2 = toKeyPair(keys[0], keys[1]);
        KeyPair kpOther = generateKeyPair();
        a = keys[0].getBytes();

        String plain = "  abcgerg,e[rw,lew, ;ler, ';ver, ';vq,relg,qr;gl,ql;xf,wqe;lfx,2po14,rpo21,{P#@<LRPO!#@KLR!@#*^$&#@U()  ";
        String signedWithPrefixPublicKey = signAndPrefixPublicKey(plain, kp, kpOther.getPublic());
        String resultOfPrefixPublicKey = verifyWithPrefixPublicKey(signedWithPrefixPublicKey, kpOther.getPrivate())[0];
        System.out.println(plain);
        System.out.println(resultOfPrefixPublicKey);
        System.out.println("Sign with private key and prefix result " + plain.equals(resultOfPrefixPublicKey));

        String signed = sign(plain, kp.getPrivate(), null);
        String result = verify(signed, kp.getPublic(), null);
        System.out.println("Sign result " + plain.equals(result));

        //byte[] cipher = encryptRSA(plain.getBytes("UTF-8"), kp.getPublic());
        //System.out.println(new String(decryptRSA(cipher, kp.getPrivate())));

        signed = sign(plain, kp.getPrivate(), kp2.getPublic());
        result = verify(signed, kp.getPublic(), kp2.getPrivate());
        System.out.println("Sign with private key result " +plain.equals(result));
        System.exit(0);

        System.out.println(keys[0].length() + "/" + keys[1].length());
        KeyPair testJoe3 = toKeyPair(testJoePublicKey, testJoePrivateKey);
        KeyPair testDoe3 = toKeyPair(testSmithPublicKey, testSmithPrivateKey);
        System.out.println(new String(decrypt(encrypt(a, testDoe3.getPrivate()), testDoe3.getPublic())));
        System.out.println(new String(decrypt(encrypt(a, testJoe3.getPrivate()), testJoe3.getPublic())));
        System.out.println(new String(decrypt(encrypt(a, kp.getPrivate()), kp2.getPublic())));
        System.out.println(new String(decrypt(encrypt(a, kp2.getPrivate()), kp.getPublic())));
    }

    public static KeyPair generateKeyPair() {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance(ALGO);
            keygen.initialize(KEY_LENGTH);
            return keygen.generateKeyPair();

        } catch (NoSuchAlgorithmException ex) {
            // should not occur
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        }
        return keyPair;
    }

    /**
     * Return {private, pub}
     * @param keyPair
     * @return
     */
    public static String[] toBase64(KeyPair keyPair) {
        String base64Prikey = Base64.encodeAsStr(keyPair.getPrivate().getEncoded());
        String base64Pubkey = Base64.encodeAsStr(keyPair.getPublic().getEncoded());
        return (new String[]{base64Prikey, base64Pubkey});
    }

    public static PublicKey toPublicKey(String base64Pubkey) {
        try {
            byte[] encPubKey = Base64.decode(base64Pubkey);
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encPubKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGO);
            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
            return pubKey;
        } catch (Exception ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static PrivateKey toPrivateKey(String base64Prikey) {
        try {
            byte[] encPriKey = Base64.decode(base64Prikey);
            PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(encPriKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGO);
            PrivateKey priKey = keyFactory.generatePrivate(priKeySpec);
            return priKey;
        } catch (Exception ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static KeyPair toKeyPair(String base64Prikey, String base64Pubkey) {
        try {
            byte[] encPriKey = Base64.decode(base64Prikey);
            byte[] encPubKey = Base64.decode(base64Pubkey);
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encPubKey);
            PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(encPriKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGO);
            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
            PrivateKey priKey = keyFactory.generatePrivate(priKeySpec);
            return new KeyPair(pubKey, priKey);
        } catch (Exception ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] encrypt(byte text[], Key key) throws Exception {
        return doAction(text, key, Cipher.ENCRYPT_MODE);
    }

    public static byte[] decrypt(byte text[], Key key)
            throws Exception {
        return doAction(text, key, Cipher.DECRYPT_MODE);
    }

    private static byte[] doAction(byte[] text, Key key, int mode) throws Exception {
        byte dectyptedText[] = null;
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        //Cipher cipher = Cipher.getInstance("RSA/CBC/PKCS5Padding");
        cipher.init(mode, key);
        dectyptedText = cipher.doFinal(text);
        return dectyptedText;
    }

    private static byte[] decryptRSA(byte[] inBuff, Key key) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Cipher rsaCipher = Cipher.getInstance(ALGO);
            rsaCipher.init(Cipher.DECRYPT_MODE, key);

            int inBytesLeft = inBuff.length;
            // compute the max length of a one block 
            int maxBlockSize = KEY_LENGTH / 8;
            // length of data to encrypt for next step
            int currLen = 0;
            if (inBytesLeft > maxBlockSize) {
                currLen = maxBlockSize; // several steps
            } else {
                currLen = inBytesLeft; // on shot
            }

            int inOffset = 0;
            byte[] outBuff = new byte[KEY_LENGTH / 8];
            while (inBytesLeft > 0) {
                int len = rsaCipher.doFinal(inBuff, inOffset, currLen, outBuff, 0);

                // step forward in input offset
                inOffset += currLen;
                out.write(outBuff, 0, len);

                // decrement the bytes
                inBytesLeft -= currLen;

                if (inBytesLeft < maxBlockSize) {
                    currLen = inBytesLeft;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    private static byte[] encryptRSA(byte[] inBuff, Key key) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Cipher rsaCipher = Cipher.getInstance(ALGO);
            rsaCipher.init(Cipher.ENCRYPT_MODE, key);

            int inBytesLeft = inBuff.length;
            // compute the max length of a one block (K-11)
            int maxBlockSize = KEY_LENGTH / 8 - 11;
            // length of data to encrypt for next step
            int currLen = 0;
            if (inBytesLeft > maxBlockSize) {
                currLen = maxBlockSize; // several steps
            } else {
                currLen = inBytesLeft; // on shot
            }

            byte[] outBuff = new byte[KEY_LENGTH / 8];
            int inOffset = 0;
            while (inBytesLeft > 0) {
                int len = rsaCipher.doFinal(inBuff, inOffset, currLen, outBuff, 0);

                // step forward in input offset
                inOffset += currLen;
                out.write(outBuff, 0, len);

                // decrement the bytes
                inBytesLeft -= currLen;

                if (inBytesLeft < maxBlockSize) {
                    currLen = inBytesLeft;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public static byte[] sign(byte[] data, PrivateKey key) throws Exception {
        Signature rsa = Signature.getInstance(SIGNATURE);
        /* Initializing the object with a private key */

        rsa.initSign(key);

        /* Update and sign the data */
        rsa.update(data);
        return rsa.sign();
    }

    public static boolean verify(byte[] data, byte[] sig, PublicKey key) throws Exception {
        Signature rsa = Signature.getInstance(SIGNATURE);
        /* Initializing the object with the public key */
        rsa.initVerify(key);

        /* Update and verify the data */
        rsa.update(data);
        return rsa.verify(sig);
    }

    /**
     * if publicKey is not null, encrypt it too
     * @param data
     * @param key
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String sign(String data, PrivateKey key, PublicKey publicKey) throws Exception {
        byte[] dataBytes = data.getBytes("UTF-8");
//        for (byte b : dataBytes) {
//            System.out.print(b + " ");
//        }
//        System.out.println("");
        byte[] signBytes = sign(dataBytes, key);
        if (publicKey != null) {
            dataBytes = encryptRSA(dataBytes, publicKey);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(signBytes);
        out.write(dataBytes);

        byte[] result = out.toByteArray();

        return Base64.encodeAsStr(result);
        //return new String(Base64.encode(result));
    }

    /**
     * Similar to sign but for convience purpose, prefix with the own public key
     * then line break with data
     * Sign with private Key
     * @param data
     * @param key
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String signAndPrefixPublicKey(String data, KeyPair keyPair, PublicKey publicKey) throws Exception {
        String signedData = sign(data, keyPair.getPrivate(), publicKey);
        
        //System.out.println(toBase64(keyPair)[1]);
        return toBase64(keyPair)[1] + "\n" + (signedData);
    }

    /**
     * @param dataWithSignAndPublicKey
     * @return [0] the msg, [1] the public key
     * @throws Exception
     */
    public static String[] verifyWithPrefixPublicKey(String dataWithSignAndPublicKey, PrivateKey myKey) throws Exception {
        BufferedReader lineReader = new BufferedReader(new StringReader(dataWithSignAndPublicKey));
        String pkStr = lineReader.readLine();
        PublicKey key = toPublicKey(pkStr);
        System.out.println(pkStr);
        String dataWithSign = lineReader.readLine();

        return new String[]{verify(dataWithSign, key, myKey), pkStr};
    }

    /**
     * Verify sign with public key,
     * if privKey is not null, decrypt the data
     * @param dataWithSign
     * @param key
     * @param privKey
     * @return
     * @throws Exception
     */
    public static String verify(String dataWithSign, PublicKey key, PrivateKey privKey) throws Exception {
        byte[] dataWithSignBytes = Base64.decode(dataWithSign);
        //byte[] dataWithSignBytes = Base64.decode(dataWithSign);
        int signLen = (int) (KEY_LENGTH / 8);
        byte[] signBytes = Arrays.copyOfRange(dataWithSignBytes, 0, signLen);
        byte[] dataBytes = Arrays.copyOfRange(dataWithSignBytes, signLen, dataWithSignBytes.length);

        if (privKey != null) {
            dataBytes = decryptRSA(dataBytes, privKey);
        }
//        for (byte b : dataBytes) {
//            System.out.print(b + " ");
//        }
//        System.out.println("");
        if (verify(dataBytes, signBytes, key)) {
            return new String(dataBytes);
        }
        return null;
    }

    /**
     * Does not seems to work to/from
     * @deprecated
     * @param kp
     * @throws Exception
     */
    public static void getBigInteger(KeyPair kp) throws Exception {
        KeyFactory fact = KeyFactory.getInstance(ALGO);

        RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(), RSAPublicKeySpec.class);
        RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(), RSAPrivateKeySpec.class);
        System.out.println((pub.getModulus() + "/" + pub.getPublicExponent()).length()); //+ "/" + priv.getPrivateExponent()).length());
        System.out.println(toBase64(kp)[1].length());
        //System.out.println(Base64.encode(
        //        pub.getModulus() + "/" + pub.getPublicExponent() + "/" + priv.getPrivateExponent()).length());
        KeyPair kp2 = generateKeyPair(pub.getModulus(), pub.getPublicExponent(), priv.getPrivateExponent());
        System.out.println(pub.getModulus().subtract(priv.getModulus()));
        String[] str = toBase64(kp);
        String[] str2 = toBase64(kp2);
        for (int i = 0; i < str2.length; i++) {
            System.out.println(str[i].equals(str2[i]));
        }
        //System.out.println(str[0].length() + str[1].length());
        // System.out.println();
    }

    /**
     * Does not seems to work to/from
     * @deprecated
     * @param m
     * @param pub
     * @param priv
     * @return
     * @throws Exception
     */
    public static KeyPair generateKeyPair(BigInteger m, BigInteger pub, BigInteger priv) throws Exception {
        KeyFactory fact = KeyFactory.getInstance(ALGO);
        RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(m, pub);
        RSAPrivateKeySpec privSpec = new RSAPrivateKeySpec(m, priv);
        return new KeyPair(fact.generatePublic(pubSpec), fact.generatePrivate(privSpec));
    }
}
