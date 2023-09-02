library("ggplot2")
library("readxl")

Antas_Espinho <- data.frame(read_excel("path", 
                            range = "A1:A8785"))
Antas_Espinho$Antas.Espinho <- as.numeric(Antas_Espinho$Antas.Espinho)
Sobreiras_Porto <- data.frame(read_excel("path", 
                                                          range = "I1:I8785"))
Sobreiras_Porto$Sobreiras.Porto <- as.numeric(Sobreiras_Porto$Sobreiras.Porto)
ggplot()+
  geom_histogram(Antas_Espinho,mapping = aes(`Antas.Espinho`,fill = "Antas.Espinho"),bins = 200,alpha=0.5,colour = "blue")+
  geom_histogram(Sobreiras_Porto,mapping = aes(`Sobreiras.Porto`, fill = "Sobreiras.Porto"),bins = 200,alpha=0.5,colour = "red")+
  labs(title = "Numero de observa��es", y = "frequencia", x ="Nivel de Ozono", fill="Localidade")
