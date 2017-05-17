package network;

public class OutputLayer extends Layer {

    public OutputLayer(int neurons, Layer leftLayer) {
        super(neurons, leftLayer);
    }

    public void sumWeights() {
            list.parallelStream().forEach(Neuron::sumWeights);
    }

    public void activate() {
        list.parallelStream().forEach(Neuron::activate);
    }
}
