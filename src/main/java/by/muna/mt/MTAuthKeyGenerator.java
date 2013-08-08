package by.muna.mt;

import by.muna.mt.crypto.*;
import by.muna.mt.messages.IMTMessageStatusListener;
import by.muna.mt.tl.*;
import by.muna.tl.ITypedData;
import by.muna.tl.TL;
import by.muna.tl.TypedData;
import by.muna.util.BytesUtil;
import org.spongycastle.asn1.pkcs.RSAPublicKey;

import java.math.BigInteger;

public class MTAuthKeyGenerator implements IMTMessageStatusListener {
    private MTClient client;
    private IMTAuthKeyListener listener;

    private byte[] nonce = MTClient.getSecureRandomBytes(16);
    private byte[] serverNonce;

    private byte[] newNonce = MTClient.getSecureRandomBytes(32);

    private byte[] b = MTClient.getSecureRandomBytes(256);

    private byte[] pq, p, q;
    private int g;
    private byte[] dhPrime, gA;

    private AesIge aes;

    private long retryId = 0;

    private byte[] authKey;

    public MTAuthKeyGenerator(MTClient client, IMTAuthKeyListener listener) {
        this.client = client;
        this.listener = listener;
    }

    public void start() {
        this.client.send(
            0, 0, new TypedData(MTReqPq.CONSTRUCTOR, new Object[] { this.nonce }), this
        );
    }

    private void finish(byte[] authKey) {
        this.finish(authKey, true);
    }
    private void finish(boolean graceful) {
        this.finish(null, graceful);
    }
    private void finish(byte[] authKey, boolean graceful) {
        this.listener.onAuthKeyResult(this.client, authKey, graceful);
        this.client.forgotAuthKeyGenerator(this);
    }

    @Override public void onConstructed(long messageId) {}
    @Override public void onSent(long messageId) {}
    @Override public void onDelivered(long messageId) {}

    @Override
    public void onConnectionError(long messageId) {
        this.finish(false);
    }

    public void sendReqDHParams(long fingerprint, byte[] encryptedData) {
        this.client.send(
            0, 0,
            new TypedData(MTReqDHParams.CONSTRUCTOR)
                .setTypedData(MTReqDHParams.nonce, this.nonce)
                .setTypedData(MTReqDHParams.serverNonce, this.serverNonce)
                .setTypedData(MTReqDHParams.p, this.p)
                .setTypedData(MTReqDHParams.q, this.q)
                .setTypedData(MTReqDHParams.publicKeyFingerprint, fingerprint)
                .setTypedData(MTReqDHParams.encryptedData, encryptedData),
            this
        );
    }

    public void sendSetDHParams(byte[] encryptedData) {
        this.client.send(
            0, 0,
            new TypedData(MTSetClientDHParams.CONSTRUCTOR, new Object[] {
                this.nonce, this.serverNonce, encryptedData
            }),
            this
        );
    }

    public void onData(ITypedData data) {
        switch (data.getId()) {
        case MTResPQ.CONSTRUCTOR_ID:
            this.processResPQ(data);
            break;
        case MTServerDHParamsOk.CONSTRUCTOR_ID:
            this.processResDH(data);
            break;
        case MTServerDHParamsFail.CONSTRUCTOR_ID:
            // TODO: check for hash
            this.finish(true);
            break;
        case MTDhGenOk.CONSTRUCTOR_ID:
            // TODO: check for hash

            this.finish(this.authKey);
            break;
        case MTDhGenRetry.CONSTRUCTOR_ID:
            // TODO: check for hash, retry
        case MTDhGenFail.CONSTRUCTOR_ID:
            // TODO : check for hash

            this.finish(true);

            break;
        default:
            this.finish(false);
        }
    }

    private void processResPQ(ITypedData resPQ) {
        this.serverNonce = resPQ.getTypedData(MTResPQ.serverNonce);

        this.pq = resPQ.getTypedData(MTResPQ.pq);

        long publicKeyFingerprint = 0;
        RSAPublicKey key = null;

        Long[] fingerprints =  resPQ.<ITypedData>getTypedData(MTResPQ.serverPublicKeyFingerprints)
            .getTypedData(0);
        for (Long fingerprint : fingerprints) {
            key = PublicKeys.getPublicKey(fingerprint);

            if (key != null) {
                publicKeyFingerprint = fingerprint;
                break;
            }
        }

        if (key != null) {
            PQ pq = new PQ(this.pq);
            this.p = pq.p;
            this.q = pq.q;

            this.sendReqDHParams(publicKeyFingerprint, this.createReqDHParamsEncryptedData(key));
        } else {
            System.err.println("No such key: " + publicKeyFingerprint);
            this.finish(false);
        }
    }
    private byte[] createReqDHParamsEncryptedData(RSAPublicKey key) {
        byte[] innerData = new TL(MTPQInnerData.TYPE,
            new TypedData(MTPQInnerData.CONSTRUCTOR)
                .setTypedData(MTPQInnerData.pq, this.pq)
                .setTypedData(MTPQInnerData.p, this.p)
                .setTypedData(MTPQInnerData.q, this.q)
                .setTypedData(MTPQInnerData.nonce, this.nonce)
                .setTypedData(MTPQInnerData.serverNonce, this.serverNonce)
                .setTypedData(MTPQInnerData.newNonce, this.newNonce)
        ).serialize();

        byte[] hash = Hashes.SHA1(innerData);

        byte[] unencrypted = BytesUtil.union(
            hash,
            innerData,
            MTClient.getRandomBytes(255 - hash.length - innerData.length)
        );

        byte[] encrypted = BytesUtil.asBE(
            new BigInteger(1, unencrypted
            ).modPow(key.getPublicExponent(), key.getModulus()),

            256
        );

        return encrypted;
    }

    private void processResDH(ITypedData dhParams) {
        byte[] encryptedAnswer = dhParams.getTypedData(MTServerDHParamsOk.encryptedAnswer);

        byte[] serverNewSHA = Hashes.SHA1(this.serverNonce, this.newNonce);

        byte[] tmpAesKey = BytesUtil.union(
            Hashes.SHA1(this.newNonce, this.serverNonce),
            BytesUtil.slice(serverNewSHA, 0, 12)
        );

        byte[] tmpAesIV = BytesUtil.union(
            BytesUtil.slice(serverNewSHA, 12, 8),
            Hashes.SHA1(this.newNonce, this.newNonce),
            BytesUtil.slice(this.newNonce, 0, 4)
        );

        this.aes = new AesIge(tmpAesKey, tmpAesIV);

        byte[] answerWithHash = this.aes.decrypt(encryptedAnswer);

        ITypedData answer = TL.parse(MTTL.SCHEMA, answerWithHash, 20);

        //byte[] answerHash = BytesUtil.slice(answerWithHash, 0, 20);
        //byte[] realAnswerHash = Encryption.SHA1(answerWithHash, 20, answer.calcSize());

        this.g = answer.getTypedData(MTServerDHInnerData.g);
        this.dhPrime = answer.getTypedData(MTServerDHInnerData.dhPrime);
        this.gA = answer.getTypedData(MTServerDHInnerData.gA);

        this.sendSetDHParams(this.createSetDHEncryptedData());
    }
    private byte[] createSetDHEncryptedData() {
        String gString = Long.toString(((long) this.g) & 0xffffffffffffffffL);
        BigInteger bBig = new BigInteger(1, this.b);
        BigInteger dhPrimeBig = new BigInteger(1, this.dhPrime);

        byte[] gB = BytesUtil.asBE(new BigInteger(gString).modPow(bBig, dhPrimeBig));

        BigInteger gABig = new BigInteger(1, this.gA);

        this.authKey = BytesUtil.asBE(gABig.modPow(bBig, dhPrimeBig));

        byte[] innerData = new TL(MTClientDHInnerData.TYPE,
            new TypedData(MTClientDHInnerData.CONSTRUCTOR)
                .setTypedData(MTClientDHInnerData.nonce, this.nonce)
                .setTypedData(MTClientDHInnerData.serverNonce, this.serverNonce)
                .setTypedData(MTClientDHInnerData.retryId, this.retryId)
                .setTypedData(MTClientDHInnerData.gB, gB)
        ).serialize();

        byte[] dataWithHash = BytesUtil.union(
            Hashes.SHA1(innerData),
            innerData,
            MTClient.getRandomBytes(MTClient.calcPadding(innerData.length + 20, 16))
        );

        return this.aes.encrypt(dataWithHash);
    }

    public byte[] getNonce() {
        return this.nonce;
    }
}
