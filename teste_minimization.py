import numpy as np
from scipy.optimize import minimize

def xplus(x):
	if x < 0.0:
		return 0.0
	return x

def O(ai, bi, aj, bj):
	if ai >= aj:
		return (1.0/(bj**4.0)) * (xplus(bj**2 - (ai-aj)**2))**2
	return (1.0/(bi**4.0)) * (xplus(bi**2 - (ai-aj)**2))**2

def func(x):        
	n = x.shape[0]/2
	N = x.shape[0]
	count = 0.0
	for i in range(0, N, 2):
		for j in range(i+2, N, 2):
			count += O(x[i], 50, x[j], 50) * O(x[i+1], 50, x[j+1], 50)
	count *= (2.0/(n*(n-1)))

	return count

def read_elems():
	with open('points.rect') as f:
		array = []
		for line in f:
			array.append([float(x) for x in line.split()])
		return [item for sublist in array for item in sublist]


x0 = np.array(read_elems())
res = minimize(func, x0)

f = open('point_solve.rect', 'w+')
for q in res.x:
	f.write(str(q)+"\n")
f.close()


print(res.x)
print(res)
