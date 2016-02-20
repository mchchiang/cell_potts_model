#!/bin/bash

inputfiles="$@"

echo "# filename, a, error, b, error" > output_file.dat

for f in $inputfiles
do

fitparameters=$( echo "
f(x)=a+x*b
set fit errorvariables
fit f(x) '$f' via a,b
set print '-'
print a,a_err,b,b_err
" | gnuplot 2> /dev/null )


echo "$f $fitparameters" >> output_file.dat

done