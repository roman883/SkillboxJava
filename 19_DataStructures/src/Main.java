import binary_tree.BinaryTree;
import binary_tree.Node;
import double_linked_list.DoubleLinkedList;
import single_linked_list.ListItem;
import single_linked_list.SingleLinkedList;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // some tests
        // singleLinkedList
        System.out.println("\n=== Test SingleLinkedList");
        ListItem sItem = new ListItem("1");
        System.out.println("Создаем элемент " + sItem.getData() + " и вносим его в список");
        SingleLinkedList sList = new SingleLinkedList();
        sList.push(sItem);
        ListItem sItem2 = new ListItem("2");
        ListItem sItem3 = new ListItem("3");
        sList.push(sItem2);
        sList.push(sItem3);
        System.out.println("- Добавляем в список в головную часть элементы " + sItem2.getData() + " и " + sItem3.getData());
        System.out.println("Первый в списке: " + sList.pop().getData() + " получаем его и удаляем из списка" +
                "\n- Снова удаляем первый элемент"); // получаем первый элемент и удаляем его из списка
        sList.removeTop();
        System.out.println("Первый в списке: " + sList.pop().getData());

        // doubleLinkedList
        System.out.println("\n=== Test DoubleLinkedList");
        double_linked_list.ListItem dItem = new double_linked_list.ListItem("1"); // 1
        System.out.println("Создаем элемент " + dItem.getData() + " и вносим его в список");
        DoubleLinkedList dList = new DoubleLinkedList();
        dList.addToHead(dItem);
        double_linked_list.ListItem dItem2 = new double_linked_list.ListItem("2");
        double_linked_list.ListItem dItem3 = new double_linked_list.ListItem("3");
        double_linked_list.ListItem dItem4 = new double_linked_list.ListItem("4");
        double_linked_list.ListItem dItem5 = new double_linked_list.ListItem("5");
        dList.addToHead(dItem2); // 2 - 1
        dList.addToHead(dItem3); // 3 - 2 - 1
        System.out.println("- Добавляем в начало элементы: " + dItem2.getData() + " и " + dItem3.getData());
        dList.addToTail(dItem4); // 3 - 2 - 1 - 4
        dList.addToTail(dItem5); //  3 - 2 - 1 - 4 - 5
        System.out.println("- Добавляем в конец элементы: " + dItem4.getData() + " и " + dItem5.getData());
        System.out.println("Первый элемент: " + dList.getHeadElement().getData());
        System.out.println("Последний элемент: " + dList.getTailElement().getData());
        System.out.println("- Получаем и удаляем первый с головы: " + dList.popHeadElement().getData()); // 2 - 1 - 4 - 5
        System.out.println("- Получаем и удаляем последний элемент: " + dList.popTailElement().getData()); // 2 - 1 - 4
        System.out.println("- Удаляем первый и последний элементы, выводим оставшиеся: ");
        dList.removeHeadElement(); // 1 - 4
        dList.removeTailElement(); // 1
        System.out.println("\t Первый элемент: " + dList.getHeadElement().getData());
        System.out.println("\t Последний элемент: " + dList.getTailElement().getData());

        // BinaryTree test
        System.out.println("\n=== Test BinaryTree");
        Node node = new Node("5");
        BinaryTree bTree = new BinaryTree();
        System.out.println("Добавляем элемент " + node.getData() + " в дерево");
        bTree.addNode(node.getData());
        for (int i = 0; i < 20; i++) {
            String temp = String.valueOf((int) (Math.random() * 10));
            bTree.addNode(temp); // Элементы от 1 до 10 чтобы были повторения
            System.out.println("\tДобавили узел " + temp);
        }
        String randomNodeDataToSearch = String.valueOf((int) (Math.random() * 10));
        System.out.println("Ищем случайный узел: " + randomNodeDataToSearch);
        List<Node> nodes = bTree.searchNodes(randomNodeDataToSearch);
        nodes.forEach(System.out::println);
    }
}
