import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Main {

    public static void main(String[] args) {

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Course course = session.get(Course.class, 12);
        Student student = session.get(Student.class, 3);
        Subscription subscription = session.get(Subscription.class, new PK(course, student));

        System.out.println(subscription.getSubscriptionDate().toString());

        course.getSubscriptions().forEach(i -> System.out.println(i.getSubscriptionDate().toString() + " " +
                i.getId().getCourse().getName() + " " + i.getId().getStudent().getName()));

        transaction.commit();
        sessionFactory.close();
    }
}
