package sample;

/**
 * Created by Tomek on 09.05.2017.
 */
import java.util.*;

public class NEURONHELP {
    static int counter = 0;
    final public int id;  // auto increment, starts at 0
    CONNECTIONHELP biasConnection;
    final double bias = -1;
    double output;

    ArrayList<CONNECTIONHELP> Inconnections = new ArrayList<CONNECTIONHELP>();
    HashMap<Integer, CONNECTIONHELP> connectionLookup = new HashMap<Integer, CONNECTIONHELP>();

    public NEURONHELP(){
        id = counter;
        counter++;
    }

    /**
     * Compute Sj = Wij*Aij + w0j*bias
     */
    public void calculateOutput(){
        double s = 0;
        for(CONNECTIONHELP con : Inconnections){
            NEURONHELP leftNeuron = con.getFromNeuron();
            double weight = con.getWeight();
            double a = leftNeuron.getOutput(); //output from previous layer

            s = s + (weight*a);
        }
        s = s + (biasConnection.getWeight()*bias);

        output = g(s);
    }


    double g(double x) {
        return sigmoid(x);
    }

    double sigmoid(double x) {
        return 1.0 / (1.0 +  (Math.exp(-x)));
    }

    public void addInConnectionsS(ArrayList<NEURONHELP> inNeurons){
        for(NEURONHELP n: inNeurons){
            CONNECTIONHELP con = new CONNECTIONHELP(n,this);
            Inconnections.add(con);
            connectionLookup.put(n.id, con);
        }
    }

    public CONNECTIONHELP getConnection(int neuronIndex){
        return connectionLookup.get(neuronIndex);
    }

    public void addInConnection(CONNECTIONHELP con){
        Inconnections.add(con);
    }
    public void addBiasConnection(NEURONHELP n){
        CONNECTIONHELP con = new CONNECTIONHELP(n,this);
        biasConnection = con;
        Inconnections.add(con);
    }
    public ArrayList<CONNECTIONHELP> getAllInConnections(){
        return Inconnections;
    }

    public double getOutput() {
        return output;
    }
    public void setOutput(double o){
        output = o;
    }
}
