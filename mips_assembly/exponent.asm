# Author: Nickzad Bayati
# Description: Calculate x raised to the power of y,
# 	where x and y are given by the user.


# declare global so programmer can see actual addresses.
.globl welcome
.globl prompt
.globl prompt2
.globl resultText

#  Data Area (this area contains strings to be displayed during the program)
.data

welcome:
	.asciiz " This program calculates x to the power of y \n\n "

prompt:
	.asciiz " Enter integer x: "

prompt2:
	.asciiz " Enter integer y: "

resultText: 
	.asciiz " \n Result = "

#Text Area (i.e. instructions)
.text

# Java:
#	public static int exponent(int x, int y) {
#		// x is the base, and y is the power
#		int sum = x;
#		int inc = sum;
#		for(int i = y - 1; i > 0; i--) {
#			for(int j = x - 1; j > 0; j--) {
#				sum += inc;
#			}
#			inc = sum;
#		}
#		return sum;
#	}

main:

	# Display the welcome message (load 4 into $v0 to display)
	ori     $v0, $0, 4			

	# This generates the starting address for the welcome message.
	# (assumes the register first contains 0).
	lui     $a0, 0x1001
	syscall

	# Display prompt
	ori     $v0, $0, 4			
	
	# This is the starting address of the prompt (notice the
	# different address from the welcome message)
	lui     $a0, 0x1001
	ori     $a0, $a0,0x32
	syscall

	# Read 1st integer from the user (5 is loaded into $v0, then a syscall)
	ori     $v0, $0, 5
	syscall

	# Clear $s0 for the result
	ori     $s0, $0, 0	

	# Set $s0 to 1st integer (x)
	addu    $s0, $v0, $0
	
	# Display prompt (4 is loaded into $v0 to display)
	# 0x44 is hexidecimal for 68 decimal (the length of the previous two messages)
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0x44
	syscall

	# Read 2nd integer 
	ori	$v0, $0, 5			
	syscall
	# $v0 now has the value of the second integer
	
	# Set $s1 to 2nd integer (y)
	addu    $s1, $v0, $0
	
	# use $s2 and $s3 for loops iterating x and y times
	add $s2, $s0, $0
	addi $s2, $s2, -1
	add $s3, $s1, $0
	addi $s3, $s3, -1
	
	# use $s4 for increment (initially equal to $s0)
	add $s4, $s0, $0
	
	# enter nested loops
	j outerloop

outerloop:
	# exit loop if $s3 is 0
	beq $s3, $0, exit
	
	# enter innerloop with parameters $s2, $s0, and $s4
	add $a0, $s2, $0
	add $a1, $s0, $0
	add $a2, $s4, $0
	jal innerloop
	
	# set $s0 and $s4 to $v0
	add $s0, $v0, $0
	add $s4, $v0, $0
	
	# decrement $s3 and iterate loop
	addi $s3, $s3, -1	
	j outerloop

innerloop:
	# exit inner loop if $a0 is 0
	beq $a0, $0, exitloop
	
	# add increment ($a2) to sum ($a1)
	add $a1, $a1, $a2
	
	# decrement $a0 and iterate loop
	addi $a0, $a0, -1
	j innerloop

exitloop:
	# set sum as $v0
	add $v0, $a1, $0
	
	# jump back to outerloop using $ra
	jr $ra
	
exit:
	# Display the result text
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0, 0x57
	syscall
	
	# Display the result
	# load 1 into $v0 to display an integer
	ori     $v0, $0, 1			
	add 	$a0, $s0, $0
	syscall
	
	# Exit (load 10 into $v0)
	ori     $v0, $0, 10
	syscall

