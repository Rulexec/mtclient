package by.muna.mt.storage;

public interface ISeqNoPoller {
    int pollSeqNo(boolean increment);
}
