package com.nhn.pinpoint.web.vo;

import com.nhn.pinpoint.common.hbase.HBaseTables;
import com.nhn.pinpoint.common.util.BytesUtils;
import com.nhn.pinpoint.common.util.TransactionIdUtils;

public class TransactionId {
    public static final String AGENT_DELIMITER = "=";
    public static final int AGENT_NAME_MAX_LEN = HBaseTables.AGENT_NAME_MAX_LEN;

    protected final String agentId;
    protected final long agentStartTime;
    protected final long transactionSequence;

    public TransactionId(byte[] transactionId) {
        if (transactionId == null) {
            throw new NullPointerException("transactionId must not be null");
        }
        if (transactionId.length < BytesUtils.LONG_LONG_BYTE_LENGTH + AGENT_NAME_MAX_LEN) {
            throw new IllegalArgumentException("invalid transactionId");
        }

        this.agentId = BytesUtils.toStringAndRightTrim(transactionId, 0, AGENT_NAME_MAX_LEN);
        this.agentStartTime = BytesUtils.bytesToLong(transactionId, AGENT_NAME_MAX_LEN);
        this.transactionSequence = BytesUtils.bytesToLong(transactionId, BytesUtils.LONG_BYTE_LENGTH + AGENT_NAME_MAX_LEN);
    }

    public TransactionId(byte[] transactionId, int offset) {
        if (transactionId == null) {
            throw new NullPointerException("transactionId must not be null");
        }
        if (transactionId.length < BytesUtils.LONG_LONG_BYTE_LENGTH + AGENT_NAME_MAX_LEN) {
            throw new IllegalArgumentException("invalid transactionId");
        }

        this.agentId = BytesUtils.toStringAndRightTrim(transactionId, offset, AGENT_NAME_MAX_LEN);
        this.agentStartTime = BytesUtils.bytesToLong(transactionId, offset + AGENT_NAME_MAX_LEN);
        this.transactionSequence = BytesUtils.bytesToLong(transactionId, offset + BytesUtils.LONG_BYTE_LENGTH + AGENT_NAME_MAX_LEN);
    }

    public TransactionId(String agentId, long agentStartTime, long transactionSequence) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        this.agentId = agentId;
        this.agentStartTime = agentStartTime;
        this.transactionSequence = transactionSequence;
    }

    public TransactionId(String transactionId) {
        if (transactionId == null) {
            throw new NullPointerException("transactionId must not be null");
        }

        final int agentIdIndex = transactionId.indexOf(AGENT_DELIMITER);
        if (agentIdIndex == -1) {
            throw new IllegalArgumentException("transactionId delimiter not found:" + transactionId);
        }
        String[] parseId = TransactionIdUtils.parseTransactionId(transactionId);
        this.agentId = parseId[0];
        this.agentStartTime = Long.parseLong(parseId[1]);
        this.transactionSequence = Long.parseLong(parseId[2]);
    }

    public String getAgentId() {
        return agentId;
    }

    public long getAgentStartTime() {
        return agentStartTime;
    }

    public long getTransactionSequence() {
        return transactionSequence;
    }

    public byte[] getBytes() {
        return BytesUtils.stringLongLongToBytes(agentId, HBaseTables.AGENT_NAME_MAX_LEN, agentStartTime, transactionSequence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionId traceId = (TransactionId) o;

        if (agentStartTime != traceId.agentStartTime) return false;
        if (transactionSequence != traceId.transactionSequence) return false;
        if (!agentId.equals(traceId.agentId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = agentId.hashCode();
        result = 31 * result + (int) (agentStartTime ^ (agentStartTime >>> 32));
        result = 31 * result + (int) (transactionSequence ^ (transactionSequence >>> 32));
        return result;
    }

    @Override
    public String toString() {
        String traceId = TransactionIdUtils.formatString(agentId, agentStartTime, transactionSequence);
        return "TransactionId [" + traceId + "]";
    }

    public String getFormatString() {
        return TransactionIdUtils.formatString(agentId, agentStartTime, transactionSequence);
    }

}
