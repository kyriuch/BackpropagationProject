package network;

public class HiddenLayer extends Layer {

    public HiddenLayer(int neurons, Layer leftLayer) { // wywołanie konstruktora klasy Layer z przekazaniem werstwy wejściowej
        super(neurons, leftLayer);
    }

    public void sumWeights() { // obliczenie sumy iloczynu wag z wyjściamy warstwy wejściowej
        list.parallelStream().forEach(Neuron::sumWeights);
    }

    public void activate() { // włączenie funkcji aktywacji
        list.parallelStream().forEach(Neuron::activate);
    }
}
