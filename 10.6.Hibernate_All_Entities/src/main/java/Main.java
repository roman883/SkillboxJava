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

        System.out.println("1");
        Course course = session.get(Course.class, 13);
        System.out.println("2");
        CompositeId compositeId = new CompositeId(session.get(Student.class, 4), session.get(Course.class, 13));
        System.out.println("3");
        System.out.println(course.getName() + " ");
        System.out.println("id " + compositeId.toString());
        Subscription subscription = session.get(Subscription.class, compositeId);
//        course.getSubscriptionList().forEach(i -> i.getSubscriptionDate().toString());

        transaction.commit();
        sessionFactory.close();
    }
}
