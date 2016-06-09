import sys

args = sys.argv
args.pop(0) #ignore self

output_file = args.pop()
data_file = args.pop()


writer = open(output_file, "w")

with open(data_file, "r") as f:
    for line in f:
        value = 0.0
        if (not line.startswith("#")):
            data = line.strip().split()
            if (data == []): #ignore any lines start with \n
                break
            
            for i in xrange(1,len(data)):
                value += float(data[i])
            
            value /= float(len(data)-1)
            writer.write(str(data[0]) + " " + str(value) + "\n")

writer.close()
    
        
    
