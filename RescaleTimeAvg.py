import sys
from itertools import izip
import math

args = sys.argv
args.pop(0) #ignore self

input_file = open(args.pop(0), "r")
output_file = open(args.pop(), "w")

files = [open(i, "r") for i in args]

accept_rate = 0.0

#average the time scale
for rows in izip(*files):
    for line in rows:
        if (line.startswith("accept_rate")):
            data = line.strip().split()
            accept_rate = accept_rate + float(data[1])
        else:
            break

accept_rate = accept_rate / len(files)

for line in input_file:
    if (not line.startswith("#")):
        data = line.strip().split()
        if (data != []): #ignore any lines start with \n
            output = ""
            for i in xrange(len(data)):
                #rescale the time column
                if (i == 0):
                    output += str(float(data[0]) * accept_rate) + " "
                elif (i < len(data)-1):
                    output += data[i] + " "
                else:
                    output += data[i] + "\n"
            output_file.write(output)

for f in files:
    f.close()

input_file.close()
output_file.close()

    
        
