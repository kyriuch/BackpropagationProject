package backpropagation_network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neuron {

    private List<Connection> connections;
    private Map<Integer, Connection> connectionLookUp = new HashMap<>();

    private final int id;
    private static int idCounter = 0;

    private double output;

    Neuron(List<Connection> connections) {
        this.id = idCounter++;

        this.connections = connections;
        this.connections.forEach(connection -> connectionLookUp.put(connection.getLeftNeuron().getId(), new Connection(connection.getLeftNeuron())));
    }

    Neuron(int biasOutput) {
        this.output = output;
        this.id = idCounter++;
    }

    Neuron() {
        this.id = idCounter++;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public double getOutput() {
        return output;
    }

    public int getId() {
        return id;
    }

    public Connection getConnection(int id) {
        return connectionLookUp.get(id);
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public void calculateOutput() {
        double output = 0;

        for(Connection connection:connections) {
            Neuron leftNeuron = connection.getLeftNeuron();

            output += connection.getConnectionWeight() * leftNeuron.getOutput();
        }

        this.output = 1.0 / (1.0 +  (Math.exp(-output)));
    }
}
