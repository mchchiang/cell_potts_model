import sys
from itertools import izip
import math

args = sys.argv
args.pop(0) #ignore self

err_col = int(args.pop())
avg_col = int(args.pop())
ref_col = int(args.pop())

output_file = args.pop()

files = [open(i, "r") for i in args]
writer = open(output_file, "w")

for rows in izip(*files):
    ref = 0.0
    value = 0.0
    error = 0.0
    hasData = False

    for line in rows:
        if (line.startswith("#")):
            data = line.strip().split()
            if (data == []): #ignore any lines start with \n
                break
            
            value = value + float(data[avg_col])
            ref = float(data[ref_col])
            sigma = float(data[err_col])
            error = error + sigma * sigma
            hasData = True

    if (hasData == True):
        value = value / len(rows)
        err = 1.0 / len(rows) * math.sqrt(error)
        writer.write(str(ref) + " " + str(value) + " " + str(err) + "\n")

for f in files:
    f.close()
writer.close()
    
        
    
