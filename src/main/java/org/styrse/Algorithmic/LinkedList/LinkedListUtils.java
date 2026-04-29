package org.styrse.Algorithmic.LinkedList;

public class LinkedListUtils {
    public static Node reverse(Node head) {
        // prev ender som det nye "head" til sidst
        Node prev = null;
        Node current = head;
        while (current != null) {
            Node next = current.next;
            // vend pilen så current peger bagud i stedet for fremad
            current.next = prev;
            prev = current;
            current = next;
        }
        return prev;
    }
}
