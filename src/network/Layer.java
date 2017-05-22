package network;

import java.util.ArrayList;
import java.util.List;

public class Layer {
    private int size; // ilość neuronów

    protected List<Neuron> list = new ArrayList<>(); // lista neuronów

    Layer(int neurons) { // inicjalizacja warstwy wejściowej
        size = neurons;

        for (int i = 0; i < neurons; i++) {
            list.add(new Neuron());
        }
    }

    Layer(int neurons, Layer leftLayer) {  // inicjalizacja warstwy ukrytej/wyjściowej
        size = neurons;

        for (int i = 0; i < neurons; i++) {
            list.add(new Neuron(leftLayer.getList())); // każdy neuron posiada listę neuronó warstwy położonej na lewo
        }
    }

    private List<Neuron> getList() {
        return list;
    }

    public void setList(List<Neuron> list) {
        this.list = list;
    }

    public int getSize() {
        return size;
    }
}
