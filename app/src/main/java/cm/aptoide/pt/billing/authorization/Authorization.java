/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 02/01/2017.
 */

package cm.aptoide.pt.billing.authorization;

public class Authorization {

  private final String id;
  private final String customerId;
  private final Status status;
  private final String transactionId;

  public Authorization(String id, String customerId, Status status, String transactionId) {
    this.id = id;
    this.customerId = customerId;
    this.status = status;
    this.transactionId = transactionId;
  }

  public String getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public boolean isPending() {
    return Status.PENDING.equals(status);
  }

  public boolean isProcessing() {
    return Status.PENDING_SYNC.equals(status) || Status.PROCESSING.equals(status);
  }

  public boolean isPendingSync() {
    return Status.PENDING_SYNC.equals(status);
  }

  public boolean isFailed() {
    return Status.FAILED.equals(status);
  }

  public boolean isActive() {
    return Status.ACTIVE.equals(status) || Status.REDEEMED.equals(status);
  }

  public Status getStatus() {
    return status;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public enum Status {
    NEW, PENDING, PENDING_SYNC, PROCESSING, REDEEMED, ACTIVE, FAILED
  }
}
