package by.muna.mt;

import by.muna.tl.ITypedData;

public interface IMTDataListener {
    void onData(MTClient client, long authKeyId, long sessionId, long messageId, ITypedData data);
}
