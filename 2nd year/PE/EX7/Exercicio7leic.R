set.seed(187)

Amostras = replicate(150, rbinom(46,47,0.47), simplify = FALSE)

MediaAmostras = sapply(Amostras, mean)
Media= mean(MediaAmostras)
ValorEsperado=47*0.47

abs(Media-(ValorEsperado))