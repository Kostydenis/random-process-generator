/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab4_Neumann;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import org.knowm.xchart.Chart;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.Series;
import org.knowm.xchart.SwingWrapper;

/**
 *
 * @author kostydenis
 */
public class Lab4_Neumann {
    
    // параметры
    public static int A = -50;
    public static int B =  50;
    public static double ALPHA = 1; // коэффициент масшатаба гамма = 1
    public static double BETA  = 0; // коэффициент сдвига
    public static double M = func(BETA); 
    public static int kol_iter = 1000; // количество чисел
    
    // вспомогательные функции
    public static int min_diff_index(double num, ArrayList<Double> lst) {
        int min_ind = Integer.MAX_VALUE;
        double min_val = Integer.MAX_VALUE;
        
        for (int i = 0; i < lst.size(); i++) {
            if (Math.max(num, lst.get(i)) - Math.min(num, lst.get(i)) < min_val) {
                min_val = Math.max(num, lst.get(i)) - Math.min(num, lst.get(i));
                min_ind = i;
            }
        }
        return min_ind;
    }
    public static double auto_r(int tau, ArrayList<Double> inlist, double avg, double var) {
        double output = 0;
        for (int i = 0; i < kol_iter-tau; i++) {
            output += ((inlist.get(i)-avg)*(inlist.get(i+tau)-avg));
        }
        output = output/(var * (inlist.size()-tau));
        return output;
    }
    public static double man_r(int tau, double alpha, double beta) {
        return Math.exp(-alpha * tau) * Math.cos(beta * Math.PI * tau);
    }
    public static void arrayToFile(ArrayList<Double> array, String filename) throws IOException {
        File file = new File(filename);

        if (!file.exists()) {
            file.createNewFile();
        }

        PrintWriter prnt = new PrintWriter(file.getAbsoluteFile());
        
        for (Double item : array) {
            prnt.print(item.toString().replace('.', ',') + "\r\n");
        }
        
        prnt.close();
    }
    
    // основные функции
    // Laplace
    public static double func(double x) {
        return (ALPHA/2)*Math.exp(-ALPHA*Math.abs(x-BETA));
    }
//    public static double func(double x) {
//        return (1/Math.PI)*(ALPHA/(Math.pow(x-BETA, 2)+Math.pow(ALPHA, 2)));
//    }
    // генерация чисел
    public static ArrayList<Double> rand() {
        int i = 0;
        Random rnd = new Random();  
        
        double n1, n2;
        double eps1, eps2;
        ArrayList<Double> output = new ArrayList<>();
                    
        while (i < kol_iter) {
            n1 = rnd.nextDouble(); //генерация сл. числа
            n2 = rnd.nextDouble();
            
            eps1 = A+(B-A)*n1;
            eps2 = M * n2;
            
            if (func(eps1) >= eps2) {
                output.add(eps1);
               i = i + 1;
            }           
        }
        return output;
    }
    // порядок чисел
    public static ArrayList<Double> swap(ArrayList<Double> inlst) {
        int buff_length = 5;
        ArrayList<Double> buff = new ArrayList<>();
        ArrayList<Double> output = new ArrayList<>();
        ArrayList<Double> tmplst = new ArrayList<>(inlst);
        
        output.add(tmplst.remove(0));
        while (tmplst.size() > 0) {
            while (buff.size() <= buff_length) {
                buff.add(tmplst.remove(0));
            }
            output.add(buff.remove(min_diff_index(output.get(output.size()-1), buff)));
        }
        while (buff.size() > 0) {
            output.add(buff.remove(min_diff_index(output.get(output.size()-1), buff)));
        }
        
        return output;
    }
    // автокорреляционная функция
    public static ArrayList<Double> auto_corr(ArrayList<Double> inlist) {
        double avg = 0;
        for (Double item : inlist) {
            avg += item;
        }
        avg = avg/inlist.size();
        
        double var = 0;
        for (Double item : inlist) {
            var += Math.pow((item - avg), 2);
        }
        var = var / inlist.size();
        
        ArrayList<Double> output = new ArrayList();
        for (int tau = 0; tau < 100; tau++) {
            output.add(auto_r(tau, inlist, avg, var));
        }
        
        return output;
        
    }
    
    public static void plot_auto_corr(ArrayList<Double> inlist, double inalpha, double inbeta) {
        ArrayList xData = new ArrayList();
        for (int i = 0; i < 100; i++) {
            xData.add(i);
        }
        
        ArrayList<Double> man_Y = new ArrayList();
        for (int i = 0; i < 100; i++) {
            man_Y.add(man_r(i, inalpha, inbeta));
        }
        
        Chart chart = new Chart(800, 600);
        chart.setChartTitle("Автокорреляционная функция");
        
//        chart.addSeries("auto", xData, auto_corr(ordered));
        chart.addSeries("auto", xData, auto_corr(inlist));
        chart.addSeries("manual", xData, man_Y);
        
        new SwingWrapper(chart).displayChart();  
    }
    
    public static void plot_array(ArrayList inlist, String name) {
        ArrayList xData = new ArrayList();
        for (int i = 0; i < inlist.size(); i++) {
            xData.add(i);
        }
        Chart chart = QuickChart.getChart(name, "X", "Y", " ", xData, inlist);
        new SwingWrapper(chart).displayChart(); 
    }

    
    public static ArrayList unordered;
    public static ArrayList ordered; 
    
    
    


    public static void main(String[] args) throws IOException {
        unordered = rand();
        ordered = swap(unordered);
        
        arrayToFile(unordered, "unordered.txt");
        arrayToFile(ordered, "ordered.txt");
        
//        double man_ALPHA = Double.parseDouble(args[0]);
//        double man_BETA = Double.parseDouble(args[1]);
        
        double man_ALPHA = 0.3;
        double man_BETA = 0.5;
        
        plot_auto_corr(unordered, man_ALPHA, man_BETA); // указать альфа и бета соотетсвенно
        
         plot_array(unordered, "unordered");
         plot_array(ordered, "ordered");
        
        
    }    
}
