set.seed(646)
library("ggplot2")

m = 1200
n = seq(100,2500,by=100)
per=0.3
nc = 0.91
lambda = 4.36
lambdac = 0.12
mediaAmp = c()
mediaAmpC = c()

for(i in 1:length(n)){
  MEDIA = c()
  MEDIAC = c()
  amostra = replicate(m,rexp(n[i],rate = lambda))
  media = 1/mean(amostra)
  erro = 2*((qnorm((1+nc)/2) * media)/sqrt(n[i]))
  amostrac = rbind(amostra[1:(n[i]*(1-per)),],replicate(m,rexp((per*n[i]),rate = lambdac)))
  mediac = 1/mean(amostrac)
  erroc = 2*((qnorm((1+nc)/2) * mediac)/sqrt(n[i]))
  amplitude = erro
  amplitudec = erroc
  MEDIA = c(MEDIA,amplitude)
  MEDIAC = c(MEDIAC,amplitudec)
  mediaAmp = c(mediaAmp,mean(MEDIA))
  mediaAmpC = c(mediaAmpC,mean(MEDIAC))
}

ggplot() + 
  geom_point(data.frame(mediaAmp),mapping = aes(y=`mediaAmp`, col = "M�dia Amplitude N�o Contaminada", x= n)) + 
  geom_point(data.frame(mediaAmpC),mapping = aes(y=`mediaAmpC`, col = "M�dia Amplitude  Contaminada", x= n)) + 
  labs(x = "Dimens�o", y = "M�dias Amplitudes", title = "Gr�fico MA e MA Contaminada ", colour = "Legenda")
