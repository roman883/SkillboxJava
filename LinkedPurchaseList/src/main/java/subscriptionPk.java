import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class subscriptionPk implements Serializable {

    @ManyToOne(cascade = CascadeType.ALL)
    private Course course;

    @ManyToOne(cascade = CascadeType.ALL)
    private Student student;

    public subscriptionPk() {
    }

    public subscriptionPk(Course course, Student student) {
        this.course = course;
        this.student = student;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        subscriptionPk subscriptionPk = (subscriptionPk) o;
        return course.equals(subscriptionPk.course) &&
                student.equals(subscriptionPk.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course, student);
    }
}
