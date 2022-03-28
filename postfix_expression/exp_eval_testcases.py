"""Project 2 Unittest File
Course: CPE202
Quarter: Spring 2020
Author: Nickzad Bayati
"""

import unittest
from exp_eval import \
    postfix_eval, \
    infix_to_postfix, \
    prefix_to_postfix


class TestCases(unittest.TestCase):
    def test_postfix_eval(self):
        self.assertEqual(postfix_eval('5 1 2 + 4 ^ + 3 -'), 83)
        self.assertEqual(postfix_eval('2 3 * 6 /'), 1)
        self.assertRaises(ValueError, postfix_eval, '1 0 /')

    def test_infix_to_postfix(self):
        self.assertEqual(
            infix_to_postfix('3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3'),
            '3 4 2 * 1 5 - 2 3 ^ ^ / +')

    def test_prefix_to_postfix(self):
        self.assertEqual(
            prefix_to_postfix('* - 3 / 2 1 - / 4 5 6'),
            '3 2 1 / - 4 5 / 6 - *')


if __name__ == '__main__':
    unittest.main()
