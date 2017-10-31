package cm.aptoide.pt.billing.authorization;

public class MetadataAuthorization extends Authorization {

  private final String metadata;

  public MetadataAuthorization(String id, String customerId, Status status, String transactionId,
      String metadata) {
    super(id, customerId, status, transactionId);
    this.metadata = metadata;
  }

  public String getMetadata() {
    return metadata;
  }
}
