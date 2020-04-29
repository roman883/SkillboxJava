package double_linked_list;

public class ListItem
{
    private String data;
    private ListItem prev;
    private ListItem next;

    public ListItem(String data) { this.data = data; }

    public String getData()
    {
        return data;
    }

    public void setNext(ListItem item)
    {
        next = item;
    }

    public ListItem getNext()
    {
        return next;
    }

    public void setPrev(ListItem item)
    {
        prev = item;
    }

    public ListItem getPrev()
    {
        return prev;
    }
}