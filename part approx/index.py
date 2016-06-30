import sys
from random import random
from math import exp, fabs, pi, cos
import matplotlib.pyplot as plt
import matplotlib.mlab as mlab
import numpy as np

START_POS = float(-10)
END_POS   = float( 10)

INTERVALS = 100
AUTO_INTERVALS = 100
QUANTITY = 1000

ALPHA = float(1)
BETA = float(0)

length_of_buffer = 50

# знакопеременная

# Cauchy
# def f(x):
# 	return 1/(pi*ALPHA * (1 + ((x-BETA)/ALPHA)**2))

# Laplace
def f(x):
	return (ALPHA/2)*exp(-ALPHA*fabs(x-BETA))

def auto_r(tau, inlist, avg, var):
	output = 0
	for t in range(0, QUANTITY-tau):
		output = output + ((inlist[t]-avg)*(inlist[t+tau]-avg))
	output = output/(var * (len(inlist)-tau))
	return output

def man_r(tau, inalpha, inbeta):
	return exp(-inalpha*tau) * cos(inbeta * pi * tau)

# def makeRnd(instart, inend, inintervals):
def makeRnd():
	steps = [];	modes = [];	density = []

	step = (END_POS - START_POS)/INTERVALS
	i = START_POS
	while i <= END_POS:
		steps.append(i)
		i = round((i + step), 7)

	sum = 0
	for x in steps:
		tmp = f(x)
		sum = sum + tmp
		modes.append(tmp)

	# write p_i + p_{i+1}/\sum{p}
	currSum = float(0)
	for x in modes:
		density.append(currSum/sum)
		currSum = currSum + x
	density.append(currSum/sum)

	it = 0
	output = []
	while it < QUANTITY:

		currInt = 0
		rnd_pos = random()

		for x in range(1, len(density)):
			if (rnd_pos >= density[x-1]) and (rnd_pos < density[x]):
				currInt = x

		outstr = steps[currInt-1] + (steps[currInt] - steps[currInt-1])*random()
		output.append(outstr)

		it = it + 1

	return output

def list_to_file(inlist, filename):
	outfile = open(filename, 'w')
	for x in inlist:
		outfile.write("{:.20f}".format(x).replace('.', ',')+'\r\n')

def min_diff_index(num, lst):
	min_val = sys.float_info.max
	min_ind = sys.float_info.max
	for it, val in enumerate(lst):
		if (max(val, num) - min(val, num) < min_val):
			min_val = max(val, num) - min(val, num)
			min_ind = it
	return min_ind
def min_diff_val(num, lst):
	min_val = sys.float_info.max
	min_ind = sys.float_info.max
	for it, val in enumerate(lst):
		if (max(val, num) - min(val, num) < min_val):
			min_val = max(val, num) - min(val, num)
			min_ind = it
	return lst[min_ind]
def max_diff_index(num, lst):
	max_val = 0
	max_ind = 0
	for it, val in enumerate(lst):
		if (max(val, num) - min(val, num) > max_val):
			max_val = max(val, num) - min(val, num)
			max_ind = it
	return max_ind
def max_diff_val(num, lst):
	max_val = 0
	max_ind = 0
	for it, val in enumerate(lst):
		if (max(val, num) - min(val, num) > max_val):
			max_val = max(val, num) - min(val, num)
			max_ind = it
	return lst[max_ind]

def swap_items(inlist):

	buff = []
	output = []
	tmpinput = inlist.copy()
	# print(tmpinput)

	output.append(tmpinput.pop(0))
	# print('output: '+str(output))
	while len(tmpinput):
		while len(buff) < length_of_buffer:
			buff.append(tmpinput.pop(0))
			# print('buff: '+str(buff))
		output.append(buff.pop(max_diff_index(output[-1], buff)))
		# print('buff: '+str(buff))
		# print('output: '+str(output))
	while len(buff) > 0:
		output.append(buff.pop(max_diff_index(output[-1], buff)))
		# print('buff: '+str(buff))
		# print('output: '+str(output))

	return output

def make_hist(inlist):
	# n, bins, patches = plt.hist(ordered, 50, normed=1, facecolor='g', alpha=0.75)
	n, bins, patches = plt.hist(inlist, INTERVALS, normed=1, facecolor='g')

	# x = np.arange(START_POS, END_POS, (END_POS - START_POS)/INTERVALS)
	x = np.arange(START_POS, END_POS, (END_POS - START_POS)/INTERVALS)
	y = [f(z) for z in x]
	plt.plot(x, y, 'r--')
	plt.show()

def auto_corr(inlist):
	avg = np.mean(inlist)
	variance = np.var(inlist)
	# for x in inlist:
	# 	variance = variance + pow((x-avg), 2)
	# variance = variance/(len(inlist)-1)
	rs = []

	for tau in range(0, AUTO_INTERVALS):
		rs.append(auto_r(tau, inlist, avg, variance))

	return rs

def plot_auto_corr(auto_corr):
	x = range(0,AUTO_INTERVALS)
	plt.plot(x, auto_corr, 'g')
	# plt.show()
	plt.savefig('auto_corr.png')

def plot_corrs(auto_corr, man_corr):
	x = range(0,AUTO_INTERVALS)
	plt.plot(x, auto_corr, 'g')
	plt.plot(x, man_corr, 'r')
	# plt.show()
	plt.savefig('auto_corr.png')

def plot_array(inlist, name):
	plt.plot(range(0, len(inlist)), inlist)
	plt.savefig(name)
	# plt.show()
	plt.clf()

def plot_arrays(inlists):
	for list in inlists:
		plt.plot(range(0, len(list)), list)
	# plt.savefig('processes.png')
	plt.show()

cor_ALPHA = float(sys.argv[1])
# cor_BETA = float(sys.argv[2])
cor_BETA = 1


unordered = makeRnd()
ordered = swap_items(unordered)

list_to_file(unordered, 'unordered.txt');
list_to_file(ordered, 'ordered.txt');

# plot_arrays([unordered, ordered])
plot_array(unordered, 'unordered.png')
plot_array(ordered, 'ordered.png')
# plot_corrs(auto_corr(unordered), [man_r(x, cor_ALPHA, cor_BETA) for x in range(0,AUTO_INTERVALS)])
# plot_auto_corr(auto_corr(unordered))

# plot_array(ordered)
