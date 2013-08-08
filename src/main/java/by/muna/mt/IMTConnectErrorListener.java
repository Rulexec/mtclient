package by.muna.mt;

public interface IMTConnectErrorListener {
    void onConnectError(MTClient client, boolean graceful);
}
