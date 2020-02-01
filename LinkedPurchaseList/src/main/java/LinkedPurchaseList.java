import javax.persistence.*;


@Entity
@Table(name = "LinkedPurchaseList")
@IdClass(LinkedPurchaseListPk.class)
public class LinkedPurchaseList {

    @Id
    @Column(name = "course_id", nullable = false)
    private Course course;

    @Id
    @Column(name = "student_id", nullable = false)
    private Student student;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LinkedPurchaseList() {
    }

    public LinkedPurchaseList(LinkedPurchaseListPk pk) {
        course = pk.getCourse();
        student = pk.getStudent();
    }
}
