
set.seed(1770)
N=696
simExpo=rexp(N,0.53)
DistEmpirica = ecdf(simExpo)

round(abs((1-DistEmpirica(2))-(1-pexp(2,0.53))),6)
