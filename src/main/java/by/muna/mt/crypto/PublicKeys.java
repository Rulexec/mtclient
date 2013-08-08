package by.muna.mt.crypto;

import by.muna.mt.tl.MTRsaPublicKey;
import by.muna.tl.TypedData;
import by.muna.util.BytesUtil;
import by.muna.util.LongUtil;
import org.spongycastle.asn1.pkcs.RSAPublicKey;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PublicKeys {
    private static Map<Long, RSAPublicKey> publicKeys = new HashMap<Long, RSAPublicKey>();

    static {
        try {
            InputStream is = PublicKeys.class.getResourceAsStream("/keys/rsa.keys");
            Scanner sc = new Scanner(is);

            while (sc.hasNextLine()) {
                String fileName = sc.nextLine();

                if (fileName.isEmpty()) continue;

                is = PublicKeys.class.getResourceAsStream("/keys/" + fileName);
                PemReader pemReader = new PemReader(new InputStreamReader(is));
                PemObject pemObject = pemReader.readPemObject();
                pemReader.close();

                RSAPublicKey rsaKey = RSAPublicKey.getInstance(pemObject.getContent());

                long fingerprint = PublicKeys.calcPublicKeyFingerprint(
                    rsaKey.getModulus(),
                    rsaKey.getPublicExponent()
                );

                PublicKeys.publicKeys.put(fingerprint, rsaKey);
            }

            sc.close();
        } catch (IOException e) {
            System.err.println("public keys read error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static RSAPublicKey getPublicKey(long fingerprint) {
        return PublicKeys.publicKeys.get(fingerprint);
    }

    public static long calcPublicKeyFingerprint(BigInteger modulus, BigInteger exponent) {
        byte[] n = BytesUtil.asBE(modulus);
        byte[] e = BytesUtil.asBE(exponent);

        return LongUtil.asLE(
            Hashes.SHA1(new TypedData(MTRsaPublicKey.CONSTRUCTOR, new Object[] { n, e }).serialize()),
            12
        );
    }
}
