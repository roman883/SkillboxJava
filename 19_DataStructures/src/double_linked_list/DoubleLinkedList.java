package double_linked_list;

public class DoubleLinkedList {
    private ListItem head;
    private ListItem tail;

    public ListItem getHeadElement() {
        return head;
    }

    public ListItem getTailElement() {
        return tail;
    }

    public ListItem popHeadElement() {
        ListItem item = head;
        if (item != null) {
            head = head.getNext();
            head.setPrev(null);
            item.setNext(null);
        }
        return item;
    }

    public ListItem popTailElement() {
        ListItem item = tail;
        if (item != null) {
            tail = tail.getPrev();
            tail.setNext(null);
            item.setPrev(null);
        }
        return item;
    }

    public void removeHeadElement() {
        if (head != null) {
            head = head.getNext();
            head.setPrev(null);
        }
    }

    public void removeTailElement() {
        if (tail != null) {
            tail = tail.getPrev();
            tail.setNext(null);
        }
    }

    public void addToHead(ListItem item) {
        item.setNext(head);
        if (head != null) {
            head.setPrev(item);
            item.setNext(head);
            if (head.getNext() == null) { // Элемент последний (единственный)
                tail = head;
            }
        }
        head = item;
        if (tail == null) { // если список был пуст
            tail = head;
        }
    }

    public void addToTail(ListItem item) {
        item.setPrev(tail);
        if (tail != null) {
            tail.setNext(item);
            item.setPrev(tail);
            if (tail.getPrev() == null) { // Элемент первый (единственный)
                head = tail;
            }
        }
        tail = item;
        if (head == null) { // если список был пуст
            head = tail;
        }
    }
}