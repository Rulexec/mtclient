package by.muna.mt;

public interface IMTConnectionListener {
    void onConnected(MTClient client);
    void onConnectError(MTClient client, boolean graceful);
}
