import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "Courses")
public class Course implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private CourseType type;

    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    private Teacher teacher;

    @Column(name = "students_count", nullable = true)
    private Integer studentsCount;

    private Integer price;

    @Column(name = "price_per_hour")
    private float pricePerHour;

    @OneToMany(mappedBy = "id.course", cascade = CascadeType.ALL)
    private Set<Subscription> subscriptions = new HashSet<Subscription>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
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

    public Course() {
    }

    public Course(int id, String name, int duration, CourseType type, String description, Teacher teacher, int studentsCount, int price, float pricePerHour, Set<Subscription> subscriptions) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.type = type;
        this.description = description;
        this.teacher = teacher;
        this.studentsCount = studentsCount;
        this.price = price;
        this.pricePerHour = pricePerHour;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public CourseType getType() {
        return type;
    }

    public void setType(CourseType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public int getStudentsCount() {
        return studentsCount;
    }

    public void setStudentsCount(int studentsCount) {
        this.studentsCount = studentsCount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(float pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

}
