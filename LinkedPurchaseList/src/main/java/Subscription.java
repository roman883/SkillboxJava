import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Subscriptions")
public class Subscription implements Serializable {

    public Subscription() {
    }

    @EmbeddedId
    private subscriptionPk id;

    @Column(name = "subscription_date")
    private Date subscriptionDate;

    public subscriptionPk getId() {
        return id;
    }

    public void setId(subscriptionPk id) {
        this.id = id;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }
}

