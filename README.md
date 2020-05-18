# Histogram-Map-Reduce-Java-Combiner
CSE 6331 : Map Reduce in Java to take pixel values (ie RGB values) as input and give count of each pixel intensity for each color using map reduce with combiner which makes it more efficient by combining intermediate results 

In this project, you are asked to improve the performance of your code developed in Project 1 in two different ways: 
1) using a combiner 
2) using in-mapper combining. 

You need to write two Hadoop Map-Reduce jobs in the same file project2/src/main/java/Histogram.java. 
Each one of this Map-Reduce jobs will read the same input file but will produce output to a different output directory: 
in your Java main program, both Map-Reduce jobs will read args[0] as the input file with the pixels (which is pixels-small.txt or pixels-large.txt). 
The first Map-Reduce job will write on the output directory args[1] (which is output) while the second Map-Reduce job will write on the output directory args[1]+"2" (which is output2). 

The proper steps and explaination is given in project2.txt file.
