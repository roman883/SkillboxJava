package binary_tree;

import java.util.ArrayList;
import java.util.List;

public class BinaryTree {
    private Node root;

    public void addNode(String data) {
        if (root == null) { // еще нет узлов
            root = new Node(data);
        } else { // есть как минимум один узел, ищем по ветвям
            Node parent = root;
            while (true) {
                if (data.compareTo(parent.getData()) < 0) { // Левое
                    if (parent.getLeft() == null) { // если пусто, то помещаем свой элемент сюда
                        Node newNode = new Node(data);
                        newNode.setParent(parent);
                        parent.setLeft(newNode);
                        break;
                    } else {
                        parent = parent.getLeft();
                    }
                } else {                            // правое
                    if (parent.getRight() == null) { // если пусто, то помещаем свой элемент сюда
                        Node newNode = new Node(data);
                        newNode.setParent(parent);
                        parent.setRight(newNode);
                        break;
                    } else {
                        parent = parent.getRight();
                    }
                }
            }
        }
    }

    public List<Node> searchNodes(String data) {
        List<Node> resultList = new ArrayList<>();
        if (root != null) {
            Node parent = root;
            while (true) {
                if (data.compareTo(parent.getData()) < 0) {
                    Node left = parent.getLeft();
                    if (left == null) {    // то что ищем меньше, чем родитель, но в левой ветви элементы кончились
                        break;
                    } else {               // еще есть потомки, значит двигаемся дальше по этой ветви
                        parent = left;
                        continue;
                    }
                }
                if (data.compareTo(parent.getData()) >= 0) {
                    if (data.equals(parent.getData())) resultList.add(parent);              // Нашли значение, вносим в список
                    Node right = parent.getRight();
                    if (right == null) { // то что ищем больше, чем родитель, но в правой ветви пусто, значит элементы кончились
                        break;
                    } else { // еще есть потомки, значит двигаемся дальше по этой ветви
                        parent = right;
                    }
                }
            }
        }
        return resultList;
    }
}

//                do {
//                    if (data.compareTo(parent.getData()) < 0) { // меньше нуля - левое поддерево
//                        nodeToPlace = left;
//                    } else {
//                        nodeToPlace = right;
//                    }
//                    if (nodeToPlace != null) { // Если место не пустое, то двигаемся в след цикл
//                        parent = nodeToPlace;
//                    }
//                } while (nodeToPlace != null);
//                nodeToPlace = new Node(data);
//                nodeToPlace.setParent(parent);
//                parent.set