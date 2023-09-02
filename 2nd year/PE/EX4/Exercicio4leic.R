library("readxl")

Exercicio4 <- data.frame(read_excel("path"))

plot(Exercicio4$Colesterol,Exercicio4$TAD , main="TAD E COLESTEROL DE 76 PACIENTES"
     , ylab = "TAD", xlab = "Colesterol",col = "blue")