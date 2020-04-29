import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "Students")
public class Student implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Integer age;

    @Column(name = "registration_date")
    private Date registrationDate;

    @OneToMany(mappedBy = "id.student", cascade = CascadeType.ALL)
    private Set<Subscription> subscriptions = new HashSet<Subscription>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private Set<LinkedPurchaseList> purchaseList = new HashSet<LinkedPurchaseList>();

    public Set<LinkedPurchaseList> getPurchaseList() {
        return purchaseList;
    }

    public void setPurchaseList(Set<LinkedPurchaseList> purchaseList) {
        this.purchaseList = purchaseList;
    }

    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

}
