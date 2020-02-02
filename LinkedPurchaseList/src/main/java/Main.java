import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Course course = session.get(Course.class, 12);
        Student student = session.get(Student.class, 3);
        Subscription subscription = session.get(Subscription.class, new subscriptionPk(course, student));

        System.out.println("\n----------- Subscription --------------");
        System.out.println(subscription.getSubscriptionDate().toString() + " "
                + subscription.getId().getCourse().getName() + " " + subscription.getId().getStudent().getName());

        System.out.println("\n----------- All subscriptions for one course --------------");
        course.getSubscriptions().forEach(i -> System.out.println(i.getSubscriptionDate().toString() + " " +
                i.getId().getCourse().getName() + " " + i.getId().getStudent().getName()));

        // Getting the purchase
        System.out.println("\n----------- Purchase --------------");
        Purchase purchase = session.get(Purchase.class, new PurchasePk(student.getName(), course.getName()));
        System.out.println(purchase.getPrice() + " " + purchase.getSubscriptionDate().toString() + " " + purchase.getStudentName());

        // CriteriaQuery Experiment
        System.out.println("\n----------- Courses over 100000 and >= 9 hours --------------");
        CriteriaBuilder cBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Course> cQuery = cBuilder.createQuery(Course.class);
        Root<Course> cRoot = cQuery.from(Course.class);
        cQuery.select(cRoot).where(cBuilder
                .and(cBuilder.greaterThan(cRoot.<Integer>get("price"), 100000),
                        cBuilder.greaterThanOrEqualTo(cRoot.get("duration"), "9")))
                .orderBy(cBuilder.desc(cRoot.get("price")));
        List<Course> courses = session.createQuery(cQuery).setMaxResults(4).getResultList();
        for (Course c : courses) {
            System.out.println(c.getName() + " " + c.getDuration() + " " + c.getPrice());
        }

        // HQL Experiments
        System.out.println("\n----------- Students > 36 years old --------------");
        String hql = "From " + Student.class.getSimpleName() + " Where age > 36";
        List<Student> studentList = session.createQuery(hql).setMaxResults(5).getResultList();
        studentList.forEach(s -> System.out.println(s.getName() + " - " + s.getAge()));

        // All purchases
        hql = "From " + Purchase.class.getSimpleName();
        List<Purchase> purchaseList = session.createQuery(hql).getResultList();

        for (int i = 0; i < purchaseList.size(); i++) {
            String courseName = purchaseList.get(i).getCourseName();
            String studentName = purchaseList.get(i).getStudentName();
            String hqlCourseId = "From Course Where name = :courseName";
            String hqlStudentId = "From Student Where name = :studentName";
            Course courseTemp = session.createQuery(hqlCourseId, Course.class)
                    .setParameter( "courseName", courseName ).getSingleResult();
            Student studentId = session.createQuery(hqlStudentId, Student.class)
                    .setParameter( "studentName", studentName ).getSingleResult();
            LinkedPurchaseList linkedPurchaseList = new LinkedPurchaseList(new LinkedPurchaseListPk(courseTemp, studentId));
            session.save(linkedPurchaseList);
            System.out.println("Added " + linkedPurchaseList.getCourse().getId() +
                    " - " + linkedPurchaseList.getStudent().getId());
        }

        String hqlLinkedList = "From " + LinkedPurchaseList.class.getSimpleName();
        List<LinkedPurchaseList> linkedPurchases = session.createQuery(hqlLinkedList).getResultList();
        System.out.println("\n=========== Получаем все элементы из LinkedPurchaseList ============");
        linkedPurchases.forEach(p -> System.out.println(p.getCourse().getId() + " " + p.getStudent().getId()));

        transaction.commit();
        sessionFactory.close();
    }
}
