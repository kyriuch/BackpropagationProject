package network;

public class OutputLayer extends Layer {

    public OutputLayer(int neurons, Layer leftLayer) { // wywołanie konstruktora klasy Layer z przekazaniem warstwy ukrytej
        super(neurons, leftLayer);
    }

    public void sumWeights() { // obliczenie sumy iloczynu wag z wyjściami warstwy ukrytej
            list.parallelStream().forEach(Neuron::sumWeights);
    }

    public void activate() { // włączenie funkcji aktywacji
        list.parallelStream().forEach(Neuron::activate);
    }
}
