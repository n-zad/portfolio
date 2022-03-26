# Author: Nickzad Bayati
# Description: program to divide a 64 bit dividend by a 31 bit divisor.


# declare global so programmer can see actual addresses.
.globl welcome
.globl prompt
.globl prompt2
.globl prompt3
.globl resultText
.globl resultText2

#  Data Area (this area contains strings to be displayed during the program)
.data

welcome:
	.asciiz " This program divides a 64 bit number (split into two 32 bit numbers) by a 31 bit divisor \n\n"

prompt:
	.asciiz " Enter high dividend: "

prompt2:
	.asciiz " Enter low dividend: "

prompt3:
	.asciiz " Enter divisor: "

resultText: 
	.asciiz " \n\n Result = "

resultText2:
	.asciiz " , "

#Text Area (i.e. instructions)
.text

# Java:
#	public static int divide(int high, int low, int divisor) {
#		int h = high;
#		int l = low;
#		int d = divisor;
#		while(d > 1) {
#			// check for overflow from h
#			int temp = h & 1;
#			
#			// right shift all three numbers
#			h >>>= 1;
#			l >>>= 1;
#			d >>>= 1;
#			
#			// add overflow to l
#			temp <<= 31;
#			l += temp;
#		}
#		int[] result = {h, l}
#		return result;
#	}

main:

	# Display the welcome message (load 4 into $v0 to display)
	ori     $v0, $0, 4			

	# This generates the starting address for the welcome message.
	# (assumes the register first contains 0).
	lui     $a0, 0x1001
	syscall

	# Display prompts
	ori     $v0, $0, 4			
	
	# This is the starting address of the prompt (notice the
	# different address from the welcome message)
	lui     $a0, 0x1001
	ori     $a0, $a0,0x5d
	syscall

	# Read 1st integer from the user (5 is loaded into $v0, then a syscall)
	ori     $v0, $0, 5
	syscall

	# Clear $s0 for the result
	ori     $s0, $0, 0	

	# Set $s0 to 1st integer (high dividend)
	addu    $s0, $v0, $0
	
	# Display prompt2 (4 is loaded into $v0 to display)
	# 0x76 is hexidecimal for 118 decimal (the length of the previous two messages)
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0x74
	syscall

	# Read 2nd integer 
	ori	$v0, $0, 5			
	syscall
	# $v0 now has the value of the second integer
	
	# Set $s1 to 2nd integer (low dividend)
	addu    $s1, $v0, $0
	
	# Display prompt3 (4 is loaded into $v0 to display)
	# 0x8c is hexidecimal for 140 decimal (the length of the previous three messages)
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0x8a
	syscall

	# Read 3rd integer 
	ori	$v0, $0, 5			
	syscall
	# $v0 now has the value of the second integer
	
	# Set $s2 to 3rd integer (divisor)
	addu    $s2, $v0, $0
	
	# enter loop
	j loop

loop:
	# exit loop if divisor ($s2) is 1
	addi $t0, $0, 1
	beq $s2, $t0, exit
	
	# check for overflow from high dividend to low dividend
	and $t1, $s0, $t0
	
	# shift right all three numbers (high/low dividend and divisor)
	srl $s0, $s0, 1
	srl $s1, $s1, 1
	srl $s2, $s2, 1
	
	# add overflow to low dividend ($s1)
	sll $t1, $t1, 31
	add $s1, $s1, $t1
	
	# iterate loop
	j loop

exit:
	# Display the result text
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0x9d
	syscall
	
	# Display the high result
	# load 1 into $v0 to display an integer
	ori     $v0, $0, 1			
	add 	$a0, $s0, $0
	syscall
	
	# Display the additional result text
	ori     $v0, $0, 4			
	lui     $a0, 0x1001
	ori     $a0, $a0,0xaa
	syscall
	
	# Display the low result
	# load 1 into $v0 to display an integer
	ori     $v0, $0, 1			
	add 	$a0, $s1, $0
	syscall
	
	# Exit (load 10 into $v0)
	ori     $v0, $0, 10
	syscall

