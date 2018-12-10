package cm.aptoide.pt.dataprovider.model.v7;

public class ClaimPromotionsResponse extends BaseV7Response {

  Errors errors;

  public ClaimPromotionsResponse() {
  }

  public Errors getClaimErrors() {
    return errors;
  }

  public void setClaimErrors(Errors errors) {
    this.errors = errors;
  }

  public static class Errors {
    String code;
    String description;

    public Errors() {
    }

    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }
  }
}
