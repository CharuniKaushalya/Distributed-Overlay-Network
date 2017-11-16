package main.java.controller;

import java.util.List;
import main.java.model.Statistics;
/**
 * Created by Charuni on 11/16/2017.
 */


public class StdCalculator {
	private List<Statistics> statList;

    public StdCalculator(List<Statistics> stats) {
        this.statList=stats;
    }

    public double getSampleMeanOfHops() {
        int count = statList.size();
        int countSum = 0;
        double upperSum = 0;
        for (int i = 0; i < count; i++) {
            countSum += statList.get(i).getNumberOfHope();
            upperSum += statList.get(i).getNumberOfHope() * statList.get(i).getHopsAverage();
        }
        return (upperSum / countSum);
    }

    public double calculateStdDevOfHops(){
        double mean = getSampleMeanOfHops();
        double upperSumFirst = 0;
        double upperSumSecond = 0;
        double countSum = 0;
        for(int i=0;i<statList.size();i++){
            upperSumFirst += (statList.get(i).getNumberOfHope()-1)*Math.pow(statList.get(i).getHopsSD(),2);
            upperSumSecond += statList.get(i).getNumberOfHope()*Math.pow((statList.get(i).getHopsAverage()-mean),2);
            countSum += statList.get(i).getNumberOfHope();
        }
        return Math.sqrt((upperSumFirst+upperSumSecond)/(countSum-1));
    }
    public double getSampleMeanOfLatencies() {
        int count = statList.size();
        int countSum = 0;
        double upperSum = 0;
        for (int i = 0; i < count; i++) {
            countSum += statList.get(i).getNumberOfLatencies();
            upperSum += statList.get(i).getNumberOfLatencies() * statList.get(i).getLatencyAverage();
        }
        return (upperSum / countSum);
    }

    public double calculateStdDevOfLatencies(){
        double mean = getSampleMeanOfLatencies();
        double upperSumFirst = 0;
        double upperSumSecond = 0;
        double countSum = 0;
        for(int i=0;i<statList.size();i++){
            upperSumFirst += (statList.get(i).getNumberOfLatencies()-1)*Math.pow(statList.get(i).getLatencySD(),2);
            upperSumSecond += statList.get(i).getNumberOfLatencies()*Math.pow((statList.get(i).getLatencyAverage()-mean),2);
            countSum += statList.get(i).getNumberOfLatencies();
        }
        return Math.sqrt((upperSumFirst+upperSumSecond)/(countSum-1));
    }
}
