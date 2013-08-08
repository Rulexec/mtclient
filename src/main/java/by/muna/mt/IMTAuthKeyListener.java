package by.muna.mt;

public interface IMTAuthKeyListener {
    void onAuthKeyResult(MTClient client, byte[] authKey, boolean graceful);
}
