package org.styrse.Algorithmic.LinkedList;

public class ListFactory {
    // Bygger en liste af et antal int-værdier
    public static Node buildList(int... values) {
        if (values.length == 0) return null;
        Node head = new Node(values[0]);
        Node current = head;
        for (int i = 1; i < values.length; i++) {
            current.next = new Node(values[i]);
            current = current.next;
        }
        return head;
    }


}
