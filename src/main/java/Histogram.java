import java.io.*;
import java.util.*;
import java.util.Scanner;
import java.util.Vector;
import java.net.URI;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;

/* single color intensity */
class Color implements WritableComparable<Color>
{
    public int type;       /* red=1, green=2, blue=3 */
    public int intensity;  /* between 0 and 255 */
    /* need class constructors, toString, write, readFields, and compareTo methods */
    
    Color()
    {
    }
    
    Color(int a, int b)
    {
        this.type = a;
        this.intensity = b;
    }
    
    public void write(DataOutput out)throws IOException
    {
        out.writeInt(this.type);
        out.writeInt(this.intensity);
    }
    
    public void readFields(DataInput in) throws IOException 
    {
        // TODO Auto-generated method stub
        type=in.readInt();
        intensity=in.readInt();
    }
    
     public int compareTo(Color o) 
     {
         if (this.type==o.type)
         {
             if (this.intensity==o.intensity)
             {
                 return 0;
             }
             else if (this.intensity > o.intensity)
             {
                 return 1;
             }
             else
             {
                 return -1;
             }
         }
         else if (this.type > o.type)
         {
             return 1;
         }
         else
         {
             return -1;
         }   
     }
     
     public String toString() 
    { 
        return this.type+" "+this.intensity; 
    }

	public int hashCode()
	{
		return this.type*1000 + this.intensity;
	}
}


public class Histogram
{
    public static class HistogramMapper extends Mapper<Object,Text,Color,IntWritable> 
    {		
        @Override
        public void map ( Object key, Text value, Context context )throws IOException, InterruptedException 
        {
            /* write your mapper code */
            Scanner s = new Scanner(value.toString()).useDelimiter(",");
            int red = s.nextInt();
            int green = s.nextInt();
            int blue = s.nextInt();
            context.write(new Color(1,red),new IntWritable(1));
            context.write(new Color(2,green),new IntWritable(1));
            context.write(new Color(3,blue),new IntWritable(1));
            s.close();
        }
    }
	
	public static class MyCombiner extends Reducer<Color,IntWritable,Color,IntWritable> 
    {
        public void reduce ( Color key_c, Iterable<IntWritable> values_c, Context context_c )throws IOException, InterruptedException 
        {
            /* write your reducer code */
            int sum =0;
            for(IntWritable x1: values_c)
            {
                sum+=x1.get();
            };
            context_c.write( key_c ,new IntWritable(sum));
        }       
    }

    public static class HistogramReducer extends Reducer<Color,IntWritable,Color,IntWritable> 
    {
        @Override
        public void reduce ( Color key, Iterable<IntWritable> values, Context context )throws IOException, InterruptedException 
        {
            /* write your reducer code */
            int sum =0;
            for(IntWritable x: values)
            {
                sum+=x.get();
            }; 
            context.write(key ,new IntWritable(sum));
        }       
    }
	
	public static class HistMapper extends Mapper<Object,Text,Color,IntWritable> 
    {
		public Hashtable<Color,IntWritable> h;
		@Override
		protected void setup ( Context context ) throws IOException,InterruptedException 
		{
			h = new Hashtable<Color,IntWritable>();
		}

        @Override
        public void map ( Object key, Text value, Context context )throws IOException, InterruptedException 
        {
            
            Scanner s = new Scanner(value.toString()).useDelimiter(",");
            int red = s.nextInt();
            int green = s.nextInt();
            int blue = s.nextInt();
            /*context.write(new Color(1,red),new IntWritable(1));
            context.write(new Color(2,green),new IntWritable(1));
            context.write(new Color(3,blue),new IntWritable(1));*/
			Color key2 = new Color(1,red);
			if (h.get(key2) == null)
				h.put(key2,new IntWritable(1));
			else 
				h.put(key2,new IntWritable(1));			/*,h.get(key2)+IntWritable(1)*/

			Color key3 = new Color(2,green);
			if (h.get(key3) == null)
				h.put(key3,new IntWritable(1));
			else 
				h.put(key3,new IntWritable(1));		/*,acc(h[key3],new IntWritable(1) )*/

			Color key4 = new Color(3,blue);
			if (h.get(key4) == null)
				h.put(key4,new IntWritable(1));
			else 
				h.put(key4,new IntWritable(1));
            s.close();
        }
		
		@Override
		protected void cleanup ( Context context ) throws IOException,InterruptedException 
		{
			for (Color c: h.keySet())
			{
				context.write(c,h.get(c));
			}
		}
    }
	
    public static void main ( String[] args ) throws Exception 
    {
        Job job = Job.getInstance();
        job.setJobName("MyJob");
        job.setJarByClass(Histogram.class);
        job.setOutputKeyClass(Color.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapOutputKeyClass(Color.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setMapperClass(HistogramMapper.class);
		job.setCombinerClass(MyCombiner.class);
        job.setReducerClass(HistogramReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.setInputPaths(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));
        job.waitForCompletion(true);
		
		/* write your main program code */
        Job jobone = Job.getInstance();
        jobone.setJobName("Myjobone");
        jobone.setJarByClass(Histogram.class);
        jobone.setOutputKeyClass(Color.class);
        jobone.setOutputValueClass(IntWritable.class);
        jobone.setMapOutputKeyClass(Color.class);
        jobone.setMapOutputValueClass(IntWritable.class);
        jobone.setMapperClass(HistMapper.class);
        jobone.setReducerClass(HistogramReducer.class);
        jobone.setInputFormatClass(TextInputFormat.class);
        jobone.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.setInputPaths(jobone,new Path(args[0]));
        FileOutputFormat.setOutputPath(jobone,new Path(args[1]+"2"));
        jobone.waitForCompletion(true);
    }
}