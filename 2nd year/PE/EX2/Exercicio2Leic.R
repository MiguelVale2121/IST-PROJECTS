library("ggplot2")
library("readxl")

data <- data.frame(read_excel("path"))

df <- filter(data, row_number() >= 5)

df <- select(df, c('...1', '...40', '...52', '...66', '...74', '...86', '...100'))
colnames(df) <- c('Ano', 'AT_Homens', 'GR_Homens', 'IS_Homens', 'AT_Mulheres', 'GR_Mulheres', 'IS_Mulheres')
df <- filter(df, row_number() >= 44)
df <- filter(df, row_number() < 19)
df <- data.frame(df)
df$AT_Homens <- as.numeric(df$AT_Homens);df$AT_Mulheres <- as.numeric(df$AT_Mulheres)
df$GR_Homens <- as.numeric(df$GR_Homens);df$GR_Mulheres <- as.numeric(df$GR_Mulheres)
df$IS_Homens <- as.numeric(df$IS_Homens);df$IS_Mulheres <- as.numeric(df$IS_Mulheres)

ggplot() +
  geom_line(df,mapping = aes(Ano,AT_Homens,linetype = "Homem", colour="Austria",group = 1)) + 
  geom_line(df,mapping = aes(Ano,AT_Mulheres,linetype = "Mulher", colour="Austria",group = 1)) +
  geom_line(df,mapping = aes(Ano,GR_Homens,linetype = "Homem", colour="Gr�cia",group = 1)) +
  geom_line(df,mapping = aes(Ano,GR_Mulheres,linetype = "Mulher", colour="Gr�cia",group = 1)) +
  geom_line(df,mapping = aes(Ano,IS_Homens,linetype = "Homem", colour="Islandia",group = 1)) +
  geom_line(df,mapping = aes(Ano,IS_Mulheres,linetype = "Mulher", colour="Islandia",group = 1)) +
  scale_y_continuous(limits = c(75,85)) + 
  labs(x= "Ano", y="N�mero total registado", linetype = "G�nero", colour = "Pa�s")

