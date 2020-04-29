import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "purchaselist")
public class Purchase implements Serializable {

    @EmbeddedId
    private PurchasePk pk;

    private int price;

    @Column(name = "subscription_date")
    private Date subscriptionDate;

    public Purchase() {
    }

    public PurchasePk getPk() {
        return pk;
    }

    public void setPk(PurchasePk pk) {
        this.pk = pk;
    }

    public String getStudentName() {
        return pk.getStudentName();
    }

    public void setStudentName(String name) {
        pk.setStudentName(name);
    }

    public String getCourseName() {
        return pk.getCourseName();
    }

    public void setCourseName(String name) {
        pk.setCourseName(name);
    }

    public Purchase(String studentName, String courseName, int price, Date subscriptionDate) {
        this.price = price;
        this.subscriptionDate = subscriptionDate;
        pk.setCourseName(courseName);
        pk.setStudentName(studentName);
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

}
