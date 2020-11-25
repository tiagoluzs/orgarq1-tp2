.text
main: jal teste
      jr $ra
      div $t0, $t1	
      lw $t0, 0($t1)
      beq $t0,$t1, juca2
      beq $t0,$t1, main
      blez $t1, main
juca2:ori $t1, $t1, 2
      nor $t0, $t1, $t2
teste:sll $t1, $t2, 1
      slti $t0, $t1, 10
