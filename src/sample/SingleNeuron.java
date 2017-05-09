package sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tomek on 09.05.2017.
 */
public class SingleNeuron {

    private static int nextId = 0;
    private final int id;

    private double output;

    Connection biasConnection;

    private final double bias = -1;

    private List<Connection> neuronConnections = new ArrayList<>();
    private Map<Integer, Connection> indexedConnections = new HashMap<>();

    public SingleNeuron() {
        id = nextId++;
    }

    public void calcOutput() {
        double out = 0;

        for(Connection connection:neuronConnections) {
            SingleNeuron leftNeuron = connection.getLeftNeuron();
            double weight = connection.getWeight();
            double input = leftNeuron.getOutput();

            out = out + (weight * input);
        }

        out = out + (biasConnection.getWeight() * bias);

        output = (1.0) / (1.0 + Math.exp(-out));
    }

    public double getOutput() {
        return output;
    }

    public void addLeftNeuronsConnections(List<SingleNeuron> leftNeurons) {
        for(SingleNeuron leftNeuron:leftNeurons) {
            Connection connection = new Connection(leftNeuron, this);
            neuronConnections.add(connection);
            indexedConnections.put(leftNeuron.getId(), connection);
        }
    }

    public void addBiasConnection(SingleNeuron biasNeuron) {
        Connection connection = new Connection(biasNeuron, this);
        biasConnection = connection;
        neuronConnections.add(connection);
    }

    public Connection getConnection(int id) {return indexedConnections.get(id);}

    public int getId() {
        return id;
    }

    public List<Connection> getNeuronConnections() {
        return neuronConnections;
    }

    public void setOutput(double output) {
        this.output = output;
    }
}
