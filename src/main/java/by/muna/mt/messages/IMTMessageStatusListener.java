package by.muna.mt.messages;

public interface IMTMessageStatusListener {
    void onConstructed(long messageId);
    void onSent(long messageId);
    void onDelivered(long messageId);

    void onConnectionError(long messageId);
}
