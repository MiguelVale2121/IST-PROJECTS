library("ggplot2")

set.seed(1830)
variancia = ((8-4)^2)/12
valorExato = (8+4)/2
n <-c(5,30,84)
for(i in n){
  amostra = replicate(1460,runif(i,4,8),simplify = FALSE)
  MediaAmostra = sapply(amostra,mean)
  df <- data.frame(MediaAmostra)
  
  print(ggplot()+
    geom_histogram(df,mapping = aes(x=df[,1],y=..density..),bins = 30,color ="black", fill="red")+
    geom_function(fun = dnorm, args=list(mean=valorExato,sd=sqrt(variancia/i)), color = "blue")+
    labs(title=paste(c("Número de observações n=", i), collapse = "")
         ,x = "Media das Amostras de dimensao " ))
}

