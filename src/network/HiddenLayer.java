package network;

public class HiddenLayer extends Layer {

    public HiddenLayer(int neurons, Layer leftLayer) {
        super(neurons, leftLayer);
    }

    public void sumWeights() {
        list.parallelStream().forEach(Neuron::sumWeights);
    }

    public void activate() {
        list.parallelStream().forEach(Neuron::activate);
    }
}
