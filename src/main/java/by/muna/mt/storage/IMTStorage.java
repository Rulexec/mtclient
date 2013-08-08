package by.muna.mt.storage;

public interface IMTStorage {
    int getTimeDiff();
    void syncTime(int serverTime);

    long getServerSalt(long authKeyId, long sessionId);
    void addServerSalt(long authKeyId, long sessionId, int validSince, int validUntil, long salt);
    void serverSalt(long authKeyId, long sessionId, long salt);

    ISeqNoPoller getSeqNoPoller(long authKeyId, long sessionId);
}
