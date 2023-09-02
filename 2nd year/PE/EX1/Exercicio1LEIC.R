library("ggplot2")
library("readxl")


table <- data.frame(read_excel("path"))
Exercicio1 <- table[6:36,1:3]

Exercicio1 <- data.frame(Exercicio1 ,row.names=1)
names(Exercicio1) <- c("2004" , "2018")
Exercicio1 <- Exercicio1[c("SK - Eslov�quia","CY - Chipre","IS - Isl�ndia"),]


Result <- data.frame(pa�ses = rep(c("SK - Eslov�quia","CY - Chipre","IS - Isl�ndia"), times = 2),
                     anos = rep(c(names(Exercicio1[1]),names(Exercicio1[2])), each = 3),
                     res�duos = rep(c(Exercicio1[1,1],Exercicio1[2,1],Exercicio1[3,1],Exercicio1[1,2],Exercicio1[2,2],Exercicio1[3,2])))


ggplot(Result, aes(fill=pa�ses,y=res�duos,x=rep(c("2004","2018"),3))) + 
  geom_bar(position = 'dodge',stat='identity') +
  labs(x="Anos",y="res�duos")
