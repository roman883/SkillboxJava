public class Movement {

    private String currency;
    private String operationType;
    private String operationDescription;
    private double sum;

    public Movement(String currency, String operationType, String operationDescription, double sum) {
        this.currency = currency;
        this.operationType = operationType;
        this.operationDescription = operationDescription;
        this.sum = sum;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public String getOperationDescription() {
        return operationDescription;
    }

    public void setOperationDescription(String operationDescription) {
        this.operationDescription = operationDescription;
    }


}
