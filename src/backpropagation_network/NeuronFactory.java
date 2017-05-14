package backpropagation_network;

import java.util.ArrayList;
import java.util.List;

public final class NeuronFactory {

    private NeuronFactory() {

    }

    public static Neuron getNeuronWithConnections(List<Neuron> leftNeurons, Neuron biasNeuron) {
        final List<Connection> connections = new ArrayList<>();

        leftNeurons.forEach(neuron -> connections.add(new Connection(neuron)));

        return new Neuron(connections, new Connection(biasNeuron));
    }

    public static Neuron getNeuronWithoutConnections() {
        return new Neuron();
    }
}
