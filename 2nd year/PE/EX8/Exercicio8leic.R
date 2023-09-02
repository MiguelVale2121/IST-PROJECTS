
set.seed(367)
amplitude <- data.frame()
for (i in 1:500){
  amostra <- rexp(628,rate = 0.58)
  media <- 1/mean(amostra)
  erro <- 2*((qnorm((1+0.93)/2) * media)/sqrt(628))
  amplitude <- rbind(amplitude,erro)
}
print(mean(amplitude[,1]))