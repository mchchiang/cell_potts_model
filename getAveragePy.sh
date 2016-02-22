#!/bin/bash                                                                     

for i in {1..6}
do
   for j in {0..8..2}
   do
       python ../../GetAverage.py "r2_200_200_1000_a_${i}.${j}_lam_1.0_P_0.0_t_\
10000_run_"*.dat "r2_200_200_1000_a_${i}.${j}_lam_1.0_P_0.0_t_10000_run_avg.dat\
" 0 1 2
   done
done