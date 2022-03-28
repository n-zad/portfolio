"""Project 2: Evaluate postfix expressions,
    and convert prefix and infix expressions to postfix.
Functions: eval_postfix, infix_to_postfix, prefix_to_postfix.
Course: CPE202
Quarter: Spring 2020
Author: Nickzad Bayati
"""

from stack_array import StackArray


def postfix_eval(input_str):
    """Function that evaluates a postfix expression using a Stack ADT.
    Args:
      input_str (str): input string containing a valid postfix expression
    Raises:
      ValueError: if a divisor is 0
    Returns:
      float: the evaluated postfix expression
    """
    postfix_str = input_str.split()
    stack = StackArray()
    for item in postfix_str:
        if item == '+':  # addition
            x = stack.pop()
            y = stack.pop()
            stack.push(y + x)
        elif item == '-':  # subtraction
            x = stack.pop()
            y = stack.pop()
            stack.push(y - x)
        elif item == '*':  # multiplication
            x = stack.pop()
            y = stack.pop()
            stack.push(y * x)
        elif item == '/':  # division
            x = stack.pop()
            if x == 0:  # divisor is 0
                raise ValueError
            y = stack.pop()
            stack.push(y / x)
        elif item == '^':  # exponentiation
            x = stack.pop()
            y = stack.pop()
            stack.push(y ** x)
        else:
            stack.push(int(item))
    return stack.peek()


def infix_to_postfix(input_str):
    """Function that converts a infix expression to postfix.
    Args:
      input_str (str): input string containing a valid infix expression
    Returns:
      str: string containing a valid postfix expression
    """
    infix_str = input_str.split()
    stack = StackArray()
    rpn = []
    for item in infix_str:
        if item in {'+', '-', '*', '/', '^'}:  # operator
            if not stack.is_empty():
                o2 = stack.peek()
                if (item in {'+', '-', '*', '/'} and o2 in {'*', '/', '^'}) \
                        or (item in {'+', '-'} and o2 in {'+', '-'}):
                    rpn.append(stack.pop())
            stack.push(item)
        elif item == '(':  # opening parenthesis
            stack.push(item)
        elif item == ')':  # closing parenthesis
            while stack.peek() != '(':
                rpn.append(stack.pop())
            stack.pop()
        else:  # operand
            rpn.append(item)
    # Append the rest of the stack into the RPN expression.
    while not stack.is_empty():
        rpn.append(stack.pop())
    return ' '.join(rpn)


def prefix_to_postfix(input_str):
    """Function that converts a prefix expression to postfix.
    Args:
      input_str (str): input string containing a valid prefix expression
    Returns:
      str: string containing a valid postfix expression
    """
    prefix_str = input_str.split()
    stack = StackArray()
    for item in reversed(prefix_str):
        if item in {'+', '-', '*', '/', '^'}:  # operator
            x = stack.pop()
            y = stack.pop()
            temp_str = ' '.join([x, y, item])
            stack.push(temp_str)
        else:  # operand
            stack.push(item)
    return stack.peek()
