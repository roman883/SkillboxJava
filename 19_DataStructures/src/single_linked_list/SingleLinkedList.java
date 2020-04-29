package single_linked_list;

public class SingleLinkedList {
    private ListItem top;

    public void push(ListItem item) {
        if (top != null) {
            item.setNext(top);
        }
        top = item;
    }

    public ListItem pop() {
        ListItem item = top;
        if (top != null) {
            top = top.getNext();
            item.setNext(null);
        }
        return item;
    }

    public void removeTop() {
        if (top != null) {
            top = top.getNext();
        }
    }

    public void removeLast() {
        if (top != null) {                                  // Если в списке есть хотя бы один элемент (top)
            if (top.getNext() != null) {                    // Если у текущего элемента есть следующий, входим в цикл перебора
                ListItem previous = top;
                ListItem current = top.getNext();
                while (current.getNext() != null) {         // перебираем элементы по ссылке getNext до тех пор, пока не будет пустая ссылка (у последнего элемента)
                    previous = current;                     // присваиваем предыдущему текущий
                    current = current.getNext();            // текущему присваиваем следующий
                }                                           // когда элементы закончатся, в current будет последний, в previous предпоследний
                previous.setNext(null);                     // для удаления достаточно у previous обнулить ссылку на следующий
            }
        }
    }
}