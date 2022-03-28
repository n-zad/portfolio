"""Project 2: StackArray class.
Methods: pop(), push(), peek(), is_empty(), size().
Course: CPE202
Quarter: Spring 2020
Author: Nickzad Bayati
"""


class StackArray:
    """Implement Stack ADT with an array.
    Attributes:
      arr (list): array used for Stack ADT
      capacity (int): size of array arr
      num_items (int): number of items in the Stack
    """

    def __init__(self):
        """Constructor method to initialize class attributes."""
        self.arr = [None] * 2
        self.capacity = 2
        self.num_items = 0

    def __repr__(self):
        """Method to create a representation of the object.
        Returns:
          str: String representation
        """
        return 'Stack: %a' % self.arr

    def __eq__(self, other):
        """Method to compare StackArray objects.
        Args:
          other (StackArray): object to compare with
        Returns:
          bool: True if all the attributes are equal, otherwise False
        """
        return isinstance(other, StackArray) \
            and self.arr == other.arr \
            and self.capacity == other.capacity \
            and self.num_items == self.num_items

    def push(self, item):
        """Method to append value to the top of the stack.
        Args:
          item (any): item to add to stack
        """
        self.arr[self.num_items] = item
        self.num_items += 1
        if self.capacity == self.num_items:
            self.enlarge(self.arr)

    def pop(self):
        """Method to remove the item at the top of the stack.
        Returns:
          any: item that was removed
        """
        item = self.peek()
        self.arr[self.num_items - 1] = None
        self.num_items -= 1
        if self.num_items <= self.capacity / 4:
            self.shrink(self.arr)
        return item

    def peek(self):
        """Method to see the item at the top of the stack.
        Returns:
          any: item at the top of the stack
        """
        if self.is_empty():
            raise IndexError
        return self.arr[self.num_items - 1]

    def is_empty(self):
        """Method that checks if stack is empty.
        Returns:
          bool: True if stack is empty, False otherwise
        """
        return self.num_items == 0

    def size(self):
        """Method to check the size of the stack.
        Returns:
          int: size of the stack
        """
        return self.capacity

    def enlarge(self, arr):
        """Method to double the capacity of the stack
        Args:
          arr (list): copy of stack array before enlarging
        """
        self.capacity *= 2
        self.arr = [None] * self.size()
        n = self.num_items
        self.num_items = 0
        for i in range(n):
            self.push(arr[i])

    def shrink(self, arr):
        """Method to half the capacity of the stack
        Args:
          arr (list): copy of stack before shrinking
        """
        self.capacity //= 2
        self.arr = [None] * self.size()
        n = self.num_items
        self.num_items = 0
        for i in range(n):
            self.push(arr[i])
