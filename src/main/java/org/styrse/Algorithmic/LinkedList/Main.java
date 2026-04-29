package org.styrse.Algorithmic.LinkedList;

public class Main {
    public static void main(String[] args) {
      Node head = ListFactory.buildList(1, 5, 7, 12, 17);
        System.out.println("LinkedList: " + head.toString());
        head = LinkedListUtils.reverse(head);
        System.out.println("LinkedList reversed: " + head.toString());
    }
}
