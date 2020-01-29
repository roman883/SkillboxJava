import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Subscriptions")
public class Subscription {

    @EmbeddedId
    private CompositeId subscriptionId;

    @Column(name = "subscription_date")
    private Date subscriptionDate;

    public Subscription() {
    }

    public Subscription(CompositeId subscriptionId, Date subscriptionDate) {
        this.subscriptionId = subscriptionId;
        this.subscriptionDate = subscriptionDate;
    }

    public CompositeId getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(CompositeId subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }
}
