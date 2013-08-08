package by.muna.mt.storage;

import java.util.HashMap;
import java.util.Map;

public class MTTransientStorage implements IMTStorage {
    private int timeDiff = 0;

    private Map<Long, Map<Long, Long>> serverSalts = new HashMap<Long, Map<Long, Long>>();
    private Map<Long, Map<Long, ISeqNoPoller>> seqNumbers = new HashMap<Long, Map<Long, ISeqNoPoller>>();

    @Override
    public int getTimeDiff() {
        return timeDiff;
    }

    @Override
    public void syncTime(int serverTime) {
        this.timeDiff = serverTime - (int) (System.currentTimeMillis() / 1000);
    }

    @Override
    public long getServerSalt(long authKeyId, long sessionId) {
        Map<Long, Long> sessionServerSalts = this.serverSalts.get(authKeyId);

        if (sessionServerSalts == null) return 0;

        Long salt = sessionServerSalts.get(sessionId);

        if (salt == null) return 0;
        else return salt;
    }

    @Override
    public void addServerSalt(long authKeyId, long sessionId, int validSince, int validUntil, long salt) {
        // TODO
    }

    @Override
    public void serverSalt(long authKeyId, long sessionId, long salt) {
        Map<Long, Long> sessionServerSalts = this.serverSalts.get(authKeyId);

        if (sessionServerSalts == null) {
            sessionServerSalts = new HashMap<Long, Long>();
            this.serverSalts.put(authKeyId, sessionServerSalts);
        }

        sessionServerSalts.put(sessionId, salt);
    }

    @Override
    public ISeqNoPoller getSeqNoPoller(long authKeyId, long sessionId) {
        Map<Long, ISeqNoPoller> sessionSeqNumbers = this.seqNumbers.get(authKeyId);

        if (sessionSeqNumbers == null) {
            sessionSeqNumbers = new HashMap<Long, ISeqNoPoller>();
            this.seqNumbers.put(authKeyId, sessionSeqNumbers);
        }

        ISeqNoPoller seqNoPoller = sessionSeqNumbers.get(sessionId);

        if (seqNoPoller == null) {
            seqNoPoller = new ISeqNoPoller() {
                private int seqNo = 0;

                @Override
                public int pollSeqNo(boolean increment) {
                    if (increment) {
                        this.seqNo += 2;

                        return this.seqNo - 1;
                    } else {
                        return this.seqNo;
                    }
                }
            };
            sessionSeqNumbers.put(sessionId, seqNoPoller);
        }

        return seqNoPoller;
    }
}
